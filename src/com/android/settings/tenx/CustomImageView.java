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

public class CustomImageView extends ImageView {

    private Context mContext;

    public CustomImageView(Context context) {
        this(context, null);
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        final Resources resources = getResources();
        mContext = context;
        setDrawableColor();
    }

    private void setDrawableColor() {
        ContentResolver resolver = mContext.getContentResolver();

        int mIconColor = Settings.System.getInt(resolver,
                Settings.System.SETTINGS_DASHBOARD_ICON_COLOR, 0);
        int mGetShadeType = Settings.System.getInt(resolver,
                Settings.System.TENX_SHADE_TYPE, 0);

        switch (mIconColor) {
            case 1:
                if (mGetShadeType == 1 || mGetShadeType == 3) {
                    setColorFilter(mContext.getResources().getColor(R.color.monet_accurate_shade_system));
                } else {
                    setColorFilter(getThemeAccentColor(mContext));
                }
                break;
            case 2:
                setColorFilter(Color.parseColor("#5a5a5a"));
                break;
            case 3:
                setColorFilter(randomColor());
                break;
        }
    }

    public static int randomColor() {
        int red = (int) (0xff * Math.random());
        int green = (int) (0xff * Math.random());
        int blue = (int) (0xff * Math.random());
        return Color.argb(255, red, green, blue);
    }

    public static int getThemeAccentColor(final Context context) {
        final TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.colorAccent, value, true);
        return value.data;
    }
}
