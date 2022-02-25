package at.pl.razer.chroma.effects;

import at.pl.razer.chroma.Device;
import at.pl.razer.chroma.EffectFrame;
import at.pl.razer.chroma.EffectFrameDefinition;

import java.util.function.Consumer;

public class ComplexStaticEffectFrame implements EffectFrame {

    private final Consumer<int[][]> function;

    public ComplexStaticEffectFrame(Consumer<int[][]> function) {
        this.function = function;
    }

    @Override
    public long getDuration() {
        return 1000 * 60 * 60 * 24;
    }

    @Override
    public EffectFrameDefinition[] getEffects() {
        return new EffectFrameDefinition[] {
                new ComplexStaticEffectFrameDefinition(Device.KEYBOARD, function),
                new ComplexStaticEffectFrameDefinition(Device.MOUSE, function),
                new ComplexStaticEffectFrameDefinition(Device.MOUSEPAD, function)
        };
    }
}
