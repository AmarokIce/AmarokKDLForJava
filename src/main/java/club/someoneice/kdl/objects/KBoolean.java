package club.someoneice.kdl.objects;

import javax.annotation.Nonnull;

public class KBoolean extends KNode<Boolean> {
  public KBoolean(final boolean value) {
    super(value);
  }

  @Nonnull
  @Override
  public KdlTypes getType() {
    return KdlTypes.Boolean;
  }

  @Nonnull
  @Override
  public KBoolean asTypeNode() {
    return this;
  }
}
