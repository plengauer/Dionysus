package at.pl.dionysus.applications.date;

import at.pl.razer.chroma.effects.Color;
import at.pl.razer.chroma.effects.ComplexStaticEffect;

public class Japan extends ComplexStaticEffect {

    private static double DIAMETER_RATIO = 0.75;

    public Japan() {
        super(map -> {
            int size = Math.max(1, (int) (Math.min(map.length, map[0].length) * DIAMETER_RATIO));
            int radius = size / 2;
            int cr = map.length / 2;
            int cc = map[0].length / 2;
            Color.fill(map, (row, col) -> Math.sqrt(Math.pow(row - cr, 2) + Math.pow(col - cc, 2)) <= radius ? 0xFF0000 : 0xFFFFFF);
        });
    }
}
