package at.pl.dionysus.applications.alert.providers;

import at.pl.dionysus.applications.alert.AlertLevel;
import at.pl.dionysus.applications.alert.AlertProvider;

import java.lang.management.ManagementFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CPUUsageAlert implements AlertProvider {

    private final Logger logger = Logger.getLogger(CPUUsageAlert.class.getName());
    private final double level_1, level_2;

    public CPUUsageAlert() {
        this(0.95, 0.99);
    }

    public CPUUsageAlert(double level_1, double level_2) {
        this.level_1 = level_1;
        this.level_2 = level_2;
    }

    @Override
    public String getName() {
        return "CPU Usage";
    }

    @Override
    public AlertLevel getLevel() {
        double usage = ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
        if (usage < 0) {
            try {
                usage = ((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getProcessCpuLoad();
            } catch (Exception e) {
                // mimimi
            }
        }
        logger.log(Level.INFO, "system load {0}", Double.valueOf(usage));
        return usage >= level_2 ? AlertLevel.SEVERE : usage >= level_1 ? AlertLevel.WARNING : AlertLevel.NONE;
    }

    @Override
    public void close() {}
}
