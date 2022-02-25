package at.pl.dionysus;

import at.pl.razer.chroma.Effect;
import at.pl.razer.chroma.EffectPlayer;
import at.pl.razer.chroma.SDK;
import at.pl.razer.chroma.effects.Color;
import at.pl.razer.chroma.effects.StarlightEffect;

import java.io.IOException;

public class TestFastChange {

    public static void main(String[] args) throws IOException, InterruptedException {
        for (int i = 0; i < 5; i++) {
            try(SDK sdk = new SDK("Razer Chroma SDK RESTful Test Application", "This is a REST interface test application")) {
                int color = Color.rgb((int) (0xFF * Math.random()), (int) (0xFF * Math.random()), (int) (0xFF * Math.random()));
                Effect effect = new StarlightEffect(0.5, 1000 * 2, color);
                try (EffectPlayer player = new EffectPlayer(sdk, effect)) {
                   player.join(1000 * 10);
                }
            }
        }
    }
}
