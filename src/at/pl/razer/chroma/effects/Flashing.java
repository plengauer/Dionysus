package at.pl.razer.chroma.effects;

import at.pl.razer.chroma.Device;
import at.pl.razer.chroma.Effect;
import at.pl.razer.chroma.EffectFrame;
import at.pl.razer.chroma.EffectFrameDefinition;

public class Flashing implements Effect {

    private final int duration;
    private final int[] colors;
    private int index;

    public Flashing(int duration, int... colors) {
        this.duration = duration;
        this.colors = colors;
        index = 0;
    }

    @Override
    public boolean hasNext() {
        return index < colors.length;
    }

    @Override
    public EffectFrame next() {
        if (index >= colors.length) {
            return null;
        } else {
            int color = colors[index++];
            return new EffectFrame() {
                @Override
                public long getDuration() {
                    return duration;
                }

                @Override
                public EffectFrameDefinition[] getEffects() {
                    return new EffectFrameDefinition[] { new StaticEffect.Definition(Device.KEYBOARD, color),  new StaticEffect.Definition(Device.MOUSE, color),  new StaticEffect.Definition(Device.MOUSEPAD, color) };
                }
            };
        }
    }
}
