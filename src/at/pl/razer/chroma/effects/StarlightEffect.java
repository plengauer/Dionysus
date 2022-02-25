package at.pl.razer.chroma.effects;

import at.pl.razer.chroma.Device;
import at.pl.razer.chroma.Effect;
import at.pl.razer.chroma.EffectFrame;

public class StarlightEffect implements Effect {

    private static final int FRAME_DURATION = 100;

    private final double coverage;
    private final int duration;
    private final int[] colors;

    private StarlightEffectFrameDefinition pk, pm, pmp;

    public StarlightEffect(double coverage, int duration, int... colors) {
        if (coverage < 0 || coverage > 1 || duration <= FRAME_DURATION || colors.length == 0) {
            throw new IllegalArgumentException();
        }
        this.coverage = coverage;
        this.duration = duration;
        this.colors = colors;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public EffectFrame next() {
        StarlightEffectFrameDefinition k = new StarlightEffectFrameDefinition(Device.KEYBOARD, coverage, duration, colors, FRAME_DURATION, pk);
        StarlightEffectFrameDefinition m = new StarlightEffectFrameDefinition(Device.MOUSE, coverage, duration, colors, FRAME_DURATION, pm);
        StarlightEffectFrameDefinition mp = new StarlightEffectFrameDefinition(Device.MOUSEPAD, coverage, duration, colors, FRAME_DURATION, pmp);
        try {
            return new StarlightEffectFrame(FRAME_DURATION, k, m, mp);
        } finally {
            pk = k;
            pm = m;
            pmp = mp;
        }
    }
}
