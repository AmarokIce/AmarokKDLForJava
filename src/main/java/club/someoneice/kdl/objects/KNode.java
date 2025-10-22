package club.someoneice.kdl.objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public abstract class KNode<T> {
  protected T value;
  protected String typeComment = null;

  public KNode(final T value) {
    this.value = value;
  }

  @Nullable
  public T getValue() {
    return value;
  }

  public void setValue(@Nullable T value) {
    this.value = value;
  }

  /**
   * KDL3 can set a cast type for values, like (int)114514 . <p />
   * The parser does not handle such projections during inference,
   * please perform external projection processing based on annotations if you needed. <p />
   * Return null if the node had no cast type.
   */
  @Nullable
  public String getTypeComment() {
    return typeComment;
  }

  /**
   * This method generally should not be used outside of parsing,
   * as it may confuse the target of the projection tool and cause unexpected results.
   */
  public KNode<?> setTypeComment(@Nullable String typeComment) {
    this.typeComment = typeComment;
    return this;
  }

  @Nonnull
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
      return KdlTypes.Array;
    }

    if (this instanceof KPair<?, ?>) {
      return KdlTypes.Pair;
    }

    return KdlTypes.Null;
  }

  @Nonnull
  @SuppressWarnings("unchecked")
  public KNode<?> asTypeNode() {
    switch (this.getType()) {
      case String:
        return new KString((String) this.value);
      case Number:
        return new KNumber((Number) this.value);
      case Boolean:
        return new KBoolean((Boolean) this.value);
      case Array:
        return new KArray((List<KNode<?>>) this.value);
      case Pair:
        return this;
      default:
        return KNull.INSTANCE;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    KNode<?> kdlValue = (KNode<?>) o;
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
    Array,
    Pair,
    Null
  }
}
