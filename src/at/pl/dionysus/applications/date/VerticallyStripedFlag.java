package at.pl.dionysus.applications.date;

import at.pl.razer.chroma.effects.Color;
import at.pl.razer.chroma.effects.ComplexStaticEffect;

public class VerticallyStripedFlag extends ComplexStaticEffect {
    public VerticallyStripedFlag(int... colors) {
        super(map -> Color.fill(map, (row, col) -> colors[Math.min(colors.length - 1, col / Math.max(1, map[row].length / colors.length))]));
    }
}
