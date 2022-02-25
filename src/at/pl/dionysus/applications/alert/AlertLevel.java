package at.pl.dionysus.applications.alert;

public enum AlertLevel {

    NONE(0), WARNING(1), SEVERE(2);

    private final int level;

    private AlertLevel(int level) {
        this.level = level;
    }

    public int level() {
        return level;
    }

    public static AlertLevel from(int level) {
        for (AlertLevel value : values()) {
            if (value.level() == level) {
                return value;
            }
        }
        throw new IllegalArgumentException();
    }
}
