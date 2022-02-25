package at.pl.dionysus.applications.weather;

import at.pl.razer.chroma.Device;
import at.pl.razer.chroma.Effect;
import at.pl.razer.chroma.EffectFrame;
import at.pl.razer.chroma.EffectFrameDefinition;
import at.pl.razer.chroma.effects.Color;
import at.pl.razer.chroma.effects.ComplexStaticEffectFrameDefinition;

import java.util.function.Consumer;

public class Clouds implements Effect {

    private static int randomizedColor() {
        return Color.darker(Colors.CLOUDS, (int) (Math.random() * 25));
    }

    private final Consumer<int[][]> function;

    public static class Overcast extends Clouds {
        public Overcast() {
            super(map -> Color.fill(map, (row, col) -> row < map.length * 1 / 3 ? randomizedColor() : Colors.SKY));
        }
    }

    public static class Fog extends Clouds {
        public Fog() {
            super(map -> Color.fill(map, (row, col) -> randomizedColor()));
        }
    }

    public static class Mist extends Clouds {
        public Mist() {
            super(map -> Color.fill(map, (row, col) -> Colors.CLOUDS));
        }
    }

    public Clouds(double coverage, int height, int width) {
        this(map -> {
            if (coverage < 0 || coverage > 1 || height < 0 || width < 0) {
                throw new IllegalArgumentException();
            }
            Color.fill(map, (__, ___) -> Colors.SKY);
            int clouds = (int) (((map.length * map[0].length) / (height * width) * coverage) * 2 * Math.random());
            for (int i = 0; i < clouds; i++) {
                int row = (int) (map.length * Math.random());
                int col = (int) (map[row].length * Math.random());
                int myHeight = (int) (height * 2 * Math.random());
                int myWidth = (int) (width * 2 * Math.random());
                for (int r = row; r < Math.min(row + myHeight, map.length); r++) {
                    for (int c = col; c < Math.min(col + myWidth, map[row].length); c++) {
                        map[r][c] = randomizedColor();
                    }
                }
            }
        });
    }

    private Clouds(Consumer<int[][]> function) {
        this.function = function;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public EffectFrame next() {
        return new EffectFrame() {
            @Override
            public long getDuration() {
                return 1000 * 60 * 60;
            }

            @Override
            public EffectFrameDefinition[] getEffects() {
                return new EffectFrameDefinition[] {
                        new ComplexStaticEffectFrameDefinition(Device.KEYBOARD, function),
                        new ComplexStaticEffectFrameDefinition(Device.MOUSE, function),
                        new ComplexStaticEffectFrameDefinition(Device.MOUSEPAD, function)
                };
            }
        };
    }
}
