package com.example.myhome.customViews

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.fragment.app.DialogFragment
import com.example.myhome.Device
import com.example.myhome.DialogCallbacks
import com.example.myhome.R
import kotlinx.android.synthetic.main.dialog_adjust_fan.*

class AdjustFanDialog(
    private val dialogCallbacks: DialogCallbacks,
    private val position: Int,
    private val device: Device
) : DialogFragment(), MySlider.OnLevelChangeCallback {

    private val animationMaxDuration = 2000L
    private val animationMinDuration = 100L

    private var isFanAnimating = false
    private val animation by lazy {
        RotateAnimation(
            0f,
            360f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        ).apply {
            repeatCount = Animation.INFINITE
            setInterpolator(requireContext(), android.R.anim.linear_interpolator)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(
            R.layout.dialog_adjust_fan, container, false
        )
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        slider.setOnLevelChangeCallback(this)

        ivPowerBtn.setOnClickListener {
            dialogCallbacks.onPowerButtonClicked(position)
            refreshPowerButton()
            refreshFanState()
        }

        init()
    }

    private fun init() {
        tvName.text = device.name
        slider.setLevel(device.level)
        refreshPowerButton()
        refreshFanState()
    }

    private fun refreshPowerButton() {
        if (device.on) {
            ivPowerBtn.setColorFilter(requireContext().getColor(R.color.green))
        } else {
            ivPowerBtn.setColorFilter(requireContext().getColor(R.color.grey))
        }
    }


    override fun onLevelChange(level: Int) {
        device.level = level
        refreshFanState()
        dialogCallbacks.onDeviceLevelChange(position)
    }

    private fun refreshFanState() {
        if (device.level == 0 || !device.on) {
            ivFan.clearAnimation()
            ivFan.setColorFilter(requireContext().getColor(R.color.grey))
            isFanAnimating = false
            return
        }
        animation.duration =
            ((100 - device.level) * animationMaxDuration / 100).coerceAtLeast(animationMinDuration)
        if (!isFanAnimating) {
            isFanAnimating = true
            ivFan.startAnimation(animation)
            ivFan.setColorFilter(requireContext().getColor(R.color.green))
        }
    }
}