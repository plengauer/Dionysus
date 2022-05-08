package at.pl.dionysus.applications.date;

import at.pl.razer.chroma.effects.Color;
import at.pl.razer.chroma.effects.ComplexStaticEffect;

public class Finland extends ComplexStaticEffect {

    public Finland() {
        super(map -> Color.fill(map, (row, col) -> row == map.length / 2 || col == map[row].length / 4 ? 0x0000FF : 0xFFFFFF));
    }
}
