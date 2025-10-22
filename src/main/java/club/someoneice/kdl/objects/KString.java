package club.someoneice.kdl.objects;

import javax.annotation.Nonnull;

public class KString extends KNode<String> {
  public KString(final String value) {
    super(value);
  }

  @Nonnull
  @Override
  public KdlTypes getType() {
    return KdlTypes.String;
  }

  @Nonnull
  @Override
  public KNode<?> asTypeNode() {
    return this;
  }
}
