package at.pl.dionysus.applications.weather;

public class Snow extends Falling {

    public Snow(double coverage) {
        super(coverage, 1000, false, Colors.SNOW);
    }
}
