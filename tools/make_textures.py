#!/usr/bin/env python3
"""16x16 임시 텍스처를 생성한다 (외부 라이브러리 없이 순수 파이썬).

사용법:  python3 tools/make_textures.py
결과:    src/main/resources/assets/carbontech/textures/item/*.png

나중에 Aseprite/Pixilart 같은 도구로 그린 진짜 텍스처로 덮어쓰면 된다.
문자 하나가 픽셀 하나다. '.'은 투명.
"""
import os
import struct
import zlib

ASSETS = os.path.join(os.path.dirname(__file__), "..", "src", "main", "resources",
                      "assets", "carbontech", "textures")
OUT = os.path.join(ASSETS, "item")
OUT_BLOCK = os.path.join(ASSETS, "block")

# 카본 팔레트 (어두운 차콜 + 금속성 하이라이트)
PALETTE = {
    ".": None,
    "O": (0x14, 0x15, 0x17, 255),  # 외곽선
    "D": (0x24, 0x26, 0x29, 255),  # 어두운 면
    "L": (0x3a, 0x3d, 0x41, 255),  # 기본 면
    "H": (0x6a, 0x6f, 0x75, 255),  # 하이라이트
    "W": (0x9a, 0xa0, 0xa6, 255),  # 강한 반사광
    # 착용 갑옷 전용: 검정 / 살짝 검정 / 진한 회색 / 진한 회색 광택
    "K": (0x0c, 0x0d, 0x0f, 255),
    "k": (0x18, 0x19, 0x1c, 255),
    "G": (0x2a, 0x2c, 0x30, 255),
    "g": (0x36, 0x38, 0x3d, 255),
    # 방패 전용 (3D 모델은 조명이 먹어 어두워지므로 명암을 크게): 바탕 / 밝은 점 / 어두운 점·테두리
    "S": (0x26, 0x28, 0x2d, 255),
    "T": (0x50, 0x54, 0x5b, 255),
    "U": (0x08, 0x08, 0x0a, 255),
    # 심층암 바탕색 (바닐라 deepslate 톤)
    "a": (0x50, 0x50, 0x52, 255),
    "b": (0x4a, 0x4a, 0x4c, 255),
    "c": (0x5a, 0x5a, 0x5c, 255),
    "d": (0x44, 0x44, 0x46, 255),
}

# 블록 텍스처: 심층암 바탕 + 카본 결정(O/L/H) 박힘
BLOCK_TEXTURES = {
    # 카본 블록: 어두운 직조 패널 + 테두리
    "carbon_block": [
        "KKKKKKKKKKKKKKKK",
        "KGkkGkkGkkGkkGkK",
        "KkKkkKkkKkkKkkGK",
        "KkkGkkGkkGkkGkkK",
        "KGkkKkkKkkKkkKkK",
        "KkKkkGkkGkkGkkGK",
        "KkkGkkKkkKkkKkkK",
        "KGkkKkkGkkGkkGkK",
        "KkKkkGkkKkkKkkGK",
        "KkkGkkKkkGkkGkkK",
        "KGkkKkkGkkKkkKkK",
        "KkKkkGkkKkkGkkGK",
        "KkkGkkKkkGkkKkkK",
        "KGkkKkkGkkKkkGkK",
        "KkkkkkkkkkkkkkkK",
        "KKKKKKKKKKKKKKKK",
    ],
    "carbon_ore": [
        "abababcbabababab",
        "babcbabababdbaba",
        "abOOabababcbOOab",
        "bOLHOabdbabOHLOb",
        "aOHLOababcbOLHOa",
        "babOOabababOOaba",
        "cbabababdbababab",
        "abababcbabababab",
        "babdbaOOabcbabab",
        "ababaOLHOabababa",
        "cbabaOHLOabdbaba",
        "ababdbOOabababab",
        "babababababOOaba",
        "abcbabdbabOLHOab",
        "babababababOOaba",
        "abababcbabababab",
    ],
}

TEXTURES = {
    "raw_carbon": [
        "................",
        "................",
        ".....OOOOO......",
        "....OLLDLLO.....",
        "...OLHLLLDLO....",
        "..OLLLLDLLLLO...",
        "..OLHLLLLLDLO...",
        "..OLLLDLLLLLO...",
        "..ODLLLLLHLLO...",
        "...OLLDLLLLO....",
        "....ODLLLDO.....",
        ".....OOOOO......",
        "................",
        "................",
        "................",
        "................",
    ],
    "carbon_ingot": [
        "................",
        "................",
        "................",
        "........OOOOOO..",
        ".......OHHHHHO..",
        "......OHWLLLLDO.",
        ".....OHLLLLLLDO.",
        "....OHLLLLLLDO..",
        "...OHLLLLLLDO...",
        "..OHLLLLLLDO....",
        "..OLLLLLLDO.....",
        "..ODDDDDDO......",
        "...OOOOOO.......",
        "................",
        "................",
        "................",
    ],
    "carbon_fiber": [
        "................",
        "..OOOOOOOOOOOO..",
        ".OHLDHLDHLDHLDO.",
        ".OLDHLDHLDHLDHO.",
        ".ODHLDHLDHLDHLO.",
        ".OHLDHLDHLDHLDO.",
        ".OLDHLDHLDHLDHO.",
        ".ODHLDHLDHLDHLO.",
        ".OHLDHLDHLDHLDO.",
        ".OLDHLDHLDHLDHO.",
        ".ODHLDHLDHLDHLO.",
        ".OHLDHLDHLDHLDO.",
        ".OLDHLDHLDHLDHO.",
        "..OOOOOOOOOOOO..",
        "................",
        "................",
    ],
    "carbon_stick": [
        "............OO..",
        "...........OHLO.",
        "..........OHLDO.",
        ".........OHLDO..",
        "........OHLDO...",
        ".......OHLDO....",
        "......OHLDO.....",
        ".....OHLDO......",
        "....OHLDO.......",
        "...OHLDO........",
        "..OHLDO.........",
        ".OHLDO..........",
        ".OLDO...........",
        ".OOO............",
        "................",
        "................",
    ],
}

# 장비 아이템 텍스처 (바닐라 실루엣을 따른 카본 톤)
TEXTURES.update({
    "carbon_sword": [
        "..............KK",
        ".............KGK",
        "............KGgK",
        "...........KGgGK",
        "..........KGgGK.",
        ".........KGgGK..",
        "........KGgGK...",
        "...KK..KGgGK....",
        "...KkK.KGgK.....",
        "....KkKGgK......",
        ".....KkgK.......",
        "....KkKkKK......",
        "...KkK.KkK......",
        "..KkK...KK......",
        ".KkK............",
        ".KK.............",
    ],
    "carbon_pickaxe": [
        ".....OOOOOO.....",
        "...OOHLLLLHOO...",
        "..OHLLOOOOLLHO..",
        "..OLLO....OLLO..",
        "..OLO...OO.OLO..",
        "..OO...OHDO.OO..",
        "......OHDO......",
        ".....OHDO.......",
        "....OHDO........",
        "...OHDO.........",
        "..OHDO..........",
        ".OHDO...........",
        ".ODO............",
        ".OO.............",
        "................",
        "................",
    ],
    "carbon_axe": [
        "......OOOO......",
        ".....OHLLLO.....",
        "....OHLLLLLO....",
        "....OLLLLLLO....",
        "....OLLOOLLO....",
        ".....OOHDOOO....",
        "......OHDO......",
        ".....OHDO.......",
        "....OHDO........",
        "...OHDO.........",
        "..OHDO..........",
        ".OHDO...........",
        ".ODO............",
        ".OO.............",
        "................",
        "................",
    ],
    "carbon_shovel": [
        "...........OOO..",
        "..........OHLLO.",
        ".........OHLLLO.",
        ".........OLLLLO.",
        ".........OLLLO..",
        "........OOLLO...",
        ".......OHDOO....",
        "......OHDO......",
        ".....OHDO.......",
        "....OHDO........",
        "...OHDO.........",
        "..OHDO..........",
        ".OHDO...........",
        ".ODO............",
        ".OO.............",
        "................",
    ],
    "carbon_hoe": [
        ".......OOOOOOO..",
        "......OHLLLLLLO.",
        ".....OHLOOOOOOO.",
        ".....OLO........",
        "....OHDO........",
        "...OHDO.........",
        "..OHDO..........",
        ".OHDO...........",
        ".ODO............",
        ".OO.............",
        "................",
        "................",
        "................",
        "................",
        "................",
        "................",
    ],
    "carbon_helmet": [
        "................",
        "................",
        "................",
        "....OOOOOOOO....",
        "...OHHLLLLLLO...",
        "..OHLLLLLLLLLO..",
        "..OLLLLLLLLLLO..",
        "..OLLLLLLLLLLO..",
        "..OLLOOOOOOLLO..",
        "..OLO......OLO..",
        "..OOO......OOO..",
        "................",
        "................",
        "................",
        "................",
        "................",
    ],
    "carbon_chestplate": [
        "................",
        "..OOOO....OOOO..",
        ".OHLLLOOOOLLLLO.",
        ".OLLLLOLLOLLLLO.",
        ".OLLLOLLLLOLLLO.",
        ".OLLOLLLLLLOLLO.",
        ".OOOLLLLLLLLOOO.",
        "...OLLLDLLLLO...",
        "...OLLLLLLLLO...",
        "...OLLLLLLLLO...",
        "...OLLLLLLLLO...",
        "...OLLLLLLLLO...",
        "...OLLLLLLLLO...",
        "...OOOOOOOOOO...",
        "................",
        "................",
    ],
    "carbon_leggings": [
        "................",
        "................",
        "...OOOOOOOOOO...",
        "..OHLLLLLLLLLO..",
        "..OLLLLLLLLLLO..",
        "..OLLLOOOOLLLO..",
        "..OLLO....OLLO..",
        "..OLLO....OLLO..",
        "..OLLO....OLLO..",
        "..OLLO....OLLO..",
        "..OLLO....OLLO..",
        "..OLLO....OLLO..",
        "..OOOO....OOOO..",
        "................",
        "................",
        "................",
    ],
    "carbon_boots": [
        "................",
        "................",
        "................",
        "................",
        "................",
        "...OOOO..OOOO...",
        "...OLLO..OLLO...",
        "...OLLO..OLLO...",
        "...OLLO..OLLO...",
        "..OHLLO.OHLLO...",
        ".OHLLLO.OHLLLO..",
        ".OLLLLO.OLLLLO..",
        ".OOOOOO.OOOOOO..",
        "................",
        "................",
        "................",
    ],
})


def weave(x, y):
    """카본 파이버 직조(트윌) 패턴의 한 픽셀.
    4x4 격자가 체스판처럼 번갈아 가며 세로결 / 가로결을 가진다.
    각 격자 안은 어두운 선 → 하이라이트 → 중간 → 그림자 순서로 섬유 다발의 입체감을 낸다."""
    cell_x, cell_y = x // 4, y // 4
    vertical = (cell_x + cell_y) % 2 == 0
    k = (x % 4) if vertical else (y % 4)
    return "OHLD"[k]


def apply_weave(rows, fill="L"):
    """아이콘 텍스처에서 fill 문자(평평한 면)를 직조 패턴으로 치환한다."""
    out = []
    for y, row in enumerate(rows):
        out.append("".join(weave(x, y) if ch == fill else ch for x, ch in enumerate(row)))
    return out


def gloss(x, y):
    """착용 갑옷용 촘촘한 대각선 카본 패턴 (어두운 3톤).
    검정 결 → 살짝 검정 바탕 → 진한 회색 광택 줄. 광택 줄은 두 단계 회색이 번갈아 가며 은은하게 빛난다."""
    k = (x + y) % 4
    if k == 0:
        return "K"
    if k == 2:
        return "g" if ((x + y) // 4) % 2 == 0 else "G"
    return "k"


def _blank(w=64, h=32):
    return [["."] * w for _ in range(h)]


def _fill(grid, x0, y0, x1, y1, pattern=gloss):
    """[x0,x1) x [y0,y1) 영역을 패턴으로 채운다."""
    for y in range(y0, y1):
        for x in range(x0, x1):
            grid[y][x] = pattern(x, y)


def _clear(grid, x0, y0, x1, y1):
    for y in range(y0, y1):
        for x in range(x0, x1):
            grid[y][x] = "."


# ── 바닐라 갑옷 텍스처(64x32) UV 레이아웃 ──
#  머리(8x8x8) 원점 (0,0):   윗면 (8,0)-(16,8)   아랫면 (16,0)-(24,8)
#                            옆/앞/옆/뒤 (0,8)-(32,16), 앞면은 (8,8)-(16,16)
#  몸통(8x12x4) 원점 (16,16): 윗면 (20,16)-(28,20) 아랫면 (28,16)-(36,20), 옆면들 (16,20)-(40,32)
#  팔(4x12x4)   원점 (40,16): 윗면 (44,16)-(48,20) 아랫면 (48,16)-(52,20), 옆면들 (40,20)-(56,32)
#  다리(4x12x4) 원점 (0,16):  윗면 (4,16)-(8,20)   아랫면 (8,16)-(12,20),  옆면들 (0,20)-(16,32)
#  layer_1 = 투구(머리) + 갑옷(몸통, 팔) + 부츠(다리 아래쪽)
#  layer_2 = 레깅스(몸통 허리띠 + 다리)

def armor_layer_1_rows():
    g = _blank()
    # 투구 (네더라이트형): 윗면 + 옆/뒤면 전체 높이, 아랫면은 비워서 턱 밑 개방
    _fill(g, 8, 0, 16, 8)
    _fill(g, 0, 8, 32, 16)
    # 앞면(8..15, 8..15): 위 3줄 이마 가리개, 양옆 기둥(x=8, 15) 전체 높이
    _clear(g, 9, 11, 15, 16)
    # 가운데 코 가리개: x=11..12, y=11..13 (눈 양쪽은 뚫림)
    _fill(g, 11, 11, 13, 14)
    # 갑옷: 몸통 전체
    _fill(g, 16, 16, 40, 32)
    # 슬림: 팔은 소매 없이 어깨 보호대만 (팔 윗면 + 옆면 위 4줄)
    _fill(g, 44, 16, 48, 20)
    _fill(g, 40, 20, 56, 24)
    # 슬림: 부츠는 다리 옆면 아래 4줄 + 발바닥
    _fill(g, 0, 28, 16, 32)
    _fill(g, 8, 16, 12, 20)
    return ["".join(r) for r in g]


def armor_layer_2_rows():
    g = _blank()
    # 레깅스: 몸통 옆면 아래 3줄(허리띠) + 다리 옆면 전체 + 다리 아랫면
    _fill(g, 16, 29, 40, 32)
    _fill(g, 0, 20, 16, 32)
    _fill(g, 8, 16, 12, 20)
    return ["".join(r) for r in g]


# 대장간 형판: 바닐라 형판 실루엣(모서리 둥근 판) + 카본 톤 격자
TEXTURES["carbon_upgrade_smithing_template"] = [
    "................",
    "..KKKKKKKKKKKK..",
    ".KGGGGGGGGGGGGK.",
    ".KGkkkkkkkkkkGK.",
    ".KGkKKkkkkKKkGK.",
    ".KGkKgkkkkgKkGK.",
    ".KGkkkkKKkkkkGK.",
    ".KGkkkkKgkkkkGK.",
    ".KGkkkkKgkkkkGK.",
    ".KGkkkkKKkkkkGK.",
    ".KGkKgkkkkgKkGK.",
    ".KGkKKkkkkKKkGK.",
    ".KGkkkkkkkkkkGK.",
    ".KGGGGGGGGGGGGK.",
    "..KKKKKKKKKKKK..",
    "................",
]


def shield_weave(x, y):
    """카본 블록과 같은 점 격자 기하에 방패 전용 고대비 색을 입힌다."""
    if (x - y) % 3 == 1:
        return "T" if (y // 3) % 2 == 0 else "U"
    return "S"


def shield_rows():
    """64x64 방패 엔티티 텍스처. 바닐라 ShieldEntityModel UV:
    판(12x22x1)은 (0,0)부터 26x23 (앞면 u1..13 v1..23, 뒷면 u14..26), 손잡이(2x6x6)는 (26,0)부터 16x12.
    카본 블록과 같은 점 격자 직조로 채우고, 판의 앞/뒷면에 블록처럼 검정 테두리를 두른다."""
    g = _blank(64, 64)
    _fill(g, 0, 0, 26, 23, shield_weave)
    _fill(g, 26, 0, 42, 12, shield_weave)
    for u0 in (1, 14):  # 앞면, 뒷면
        for v in range(1, 23):
            g[v][u0] = "U"
            g[v][u0 + 11] = "U"
        for u in range(u0, u0 + 12):
            g[1][u] = "U"
            g[22][u] = "U"
    return ["".join(r) for r in g]


OUT_ENTITY = os.path.join(ASSETS, "entity")


def block_weave(x, y):
    """카본 블록과 같은 점 격자 직조: 살짝 검정 바탕에 (x - y) % 3 == 1 자리마다 점,
    점 색은 3줄 단위로 진한 회색 / 검정이 번갈아 → 대각선으로 흐르는 섬유 다발 느낌."""
    if (x - y) % 3 == 1:
        return "G" if (y // 3) % 2 == 0 else "K"
    return "k"


def apply_block_weave(rows, fills=("k", "G")):
    """아이콘의 면(살짝 검정 k, 진한 회색 G)을 블록 직조 패턴으로 치환. 외곽선(K)과 광택선(g)은 유지."""
    return ["".join(block_weave(x, y) if ch in fills else ch for x, ch in enumerate(row))
            for y, row in enumerate(rows)]


def darken(rows):
    """도구 아이콘을 어두운 3톤으로 재매핑: 외곽선 검정, 면 살짝 검정, 하이라이트 진한 회색."""
    table = {"O": "K", "D": "K", "L": "k", "H": "G", "W": "g"}
    return ["".join(table.get(ch, ch) for ch in row) for row in rows]


# 카본 원광석 아이템(원석)만 제외하고 모든 아이템 아이콘: 어두운 팔레트로 바꾼 뒤 블록 직조 패턴 적용
for _name in list(TEXTURES):
    if _name == "raw_carbon":
        TEXTURES[_name] = darken(TEXTURES[_name])
    else:
        TEXTURES[_name] = apply_block_weave(darken(TEXTURES[_name]))


import math


def bow_rows(pull=None):
    """16x16 활, 바닐라 활과 같은 구도.
    림: (14,1)~(1,14) 현에서 좌상단으로 불룩한 호를 '영역'으로 칠한다 (픽셀마다 안/밖 판정 → 구멍 없음).
    시위: 두 끝을 잇는 끊김 없는 한 줄. pull이 있으면 우하단으로 당겨지고 화살이 걸린다."""
    g = _blank(16, 16)

    def put(x, y, ch):
        if 0 <= x < 16 and 0 <= y < 16:
            g[y][x] = ch

    # 좌표계: 현 방향 t (0=우상단 끝, 13=좌하단 끝), 수직 방향 perp (+ = 좌상단으로)
    def bulge(t):
        return 3.5 * math.sin(math.pi * t / 13)

    for y in range(16):
        for x in range(16):
            t = ((14 - x) + (y - 1)) / 2
            perp = ((14 - x) - (y - 1)) / 2
            if not (-0.75 <= t <= 13.75):
                continue
            tc = min(max(t, 0.0), 13.0)
            dist = abs(perp - bulge(tc)) + (0.0 if 0 <= t <= 13 else abs(t - tc))
            if not (0 <= t <= 13):
                if dist <= 0.75:
                    put(x, y, "K")
                continue
            if dist <= 0.5:                       # 림 안쪽 면 (두께 3 대각 픽셀)
                face = "G" if (x + y) % 4 == 0 else "k"
                if 5.0 <= t <= 8.0:               # 손잡이
                    face = "G"
                put(x, y, face)
            elif dist <= 1.25:                    # 외곽선
                put(x, y, "K")

    # 시위: 끊김 없는 한 줄 (당김만큼 우하단으로)
    pulled = 0.0 if pull is None else pull
    def line(x0, y0, x1, y1, ch):
        dx, dy = abs(x1 - x0), abs(y1 - y0)
        sx, sy = (1 if x1 > x0 else -1), (1 if y1 > y0 else -1)
        err = dx - dy
        while True:
            put(x0, y0, ch)
            if x0 == x1 and y0 == y1:
                break
            e2 = 2 * err
            if e2 > -dy:
                err -= dy; x0 += sx
            if e2 < dx:
                err += dx; y0 += sy

    pts = []
    for t in range(14):
        sft = pulled * 4 * math.sin(math.pi * t / 13)
        pts.append((round(14 - t + sft), round(1 + t + sft)))
    for (x0, y0), (x1, y1) in zip(pts, pts[1:]):
        line(x0, y0, x1, y1, "g")

    # 화살: 시위 중앙에서 좌상단(발사 방향)으로. 깃 g, 대 T, 촉 W
    if pull is not None:
        c = 8 + round(pulled * 4)
        for u in range(-1, 9):
            ch = "g" if u < 1 else ("W" if u >= 7 else "T")
            put(c - u, c - u, ch)
    return ["".join(r) for r in g]


OUT_ARMOR = os.path.join(ASSETS, "models", "armor")


def write_png(path, rows):
    h = len(rows)
    w = len(rows[0])
    assert all(len(r) == w for r in rows), f"{path}: 모든 줄의 길이가 같아야 함"
    raw = bytearray()
    for row in rows:
        raw.append(0)  # 필터 없음
        for ch in row:
            rgba = PALETTE[ch]
            raw.extend(rgba if rgba else (0, 0, 0, 0))

    def chunk(tag, data):
        body = tag + data
        return struct.pack(">I", len(data)) + body + struct.pack(">I", zlib.crc32(body) & 0xFFFFFFFF)

    png = b"\x89PNG\r\n\x1a\n"
    png += chunk(b"IHDR", struct.pack(">IIBBBBB", w, h, 8, 6, 0, 0, 0))
    png += chunk(b"IDAT", zlib.compress(bytes(raw), 9))
    png += chunk(b"IEND", b"")
    with open(path, "wb") as f:
        f.write(png)


if __name__ == "__main__":
    os.makedirs(OUT, exist_ok=True)
    for name, rows in TEXTURES.items():
        write_png(os.path.join(OUT, f"{name}.png"), rows)
        print("생성: item/" + name + ".png")
    os.makedirs(OUT_BLOCK, exist_ok=True)
    for name, rows in BLOCK_TEXTURES.items():
        write_png(os.path.join(OUT_BLOCK, f"{name}.png"), rows)
        print("생성: block/" + name + ".png")
    os.makedirs(OUT_ARMOR, exist_ok=True)
    write_png(os.path.join(OUT_ARMOR, "carbon_layer_1.png"), armor_layer_1_rows())
    write_png(os.path.join(OUT_ARMOR, "carbon_layer_2.png"), armor_layer_2_rows())
    print("생성: models/armor/carbon_layer_1.png, carbon_layer_2.png")
    os.makedirs(OUT_ENTITY, exist_ok=True)
    write_png(os.path.join(OUT_ENTITY, "carbon_shield.png"), shield_rows())
    print("생성: entity/carbon_shield.png")
    for name, pull in (("carbon_bow", None), ("carbon_bow_pulling_0", 0.0),
                       ("carbon_bow_pulling_1", 0.5), ("carbon_bow_pulling_2", 1.0)):
        write_png(os.path.join(OUT, f"{name}.png"), bow_rows(pull))
        print("생성: item/" + name + ".png")
