package com.mobapply.happymoments.adapter;


import android.content.Context;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.AttributeSet;
import android.widget.ImageView;

public class AlbumImageView  extends ImageView {
    public AlbumImageView(Context context) {
        super(context);
    }

    public AlbumImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AlbumImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final int width = getDefaultSize(getSuggestedMinimumWidth(),widthMeasureSpec);
        final int height=width*4/3;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
        super.onSizeChanged(w, w, oldw, oldh);
    }


}
