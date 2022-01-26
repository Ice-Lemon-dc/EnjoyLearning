package com.dc.enjoylearning.recyclerview.itemdecoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * RecyclerView通用分割线
 *
 * 通过阅读源码我们知道，分割线所留出来的位置其实是占用了子View的位置，所以才会出现条目宽度不一（分割线比较宽时最后一行高度/最后一列宽度比较大）的情况，
 * 这个Bug目前可能不能去解决，因为这个google留下的。
 * @author Lemon
 */
public class GridLayoutItemDecoration extends RecyclerView.ItemDecoration {

    private final Drawable mDivider;

    public GridLayoutItemDecoration(Context context, int drawableResourceId) {
        mDivider = ContextCompat.getDrawable(context, drawableResourceId);
    }

    /**
     * 留出分割线位置
     *
     * @param outRect Rect
     * @param view    View
     * @param parent  RecyclerView
     * @param state   State
     */
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {

        int bottom = mDivider.getIntrinsicHeight();
        int right = mDivider.getIntrinsicWidth();
        // 留出分割线的位置 最下面和最右边不要留
        if (isLastColumn(view, parent)) {
            right = 0;
        }
        if (isLastRow(view, parent)) {
            bottom = 0;
        }
        outRect.bottom = bottom;
        outRect.right = right;
    }

    /**
     * 最后一列
     *
     * @param view   View
     * @param parent RecyclerView
     * @return boolean
     */
    private boolean isLastColumn(View view, RecyclerView parent) {
        // 获取当前位置 --> 参考源码中getItemOffsets调用过时方法getItemOffsets中的itemPosition
        int currentPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        // 获取列数
        int spanCount = getSpanCount(parent);
        return (currentPosition + 1) % spanCount == 0;
    }


    /**
     * 最后一行
     *
     * @param view   View
     * @param parent RecyclerView
     * @return boolean
     */
    private boolean isLastRow(View view, RecyclerView parent) {

        int currentPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        // 列数
        int spanCount = getSpanCount(parent);

        // 行数 = 总的条目 / 列数 (不能整除则+1)
        int rowCount = parent.getAdapter().getItemCount() / spanCount == 0 ?
                parent.getAdapter().getItemCount() / spanCount : (parent.getAdapter().getItemCount() / spanCount + 1);

        // 返回 (当前位置 + 1) > (行数 - 1) * 列数
        return (currentPosition + 1) > (rowCount - 1) * spanCount;
    }

    /**
     * 获取RecyclerView列数
     *
     * @param recyclerView RecyclerView
     * @return int
     */
    private int getSpanCount(RecyclerView recyclerView) {
        // 获取列数
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            return gridLayoutManager.getSpanCount();
        }
        return 1;
    }

    @Override
    public void onDraw(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        drawHorizontal(canvas, parent);
        drawVertical(canvas, parent);
    }

    /**
     * 绘制垂直方向
     *
     * @param canvas Canvas
     * @param parent RecyclerView
     */
    private void drawVertical(Canvas canvas, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) childView.getLayoutParams();

            int top = childView.getTop() - params.topMargin;
            int bottom = childView.getBottom() + params.bottomMargin;
            int left = childView.getRight() + params.rightMargin;
            int right = left + mDivider.getIntrinsicWidth();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(canvas);
        }
    }

    /**
     * 绘制水平方向
     *
     * @param canvas Canvas
     * @param parent RecyclerView
     */
    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) childView.getLayoutParams();

            int left = childView.getLeft() - params.leftMargin;
            int right = childView.getRight() + mDivider.getIntrinsicWidth() + params.rightMargin;
            int top = childView.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(canvas);
        }
    }
}

