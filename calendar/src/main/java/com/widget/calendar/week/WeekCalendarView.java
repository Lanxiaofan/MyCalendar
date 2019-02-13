package com.widget.calendar.week;

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

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Jimmy on 2016/10/7 0007.
 */
public class WeekCalendarView extends ViewPager implements OnWeekClickListener {

    private OnCalendarClickListener mOnCalendarClickListener;
    private WeekAdapter mWeekAdapter;
    public WeekCalendarView(Context context) {
        this(context, null);
    }
    private int type=0;
    private Timer timer;
    private long time;

    public WeekCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        addOnPageChangeListener(mOnPageChangeListener);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        initWeekAdapter(context, context.obtainStyledAttributes(attrs, R.styleable.WeekCalendarView));
    }

    private void initWeekAdapter(Context context, TypedArray array) {
        mWeekAdapter = new WeekAdapter(context, array,this);
        setAdapter(mWeekAdapter);
        setCurrentItem(mWeekAdapter.getWeekCount() / 2, false);
    }

    @Override
    public void onClickDate(int year, int month, int day,int type) {
        if (mOnCalendarClickListener != null) {
            mOnCalendarClickListener.onClickDate(year, month, day,type);
        }
    }



    private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(final int position) {
            WeekView weekView = mWeekAdapter.getViews().get(position);
            //Log.e("hhhhhh",weekView.getSelectYear()+" "+weekView.getSelectMonth()+" "+weekView.getSelectDay());
            //final Intent intent = new Intent("com.calendar.week.broadcastReceiver");
            final Intent intent = new Intent();
            intent.putExtra("year",weekView.getSelectYear());
            intent.putExtra("month",weekView.getSelectMonth());
            intent.putExtra("day",weekView.getSelectDay());
            if (timer==null) {
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        //Log.e("test","MonthCalendarView "+"-------------时间到了咩");
                        //getContext().sendBroadcast(intent);
                        EventBus.getDefault().post(intent,"WeekCalendarView");
                        timer.cancel();
                    }
                }, 500);
            }else {
                timer.cancel();
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        //Log.e("test","MonthCalendarView "+"-------------时间到了咩2");
                        //getContext().sendBroadcast(intent);
                        EventBus.getDefault().post(intent,"WeekCalendarView");
                        timer.cancel();
                    }
                }, 500);
            }

            if (weekView != null) {
                if (mOnCalendarClickListener != null) {
                    mOnCalendarClickListener.onPageChange(weekView.getSelectYear(), weekView.getSelectMonth(), weekView.getSelectDay());
                }
                weekView.clickThisWeek(weekView.getSelectYear(), weekView.getSelectMonth(), weekView.getSelectDay(),1);
            } else {
                WeekCalendarView.this.postDelayed(new Runnable() {
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
     * 设置点击日期监听
     * @param onCalendarClickListener
     */
    public void setOnCalendarClickListener(OnCalendarClickListener onCalendarClickListener) {
        mOnCalendarClickListener = onCalendarClickListener;
    }

    public SparseArray<WeekView> getWeekViews() {
        return mWeekAdapter.getViews();
    }

    public WeekAdapter getWeekAdapter() {
        return mWeekAdapter;
    }

    public WeekView getCurrentWeekView() {
        return getWeekViews().get(getCurrentItem());
    }


    public void setReturnCalMonthBeanList(List<ReturnCalMonthBean> returnCalMonthBeans) {
        mWeekAdapter.setReturnCalMonthBeanList(returnCalMonthBeans);
        mWeekAdapter.notifyDataSetChanged();
    }


    public int getType() {
        return type;
    }

}
