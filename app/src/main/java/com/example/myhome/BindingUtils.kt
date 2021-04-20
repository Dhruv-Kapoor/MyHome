package com.example.myhome

import android.widget.ImageView
import androidx.databinding.BindingAdapter

object BindingUtils {

    @BindingAdapter("setFanLevel")
    @JvmStatic
    fun setFanLevel(iv: ImageView, device: Device) {
        if (device.on) {
            if (device.level > 0) {
                iv.setColorFilter(iv.context.getColor(R.color.green))
            } else {
                iv.setColorFilter(iv.context.getColor(R.color.grey))
            }
        } else {
            iv.setColorFilter(iv.context.getColor(R.color.grey))
        }
    }

    @BindingAdapter("isOn")
    @JvmStatic
    fun setIsOn(iv: ImageView, isOn: Boolean) {
        if (isOn) {
            iv.setColorFilter(iv.context.getColor(R.color.green))
        } else {
            iv.setColorFilter(iv.context.getColor(R.color.grey))
        }
    }

    @BindingAdapter("setLightLevel")
    @JvmStatic
    fun setLightLevel(iv: ImageView, device: Device) {
        if (device.on && device.level > 0) {
            iv.setImageResource(R.drawable.bulb_on)
        } else {
            iv.setImageResource(R.drawable.bulb_off)
        }
    }
}