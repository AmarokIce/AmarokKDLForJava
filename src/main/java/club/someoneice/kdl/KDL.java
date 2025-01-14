package club.someoneice.kdl;

import club.someoneice.kdl.objects.KDLNode;
import club.someoneice.kdl.objects.KDLValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * KDL is the core parse for KDL files.
 *
 * @author AmarokIce
 */
public final class KDL {
    private KDLNode parse(final String[] lines) {
        final List<String> list = Arrays.asList(lines);

        preHandler(list);
        // TODO

        return null;
    }


    private KDLValue<?>[] handler(final String[] raw) {
        final Map<String, String> dataPool = new HashMap<>();
        final String[] lines = replaceMultilineData(raw, dataPool);

        return null;
    }

    private String[] replaceMultilineData(final String[] raw, final Map<String, String> multiPools) {
        final List<String> list = new ArrayList<>(raw.length);
        int keyIndex = 0;

        for (int i = 0; i < raw.length; i++) {
            final String line = raw[i];
            // Multi String Handler
            if (line.endsWith("\"\"\"")) {
                final int indexOfSpace = line.lastIndexOf(' ');
                final String keyIn = line.substring(indexOfSpace, line.length());
                final StringBuilder builder = new StringBuilder();
                final String key = String.format("${DSL_KEY_INPUT_%d}", keyIndex++);
                final boolean flag = keyIn.startsWith("/-");

                i = findMutliString(raw, i + 1, flag ? keyIn.substring(2) : keyIn, builder);

                if (flag) {
                    continue;
                }
                multiPools.put(key, builder.toString());
                list.add(line.replace(keyIn, key));
                continue;
            }

            if (line.endsWith("{")) {
                // TODO
            }

            list.add(line);
        }

        return list.toArray(new String[0]);
    }


    /**
     * Handle the multi-line string / raw string.
     *
     * @param raw the raw lines.
     * @param startAt the line startAt
     * @param keyIn the key input
     * @param builder StringBuilder object to hold the datas.
     * @return
     */
    private int findMutliString(final String[] raw, final int startAt, final String keyIn, final StringBuilder builder) {
        for (int i = startAt; i < raw.length; i++) {
            final String line = raw[i];
            builder.append('\n').append(line);
            if (line.endsWith(keyIn)) {
                return i;
            }
        }

        return raw.length;
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
     * @return The data finish pre handle.
     */
    private void preHandler(final List<String> raw) {
        mergeMultilines(raw);
        removeAllComment(raw);
    }

    private void mergeMultilines(final List<String> lines) {
        final List<String> raw = new ArrayList<>(lines);
        final StringBuilder builder = new StringBuilder();

        lines.clear();

        for (final String lineRaw : raw) {
            builder.append(lineRaw.trim());
            if (!lineRaw.endsWith("\\")) {
                lines.add(builder.toString().trim());
                builder.setLength(0);
                continue;
            }
            builder.substring(0, builder.length() - 2);
            builder.append(" ");
        }
    }

    private void removeAllComment(final List<String> lines) {
        for(int i = 0; i < lines.size(); i++) {
            final StringBuilder line = new StringBuilder(lines.get(i));
            removeSingleLineComment(line);
            lines.set(i, line.toString().trim());
        }

        final List<String> list = new ArrayList<>(lines);
        removeMultiLineComment(list);
        lines.clear();
        lines.addAll(list);
    }

    private void removeSingleLineComment(final StringBuilder line) {
        // Single line
        final int indexComment;
        if ((indexComment = line.indexOf("//")) != -1) {
            line.delete(indexComment, line.length());
        }

        // Multi line
        while(line.indexOf("/*") != -1) {
            final int startAt = line.indexOf("/*");
            final int endAt = StringUtil.findMarkRound(line.toString(), "/*", "*/");

            if (endAt == -1) {
                return;
            }

            line.delete(startAt, endAt);
            line.insert(startAt, " ");
        }
    }

    private void removeMultiLineComment(final List<String> raw) {
        final List<String> list = new ArrayList<>();
        final StringBuilder builder = new StringBuilder();

        final Iterator<String> itor = raw.iterator();
        while(itor.hasNext()) {
            final String line = itor.next();

            if (line.contains("/*")) {
                builder.append(line);
                continue;
            }

            if (builder.indexOf("/*") != -1) {
                removeSingleLineComment(builder);
                continue;
            }

            list.add(builder.toString());
            return;
        }
    }
}
