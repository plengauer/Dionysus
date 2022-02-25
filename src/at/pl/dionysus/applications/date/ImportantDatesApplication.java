package at.pl.dionysus.applications.date;

import at.pl.dionysus.applications.Application;
import at.pl.razer.chroma.Effect;
import at.pl.razer.chroma.EffectPlayer;
import at.pl.razer.chroma.SDK;
import at.pl.razer.chroma.SingletonEffectPlayer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImportantDatesApplication implements Application {

    private final Logger logger = Logger.getLogger(ImportantDatesApplication.class.getName());
    private final Thread thread = new Thread(this::run, "Date Application Worker");

    {
        thread.start();
    }

    @Override
    public String getName() {
        return "Important Dates";
    }

    @Override
    public String getDescription() {
        return "An application that is creating chroma effects based on important dates";
    }

    private void run() {
        logger.info("running");
        while (!thread.isInterrupted()) {
            try {
                LocalDate date = LocalDate.now();
                int month = date.getMonth().getValue();
                int day = date.getDayOfMonth();
                Effect effect = getEffect(month, day);
                long rest = (60 * 60 * 24 - LocalTime.now().toSecondOfDay()) * 1000;
                if (effect != null) {
                    logger.log(Level.INFO, "important date {0}.{1}", new Object[] { day, month });
                    try (SingletonEffectPlayer player = new SingletonEffectPlayer(getName(), getDescription(), effect)) {
                        logger.log(Level.FINE, "sleeping {0} secs", new Object[] { rest });
                        player.join(rest);
                    }
                } else {
                    logger.log(Level.FINE, "sleeping {0} secs", new Object[] { rest });
                    Thread.sleep(rest);
                }
            } catch (InterruptedException e) {
                thread.interrupt();
            }
        }
        logger.info("stopped");
    }

    private Effect getEffect(int month, int day) {
        if (month == 10 && day == 26) return new Austria();
        else if (month == 7 && day == 14) return new France();
        else if (month == 10 && day == 3) return new Germany();
        else if (month == 12 && day == 24) return new XMas();
        else if (month == 2 && day == 14) return new ValentinesDay();
        else if (month == 10 && day == 31) return new Halloween();
        return null;
    }

    @Override
    public void close() throws IOException {
        logger.info("stopping");
        thread.interrupt();
    }
}
