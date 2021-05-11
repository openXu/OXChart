package com.openxu.hkchart.data;

/**
 * (共用)数据转换器，将服务器返回的数据转换为对应图表需要的数据格式
 * @param <T>
 * @param <R>
 */
public interface DataTransform <T, R>{

    R transform(T t);

}
