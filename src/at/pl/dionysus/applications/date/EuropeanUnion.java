package at.pl.dionysus.applications.date;

import at.pl.razer.chroma.effects.Color;
import at.pl.razer.chroma.effects.ComplexStaticEffect;

public class EuropeanUnion extends ComplexStaticEffect {
    public EuropeanUnion() {
        super(map -> Color.fill(map, (row, col) -> row % 3 == 0 && col % 3 == 0 ? 0xFFFF00 /*0xFFC81F*/ : 0x0000FF));
    }
}
