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
import android.widget.ImageView;

import com.android.settings.R;

public class Arrow extends ImageView {

    private Context mContext;

    public Arrow(Context context) {
        this(context, null);
    }

    public Arrow(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Arrow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        final Resources resources = getResources();
        mContext = context;
        setArrowColor();
    }

    private void setArrowColor() {
        ContentResolver resolver = mContext.getContentResolver();
        int mArrowColor = Settings.System.getInt(resolver,
                Settings.System.SETTINGS_DASHBOARD_ARROW_COLOR, 0);
        int mGetShadeType = Settings.System.getInt(resolver,
                Settings.System.TENX_SHADE_TYPE, 0);

        if (mArrowColor == 0) {
            setColorFilter(Color.WHITE);
        } else if (mArrowColor == 1) {
            setColorFilter(Color.BLACK);
        } else {
            if (mGetShadeType == 1 || mGetShadeType == 3) {
                setColorFilter(mContext.getResources().getColor(R.color.monet_accurate_shade_system));
            } else {
                setColorFilter(getThemeAccentColor(mContext));
            }
        }
    }

    public static int getThemeAccentColor(final Context context) {
        final TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.colorAccent, value, true);
        return value.data;
    }
}
