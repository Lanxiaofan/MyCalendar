package com.widget.calendar.month;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.SparseArray;

import com.widget.calendar.library.R;
import com.widget.calendar.Bean.ReturnCalMonthBean;
import com.widget.calendar.OnCalendarClickListener;

import org.simple.eventbus.EventBus;

import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Jimmy on 2016/10/6 0006.
 */
public class MonthCalendarView extends ViewPager implements OnMonthClickListener {

    private MonthAdapter mMonthAdapter;
    private OnCalendarClickListener mOnCalendarClickListener;
    private OnSelectedListener onSelectedListener;

    private int middleYear,middleMonth,middleDay;
    private Timer timer;
    private boolean isToSpecifyDate = false;
    private long time;

    public MonthCalendarView(Context context) {
        this(context, null);
    }

    public MonthCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        addOnPageChangeListener(mOnPageChangeListener);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        initMonthAdapter(context, context.obtainStyledAttributes(attrs, R.styleable.MonthCalendarView));
    }

    private void initMonthAdapter(Context context, TypedArray array) {
        mMonthAdapter = new MonthAdapter(context, array,this);

        setAdapter(mMonthAdapter);
        setCurrentItem(mMonthAdapter.getMonthCount() / 2, false);

        Calendar calendar = Calendar.getInstance();
        middleYear = calendar.get(Calendar.YEAR);
        middleMonth = calendar.get(Calendar.MONTH);
        middleDay = calendar.get(Calendar.DAY_OF_MONTH);

    }

    @Override
    public void onClickThisMonth(int year, int month, int day,int type) {
        //Log.e("test_1111","mcv_onClickThisMonth" + year + "=" + month + "=" + day);
        if (mOnCalendarClickListener != null) {
            mOnCalendarClickListener.onClickDate(year, month, day,type);
        }

    }

    @Override
    public void onClickLastMonth(int year, int month, int day) {
        MonthView monthDateView = mMonthAdapter.getViews().get(getCurrentItem() - 1);
        if (monthDateView != null) {
            monthDateView.setSelectYearMonth(year, month, 1);
        }
        setCurrentItem(getCurrentItem() - 1, true);
    }

    @Override
    public void onClickNextMonth(int year, int month, int day) {
        MonthView monthDateView = mMonthAdapter.getViews().get(getCurrentItem() + 1);
        if (monthDateView != null) {
            monthDateView.setSelectYearMonth(year, month, 1);
        }
        setCurrentItem(getCurrentItem() + 1, true);
    }

    private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(final int position) {
            MonthView monthView = mMonthAdapter.getViews().get(getCurrentItem());
            if (monthView != null) {
                //final Intent intent = new Intent("com.calendar.month.broadcastReceiver");
                final Intent intent = new Intent();
                intent.putExtra("year",monthView.getSelectYear());
                intent.putExtra("month",monthView.getSelectMonth());
                intent.putExtra("day",monthView.getSelectDay());
                if(!isToSpecifyDate) {
                    if (timer == null) {
                        timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                //onSelectedListener.onSelected();
                                //Log.e("test","MonthCalendarView "+"-------------时间到了咩");
                                //getContext().sendBroadcast(intent);
                                EventBus.getDefault().post(intent,"MonthCalendarView");
                               /* Log.e("emmmm1",intent.getIntExtra("year",2017)
                                        +"-"+intent.getIntExtra("month",0)
                                        +"-"+intent.getIntExtra("day",1));*/
                                timer.cancel();
                            }
                        }, 500);
                    } else {
                        timer.cancel();
                        timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                //onSelectedListener.onSelected();
                                //Log.e("test","MonthCalendarView "+"-------------时间到了咩2");
                                //getContext().sendBroadcast(intent);
                                /*Log.e("emmmm2",intent.getIntExtra("year",2017)
                                        +"-"+intent.getIntExtra("month",0)
                                        +"-"+intent.getIntExtra("day",1));*/
                                EventBus.getDefault().post(intent,"MonthCalendarView");
                                timer.cancel();
                            }
                        }, 500);
                    }
                }
                monthView.clickThisMonth(monthView.getSelectYear(), monthView.getSelectMonth(), monthView.getSelectDay(),1);
//                if(!isToSpecifyDate) {
//                    if(System.currentTimeMillis() - time > 700){
//                        onSelectedListener.onSelected(monthView.getSelectYear(), monthView.getSelectMonth(), monthView.getSelectDay());
//                    }
//                    time = System.currentTimeMillis();
//                }
                if (mOnCalendarClickListener != null && !isToSpecifyDate) {
                    mOnCalendarClickListener.onPageChange(monthView.getSelectYear(), monthView.getSelectMonth(), monthView.getSelectDay());
                }


/*                MonthView monthViewPrevious = mMonthAdapter.getViews().get(getCurrentItem()- 1);
                if(monthViewPrevious!=null && mMonthAdapter!=null) {
                    int[] date = mMonthAdapter.getYearAndMonth(getCurrentItem() - 1);
                    monthViewPrevious.setSelectYearMonth(date[0], date[1], 1);
                }

                MonthView monthViewNext = mMonthAdapter.getViews().get(getCurrentItem() + 1);
                if(monthViewNext!=null && mMonthAdapter!=null) {
                    int[] date = mMonthAdapter.getYearAndMonth(getCurrentItem() + 1);
                    monthViewNext.setSelectYearMonth(date[0], date[1], 1);
                }*/

            } else {
                MonthCalendarView.this.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onPageSelected(position);
                    }
                }, 50);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


    /**
     * 跳转到指定日期zf
     */
    public void toSpecifyDate(int year, int month, int day){
        isToSpecifyDate = true;     //判断是跳转还是普通滑动操作
        setCurrentItem(mMonthAdapter.getMonthCount() / 2 , true);
        MonthView monthView = mMonthAdapter.getViews().get(mMonthAdapter.getMonthCount() / 2);
        if (monthView != null) {
            middleYear = year;
            middleMonth = month;
            middleDay = day;
            if (mOnCalendarClickListener != null && isToSpecifyDate) {
                isToSpecifyDate = false;
                mOnCalendarClickListener.onPageChange(middleYear, middleMonth, middleDay);
            }

            monthView.clickThisMonth(year,month,day,2);
        }

        MonthView monthViewPrevious = mMonthAdapter.getViews().get(mMonthAdapter.getMonthCount() / 2 - 1);
        if(monthViewPrevious!=null && mMonthAdapter!=null) {
            int[] date = mMonthAdapter.getYearAndMonth(mMonthAdapter.getMonthCount() / 2 - 1);
            monthViewPrevious.setSelectYearMonth(date[0], date[1], 1);
        }

        MonthView monthViewNext = mMonthAdapter.getViews().get(mMonthAdapter.getMonthCount() / 2 + 1);
        if(monthViewNext!=null && mMonthAdapter!=null) {
            int[] date = mMonthAdapter.getYearAndMonth(mMonthAdapter.getMonthCount() / 2 + 1);
            monthViewNext.setSelectYearMonth(date[0], date[1], 1);
        }


    }


    /**
     * 设置点击日期监听
     *
     * @param onCalendarClickListener
     */
    public void setOnCalendarClickListener(OnCalendarClickListener onCalendarClickListener) {
        mOnCalendarClickListener = onCalendarClickListener;
    }

    public SparseArray<MonthView> getMonthViews() {
        return mMonthAdapter.getViews();
    }

    public MonthView getCurrentMonthView() {
        return getMonthViews().get(getCurrentItem());
    }

    public int getMiddleYear() {
        return middleYear;
    }

    public int getMiddleMonth() {
        return middleMonth;
    }

    public int getMiddleDay() {
        return middleDay;
    }

    public void setReturnCalMonthBeanList(List<ReturnCalMonthBean> returnCalMonthBeans,boolean tag) {
        mMonthAdapter.setReturnCalMonthBeanList(returnCalMonthBeans,tag);
        mMonthAdapter.notifyDataSetChanged();
    }

    public interface OnSelectedListener{
        void onSelected(int year, int month, int day);
    }

    public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
        this.onSelectedListener = onSelectedListener;
    }

}
