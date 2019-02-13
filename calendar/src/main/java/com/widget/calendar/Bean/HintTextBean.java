package com.widget.calendar.Bean;

import java.util.ArrayList;

/**
 * Created by zhaofan on 2018/11/20.
 */

public class HintTextBean {
    private ArrayList<String> date;       //获得有特殊标识（有回款或者未回款）的日期
    private int[] hintText;               //获得该日期对应的小红点数字
    private boolean[] isReceivedPayment;  //判断是否已经回款

    public ArrayList<String> getDate() {
        return date;
    }

    public void setDate(ArrayList<String> date) {
        this.date = date;
    }

    public int[] getHintText() {
        return hintText;
    }

    public void setHintText(int[] hintText) {
        this.hintText = hintText;
    }

    public boolean[] getIsReceivedPayment() {
        return isReceivedPayment;
    }

    public void setIsReceivedPayment(boolean[] receivedPayment) {
        isReceivedPayment = receivedPayment;
    }

}
