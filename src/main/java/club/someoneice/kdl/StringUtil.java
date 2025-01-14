package club.someoneice.kdl;

public final class StringUtil {
    public static int findStringIn(String str, final String target) {
        int counter = 0;

        int at;
        while((at = str.indexOf(target)) != -1) {
            counter++;
            str = str.substring(at + target.length());
        }

        return counter;
    }

    public static int findStringInHead(String str, final String target) {
        int counter = 0;

        while(str.startsWith(target)) {
            counter++;
            str = str.substring(1, target.length());
        }

        return counter;
    }

    public static int findMarkRound(String str, final String markStart, final String markEnd) {
        int endAt = str.indexOf(markEnd) + 2;
        if (endAt == -1) {
            return -1;
        }

        final int startAt = str.indexOf(markStart);

        int counter = StringUtil.findStringIn(str.substring(startAt, endAt), markStart);
        while (counter > 1) {
            str = str.substring(endAt);
            endAt = str.indexOf(markEnd) + 2;
            if (endAt == -1) {
                endAt = str.length();
                break;
            }

            int count = StringUtil.findStringIn(str.substring(startAt, endAt), markStart);
            if (count == 0) {
                counter--;
            } else {
                counter += count - 1;
            }
        }

        return endAt;
    }
}
