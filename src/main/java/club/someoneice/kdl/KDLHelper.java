package club.someoneice.kdl;

import club.someoneice.kdl.exception.KDLStyleException;

import java.util.ArrayList;
import java.util.List;

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
        sb.append(temp);
      }

      count += c;

      if (count < 0) {
        throw new KDLStyleException("Comment never start.");
      }

      if (count == 0 && temp.length() > 0) {
        final int indexEnd = temp.lastIndexOf("*/") + 2;
        sb.append(temp.substring(indexEnd));
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
    for (String line : lines) {
      line = line.trim();
      if (count != 0) {
        if (line.equals("}")) {
          count--;
        } else if (line.endsWith("{")) {
          count++;
        }
        continue;
      }

      if (line.startsWith("/-")) {
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
      sb.append(line.replaceFirst(forEachFill(" ", min), ""));
    }
  }
}
