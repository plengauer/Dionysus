package at.pl.dionysus.applications.weather;

import at.pl.razer.chroma.Device;
import at.pl.razer.chroma.Effect;
import at.pl.razer.chroma.EffectFrame;
import at.pl.razer.chroma.EffectFrameDefinition;
import at.pl.razer.chroma.effects.Color;
import at.pl.razer.chroma.effects.ComplexStaticEffectFrameDefinition;
import at.pl.razer.chroma.effects.StaticEffect;

import java.util.function.BiFunction;

public abstract class Falling implements Effect {

    private final long FRAME_DURATION = 1000 / 60;

    private final double coverage;
    private final long duration;
    private final boolean soft;
    private final int color;

    private Definition prev;

    protected Falling(double coverage, long duration, boolean soft, int color) {
        if (coverage < 0 || coverage > 1 || duration < 0 || duration < FRAME_DURATION) {
            throw new IllegalArgumentException();
        }
        this.coverage = coverage;
        this.duration = duration;
        this.soft = soft;
        this.color = color;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public EffectFrame next() {
        Definition definition = soft ? new SoftDefinition(prev) : new HardDefinition(prev);
        try {
            return new EffectFrame() {
                @Override
                public long getDuration() {
                    return soft ? FRAME_DURATION : duration;
                }

                @Override
                public EffectFrameDefinition[] getEffects() {
                    return new EffectFrameDefinition[] { definition, new StaticEffect.Definition(Device.MOUSE, 0), new StaticEffect.Definition(Device.MOUSEPAD, 0) };
                }
            };
        } finally {
            prev = definition;
        }
    }

    private abstract class Definition extends ComplexStaticEffectFrameDefinition {
        public Definition(BiFunction<Integer, Integer, Integer> function) { super(Device.KEYBOARD, function); }

        @Override
        public int[][] getColors() {
            return super.getColors();
        }
    }

    private class SoftDefinition extends Definition {

        public SoftDefinition(Definition prev) {
            super((row, col) -> {
                int TTL = (int) (duration / FRAME_DURATION) * 2;
                int ttl;
                if (prev != null && prev.getColors()[row][col] != 0) {
                    int value = prev.getColors()[row][col];
                    ttl = ((value >> (3*8)) & 0xFF) - 1;
                } else if (row == 0) {
                    ttl = (Math.random() < coverage * (1.0 * FRAME_DURATION / duration) ? TTL : 0);
                } else if (prev != null && prev.getColors()[row-1][col] != 0) {
                    if (((prev.getColors()[row - 1][col] >> (3 * 8)) & 0xFF) == TTL / 2) {
                        ttl = TTL;
                    } else {
                        ttl = 0;
                    }
                } else {
                    ttl = 0;
                }
                if (ttl == 0) {
                    return 0;
                } else {
                    double ratio;
                    if (ttl > TTL / 2) {
                        ratio = 1.0 * (TTL - ttl) / (TTL / 2);
                    } else {
                        ratio = 1.0 * ttl / (TTL / 2);
                    }
                    return Color.darker(color, ratio) | (ttl << (3*8));
                }
            });
        }
    }

    private class HardDefinition extends Definition {

        public HardDefinition(Definition prev) {
            super((row, col) -> row == 0 ? (Math.random() < coverage ? color : 0) : (prev != null ? prev.getColors()[row - 1][col] : 0));
        }
    }
}
