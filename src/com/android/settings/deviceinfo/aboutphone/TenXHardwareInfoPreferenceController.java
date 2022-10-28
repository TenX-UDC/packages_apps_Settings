/*
 * Copyright (C) 2020-2021 TenX-OS
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

package com.android.settings.deviceinfo.aboutphone;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.view.Display;
import android.view.WindowManager;
import android.util.DisplayMetrics;
import android.graphics.Point;
import android.widget.TextView;

import androidx.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.widget.LayoutPreference;

import com.android.internal.util.MemInfoReader;

import java.io.File;

public class TenXHardwareInfoPreferenceController extends AbstractPreferenceController {

    private static final String KEY_TENX_HARDWARE_INFO = "tenx_hardware_info";
    static String aproxStorage;

    public TenXHardwareInfoPreferenceController(Context context) {
        super(context);
    }

    public static String getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        double total = (totalBlocks * blockSize)/ 1073741824;
        int lastval = (int) Math.round(total);
            if ( lastval > 0  && lastval <= 16){
                aproxStorage = "16";
            } else if (lastval > 16 && lastval <=32) {
                aproxStorage = "32";
            } else if (lastval > 32 && lastval <=64) {
                aproxStorage = "64";
            } else if (lastval > 64 && lastval <=128) {
                aproxStorage = "128";
            } else if (lastval > 128 && lastval <= 256) {
                aproxStorage = "256";
            } else if (lastval > 256 && lastval <= 512) {
                aproxStorage = "512";
            } else if (lastval > 512) {
                aproxStorage = "512+";
            } else aproxStorage = "null";
        return aproxStorage;
    }

    public static int getTotalRAM() {
        MemInfoReader memReader = new MemInfoReader();
        memReader.readMemInfo();
        String aproxStorage;
        double totalmem = memReader.getTotalSize();
        double gb = (totalmem / 1073741824) + 0.1f; // Cause 4gig devices show memory as 3.48 .-.
        int gigs = (int) Math.round(gb);
        return gigs;
    }

    public static String getScreenRes(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y + getNavigationBarHeight(windowManager);
        return width + "x" + height;
    }

    private static int getNavigationBarHeight(WindowManager wm) {
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        wm.getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight)
            return realHeight - usableHeight;
        else
            return 0;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        final LayoutPreference TenXHardwareInfoPreference = screen.findPreference(KEY_TENX_HARDWARE_INFO);
        final TextView storage = (TextView) TenXHardwareInfoPreference.findViewById(R.id.storage_summary);
        final TextView resolution = (TextView) TenXHardwareInfoPreference.findViewById(R.id.resolution_summary);

        storage.setText(String.valueOf(getTotalInternalMemorySize()) + "GB + " + String.valueOf(getTotalRAM()) + "GB");
        resolution.setText(getScreenRes(mContext));
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String getPreferenceKey() {
        return KEY_TENX_HARDWARE_INFO;
    }
}
