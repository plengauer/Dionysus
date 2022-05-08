package at.pl.dionysus.applications.date;

import at.pl.razer.chroma.effects.Color;
import at.pl.razer.chroma.effects.ComplexStaticEffect;

public class StripedFlag extends ComplexStaticEffect {
    public StripedFlag(int... colors) {
        super(map -> Color.fill(map, (row, col) -> colors[row / colors.length]));
    }
}
