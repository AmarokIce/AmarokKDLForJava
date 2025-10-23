package club.someoneice.kdl;

import club.someoneice.kdl.exception.KDLStyleException;
import club.someoneice.kdl.objects.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

final class KDLHelper {
  public static void mergeMultiLines(final List<String> lines) {
    final List<String> result = new ArrayList<>();
    final StringBuilder sb = new StringBuilder();

    for (final String line : lines) {
      if (line.endsWith("\\")) {
        sb.append(line, 0, line.length() - 1);
        continue;
      }

      sb.append(line);
      result.add(sb.toString());
      sb.setLength(0);
    }

    if (sb.length() > 0) {
      result.add(sb.toString());
    }

    lines.clear();
    lines.addAll(result);
  }

  public static void removeSingleLineComment(final List<String> lines) {
    final List<String> result = new ArrayList<>();

    for (String line : lines) {
      final int index = line.indexOf("//");
      if (index == -1) {
        result.add(line);
        continue;
      }
      result.add(line.substring(0, index));
    }

    lines.clear();
    lines.addAll(result);
  }

  public static void removeMultiLineComment(final List<String> lines) throws KDLStyleException {
    final List<String> result = new ArrayList<>();
    final StringBuilder sb = new StringBuilder();

    int count = 0;
    for (String line : lines) {
      if (count == 0 && !line.contains("/*") && !line.contains("*/")) {
        result.add(line);
        continue;
      }

      final StringBuilder temp = new StringBuilder(line);
      final int c = checkIndexes(temp);

      if (count == 0 && temp.indexOf("/*") > 0) {
        sb.append(temp, 0, temp.indexOf("/*"));
      }

      count += c;

      if (count < 0) {
        throw new KDLStyleException("Comment never start.");
      }

      if (count == 0 && temp.length() > 0) {
        final int indexEnd = temp.lastIndexOf("*/");
        sb.append(temp.substring(indexEnd == -1 ? 0 : indexEnd + 2));
        result.add(sb.toString());
        sb.setLength(0);
      }
    }

    lines.clear();
    lines.addAll(result);
  }

  public static int checkIndexes(final StringBuilder sb) {
    List<Integer> startIndexes;
    List<Integer> endIndexes;

    do {
      startIndexes = allIndexes(sb.toString(), "/*");
      endIndexes = allIndexes(sb.toString(), "*/");
    } while (removeComment(startIndexes, endIndexes, sb));

    return startIndexes.size() - endIndexes.size();
  }

  public static boolean removeComment(final List<Integer> startIndexes,
                                      final List<Integer> endIndexes,
                                      final StringBuilder sb) {
    for (final int endIndex : endIndexes) {
      for (int o = startIndexes.size() - 1; o >= 0; o--) {
        final int startIndex = startIndexes.get(o);
        if (startIndex > endIndex) {
          continue;
        }

        sb.delete(startIndex, endIndex + 2);
        return true;
      }
    }

    return false;
  }

  public static void removeSlashdashComment(final List<String> lines) {
    final List<String> result = new ArrayList<>();

    int count = 0;
    for (final String line : lines) {
      final String tempLine = line.trim();
      if (count != 0) {
        if (tempLine.equals("}")) {
          count--;
        } else if (tempLine.endsWith("{")) {
          count++;
        }
        continue;
      }

      if (tempLine.startsWith("/-")) {
        count += line.endsWith("{") ? 1 : 0;
        continue;
      }

      result.add(line);
    }

    lines.clear();
    lines.addAll(result);
  }

  public static List<Integer> allIndexes(String str, final String target) {
    List<Integer> indexes = new ArrayList<>();

    int at = -1;
    while ((at = str.indexOf(target, at + 1)) != -1) {
      indexes.add(at);
    }

    return indexes;
  }

  public static int countStartStr(String target, final String c) {
    int counter = 0;
    while (target.startsWith(c)) {
      target = target.substring(c.length());
      counter++;
    }
    return counter;
  }

  public static String forEachFill(final String c, int count) {
    final StringBuilder sb = new StringBuilder();
    for (int i = 0; i < count; i++) {
      sb.append(c);
    }
    return sb.toString();
  }

  public static void trim(final StringBuilder sb) {
    if (sb.length() == 0) {
      return;
    }

    String[] lines = sb.toString().split("\n");
    int min = -1;

    for (String line : lines) {
      int minIn = countStartStr(line, " ");
      if (min == -1 || min > minIn) {
        min = minIn;
      }
    }

    sb.setLength(0);
    for (String line : lines) {
      sb.append(line.substring(min)).append('\n');
    }
    sb.delete(sb.length() - 1, sb.length());
  }

  public static String findStringEndSign(final String target) {
    final boolean flag = (target.startsWith("#") && target.contains("#\""))
        || target.startsWith("\"");
    if (!flag) {
      return "";
    }

    if (target.startsWith("#")) {
      final StringBuilder endOfSb = new StringBuilder();
      final int countSignRaw = KDLHelper.countStartStr(target, "#");
      final String signRaw = KDLHelper.forEachFill("#", countSignRaw);
      endOfSb.append(signRaw);

      final int countSignStr = KDLHelper.countStartStr(
          target.substring(countSignRaw), "\"");
      final String signStr = KDLHelper.forEachFill("\"", countSignStr);
      endOfSb.insert(0, signStr);
      return endOfSb.toString();
    } else {
      return target.startsWith("\"\"\"") ? "\"\"\"" : "\"";
    }
  }

  public static String getStartByEnd(final String target) {
    if (!target.contains("#")) {
      return target;
    }

    final StringBuilder endOfSb = new StringBuilder();
    endOfSb.append(target.substring(target.indexOf("#")));
    endOfSb.append(target.substring(0, target.indexOf("#")));
    return endOfSb.toString();
  }

  public static KNode<?> createNodeByString(String target) {
    KNode<?> node;
    if (Objects.nonNull(node = KDLHelper.checkNull(target))) {
      return node;
    }

    if (Objects.nonNull(node = KDLHelper.checkBoolean(target))) {
      return node;
    }

    if (Objects.nonNull(node = KDLHelper.checkNumber(target))) {
      return node;
    }

    return new KString(target);
  }

  @Nullable
  public static KNumber checkNumber(String raw) {
    raw = raw.replace("_", "").toLowerCase();
    raw = raw.startsWith("+") ? raw.substring(1) : raw;

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
        return new KNumber(Integer.parseInt(raw.substring(2), 16));
      }

      if (raw.startsWith("0o")) {
        return new KNumber(Integer.parseInt(raw.substring(2), 10));
      }

      if (raw.startsWith("0b")) {
        return new KNumber(Integer.parseInt(raw.substring(2), 2));
      }

      int e = 0;

      if (raw.contains("e")) {
        final String[] dat = raw.split("e");
        e = Integer.parseInt(dat[1]);
        raw = dat[0];
      }

      double db = 0;
      final boolean flag = raw.startsWith(".");
      if (flag) {
        db = Double.parseDouble(raw.substring(1));
        db /= 10;
      } else {
        db = Double.parseDouble(raw);
      }

      if (e != 0) {
        db /= (e * 10);
      }

      return new KNumber(db);
    } catch (Exception ex) {
      // Jump to string.
    }

    return null;
  }

  @Nullable
  public static KBoolean checkBoolean(String raw) {
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
  public static KNull checkNull(String raw) {
    raw = raw.toLowerCase();
    if (raw.equals("#null")) {
      return KNull.INSTANCE;
    }

    return null;
  }
}
