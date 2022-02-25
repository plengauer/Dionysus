package at.pl.dionysus.applications.date;

import at.pl.razer.chroma.effects.Color;
import at.pl.razer.chroma.effects.ComplexStaticEffect;

public class Germany extends ComplexStaticEffect {
    public Germany() { super(map -> Color.fill(map, (row, col) -> row < map.length * 1 / 3 ? 0x000000 : row < map.length * 2 / 3 ? 0xFF0000 : 0xFFCC00)); }
}
