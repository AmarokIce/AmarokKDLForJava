import club.someoneice.kdl.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        String[] texts = {
                "Test /*Test /*Test*/ Test*/ Test",
                "Test /*Test*/ Test /*Test*/ Test /*",
                "Test */Test/* /*Test*/ */Test /*Test",
                "Test Test Test Test Test",
                "*/ /**/ /*Test Test Test/* Test /*Test",
                "Test Test*/ Test*/ Test*/ Test"
        };

        List<String> textList = new ArrayList<>(Arrays.asList(texts));
        Test test = new Test();
        test.preHandler(textList);
        textList.forEach(System.out::println);
    }

    private void preHandler(final List<String> raw) {
        mergeMultiLines(raw);
        removeAllComment(raw);
    }

    private void mergeMultiLines(final List<String> lines) {
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

    private void removeAllComment(final List<String> lines) {
        final List<String> list = new ArrayList<>();
        for (String s : lines) {
            final StringBuilder line = new StringBuilder(s);
            removeLineComment(line);
            removeSingleLineComment(line);

            final String lineStr = line.toString().trim();
            if (lineStr.isEmpty()) {
                continue;
            }
            list.add(lineStr);
        }

        removeMultiLineComment(list);
        lines.clear();
        lines.addAll(list);
    }

    private void removeLineComment(final StringBuilder line) {
        final int indexComment;
        if ((indexComment = line.indexOf("//")) != -1) {
            line.delete(indexComment, line.length());
        }
    }

    private void removeSingleLineComment(final StringBuilder line) {
        final StringBuilder temp = new StringBuilder(line.toString());

        int pass = 0;

        int startIndex;
        int endIndex;

        while((startIndex = temp.indexOf("/*")) != -1 && (endIndex = temp.indexOf("*/")) != -1) {
            endIndex += 2;

            if (endIndex < startIndex) {
                pass += endIndex;
                temp.delete(0, endIndex);
                continue;
            }

            String subTempStr;
            while ((subTempStr = temp.substring(startIndex + 2, endIndex)).contains("/*")) {
                startIndex += subTempStr.indexOf("/*") + 2;
            }

            temp.delete(startIndex, endIndex).insert(startIndex, " ");
            line.delete(startIndex + pass, endIndex + pass).insert(startIndex + pass, " ");
        }
    }

    private void removeMultiLineComment(final List<String> raw) {
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

            removeSingleLineComment(builder);
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
