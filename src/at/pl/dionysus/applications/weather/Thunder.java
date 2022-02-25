package at.pl.dionysus.applications.weather;

import at.pl.razer.chroma.Device;
import at.pl.razer.chroma.Effect;
import at.pl.razer.chroma.EffectFrame;
import at.pl.razer.chroma.EffectFrameDefinition;
import at.pl.razer.chroma.effects.Color;
import at.pl.razer.chroma.effects.ComplexStaticEffectFrameDefinition;
import at.pl.razer.chroma.effects.StaticEffect;

public class Thunder implements Effect {
    private final long period;
    private final Effect inner;

    private long countdown;

    public Thunder(long period, Effect inner) {
        this.period = period;
        this.inner = inner;

        countdown = period;
    }

    @Override
    public boolean hasNext() {
        return inner.hasNext();
    }

    @Override
    public EffectFrame next() {
        if (countdown < 0) {
            countdown = period;
            return new EffectFrame() {
                @Override
                public long getDuration() {
                    return 500;
                }

                @Override
                public EffectFrameDefinition[] getEffects() {
                    return new EffectFrameDefinition[] {
                            new ComplexStaticEffectFrameDefinition(Device.KEYBOARD, map -> {
                                int location = (int) (map[0].length * Math.random());
                                Color.fill(map, (row, col) -> col == location ? 0xFFC81F : 0);
                            }),
                            new StaticEffect.Definition(Device.MOUSE, 0),
                            new StaticEffect.Definition(Device.MOUSEPAD, 0)
                    };
                }
            };
        } else {
            EffectFrame frame = inner.next();
            countdown -= frame.getDuration();
            return frame;
        }
    }
}
