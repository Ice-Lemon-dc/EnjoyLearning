package com.dc.enjoylearning.customview.flowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lemon
 */
public class FlowLayout extends ViewGroup {

    /**
     * 每一行的子View
     */
    private List<View> lineViews;

    /**
     * 所有的行 一行一行的存储
     */
    private List<List<View>> views;

    /**
     * 每一行的高度
     */
    private List<Integer> heights;

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        lineViews = new ArrayList<>();
        views = new ArrayList<>();
        heights = new ArrayList<>();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 1.测量自身
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 2.为每个子View结算测量的限制信息Mode/Size
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        // 当前行的宽度和高度
        int lineWidth = 0;
        int lineHeight = 0;
        // 流式布局的宽度和高度
        int flowLayoutWidth = 0;
        int flowLayoutHeight = 0;


        // 3.把上一步确定的限制信息传递给子View，然后子View开始测量
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            // 测量子View，获取当前子view的宽高
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            // 4.获取子View测量完成后的尺寸
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            if (lineWidth + childWidth > widthSize) {
                views.add(lineViews);
                lineViews = new ArrayList<>();
                flowLayoutWidth = Math.max(flowLayoutWidth, lineWidth);
                flowLayoutHeight += lineHeight;
                heights.add(lineHeight);
                lineWidth = 0;
                lineHeight = 0;
            }
            lineViews.add(child);
            lineWidth += childWidth;
            lineHeight = Math.max(lineHeight, childHeight);
        }

        // 5.ViewGroup根据自身的情况，计算自己的尺寸
        // 6.保存自身的尺寸
        setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? widthSize : flowLayoutWidth, heightMode == MeasureSpec.EXACTLY ? heightSize : flowLayoutHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int curX = 0;
        int curY = 0;

        // 1. 遍历子View for
        int lineCount = views.size();
        //大循环，所有的子View 一行一行的布局
        for (int i = 0; i < lineCount; i++) {
            // 取出一行
            List<View> lineViews = views.get(i);
            // 取出这一行的高度值
            int lineHeight = heights.get(i);
            // 遍历当前行的子View
            int size = lineViews.size();
            //布局当前行的每一个view
            for (int j = 0; j < size; j++) {
                View child = lineViews.get(j);
                int left = curX;
                int top = curY;
                int right = left + child.getMeasuredWidth();
                int bottom = top + child.getMeasuredHeight();
                child.layout(left, top, right, bottom);
                curX += child.getMeasuredWidth();
            }
            curX = 0;
            curY += lineHeight;
        }
        // 2. 确定自己的规则
        // 3. 子View的测量尺寸
        // 4. left,top,right,bottom
        // 5. child.layout
    }
}
