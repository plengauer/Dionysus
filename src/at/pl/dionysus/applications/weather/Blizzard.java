package at.pl.dionysus.applications.weather;

import at.pl.razer.chroma.Device;
import at.pl.razer.chroma.EffectFrame;
import at.pl.razer.chroma.EffectFrameDefinition;
import at.pl.razer.chroma.Effect;
import at.pl.razer.chroma.effects.ComplexStaticEffectFrameDefinition;
import at.pl.razer.chroma.effects.StaticEffect;

public class Blizzard implements Effect {

    private Definition prev;

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public EffectFrame next() {
        Definition definition = new Definition(prev);
        try {
            return new EffectFrame() {
                @Override
                public long getDuration() {
                    return 50;
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

    private static class Definition extends ComplexStaticEffectFrameDefinition {
        public Definition(Definition prev) {
            super(Device.KEYBOARD, (row, col) -> col == 0 ? (Math.random() < 0.1 ? Colors.SNOW : 0) : (prev != null ? prev.getColors()[row][col-1] : 0));
        }
    }
}
