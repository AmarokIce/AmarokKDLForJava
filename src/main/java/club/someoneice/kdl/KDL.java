package club.someoneice.kdl;

import club.someoneice.kdl.exception.KDLStyleException;
import club.someoneice.kdl.objects.KArray;
import club.someoneice.kdl.objects.KDoc;
import club.someoneice.kdl.objects.KNode;
import club.someoneice.kdl.objects.KPair;

import javax.annotation.Nonnull;
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
  public static KDoc parse(@Nonnull final InputStream stream, boolean shouldClose)
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
      return new KDoc();
    }
  }

  @Nonnull
  public static KDoc parse(@Nonnull final File file) throws KDLStyleException {
    try {
      return parse(Files.readAllLines(file.toPath()));
    } catch (IOException e) {
      e.printStackTrace();
      return new KDoc();
    }
  }

  @Nonnull
  public static KDoc parse(@Nonnull final List<String> lines) throws KDLStyleException {
    preHandler(lines);
    return handler(lines).cleanEmptyLine();
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

  private static KDoc handler(List<String> lines) {
    final KDoc result = new KDoc();

    final Queue<String> queue = new ArrayDeque<>(lines);

    final StringBuilder sb = new StringBuilder();
    final StringBuilder node = new StringBuilder();
    final StringBuilder endOf = new StringBuilder();

    final KArray array = new KArray();

    int deepen = 0;
    final List<String> childrenLines = new ArrayList<>();
    boolean ig = false;

    while(!queue.isEmpty()) {
      node.append(queue.poll());

      while(node.length() > 0) {
        final String lineRaw = node.toString().trim();

        if (deepen == 0) {
          if (endOf.length() != 0 || (!lineRaw.startsWith("{") && !lineRaw.endsWith("/-{"))) {
            handleNode(node, endOf, sb, array);
            continue;
          }

          if (lineRaw.startsWith("/-")) {
            ig = true;
            node.delete(0, 2);
          }

          node.delete(0, node.indexOf("{") + 1);
          childrenLines.add(node.toString());
          deepen++;
          node.setLength(0);
          continue;
        }


        if (!lineRaw.startsWith("}")) {
          childrenLines.add(node.toString());
          node.setLength(0);
          continue;
        }

        if (--deepen != 0) {
          continue;
        }

        if (!ig) {
          array.add(handler(childrenLines).cleanEmptyLine());
        }
        childrenLines.clear();
        result.add(new KArray(array));
        array.clear();
      }

      if (endOf.length() == 0 && childrenLines.isEmpty()) {
        result.add(new KArray(array));
        array.clear();
      }
    }

    return result;
  }

  private static void handleNode(StringBuilder line, StringBuilder endOf,
                                 StringBuilder sb, KArray array) {
    if (endOf.length() > 0) {
      final int indexEnd = line.indexOf(endOf.toString());

      if (indexEnd == -1) {
        sb.append(line).append("\n");
        line.setLength(0);
        // Multi line, Next line.
        return;
      }

      sb.append(line, 0, indexEnd + endOf.length());
      line.delete(0, indexEnd + endOf.length());
      endOf.setLength(0);
      addStringToArray(array, sb);
      return;
    }

    line.delete(0, KDLHelper.countStartStr(line.toString(), " "));
    String raw = line.toString();
    if (raw.startsWith("/-")) {
      return;
    }

    if (raw.startsWith("=")) {
      sb.append("=");
      line.delete(0, 1);
      raw = line.toString();
    }

    if (raw.startsWith("(")) {
      final int indexAnn = line.indexOf(")");
      sb.append("(")
          .append(line, 1, indexAnn)
          .append(")");
      line.delete(0, indexAnn + 1);
      raw = line.toString();
    }

    if (endOf.append(KDLHelper.findStringEndSign(raw)).length() > 0) {
      sb.append(KDLHelper.getStartByEnd(endOf.toString()));
      line.delete(0, endOf.length());
      return;
    }

    raw = raw.split(" ")[0];
    raw = raw.split("=")[0];
    line.delete(0, raw.length());
    addStringToArray(array, sb.append(raw));
  }

  private static void addStringToArray(KArray array, StringBuilder value) {
    String anno = "";

    if (value.length() == 0) {
      return;
    }

    final KNode<?> nodeKey = value.charAt(0) == '=' ? array.remove(array.size() - 1) : null;
    value.delete(0, Objects.isNull(nodeKey) ? 0 : 1);
    if (value.charAt(0) == '(') {
      final int indexAnn = value.indexOf(")");
      anno = value.substring(1, indexAnn);
      value.delete(0, indexAnn + 1);
    }

    final String endOf = KDLHelper.findStringEndSign(value.toString());
    if (!endOf.isEmpty()) {
      value.delete(0, endOf.length());
      final int len = value.length();
      value.delete(len - endOf.length(), len);
      KDLHelper.trim(value);
    }
    final KNode<?> nodeValue = KDLHelper.createNodeByString(value.toString())
        .setTypeComment(anno);

    if (Objects.nonNull(nodeKey)) {
      array.add(new KPair<>(nodeKey, nodeValue));
      value.setLength(0);
      return;
    }

    array.add(nodeValue);
    value.setLength(0);
  }
}
