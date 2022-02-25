package at.pl.razer.chroma;

public enum Device {
    KEYBOARD("keyboard"), MOUSE("mouse"), MOUSEPAD("mousepad");

    private final String type;

    private Device(String type) {
        this.type = type;
    }

    public String type() {
        return type;
    }
}
