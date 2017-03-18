package com.mobapply.coolphotos.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;


public class HeaderImageView extends ImageView {

    public HeaderImageView(Context context) {
        super(context);
    }

    public HeaderImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeaderImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final int width = getDefaultSize(getSuggestedMinimumWidth(),widthMeasureSpec);
        final int height=width/2;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
        super.onSizeChanged(w, w, oldw, oldh);
    }
}
