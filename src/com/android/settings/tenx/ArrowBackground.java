package com.android.settings.tenx;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.PorterDuff;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.View;
import android.util.TypedValue;
import android.widget.RelativeLayout;

import com.android.settings.R;

public class ArrowBackground extends RelativeLayout {

    private Context mContext;

    public ArrowBackground(Context context) {
        this(context, null);
    }

    public ArrowBackground(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArrowBackground(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        final Resources resources = getResources();
        mContext = context;
        setArrow();
    }

    private void setArrow() {
        ContentResolver resolver = mContext.getContentResolver();
        int mArrowBackground = Settings.System.getInt(resolver,
                Settings.System.SETTINGS_DASHBOARD_BACKGROUND_ARROW, 0);

        switch (mArrowBackground) {
            // Disabled
            case 0:
                setVisibility(View.GONE);
                break;
            // Arrow only
            case 1:
                setBackgroundResource(Color.TRANSPARENT);
                break;
            // Visible
            case 2:
                setVisibility(View.VISIBLE);
                break;
        }
    }
}
