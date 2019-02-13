package com.example.zhaofan.mycalendar.Schedule;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.zhaofan.mycalendar.MyUtils;
import com.example.zhaofan.mycalendar.R;
import com.widget.calendar.Bean.ReturnCalDayBean;

import java.util.List;


/**
 * 回款日历页面底部的数据组View适配器
 */
public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {
    private Context mContext;
    List<ReturnCalDayBean.DayPayDetailBean> dayPayList;

    public ScheduleAdapter(Context context,List<ReturnCalDayBean.DayPayDetailBean> dayPayList) {
        mContext = context;
        this.dayPayList = dayPayList;
    }


    @Override
    public ScheduleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ScheduleViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_schedule, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        //Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/DIN-Medium.otf");

        holder.tvScheduleTitle.setText(dayPayList.get(position).getTitle());
        holder.tvAccrual.setText(MyUtils.addComma(String.format("%.2f", dayPayList.get(position).getInterest())));
        //holder.tvAccrual.setTypeface(tf);

        if(dayPayList.get(position).getStatus()==2) {
            holder.tvIsReturnMoney.setText("已回款");
            holder.tvIsReturnMoney.setTextColor(mContext.getResources().getColor(R.color.txt_newamont));
            holder.tvIsReturnMoney.setBackground(mContext.getResources().getDrawable(R.drawable.bg_need_return_money));
        } else  {
            holder.tvIsReturnMoney.setText("待回款");
            holder.tvIsReturnMoney.setTextColor(mContext.getResources().getColor(R.color.txt_yellow));
            holder.tvIsReturnMoney.setBackground(mContext.getResources().getDrawable(R.drawable.bg_already_return_money));
        }
        holder.tvCapital.setText(MyUtils.addComma(String.format("%.2f", dayPayList.get(position).getCapital())));
        //holder.tvCapital.setTypeface(tf);
    }

    @Override
    public int getItemCount() {
        return dayPayList != null ? dayPayList.size() : 0 ;
    }

    protected class ScheduleViewHolder extends RecyclerView.ViewHolder {
        protected TextView tvScheduleTitle;
        protected TextView tvAccrual;
        protected TextView tvIsReturnMoney;
        protected TextView tvCapital;

        public ScheduleViewHolder(View itemView) {
            super(itemView);
            tvScheduleTitle = (TextView) itemView.findViewById(R.id.tvScheduleTitle);
            tvAccrual = (TextView) itemView.findViewById(R.id.tvAccrual);
            tvIsReturnMoney = (TextView) itemView.findViewById(R.id.tvIsReturnMoney);
            tvCapital = (TextView) itemView.findViewById(R.id.tvCapital);
        }

    }

    public void insertItem(ReturnCalDayBean.DayPayDetailBean dayPayDetailBean) {
        dayPayList.add(dayPayDetailBean);
        notifyItemInserted(dayPayList.size() - 1);
    }

    public void removeItem(ReturnCalDayBean.DayPayDetailBean dayPayDetailBean) {
        if (dayPayList.remove(dayPayDetailBean)) {
            notifyDataSetChanged();
        }
    }

    public void setReturnCalDayBean(List<ReturnCalDayBean.DayPayDetailBean> dayPayList) {
        this.dayPayList = dayPayList;
    }

}
