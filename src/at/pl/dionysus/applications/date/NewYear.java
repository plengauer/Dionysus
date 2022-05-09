package at.pl.dionysus.applications.date;

import at.pl.razer.chroma.Device;
import at.pl.razer.chroma.Effect;
import at.pl.razer.chroma.EffectFrame;
import at.pl.razer.chroma.EffectFrameDefinition;
import at.pl.razer.chroma.effects.Color;
import at.pl.razer.chroma.effects.ComplexStaticEffectFrameDefinition;

import java.util.Random;

public class NewYear implements Effect {

    private final long FRAME_DURATION = 100;

    private double x;
    private int age;
    private int color;

    public NewYear() {
        x = -1;
        age = 0;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public EffectFrame next() {
        Definition def = new Definition();
        if (age == -1) {
            x = Math.random();
            age = 0;
            color = switch (new Random().nextInt(3)) {
                case 0 -> 0xFF0000;
                case 1 -> 0x00FF00;
                case 2 -> 0x0000FF;
                default -> 0xFFFFFF;
            };
        } else if (age == 15) {
            age = -1;
        } else {
            age++;
        }
        return new EffectFrame() {
            @Override
            public long getDuration() {
                return FRAME_DURATION;
            }

            @Override
            public EffectFrameDefinition[] getEffects() {
                return new EffectFrameDefinition[] { def };
            }
        };
    }

    private class Definition extends ComplexStaticEffectFrameDefinition {
        public Definition() {
            super(Device.KEYBOARD, map -> Color.fill(map, (row, col) -> {
                if (age < 0) return 0x000000;
                int cc = (int) (map[row].length * x);
                int rc = 2;
                int distance = age - 7;
                if (distance > 0 && Math.abs(row - rc) < distance && Math.abs(col - cc) < distance && (row == rc || col == cc || Math.abs(row - rc) == Math.abs(col - cc))) return color;
                if (col == cc && map.length - row < Math.min(age, 6)) return 0xFFFFFF;
                return 0x000000;
            }));
        }
    }
}
