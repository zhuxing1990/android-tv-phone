package com.vunke.videochat.tools

import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils
import com.vunke.videochat.R

/**
 * Created by zhuxi on 2019/11/21.
 */
object FocusUtil {
     fun setFocus(hasFocus: Boolean, v: View, context: Context) {
        if (hasFocus) {
            val animation = AnimationUtils.loadAnimation(context, R.anim.anim_scale_big)
            v.setAnimation(animation)
            v.bringToFront()
        } else {
            val animation = AnimationUtils.loadAnimation(context, R.anim.anim_scale_small)
            v.startAnimation(animation)
        }
    }
}