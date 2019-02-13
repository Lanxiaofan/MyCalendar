package com.example.zhaofan.mycalendar;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by zhaofan on 2018/11/12.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
    private List<String> months;
    private OnItemClickListener mOnItemClickListener;
    private int currentMonth;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_month;
        public ViewHolder(View itemView) {
            super(itemView);
            tv_month = (TextView) itemView.findViewById(R.id.tv_month_item);
        }
    }

    public RecyclerViewAdapter(List<String> months) {
        this.months = months;
    }

    /**
     * 用于创建ViewHolder实例的，并把加载出来的布局传入到构造函数当中，最后将viewholder的实例返回
     * @param parent
     * @param viewType
     * @return
     */


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview_layout,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.tv_month.setText(months.get(position));
        if(position==currentMonth){
            //当月日期显示为红底白字
            //holder.tv_month.setBackgroundColor(Color.parseColor("#fe453c"));
            holder.tv_month.setTextColor(Color.parseColor("#ffffff"));
        }else {
            //其他月日期显示为红底灰字
            //holder.tv_month.setBackgroundColor(Color.parseColor("#fe453c"));
            holder.tv_month.setTextColor(Color.parseColor("#fe7873"));
        }

        if(mOnItemClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onClick(position);
                    //holder.tv_month.setBackgroundColor(Color.parseColor("#fe453c"));
                    holder.tv_month.setTextColor(Color.parseColor("#ffffff"));

                }
            });
        }
    }



    @Override
    public int getItemCount() {
        return months.size();
    }

    public interface OnItemClickListener{
        void onClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener ){
        this.mOnItemClickListener=onItemClickListener;
    }

    public void setCurrentMonth(int currentMonth){
        this.currentMonth = currentMonth;
        //Log.e("MainActivity",this.currentMonth+"");
    }

}
