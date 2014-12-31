package org.hcsoups.hardcore.utils;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtils {

    public static long getTimeStamp(String time) {
        long timeReturn;
        try {
            timeReturn = parseDateDiff(time, true);
        } catch (Exception e) {
            timeReturn = 0;
        }
        return timeReturn;
    }

    // Copyright essentials, all credits to them, this is here to remove
    // dependency on it, I did not create these functions! <-- ya thx :P)
    public static long parseDateDiff(String time, boolean future) throws Exception {
        Pattern timePattern = Pattern.compile("(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*(?:s[a-z]*)?)?", Pattern.CASE_INSENSITIVE);
        Matcher m = timePattern.matcher(time);
        int years = 0;
        int months = 0;
        int weeks = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        boolean found = false;
        while (m.find()) {
            if (m.group() == null || m.group().isEmpty()) {
                continue;
            }
            for (int i = 0; i < m.groupCount(); i++) {
                if (m.group(i) != null && !m.group(i).isEmpty()) {
                    found = true;
                    break;
                }
            }
            if (found) {
                if (m.group(1) != null && !m.group(1).isEmpty())
                    years = Integer.parseInt(m.group(1));
                if (m.group(2) != null && !m.group(2).isEmpty())
                    months = Integer.parseInt(m.group(2));
                if (m.group(3) != null && !m.group(3).isEmpty())
                    weeks = Integer.parseInt(m.group(3));
                if (m.group(4) != null && !m.group(4).isEmpty())
                    days = Integer.parseInt(m.group(4));
                if (m.group(5) != null && !m.group(5).isEmpty())
                    hours = Integer.parseInt(m.group(5));
                if (m.group(6) != null && !m.group(6).isEmpty())
                    minutes = Integer.parseInt(m.group(6));
                if (m.group(7) != null && !m.group(7).isEmpty())
                    seconds = Integer.parseInt(m.group(7));
                break;
            }
        }
        if (!found)
            throw new  Exception("Illegal Date");

        if (years > 20)
            throw new Exception("Illegal Date");

        Calendar c = new GregorianCalendar();
        if (years > 0)
            c.add(Calendar.YEAR, years * (future ? 1 : -1));
        if (months > 0)
            c.add(Calendar.MONTH, months * (future ? 1 : -1));
        if (weeks > 0)
            c.add(Calendar.WEEK_OF_YEAR, weeks * (future ? 1 : -1));
        if (days > 0)
            c.add(Calendar.DAY_OF_MONTH, days * (future ? 1 : -1));
        if (hours > 0)
            c.add(Calendar.HOUR_OF_DAY, hours * (future ? 1 : -1));
        if (minutes > 0)
            c.add(Calendar.MINUTE, minutes * (future ? 1 : -1));
        if (seconds > 0)
            c.add(Calendar.SECOND, seconds * (future ? 1 : -1));
        return c.getTimeInMillis();
    }

    public static String formatDateDiff(Calendar fromDate, Calendar toDate) {
        boolean future = false;
        if (toDate.equals(fromDate)) {
            return "now";
        }
        if (toDate.after(fromDate)) {
            future = true;
        }

        StringBuilder sb = new StringBuilder();
        int[] types = new int[] { Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND };
        String[] names = new String[] { "year", "years", "month", "months", "day", "days", "hour", "hours", "minute", "minutes", "second", "seconds" };
        for (int i = 0; i < types.length; i++) {
            int diff = dateDiff(types[i], fromDate, toDate, future);
            if (diff > 0) {
                sb.append(" ").append(diff).append(" ").append(names[i * 2 + (diff > 1 ? 1 : 0)]);
            }
        }
        if (sb.length() == 0) {
            return "now";
        }
        return sb.toString().trim();
    }

    public static String formatDateDiff(long date) {
        Calendar now = new GregorianCalendar();
        Calendar c = new GregorianCalendar();
        c.setTimeInMillis(date);
        return formatDateDiff(now, c);
    }

    private static int dateDiff(int type, Calendar fromDate, Calendar toDate, boolean future) {
        int diff = 0;
        long savedDate = fromDate.getTimeInMillis();
        while ((future && !fromDate.after(toDate)) || (!future && !fromDate.before(toDate))) {
            savedDate = fromDate.getTimeInMillis();
            fromDate.add(type, future ? 1 : -1);
            diff++;
        }
        diff--;
        fromDate.setTimeInMillis(savedDate);
        return diff;
    }

    public static String getShortTime(long ms) {
        String s = getTime(ms);
        String[] vals = s.split(" ");
        if (vals.length < 2) {
            return s;
        }
        return vals[0] + " " + vals[1];
    }

    public static String getTimeUntil(long epoch) {
        epoch -= System.currentTimeMillis();

        return getTime(epoch);
    }

    public static String getTime(long ms) {
        double dub = Double.parseDouble(ms + "") / 1000.0D;
        String str = "" + Math.ceil(dub);
        ms = Long.parseLong(str.substring(0, (str.length() - 1) - 1).replace(".", ""));
        StringBuilder sb = new StringBuilder(40);
        if (ms / 31449600L > 0L) {
            long years = ms / 31449600L;
            if (years > 100L) {
                return "Never";
            }
            sb.append(years + (years == 1L ? " year " : " years "));
            ms -= years * 31449600L;
        }
        if (ms / 2620800L > 0L) {
            long months = ms / 2620800L;
            sb.append(months + (months == 1L ? " month " : " months "));
            ms -= months * 2620800L;
        }
        if (ms / 604800L > 0L) {
            long weeks = ms / 604800L;
            sb.append(weeks + (weeks == 1L ? " week " : " weeks "));
            ms -= weeks * 604800L;
        }
        if (ms / 86400L > 0L) {
            long days = ms / 86400L;
            sb.append(days + (days == 1L ? " day " : " days "));
            ms -= days * 86400L;
        }
        if (ms / 3600L > 0L) {
            long hours = ms / 3600L;
            sb.append(hours + (hours == 1L ? " hour " : " hours "));
            ms -= hours * 3600L;
        }
        if (ms / 60L > 0L) {
            long minutes = ms / 60L;
            sb.append(minutes + (minutes == 1L ? " minute " : " minutes "));
            ms -= minutes * 60L;
        }
        if (ms > 0L) {
            sb.append(ms + (ms == 1L ? " second " : " seconds "));
        }
        if (sb.length() > 1) {
            sb.replace(sb.length() - 1, sb.length(), "");
        } else {
            sb = new StringBuilder("N/A");
        }
        return sb.toString();
    }

    public static long getTime(String[] args) {
        String arg = args[2].toLowerCase();
        int modifier;
        if (arg.startsWith("hour")) {
            modifier = 3600;
        } else {
            if (arg.startsWith("min")) {
                modifier = 60;
            } else {
                if (arg.startsWith("sec")) {
                    modifier = 1;
                } else {
                    if (arg.startsWith("week")) {
                        modifier = 604800;
                    } else {
                        if (arg.startsWith("day")) {
                            modifier = 86400;
                        } else {
                            if (arg.startsWith("year")) {
                                modifier = 31449600;
                            } else {
                                if (arg.startsWith("month")) {
                                    modifier = 2620800;
                                } else {
                                    modifier = 0;
                                }
                            }
                        }
                    }
                }
            }
        }
        double time = 0.0D;
        try {
            time = Double.parseDouble(args[1]);
        } catch (NumberFormatException localNumberFormatException) {
        }
        for (int j = 0; j < args.length - 2; j++) {
            args[j] = args[(j + 2)];
        }
        args[(args.length - 1)] = "";
        args[(args.length - 2)] = "";

        return (modifier * (long) time) * 1000L;
    }

    public static boolean elapsed(long from, long required) {
        return System.currentTimeMillis() - from > required;
    }


}