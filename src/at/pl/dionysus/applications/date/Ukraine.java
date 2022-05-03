package at.pl.dionysus.applications.date;

import at.pl.razer.chroma.effects.Color;
import at.pl.razer.chroma.effects.ComplexStaticEffect;

public class Ukraine extends ComplexStaticEffect {
    public Ukraine() { super(map -> Color.fill(map, (row, col) -> row < map.length * 1 / 2 ? 0x0057B8 : 0xFFD700)); }
}
