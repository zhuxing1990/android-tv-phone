package com.vunke.videochat.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class HorseRaceLampTextView extends TextView {
    private static final String TAG = "AlwaysMarqueeTextView";

    public HorseRaceLampTextView(Context context) {
        super(context);
    }

    public HorseRaceLampTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HorseRaceLampTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}