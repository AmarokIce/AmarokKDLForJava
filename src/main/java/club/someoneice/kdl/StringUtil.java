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
}
