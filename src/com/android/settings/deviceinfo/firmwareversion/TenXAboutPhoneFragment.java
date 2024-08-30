/*
 * Copyright (C) 2024 TenX-OS
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
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.BidiFormatter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.settings.R;
import com.android.settings.Utils;

import com.android.settingslib.DeviceInfoUtils;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;

public class TenXAboutPhoneFragment extends Fragment {

    private static final String BASEBAND_PROPERTY = "gsm.version.baseband";
    private static final String KEY_BUILD_DATE_PROP = "ro.build.date";
    private static final String PROP_TENX_VERSION = "org.tenx_version";
    private static final String PROP_TENX_VERSION_CODE = "org.tenx.fanbase_name";
    private static final String PROP_TENX_BUILDTYPE = "org.tenx.build_type";
    private static final String PROP_TENX_DEVICE_CODENAME = "org.tenx.device";
    private static final String PROP_TENX_DEVICE = "ro.product.system.brand";

    private static final int DELAY_TIMER_MILLIS = 500;
    private static final int ACTIVITY_TRIGGER_COUNT = 3;

    private UserManager mUserManager;
    private long[] mHits = new long[ACTIVITY_TRIGGER_COUNT];

    private RestrictedLockUtils.EnforcedAdmin mFunDisallowedAdmin;
    private boolean mFunDisallowedBySystem;

    private ImageView mBuildTypeIcon;
    private LinearLayout mAndroidVersionContainer;

    private TextView mTenXOSVersionSummary;
    private TextView mTenXOSRomTagSummary;
    private TextView mTenXOSDeviceSummary;
    private TextView mTenXOSDeviceCodenameSummary;
    private TextView mTenXOSMaintainerSummary;
    private TextView mTenXOSBuildTypeSummary;
    private TextView mAndroidVersionSummary;
    private TextView mAndroidSecurityPatchSummary;
    private TextView mBasebandVersionSummary;
    private TextView mKernelVersionSummary;
    private TextView mBuildDateSummary;
    private TextView mBuildNumberSummary;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tenx_about_phone, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUserManager = (UserManager) getContext().getSystemService(Context.USER_SERVICE);
        initializeAdminPermissions();

        if (getActivity() != null) {
            getActivity().setTitle(R.string.firmware_version);
        }

        mBuildTypeIcon = view.findViewById(R.id.tenx_build_type_icon);
        mAndroidVersionContainer = view.findViewById(R.id.android_version_container);

        mTenXOSVersionSummary = view.findViewById(R.id.tenx_version_summary);
        mTenXOSRomTagSummary = view.findViewById(R.id.tenx_rom_tag_summary);
        mTenXOSDeviceSummary = view.findViewById(R.id.tenx_device_summary);
        mTenXOSDeviceCodenameSummary = view.findViewById(R.id.tenx_device_codename_summary);
        mTenXOSMaintainerSummary = view.findViewById(R.id.tenx_maintainer_summary);
        mTenXOSBuildTypeSummary = view.findViewById(R.id.tenx_build_type_summary);
        mAndroidVersionSummary = view.findViewById(R.id.android_version_summary);
        mAndroidSecurityPatchSummary = view.findViewById(R.id.android_security_update_summary);
        mBasebandVersionSummary = view.findViewById(R.id.baseband_version_summary);
        mKernelVersionSummary = view.findViewById(R.id.kernel_version_summary);
        mBuildDateSummary = view.findViewById(R.id.build_date_summary);
        mBuildNumberSummary = view.findViewById(R.id.build_number_summary);

        mAndroidVersionContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFunActivity();
            }
        });

        String tenxBuildType = SystemProperties.get(PROP_TENX_BUILDTYPE,
                getContext().getString(R.string.device_info_default));

        mTenXOSVersionSummary.setText(getTenXVersion());
        mTenXOSRomTagSummary.setText(getRomTag());
        mTenXOSDeviceSummary.setText(getDeviceName());
        mTenXOSDeviceCodenameSummary.setText(getDeviceCodename());
        mTenXOSMaintainerSummary.setText(getContext().getString(R.string.maintainer_name));
        mTenXOSBuildTypeSummary.setText(tenxBuildType);
        mAndroidVersionSummary.setText(getAndroidVersion());
        mAndroidSecurityPatchSummary.setText(DeviceInfoUtils.getSecurityPatch());
        mBasebandVersionSummary.setText(getBasebandVersion());
        mKernelVersionSummary.setText(DeviceInfoUtils.getFormattedKernelVersion(getContext()));
        mBuildDateSummary.setText(getBuildDate());
        mBuildNumberSummary.setText(getBuildNumber());

        if (tenxBuildType.equalsIgnoreCase("Official")) {
            mBuildTypeIcon.setImageResource(R.drawable.ic_build_type);
        } else {
            mBuildTypeIcon.setImageResource(R.drawable.ic_unofficial);
        }
    }

    public CharSequence getAndroidVersion() {
        return Build.VERSION.RELEASE_OR_PREVIEW_DISPLAY;
    }

    public CharSequence getBasebandVersion() {
        String baseband = SystemProperties.get(BASEBAND_PROPERTY,
                getContext().getString(R.string.device_info_default));
        for (String str : baseband.split(",")) {
            if (!TextUtils.isEmpty(str)) {
                return str;
            }
        }
        return baseband;
    }

    public CharSequence getBuildDate() {
        return SystemProperties.get(KEY_BUILD_DATE_PROP,
                getContext().getString(R.string.unknown));
    }

    public CharSequence getBuildNumber() {
        return BidiFormatter.getInstance().unicodeWrap(Build.DISPLAY);
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
                getContext().getString(R.string.device_info_default));

        return version;
    }

    private String getRomTag() {
        final String versionCode = SystemProperties.get(PROP_TENX_VERSION_CODE,
                getContext().getString(R.string.device_info_default));

        return versionCode;
    }

    private boolean showFunActivity() {
        if (Utils.isMonkeyRunning()) {
            return false;
        }
        arrayCopy();
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
        if (mHits[0] >= (SystemClock.uptimeMillis() - DELAY_TIMER_MILLIS)) {
            if (mUserManager.hasUserRestriction(UserManager.DISALLOW_FUN)) {
                if (mFunDisallowedAdmin != null && !mFunDisallowedBySystem) {
                    RestrictedLockUtils.sendShowAdminSupportDetailsIntent(getContext(),
                            mFunDisallowedAdmin);
                }
                return true;
            }

            final Intent intent = new Intent(Intent.ACTION_MAIN)
                    .setClassName(
                            "android", com.android.internal.app.PlatLogoActivity.class.getName())
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                getContext().startActivity(intent);
            } catch (Exception e) {
            }
        }
        return true;
    }

    void arrayCopy() {
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
    }

    void initializeAdminPermissions() {
        mFunDisallowedAdmin = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(
                getContext(), UserManager.DISALLOW_FUN, UserHandle.myUserId());
        mFunDisallowedBySystem = RestrictedLockUtilsInternal.hasBaseUserRestriction(
                getContext(), UserManager.DISALLOW_FUN, UserHandle.myUserId());
    }
}
