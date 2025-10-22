package club.someoneice.kdl.objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class KPair<K extends KNode<?>, V extends KNode<?>> extends KNode<V> {
  protected K key;

  public KPair(K key, V value) {
    super(value);
    this.key = key;
  }

  public KPair() {
    super(null);
  }

  @Nullable
  public K getKey() {
    return key;
  }

  public void setKey(@Nullable K key) {
    this.key = key;
  }

  @Nonnull
  @Override
  public KdlTypes getType() {
    return KdlTypes.Pair;
  }

  @Nonnull
  @Override
  public KPair<K, V> asTypeNode() {
    return this;
  }
}
