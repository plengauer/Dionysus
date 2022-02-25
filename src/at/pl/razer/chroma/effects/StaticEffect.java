package at.pl.razer.chroma.effects;

import at.pl.razer.chroma.Device;
import at.pl.razer.chroma.Effect;
import at.pl.razer.chroma.EffectFrame;
import at.pl.razer.chroma.EffectFrameDefinition;


public class StaticEffect implements Effect {

    private final int color;

    public StaticEffect(int color) {
        this.color = color;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public EffectFrame next() {
        return new Frame(color);
    }

    private static final class Frame implements EffectFrame {

        private final int color;

        public Frame(int color) {
            this.color = color;
        }

        @Override
        public long getDuration() {
            return 1000 * 60 * 60;
        }

        @Override
        public EffectFrameDefinition[] getEffects() {
            return new EffectFrameDefinition[] { new Definition(Device.KEYBOARD, color), new Definition(Device.MOUSE, color), new Definition(Device.MOUSEPAD, color) };
        }
    }

    public static final class Definition implements EffectFrameDefinition {

        private final Device device;
        private final int color;

        public Definition(Device device, int color) {
            this.device = device;
            this.color = color;
        }

        @Override
        public Device getDevice() {
            return device;
        }

        @Override
        public String getDefinition() {
            return "{\n" +
                    "    \"effect\": \"CHROMA_STATIC\",\n" +
                    "    \"param\": {\n" +
                    "        \"color\": " + Color.rgb2bgr(color) + "\n" +
                    "    }\n" +
                    "}";
        }
    }
}
