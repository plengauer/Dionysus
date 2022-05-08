package at.pl.dionysus.applications.date;

import at.pl.razer.chroma.effects.StarlightEffect;

public class Easter extends StarlightEffect {

    public static int FIRST_YEAR = 2023;
    public static int[] MONTHS = { 4,  3,  4, 4,  3,  4, 4,  4,  4,  3,  4, 4 };
    public static int[]   DAYS = { 9, 31, 20, 5, 28, 16, 1, 21, 13, 28, 17, 9 };

    public Easter() {
        super(1, 1000 * 10, 0xFFFFFF, 0xFF69F7);
    }
}
