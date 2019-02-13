package com.widget.calendar.month;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.widget.calendar.library.R;
import com.widget.calendar.Bean.ReturnCalMonthBean;
import com.widget.calendar.Utils.CalendarUtils;
import com.widget.calendar.Utils.LunarCalendarUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Jimmy on 2016/10/6 0006.
 */
public class MonthView extends View {

    private static final int NUM_COLUMNS = 7;
    private static final int NUM_ROWS = 6;
    private Paint mPaint;
    private Paint mLunarPaint;
    private int mNormalDayColor;
    private int mSelectDayColor;
    private int mSelectBGColor;
    private int mCurrentDayColor;

    private int mHintCircleColor;
    private int mHolidayTextColor;
    private int mLunarTextColor;
    private int mLastOrNextMonthTextColor;
    private int mCurrYear, mCurrMonth, mCurrDay;
    private int mSelYear;
    private int mSelMonth;
    private int mSelDay;
    private int mColumnSize, mRowSize, mSelectCircleSize;
    private int mDaySize;
    private int mLunarTextSize;
    private int mWeekRow; // 当前月份第几周
    private int[][] mDaysText;
    private int[] mHolidays;
    private List<ReturnCalMonthBean> returnCalMonthBeans;
    private String[][] mHolidayOrLunarText;
    private boolean mIsShowLunar;
    private boolean mIsShowHint;
    private boolean mIsShowHolidayHint;
    private DisplayMetrics mDisplayMetrics;
    private OnMonthClickListener mDateClickListener;
    private GestureDetector mGestureDetector;
    private Bitmap mRestBitmap, mWorkBitmap,mHintTrueBitmap,mHintFalseBitmap;

    public MonthView(Context context, int year, int month) {
        this(context, null, year, month);
    }

    public MonthView(Context context, TypedArray array, int year, int month) {
        this(context, array, null, year, month);
    }

    public MonthView(Context context, TypedArray array, AttributeSet attrs, int year, int month) {
        this(context, array, attrs, 0, year, month);
    }

    public MonthView(Context context, TypedArray array, AttributeSet attrs, int defStyleAttr, int year, int month) {
        super(context, attrs, defStyleAttr);
        initAttrs(array, year, month);
        initPaint();
        initMonth();
        initGestureDetector();
        //initTaskHint();
        returnCalMonthBeans = new ArrayList<>();
    }

//    private void initTaskHint() {
//        if (mIsShowHint) {
//            // 从数据库中获取圆点提示数据
//            ScheduleDao dao = ScheduleDao.getInstance(getContext());
//            CalendarUtils.getInstance(getContext()).addTaskHints(mSelYear, mSelMonth, dao.getTaskHintByMonth(mSelYear, mSelMonth));
//        }
//    }

    private void initGestureDetector() {
        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                //Log.e("test","onSingleTapUp");
                doClickAction((int) e.getX(), (int) e.getY());
                return true;
            }
        });
    }

    private void initAttrs(TypedArray array, int year, int month) {
        if (array != null) {
            mSelectDayColor = array.getColor(R.styleable.MonthCalendarView_month_selected_text_color, Color.parseColor("#FFFFFF"));
            mSelectBGColor = array.getColor(R.styleable.MonthCalendarView_month_selected_circle_color, Color.parseColor("#E8E8E8"));
            mCurrentDayColor = array.getColor(R.styleable.MonthCalendarView_month_circle_today_color, Color.parseColor("#FF8594"));
            mNormalDayColor = array.getColor(R.styleable.MonthCalendarView_month_normal_text_color, Color.parseColor("#575471"));
            //mCurrentDayColor = array.getColor(R.styleable.MonthCalendarView_month_today_text_color, Color.parseColor("#4d7bfe"));
            mHintCircleColor = array.getColor(R.styleable.MonthCalendarView_month_hint_circle_color, Color.parseColor("#FE8595"));
            mLastOrNextMonthTextColor = array.getColor(R.styleable.MonthCalendarView_month_last_or_next_month_text_color, Color.parseColor("#ACA9BC"));
            mLunarTextColor = array.getColor(R.styleable.MonthCalendarView_month_lunar_text_color, Color.parseColor("#ACA9BC"));
            mHolidayTextColor = array.getColor(R.styleable.MonthCalendarView_month_holiday_color, Color.parseColor("#A68BFF"));
            mDaySize = array.getInteger(R.styleable.MonthCalendarView_month_day_text_size, 14);
            mLunarTextSize = array.getInteger(R.styleable.MonthCalendarView_month_day_lunar_text_size, 8);
            mIsShowHint = array.getBoolean(R.styleable.MonthCalendarView_month_show_task_hint, true);
            mIsShowLunar = array.getBoolean(R.styleable.MonthCalendarView_month_show_lunar, true);
            mIsShowHolidayHint = array.getBoolean(R.styleable.MonthCalendarView_month_show_holiday_hint, true);
        } else {
            mSelectDayColor = Color.parseColor("#FFFFFF");
            mSelectBGColor = Color.parseColor("#E8E8E8");
            mCurrentDayColor = Color.parseColor("#4d7bfe");
            mNormalDayColor = Color.parseColor("#575471");
            //mCurrentDayColor = Color.parseColor("#4d7bfe");
            mHintCircleColor = Color.parseColor("#FE8595");
            mLastOrNextMonthTextColor = Color.parseColor("#ACA9BC");
            mHolidayTextColor = Color.parseColor("#A68BFF");
            mDaySize = 14;
            mLunarTextSize = 8;
            mIsShowHint = true;
            mIsShowLunar = true;
            mIsShowHolidayHint = true;
        }
        mSelYear = year;
        mSelMonth = month;
//        mRestBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_rest_day);
//        mWorkBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_work_day);
        mHintTrueBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_red_dot_red);
        mHintFalseBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_red_dot_gray);
        mHolidays = CalendarUtils.getInstance(getContext()).getHolidays(mSelYear, mSelMonth + 1);
    }

    private void initPaint() {
        mDisplayMetrics = getResources().getDisplayMetrics();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(mDaySize * mDisplayMetrics.scaledDensity);

        mLunarPaint = new Paint();
        mLunarPaint.setAntiAlias(true);
        mLunarPaint.setTextSize(mLunarTextSize * mDisplayMetrics.scaledDensity);
        mLunarPaint.setColor(mLunarTextColor);
    }

    private void initMonth() {
        Calendar calendar = Calendar.getInstance();
        mCurrYear = calendar.get(Calendar.YEAR);
        mCurrMonth = calendar.get(Calendar.MONTH);
        mCurrDay = calendar.get(Calendar.DATE);
        if (mSelYear == mCurrYear && mSelMonth == mCurrMonth) {
            setSelectYearMonth(mSelYear, mSelMonth, mCurrDay);
        } else {
            setSelectYearMonth(mSelYear, mSelMonth, 1);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = mDisplayMetrics.densityDpi * 200;
        }
        if (widthMode == MeasureSpec.AT_MOST) {
            widthSize = mDisplayMetrics.densityDpi * 300;
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        initSize();
        clearData();
        drawLastMonth(canvas);
        int selected[] = drawThisMonth(canvas);
        drawNextMonth(canvas);
        drawHintCircle(canvas);
        drawLunarText(canvas, selected);
        //drawHoliday(canvas);
    }

    private void initSize() {
        mColumnSize = getWidth() / NUM_COLUMNS;
        mRowSize = getHeight() / NUM_ROWS;
        mSelectCircleSize = (int) (mColumnSize / 3.2);
        while (mSelectCircleSize > mRowSize / 2) {
            mSelectCircleSize = (int) (mSelectCircleSize / 1.3);
        }
    }

    private void clearData() {
        mDaysText = new int[6][7];
        mHolidayOrLunarText = new String[6][7];
    }

    private void drawLastMonth(Canvas canvas) {
        int lastYear, lastMonth;
        if (mSelMonth == 0) {
            lastYear = mSelYear - 1;
            lastMonth = 11;
        } else {
            lastYear = mSelYear;
            lastMonth = mSelMonth - 1;
        }
        mPaint.setColor(mLastOrNextMonthTextColor);
        int monthDays = CalendarUtils.getMonthDays(lastYear, lastMonth);
        int weekNumber = CalendarUtils.getFirstDayWeek(mSelYear, mSelMonth);
        for (int day = 0; day < weekNumber - 1; day++) {
            mDaysText[0][day] = monthDays - weekNumber + day + 2;
            String dayString = String.valueOf(mDaysText[0][day]);
            int startX = (int) (mColumnSize * day + (mColumnSize - mPaint.measureText(dayString)) / 2);
            int startY = (int) (mRowSize / 2 - (mPaint.ascent() + mPaint.descent()) / 2);
            canvas.drawText(dayString, startX, startY, mPaint);
            mHolidayOrLunarText[0][day] = CalendarUtils.getHolidayFromSolar(lastYear, lastMonth, mDaysText[0][day]);
        }
    }

    private int[] drawThisMonth(Canvas canvas) {
        String dayString;
        int selectedPoint[] = new int[2];
        int monthDays = CalendarUtils.getMonthDays(mSelYear, mSelMonth);
        int weekNumber = CalendarUtils.getFirstDayWeek(mSelYear, mSelMonth);
        ArrayList<String> sundays = CalendarUtils.getSundays(mSelYear,mSelMonth);
        //Log.e("test","getSundays :"+mSelYear+" "+mSelMonth+" "+weekNumber+" "+sundays.toString());
        for (int day = 0; day < monthDays; day++) {
            dayString = String.valueOf(day + 1);
            int col = (day + weekNumber - 1) % 7;
            int row = (day + weekNumber - 1) / 7;
            mDaysText[row][col] = day + 1;
            int startX = (int) (mColumnSize * col + (mColumnSize - mPaint.measureText(dayString)) / 2);
            int startY = (int) (mRowSize * row + mRowSize / 2 - (mPaint.ascent() + mPaint.descent()) / 2);
            if (dayString.equals(String.valueOf(mSelDay))) {
                int startRecX = mColumnSize * col;
                int startRecY = mRowSize * row;
                int endRecX = startRecX + mColumnSize;
                int endRecY = startRecY + mRowSize;
                if (mSelYear == mCurrYear && mCurrMonth == mSelMonth && day + 1 == mCurrDay) {
                    mPaint.setColor(mCurrentDayColor);
                } else {
                    mPaint.setColor(mSelectBGColor);
                }
                canvas.drawCircle((startRecX + endRecX) / 2, (startRecY + endRecY) / 2, mSelectCircleSize, mPaint);
                mWeekRow = row + 1;
            }
            if (dayString.equals(String.valueOf(mSelDay))) {
                selectedPoint[0] = row;
                selectedPoint[1] = col;
                mPaint.setColor(mSelectDayColor);
            } else if (dayString.equals(String.valueOf(mCurrDay)) && mCurrDay != mSelDay && mCurrMonth == mSelMonth && mCurrYear == mSelYear) {

                /*
                    设置今天的显示样式（当点击其他日期时）
                */
                //设置数字颜色(当遇到周日的日期时显示红色，其他为黑色)
                if(sundays.contains(dayString)){
                    mPaint.setColor(Color.parseColor("#ff4040"));
                }else {
                    mPaint.setColor(mNormalDayColor);
                }
                /*
                    画一个灰色背景
                */
                int startRecX = mColumnSize * col;
                int startRecY = mRowSize * row;
                int endRecX = startRecX + mColumnSize;
                int endRecY = startRecY + mRowSize;
                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setColor(Color.parseColor("#efefef"));
                canvas.drawCircle((startRecX + endRecX) / 2, (startRecY + endRecY) / 2, mSelectCircleSize, paint);
            } else {
                if(sundays.contains(dayString)){
                    mPaint.setColor(Color.parseColor("#ff4040"));
                }else {
                    mPaint.setColor(mNormalDayColor);
                }
            }

            canvas.drawText(dayString, startX, startY, mPaint);
            mHolidayOrLunarText[row][col] = CalendarUtils.getHolidayFromSolar(mSelYear, mSelMonth, mDaysText[row][col]);
        }

        return selectedPoint;
    }



    private void drawNextMonth(Canvas canvas) {
        mPaint.setColor(mLastOrNextMonthTextColor);
        int monthDays = CalendarUtils.getMonthDays(mSelYear, mSelMonth);
        int weekNumber = CalendarUtils.getFirstDayWeek(mSelYear, mSelMonth);
        int nextMonthDays = 42 - monthDays - weekNumber + 1;
        int nextMonth = mSelMonth + 1;
        int nextYear = mSelYear;
        if (nextMonth == 12) {
            nextMonth = 0;
            nextYear += 1;
        }
        for (int day = 0; day < nextMonthDays; day++) {
            int column = (monthDays + weekNumber - 1 + day) % 7;
            int row = 5 - (nextMonthDays - day - 1) / 7;
            try {
                mDaysText[row][column] = day + 1;
                mHolidayOrLunarText[row][column] = CalendarUtils.getHolidayFromSolar(nextYear, nextMonth, mDaysText[row][column]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String dayString = String.valueOf(mDaysText[row][column]);
            int startX = (int) (mColumnSize * column + (mColumnSize - mPaint.measureText(dayString)) / 2);
            int startY = (int) (mRowSize * row + mRowSize / 2 - (mPaint.ascent() + mPaint.descent()) / 2);
            canvas.drawText(dayString, startX, startY, mPaint);
        }
    }

    /**
     * 绘制农历
     *
     * @param canvas
     * @param selected
     */
    private void drawLunarText(Canvas canvas, int[] selected) {
        if (mIsShowLunar) {
            int firstYear, firstMonth, firstDay, monthDays;
            int weekNumber = CalendarUtils.getFirstDayWeek(mSelYear, mSelMonth);
            if (weekNumber == 1) {
                firstYear = mSelYear;
                firstMonth = mSelMonth + 1;
                firstDay = 1;
                monthDays = CalendarUtils.getMonthDays(firstYear, firstMonth);
            } else {
                if (mSelMonth == 0) {
                    firstYear = mSelYear - 1;
                    firstMonth = 11;
                    monthDays = CalendarUtils.getMonthDays(firstYear, firstMonth);
                    firstMonth = 12;
                } else {
                    firstYear = mSelYear;
                    firstMonth = mSelMonth - 1;
                    monthDays = CalendarUtils.getMonthDays(firstYear, firstMonth);
                    firstMonth = mSelMonth;
                }
                firstDay = monthDays - weekNumber + 2;
            }
            LunarCalendarUtils.Lunar lunar = LunarCalendarUtils.solarToLunar(new LunarCalendarUtils.Solar(firstYear, firstMonth, firstDay));
            int days;
            int day = lunar.lunarDay;
            int leapMonth = LunarCalendarUtils.leapMonth(lunar.lunarYear);
            days = LunarCalendarUtils.daysInMonth(lunar.lunarYear, lunar.lunarMonth, lunar.isLeap);
            boolean isChangeMonth = false;
            for (int i = 0; i < 42; i++) {
                int column = i % 7;
                int row = i / 7;
                if (day > days) {
                    day = 1;
                    boolean isAdd = true;
                    if (lunar.lunarMonth == 12) {
                        lunar.lunarMonth = 1;
                        lunar.lunarYear = lunar.lunarYear + 1;
                        isAdd = false;
                    }
                    if (lunar.lunarMonth == leapMonth) {
                        days = LunarCalendarUtils.daysInMonth(lunar.lunarYear, lunar.lunarMonth, lunar.isLeap);
                    } else {
                        if (isAdd) {
                            lunar.lunarMonth++;
                            days = LunarCalendarUtils.daysInLunarMonth(lunar.lunarYear, lunar.lunarMonth);
                        }
                    }
                }
                if (firstDay > monthDays) {
                    firstDay = 1;
                    isChangeMonth = true;
                }
                if (row == 0 && mDaysText[row][column] >= 23 || row >= 4 && mDaysText[row][column] <= 14) {
                    mLunarPaint.setColor(mLunarTextColor);
                } else {
                    mLunarPaint.setColor(mHolidayTextColor);
                }
                String dayString = mHolidayOrLunarText[row][column];
                if ("".equals(dayString)) {
                    dayString = LunarCalendarUtils.getLunarHoliday(lunar.lunarYear, lunar.lunarMonth, day);
                }
                if ("".equals(dayString)) {
                    dayString = LunarCalendarUtils.getLunarDayString(day);
                    mLunarPaint.setColor(mLunarTextColor);
                }
                if ("初一".equals(dayString)) {
                    int curYear = firstYear, curMonth = firstMonth;
                    if (isChangeMonth) {
                        curMonth++;
                        if (curMonth == 13) {
                            curMonth = 1;
                            curYear++;
                        }
                    }
                    LunarCalendarUtils.Lunar chuyi = LunarCalendarUtils.solarToLunar(new LunarCalendarUtils.Solar(curYear, curMonth, firstDay));
                    dayString = LunarCalendarUtils.getLunarFirstDayString(chuyi.lunarMonth, chuyi.isLeap);
                }
                if (selected[0] == row && selected[1] == column) {
                    mLunarPaint.setColor(mSelectDayColor);
                }
                int startX = (int) (mColumnSize * column + (mColumnSize - mLunarPaint.measureText(dayString)) / 2);
                int startY = (int) (mRowSize * row + mRowSize * 0.72 - (mLunarPaint.ascent() + mLunarPaint.descent()) / 2);
                canvas.drawText(dayString, startX, startY, mLunarPaint);
                day++;
                firstDay++;
            }
        }
    }

//    private void drawHoliday(Canvas canvas) {
//        if (mIsShowHolidayHint) {
//            Rect rect = new Rect(0, 0, mRestBitmap.getWidth(), mRestBitmap.getHeight());
//            Rect rectF = new Rect();
//            int distance = (int) (mSelectCircleSize / 2.5);
//            for (int i = 0; i < mHolidays.length; i++) {
//                int column = i % 7;
//                int row = i / 7;
//                rectF.set(mColumnSize * (column + 1) - mRestBitmap.getWidth() - distance, mRowSize * row + distance, mColumnSize * (column + 1) - distance, mRowSize * row + mRestBitmap.getHeight() + distance);
//                if (mHolidays[i] == 1) {
//                    canvas.drawBitmap(mRestBitmap, rect, rectF, null);
//                } else if (mHolidays[i] == 2) {
//                    canvas.drawBitmap(mWorkBitmap, rect, rectF, null);
//                }
//            }
//        }
//    }



    /**
     * 绘制圆点提示
     *
     * @param canvas
     */
    private void drawHintCircle(Canvas canvas) {
        if (mIsShowHint) {
            //Log.e("test","收到了数据："+hintTextBean.toString());
            int distance = (int) (mSelectCircleSize / 2.5);
            Calendar calendar = Calendar.getInstance();
            if(returnCalMonthBeans!=null){
                for(int index = 0;index<returnCalMonthBeans.size();index++){
                    long time = returnCalMonthBeans.get(index).getAddTime();
                    calendar.setTime(new Date(time));
                    int num = returnCalMonthBeans.get(index).getCount();
                    //Log.e("test",calendar.get(calendar.YEAR)+" "+calendar.get(calendar.MONTH)+" "+calendar.get(calendar.DATE)+" "+num);
                    //Log.e("test",mSelYear+" "+mSelMonth+" "+mSelDay);
                    if(mSelYear == calendar.get(calendar.YEAR) && mSelMonth == calendar.get(calendar.MONTH)){
                        int firstDayWeek = CalendarUtils.getFirstDayWeek(mSelYear,mSelMonth);
                        int[] position = dateToPosition(firstDayWeek,calendar.get(calendar.DATE));
                        //Log.e("test",position[0]+" "+position[1]+" "+firstDayWeek)

                        int column = position[0];
                        int row = position[1];
                        int shift = 13;

                        Rect rect = new Rect(0, 0, mHintTrueBitmap.getWidth(), mHintTrueBitmap.getHeight());
                        Rect rectF = new Rect();

                        rectF.set(mColumnSize * (column + 1) - mHintTrueBitmap.getWidth() - distance + shift-2, mRowSize * row + distance - shift-3 , mColumnSize * (column + 1) - distance + shift -2, mRowSize * row + mHintTrueBitmap.getHeight() + distance - shift-3);
                        //根据有无回款绘制不同背景的图
                        if(returnCalMonthBeans.get(index).getStatus()==2) {
                            canvas.drawBitmap(mHintFalseBitmap, rect, rectF, null);
                        }else{
                            canvas.drawBitmap(mHintTrueBitmap, rect, rectF, null);
                        }


                        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                        paint.setColor(Color.parseColor("#ffffff"));
                        paint.setTextSize(12* mDisplayMetrics.scaledDensity);

//                        //做字体显示适配
//                        if(num<10) {
//                            canvas.drawText(num + "", (rectF.left + mHintTrueBitmap.getWidth() / 4 + 2), rectF.top + mHintTrueBitmap.getHeight() - mHintTrueBitmap.getWidth() / 4, paint);
//                        }else {
//                            paint.setTextSize(10* mDisplayMetrics.scaledDensity);
//                            canvas.drawText(num + "", (rectF.left + mHintTrueBitmap.getWidth() / 4 - 5), rectF.top + mHintTrueBitmap.getHeight() - mHintTrueBitmap.getWidth() / 4, paint);
//                        }

                        //做字体显示适配
                        if (num < 10) {
                            canvas.drawText(num + "", (rectF.left + mHintTrueBitmap.getWidth() / 4 ), rectF.top + mHintTrueBitmap.getHeight() - mHintTrueBitmap.getWidth() / 4-1, paint);
                        } else {
                            paint.setTextSize(10 * mDisplayMetrics.scaledDensity);
                            canvas.drawText(num + "", (rectF.left + mHintTrueBitmap.getWidth() / 4 - 8), rectF.top + mHintTrueBitmap.getHeight() - mHintTrueBitmap.getWidth() / 4-3, paint);
                        }

                    }
                }
            }
        }


//            List<Integer> hints = CalendarUtils.getInstance(getContext()).getTaskHints(mSelYear, mSelMonth);
//            if (hints.size() > 0) {
//                mPaint.setColor(mHintCircleColor);
//                int monthDays = CalendarUtils.getMonthDays(mSelYear, mSelMonth);
//                int weekNumber = CalendarUtils.getFirstDayWeek(mSelYear, mSelMonth);
//                for (int day = 0; day < monthDays; day++) {
//                    int col = (day + weekNumber - 1) % 7;
//                    int row = (day + weekNumber - 1) / 7;
//                    if (!hints.contains(day + 1)) continue;
//                    float circleX = (float) (mColumnSize * col + mColumnSize * 0.5);
//                    float circleY = (float) (mRowSize * row + mRowSize * 0.75);
//                    canvas.drawCircle(circleX, circleY, mCircleRadius, mPaint);
//                }
//            }

    }


    /**
     * 将日期转换成该日期对应所在的行列 {0,0}为起始
     * 算法
     *
     * @param firstDayWeek 该月1号的位置(星期几 DAY_OF_WEEK)    （日 一 二 三 四 五 六） 对应的firstDayWeek （1 2 3 4 5 6 7）
     * @param day 具体天（该月几号）
     */
    private int[] dateToPosition(int firstDayWeek,int day){
        //纵向
        int a = 7- firstDayWeek+1;
        int[] position = {0,0};
        if(day % 7==0){
            if (firstDayWeek == 1) {
                position[1] = day/7-1;
            }else {
                position[1] = day/7;
            }
        }else if(day % 7 <= a ){
            position[1] = day/7;
        }else {
            position[1] = day/7+1;
        }
        //横向
        position[0] = (day - a)%7-1;
        if(position[0]<0){
            position[0]+=7;
        }

        return position;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    public void setSelectYearMonth(int year, int month, int day) {
        mSelYear = year;
        mSelMonth = month;
        mSelDay = day;
    }

    private void doClickAction(int x, int y) {
        if (y > getHeight())
            return;
        int row = y / mRowSize;
        int column = x / mColumnSize;
        column = Math.min(column, 6);
        int clickYear = mSelYear, clickMonth = mSelMonth;
        if (row == 0) {
            if (mDaysText[row][column] >= 23) {
                if (mSelMonth == 0) {
                    clickYear = mSelYear - 1;
                    clickMonth = 11;
                } else {
                    clickYear = mSelYear;
                    clickMonth = mSelMonth - 1;
                }
                if (mDateClickListener != null) {
                    mDateClickListener.onClickLastMonth(clickYear, clickMonth, mDaysText[row][column]);
                }
            } else {
                //Log.e("test","MonthView_doClickAction "+"row == 0");
                clickThisMonth(clickYear, clickMonth, mDaysText[row][column],0);
            }
        } else {
            int monthDays = CalendarUtils.getMonthDays(mSelYear, mSelMonth);
            int weekNumber = CalendarUtils.getFirstDayWeek(mSelYear, mSelMonth);
            int nextMonthDays = 42 - monthDays - weekNumber + 1;
            if (mDaysText[row][column] <= nextMonthDays && row >= 4) {
                if (mSelMonth == 11) {
                    clickYear = mSelYear + 1;
                    clickMonth = 0;
                } else {
                    clickYear = mSelYear;
                    clickMonth = mSelMonth + 1;
                }
                if (mDateClickListener != null) {
                    mDateClickListener.onClickNextMonth(clickYear, clickMonth, mDaysText[row][column]);
                }
            } else {
                //Log.e("test","MonthView_doClickAction "+"row != 0");
                clickThisMonth(clickYear, clickMonth, mDaysText[row][column],0);
            }
        }
    }

    /**
     * 跳转到某日期
     *
     * @param year
     * @param month
     * @param day
     */
    public void clickThisMonth(int year, int month, int day,int type) {
        //Log.e("test_0000","MonthView_clickThisMonth "+year+" "+month+" "+day);
        if (mDateClickListener != null) {
            //Log.e("test_9999","MonthView_clickThisMonth "+year+" "+month+" "+day);
            mDateClickListener.onClickThisMonth(year, month, day,type);
        }
        setSelectYearMonth(year, month, day);
        invalidate();
        //Log.e("test_4444","MonthView_clickThisMonth "+year+" "+month+" "+day);
    }

    /**
     * 获取当前选择年
     *
     * @return
     */
    public int getSelectYear() {
        return mSelYear;
    }

    /**
     * 获取当前选择月
     *
     * @return
     */
    public int getSelectMonth() {
        return mSelMonth;
    }

    /**
     * 获取当前选择日
     *
     * @return
     */
    public int getSelectDay() {
        return this.mSelDay;
    }

    public int getRowSize() {
        return mRowSize;
    }

    public int getWeekRow() {
        return mWeekRow;
    }

/*    *//**
     * 添加多个圆点提示
     *
     * @param hints
     *//*
    public void addTaskHints(List<Integer> hints) {
        if (mIsShowHint) {
            CalendarUtils.getInstance(getContext()).addTaskHints(mSelYear, mSelMonth, hints);
            invalidate();
        }
    }

    *//**
     * 删除多个圆点提示
     *
     * @param hints
     *//*
    public void removeTaskHints(List<Integer> hints) {
        if (mIsShowHint) {
            CalendarUtils.getInstance(getContext()).removeTaskHints(mSelYear, mSelMonth, hints);
            invalidate();
        }
    }

    *//**
     * 添加一个圆点提示
     *
     * @param day
     *//*
    public boolean addTaskHint(Integer day) {
        if (mIsShowHint) {
            if (CalendarUtils.getInstance(getContext()).addTaskHint(mSelYear, mSelMonth, day)) {
                invalidate();
                return true;
            }
        }
        return false;
    }

    *//**
     * 删除一个圆点提示
     *
     * @param day
     *//*
    public boolean removeTaskHint(Integer day) {
        if (mIsShowHint) {
            if (CalendarUtils.getInstance(getContext()).removeTaskHint(mSelYear, mSelMonth, day)) {
                invalidate();
                return true;
            }
        }
        return false;
    }*/

    /**
     * 设置点击日期监听
     *
     * @param dateClickListener
     */
    public void setOnDateClickListener(OnMonthClickListener dateClickListener) {
        this.mDateClickListener = dateClickListener;
    }



    public void setReturnCalMonthBeanList(List<ReturnCalMonthBean> returnCalMonthBeans) {
        this.returnCalMonthBeans = returnCalMonthBeans;
    }

    public void setmSelDay(int mSelDay) {
        this.mSelDay = mSelDay;
    }

}

