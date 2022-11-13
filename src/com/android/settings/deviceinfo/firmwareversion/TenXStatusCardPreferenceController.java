/*
 * Copyright (C) 2020-2022, TenX-OS
*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.deviceinfo.firmwareversion;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.SystemProperties;
import android.util.TypedValue;
import android.widget.RelativeLayout;

import androidx.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.widget.LayoutPreference;

public class TenXStatusCardPreferenceController extends AbstractPreferenceController {

    private static final String KEY_STATUS_INFO = "status_info";

    public TenXStatusCardPreferenceController(Context context) {
        super(context);
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        final LayoutPreference StatusInfo = screen.findPreference(KEY_STATUS_INFO);
        final RelativeLayout mStatusLayout = StatusInfo.findViewById(R.id.status_info_card);

        int strokeWidth = 6;
        int strokeColor = getThemeAccentColor(mContext);
        int bgColor = getThemeAccentColor(mContext);
        GradientDrawable gD = new GradientDrawable();
        gD.setColor(getColorWithAlpha(bgColor, 0.2f));
        gD.setShape(GradientDrawable.RECTANGLE);
        gD.setStroke(strokeWidth, strokeColor);
        gD.setCornerRadius(25);
        mStatusLayout.setBackground(gD);
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

    public static int getThemeAccentColor (final Context context) {
        final TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.colorAccent, value, true);
        return value.data;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String getPreferenceKey() {
        return KEY_STATUS_INFO;
    }
}
