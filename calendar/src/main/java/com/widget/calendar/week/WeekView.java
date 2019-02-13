package com.widget.calendar.week;

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

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Jimmy on 2016/10/7 0007.
 */
public class WeekView extends View {

    private static final int NUM_COLUMNS = 7;
    private Paint mPaint;
    private Paint mLunarPaint;
    private int mNormalDayColor;
    private int mSelectDayColor;
    private int mSelectBGColor;
    private int mSelectBGTodayColor;
    private int mCurrentDayColor;
    private int mHintCircleColor;
    private int mLunarTextColor;
    private int mHolidayTextColor;
    private int mCurrYear, mCurrMonth, mCurrDay;
    private int mSelYear, mSelMonth, mSelDay;
    private int mColumnSize, mRowSize, mSelectCircleSize;
    private int mDaySize;
    private int mLunarTextSize;
    private int mCircleRadius = 6;
    private int[] mHolidays;
    private String mHolidayOrLunarText[];
    private boolean mIsShowLunar;
    private boolean mIsShowHint;
    private boolean mIsShowHolidayHint;
    private DateTime mStartDate;
    private DisplayMetrics mDisplayMetrics;
    private OnWeekClickListener mOnWeekClickListener;
    private GestureDetector mGestureDetector;
    private Bitmap mRestBitmap, mWorkBitmap,mHintTrueBitmap,mHintFalseBitmap;
    private List<ReturnCalMonthBean> returnCalMonthBeans;


    public WeekView(Context context, DateTime dateTime) {
        this(context, null, dateTime);
    }

    public WeekView(Context context, TypedArray array, DateTime dateTime) {
        this(context, array, null, dateTime);
    }

    public WeekView(Context context, TypedArray array, AttributeSet attrs, DateTime dateTime) {
        this(context, array, attrs, 0, dateTime);
    }

    public WeekView(Context context, TypedArray array, AttributeSet attrs, int defStyleAttr, DateTime dateTime) {
        super(context, attrs, defStyleAttr);
        initAttrs(array, dateTime);
        initPaint();
        initWeek();
        initGestureDetector();
        returnCalMonthBeans = new ArrayList<>();
    }

//    private void initTaskHint(DateTime date) {
//        if (mIsShowHint) {
//            // 从数据库中获取圆点提示数据
//            ScheduleDao dao = ScheduleDao.getInstance(getContext());
//            if (CalendarUtils.getInstance(getContext()).getTaskHints(date.getYear(), date.getMonthOfYear() - 1).size() == 0)
//                CalendarUtils.getInstance(getContext()).addTaskHints(date.getYear(), date.getMonthOfYear() - 1, dao.getTaskHintByMonth(mSelYear, mSelMonth));
//        }
//    }

    private void initAttrs(TypedArray array, DateTime dateTime) {
        if (array != null) {
            mSelectDayColor = array.getColor(R.styleable.WeekCalendarView_week_selected_text_color, Color.parseColor("#FFFFFF"));
            mSelectBGColor = array.getColor(R.styleable.WeekCalendarView_week_selected_circle_color, Color.parseColor("#E8E8E8"));
            mSelectBGTodayColor = array.getColor(R.styleable.WeekCalendarView_week_circle_today_color, Color.parseColor("#FF8594"));
            mNormalDayColor = array.getColor(R.styleable.WeekCalendarView_week_normal_text_color, Color.parseColor("#575471"));
            mCurrentDayColor = array.getColor(R.styleable.WeekCalendarView_week_today_text_color, Color.parseColor("#FF8594"));
            mHintCircleColor = array.getColor(R.styleable.WeekCalendarView_week_hint_circle_color, Color.parseColor("#FE8595"));
            mLunarTextColor = array.getColor(R.styleable.WeekCalendarView_week_lunar_text_color, Color.parseColor("#ACA9BC"));
            mHolidayTextColor = array.getColor(R.styleable.WeekCalendarView_week_holiday_color, Color.parseColor("#A68BFF"));
            mDaySize = array.getInteger(R.styleable.WeekCalendarView_week_day_text_size, 13);
            mLunarTextSize = array.getInteger(R.styleable.WeekCalendarView_week_day_lunar_text_size, 8);
            mIsShowHint = array.getBoolean(R.styleable.WeekCalendarView_week_show_task_hint, true);
            mIsShowLunar = array.getBoolean(R.styleable.WeekCalendarView_week_show_lunar, false);
            mIsShowHolidayHint = array.getBoolean(R.styleable.WeekCalendarView_week_show_holiday_hint, false);
        } else {
            mSelectDayColor = Color.parseColor("#FFFFFF");
            mSelectBGColor = Color.parseColor("#E8E8E8");
            mSelectBGTodayColor = Color.parseColor("#FF8594");
            mNormalDayColor = Color.parseColor("#575471");
            mCurrentDayColor = Color.parseColor("#FF8594");
            mHintCircleColor = Color.parseColor("#FE8595");
            mLunarTextColor = Color.parseColor("#ACA9BC");
            mHolidayTextColor = Color.parseColor("#A68BFF");
            mDaySize = 13;
            mDaySize = 8;
            mIsShowHint = true;
            mIsShowLunar = false;
            mIsShowHolidayHint = false;
        }
        mStartDate = dateTime;
//        mRestBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_rest_day);
//        mWorkBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_work_day);
        mHintTrueBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_red_dot_red);
        mHintFalseBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_red_dot_gray);

        int holidays[] = CalendarUtils.getInstance(getContext()).getHolidays(mStartDate.getYear(), mStartDate.getMonthOfYear());
        int row = CalendarUtils.getWeekRow(mStartDate.getYear(), mStartDate.getMonthOfYear() - 1, mStartDate.getDayOfMonth());
        mHolidays = new int[7];
        System.arraycopy(holidays, row * 7, mHolidays, 0, mHolidays.length);
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

    private void initWeek() {
        Calendar calendar = Calendar.getInstance();
        mCurrYear = calendar.get(Calendar.YEAR);
        mCurrMonth = calendar.get(Calendar.MONTH);
        mCurrDay = calendar.get(Calendar.DATE);
        DateTime endDate = mStartDate.plusDays(7);
        if (mStartDate.getMillis() <= System.currentTimeMillis() && endDate.getMillis() > System.currentTimeMillis()) {
            if (mStartDate.getMonthOfYear() != endDate.getMonthOfYear()) {
                if (mCurrDay < mStartDate.getDayOfMonth()) {
                    if(mCurrMonth==0) {
                        setSelectYearMonth(endDate.getYear(), endDate.getMonthOfYear() - 1, mCurrDay);
                    }else{
                        setSelectYearMonth(mStartDate.getYear(), endDate.getMonthOfYear() - 1, mCurrDay);
                    }
                } else {
                    setSelectYearMonth(mStartDate.getYear(), mStartDate.getMonthOfYear() - 1, mCurrDay);
                }
            } else {
                setSelectYearMonth(mStartDate.getYear(), mStartDate.getMonthOfYear() - 1, mCurrDay);
            }
        } else {
            setSelectYearMonth(mStartDate.getYear(), mStartDate.getMonthOfYear() - 1, mStartDate.getDayOfMonth());
        }
        //initTaskHint(mStartDate);
        //initTaskHint(endDate);
    }

    private void initGestureDetector() {
        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                doClickAction((int) e.getX(), (int) e.getY());
                return true;
            }
        });
    }

    public void setSelectYearMonth(int year, int month, int day) {
        mSelYear = year;
        mSelMonth = month;
        mSelDay = day;
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
        int selected = drawThisWeek(canvas);
        drawLunarText(canvas, selected);
        drawHintCircle(canvas);
        //drawHoliday(canvas);
    }

    private void clearData() {
        mHolidayOrLunarText = new String[7];
    }

    private void initSize() {
        mColumnSize = getWidth() / NUM_COLUMNS;
        mRowSize = getHeight();
        mSelectCircleSize = (int) (mColumnSize / 3.2);
        while (mSelectCircleSize > mRowSize / 2) {
            mSelectCircleSize = (int) (mSelectCircleSize / 1.3);
        }
    }

//    private void drawHoliday(Canvas canvas) {
//        if (mIsShowHolidayHint) {
//            Rect rect = new Rect(0, 0, mRestBitmap.getWidth(), mRestBitmap.getHeight());
//            Rect rectF = new Rect();
//            int distance = (int) (mSelectCircleSize / 2.5);
//            for (int i = 0; i < mHolidays.length; i++) {
//                int column = i % 7;
//                rectF.set(mColumnSize * (column + 1) - mRestBitmap.getWidth() - distance, distance, mColumnSize * (column + 1) - distance, mRestBitmap.getHeight() + distance);
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
            Calendar calendar = Calendar.getInstance();
            int distance = (int) (mSelectCircleSize / 2.5);
            if(returnCalMonthBeans!=null){
                for(int index = 0;index<returnCalMonthBeans.size();index++){
                    long time = returnCalMonthBeans.get(index).getAddTime();
                    calendar.setTime(new Date(time));
                    int num = returnCalMonthBeans.get(index).getCount();

                    if(mSelYear == calendar.get(calendar.YEAR) && mSelMonth == calendar.get(calendar.MONTH)){
                        int firstDayWeek = CalendarUtils.getFirstDayWeek(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH));
                        int[] position = dateToPosition(firstDayWeek,mSelDay);
                        /*
                            获取选中的日期在当月对应一整行的所有日期（不包括上 下个月的日期）
                            比如20号 此行日期为18 ~ 24，20>(20-2-1)  20<(20+(7-2))
                        */
                        if(calendar.get(calendar.DATE)>(mSelDay-position[0]-1) && calendar.get(calendar.DATE)<(mSelDay+(7-position[0]))) {
                            //Log.e("test", "mSelYear mSelMonth mSelDay-----------------" + mSelYear + " " + mSelMonth + " " + mSelDay);
                            //Log.e("test", "position[0] position[1] firstDayWeek-------" + position[0] + " " + position[1] + " " + firstDayWeek);
                            //Log.e("test", "----------------------------");
                            int column = dateToPosition(firstDayWeek,calendar.get(calendar.DATE))[0];
                            int shift = 8;
                            Rect rect = new Rect(0, 0, mHintTrueBitmap.getWidth(), mHintTrueBitmap.getHeight());
                            Rect rectF = new Rect();
                            rectF.set(mColumnSize * (column + 1) - mHintTrueBitmap.getWidth() - distance +shift + 7 , distance - shift-5, mColumnSize * (column + 1) - distance + shift + 7, mHintTrueBitmap.getHeight()+shift-5);
                            //rectF.set(mColumnSize * (column + 1) - mHintTrueBitmap.getWidth() - distance + shift, mRowSize * row + distance - shift, mColumnSize * (column + 1) - distance + shift, mRowSize * row + mHintTrueBitmap.getHeight() + distance - shift);

                            //根据有无回款绘制不同背景的图
                            if (returnCalMonthBeans.get(index).getStatus()==2) {
                                canvas.drawBitmap(mHintFalseBitmap, rect, rectF, null);
                            } else{
                                canvas.drawBitmap(mHintTrueBitmap, rect, rectF, null);
                            }

                            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                            paint.setColor(Color.parseColor("#ffffff"));
                            paint.setTextSize(12 * mDisplayMetrics.scaledDensity);

                            //做字体显示适配
                            if (num < 10) {
                                canvas.drawText(num + "", (rectF.left + mHintTrueBitmap.getWidth() / 4 ), rectF.top + mHintTrueBitmap.getHeight() - mHintTrueBitmap.getWidth() / 4, paint);
                            } else {
                                paint.setTextSize(10 * mDisplayMetrics.scaledDensity);
                                canvas.drawText(num + "", (rectF.left + mHintTrueBitmap.getWidth() / 4 - 8), rectF.top + mHintTrueBitmap.getHeight() - mHintTrueBitmap.getWidth() / 4-2, paint);
                            }
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

    //将日期转换成该日期对应所在的行列 {0,0}为起始
    private int[] dateToPosition(int firstDayWeek,int day){
        //纵向
        int a = 7- firstDayWeek+1;
        int[] position = {0,0};
//        if((day % 7 < a )||(day % 7 == a)){
//            position[1] = day/7;
//        }else {
//            position[1] = day/7+1;
//        }
        //横向
        position[0] = (day - a)%7-1;
        if(position[0]<0){
            position[0]+=7;
        }

        return position;
    }


    private int drawThisWeek(Canvas canvas) {
        int selected = 0;
        for (int i = 0; i < 7; i++) {
            DateTime date = mStartDate.plusDays(i);
            int day = date.getDayOfMonth();
            String dayString = String.valueOf(day);
            int startX = (int) (mColumnSize * i + (mColumnSize - mPaint.measureText(dayString)) / 2);
            int startY = (int) (mRowSize / 2 - (mPaint.ascent() + mPaint.descent()) / 2);
            //Log.e("test","-------------"+date.getYear()+" "+date.getMonthOfYear()+" "+day);
            //Log.e("test","-------------"+mCurrYear+" "+mCurrMonth+" "+mCurrDay);
            //Log.e("test","-------------"+mSelYear+" "+mSelMonth+" "+mSelDay);
            if(date.getMonthOfYear() - 1 == mSelMonth) {
                if (day == mSelDay) {
                    int startRecX = mColumnSize * i;
                    int endRecX = startRecX + mColumnSize;
                    if (date.getYear() == mCurrYear && date.getMonthOfYear() - 1 == mCurrMonth && day == mCurrDay) {
                        mPaint.setColor(mSelectBGTodayColor);
                    } else {
                        mPaint.setColor(mSelectBGColor);
                    }
                    canvas.drawCircle((startRecX + endRecX) / 2, mRowSize / 2, mSelectCircleSize, mPaint);
                    selected = i;
                    mPaint.setColor(mSelectDayColor);
                } else if (date.getYear() == mCurrYear && date.getMonthOfYear() - 1 == mCurrMonth && day == mCurrDay) {
                /*
                    设置今天的显示样式（当点击其他日期时）
                */
                    //设置数字颜色(当遇到周日的日期也就是第一个数字时显示红色，其他为黑色)
                    if (i == 0) {
                        mPaint.setColor(Color.parseColor("#ff4040"));
                    } else {
                        mPaint.setColor(mNormalDayColor);
                    }
                    //画一个灰色背景
                    int startRecX = mColumnSize * i;
                    int endRecX = startRecX + mColumnSize;
                    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    paint.setColor(Color.parseColor("#efefef"));
                    canvas.drawCircle((startRecX + endRecX) / 2, mRowSize / 2, mSelectCircleSize, paint);
                } else {
                    if (i == 0) {
                        mPaint.setColor(Color.parseColor("#ff4040"));
                    } else {
                        mPaint.setColor(mNormalDayColor);
                    }
                }
            }else {
                mPaint.setColor(Color.parseColor("#ACA9BC"));
            }
            canvas.drawText(dayString, startX, startY, mPaint);
            mHolidayOrLunarText[i] = CalendarUtils.getHolidayFromSolar(date.getYear(), date.getMonthOfYear() - 1, day);

        }
        return selected;
    }

    /**
     * 绘制农历
     *
     * @param canvas
     * @param selected
     */
    private void drawLunarText(Canvas canvas, int selected) {
        if (mIsShowLunar) {
            LunarCalendarUtils.Lunar lunar = LunarCalendarUtils.solarToLunar(new LunarCalendarUtils.Solar(mStartDate.getYear(), mStartDate.getMonthOfYear(), mStartDate.getDayOfMonth()));
            int leapMonth = LunarCalendarUtils.leapMonth(lunar.lunarYear);
            int days = LunarCalendarUtils.daysInMonth(lunar.lunarYear, lunar.lunarMonth, lunar.isLeap);
            int day = lunar.lunarDay;
            for (int i = 0; i < 7; i++) {
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
                mLunarPaint.setColor(mHolidayTextColor);
                String dayString = mHolidayOrLunarText[i];
                if ("".equals(dayString)) {
                    dayString = LunarCalendarUtils.getLunarHoliday(lunar.lunarYear, lunar.lunarMonth, day);
                }
                if ("".equals(dayString)) {
                    dayString = LunarCalendarUtils.getLunarDayString(day);
                    mLunarPaint.setColor(mLunarTextColor);
                }
                if ("初一".equals(dayString)) {
                    DateTime curDay = mStartDate.plusDays(i);
                    LunarCalendarUtils.Lunar chuyi = LunarCalendarUtils.solarToLunar(new LunarCalendarUtils.Solar(curDay.getYear(), curDay.getMonthOfYear(), curDay.getDayOfMonth()));
                    dayString = LunarCalendarUtils.getLunarFirstDayString(chuyi.lunarMonth, chuyi.isLeap);
                }
                if (i == selected) {
                    mLunarPaint.setColor(mSelectDayColor);
                }
                int startX = (int) (mColumnSize * i + (mColumnSize - mLunarPaint.measureText(dayString)) / 2);
                int startY = (int) (mRowSize * 0.72 - (mLunarPaint.ascent() + mLunarPaint.descent()) / 2);
                canvas.drawText(dayString, startX, startY, mLunarPaint);
                day++;
            }
        }
    }



//    /**
//     * 绘制圆点提示
//     *
//     * @param canvas
//     */
//    private void drawHintCircle(Canvas canvas) {
//        if (mIsShowHint) {
//            mPaint.setColor(mHintCircleColor);
//            int startMonth = mStartDate.getMonthOfYear();
//            int endMonth = mStartDate.plusDays(7).getMonthOfYear();
//            int startDay = mStartDate.getDayOfMonth();
//            if (startMonth == endMonth) {
//                List<Integer> hints = CalendarUtils.getInstance(getContext()).getTaskHints(mStartDate.getYear(), mStartDate.getMonthOfYear() - 1);
//                for (int i = 0; i < 7; i++) {
//                    drawHintCircle(hints, startDay + i, i, canvas);
//                }
//            } else {
//                for (int i = 0; i < 7; i++) {
//                    List<Integer> hints = CalendarUtils.getInstance(getContext()).getTaskHints(mStartDate.getYear(), mStartDate.getMonthOfYear() - 1);
//                    List<Integer> nextHints = CalendarUtils.getInstance(getContext()).getTaskHints(mStartDate.getYear(), mStartDate.getMonthOfYear());
//                    DateTime date = mStartDate.plusDays(i);
//                    int month = date.getMonthOfYear();
//                    if (month == startMonth) {
//                        drawHintCircle(hints, date.getDayOfMonth(), i, canvas);
//                    } else {
//                        drawHintCircle(nextHints, date.getDayOfMonth(), i, canvas);
//                    }
//                }
//            }
//        }
//    }

//    private void drawHintCircle(List<Integer> hints, int day, int col, Canvas canvas) {
//        if (!hints.contains(day)) return;
//        float circleX = (float) (mColumnSize * col + mColumnSize * 0.5);
//        float circleY = (float) (mRowSize * 0.75);
//        canvas.drawCircle(circleX, circleY, mCircleRadius, mPaint);
//    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    private void doClickAction(int x, int y) {
        if (y > getHeight())
            return;
        int column = x / mColumnSize;
        column = Math.min(column, 6);
        DateTime date = mStartDate.plusDays(column);

        clickThisWeek(date.getYear(), date.getMonthOfYear() - 1, date.getDayOfMonth(),0);
    }

    public void clickThisWeek(int year, int month, int day,int type) {
        if (mOnWeekClickListener != null) {
            mOnWeekClickListener.onClickDate(year, month, day,type);
        }
        setSelectYearMonth(year, month, day);
        invalidate();
    }

    public void setOnWeekClickListener(OnWeekClickListener onWeekClickListener) {
        mOnWeekClickListener = onWeekClickListener;
    }

    public DateTime getStartDate() {
        return mStartDate;
    }

    public DateTime getEndDate() {
        return mStartDate.plusDays(6);
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

   /* *//**
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
    public void addTaskHint(Integer day) {
        if (mIsShowHint) {
            if (CalendarUtils.getInstance(getContext()).addTaskHint(mSelYear, mSelMonth, day)) {
                invalidate();
            }
        }
    }

    *//**
     * 删除一个圆点提示
     *
     *//*
    public void removeTaskHint(Integer day) {
        if (mIsShowHint) {
            if (CalendarUtils.getInstance(getContext()).removeTaskHint(mSelYear, mSelMonth, day)) {
                invalidate();
            }
        }
    }*/

    public void setReturnCalMonthBeanList(List<ReturnCalMonthBean> returnCalMonthBeans) {
        this.returnCalMonthBeans = returnCalMonthBeans;
    };

}
