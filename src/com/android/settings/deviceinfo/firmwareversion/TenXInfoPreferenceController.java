/*
 * Copyright (C) 2020 Wave-OS
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
import android.os.Build;
import android.os.SystemProperties;
import android.widget.TextView;

import androidx.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.widget.LayoutPreference;

public class TenXInfoPreferenceController extends AbstractPreferenceController {

    private static final String KEY_TENX_INFO = "tenx_info";

    private static final String PROP_TENX_VERSION = "org.tenx_version";
    private static final String PROP_TENX_VERSION_CODE = "org.tenx.fanbase_name";
    private static final String PROP_TENX_BUILDTYPE = "org.tenx.build_type";
    private static final String PROP_TENX_DEVICE_CODENAME = "ro.product.model";
    private static final String PROP_TENX_DEVICE = "org.tenx.device";

    public TenXInfoPreferenceController(Context context) {
        super(context);
    }

    private String getDeviceName() {
        String device = SystemProperties.get(PROP_TENX_DEVICE, "");
        if (device.equals("")) {
            device = Build.MANUFACTURER;
        }
        return device;
    }

    private String getDeviceCodename() {
        String deviceCodename = SystemProperties.get(PROP_TENX_DEVICE_CODENAME, "");
        if (deviceCodename.equals("")) {
            deviceCodename = Build.MODEL;
        }
        return deviceCodename;
    }

    private String getTenXVersion() {
        final String version = SystemProperties.get(PROP_TENX_VERSION,
                this.mContext.getString(R.string.device_info_default));
        final String versionCode = SystemProperties.get(PROP_TENX_VERSION_CODE,
                this.mContext.getString(R.string.device_info_default));

        return version + " | " + versionCode;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        final LayoutPreference TenXInfoPreference = screen.findPreference(KEY_TENX_INFO);
        final TextView version = (TextView) TenXInfoPreference.findViewById(R.id.version_message);
        final TextView buildType = (TextView) TenXInfoPreference.findViewById(R.id.build_type_message);
        final TextView device = (TextView) TenXInfoPreference.findViewById(R.id.device_message);
        final TextView devCodename = (TextView) TenXInfoPreference.findViewById(R.id.device_codename_message);
        final String tenxVersion = getTenXVersion();
        final String tenxDevice = getDeviceName();
        final String deviceCodename = getDeviceCodename();
        final String tenxBuildType = SystemProperties.get(PROP_TENX_BUILDTYPE,
                this.mContext.getString(R.string.device_info_default));
        version.setText(tenxVersion);
        device.setText(tenxDevice);
        devCodename.setText(deviceCodename);
        buildType.setText(tenxBuildType);
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String getPreferenceKey() {
        return KEY_TENX_INFO;
    }
}
