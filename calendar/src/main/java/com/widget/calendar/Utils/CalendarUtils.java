package com.widget.calendar.Utils;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jimmy on 2016/10/6 0006.
 */
public class CalendarUtils {

    private static CalendarUtils sUtils;
    private Map<String, int[]> sAllHolidays = new HashMap<>();
    private Map<String, List<Integer>> sMonthTaskHint = new HashMap<>();

    public static synchronized CalendarUtils getInstance(Context context) {
        if (sUtils == null) {
            sUtils = new CalendarUtils();
            //sUtils.initAllHolidays(context);
        }
        return sUtils;
    }

    /*private void initAllHolidays(Context context) {
        try {
            InputStream is = context.getAssets().open("holiday.json");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int i;
            while ((i = is.read()) != -1) {
                baos.write(i);
            }
            sAllHolidays = new Gson().fromJson(baos.toString(), new TypeToken<Map<String, int[]>>() {
            }.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Integer> addTaskHints(int year, int month, List<Integer> days) {
        String key = hashKey(year, month);
        List<Integer> hints = sUtils.sMonthTaskHint.get(key);
        if (hints == null) {
            hints = new ArrayList<>();
            hints.removeAll(days); // 避免重复
            hints.addAll(days);
            sUtils.sMonthTaskHint.put(key, hints);
        } else {
            hints.addAll(days);
        }
        return hints;
    }

    public List<Integer> removeTaskHints(int year, int month, List<Integer> days) {
        String key = hashKey(year, month);
        List<Integer> hints = sUtils.sMonthTaskHint.get(key);
        if (hints == null) {
            hints = new ArrayList<>();
            sUtils.sMonthTaskHint.put(key, hints);
        } else {
            hints.removeAll(days);
        }
        return hints;
    }

    public boolean addTaskHint(int year, int month, int day) {
        String key = hashKey(year, month);
        List<Integer> hints = sUtils.sMonthTaskHint.get(key);
        if (hints == null) {
            hints = new ArrayList<>();
            hints.add(day);
            sUtils.sMonthTaskHint.put(key, hints);
            return true;
        } else {
            if (!hints.contains(day)) {
                hints.add(day);
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean removeTaskHint(int year, int month, int day) {
        String key = hashKey(year, month);
        List<Integer> hints = sUtils.sMonthTaskHint.get(key);
        if (hints == null) {
            hints = new ArrayList<>();
            sUtils.sMonthTaskHint.put(key, hints);
        } else {
            if (hints.contains(day)) {
                Iterator<Integer> i = hints.iterator();
                while (i.hasNext()) {
                    Integer next = i.next();
                    if (next == day) {
                        i.remove();
                        break;
                    }
                }
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public List<Integer> getTaskHints(int year, int month) {
        String key = hashKey(year, month);
        List<Integer> hints = sUtils.sMonthTaskHint.get(key);
        if (hints == null) {
            hints = new ArrayList<>();
            sUtils.sMonthTaskHint.put(key, hints);
        }
        return hints;
    }

    holiday.json文件
    {
	"201612":[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0],
	"20171":[1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,1,1,1,1,1,1,1,0,2,0,0,0,0,0,0,0],
	"20172":[1,1,1,1,1,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
	"20173":[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,1,1,1,0,0,0,0],
	"20174":[0,0,0,0,0,0,2,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0],
	"20175":[1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,1,1,1,0,0,0,0,0,0,0,0,0,0,0],
	"20176":[1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
	"20177":[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
	"20178":[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
	"20179":[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,1,1,1,1,1,1,1],
	"201710":[1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
	"201711":[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
	"201712":[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0]

    }



    */

    private static String hashKey(int year, int month) {
        return String.format("%s:%s", year, month);
    }

    /**
     * 通过年份和月份 得到当月的日子
     *
     * @param year
     * @param month
     * @return
     */
    public static int getMonthDays(int year, int month) {
        month++;
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)) {
                    return 29;
                } else {
                    return 28;
                }
            default:
                return -1;
        }
    }

    /**
     * 返回当前月份1号位于周几
     *
     * @param year  年份
     * @param month 月份，传入系统获取的，不需要正常的
     * @return 日：1		一：2		二：3		三：4		四：5		五：6		六：7
     */
    public static int getFirstDayWeek(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 获得两个日期距离几周
     *
     * @return
     */
    public static int getWeeksAgo(int lastYear, int lastMonth, int lastDay, int year, int month, int day) {
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        start.set(lastYear, lastMonth, lastDay);
        end.set(year, month, day);
        int week = start.get(Calendar.DAY_OF_WEEK);
        start.add(Calendar.DATE, -week);
        week = end.get(Calendar.DAY_OF_WEEK);
        end.add(Calendar.DATE, 7 - week);
        float v = (end.getTimeInMillis() - start.getTimeInMillis()) / (3600 * 1000 * 24 * 7 * 1.0f);
        return (int) (v - 1);
    }

    /**
     * 获得两个日期距离几个月
     *
     * @return
     */
    public static int getMonthsAgo(int lastYear, int lastMonth, int year, int month) {
        return (year - lastYear) * 12 + (month - lastMonth);
    }

    public static int getWeekRow(int year, int month, int day) {
        int week = getFirstDayWeek(year, month);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        int lastWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (lastWeek == 7)
            day--;
        return (day + week - 1) / 7;
    }

    /**
     * 根据国历获取假期
     *
     * @return
     */
    public static String getHolidayFromSolar(int year, int month, int day) {
        String message = "";
        if (month == 0 && day == 1) {
            message = "元旦";
        } else if (month == 1 && day == 14) {
            message = "情人节";
        } else if (month == 2 && day == 8) {
            message = "妇女节";
        } else if (month == 2 && day == 12) {
            message = "植树节";
        } else if (month == 3) {
            if (day == 1) {
                message = "愚人节";
            } else if (day >= 4 && day <= 6) {
                if (year <= 1999) {
                    int compare = (int) (((year - 1900) * 0.2422 + 5.59) - ((year - 1900) / 4));
                    if (compare == day) {
                        message = "清明节";
                    }
                } else {
                    int compare = (int) (((year - 2000) * 0.2422 + 4.81) - ((year - 2000) / 4));
                    if (compare == day) {
                        message = "清明节";
                    }
                }
            }
        } else if (month == 4 && day == 1) {
            message = "劳动节";
        } else if (month == 4 && day == 4) {
            message = "青年节";
        } else if (month == 4 && day == 12) {
            message = "护士节";
        } else if (month == 5 && day == 1) {
            message = "儿童节";
        } else if (month == 6 && day == 1) {
            message = "建党节";
        } else if (month == 7 && day == 1) {
            message = "建军节";
        } else if (month == 8 && day == 10) {
            message = "教师节";
        } else if (month == 9 && day == 1) {
            message = "国庆节";
        } else if (month == 10 && day == 11) {
            message = "光棍节";
        } else if (month == 11 && day == 25) {
            message = "圣诞节";
        }
        return message;
    }

    public int[] getHolidays(int year, int month) {
        int holidays[];
        if (sUtils.sAllHolidays != null) {
            holidays = sUtils.sAllHolidays.get(year + "" + month);
            if (holidays == null) {
                holidays = new int[42];
            }
        } else {
            holidays = new int[42];
        }
        return holidays;
    }

    public static int getMonthRows(int year, int month) {
        int size = getFirstDayWeek(year, month) + getMonthDays(year, month) - 1;
        return size % 7 == 0 ? size / 7 : (size / 7) + 1;
    }

    /**
     * 根据ViewPager position 得到对应年月
     *
     * @param position
     * @return
     */
    public static int[] positionToDate(int position, int startY, int startM) {
        int year = position / 12 + startY;
        int month = position % 12 + startM;

        if (month > 12) {
            month = month % 12;
            year = year + 1;
        }

        return new int[]{year, month};
    }

    /**
     * 根据年月得到ViewPager position
     *
     * @param year
     * @param month
     * @return
     */
    public static int dateToPosition(int year, int month, int startY, int startM) {
        return (year - startY) * 12 + month - startM;
    }

    /**
     * 计算当前日期
     *
     * @return
     */
    public static int[] getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        return new int[]{calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)};
    }

    public static int[] strToArray(String str) {
        if (!TextUtils.isEmpty(str)) {
            String[] strArray = str.split("\\.");
            int[] result = new int[strArray.length];
            for (int i = 0; i < strArray.length; i++) {
                result[i] = Integer.valueOf(strArray[i]);
            }
            return result;
        }
        return null;
    }

    //指定年月，获取该月对应的周日的日期
    public static ArrayList<String> getSundays(int year, int month){
        int monthDays = CalendarUtils.getMonthDays(year, month);
        ArrayList<String> sundays = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        for(int i =1;i<monthDays+1;i++){
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.DAY_OF_MONTH, i);
            int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
            if(w==0){
                sundays.add(i+"");
            }
        }
        return sundays;
    }

}

