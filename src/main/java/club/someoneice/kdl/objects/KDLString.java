package club.someoneice.kdl.objects;

public class KDLString extends KDLValue<String> {
    public KDLString(final String value) {
        super(value);
    }

    public KDLString(final String name, final String value) {
        super(name, value);
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
