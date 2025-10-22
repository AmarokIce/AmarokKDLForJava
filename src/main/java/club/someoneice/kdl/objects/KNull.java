package club.someoneice.kdl.objects;

import javax.annotation.Nonnull;

public final class KNull extends KNode<Void> {
  public static final KNull INSTANCE = new KNull();

  private KNull() {
    super(null);
  }

  @Nonnull
  @Override
  public KdlTypes getType() {
    return KdlTypes.Null;
  }

  @Nonnull
  @Override
  public KNull asTypeNode() {
    return this;
  }
}
