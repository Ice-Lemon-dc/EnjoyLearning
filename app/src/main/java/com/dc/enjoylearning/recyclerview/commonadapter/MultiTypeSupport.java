package com.dc.enjoylearning.recyclerview.commonadapter;

/**
 * 多条目布局
 * @author Lemon
 */
public interface MultiTypeSupport<T> {

    /**
     * 获取布局id
     *
     * @param t 数据
     * @return 布局id
     */
    int getLayoutId(T t);
}
