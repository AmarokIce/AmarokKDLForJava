package club.someoneice.kdl;

import club.someoneice.kdl.exception.KDLStyleException;
import club.someoneice.kdl.objects.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;

/**
 * KDL is the core parse for KDL files.
 *
 * @author AmarokIce
 */
public final class KDL {
  public static KArray parse(@Nonnull final InputStream stream, boolean shouldClose)
      throws KDLStyleException {
    try {
      byte[] bytes = new byte[stream.available()];
      stream.read(bytes);
      stream.close();

      final String dat = new String(bytes);
      if (shouldClose) {
        stream.close();
      }
      return parse(Arrays.asList(dat.split("\n")));
    } catch (IOException e) {
      e.printStackTrace();
      return new KArray();
    }
  }

  @Nonnull
  public static KArray parse(@Nonnull final File file) throws KDLStyleException {
    try {
      return parse(Files.readAllLines(file.toPath()));
    } catch (IOException e) {
      e.printStackTrace();
      return new KArray();
    }
  }

  @Nonnull
  public static KArray parse(@Nonnull final List<String> lines) throws KDLStyleException {
    preHandler(lines);
    return handler(lines);
  }

  /**
   * The pre handler will do four things:
   * - Clean the line break;
   * - Remove line comment;
   * - Remove multi-line comment;
   * - Remove the "slashdash" comment in head and "slashdash" comment nested child nodes
   * <p>
   * Other node of "slashdash" comment will handle on common handler.
   * Because the multi-line's data like multi-line text should handle in deep.
   *
   * @param lines The raw data of lines.
   */
  static void preHandler(List<String> lines) throws KDLStyleException {
    KDLHelper.removeSingleLineComment(lines);
    KDLHelper.removeMultiLineComment(lines);
    KDLHelper.mergeMultiLines(lines);
    KDLHelper.removeSlashdashComment(lines);
  }

  static KArray handler(final List<String> raw) throws KDLStyleException {
    final List<KArray> result = new ArrayList<>();

    final Queue<String> queue = new ArrayDeque<>(raw);

    final StringBuilder sb = new StringBuilder();
    final StringBuilder endOf = new StringBuilder();
    final StringBuilder anno = new StringBuilder();

    final KArray array = new KArray();
    KNode<?> key = null;

    while (!queue.isEmpty()) {
      final String line = queue.poll();
      final String[] nodes = line.split(" ");
      for (String node : nodes) {
        final StringBuilder nb = new StringBuilder(node);
        final KNode<?> nd = handleNode(nb, sb, endOf, anno, queue);
        if (Objects.isNull(nd)) {
          continue;
        }

        sb.setLength(0);
        if (node.startsWith("=")) {
          if (Objects.nonNull(key)) {
            throw new KDLStyleException("Duplicate key at " + sb);
          }

          key = nd.setTypeComment(anno.toString());
          anno.setLength(0);
          continue;
        }

        if (Objects.isNull(key)) {
          array.add(nd.setTypeComment(anno.toString()));
          anno.setLength(0);
          continue;
        }

        array.add(new KPair<>(key, nd.setTypeComment(anno.toString())));
        anno.setLength(0);
        key = null;
      }

      if (sb.length() > 0) {
        continue;
      }

      result.add(new KArray(array));
      array.clear();
    }

    array.clear();
    array.addAll(result);

    return array;
  }

  // Fixme 字符串解析的空格会被缺省。
  @Nullable
  private static KNode<?> handleNode(StringBuilder node,
                                     StringBuilder sb,
                                     StringBuilder endOf,
                                     StringBuilder anno,
                                     Queue<String> queue) throws KDLStyleException {
    if (endOf.length() != 0) {
      final int indexEnd = node.indexOf(endOf.toString());
      if (indexEnd == -1) {
        sb.append(" ").append(node);
        return null;
      }

      if (endOf.length() > 1) {
        KDLHelper.trim(sb);
      }

      sb.append(node, 0, indexEnd);
      node.delete(0, indexEnd + endOf.length());

      endOf.setLength(0);
      return new KString(sb.toString());
    }

    String raw = node.toString();
    if (raw.startsWith("/-")) {
      return null;
    }

    if (raw.equals("{")) {
      final List<String> arr = new ArrayList<>();
      int count = 0;
      while (!queue.isEmpty()) {
        final String line = queue.poll();
        if (line.trim().endsWith("{")) {
          count++;
          continue;
        }
        if (line.trim().endsWith("}")) {
          count--;
        }
        if (count == 0) {
          return handler(arr);
        }
      }
    }

    if (raw.startsWith("\"")) {
      endOf.append("\"");
      node.delete(0, 1);
      return handleNode(node, sb, endOf, anno, queue);
    }

    if (raw.startsWith("\"\"\"")) {
      endOf.append("\"\"\"");
      node.delete(0, 3);
      return handleNode(node, sb, endOf, anno, queue);
    }

    if (raw.startsWith("(")) {
      anno.append(raw, 1, raw.indexOf(")"));
      sb.delete(0, raw.indexOf(")") + 1);
      raw = sb.toString();
    }

    KNode<?> result;
    final int indexOfPair = raw.indexOf("=");
    final String[] dat = raw.split("=");
    if (Objects.nonNull(result = checkNull(dat[0]))) {
      node.delete(0, indexOfPair == -1 ? 0 : indexOfPair);
      return result;
    }

    if (Objects.nonNull(result = checkBoolean(dat[0]))) {
      node.delete(0, indexOfPair == -1 ? 0 : indexOfPair);
      return result;
    }

    if (Objects.nonNull(result = checkNumber(dat[0]))) {
      node.delete(0, indexOfPair == -1 ? 0 : indexOfPair);
      return result;
    }

    if (!raw.startsWith("#")) {
      node.delete(0, indexOfPair == -1 ? 0 : indexOfPair);
      return new KString(dat[0]);
    }

    final StringBuilder endOfSb = new StringBuilder();
    final int countSignRaw = KDLHelper.countStartStr(raw, "#");
    final String signRaw = KDLHelper.forEachFill("#", countSignRaw);
    endOfSb.append(signRaw);

    final int countSignStr = KDLHelper.countStartStr(
        raw.replaceFirst(endOfSb.toString(), ""), "\"");
    final String signStr = KDLHelper.forEachFill("\"", countSignStr);
    endOfSb.insert(0, signStr);
    endOf.append(endOfSb);

    node.delete(0, endOf.length());
    return handleNode(node, sb, endOf, anno, queue);
  }

  @Nullable
  private static KNumber checkNumber(String raw) {
    raw = raw.replace("_", "").toLowerCase();

    switch (raw) {
      case "#inf":
        return new KNumber(Double.MAX_VALUE);
      case "#-inf":
        return new KNumber(Double.MIN_VALUE);
      case "#nan":
        return new KNumber(Double.NaN);
    }

    try {
      if (raw.startsWith("0x")) {
        return new KNumber(Integer.parseInt(raw, 16));
      }

      if (raw.startsWith("0o")) {
        return new KNumber(Integer.parseInt(raw, 10));
      }

      if (raw.startsWith("0b")) {
        return new KNumber(Integer.parseInt(raw, 2));
      }

      int e = 0;

      if (raw.contains("e")) {
        final String[] dat = raw.split("e");
        e = Integer.parseInt(dat[1]);
        raw = dat[0];
      }

      double db = Double.parseDouble(raw.startsWith(".") ? raw.substring(1) : raw);
      db /= 10;
      if (e != 0) {
        db /= (db * 10);
      }

      return new KNumber(db);
    } catch (Exception ex) {
      // Jump to string.
    }

    return null;
  }

  @Nullable
  private static KBoolean checkBoolean(String raw) {
    raw = raw.toLowerCase();

    if (raw.equals("#true")) {
      return new KBoolean(true);
    }

    if (raw.equals("#false")) {
      return new KBoolean(false);
    }

    return null;
  }

  @Nullable
  private static KNull checkNull(String raw) {
    raw = raw.toLowerCase();
    if (raw.equals("#null")) {
      return KNull.INSTANCE;
    }

    return null;
  }
}
