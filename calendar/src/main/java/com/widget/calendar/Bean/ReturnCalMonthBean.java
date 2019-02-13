package com.widget.calendar.Bean;

import java.io.Serializable;

/**
 * Created by Linc on 2018/12/14.
 *
 * 这里就是一个月的集合
 */

public class ReturnCalMonthBean extends com.widget.calendar.Bean.BaseBean implements Serializable{

    private static final long serialVersionUID = 700739060794591692L;

    private long addTime;//对应日期

    private int count; //个数

    private int status; //状态: 待回款1 ,已回款 2

    public ReturnCalMonthBean(){}

    public ReturnCalMonthBean(long addTime, int count, int status) {
        this.addTime = addTime;
        this.count = count;
        this.status = status;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    @Override
    public String toString() {
        return "ReturnCalMonthBean{" +
                "addTime=" + addTime +
                ", count=" + count +
                ", status=" + status +
                '}';
    }
}
