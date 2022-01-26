package com.dc.enjoylearning.recyclerview.itemdecoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * ListView样式的分割线
 * @author Lemon
 */
public class LinearLayoutItemDecoration extends RecyclerView.ItemDecoration {

    private final Drawable mDivider;

    public LinearLayoutItemDecoration(Context context, int drawableResourceId) {
        mDivider = ContextCompat.getDrawable(context, drawableResourceId);
    }

    /**
     * 留出分割线位置
     *
     * @param outRect Rect
     * @param view View
     * @param parent RecyclerView
     * @param state RecyclerView
     */
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if (position != 0) {
//            outRect.top = mDrawable.getIntrinsicHeight();
            outRect.set(0, mDivider.getIntrinsicHeight(), 0, 0);
        }
    }

    @Override
    public void onDraw(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int childCount = parent.getChildCount();

        Rect rect = new Rect();
        rect.left = parent.getPaddingLeft();
        rect.right = parent.getWidth() - parent.getPaddingRight();
        for (int i = 1; i < childCount; i++) {
            rect.bottom = parent.getChildAt(i).getTop();
            rect.top = rect.bottom - mDivider.getIntrinsicHeight();

            mDivider.setBounds(rect);
            mDivider.draw(canvas);
        }
    }
}
