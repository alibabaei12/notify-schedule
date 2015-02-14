package org.jakelcode.schedule.ui;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Checkable;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Pin Khe "Jake" Loo (11 February, 2015)
 */
public class FoldLayout extends FrameLayout implements Checkable {
    private static final String TAG = FoldLayout.class.getName();

    private boolean mChecked;

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

        for (int i = 0; i < getChildCount(); i++) {
            final View v = getChildAt(i);

            v.animate().setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (isChecked()) {
                        v.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (!isChecked()) {
                        v.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    if (!isChecked()) {
                        v.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }

    @Override
    public void setChecked(boolean checked) {
        if (mChecked != checked) {
            this.mChecked = checked;
            invalidate();
        }
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        for (int i = 0 ; i < getChildCount(); i ++) {
//            getChildAt(i).animate().translationY(mChecked ? 100 : -100);
//        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

//        int wspec = MeasureSpec.makeMeasureSpec(
//                getMeasuredWidth(), MeasureSpec.EXACTLY);
//        int hspec = MeasureSpec.makeMeasureSpec(
//                getMeasuredHeight(), MeasureSpec.EXACTLY);
//        for(int i=0; i<getChildCount(); i++){
//            View v = getChildAt(i);
//            v.measure(wspec, hspec);
//        }

    }
}
