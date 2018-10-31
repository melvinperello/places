package com.melvinperello.places.util;

public class StringTool {

    /**
     * A way to split CSV Strings.
     * <p>
     * source: https://stackoverflow.com/questions/15738918/splitting-a-csv-file-with-quotes-as-text-delimiter-using-string-split
     * answered by: Achintya Jha
     *
     * @param csv
     * @return
     */
    public static String[] splitCSV(String csv) {
        return csv.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
    }

    public static String unqoute(String qoutedString) {
        if (qoutedString.isEmpty()) {
            return qoutedString;
        }
        try {
            return qoutedString.substring(1, qoutedString.length() - 1);
        } catch (IndexOutOfBoundsException noindex) {
            return qoutedString;
        }
    }
}
