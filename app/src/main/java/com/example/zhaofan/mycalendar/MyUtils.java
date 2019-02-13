package com.example.zhaofan.mycalendar;


import android.content.Context;

import org.jsoup.helper.StringUtil;

/**
 * Created by zhaofan on 2019/2/13.
 */

public class MyUtils {


    public static String addComma(String str) {
        if (!StringUtil.isBlank(str)) {
            String mst = str.substring(0, str.length() - 3);
            String sy = str.substring(str.length() - 3);
            String reverseStr = new StringBuilder(mst).reverse().toString();
            String strTemp = "";
            for (int i = 0; i < reverseStr.length(); i++) {
                if (i * 3 + 3 > reverseStr.length()) {
                    strTemp += reverseStr.substring(i * 3, reverseStr.length());
                    break;
                }
                strTemp += reverseStr.substring(i * 3, i * 3 + 3) + ",";
            }
// 将[789,456,] 中最后一个[,]去除
            if (strTemp.endsWith(",")) {
                strTemp = strTemp.substring(0, strTemp.length() - 1);
            }

// 将数字重新反转
            String resultStr = new StringBuilder(strTemp).reverse().toString();

            return resultStr + sy;
        }
        return "";
    }

    public static int dp2px(Context context, float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

}
