package at.pl.dionysus.applications.date;

import at.pl.razer.chroma.effects.Color;
import at.pl.razer.chroma.effects.ComplexStaticEffect;

public class HorizontallyStripedFlag extends ComplexStaticEffect {
    public HorizontallyStripedFlag(int... colors) {
        super(map -> Color.fill(map, (row, col) -> colors[Math.min(colors.length - 1, row / Math.max(1, map.length / colors.length))]));
    }
}
/*
colors = { A, B, C }
colors.length = 3
map = [6, 21]
r = 6
c = 21
l = 3

r | h | cl || r / (h / cl)
--+---+----++---
0 | 6 |  3 || 0 / (6 / 3) = 0 / 2 = 0
1 | 6 |  3 || 1 / (6 / 3) = 1 / 2 = 0
2 | 6 |  3 || 2 / (6 / 3) = 2 / 2 = 1
3 | 6 |  3 || 3 / (6 / 3) = 3 / 2 = 1
4 | 6 |  3 || 4 / (6 / 3) = 4 / 2 = 2
5 | 6 |  3 || 5 / (6 / 3) = 5 / 2 = 2

r | h | cl || r / (h / cl)
--+---+----++---
0 | 9 |  2 || 0 / (9 / 2) = 0 / 4 = 0
1 | 9 |  2 || 1 / (9 / 2) = 1 / 4 = 0
2 | 9 |  2 || 2 / (9 / 2) = 2 / 4 = 0
3 | 9 |  2 || 3 / (9 / 2) = 3 / 4 = 0
4 | 9 |  2 || 4 / (9 / 2) = 4 / 4 = 1
5 | 9 |  2 || 5 / (9 / 2) = 5 / 4 = 1
6 | 9 |  2 || 6 / (9 / 2) = 6 / 4 = 1
7 | 9 |  2 || 7 / (9 / 2) = 7 / 4 = 1
8 | 9 |  2 || 8 / (9 / 2) = 8 / 4 = 2


 */
