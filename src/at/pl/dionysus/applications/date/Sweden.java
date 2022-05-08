package at.pl.dionysus.applications.date;

import at.pl.razer.chroma.effects.Color;
import at.pl.razer.chroma.effects.ComplexStaticEffect;

public class Sweden extends ComplexStaticEffect {

    public Sweden() {
        super(map -> Color.fill(map, (row, col) -> row == map.length / 2 || col == map[row].length / 4 ? 0xFFFF00 : 0x0000FF));
    }
}
