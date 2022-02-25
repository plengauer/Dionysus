package at.pl.dionysus.applications.alert;

import at.pl.razer.chroma.Device;
import at.pl.razer.chroma.Effect;
import at.pl.razer.chroma.EffectFrame;
import at.pl.razer.chroma.EffectFrameDefinition;
import at.pl.razer.chroma.effects.StaticEffect;

public class Alert implements Effect {

    private final long period;
    private final int color;

    private boolean on;

    public Alert(long period, int color) {
        this.period = period;
        this.color = color;
        on = false;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public EffectFrame next() {
        on = !on;
        return new EffectFrame() {

            @Override
            public long getDuration() {
                return period;
            }

            @Override
            public EffectFrameDefinition[] getEffects() {
                int color = on ? Alert.this.color : 0;
                return new EffectFrameDefinition[] {
                        new StaticEffect.Definition(Device.KEYBOARD, color),
                        new StaticEffect.Definition(Device.MOUSE, color),
                        new StaticEffect.Definition(Device.MOUSEPAD, color)
                };
            }
        };
    }
}
