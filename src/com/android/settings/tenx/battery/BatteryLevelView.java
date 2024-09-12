	/*
 * Copyright (C) 2020-2024 TenX-OS
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
package com.android.settings.tenx.battery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.BatteryManager;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.graphics.ColorUtils;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.android.settings.tenx.battery.model.BatteryViewModel;

public class BatteryLevelView extends View {

    private static final String TAG = "BatteryLevelView";

    private float batteryLevel = 100;
    private final Paint paint = new Paint();
    private BatteryViewModel batteryViewModel;

    private final BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (batteryBarEnabled()) {
                setVisibility(View.VISIBLE);
                fetchBatteryLevel(context);
            } else {
                setVisibility(View.GONE);
            }
        }
    };

    public BatteryLevelView(Context context) {
        super(context);
        init(context);
    }

    public BatteryLevelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BatteryLevelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        batteryViewModel = new ViewModelProvider((ViewModelStoreOwner) getContext()).get(BatteryViewModel.class);
        paint.setStyle(Paint.Style.FILL);

        if (batteryBarEnabled()) {
            setVisibility(View.VISIBLE);
            fetchBatteryLevel(context);
        } else {
            setVisibility(View.GONE);
        }
    }

    private void fetchBatteryLevel(Context context) {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, filter);

        if (batteryStatus != null) {
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            if (scale > 0) {
                batteryLevel = level / (float) scale * 100;
            } else {
                batteryLevel = 0;
            }
            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;

            invalidate();
            batteryViewModel.updateBatteryLevel(batteryLevel, isCharging);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        float cornerRadius = 20;

        RectF backgroundRect = new RectF(0, 0, width, height);

        paint.setColor(Color.GRAY);
        canvas.drawRoundRect(backgroundRect, cornerRadius, cornerRadius, paint);

        int foregroundWidth = (int) (width * (batteryLevel / 100f));

        RectF foregroundRect = new RectF(0, 0, foregroundWidth, height);

        paint.setColor(getColorForBatteryLevel(batteryLevel));
        canvas.drawRoundRect(foregroundRect, cornerRadius, cornerRadius, paint);
    }

    private int getColorForBatteryLevel(float batteryLevel) {
        boolean useBatteryLevelColor = Settings.System.getIntForUser(getContext().getContentResolver(),
                Settings.System.SETTINGS_TENX_DASHBOARD_BATTERY_LEVEL_COLOR, 0,
                UserHandle.USER_CURRENT) != 0;
        int eightyPctColor = Settings.System.getIntForUser(getContext().getContentResolver(),
                Settings.System.SETTINGS_TENX_DASHBOARD_BATTERY_LEVEL_COLOR_EIGHTY, 0xff0FFF50,
                UserHandle.USER_CURRENT);
        int sixtyPctColor = Settings.System.getIntForUser(getContext().getContentResolver(),
                Settings.System.SETTINGS_TENX_DASHBOARD_BATTERY_LEVEL_COLOR_SIXTY, 0xff0000FF,
                UserHandle.USER_CURRENT);
        int fourtyPctColor = Settings.System.getIntForUser(getContext().getContentResolver(),
                Settings.System.SETTINGS_TENX_DASHBOARD_BATTERY_LEVEL_COLOR_FOURTY, 0xffFFEA00,
                UserHandle.USER_CURRENT);
        int twentyPctColor = Settings.System.getIntForUser(getContext().getContentResolver(),
                Settings.System.SETTINGS_TENX_DASHBOARD_BATTERY_LEVEL_COLOR_TWENTY, 0xffEE4B2B,
                UserHandle.USER_CURRENT);
        int batteryLevelColorAlpha = Settings.System.getIntForUser(getContext().getContentResolver(),
                Settings.System.SETTINGS_TENX_DASHBOARD_BATTERY_LEVEL_COLOR_ALPHA, 255,
                UserHandle.USER_CURRENT);
        if (!useBatteryLevelColor) {
            return Color.parseColor("#BFFFFFFF");
        }

        if (batteryLevel < 20) return ColorUtils.setAlphaComponent(twentyPctColor, batteryLevelColorAlpha);
        if (batteryLevel < 40) return ColorUtils.setAlphaComponent(fourtyPctColor, batteryLevelColorAlpha);
        if (batteryLevel < 60) return ColorUtils.setAlphaComponent(sixtyPctColor, batteryLevelColorAlpha);
        if (batteryLevel < 80) return ColorUtils.setAlphaComponent(eightyPctColor, batteryLevelColorAlpha);
        return Color.parseColor("#BFFFFFFF");
    }

    private boolean batteryBarEnabled() {
         return Settings.System.getIntForUser(getContext().getContentResolver(),
                Settings.System.SETTINGS_TENX_DASHBOARD_BATTERY_BAR, 0, UserHandle.USER_CURRENT) != 0;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        getContext().registerReceiver(batteryReceiver, filter);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getContext().unregisterReceiver(batteryReceiver);
    }
}
