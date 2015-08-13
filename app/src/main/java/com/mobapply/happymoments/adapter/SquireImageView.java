package com.mobapply.happymoments.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;


public class SquireImageView extends ImageView {

    public SquireImageView(Context context) {
        super(context);
    }

    public SquireImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquireImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final int width = getDefaultSize(getSuggestedMinimumWidth(),widthMeasureSpec);
        setMeasuredDimension(width, width);
    }

    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
        super.onSizeChanged(w, w, oldw, oldh);
    }


}
