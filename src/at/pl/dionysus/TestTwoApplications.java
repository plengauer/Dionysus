package at.pl.dionysus;

import at.pl.dionysus.applications.date.Austria;
import at.pl.razer.chroma.SingletonEffectPlayer;
import at.pl.razer.chroma.effects.Color;
import at.pl.razer.chroma.effects.StarlightEffect;
import at.pl.razer.chroma.effects.StaticEffect;

import java.io.IOException;

public class TestTwoApplications {

    public static void main(String[] args) throws IOException, InterruptedException {
        int color = Color.rgb((int) (0xFF * Math.random()), (int) (0xFF * Math.random()), (int) (0xFF * Math.random()));
        try (SingletonEffectPlayer player = new SingletonEffectPlayer("Razer Chroma SDK RESTful Test Application", "This is a REST interface test application", new StaticEffect(color))) {
            player.join(1000 * 10 / 2);
            try (SingletonEffectPlayer player2 = new SingletonEffectPlayer("Razer Chroma SDK RESTful Test Application 2", "This is a REST interface test application", new StarlightEffect(0.5, 1000 * 2, color))) {
                player2.join(1000 * 10 / 2);
                try (SingletonEffectPlayer player3 = new SingletonEffectPlayer("Razer Chroma SDK RESTful Test Application 3", "This is a REST interface test application", new Austria())) {
                    player3.join(1000 * 10);
                }
                player2.join(1000 * 10 / 2);
            }
            player.join(1000 * 10 / 2);
        }
    }

}
