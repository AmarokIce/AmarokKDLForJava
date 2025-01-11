package club.someoneice.kdl.objects;

public class KDLBoolean extends KDLValue<Boolean> {
    public KDLBoolean(final boolean value) {
        super(value);
    }

    public KDLBoolean(final String name, final boolean value) {
        super(name, value);
    }

    @Override
    public KdlTypes getType() {
        return KdlTypes.Boolean;
    }

    @Override
    public KDLValue<?> asValue() {
        return this;
    }
}
