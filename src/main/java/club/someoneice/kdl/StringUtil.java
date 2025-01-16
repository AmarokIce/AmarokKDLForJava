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
        int pass = 0;

        int startAt;
        int endAt;

        do {
            startAt = str.indexOf(markStart);
            endAt = str.indexOf(markEnd);

            if (endAt == -1 || startAt == -1) {
                return -1;
            }

            if (endAt < startAt) {
                endAt += markEnd.length();

                pass += endAt;
                str = str.substring(endAt);
            } else break;
        } while (true);

        startAt += markStart.length();
        endAt += markEnd.length();

        int counter = StringUtil.findStringIn(str.substring(startAt, endAt), markStart);
        while (counter > 1) {
            str = str.substring(endAt);
            endAt = str.indexOf(markEnd);
            if (endAt == -1) {
                return pass + str.length();
            }

            endAt += markEnd.length();

            int count = StringUtil.findStringIn(str.substring(startAt, endAt), markStart);
            if (count == 0) {
                counter--;
            } else {
                counter += count - 1;
            }
        }

        return pass + endAt;
    }
}
