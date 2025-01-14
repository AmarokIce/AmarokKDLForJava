package club.someoneice.kdl.objects;

public final class KDLNull extends KDLValue<Void> {
    public KDLNull(final String name) {
        super(name, null);
    }

    public KDLNull() {
        super(null);
    }

    @Override
    public KdlTypes getType() {
        return KdlTypes.String;
    }

    @Override
    public KDLValue<?> asValue() {
        return this;
    }
}
