#!/usr/bin/env python3
"""
Analiza archivos CSV de la carpeta 'salida' y genera:
1) Un gráfico de barras por cada archivo Tiempos_<numero>.csv (o Tiempo_<numero>.csv).
2) Un resumen general combinando tiempos y archivos Resultado_<algoritmo>_<numero>.csv.
3) Un resumen detallado por archivo de resultado (dimensiones y hash).

Uso:
    python analizar_salida.py
    python analizar_salida.py --salida "./salida"
"""

from __future__ import annotations

import argparse
import csv
import hashlib
import re
import statistics
from dataclasses import dataclass
from pathlib import Path
from typing import Dict, List, Tuple

# Dependencia externa para gráficos
import matplotlib.pyplot as plt


TIEMPOS_RE = re.compile(r"(?i)^tiempos?_(\d+)\.csv$")
RESULTADO_RE = re.compile(r"(?i)^resultado_(.+)_(\d+)\.csv$")


@dataclass
class TiempoRegistro:
    algoritmo: str
    tiempo_ms: float
    verificacion: str


@dataclass
class TiemposArchivo:
    numero: str
    path: Path
    metadata: Dict[str, str]
    registros: List[TiempoRegistro]


def md5_file(path: Path, chunk_size: int = 1024 * 1024) -> str:
    h = hashlib.md5()
    with path.open("rb") as f:
        while True:
            chunk = f.read(chunk_size)
            if not chunk:
                break
            h.update(chunk)
    return h.hexdigest()


def csv_shape(path: Path) -> Tuple[int, int]:
    filas = 0
    cols = 0
    with path.open("r", encoding="utf-8", newline="") as f:
        reader = csv.reader(f)
        for row in reader:
            filas += 1
            if len(row) > cols:
                cols = len(row)
    return filas, cols


def parse_tiempos_file(path: Path) -> TiemposArchivo:
    rows: List[List[str]] = []
    with path.open("r", encoding="utf-8", newline="") as f:
        reader = csv.reader(f)
        rows = [r for r in reader if r]

    if not rows:
        raise ValueError(f"Archivo vacío: {path}")

    header_idx = None
    for i, row in enumerate(rows):
        if row and row[0].strip().lower() == "algoritmo":
            header_idx = i
            break

    if header_idx is None:
        raise ValueError(f"No se encontró encabezado 'algoritmo' en {path}")

    metadata: Dict[str, str] = {}
    if header_idx >= 2:
        keys = [x.strip() for x in rows[0]]
        vals = [x.strip() for x in rows[1]]
        for i, k in enumerate(keys):
            if k:
                metadata[k] = vals[i] if i < len(vals) else ""

    registros: List[TiempoRegistro] = []
    for row in rows[header_idx + 1 :]:
        if len(row) < 2:
            continue
        algoritmo = row[0].strip()
        tiempo_s = row[1].strip().replace(",", ".")
        verificacion = row[2].strip() if len(row) > 2 else ""

        try:
            tiempo = float(tiempo_s)
        except ValueError:
            continue

        registros.append(TiempoRegistro(algoritmo=algoritmo, tiempo_ms=tiempo, verificacion=verificacion))

    m = TIEMPOS_RE.match(path.name)
    if not m:
        raise ValueError(f"Nombre inválido para archivo de tiempos: {path.name}")

    numero = m.group(1)
    return TiemposArchivo(numero=numero, path=path, metadata=metadata, registros=registros)


def generar_grafico_barras(tiempos: TiemposArchivo, out_dir: Path) -> Path:
    if not tiempos.registros:
        raise ValueError(f"Sin registros de tiempos en {tiempos.path.name}")

    algoritmos = [r.algoritmo for r in tiempos.registros]
    valores = [r.tiempo_ms for r in tiempos.registros]
    colores = ["#2E8B57" if r.verificacion.upper() == "OK" else "#B22222" for r in tiempos.registros]

    plt.figure(figsize=(14, 7))
    bars = plt.bar(algoritmos, valores, color=colores)

    plt.title(f"Tiempos de ejecución - n={tiempos.numero}")
    plt.xlabel("Algoritmo")
    plt.ylabel("Tiempo (ms)")
    plt.xticks(rotation=45, ha="right")
    plt.grid(axis="y", linestyle="--", alpha=0.3)

    for bar, v in zip(bars, valores):
        plt.text(bar.get_x() + bar.get_width() / 2, bar.get_height(), f"{v:.2f}", ha="center", va="bottom", fontsize=8)

    plt.tight_layout()
    out_path = out_dir / f"Barras_Tiempos_{tiempos.numero}.png"
    plt.savefig(out_path, dpi=150)
    plt.close()

    return out_path


def analizar_resultados_por_numero(salida_dir: Path) -> Dict[str, Dict[str, Dict[str, object]]]:
    """
    Devuelve estructura:
    {
      "1024": {
         "NaivOnArray": {
             "archivo": "Resultado_NaivOnArray_1024.csv",
             "filas": 1024,
             "columnas": 1024,
             "md5": "..."
         },
         ...
      },
      ...
    }
    """
    data: Dict[str, Dict[str, Dict[str, object]]] = {}

    for p in sorted(salida_dir.glob("*.csv")):
        m = RESULTADO_RE.match(p.name)
        if not m:
            continue

        algoritmo = m.group(1)
        numero = m.group(2)

        filas, cols = csv_shape(p)
        digest = md5_file(p)

        data.setdefault(numero, {})[algoritmo] = {
            "archivo": p.name,
            "filas": filas,
            "columnas": cols,
            "md5": digest,
        }

    return data


def construir_resumen_general(
    tiempos_files: List[TiemposArchivo], resultados: Dict[str, Dict[str, Dict[str, object]]]
) -> List[Dict[str, object]]:
    resumen: List[Dict[str, object]] = []

    for tf in sorted(tiempos_files, key=lambda x: int(x.numero)):
        registros = tf.registros
        if not registros:
            continue

        tiempos_vals = [r.tiempo_ms for r in registros]
        verifs = [r.verificacion.upper() == "OK" for r in registros]

        fastest = min(registros, key=lambda r: r.tiempo_ms)
        slowest = max(registros, key=lambda r: r.tiempo_ms)

        alg_tiempos = {r.algoritmo for r in registros}
        alg_result = set(resultados.get(tf.numero, {}).keys())

        faltantes = sorted(alg_tiempos - alg_result)
        extras = sorted(alg_result - alg_tiempos)

        hashes = [v["md5"] for v in resultados.get(tf.numero, {}).values()]
        unique_hashes = len(set(hashes)) if hashes else 0

        resumen.append(
            {
                "numero": tf.numero,
                "tamano": tf.metadata.get("tamano", ""),
                "block_size": tf.metadata.get("block_size", ""),
                "hilos": tf.metadata.get("hilos", ""),
                "algoritmos_tiempos": len(alg_tiempos),
                "resultados_encontrados": len(alg_result),
                "faltantes_resultado": ";".join(faltantes),
                "extras_resultado": ";".join(extras),
                "mas_rapido": fastest.algoritmo,
                "ms_mas_rapido": round(fastest.tiempo_ms, 4),
                "mas_lento": slowest.algoritmo,
                "ms_mas_lento": round(slowest.tiempo_ms, 4),
                "promedio_ms": round(statistics.mean(tiempos_vals), 4),
                "mediana_ms": round(statistics.median(tiempos_vals), 4),
                "todas_verificaciones_ok": all(verifs),
                "hashes_unicos_resultado": unique_hashes,
            }
        )

    return resumen


def escribir_csv(path: Path, rows: List[Dict[str, object]]) -> None:
    if not rows:
        with path.open("w", encoding="utf-8", newline="") as f:
            f.write("")
        return

    headers = list(rows[0].keys())
    with path.open("w", encoding="utf-8", newline="") as f:
        writer = csv.DictWriter(f, fieldnames=headers)
        writer.writeheader()
        writer.writerows(rows)


def main() -> None:
    parser = argparse.ArgumentParser(description="Genera gráficos y resumen de resultados de multiplicación de matrices.")
    parser.add_argument(
        "--salida",
        type=str,
        default="salida",
        help="Ruta de la carpeta que contiene los CSV de salida.",
    )
    args = parser.parse_args()

    salida_dir = Path(args.salida).resolve()
    if not salida_dir.exists() or not salida_dir.is_dir():
        raise FileNotFoundError(f"La carpeta no existe o no es un directorio: {salida_dir}")

    graficos_dir = salida_dir / "graficos"
    resumen_dir = salida_dir / "resumen"
    graficos_dir.mkdir(parents=True, exist_ok=True)
    resumen_dir.mkdir(parents=True, exist_ok=True)

    tiempos_paths = [p for p in sorted(salida_dir.glob("*.csv")) if TIEMPOS_RE.match(p.name)]
    if not tiempos_paths:
        print("No se encontraron archivos Tiempos_<numero>.csv ni Tiempo_<numero>.csv en la carpeta salida.")
        return

    tiempos_files: List[TiemposArchivo] = []
    for p in tiempos_paths:
        try:
            tf = parse_tiempos_file(p)
            tiempos_files.append(tf)
            out_img = generar_grafico_barras(tf, graficos_dir)
            print(f"[OK] Gráfico generado: {out_img}")
        except Exception as e:
            print(f"[WARN] No se pudo procesar {p.name}: {e}")

    resultados = analizar_resultados_por_numero(salida_dir)

    # Resumen general por cada número detectado en tiempos
    resumen_general = construir_resumen_general(tiempos_files, resultados)
    resumen_general_path = resumen_dir / "Resumen_general.csv"
    escribir_csv(resumen_general_path, resumen_general)

    # Resumen detallado de archivos Resultado_<algoritmo>_<numero>.csv
    detalle_rows: List[Dict[str, object]] = []
    for numero in sorted(resultados.keys(), key=lambda x: int(x)):
        for algoritmo, info in sorted(resultados[numero].items()):
            detalle_rows.append(
                {
                    "numero": numero,
                    "algoritmo": algoritmo,
                    "archivo": info["archivo"],
                    "filas": info["filas"],
                    "columnas": info["columnas"],
                    "md5": info["md5"],
                }
            )

    detalle_path = resumen_dir / "Resumen_resultados_detalle.csv"
    escribir_csv(detalle_path, detalle_rows)

    print(f"[OK] Resumen general: {resumen_general_path}")
    print(f"[OK] Resumen de detalle: {detalle_path}")
    print("Proceso finalizado.")


if __name__ == "__main__":
    main()
