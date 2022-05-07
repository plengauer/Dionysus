package at.pl.dionysus.applications.date;

import at.pl.razer.chroma.effects.Color;
import at.pl.razer.chroma.effects.ComplexStaticEffect;

public class GayPride extends ComplexStaticEffect {
    public GayPride() {
        super(map -> Color.fill(map, (row, col) -> {
                 if (row < map.length * 1 / 6) return 0xE50000;
            else if (row < map.length * 2 / 6) return 0xFF8D00;
            else if (row < map.length * 3 / 6) return 0xFFEE00;
            else if (row < map.length * 4 / 6) return 0x028121;
            else if (row < map.length * 5 / 6) return 0x004CFF;
            else if (row < map.length * 6 / 6) return 0x770088;
            else return 0x000000;
        }));
    }
}
