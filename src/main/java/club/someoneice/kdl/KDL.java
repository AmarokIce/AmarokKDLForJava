package club.someoneice.kdl;

import club.someoneice.kdl.objects.KDLNode;
import club.someoneice.kdl.objects.KDLValue;

import java.util.ArrayList;
import java.util.List;

public final class KDL {


    private KDLNode parse(String[] lines) {
        final String name = null;
        List<KDLValue<?>> values = new ArrayList<>();

        lines = preHandler(lines);
        // TODO

        return new KDLNode(name, values);
    }

    private String[] preHandler(final String[] raw) {

        List<String> list = new ArrayList<>();
        for (int i = 0; i < raw.length; i++) {
            final StringBuilder line = new StringBuilder(raw[i]);

            i = mergeMultiLines(line, raw, i);
            removeSingleLineComment(line);
            removeMultiLineComment(line);

            list.add(line.toString());
        }

        return list.toArray(new String[0]);
    }

    private static int mergeMultiLines(final StringBuilder line, final String[] lines, int lineNumber) {
        while (line.toString().endsWith("\\")) {
            String raw = line.toString();
            line.setLength(0);
            line.append(raw, 0, raw.length() - 1).append(" ").append(lines[++lineNumber]);
        }

        return lineNumber;
    }

    private static void removeSingleLineComment(final StringBuilder line) {
        int indexComment;
        if ((indexComment = line.indexOf("//")) != -1) {
            line.delete(indexComment, line.length());
        }
    }

    private static void removeMultiLineComment(final StringBuilder line) {
        final int startAt = line.indexOf("/*");

        int endAt = line.indexOf("*/") + 2;
        if (endAt == -1) {
            line.delete(startAt, line.length());
            return;
        }

        int counter = StringUtil.findStringIn(line.substring(startAt, endAt), "/*");
        while (counter > 1) {
            line.delete(startAt, endAt);
            endAt = line.indexOf("*/") + 2;
            if (endAt == -1) {
                endAt = line.length();
                break;
            }

            int count = StringUtil.findStringIn(line.substring(startAt, endAt), "/*");
            if (count == 0) {
                counter--;
            } else {
                counter += count - 1;
            }
        }

        line.delete(startAt, endAt);
    }
}
