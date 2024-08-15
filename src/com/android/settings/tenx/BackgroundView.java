package com.android.settings.tenx;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.util.TypedValue;
import android.widget.RelativeLayout;

import com.android.settings.R;

public class BackgroundView extends RelativeLayout {

    private Context mContext;

    public BackgroundView(Context context) {
        this(context, null);
    }

    public BackgroundView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BackgroundView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        final Resources resources = getResources();
        mContext = context;
        setShapes();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setSizeFromSettings();
    }

    private void setShapes() {
        ContentResolver resolver = mContext.getContentResolver();

        boolean mBackgroundShown = Settings.System.getInt(resolver,
                Settings.System.SETTINGS_DASHBOARD_BACKGROUND_SHOWN, 0) == 1;
        boolean mBackgroundColor = Settings.System.getInt(resolver,
                Settings.System.SETTINGS_DASHBOARD_BACKGROUND_COLOR, 0) == 0;
        int mBackgroundStyle = Settings.System.getInt(resolver,
                Settings.System.SETTINGS_DASHBOARD_BACKGROUND_STYLE, 0);
        int mStrokeWidth = Settings.System.getInt(resolver,
                Settings.System.SETTINGS_DASHBOARD_BACKGROUND_STROKE_WIDTH, 5);
        int mGradientStartColor = Settings.System.getInt(resolver,
                Settings.System.SETTINGS_DASHBOARD_BACKGROUND_GRADIENT_START_COLOR, 0xff1a73e8);
        int mGradientEndColor = Settings.System.getInt(resolver,
                Settings.System.SETTINGS_DASHBOARD_BACKGROUND_GRADIENT_END_COLOR, 0xff1a73e8);
        boolean mMonetAccurateShade = Settings.System.getInt(resolver,
                Settings.System.MONET_ACCURATE_SHADE, 0) != 0;

        int strokeWidth = mStrokeWidth;
        int strokeColor = !mMonetAccurateShade
                              ? getThemeAccentColor(mContext)
                              : mContext.getResources().getColor(R.color.monet_accurate_shade_system);
        int backgroundColor = !mMonetAccurateShade
                                  ? getThemeAccentColor(mContext)
                                  : mContext.getResources().getColor(R.color.monet_accurate_shade_system);
        int transparentColor = Color.TRANSPARENT;
        int colors[] = {mGradientStartColor, mGradientEndColor};
        GradientDrawable gD = new GradientDrawable();
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, colors);

        switch (mBackgroundStyle) {
            case 0:
                // Solid accent (Circle)
                gD.setShape(GradientDrawable.OVAL);
                if (mBackgroundColor) {
                    gD.setColor(backgroundColor);
                } else {
                    gD.setColor(randomColor());
                }
                break;
            case 1:
                // Stroke accent (Circle)
                gD.setShape(GradientDrawable.OVAL);
                gD.setColor(transparentColor);
                if (mBackgroundColor) {
                    gD.setStroke(strokeWidth, strokeColor);
                } else {
                    gD.setStroke(strokeWidth, randomColor());
                }
                break;
            case 2:
                // Stroke accent (Circle with background alpha)
                gD.setShape(GradientDrawable.OVAL);
                if (mBackgroundColor) {
                    gD.setColor(getColorWithAlpha(backgroundColor, 0.2f));
                } else {
                    gD.setColor(getColorWithAlpha(randomColor(), 0.2f));
                }
                if (mBackgroundColor) {
                    gD.setStroke(strokeWidth, strokeColor);
                } else {
                    gD.setStroke(strokeWidth, randomColor());
                }
                break;
            case 3:
                // Solid accent (Square);
                gD.setShape(GradientDrawable.RECTANGLE);
                if (mBackgroundColor) {
                    gD.setColor(backgroundColor);
                } else {
                    gD.setColor(randomColor());
                }
                gD.setCornerRadius(25);
                break;
            case 4:
                // Stroke accent (Square);
                gD.setShape(GradientDrawable.RECTANGLE);
                gD.setColor(transparentColor);
                if (mBackgroundColor) {
                    gD.setStroke(strokeWidth, strokeColor);
                } else {
                    gD.setStroke(strokeWidth, randomColor());
                }
                gD.setCornerRadius(25);
                break;
            case 5:
                // Stroke accent (Square with background alpha);
                gD.setShape(GradientDrawable.RECTANGLE);
                if (mBackgroundColor) {
                    gD.setColor(getColorWithAlpha(backgroundColor, 0.2f));
                } else {
                    gD.setColor(getColorWithAlpha(randomColor(), 0.2f));
                }
                if (mBackgroundColor) {
                    gD.setStroke(strokeWidth, strokeColor);
                } else {
                    gD.setStroke(strokeWidth, randomColor());
                }
                gD.setCornerRadius(25);
                break;
            case 6:
                // Solid gradient (Circle)
                gradientDrawable.setShape(GradientDrawable.OVAL);
                break;
            case 7:
                // Stroke gradient (Circle)
                setBackground(getResources().getDrawable(R.drawable.gradient_background));
                GradientDrawable drawable = (GradientDrawable) this.getBackground();
                drawable.setColors(colors);
                drawable.setThickness(strokeWidth);
                setBackground(drawable);
                break;
            case 8:
                // Solid gradient (Square)
                gradientDrawable.setShape(GradientDrawable.RECTANGLE);
                gradientDrawable.setCornerRadius(25);
                break;
        }

        if (mBackgroundShown) {
            if (mBackgroundStyle == 0 || mBackgroundStyle == 1 || mBackgroundStyle == 2 || mBackgroundStyle == 3 || mBackgroundStyle == 4 || mBackgroundStyle == 5) {
                setBackground(gD);
            } else if (mBackgroundStyle == 6 || mBackgroundStyle == 8) {
                setBackground(gradientDrawable);
            }
        } else {
            setBackground(null);
        }
    }

    private void setSizeFromSettings() {
        ContentResolver resolver = mContext.getContentResolver();
        int defaultWidthHeight = mContext.getResources().getDimensionPixelSize(
                R.dimen.settings_dashboard_background_width_height);

        int backgroundWidthHeight = Settings.System.getInt(resolver,
                Settings.System.SETTINGS_DASHBOARD_BACKGROUND_SIZE, defaultWidthHeight);

        setSize(dpToPx(backgroundWidthHeight), dpToPx(backgroundWidthHeight));
    }

    private void setSize(int width, int height) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new ViewGroup.LayoutParams(width, height);
        } else {
            layoutParams.width = width;
            layoutParams.height = height;
        }
        setLayoutParams(layoutParams);
        requestLayout();
    }

    private int getColorWithAlpha(int color, float ratio) {
        int newColor = 0;
        int alpha = Math.round(Color.alpha(color) * ratio);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        newColor = Color.argb(alpha, r, g, b);
        return newColor;
    }

    public static int getThemeAccentColor(final Context context) {
        final TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.colorAccent, value, true);
        return value.data;
    }

    public static int randomColor() {
        int red = (int) (0xff * Math.random());
        int green = (int) (0xff * Math.random());
        int blue = (int) (0xff * Math.random());
        return Color.argb(255, red, green, blue);
    }

    public int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
