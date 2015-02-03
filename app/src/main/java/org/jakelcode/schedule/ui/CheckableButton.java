package org.jakelcode.schedule.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.Checkable;

/**
 *
 * @author Pin Khe "Jake" Loo (30 January, 2015)
 */
public class CheckableButton extends Button implements Checkable {
    private boolean mChecked = false;
    private static final int[] CheckedStateSet = {android.R.attr.state_checked};

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            toggle();
        }
    };

    public CheckableButton(Context context) {
        super(context);
        setOnClickListener(onClickListener);
    }

    public CheckableButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(onClickListener);
    }

    public CheckableButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnClickListener(onClickListener);
    }

    @Override
    public void setChecked(boolean b) {
        if (b != mChecked) {
            mChecked = b;
            refreshDrawableState();
        }
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        mChecked = !mChecked;
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);

        if (isChecked()) {
            mergeDrawableStates(drawableState, CheckedStateSet);
        }
        return drawableState;
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        invalidate();
    }
}
