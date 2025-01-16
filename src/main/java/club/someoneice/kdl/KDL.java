package club.someoneice.kdl;

import club.someoneice.kdl.exception.UnexpectedTextInputException;
import club.someoneice.kdl.objects.KDLNode;
import club.someoneice.kdl.objects.KDLValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * KDL is the core parse for KDL files.
 *
 * @author AmarokIce
 */
public final class KDL {
    private static final Map<String, String> variriablePools = new HashMap<>();

    private static KDLNode parse(final String[] lines) {
        final List<String> list = Arrays.asList(lines);

        preHandler(list);
        // TODO

        return null;
    }


    private static KDLValue<?>[] handler(final String[] raw) {
        final Map<String, String> dataPool = new HashMap<>();

        return null;
    }


    /**
     * The pre handler will do three things:<br>
     * - Clean the line break;<br>
     * - Remove line comment;<br>
     * - Remove multi-line comment;<br>
     * <br>
     * The "slashdash" comment will handle on common handler.<br>
     * Because the multi-line's data like multi-line text should handle in deep.
     *
     * @param raw The raw data of lines.
     */
    private static void preHandler(final List<String> raw) {
        mergeMultiLines(raw);
        removeAllComment(raw);
    }

    private static void mergeMultiLines(final List<String> lines) {
        final List<String> raw = new ArrayList<>(lines);
        final StringBuilder builder = new StringBuilder();

        lines.clear();

        for (final String lineRaw : raw) {
            builder.append(lineRaw.trim());
            if (builder.charAt(builder.length() - 1) != '\\') {
                lines.add(builder.toString().trim());
                builder.setLength(0);
                continue;
            }
            builder.setLength(builder.length() - 2);
            builder.append(" ");
        }
    }

    private static void findAndReplaceAllRawString(final List<String> lines) {
        for (int i = 0; i < lines.size(); i++) {
            final String line = lines.get(i);

            // TODO while
            if (!line.contains("#\"")) {
                continue;
            }

            findRawString(lines, i);
        }
    }

    private static void findRawString(final List<String> lines, final int lineAt) {
        int startAt = 0;

        final StringBuilder line = new StringBuilder(lines.get(lineAt));
        final int indexAt = line.indexOf("#\"");

        final boolean isMultiString = line.substring(indexAt + 1).startsWith("\"\"\"\"");
        final StringBuilder marked = new StringBuilder();
        for(int i = indexAt + 1; i > 0; i--) {
            final char c = line.charAt(i);
            if (c != '#') {
                startAt = i + 1;
                break;
            }
            marked.append(c);
        }

        if (!isMultiString) {
            final int index = line.indexOf("\"" + marked, indexAt + 1);
            if (index == -1) {
                throw new UnexpectedTextInputException("The single line of raw string has no end! At line " + lineAt);
            }

            final int counter = variriablePools.size();
            final String variavleName = "$string%" + counter;
            final String data = line.substring(startAt, index + marked.length() + 1);

            variriablePools.put(variavleName, data);

            line.delete(startAt, index + marked.length() + 1).insert(index, variavleName);
            lines.set(lineAt, line.toString());
        }

        // TODO multi line.
    }

    private static void removeAllComment(final List<String> lines) {
        final List<String> list = new ArrayList<>();
        for (String s : lines) {
            final StringBuilder line = new StringBuilder(s);
            removeSingleLineComment(line);
            removeSingleLineMultiComment(line);

            final String lineStr = line.toString().trim();
            if (lineStr.isEmpty()) {
                continue;
            }
            list.add(lineStr);
        }

        removeMultiLineMultiComment(list);
        lines.clear();
        lines.addAll(list);
    }

    private static void removeSingleLineComment(final StringBuilder line) {
        final int indexComment;
        if ((indexComment = line.indexOf("//")) != -1) {
            line.delete(indexComment, line.length());
        }
    }

    private static void removeSingleLineMultiComment(final StringBuilder line) {
        int pass = 0;

        int startIndex;
        int endIndex;
        while((startIndex = line.indexOf("/*", pass)) != -1 && (endIndex = line.indexOf("*/", pass)) != -1) {
            endIndex += 2;

            if (endIndex < startIndex) {
                pass += endIndex;
                continue;
            }

            String subTempStr;
            while ((subTempStr = line.substring(startIndex + 2, endIndex)).contains("/*")) {
                startIndex += subTempStr.indexOf("/*") + 2;
            }

            line.delete(startIndex, endIndex).insert(startIndex, " ");
        }
    }

    private static void removeMultiLineMultiComment(final List<String> raw) {
        final List<String> list = new ArrayList<>();
        final StringBuilder builder = new StringBuilder();

        boolean flag = false;

        for (String line : raw) {
            builder.append(line);

            if (line.contains("/*")) {
                flag = true;
                continue;
            }

            if (!flag) {
                list.add(builder.toString().trim());
                builder.setLength(0);
                continue;
            }

            removeSingleLineMultiComment(builder);
            if (builder.indexOf("/*") != -1) {
                continue;
            }

            list.add(builder.toString().trim());
            flag = false;
            builder.setLength(0);
        }

        raw.clear();
        raw.addAll(list);
    }
}
