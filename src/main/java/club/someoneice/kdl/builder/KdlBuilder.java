package club.someoneice.kdl.builder;

import club.someoneice.kdl.objects.*;
import club.someoneice.kdl.objects.KNode.KdlTypes;

public final class KdlBuilder {
  private KdlBuilder() {}

  public static String toString(final KNode<?> node) {
    final KdlTypes type = node.getType();
    if (type == KdlTypes.String) {
      return "#\"" + node.getValue().toString() + "\"#";
    }

    if (type == KdlTypes.Boolean) {
      return "#" + ((KBoolean) node.asTypeNode()).getValue();
    }

    if (type == KdlTypes.Number)  {
      final double db = ((KNumber) node.asTypeNode()).getDouble();
      if (db == Double.MAX_VALUE) {
        return "#inf";
      } else if (db == Double.MIN_VALUE) {
        return "#-inf";
      } else if (Double.isNaN(db)) {
        return "#NaN";
      }
      return Double.toString(db);
    }

    if (type == KdlTypes.Null) {
      return "#null";
    }

    if (type == KdlTypes.Pair) {
      final KPair<?, ?> pair = node.asTypeNode().asPair();
      final String key = toString(pair.getKey());
      final String value = toString(pair.getValue());
      return String.format("%s=%s", key, value);
    }

    if (type == KdlTypes.Array) {
      final StringBuilder builder = new StringBuilder();
      final KArray array = node.asArrayOrEmpty();
      for (KNode<?> kNode : array) {
        builder.append(toString(kNode));
        builder.append(" ");
      }
      return builder.toString();
    }

    if (type == KdlTypes.Doc) {
      final StringBuilder builder = new StringBuilder();
      final KDoc doc = node.asDocOrEmpty();
      for (KNode<?> kNode : doc) {
        builder.append(toString(kNode));
        builder.append("\n");
      }
      return builder.toString();
    }

    return "";
  }
}
