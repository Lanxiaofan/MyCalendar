package com.widget.calendar.week;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.widget.calendar.library.R;
import com.widget.calendar.Bean.ReturnCalMonthBean;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by Jimmy on 2016/10/7 0007.
 */
public class WeekAdapter extends PagerAdapter {

    private SparseArray<WeekView> mViews;
    private Context mContext;
    private TypedArray mArray;
    private WeekCalendarView mWeekCalendarView;
    private DateTime mStartDate;
    private int mWeekCount = 220;
    private List<ReturnCalMonthBean> returnCalMonthBeans;


    public WeekAdapter(Context context, TypedArray array, WeekCalendarView weekCalendarView) {
        mContext = context;
        mArray = array;
        mWeekCalendarView = weekCalendarView;
        mViews = new SparseArray<>();
        initStartDate();
        mWeekCount = array.getInteger(R.styleable.WeekCalendarView_week_count, 220);
    }

    private void initStartDate() {
        mStartDate = new DateTime();
        mStartDate = mStartDate.plusDays(-mStartDate.getDayOfWeek() % 7);
    }

    @Override
    public int getCount() {
        return mWeekCount;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        for (int i = 0; i < 3; i++) {
            if(mViews.get(position - 2 + i) == null) {
                if (position - 2 + i >= 0 && position - 2 + i < mWeekCount) {
                    instanceWeekView(position - 2 + i);
                }
            }else {
                if(returnCalMonthBeans!=null){
                    mViews.get(position - 2 + i).setReturnCalMonthBeanList(returnCalMonthBeans);
                }
                mViews.get(position - 2 + i).invalidate();
            }
        }
        container.addView(mViews.get(position));
        return mViews.get(position);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public SparseArray<WeekView> getViews() {
        return mViews;
    }

    public int getWeekCount() {
        return mWeekCount;
    }

    public WeekView instanceWeekView(int position) {
        WeekView weekView = new WeekView(mContext, mArray, mStartDate.plusWeeks(position - mWeekCount / 2));
        weekView.setId(position);
        weekView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        weekView.setOnWeekClickListener(mWeekCalendarView);
        mViews.put(position, weekView);
        if(returnCalMonthBeans!=null){
            weekView.setReturnCalMonthBeanList(returnCalMonthBeans);
        }
        weekView.invalidate();
        return weekView;
    }

    public void setReturnCalMonthBeanList(List<ReturnCalMonthBean> returnCalMonthBeans) {
        this.returnCalMonthBeans = returnCalMonthBeans;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
