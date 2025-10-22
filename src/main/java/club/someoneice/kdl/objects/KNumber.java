package club.someoneice.kdl.objects;

import javax.annotation.Nonnull;

public class KNumber extends KNode<Number> {
  public KNumber(final Number value) {
    super(value);
  }

  @Nonnull
  @Override
  public KdlTypes getType() {
    return KdlTypes.Number;
  }

  @Nonnull
  @Override
  public KNumber asTypeNode() {
    return this;
  }

  @Nonnull
  @Override
  public Number getValue() {
    return this.value;
  }

  public int getInt() {
    return this.getValue().intValue();
  }

  public long getLong() {
    return this.getValue().longValue();
  }

  public float getFloat() {
    return this.getValue().floatValue();
  }

  public double getDouble() {
    return this.getValue().doubleValue();
  }
}
