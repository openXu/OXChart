package com.openxu.chart.element;

public interface DataTransform <T, R>{

    R transform(T t);

}
