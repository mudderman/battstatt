package net.bajawa.lib.util;


public class DateUtils {

    public static String getStyledSelectedDays(String string, boolean widget) {

        String[] weekdayAbbrNames = { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun" };
        String[] weekdayNames = { "Mondays", "Tuesdays", "Wednesdays", "Thursdays", "Fridays", "Saturdays", "Sundays" };

        if (string.equals("0123456") || string.equals("")) {
            return "<b>Every day</b>";

        } else if (string.equals("01234")) {
            return "<b>Weekdays</b>";

        } else if (string.equals("56")) {
            return "<b>Weekends</b>";
            
        } else if (string.length() == 2 && !widget) {
            StringBuilder sb = new StringBuilder();

            int index = Integer.parseInt(Character.valueOf(string.charAt(0)).toString());
            int index2 = Integer.parseInt(Character.valueOf(string.charAt(1)).toString());

            sb.append("<b>").append(weekdayNames[index]).append("</b> and <b>").append(weekdayNames[index2]).append("</b>");
            return sb.toString();

        } else if (string.length() == 1) {
            int index = Integer.parseInt(Character.valueOf(string.charAt(0)).toString());
            return "<b>" + weekdayNames[index] + "</b>";
        }
        
        // "Else"
        StringBuilder builder = new StringBuilder(string.length() * 3);
        
        builder.append("<b>");
        for (int i = 0; i < string.length(); i++) {
            int index = Integer.parseInt(Character.valueOf(string.charAt(i)).toString());
            builder.append(weekdayAbbrNames[index]).append(", ");
        }
        
        String retString = builder.toString();
        if (retString.endsWith(", ")) {
            builder = new StringBuilder(retString.substring(0, retString.length() - 2));
            builder.append("</b>");
            return builder.toString();
        }
        
        builder.append("</b>");
        
        return retString;
    }
    
    public static String getFormattedSelectedDays(String string, boolean widget) {
        string = getStyledSelectedDays(string, widget);
        string = string.replace("<b>", "").replace("</b>", "");
        if (string.endsWith(", ")) {
            string = string.substring(0, string.length() - 2);
        }
        return string;
    }

}
