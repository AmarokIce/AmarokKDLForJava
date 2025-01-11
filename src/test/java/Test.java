import club.someoneice.kdl.StringUtil;

public class Test {
    public static void main(String[] args) {
        String text = "String/* String /* /* String */ String */ */ String /* String */ /* String /* String */ /* String */ /* String */ /* String */ */  /* String */ /* String */";
        System.out.println(text);

        while(text.contains("/*")) {
            StringBuilder builder = new StringBuilder(text);
            removeMultiLineComment(builder);
            text = builder.toString();
        }

        System.out.println(text);
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
