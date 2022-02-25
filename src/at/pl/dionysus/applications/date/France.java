package at.pl.dionysus.applications.date;

import at.pl.razer.chroma.effects.Color;
import at.pl.razer.chroma.effects.ComplexStaticEffect;

public class France extends ComplexStaticEffect {
    public France() {super(map -> Color.fill(map, (row, col) -> col <= map[row].length * 1 / 3 ? 0x000092  : col <= map[row].length * 2 / 3 ? 0xFFFFFF : 0xE1000F)); }
}
