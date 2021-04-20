package com.example.myhome.customViews

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.example.myhome.Device
import com.example.myhome.DialogCallbacks
import com.example.myhome.R
import kotlinx.android.synthetic.main.dialog_adjust_light.*

class AdjustLightDialog(
    private val dialogCallbacks: DialogCallbacks,
    private val position: Int,
    private val device: Device
): DialogFragment(), MySlider.OnLevelChangeCallback{

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(
            R.layout.dialog_adjust_light, container, false
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        slider.setOnLevelChangeCallback(this)

        ivPowerBtn.setOnClickListener {
            dialogCallbacks.onPowerButtonClicked(position)
            refreshPowerButton()
            refreshBulbAnimation()
        }

        init()
    }

    private fun refreshBulbAnimation() {
        if(device.on){
            bulbAnimation.setLevel(device.level)
        }else{
            bulbAnimation.setLevel(0)
        }
    }

    private fun init() {
        tvName.text = device.name
        slider.setLevel(device.level)
        refreshPowerButton()
        refreshBulbAnimation()
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
        bulbAnimation.setLevel(level)
        dialogCallbacks.onDeviceLevelChange(position)
    }
}