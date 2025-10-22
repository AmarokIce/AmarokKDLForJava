package club.someoneice.kdl.objects;

import javax.annotation.Nonnull;
import java.util.*;

@SuppressWarnings("all")
public class KArray extends KNode<List<KNode<?>>> implements Iterable<KNode<?>>, List<KNode<?>> {
  public KArray(KNode<?>... children) {
    super(new ArrayList<>(Arrays.asList(children)));
  }

  public KArray(List<KNode<?>> value) {
    super(value);
  }

  public KArray(KArray array) {
    super(new ArrayList<>(array));
  }

  @Nonnull
  @Override
  public KdlTypes getType() {
    return KdlTypes.Array;
  }

  @Nonnull
  @Override
  public KArray asTypeNode() {
    return this;
  }

  @Override
  public int size() {
    return this.getValue().size();
  }

  @Override
  public boolean isEmpty() {
    return this.getValue().isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    if (!(o instanceof KNode<?>)) {
      return false;
    }

    KNode<?> node = (KNode<?>) o;
    return this.getValue().stream()
        .anyMatch(it ->
            it.getType() == node.getType()
                && it.getValue().equals(node.getValue()));
  }

  @Override
  public @Nonnull Iterator<KNode<?>> iterator() {
    return new Iterator<KNode<?>>() {
      int index = 0;

      @Override
      public boolean hasNext() {
        return index < getValue().size();
      }

      @Override
      public KNode<?> next() {
        return getValue().get(index++);
      }
    };
  }

  @Override
  public @Nonnull Object[] toArray() {
    return this.getValue().toArray();
  }

  @Override
  public @Nonnull <T> T[] toArray(@Nonnull T[] a) {
    return this.getValue().toArray(a);
  }

  @Override
  public boolean add(KNode<?> kNode) {
    return this.getValue().add(kNode);
  }

  @Override
  public boolean remove(Object o) {
    if (!(o instanceof KNode<?>)) {
      return false;
    }

    KNode<?> node = (KNode<?>) o;

    final KNode<?> otNode = this.getValue().stream()
        .filter(it ->
            it.getType() == node.getType()
                && it.getValue().equals(node.getValue()))
        .findFirst()
        .orElse(null);

    if (Objects.isNull(otNode)) {
      return false;
    }

    return this.getValue().remove(node);
  }

  @Override
  public boolean containsAll(@Nonnull Collection<?> c) {
    return new HashSet<>(this.getValue()).containsAll(c);
  }

  @Override
  public boolean addAll(@Nonnull Collection<? extends KNode<?>> c) {
    return this.getValue().addAll(c);
  }

  @Override
  public boolean addAll(int index, @Nonnull Collection<? extends KNode<?>> c) {
    return this.getValue().addAll(index, c);
  }

  @Override
  public boolean removeAll(@Nonnull Collection<?> c) {
    return this.getValue().removeAll(c);
  }

  @Override
  public boolean retainAll(@Nonnull Collection<?> c) {
    return this.getValue().retainAll(c);
  }

  @Override
  public void clear() {
    this.getValue().clear();
  }

  @Override
  public KNode<?> get(int index) {
    return this.getValue().get(index);
  }

  @Override
  public KNode<?> set(int index, KNode<?> element) {
    return this.getValue().set(index, element);
  }

  @Override
  public void add(int index, KNode<?> element) {
    this.getValue().add(index, element);
  }

  @Override
  public KNode<?> remove(int index) {
    return this.getValue().remove(index);
  }

  @Override
  public int indexOf(Object o) {
    if (!(o instanceof KNode<?>)) {
      return -1;
    }

    KNode<?> node = (KNode<?>) o;
    for (int i = 0; i < this.getValue().size(); i++) {
      final KNode<?> thisNode = this.getValue().get(i);
      if (thisNode.getType() == node.getType()
          && thisNode.getValue().equals(node.getValue())) {
        return i;
      }
    }
    return -1;
  }

  @Override
  public int lastIndexOf(Object o) {
    if (!(o instanceof KNode<?>)) {
      return -1;
    }

    KNode<?> node = (KNode<?>) o;
    for (int i = this.getValue().size() - 1; i >= 0; i--) {
      final KNode<?> thisNode = this.getValue().get(i);
      if (thisNode.getType() == node.getType()
          && thisNode.getValue().equals(node.getValue())) {
        return i;
      }
    }
    return -1;
  }

  @Override
  public @Nonnull ListIterator<KNode<?>> listIterator() {
    return this.getValue().listIterator();
  }

  @Override
  public @Nonnull ListIterator<KNode<?>> listIterator(int index) {
    return this.getValue().listIterator(index);
  }

  @Override
  public @Nonnull List<KNode<?>> subList(int fromIndex, int toIndex) {
    return this.getValue().subList(fromIndex, toIndex);
  }
}
