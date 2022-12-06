package it.uniba.dib.sms22235.entities.operations;

public class Interval <T extends Number> {
    private final T min;
    private final T max;

    public Interval(T min, T max) {
        this.min = min;
        this.max = max;
    }

    public T getMax() {
        return max;
    }

    public T getMin() {
        return min;
    }
}
