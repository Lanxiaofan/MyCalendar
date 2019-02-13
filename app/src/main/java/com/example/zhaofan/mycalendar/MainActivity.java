package com.example.zhaofan.mycalendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhaofan.mycalendar.Schedule.ScheduleAdapter;
import com.widget.calendar.Bean.HintTextBean;
import com.widget.calendar.Bean.ReturnCalDayBean;
import com.widget.calendar.Bean.ReturnCalMonthBean;
import com.widget.calendar.OnCalendarClickListener;
import com.widget.calendar.month.MonthCalendarView;
import com.widget.calendar.month.MonthView;
import com.widget.calendar.schedule.ScheduleLayout;
import com.widget.calendar.schedule.ScheduleRecyclerView;
import com.widget.calendar.week.WeekCalendarView;


import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.zhaofan.mycalendar.MyUtils.dp2px;

public class MainActivity extends AppCompatActivity implements OnCalendarClickListener {
    //private EditText et;
    private TextView tv_needRepayAmount;
    private TextView tv_getRepayAmount;

    private MonthCalendarView monthCalendarView;
    private WeekCalendarView weekCalendarView;
    private int[] cDate = getCurrentDate();
    private SpinnerPopWindow<String> mSpinnerPopWindow;
    private TextView tvValue;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private ScheduleLayout slSchedule;

    private LinearLayout ll_no_view;

    private List<String> months = Arrays.asList("1月","2月","3月","4月","5月","6月","7月","8月","9月","10月","11月","12月");
    private List<String> list;
    private int year_position = 0;//RecyclerView的位置

    private ScheduleRecyclerView scheduleRecyclerView;
    private ScheduleAdapter scheduleAdapter;
    private View calendar_view;
    private TextView tvSelectTime;
    private List<ReturnCalMonthBean> returnCalMonthList;
    private ReturnCalDayBean returnCalDayBean;
    private int type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        EventBus.getDefault().register(this);
        returnCalMonthList = new ArrayList<>();
        returnCalDayBean = new ReturnCalDayBean();
        initView();

        initCalendarMonth(slSchedule,cDate[0],cDate[1],true); //cDate[0] 对应年， cDate[1] 对应月  ，

        mSpinnerPopWindow = new SpinnerPopWindow<String>(this, list,itemClickListener);
        mSpinnerPopWindow.setOnDismissListener(dismissListener);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerViewAdapter = new RecyclerViewAdapter(months);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                //月份点击事件

                //Log.e("test","position="+position);
                //Log.e("test","year="+(2016 + year_position)+" month="+(position+1)+" day="+1);

                if(2017 + year_position == cDate[0] && position + 1 == cDate[1]){
                    monthCalendarView.toSpecifyDate(2017 + year_position, position, cDate[2]);
                }else {
                    monthCalendarView.toSpecifyDate(2017 + year_position,position,1);
                }

            }
        });


        //日历初始化显示当前年月
        year_position = cDate[0] - 2017;
        tvValue.setText(cDate[0]+"年");
        recyclerViewAdapter.setCurrentMonth(cDate[1]-1);
        recyclerViewAdapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(cDate[1]-1); //滑动到当前月份
        initScheduleList();
    }

    private void initView(){
        tv_needRepayAmount = (TextView) findViewById(R.id.tv_needRepayAmount);
        tv_getRepayAmount  = (TextView) findViewById(R.id.tv_getRepayAmount);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        tvValue = (TextView) findViewById(R.id.tv_value);
        tvValue.setOnClickListener(clickListener);

        calendar_view = findViewById(R.id.calendar_view);
        slSchedule = (ScheduleLayout) calendar_view.findViewById(R.id.slSchedule);
        slSchedule.setOnCalendarClickListener(this);
        monthCalendarView = (MonthCalendarView) calendar_view.findViewById(R.id.mcvCalendar);
        weekCalendarView = (WeekCalendarView) calendar_view.findViewById(R.id.wcvCalendar);

        tvSelectTime = (TextView) findViewById(R.id.tv_time);
        ll_no_view = (LinearLayout)  findViewById(R.id.ll_no_view);

        list= new ArrayList<String>();
        for(int i = 2017;i<=cDate[0]+1;i++){
            list.add(i+"");
        }
    }

    private void initScheduleList() {
        scheduleRecyclerView = slSchedule.getSchedulerRecyclerView();
        LinearLayoutManager manager = new LinearLayoutManager(MainActivity.this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        scheduleRecyclerView.setLayoutManager(manager);
        DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setSupportsChangeAnimations(false);
        scheduleRecyclerView.setItemAnimator(itemAnimator);
        initCalendarDay(cDate[0],cDate[1],cDate[2]);
        scheduleAdapter = new ScheduleAdapter(MainActivity.this, returnCalDayBean.getDayPayList());
        scheduleRecyclerView.setAdapter(scheduleAdapter);
    }

    //MonthCalendarView传递过来的值
    @Subscriber(tag = "MonthCalendarView")
    public void MonthCalendarViewData(Intent intent) {
        //Log.e("test","mMonthViewReceiver "+"-------------时间到了咩hhhhhhhh");
        int year = intent.getIntExtra("year", 2017);
        int month = intent.getIntExtra("month", 0);
        int day = intent.getIntExtra("day", 1);
        if(monthCalendarView.getVisibility()==View.VISIBLE) {
            initCalendarMonth(slSchedule, year, month+1, true);
            if (year == cDate[0] && month+1 == cDate[1]) {
                //如果是当月则请求今日的接口
                initCalendarDay(cDate[0], cDate[1], cDate[2]);
            } else {
                //如果是其他月则请求1号的接口
                initCalendarDay(year, month+1, 1);
            }
            recyclerView.smoothScrollToPosition(month); //滑动到当前月份
        }
    }

    //WeekCalendarView
    @Subscriber(tag = "WeekCalendarView")
    public void WeekCalendarViewData(Intent intent) {
        //Log.e("test","mWeekViewReceiver "+"-------------时间到了咩hhhhhhhh");
        if(weekCalendarView.getVisibility()==View.VISIBLE) {
            if (type != 2) {
                int year = intent.getIntExtra("year", 2017);
                int month = intent.getIntExtra("month", 0);
                int day = intent.getIntExtra("day", 1);
                //Log.e("test", "mWeekViewReceiver:" + year + " " + month + " " + day);

                initCalendarMonth(slSchedule, year, month + 1, false);
                initCalendarDay(year, month + 1, day);

                recyclerView.smoothScrollToPosition(month); //滑动到当前月份
            }
        }
    }


    /**
     * popupwindow(年份下拉框)显示的ListView的item点击事件
     */
    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
            mSpinnerPopWindow.dismiss();
            tvValue.setText(list.get(position)+"年");
            year_position = position;

            if(2017+year_position==cDate[0] && 1==cDate[1]) {
                monthCalendarView.toSpecifyDate(2017 + year_position, 0, cDate[2]);
            }else {
                monthCalendarView.toSpecifyDate(2017 + year_position, 0, 1);
            }
            recyclerView.smoothScrollToPosition(0); //滑动到当前月份
        }
    };


    /**
     * 显示PopupWindow
     */
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_value:
                    mSpinnerPopWindow.setWidth(tvValue.getWidth() + dp2px(getApplicationContext(),17));
                    mSpinnerPopWindow.showAsDropDown(tvValue,-dp2px(getApplicationContext(),8),0);
                    //setTextImage(R.drawable.icon_xiala);
                    break;
            }
        }
    };


    /**
     * 监听popupwindow取消
     */
    private PopupWindow.OnDismissListener dismissListener=new PopupWindow.OnDismissListener() {
        @Override
        public void onDismiss() {
            //setTextImage(R.drawable.icon_xiala);
        }
    };


    /**
     * 点击或者滑动日期View后返回的对应日期
     *
     * @param year 年
     * @param month 月
     * @param day 日
     * @param type
     * type=0表示做了点击操作
     * type=1表示做了滑动操作
     * type=2表示调用了toSpecifyDate()方法
     */
    @Override
    public void onClickDate(int year, int month, int day,int type) {
        //Log.e("test_2222","MainActivity_onClickDate "+year+" "+month+" "+day);
        year_position = year-2017;
        //   middleYear = year;
        //   middleMonth = month+1;
        this.type =type;

        tvValue.setText(year+"年");
        recyclerViewAdapter.setCurrentMonth(month);
        recyclerViewAdapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(month); //滑动到当前月份

        //点击时（type==0）才做请求日接口操作
        //Log.e("dede","final type:"+type+" "+year+" "+month+" "+day);
        if(type==0 || type==2){
            //Log.e("dede","type==0 || type==2");
            initCalendarMonth(slSchedule,year,month+1,false);
            initCalendarDay(year,month+1,day);
        }
    }
    @Override
    public void onPageChange(int year, int month, int day) {

    }


    /**
     * 获取CalendarView下面list数据
     * 日接口
     *
     * @param year 年
     * @param month 月
     * @param day 日
     */
    private void initCalendarDay(int year, int month, int day) {
        tvSelectTime.setText(year+"年"+month+"月"+day+"日");
        //String dayDate = getYearMonthDay(year,month,day);

        returnCalDayBean = new ReturnCalDayBean();
        List<ReturnCalDayBean.DayPayDetailBean> dueCalendarInfoList = new ArrayList<>();
        //模拟数据2019-2-11 2019-2-12 2019-2-13 2019-2-14
        if(year==2019&&month==2&&(day==11||day==12||day==13||day==14)) {
            returnCalDayBean.setAlreadyRepayAmount(1000.00);
            returnCalDayBean.setNeedRepayAmount(10000.00);

            dueCalendarInfoList.add(new ReturnCalDayBean.DayPayDetailBean("标题一", 50, 100, 1));
            dueCalendarInfoList.add(new ReturnCalDayBean.DayPayDetailBean("标题二", 30, 200, 2));
            dueCalendarInfoList.add(new ReturnCalDayBean.DayPayDetailBean("标题三", 40, 500, 1));
            returnCalDayBean.setDayPayList(dueCalendarInfoList);
        }else {
            returnCalDayBean.setAlreadyRepayAmount(0.00);
            returnCalDayBean.setNeedRepayAmount(0.00);
            dueCalendarInfoList.clear();
            returnCalDayBean.setDayPayList(dueCalendarInfoList);
        }

        tv_needRepayAmount.setText(MyUtils.addComma(String.format("%.2f", returnCalDayBean.getNeedRepayAmount())));
        tv_getRepayAmount.setText(MyUtils.addComma(String.format("%.2f", returnCalDayBean.getAlreadyRepayAmount())));

        if(returnCalDayBean.getDayPayList().size()>0){
            ll_no_view.setVisibility(View.GONE);
        }else {
            ll_no_view.setVisibility(View.VISIBLE);
        }
        if(scheduleAdapter!=null) {
            scheduleAdapter.setReturnCalDayBean(returnCalDayBean.getDayPayList());
            scheduleAdapter.notifyDataSetChanged();
        }

        MonthView monthView = monthCalendarView.getCurrentMonthView();
        if(monthView!=null) {
            if (year != monthView.getSelectYear() || month != (monthView.getSelectMonth() + 1)) {
                //TODO:触发点击事件
                //Log.e("test_8888","ddd");
                monthView.clickThisMonth(monthView.getSelectYear(), monthView.getSelectMonth(), monthView.getSelectDay(), 0);
            }
        }

    }


    /**
     * 获取小红点数据 （WeekView和MonthView日期对应）
     * 月接口
     *
     * @param slSchedule  ScheduleLayout
     * @param year 年
     * @param month 月
     * @param tag true表示是MonthView请求接口  false是weekView请求接口
     */
    private void initCalendarMonth(ScheduleLayout slSchedule, int year, int month, boolean tag){
        String monthDate = getYearMonthDay(year,month); //对月加0操作

        returnCalMonthList = new ArrayList<>();
        returnCalMonthList.add(new ReturnCalMonthBean(1550029511000l,3,1));
        returnCalMonthList.add(new ReturnCalMonthBean(1549943111000l,3,1));
        returnCalMonthList.add(new ReturnCalMonthBean(1550115911000l,3,2));
        returnCalMonthList.add(new ReturnCalMonthBean(1550202311000l,3,1));

        slSchedule.setReturnCalMonthBeanList(returnCalMonthList,tag);


    }


    private String getYearMonthDay(int year,int month,int day){
        String date = year+"";
        if(month<10){
            date+="0"+month;
        }else {
            date+=month;
        }

        if(day<10){
            date+="0"+day;
        }else {
            date+=day;
        }
        return date;
    }

    private String getYearMonthDay(int year,int month){
        String date = year+"";
        if(month<10){
            date+="0"+month;
        }else {
            date+=month;
        }
        return date;
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



    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }
}
