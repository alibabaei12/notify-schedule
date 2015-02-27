package org.jakelcode.schedule.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.FrameLayout;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Pin Khe "Jake" Loo (11 February, 2015)
 */
public class FoldLayout extends FrameLayout implements Checkable {
    private static final String TAG = FoldLayout.class.getName();
    private boolean mHideChild;

    private String mTitle;
    private long mDuration;
    private Drawable mHideDrawable;
    private Drawable mShowDrawable;

    public FoldLayout(Context context) {
        super(context);
        init();
    }

    public FoldLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FoldLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        setClickable(true);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    public void setChecked(boolean checked) {
        if (mHideChild != checked) {
            this.mHideChild = checked;

            animateChild();
            invalidate();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void animateChild() {
        for (int i = 0 ; i < getChildCount(); i ++) {
            final View child = getChildAt(i);

            child.animate().y(mHideChild ? -child.getTop() : child.getTop())
            .withStartAction(new Runnable() {

                @Override
                public void run() {
                    if (!mHideChild) {
                        child.setVisibility(View.VISIBLE);
                    }
                }
            })
            .withEndAction(new Runnable() {
                @Override
                public void run() {
                    if (mHideChild) {
                        child.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
    }

    @Override
    public boolean isChecked() {
        return mHideChild;
    }

    @Override
    public void toggle() {
        setChecked(!mHideChild);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
