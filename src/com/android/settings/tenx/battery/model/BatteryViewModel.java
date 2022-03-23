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
package com.android.settings.tenx.battery.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BatteryViewModel extends ViewModel {
    private final MutableLiveData<Float> batteryLevel = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isCharging = new MutableLiveData<>();

    public LiveData<Float> getBatteryLevel() {
        return batteryLevel;
    }

    public LiveData<Boolean> getIsCharging() {
        return isCharging;
    }

    public void updateBatteryLevel(float level, boolean charging) {
        batteryLevel.setValue(level);
        isCharging.setValue(charging);
    }
}
