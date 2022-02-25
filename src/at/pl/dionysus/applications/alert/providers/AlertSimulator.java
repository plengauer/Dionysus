package at.pl.dionysus.applications.alert.providers;

import at.pl.dionysus.applications.alert.AlertLevel;
import at.pl.dionysus.applications.alert.AlertProvider;

public class AlertSimulator implements AlertProvider {

    private final AlertLevel[] levels;
    private int index;

    public AlertSimulator(AlertLevel... levels) {
        this.levels = levels;
        index = 0;
    }

    @Override
    public String getName() {
        return "Simulator";
    }

    @Override
    public AlertLevel getLevel() {
        return index < levels.length ? levels[index++] : AlertLevel.NONE;
    }

    @Override
    public void close() {}

}
