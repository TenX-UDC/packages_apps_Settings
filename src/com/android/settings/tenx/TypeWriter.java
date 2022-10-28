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
package com.android.settings.tenx;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.android.settings.Utils;

public class TypeWriter extends TextView {

    private Context mContext;
    private Handler mHandler;

    public TypeWriter(Context context) {
        this(context, null);
    }

    public TypeWriter(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TypeWriter(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        final Resources resources = getResources();
        mContext = context;
        setWriterText();
        setTextGravity();
    }

    private void setWriterText() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setText("#");
            }
        }, 300);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                append(" ");
            }
        }, 400);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                append("T");
            }
        }, 500);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                append("h");
            }
        }, 600);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                append("e");
            }
        }, 700);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                append(" ");
            }
        }, 800);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                append("C");
            }
        }, 900);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                append("u");
            }
        }, 1000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                append("s");
            }
        }, 1100);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                append("t");
            }
        }, 1200);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                append("o");
            }
        }, 1300);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                append("m");
            }
        }, 1400);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                append(" ");
            }
        }, 1500);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                append("R");
            }
        }, 1600);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                append("O");
            }
        }, 1700);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                append("M");
            }
        }, 1800);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                append(" ");
            }
        }, 1900);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                append("W");
            }
        }, 2000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                append("e");
            }
        }, 2100);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                append(" ");
            }
        }, 2200);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                append("L");
            }
        }, 2300);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                append("o");
            }
        }, 2400);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                append("v");
            }
        }, 2500);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                append("e");
            }
        }, 2600);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                append("!");
            }
        }, 2700);
        setTextColor(Utils.getColorAttr(mContext, android.R.attr.textColorPrimary));
    }

    private void setTextGravity() {
        setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
    }
}
