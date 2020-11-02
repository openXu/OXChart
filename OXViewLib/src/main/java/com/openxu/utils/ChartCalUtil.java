package com.openxu.utils;


import android.util.Log;

import com.openxu.cview.xmstock20201030.build.AxisMark;

import java.util.Arrays;

/**
 * 图表计算工具
 */
public class ChartCalUtil {

    private static String TAG = "ChartCalUtil";

    /**
     * 计算X轴刻度点lable值
     * @param axisMark
     */
    public static void calXLable(AxisMark axisMark){/*List datas, String field, int num){*/
        Object[] newDatas;
        if(axisMark.datas==null || axisMark.field==null) {
            axisMark.lables = new String[]{};
        }
        if(axisMark.datas.size()<=axisMark.lableNum){
            axisMark.lables = new String[axisMark.datas.size()];
            newDatas = axisMark.datas.toArray();
        }else{
            axisMark.lables = new String[axisMark.lableNum];
            newDatas = new Object[axisMark.lableNum];
            //取第一个和最后一个
            newDatas[0] = axisMark.datas.get(0);
            newDatas[axisMark.lableNum-1] = axisMark.datas.get(axisMark.datas.size()-1);
            int part = axisMark.datas.size() / axisMark.lableNum;
            Log.i(TAG, "均匀取值："+part);
            for(int i = 1; i< axisMark.lableNum-1; i++){
                newDatas[i] = axisMark.datas.get(i*part);
            }
        }

        for(int j = 0; j<newDatas.length; j++){
            axisMark.lables[j] = ReflectUtil.getField(newDatas[j], axisMark.field).toString();
        }
    }


    /**
     * 根据传入的Y轴信息，从数据中获取最小最大值，并获取到Y刻度点值
     * @param axisMark
     * @return
     */
    public static AxisMark calYLable(AxisMark axisMark){
        axisMark.cal_mark_max =  Float.MIN_VALUE;    //Y轴刻度最大值
        axisMark.cal_mark_min =  Float.MAX_VALUE;    //Y轴刻度最小值
        for(Object data : axisMark.datas){
            for(int i = 1; i< axisMark.lableNum + 1 ; i++){
                String str = ReflectUtil.getField(data, axisMark.field).toString();
                try {
                    if(str.contains("%")){
                        str = str.substring(0,str.indexOf("%"));//百分数不能直接强转
                        axisMark.cal_mark = Float.parseFloat(str) /100.0f;
                    }else{
                        axisMark.cal_mark = Float.parseFloat(str);
                    }
//                    LogUtil.d(TAG, str+" Y轴 被识别为Float = "+YMARK);
                    if(axisMark.cal_mark>axisMark.cal_mark_max)
                        axisMark.cal_mark_max = axisMark.cal_mark;
                    if(axisMark.cal_mark<axisMark.cal_mark_min)
                        axisMark.cal_mark_min = axisMark.cal_mark;
                }catch (Exception e){
                }
            }

        }
        LogUtil.i(TAG, "Y轴真实axisMark.cal_mark_min="+axisMark.cal_mark_min+"   axisMark.cal_mark_max="+axisMark.cal_mark_max);
        if(axisMark.cal_mark_max>0)
            axisMark.cal_mark_max *= 1.1f;
        else
            axisMark.cal_mark_max /= 1.1f;
        if(axisMark.cal_mark_min>0)
            axisMark.cal_mark_min /= 1.1f;
        else
            axisMark.cal_mark_min *= 1.1f;
        if(axisMark.cal_mark_min>0)
            axisMark.cal_mark_min = 0;

        axisMark.cal_mark = (axisMark.cal_mark_max-axisMark.cal_mark_min)/(axisMark.lableNum - 1);
        axisMark.lables = new String[axisMark.lableNum];
        if(axisMark.lableType == AxisMark.LABLE_TYPE.INTEGER){
            axisMark.cal_mark = (int)axisMark.cal_mark + 1;
            axisMark.cal_mark_min = (int)axisMark.cal_mark_min;
            axisMark.cal_mark_max = axisMark.cal_mark_min+axisMark.cal_mark*(axisMark.lableNum-1);
        }else if(axisMark.lableType == AxisMark.LABLE_TYPE.PERCENTAGE){
        }else if(axisMark.lableType == AxisMark.LABLE_TYPE.FLOAT){
        }
        LogUtil.i(TAG, "Y轴axisMark.cal_mark_min="+axisMark.cal_mark_min+"   axisMark.cal_mark_max="+axisMark.cal_mark_max+"   axisMark.cal_mark="+axisMark.cal_mark);
        for(int i = 0; i< axisMark.lableNum; i++){
            float num = axisMark.cal_mark_min + i * axisMark.cal_mark;
            if(axisMark.lableType == AxisMark.LABLE_TYPE.INTEGER){
                axisMark.lables[i] = ((int)num)+"";
            }else if(axisMark.lableType == AxisMark.LABLE_TYPE.PERCENTAGE){
                axisMark.lables[i] = NumberFormatUtil.formattedDecimalToPercentage(num);
            }else if(axisMark.lableType == AxisMark.LABLE_TYPE.FLOAT){
                axisMark.lables[i] = NumberFormatUtil.formattedDecimal(num);
            }
        }
        Log.i(TAG, "计算Y轴刻度："+ Arrays.asList(axisMark.lables));
        return axisMark;
    }





}
