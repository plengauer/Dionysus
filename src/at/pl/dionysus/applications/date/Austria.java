package at.pl.dionysus.applications.date;

import at.pl.razer.chroma.effects.Color;
import at.pl.razer.chroma.effects.ComplexStaticEffect;

public class Austria extends ComplexStaticEffect {
    public Austria() { super(map -> Color.fill(map, (row, col) -> row < map.length * 1 / 3 || map.length * 2 / 3 <= row ? 0xFF0000 : 0xFFFFFF)); }
}
