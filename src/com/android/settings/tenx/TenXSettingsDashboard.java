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

package com.android.settings.tenx;

import android.content.Context;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.android.internal.util.tenx.Utils;

import com.android.settings.R;
import com.android.settings.tenx.battery.model.BatteryViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TenXSettingsDashboard extends RelativeLayout {

    private BatteryViewModel batteryViewModel;
    private LifecycleOwner lifecycleOwner;

    private boolean mHasNoSims;
    private boolean mIsWifiConnected;

    public TenXSettingsDashboard(Context context) {
        super(context);
        initializeLifecycleOwner(context);
    }

    public TenXSettingsDashboard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeLifecycleOwner(context);
    }

    public TenXSettingsDashboard(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeLifecycleOwner(context);
    }

    private void initializeLifecycleOwner(Context context) {
        if (context instanceof LifecycleOwner) {
            this.lifecycleOwner = (LifecycleOwner) context;
        } else if (getParent() instanceof LifecycleOwner) {
            this.lifecycleOwner = (LifecycleOwner) getParent();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setViews(getContext());
    }

    public void setViews(Context context) {
        CardView backgroundCard = (CardView) findViewById(R.id.rounded_card);
        ImageView blurImage = (ImageView) findViewById(R.id.blur_image);
        TextView greetingTitle = (TextView) findViewById(R.id.greetings_title);
        TextView greetingSummary = (TextView) findViewById(R.id.greeting_summary);
        TextView batteryLevel = (TextView) findViewById(R.id.battery_level_text);
        TextView day = (TextView) findViewById(R.id.day);
        TextView date = (TextView) findViewById(R.id.date);
        TextView cpuTemp = (TextView) findViewById(R.id.cpu_temp);
        TextView batteryTemp = (TextView) findViewById(R.id.battery_temp);

        batteryViewModel = new ViewModelProvider((ViewModelStoreOwner) lifecycleOwner).get(BatteryViewModel.class);

        boolean shown = Settings.System.getIntForUser(getContext().getContentResolver(),
                Settings.System.SETTINGS_TENX_DASHBOARD_ENABLED, 0,
                UserHandle.USER_CURRENT) != 0;
        int backgroundStyle = Settings.System.getIntForUser(getContext().getContentResolver(),
                Settings.System.SETTINGS_TENX_DASHBOARD_BACKGROUND, 0,
                UserHandle.USER_CURRENT);
        int cornerRadius = Settings.System.getIntForUser(getContext().getContentResolver(),
                Settings.System.SETTINGS_TENX_DASHBOARD_ROUND_IMAGE_RADIUS, 0,
                UserHandle.USER_CURRENT);
        float blurRadius = Settings.System.getFloatForUser(getContext().getContentResolver(),
                Settings.System.SETTINGS_TENX_DASHBOARD_BACKGROUND_BLUR, 0f,
                UserHandle.USER_CURRENT);
        String customName = Settings.System.getStringForUser(getContext().getContentResolver(),
                Settings.System.SETTINGS_TENX_DASHBOARD_CUSTOM_NAME,
                UserHandle.USER_CURRENT);
        int imageAlpha = Settings.System.getIntForUser(getContext().getContentResolver(),
                Settings.System.SETTINGS_TENX_DASHBOARD_IMAGE_ALPHA, 255, UserHandle.USER_CURRENT);
        boolean batteryBarEnabled = Settings.System.getIntForUser(getContext().getContentResolver(),
                Settings.System.SETTINGS_TENX_DASHBOARD_BATTERY_BAR, 0, UserHandle.USER_CURRENT) != 0;

        if (!shown) return;

        // Set corner radius in dp
        backgroundCard.setRadius(dpToPx(cornerRadius));

        // Set background style
        setBackgroundImage(backgroundStyle, blurImage);

        // Set image alpha
        blurImage.setImageAlpha(imageAlpha);

        // Set blur radius
        if (blurImage != null) {
            setBlur(blurImage, blurRadius);
        }

        // Set greetings
        setGreetingTitle(greetingTitle, customName);
        setGreetingSummary(greetingSummary);

        // Set battery status
        if (batteryBarEnabled) {
            batteryViewModel.getBatteryLevel().observe(lifecycleOwner, level -> {
                if (batteryViewModel.getIsCharging().getValue() != null) {
                    boolean isCharging = batteryViewModel.getIsCharging().getValue();
                    batteryLevel.setText(String.format(isCharging ? "⚡\uFE0F %d%%" : "%d%%", Math.round(level)));
                }
            });
        }

        // Set current day
        if (day != null) {
            day.setText(getDay());
        }

        // Set CPU temperature
        if (cpuTemp != null) {
            cpuTemp.setText(Utils.getCPUTemp(getContext()));
        }

        // set Battery temperature
        if (batteryTemp != null) {
            batteryTemp.setText(Utils.batteryTemperature(getContext(), false));
        }

        // Set date
        if (date != null) {
            date.setText(getDate());
        }
    }

    private void setBackgroundImage(int value, ImageView background) {
        if (background == null) return;
        switch (value) {
            case 1:
                background.setImageDrawable(getResources().getDrawable(R.drawable.tenx_settings_dashboard_background_image_1));
                break;
            case 2:
                background.setImageDrawable(getResources().getDrawable(R.drawable.tenx_settings_dashboard_background_image_2));
                break;
            case 3:
                background.setImageDrawable(getResources().getDrawable(R.drawable.tenx_settings_dashboard_background_image_3));
                break;
            case 4:
                background.setImageDrawable(getResources().getDrawable(R.drawable.tenx_settings_dashboard_background_image_4));
                break;
            case 5:
                background.setImageDrawable(getResources().getDrawable(R.drawable.tenx_settings_dashboard_background_image_5));
                break;
            case 6:
                background.setImageDrawable(getResources().getDrawable(R.drawable.tenx_settings_dashboard_background_image_6));
                break;
            case 7:
                background.setImageDrawable(getResources().getDrawable(R.drawable.tenx_settings_dashboard_background_image_7));
                break;
            case 8:
                background.setImageDrawable(getResources().getDrawable(R.drawable.tenx_settings_dashboard_background_image_8));
                break;
            case 9:
                background.setImageDrawable(getResources().getDrawable(R.drawable.tenx_settings_dashboard_background_image_9));
                break;
            case 10:
                background.setImageDrawable(getResources().getDrawable(R.drawable.tenx_settings_dashboard_background_image_10));
                break;
            case 11:
                background.setImageDrawable(getResources().getDrawable(R.drawable.tenx_settings_dashboard_background_image_11));
                break;
            case 12:
                background.setImageDrawable(getResources().getDrawable(R.drawable.tenx_settings_dashboard_background_image_12));
                break;
            case 13:
                background.setImageDrawable(getResources().getDrawable(R.drawable.tenx_settings_dashboard_background_image_13));
                break;
            case 14:
                background.setImageDrawable(getResources().getDrawable(R.drawable.tenx_settings_dashboard_background_image_14));
                break;
            case 15:
                background.setImageDrawable(getResources().getDrawable(R.drawable.tenx_settings_dashboard_background_image_15));
                break;
            case 0:
            default:
                background.setImageDrawable(getResources().getDrawable(R.drawable.tenx_settings_dashboard_background_image));
                break;
        }
    }

    private void setBlur(View view, Float blurRadius) {
        RenderEffect blurEffect = RenderEffect.createBlurEffect(blurRadius, blurRadius, Shader.TileMode.CLAMP );
        if (view != null) {
            view.setRenderEffect(blurEffect);
        }
    }

    private void setGreetingTitle(TextView greetingTitleView, String greetingTitle) {
        if (greetingTitleView == null) return;
        if (greetingTitle == null || greetingTitle == "") {
            greetingTitleView.setText(getResources().getString(R.string.greetings_title));
        } else {
            greetingTitleView.setText("Hello, " + greetingTitle);
        }
    }

    private void setGreetingSummary(TextView greetingSummary) {
        if (greetingSummary == null) return;
        Calendar calendar = Calendar.getInstance();
        int hours = calendar.get(Calendar.HOUR_OF_DAY);

        CharSequence greetingText;

        if (hours >= 0 && hours < 12) {
            greetingText = getResources().getString(R.string.morning_string);
        } else if (hours >= 12 && hours < 15) {
            greetingText = getResources().getString(R.string.afternoon_string);
        } else if (hours >= 15 && hours < 19) {
            greetingText = getResources().getString(R.string.evening_string);
        } else {
            greetingText = getResources().getString(R.string.night_string);
        }

        greetingSummary.setText(greetingText);
    }

    private String getDay() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE");
        Date date = new Date();
        return simpleDateFormat.format(date);
    }

    private String getDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return simpleDateFormat.format(new Date());
    }

    public float dpToPx(int dp) {
        return dp * getResources().getDisplayMetrics().density;
    }
}
