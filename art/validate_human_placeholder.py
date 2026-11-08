#!/usr/bin/env python3
"""Validate the human placeholder texture. Python stdlib only.

The runtime file is
src/main/resources/assets/livingcivilizations/textures/entity/human.png.

Default mode checks the committed file. ``--apply-from`` rewrites that file
from a PNG whose bytes match the imported baseline. ``--write-previews``
writes nearest-neighbor before/after images from the committed file.
"""

from __future__ import annotations

import argparse
import hashlib
import struct
import sys
import zlib
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
TEXTURE = ROOT / "src/main/resources/assets/livingcivilizations/textures/entity/human.png"

WIDTH = 64
HEIGHT = 64
MARKER_X = 24
MARKER_Y = 24
MARKER_SIZE = 4
# Body north face from HumanoidModel.createMesh body texOffs(16, 16),
# addBox width 8, height 12, depth 4, not mirrored. ModelPart.Cube maps
# Direction.NORTH to [u+depth, v+depth, u+depth+width, v+depth+height).
BODY_FRONT = (20, 20, 8, 12)  # x, y, w, h

ORIGINAL_FILE_SHA256 = "963137f19e8e604689fae86a0f3c66a3ab4de50651b04dd7fb07f72744c5b9ad"
ORIGINAL_DECODED_SHA256 = "f7f0b6b916b158d546849a38c09eb08aa2613567c513fe5e163ef1620b5c8f88"
ORIGINAL_MARKER_RGBA = (42, 105, 116, 255)
MAGENTA = (255, 0, 255, 255)
BLACK = (0, 0, 0, 255)
# Bytes produced by encode_png on Python 3.12.3 / zlib 1.3 (2026-11-08).
OUTPUT_FILE_SHA256 = "703f30d2e5b048dae204b8c129a88fbe4369d3aed4fdabeea8a4be3963d11cc4"


def marker_coords():
    for y in range(MARKER_Y, MARKER_Y + MARKER_SIZE):
        for x in range(MARKER_X, MARKER_X + MARKER_SIZE):
            yield x, y


def marker_rgba(x: int, y: int):
    if (x + y) % 2 == 0:
        return MAGENTA
    return BLACK


def _paeth(left: int, up: int, upper_left: int) -> int:
    estimate = left + up - upper_left
    left_distance = abs(estimate - left)
    up_distance = abs(estimate - up)
    upper_left_distance = abs(estimate - upper_left)
    if left_distance <= up_distance and left_distance <= upper_left_distance:
        return left
    if up_distance <= upper_left_distance:
        return up
    return upper_left


def decode_png(data: bytes):
    """Return (rows of tightly packed RGBA, chunk types). Raise ValueError."""
    signature = b"\x89PNG\r\n\x1a\n"
    if not data.startswith(signature):
        raise ValueError("missing PNG signature")
    offset = len(signature)
    idat = bytearray()
    ihdr = None
    saw_iend = False
    types = []
    while offset < len(data):
        if offset + 8 > len(data):
            raise ValueError("truncated chunk header")
        length = struct.unpack(">I", data[offset : offset + 4])[0]
        chunk_type = data[offset + 4 : offset + 8]
        start = offset + 8
        end = start + length
        if end + 4 > len(data):
            raise ValueError(f"truncated chunk {chunk_type!r}")
        payload = data[start:end]
        stored_crc = struct.unpack(">I", data[end : end + 4])[0]
        actual_crc = zlib.crc32(chunk_type + payload) & 0xFFFFFFFF
        if stored_crc != actual_crc:
            raise ValueError(f"bad CRC for {chunk_type!r}")
        types.append(chunk_type.decode("ascii"))
        if chunk_type == b"IHDR":
            ihdr = payload
        elif chunk_type == b"IDAT":
            idat.extend(payload)
        elif chunk_type == b"IEND":
            saw_iend = True
        offset = end + 4
    if ihdr is None or not saw_iend:
        raise ValueError("PNG is missing IHDR or IEND")
    width, height, bit_depth, color_type, compression, filter_method, interlace = struct.unpack(">IIBBBBB", ihdr)
    if (width, height, bit_depth, color_type, compression, filter_method, interlace) != (WIDTH, HEIGHT, 8, 6, 0, 0, 0):
        raise ValueError(
            f"IHDR {width}x{height} bit {bit_depth} color {color_type} "
            f"comp {compression} filter {filter_method} interlace {interlace}"
        )
    raw = zlib.decompress(bytes(idat))
    stride = width * 4
    expected = height * (1 + stride)
    if len(raw) != expected:
        raise ValueError(f"inflated size {len(raw)} != {expected}")
    rows = []
    previous = bytearray(stride)
    cursor = 0
    for _y in range(height):
        filter_type = raw[cursor]
        cursor += 1
        row = bytearray(raw[cursor : cursor + stride])
        cursor += stride
        if filter_type == 0:
            pass
        elif filter_type == 1:
            for index in range(stride):
                left = row[index - 4] if index >= 4 else 0
                row[index] = (row[index] + left) & 255
        elif filter_type == 2:
            for index in range(stride):
                row[index] = (row[index] + previous[index]) & 255
        elif filter_type == 3:
            for index in range(stride):
                left = row[index - 4] if index >= 4 else 0
                row[index] = (row[index] + ((left + previous[index]) // 2)) & 255
        elif filter_type == 4:
            for index in range(stride):
                left = row[index - 4] if index >= 4 else 0
                upper_left = previous[index - 4] if index >= 4 else 0
                row[index] = (row[index] + _paeth(left, previous[index], upper_left)) & 255
        else:
            raise ValueError(f"unsupported PNG filter {filter_type}")
        rows.append(bytes(row))
        previous = row
    return rows, types


def encode_png(rows) -> bytes:
    raw = b"".join(b"\x00" + bytes(row) for row in rows)
    compressed = zlib.compress(raw, 9)

    def chunk(tag: bytes, payload: bytes) -> bytes:
        crc = zlib.crc32(tag + payload) & 0xFFFFFFFF
        return struct.pack(">I", len(payload)) + tag + payload + struct.pack(">I", crc)

    ihdr = struct.pack(">IIBBBBB", WIDTH, HEIGHT, 8, 6, 0, 0, 0)
    return b"\x89PNG\r\n\x1a\n" + chunk(b"IHDR", ihdr) + chunk(b"IDAT", compressed) + chunk(b"IEND", b"")


def pixel(row: bytes, x: int):
    index = x * 4
    return tuple(row[index : index + 4])


def set_pixel(row: bytearray, x: int, rgba):
    index = x * 4
    row[index : index + 4] = bytes(rgba)


def decoded_bytes(rows) -> bytes:
    return b"".join(rows)


def sha256(data: bytes) -> str:
    return hashlib.sha256(data).hexdigest()


def alpha_bytes(rows) -> bytes:
    return bytes(row[index] for row in rows for index in range(3, len(row), 4))


def restore_marker(rows):
    restored = [bytearray(row) for row in rows]
    for x, y in marker_coords():
        set_pixel(restored[y], x, ORIGINAL_MARKER_RGBA)
    return restored


def paint_marker(rows):
    painted = [bytearray(row) for row in rows]
    changed = []
    for x, y in marker_coords():
        current = pixel(painted[y], x)
        if current != ORIGINAL_MARKER_RGBA:
            raise ValueError(f"refusing to mark {(x, y)}: expected {ORIGINAL_MARKER_RGBA}, found {current}")
        updated = marker_rgba(x, y)
        if updated[3] != current[3]:
            raise ValueError("marker would change alpha")
        set_pixel(painted[y], x, updated)
        changed.append((x, y, current, updated))
    return painted, changed


def assert_marker_inside_body_front():
    front_x, front_y, front_w, front_h = BODY_FRONT
    for x, y in marker_coords():
        if not (front_x <= x < front_x + front_w and front_y <= y < front_y + front_h):
            raise ValueError(f"marker pixel {(x, y)} is outside the body front UV rectangle")


def validate_file(path: Path) -> dict:
    assert_marker_inside_body_front()
    data = path.read_bytes()
    rows, chunk_types = decode_png(data)
    if any(value not in (0, 255) for value in alpha_bytes(rows)):
        raise ValueError("alpha is not binary")
    restored = restore_marker(rows)
    restored_sha = sha256(decoded_bytes(restored))
    if restored_sha != ORIGINAL_DECODED_SHA256:
        raise ValueError(f"pixels outside the marker do not match the import ({restored_sha})")
    changes = []
    for x, y in marker_coords():
        current = pixel(rows[y], x)
        expected = marker_rgba(x, y)
        if current != expected:
            raise ValueError(f"marker {(x, y)} is {current}, expected {expected}")
        if current[3] != ORIGINAL_MARKER_RGBA[3]:
            raise ValueError(f"marker {(x, y)} changed alpha")
        changes.append((x, y, ORIGINAL_MARKER_RGBA, current))
    # Restoring the marker must be the only difference, so the alpha plane matches.
    if alpha_bytes(rows) != alpha_bytes(restored):
        raise ValueError("alpha plane differs from the imported image")
    file_sha = sha256(data)
    if OUTPUT_FILE_SHA256 is not None and file_sha != OUTPUT_FILE_SHA256:
        raise ValueError(f"file SHA256 {file_sha} != pinned {OUTPUT_FILE_SHA256}")
    outside = WIDTH * HEIGHT - len(changes)
    return {
        "path": str(path),
        "file_sha256": file_sha,
        "decoded_sha256": sha256(decoded_bytes(rows)),
        "restored_decoded_sha256": restored_sha,
        "alpha_sha256": sha256(alpha_bytes(rows)),
        "chunks": chunk_types,
        "changed": changes,
        "unchanged_pixels": outside,
        "width": WIDTH,
        "height": HEIGHT,
    }


def apply_from(original: Path, destination: Path) -> dict:
    data = original.read_bytes()
    file_sha = sha256(data)
    if file_sha != ORIGINAL_FILE_SHA256:
        raise ValueError(f"input SHA256 {file_sha} != imported baseline {ORIGINAL_FILE_SHA256}")
    rows, _types = decode_png(data)
    if sha256(decoded_bytes(rows)) != ORIGINAL_DECODED_SHA256:
        raise ValueError("input decoded pixels do not match the imported baseline")
    painted, _changes = paint_marker(rows)
    if alpha_bytes(painted) != alpha_bytes(rows):
        raise ValueError("apply changed the alpha plane")
    encoded = encode_png(painted)
    destination.parent.mkdir(parents=True, exist_ok=True)
    destination.write_bytes(encoded)
    # Re-decode the bytes just written. Pixel equality is the contract.
    written, _types = decode_png(encoded)
    if decoded_bytes(written) != decoded_bytes(painted):
        raise ValueError("encoder round-trip changed pixels")
    return validate_file(destination)


def scale_nearest(rows, factor: int):
    scaled = []
    for row in rows:
        wide = bytearray()
        for x in range(WIDTH):
            wide.extend(pixel(row, x) * factor)
        scaled.extend([bytes(wide)] * factor)
    return scaled


def crop(rows, x0, y0, width, height):
    cropped = []
    for y in range(y0, y0 + height):
        start = x0 * 4
        cropped.append(rows[y][start : start + width * 4])
    return cropped


def write_png_rows(path: Path, rows):
    height = len(rows)
    width = len(rows[0]) // 4
    raw = b"".join(b"\x00" + row for row in rows)
    compressed = zlib.compress(raw, 9)

    def chunk(tag: bytes, payload: bytes) -> bytes:
        crc = zlib.crc32(tag + payload) & 0xFFFFFFFF
        return struct.pack(">I", len(payload)) + tag + payload + struct.pack(">I", crc)

    ihdr = struct.pack(">IIBBBBB", width, height, 8, 6, 0, 0, 0)
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_bytes(b"\x89PNG\r\n\x1a\n" + chunk(b"IHDR", ihdr) + chunk(b"IDAT", compressed) + chunk(b"IEND", b""))


def write_previews(directory: Path, rows):
    before = restore_marker(rows)
    after = rows
    before_x8 = scale_nearest(before, 8)
    after_x8 = scale_nearest(after, 8)
    write_png_rows(directory / "human-before-nearest-x8.png", before_x8)
    write_png_rows(directory / "human-after-nearest-x8.png", after_x8)
    side = []
    gap_px = bytes((200, 200, 200, 255))
    for index in range(len(before_x8)):
        side.append(before_x8[index] + gap_px * 8 + after_x8[index])
    write_png_rows(directory / "human-before-after-nearest-x8.png", side)
    # Body unwrap around the front face, including the texOffs(16, 16) island.
    body_box = (16, 16, 24, 16)
    before_body = scale_rows(crop(before, *body_box), 16, body_box[2])
    after_body = scale_rows(crop(after, *body_box), 16, body_box[2])
    body_side = []
    for index in range(len(before_body)):
        body_side.append(before_body[index] + gap_px * 8 + after_body[index])
    write_png_rows(directory / "human-body-before-after-nearest-x16.png", body_side)


def scale_rows(rows, factor: int, width: int):
    scaled = []
    for row in rows:
        wide = bytearray()
        for x in range(width):
            wide.extend(pixel(row, x) * factor)
        scaled.extend([bytes(wide)] * factor)
    return scaled


def print_report(result: dict):
    print(f"path: {result['path']}")
    print(f"dimensions: {result['width']}x{result['height']} RGBA")
    print(f"chunks: {','.join(result['chunks'])}")
    print(f"file_sha256: {result['file_sha256']}")
    print(f"decoded_sha256: {result['decoded_sha256']}")
    print(f"restored_to_import_sha256: {result['restored_decoded_sha256']}")
    print(f"alpha_sha256: {result['alpha_sha256']}")
    print(f"changed_pixels: {len(result['changed'])}")
    print(f"unchanged_pixels: {result['unchanged_pixels']}")
    for x, y, old, new in result["changed"]:
        print(f"  ({x},{y}) {old} -> {new}")


def main(argv):
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--apply-from", type=Path, help="baseline PNG whose SHA256 is the imported file")
    parser.add_argument("--write-previews", type=Path, help="directory for nearest-neighbor previews")
    parser.add_argument("--texture", type=Path, default=TEXTURE)
    args = parser.parse_args(argv)
    if args.apply_from:
        result = apply_from(args.apply_from, args.texture)
    else:
        result = validate_file(args.texture)
    print_report(result)
    if args.write_previews:
        rows, _types = decode_png(args.texture.read_bytes())
        write_previews(args.write_previews, rows)
        print(f"previews: {args.write_previews}")
    return 0


if __name__ == "__main__":
    try:
        sys.exit(main(sys.argv[1:]))
    except (OSError, ValueError, zlib.error) as exc:
        print(f"FAIL: {exc}", file=sys.stderr)
        sys.exit(1)
