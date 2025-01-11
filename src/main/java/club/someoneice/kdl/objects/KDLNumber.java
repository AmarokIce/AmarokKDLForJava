package club.someoneice.kdl.objects;

public class KDLNumber extends KDLValue<Number> {
    public KDLNumber(final Number value) {
        super(value);
    }

    public KDLNumber(final String name, final Number value) {
        super(name, value);
    }

    @Override
    public KdlTypes getType() {
        return KdlTypes.Number;
    }

    @Override
    public KDLValue<?> asValue() {
        return this;
    }

    public int getInt() {
        return this.getValue().intValue();
    }

    public float getFloat() {
        return this.getValue().floatValue();
    }

    public double getDouble() {
        return this.getValue().doubleValue();
    }
}
