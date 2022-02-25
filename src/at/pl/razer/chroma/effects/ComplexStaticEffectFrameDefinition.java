package at.pl.razer.chroma.effects;

import at.pl.razer.chroma.Device;
import at.pl.razer.chroma.EffectFrameDefinition;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public class ComplexStaticEffectFrameDefinition implements EffectFrameDefinition {

    protected static final int KEYBOARD_ROWS = 6;
    protected static final int KEYBOARD_COLS = 22;
    protected static final int MOUSE_ROWS = 9;
    protected static final int MOUSE_COLS = 7;
    protected static final int MOUSEPAD_ROWS = 15;
    protected static final int MOUSEPAD_COLS = 1;

    private final Device device;
    private final int[][] colors;

    public ComplexStaticEffectFrameDefinition(Device device, BiFunction<Integer, Integer, Integer> function) {
        this(device, map -> Color.fill(map, function));
    }

    public ComplexStaticEffectFrameDefinition(Device device, Consumer<int[][]> function) {
        this(device);
        function.accept(colors);
    }

    public ComplexStaticEffectFrameDefinition(Device device) {
        this.device = device;
        switch (device) {
            case KEYBOARD:
                colors = new int[KEYBOARD_ROWS][KEYBOARD_COLS];
                break;
            case MOUSE:
                colors = new int[MOUSE_ROWS][MOUSE_COLS];
                break;
            case MOUSEPAD:
                colors = new int[MOUSEPAD_ROWS][MOUSEPAD_COLS];
                break;
            default:
                throw new IllegalArgumentException(device.toString());
        }
    }

    @Override
    public Device getDevice() {
        return device;
    }

    protected int[][] getColors() {
        return colors;
    }

    @Override
    public String getDefinition() {
        StringBuilder map = new StringBuilder("[");
        for (int row = 0; row < colors.length; row++) {
            if (row > 0) map.append(",");
            if (colors[row].length == 1) {
                map.append(Color.rgb2bgr(colors[row][0] & 0x00FFFFFF));
            } else {
                map.append("[");
                for (int col = 0; col < colors[row].length; col++) {
                    if (col > 0) map.append(",");
                    map.append(Color.rgb2bgr(colors[row][col] & 0x00FFFFFF));
                }
                map.append("]");
            }
        }
        map.append("]");
        String effect = device == Device.MOUSE ? "CHROMA_CUSTOM2" : "CHROMA_CUSTOM";
        return "{\"effect\": \"" + effect + "\", \"param\": " + map + "}";
    }
}
