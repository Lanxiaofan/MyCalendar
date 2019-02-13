package com.widget.calendar.Bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Linc on 2018/12/14.
 *
 * 这里就是天的数据
 */

public class ReturnCalDayBean extends com.widget.calendar.Bean.BaseBean implements Serializable{

    private static final long serialVersionUID = 5119115976515178025L;

    private double needRepayAmount; //当日应回款

    private double alreadyRepayAmount; //当日已回款

    private List<DayPayDetailBean> dueCalendarInfoList;// 当年详情列表

    public static class DayPayDetailBean {
        public DayPayDetailBean(String title, double interest, double capital, int status) {
            this.title = title;
            this.interest = interest;
            this.capital = capital;
            this.status = status;
        }

        private String title; //标的标题

        private double interest ; // 利息

        private double capital; //本金

        private int status; //状态: 待回款1 ,已回款 2


        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public double getInterest() {
            return interest;
        }

        public void setInterest(double interest) {
            this.interest = interest;
        }

        public double getCapital() {
            return capital;
        }

        public void setCapital(double capital) {
            this.capital = capital;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return "DayPayDetailBean{" +
                    "title='" + title + '\'' +
                    ", interest=" + interest +
                    ", capital=" + capital +
                    ", status=" + status +
                    '}';
        }
    }

    public double getNeedRepayAmount() {
        return needRepayAmount;
    }

    public void setNeedRepayAmount(double needRepayAmount) {
        this.needRepayAmount = needRepayAmount;
    }

    public double getAlreadyRepayAmount() {
        return alreadyRepayAmount;
    }

    public void setAlreadyRepayAmount(double alreadyRepayAmount) {
        this.alreadyRepayAmount = alreadyRepayAmount;
    }



    public List<DayPayDetailBean> getDayPayList() {
        return dueCalendarInfoList;
    }

    public void setDayPayList(List<DayPayDetailBean> dueCalendarInfoList) {
        this.dueCalendarInfoList = dueCalendarInfoList;
    }

    @Override
    public String toString() {
        return "ReturnCalDayBean{" +
                "needRepayAmount=" + needRepayAmount +
                ", alreadyRepayAmount=" + alreadyRepayAmount +
                ", dayPayList=" + dueCalendarInfoList +
                '}';
    }
}
