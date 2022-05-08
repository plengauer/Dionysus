package at.pl.dionysus.applications.date;

import at.pl.razer.chroma.effects.Color;
import at.pl.razer.chroma.effects.ComplexStaticEffect;

public class USA extends ComplexStaticEffect {

    public USA() {
        super(map -> Color.fill(map, (row, col) -> {
            int l = (int) (Math.min(map.length, map[row].length) * 0.5);
            if (row < l && col < l) return 0x0000FF;
            else if (row % 2 == 0) return 0xFF0000;
            else return 0xFFFFFF;
        }));
    }
}
