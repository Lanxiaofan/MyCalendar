package com.widget.calendar.month;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.widget.calendar.library.R;
import com.widget.calendar.Bean.ReturnCalMonthBean;
import com.widget.calendar.Utils.CalendarUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Jimmy on 2016/10/6 0006.
 */
public class MonthAdapter extends PagerAdapter {

    private SparseArray<MonthView> mViews;
    private Context mContext;
    private TypedArray mArray;
    private MonthCalendarView mMonthCalendarView;
    private int mMonthCount;
    private boolean tag=true; //monthView true  weekView false

    //private HintTextBean hintTextBean;
    private List<ReturnCalMonthBean> returnCalMonthBeans;


    public MonthAdapter(Context context, TypedArray array, MonthCalendarView monthCalendarView) {
        mContext = context;
        mArray = array;
        mMonthCalendarView = monthCalendarView;
        mViews = new SparseArray<>();
        mMonthCount = array.getInteger(R.styleable.MonthCalendarView_month_count, 48);
    }

    @Override
    public int getCount() {
        return mMonthCount;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //Log.e("test","------------instantiateItem");
        //zf
        if (mViews.get(position) == null) {
            int[] date = getYearAndMonth(position);
            //Log.e("test","MonthAdapter "+date[0]+" "+date[1]);
            MonthView monthView = new MonthView(mContext, mArray, date[0], date[1]);
            monthView.setId(position);
            monthView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            if(returnCalMonthBeans!=null){
                monthView.setReturnCalMonthBeanList(returnCalMonthBeans);
            }

            monthView.invalidate();
            monthView.setOnDateClickListener(mMonthCalendarView);
            mViews.put(position, monthView);
        }else {
            int[] date = getYearAndMonth(position);
            if(tag){
                    //monthView的滑动
                    mViews.get(position).setSelectYearMonth(date[0],date[1],1);
                    if(CalendarUtils.getCurrentDate()[0]==date[0] && CalendarUtils.getCurrentDate()[1]==(date[1]+1)){
                        mViews.get(position).setSelectYearMonth(date[0],date[1],CalendarUtils.getCurrentDate()[2]);
                    }
            }
            if(returnCalMonthBeans!=null){
                mViews.get(position).setReturnCalMonthBeanList(returnCalMonthBeans);
            }
            mViews.get(position).invalidate();
            mViews.get(position).setOnDateClickListener(mMonthCalendarView);

        }
        container.addView(mViews.get(position));
        return mViews.get(position);
    }



    public int[] getYearAndMonth(int position) {
        int date[] = new int[3];

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(mMonthCalendarView.getMiddleYear()+"/"+(mMonthCalendarView.getMiddleMonth()+1)+"/"+mMonthCalendarView.getMiddleDay()));
        calendar.add(Calendar.MONTH,position - mMonthCount / 2);
        date[0] = calendar.get(Calendar.YEAR);
        date[1] = calendar.get(Calendar.MONTH);
        date[2] = calendar.get(Calendar.DATE);
        return date;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public SparseArray<MonthView> getViews() {
        return mViews;
    }

    public int getMonthCount() {
        return mMonthCount;
    }

    public void setReturnCalMonthBeanList(List<ReturnCalMonthBean> returnCalMonthBeans,boolean tag) {
        this.returnCalMonthBeans = returnCalMonthBeans;
        this.tag = tag;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
