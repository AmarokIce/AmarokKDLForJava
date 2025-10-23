package club.someoneice.kdl.objects;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.*;

public class KDoc extends KNode<List<KArray>> implements Iterable<KArray>, List<KArray> {
  public KDoc(List<KArray> value) {
    super(value);
  }

  public KDoc() {
    super(new ArrayList<>());
  }

  public KDoc cleanEmptyLine() {
    this.getValue().removeIf(KArray::isEmpty);
    return this;
  }

  @CheckForNull
  public KNode<?> get(int line, int index) {
    return this.getValue().get(line).get(index);
  }

  public KNode<?> set(int line, int index, KNode<?> value) {
    return this.getValue().get(line).set(index, value);
  }

  public KNode<?> addNode(int line, KNode<?> value) {
    this.getValue().get(line).add(value);
    return value;
  }

  public KNode<?> removeNode(int line, int index) {
    return this.getValue().get(line).remove(index);
  }

  public Map<String, List<KNode<?>>> asMap() {
    this.cleanEmptyLine();
    final Map<String, List<KNode<?>>> map = new HashMap<>();
    for (KArray kNodes : this.getValue()) {
      final List<KNode<?>> list = new ArrayList<>();
      final String key = kNodes.get(0).getValue().toString();
      for (int i = 1; i < kNodes.size(); i++) {
        list.add(kNodes.get(i));
      }
      if (map.containsKey(key)) {
        map.get(key).addAll(list);
      }
      map.put(key, list);
    }
    return map;
  }


  @Nonnull
  @Override
  public List<KArray> getValue() {
    if (Objects.isNull(this.value)) {
      this.setValue(new ArrayList<>());
    }
    return this.value;
  }

  @Nonnull
  @Override
  public KdlTypes getType() {
    return KdlTypes.Doc;
  }

  @Nonnull
  @Override
  public KDoc asTypeNode() {
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
    if (!(o instanceof KArray)) {
      return false;
    }

    KNode<?> node = (KNode<?>) o;
    return this.getValue().stream()
        .anyMatch(it ->
            it.getType() == node.getType()
                && it.getValue().equals(node.getValue()));
  }

  @Override
  public @Nonnull Iterator<KArray> iterator() {
    return new Iterator<KArray>() {
      int index = 0;

      @Override
      public boolean hasNext() {
        return index < getValue().size();
      }

      @Override
      public KArray next() {
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
  public boolean add(KArray kNode) {
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
  public boolean addAll(@Nonnull Collection<? extends KArray> c) {
    return this.getValue().addAll(c);
  }

  @Override
  public boolean addAll(int index, @Nonnull Collection<? extends KArray> c) {
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
  public KArray get(int index) {
    return this.getValue().get(index);
  }

  @Override
  public KArray set(int index, KArray element) {
    return this.getValue().set(index, element);
  }

  @Override
  public void add(int index, KArray element) {
    this.getValue().add(index, element);
  }

  @Override
  public KArray remove(int index) {
    return this.getValue().remove(index);
  }

  @Override
  public int indexOf(Object o) {
    if (!(o instanceof KArray)) {
      return -1;
    }

    KArray node = (KArray) o;
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
    if (!(o instanceof KArray)) {
      return -1;
    }

    KNode<?> node = (KArray) o;
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
  public @Nonnull ListIterator<KArray> listIterator() {
    return this.getValue().listIterator();
  }

  @Override
  public @Nonnull ListIterator<KArray> listIterator(int index) {
    return this.getValue().listIterator(index);
  }

  @Override
  public @Nonnull List<KArray> subList(int fromIndex, int toIndex) {
    return this.getValue().subList(fromIndex, toIndex);
  }
}
