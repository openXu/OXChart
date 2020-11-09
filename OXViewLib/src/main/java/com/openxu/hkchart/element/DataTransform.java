package com.openxu.hkchart.element;

public interface DataTransform <T, R>{

    R transform(T t);

}
