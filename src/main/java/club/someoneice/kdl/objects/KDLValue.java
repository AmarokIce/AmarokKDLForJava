package club.someoneice.kdl.objects;

import java.util.List;
import java.util.Objects;

public class KDLValue<T> {
    protected final String name;
    protected T value;

    public KDLValue(final String name, final T value) {
        this.name = name;
        this.value = value;
    }

    public KDLValue(final T value) {
        this("", value);
    }

    public KdlTypes getType() {
        if (Objects.isNull(this.value)) {
            return KdlTypes.Null;
        }

        if (this.value instanceof String) {
            return KdlTypes.String;
        }

        if (this.value instanceof Number) {
            return KdlTypes.Number;
        }

        if (this.value instanceof Boolean) {
            return KdlTypes.Boolean;
        }

        if (this.value instanceof List) {
            return KdlTypes.Node;
        }

        return KdlTypes.Null;
    }

    @SuppressWarnings("unchecked")
    public KDLValue<?> asValue() {
        switch (this.getType()) {
            case String: return new KDLString(this.name, (String) this.value);
            case Number: return new KDLNumber(this.name, (Number) this.value);
            case Boolean: return new KDLBoolean(this.name, (Boolean) this.value);
            case Node: return new KDLNode(this.name, (List<KDLValue<?>>) this.value);
            default: return new KDLNull();
        }
    }

    public String getName() {
        return this.name;
    }

    public boolean hasName() {
        return !this.name.isEmpty();
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        KDLValue<?> kdlValue = (KDLValue<?>) o;
        return Objects.equals(getValue(), kdlValue.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getValue());
    }

    public enum KdlTypes {
        String,
        Number,
        Boolean,
        Node,
        Null
    }
}
