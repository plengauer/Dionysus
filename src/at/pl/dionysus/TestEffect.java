package at.pl.dionysus;

import at.pl.razer.chroma.Effect;
import at.pl.razer.chroma.EffectPlayer;
import at.pl.razer.chroma.SDK;
import at.pl.razer.chroma.effects.StarlightEffect;

import java.io.IOException;

public class TestEffect {

    public static void main(String[] args) throws IOException, InterruptedException {
        try(SDK sdk = new SDK("Razer Chroma SDK RESTful Test Application", "This is a REST interface test application")) {
            Effect effect = new StarlightEffect(0.1, 1000 * 10, 0xFFFFFF);
            try (EffectPlayer player = new EffectPlayer(sdk, effect)) {
                player.join(1000 * 60);
            }
        }
    }
}
