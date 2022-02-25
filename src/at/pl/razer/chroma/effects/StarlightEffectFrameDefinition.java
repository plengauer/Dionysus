package at.pl.razer.chroma.effects;

import at.pl.razer.chroma.Device;

public class StarlightEffectFrameDefinition extends ComplexStaticEffectFrameDefinition {

    public StarlightEffectFrameDefinition(Device device, double coverage, int starDuration, int[] colors, int frameDuration, StarlightEffectFrameDefinition prev) {
        super(device, map -> {
            if (starDuration < frameDuration) {
                throw new IllegalArgumentException();
            }
            int totalStars = (int) (map.length * map[0].length * coverage);
            int ttl = starDuration / frameDuration;
            double newStars = 1.0 * totalStars / ttl;
            Color.fill(map, (row, col) -> {
                if (prev != null && prev.getColors()[row][col] != 0) {
                    int cttl = (prev.getColors()[row][col] >> (3*8)) & 0xFF;
                    cttl -= 1;
                    if (cttl == 0) {
                        return 0;
                    } else {
                        return (prev.getColors()[row][col] & 0x00FFFFFF) | (cttl << (3*8));
                    }
                } else if (Math.random() < (1.0 * newStars / (map.length * map[0].length))) {
                    return colors[(int) (Math.random() * colors.length)] | (ttl << (3*8));
                } else {
                    return 0;
                }
            });
        });
    }
}
