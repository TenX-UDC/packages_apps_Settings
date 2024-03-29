/*
 * Copyright (C) 2019 The Android Open Source Project
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
 * limitations under the License
 */

package com.android.settings.biometrics.face;

import static com.android.settings.Utils.SETTINGS_PACKAGE_NAME;

import android.content.Context;
import android.content.Intent;

import androidx.preference.Preference;

import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.password.ChooseLockSettingsHelper;
import com.android.settingslib.widget.LayoutPreference;

import com.google.android.setupdesign.util.PartnerStyleHelper;

/**
 * Preference controller that allows a user to enroll their face.
 */
public class FaceSettingsEnrollButtonPreferenceController extends BasePreferenceController
        implements Preference.OnPreferenceClickListener {

    private static final String TAG = "FaceSettings/Remove";
    static final String KEY = "security_settings_face_enroll_faces_container";

    private final Context mContext;

    private int mUserId;
    private byte[] mToken;
    private boolean mIsClicked;
    private Listener mListener;

    public FaceSettingsEnrollButtonPreferenceController(Context context) {
        this(context, KEY);
    }

    public FaceSettingsEnrollButtonPreferenceController(Context context, String preferenceKey) {
        super(context, preferenceKey);
        mContext = context;
    }

    @Override
    public void updateState(Preference preference) {
        super.updateState(preference);
        preference.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        // If it's in multi window mode, do not start the introduction intent.
        if (mListener != null && mListener.onShowSplitScreenDialog()) {
            return false;
        }

        mIsClicked = true;
        final Intent intent = new Intent();
        intent.setClassName(SETTINGS_PACKAGE_NAME, FaceEnrollIntroduction.class.getName());
        intent.putExtra(Intent.EXTRA_USER_ID, mUserId);
        intent.putExtra(ChooseLockSettingsHelper.EXTRA_KEY_CHALLENGE_TOKEN, mToken);
        if (mListener != null) {
            mListener.onStartEnrolling(intent);
        } else {
            mContext.startActivity(intent);
        }
        return true;
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE;
    }

    public void setUserId(int userId) {
        mUserId = userId;
    }

    public void setToken(byte[] token) {
        mToken = token;
    }

    // Return the click state, then clear its state.
    public boolean isClicked() {
        final boolean wasClicked = mIsClicked;
        mIsClicked = false;
        return wasClicked;
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    /**
     * Interface for registering callbacks related to the face enroll preference button.
     */
    public interface Listener {
        /**
         * Called to check whether to show dialog in split screen mode
         * @return Whether split screen warning dialog shown.
         */
        boolean onShowSplitScreenDialog();

        /**
         * Called when the user has indicated an intent to begin enrolling a new face.
         * @param intent The Intent that should be used to launch face enrollment.
         */
        void onStartEnrolling(Intent intent);
    }
}
