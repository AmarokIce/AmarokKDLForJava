package club.someoneice.kdl.objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class KDLNode extends KDLValue<List<KDLValue<?>>> implements Iterable<KDLValue<?>> {
    public KDLNode(final String name) {
        super(name, new ArrayList<>());
    }

    public KDLNode(final String name, final List<KDLValue<?>> value) {
        super(name, value);
    }

    public KDLNode() {
        super(new ArrayList<>());
    }

    public KDLNode(final List<KDLValue<?>> value) {
        super(value);
    }

    @Override
    public KdlTypes getType() {
        return KdlTypes.Node;
    }

    @Override
    public KDLValue<?> asValue() {
        return this;
    }

    public void add(KDLValue<?> value) {
        if (!value.hasName()) {
            this.getValue().add(value);
            return;
        }

        if (value.getType() == KdlTypes.Node) {
            this.remove(value.getName());
            this.getValue().add(value);
            return;
        }

        KDLValue<?> value2 = this.get(value.getName());
        if (Objects.isNull(value2) || value2.getType() == KdlTypes.Node) {
            this.remove(value.getName());
            this.getValue().add(value);
            return;
        }

        ((KDLNode) value2).addAll((KDLNode) value);
    }

    public void add(final String name, final KDLValue<?> value) {
        this.add(new KDLValue<>(name, value.getValue()).asValue());
    }

    public void addAll(final List<KDLValue<?>> value) {
        value.forEach(this::add);
    }

    public void addAll(final KDLNode value) {
        value.forEach(this::add);
    }


    @Nullable
    public KDLValue<?> getAt(final int index) {
        return this.getValue().get(index);
    }

    @Nullable
    public KDLValue<?> get(final String name) {
        return name.isEmpty() ? null : this.getValue().stream()
                .filter(KDLValue::hasName)
                .filter(v -> v.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public KDLValue<?> findOrAdd(final String name, final KDLValue<?> value) {
        if (name.isEmpty()) return null;
        KDLValue<?> val = this.getValue().stream()
                .filter(KDLValue::hasName)
                .filter(v -> v.getName().equals(name))
                .findFirst()
                .orElse(null);

        if (Objects.nonNull(val)) {
            return val;
        }

        this.add(name, value);
        return value;
    }

    public void remove(final String name) {
        if (name.isEmpty()) return;
        this.getValue().removeIf(v -> v.getName().equals(name));
    }

    @Override
    public @NotNull Iterator<KDLValue<?>> iterator() {
        return new Iterator<KDLValue<?>>() {
            int index = 0;

            @Override
            public boolean hasNext() {
                return index < getValue().size();
            }

            @Override
            public KDLValue<?> next() {
                return getValue().get(index++);
            }
        };
    }
}
