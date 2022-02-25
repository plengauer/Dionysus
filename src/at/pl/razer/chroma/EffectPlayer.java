package at.pl.razer.chroma;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EffectPlayer implements java.io.Closeable {

    private final Logger logger = Logger.getLogger(EffectPlayer.class.getName());
    private final SDK sdk;
    private final Effect effect;
    private final Thread thread;

    public EffectPlayer(SDK sdk, Effect effect) {
        this.sdk = sdk;
        this.effect = effect;
        this.thread = new Thread(this::run);

        thread.start();
    }

    public boolean isRunning() {
        return thread.isAlive();
    }

    public void close() {
        logger.info("stopping");
        this.thread.interrupt();
        try {
            this.thread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void join(long time) throws InterruptedException {
        thread.join(time);
    }

    private void run() {
        logger.info("running");
        int count = 0;
        int total = 0;
        List<String> effectIDs = new ArrayList<>();
        while (!thread.isInterrupted() && effect.hasNext()) {
            EffectFrame frame = effect.next();
            try {
                long time = System.currentTimeMillis();
                List<String> currentEffectIDs = new ArrayList<>();
                for (EffectFrameDefinition definition : frame.getEffects()) {
                    currentEffectIDs.add(sdk.createEffect(definition.getDevice(), definition.getDefinition()));
                    count++;
                    total++;
                }
                logger.log(Level.FINE, "next frame with {0} effects", currentEffectIDs.size());
                for (String effectID : currentEffectIDs) {
                    sdk.activateEffect(effectID);
                }
                for (String effectID : effectIDs) {
                    sdk.deleteEffect(effectID);
                    count--;
                }
                effectIDs = currentEffectIDs;
                time = System.currentTimeMillis() - time;
                long wait = Math.max(1, frame.getDuration() - time);
                Thread.sleep(wait);
            } catch (IOException e) {
                logger.log(Level.WARNING, "", e);
                thread.interrupt();
            } catch (InterruptedException e) {
                thread.interrupt();
            }
        }
        Thread.interrupted(); // reset so i can delete
        for (String effectID : effectIDs) {
            try {
                sdk.deleteEffect(effectID);
                count--;
            } catch (Exception e) {
                // mimimi
            }
        }
        logger.log(Level.INFO, "stopped (total {0}, checksum {1})", new Object[] { total, count });
    }

}
