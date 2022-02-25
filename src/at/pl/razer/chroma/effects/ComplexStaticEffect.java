package at.pl.razer.chroma.effects;

import at.pl.razer.chroma.Effect;
import at.pl.razer.chroma.EffectFrame;

import java.util.Objects;
import java.util.function.Consumer;

public class ComplexStaticEffect implements Effect {

    private final Consumer<int[][]> function;

    public ComplexStaticEffect(Consumer<int[][]> function) {
        Objects.requireNonNull(function);
        this.function = function;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public EffectFrame next() {
        return new ComplexStaticEffectFrame(function);
    }
}
