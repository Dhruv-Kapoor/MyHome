package com.example.myhome.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.myhome.Device
import com.example.myhome.R
import com.example.myhome.databinding.FanDeviceViewBinding
import com.example.myhome.databinding.LightDeviceViewBinding
import kotlinx.android.synthetic.main.fan_device_view.view.*

private const val TAG = "DeviceAdapter"

class DeviceAdapter(
    private val deviceAdapterCallbacks: DeviceAdapterCallbacks,
    private val list: ArrayList<Device>
) :
    RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {

        val inflate =
            { layout: Int -> LayoutInflater.from(parent.context).inflate(layout, parent, false) }

        return when (viewType) {
            TYPE_FAN -> {
                FanViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.fan_device_view,
                        parent,
                        false
                    )
                )
            }
            TYPE_LIGHT -> {
                LightViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.light_device_view,
                        parent,
                        false
                    )
                )
            }
            else -> {
                OtherViewHolder(inflate(R.layout.other_device_view))
            }
        }
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: ")
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int {
        return when (list[position].type) {
            Device.TYPE_FAN -> TYPE_FAN
            Device.TYPE_LIGHT -> TYPE_LIGHT
            else -> TYPE_OTHER
        }
    }

    companion object {
        private const val TYPE_OTHER = -1
        private const val TYPE_FAN = 1
        private const val TYPE_LIGHT = 2
    }

    abstract inner class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(device: Device)
    }

    inner class FanViewHolder(private val binding: FanDeviceViewBinding) :
        DeviceViewHolder(binding.root) {
        override fun bind(device: Device) {
            binding.device = device
            binding.executePendingBindings()
            with(itemView) {
                setOnClickListener {
                    deviceAdapterCallbacks.onDeviceClicked(adapterPosition)
                }
                ivPowerBtn.setOnClickListener {
                    deviceAdapterCallbacks.onPowerButtonClicked(adapterPosition)
                }
            }
        }
    }

    inner class LightViewHolder(private val binding: LightDeviceViewBinding) :
        DeviceViewHolder(binding.root) {
        override fun bind(device: Device) {
            binding.device = device
            binding.executePendingBindings()
            with(itemView) {
                setOnClickListener {
                    deviceAdapterCallbacks.onDeviceClicked(adapterPosition)
                }
                ivPowerBtn.setOnClickListener {
                    deviceAdapterCallbacks.onPowerButtonClicked(adapterPosition)
                }
            }
        }
    }

    inner class OtherViewHolder(private val itemView: View) : DeviceViewHolder(itemView) {
        override fun bind(device: Device) {

        }
    }

    interface DeviceAdapterCallbacks {
        fun onDeviceClicked(position: Int)
        fun onPowerButtonClicked(position: Int)
        fun onFavouriteClicked(position: Int)
    }
}
