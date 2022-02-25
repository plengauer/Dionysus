package at.pl.dionysus.applications.alert;

public interface AlertProvider extends java.io.Closeable {

    public abstract String getName();

    public abstract AlertLevel getLevel();

}
