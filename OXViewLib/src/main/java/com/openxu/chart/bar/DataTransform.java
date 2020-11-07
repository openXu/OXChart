package com.openxu.chart.bar;

import java.util.List;

public interface DataTransform <T>{

    List<List<Bar>> transform(T t);

}
