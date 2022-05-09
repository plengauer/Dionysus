package at.pl.dionysus;

import at.pl.dionysus.applications.date.*;
import at.pl.razer.chroma.Effect;
import at.pl.razer.chroma.EffectPlayer;
import at.pl.razer.chroma.SDK;

import java.io.IOException;

public class TestEffect {

    public static void main(String[] args) throws IOException, InterruptedException {
        try(SDK sdk = new SDK("Razer Chroma SDK RESTful Test Application", "This is a REST interface test application")) {
            play(sdk, new NewYear());
            play(sdk, new Sweden());
            play(sdk, new Finland());
            play(sdk, new EuropeanUnion());
            play(sdk, new USA());
            play(sdk, new Japan());
            play(sdk, new Austria());
            play(sdk, new France());
            play(sdk, new Germany());
            play(sdk, new Ukraine());
            play(sdk, new GayPride());
            play(sdk, new ValentinesDay());
            play(sdk, new Easter());
            play(sdk, new Halloween());
            play(sdk, new XMas());
        }
    }

    private static void play(SDK sdk, Effect effect) throws InterruptedException {
        try (EffectPlayer player = new EffectPlayer(sdk, effect)) {
            player.join(1000 * 60);
        }
    }
}
