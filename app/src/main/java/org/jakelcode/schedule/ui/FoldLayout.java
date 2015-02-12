package org.jakelcode.schedule.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.FrameLayout;

/**
 * @author Pin Khe "Jake" Loo (11 February, 2015)
 */
public class FoldLayout extends FrameLayout implements Checkable {
    private boolean mChecked;

    public FoldLayout(Context context) {
        super(context);
    }

    public FoldLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public FoldLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        
    }

    @Override
    public void setChecked(boolean checked) {
        if (mChecked != checked) {
            this.mChecked = checked;
            drawableStateChanged();
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
}
