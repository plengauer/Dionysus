package at.pl.dionysus.applications.weather;

import at.pl.razer.chroma.Device;
import at.pl.razer.chroma.Effect;
import at.pl.razer.chroma.EffectFrame;
import at.pl.razer.chroma.EffectFrameDefinition;
import at.pl.razer.chroma.effects.ComplexStaticEffectFrameDefinition;
import at.pl.razer.chroma.effects.StaticEffect;

public class Sunny implements Effect {
    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public EffectFrame next() {
        return new Frame();
    }

    private static final class Frame implements EffectFrame {

        @Override
        public long getDuration() {
            return 1000 * 60 * 60;
        }

        @Override
        public EffectFrameDefinition[] getEffects() {
            return new EffectFrameDefinition[] {
                    new ComplexStaticEffectFrameDefinition(Device.KEYBOARD, (row, col) -> row <= 1 && col <= 1 ? 0xFFCC00 : Colors.SKY),
                    new StaticEffect.Definition(Device.MOUSE, Colors.SKY),
                    new StaticEffect.Definition(Device.MOUSEPAD, Colors.SKY)
            };
        }
    }
}
