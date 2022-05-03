package at.pl.dionysus;

import at.pl.dionysus.applications.date.NewYear;
import at.pl.razer.chroma.Effect;
import at.pl.razer.chroma.EffectPlayer;
import at.pl.razer.chroma.SDK;

import java.io.IOException;

public class TestEffect {

    public static void main(String[] args) throws IOException, InterruptedException {
        try(SDK sdk = new SDK("Razer Chroma SDK RESTful Test Application", "This is a REST interface test application")) {
            Effect effect = new NewYear();
            try (EffectPlayer player = new EffectPlayer(sdk, effect)) {
                player.join(1000 * 60);
            }
        }
    }
}
