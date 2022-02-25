package at.pl.razer.util;

public class JSON {

    public static String readField(String json, String field) {
        String haystack = json;
        String needle = "\"" + field + "\"";
        int from = haystack.indexOf(needle);
        if (from < 0) {
            return null;
        }
        from = from + needle.length();

        while (Character.isWhitespace(haystack.charAt(from))) from++;
        if (haystack.charAt(from) != ':') {
            return null;
        }
        from++;
        while (Character.isWhitespace(haystack.charAt(from))) from++;

        boolean usingQuotes = haystack.charAt(from) == '"';
        if (usingQuotes) {
            from++;
        }
        int to;
        if (usingQuotes)  {
            to = haystack.indexOf("\"", from);
        } else {
            to = haystack.indexOf("}", from);
            if (to < 0) to = haystack.indexOf(",", from);
            if (to < 0) to = haystack.indexOf(" ", from);
            if (to < 0) return null;
        }

        if (to < 0) {
            return null;
        }
        return haystack.substring(from, to);
    }

}
