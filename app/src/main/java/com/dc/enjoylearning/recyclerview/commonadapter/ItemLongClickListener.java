package com.dc.enjoylearning.recyclerview.commonadapter;

/**
 * @author Lemon
 */
public interface ItemLongClickListener {

    /**
     * 长按事件
     *
     * @param position int
     * @return boolean
     */
    boolean onItemLongClick(int position);
}
