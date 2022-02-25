package at.pl.razer.chroma.effects;

import at.pl.razer.chroma.EffectFrame;
import at.pl.razer.chroma.EffectFrameDefinition;

public class StarlightEffectFrame implements EffectFrame {

    private final long duration;
    private final StarlightEffectFrameDefinition[] defs;

    public StarlightEffectFrame(long duration, StarlightEffectFrameDefinition... defs) {
        this.duration = duration;
        this.defs = defs;
    }

    @Override
    public long getDuration() {
        return duration;
    }

    @Override
    public EffectFrameDefinition[] getEffects() {
        return defs;
    }
}
