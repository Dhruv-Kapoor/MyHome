package com.example.myhome

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myhome.adapters.DeviceAdapter
import com.example.myhome.customViews.AdjustFanDialog
import com.example.myhome.customViews.AdjustLightDialog
import com.example.myhome.customViews.GridSpacesItemDecoration
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragmet_room.*

private const val TAG = "RoomFragment"
private const val KEY_ROOM_NAME = "Room Name"
class RoomFragment(
    name: String = ""
): Fragment(), DeviceAdapter.DeviceAdapterCallbacks, DialogCallbacks {

    private var callback: RoomFragmentCallback? = null
    private var roomName = name

    private var mainActivityViewModel: MainActivityViewModel? = null

    private val myDeviceIds = HashSet<String>()
    private val myDevices = ArrayList<Device>()

    private val adapter by lazy {
        DeviceAdapter(this, myDevices)
    }

    private val database by lazy{
        FirebaseDatabase.getInstance().reference.child("user1")
    }

    private val roomObserver by lazy {
        Observer<ArrayList<String>>{ tabs->
            if(tabs.contains(roomName)){
                mainActivityViewModel?.getRoom(roomName)?.observe(this, {
                    it.forEach { id ->
                        if (!myDeviceIds.contains(id)) {
                            Log.d(TAG, "device added: ")
                            mainActivityViewModel?.getDevice(id)?.let { device ->
                                myDeviceIds.add(id)
                                myDevices.add(device)
                                adapter.notifyItemInserted(myDevices.size - 1)
                            }
                        }
                    }
                })
                removeRoomObserver()
            }
        }
    }

    private fun removeRoomObserver() {
        mainActivityViewModel?.getTabNames()?.removeObserver(roomObserver)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.fragmet_room, container, false
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if(savedInstanceState?.containsKey(KEY_ROOM_NAME)==true){
            roomName = savedInstanceState.getString(KEY_ROOM_NAME, "")
        }
        
        init()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(KEY_ROOM_NAME, roomName)
        super.onSaveInstanceState(outState)
    }

    private fun init() {
        Log.d(TAG, "init: ")
        mainActivityViewModel = ViewModelProvider(
            requireActivity(),
            ViewModelProvider.NewInstanceFactory()
        )[MainActivityViewModel::class.java]

        rvDevices.layoutManager = GridLayoutManager(requireContext(), 2)
        rvDevices.adapter = adapter
        rvDevices.addItemDecoration(GridSpacesItemDecoration(10f.dp().toInt(), 2))

        mainActivityViewModel?.getTabNames()?.observe(this, roomObserver)
    }

    private fun Float.dp(): Float = this * requireContext().resources.displayMetrics.density

    override fun onDeviceClicked(position: Int) {
        val device = myDevices[position]
        when(device.type){
            Device.TYPE_LIGHT->{
                fragmentManager?.let { AdjustLightDialog(this, position, device).show(it, null) }
            }
            Device.TYPE_FAN->{
                fragmentManager?.let { AdjustFanDialog(this, position, device).show(it, null) }
            }
        }
    }

    override fun onPowerButtonClicked(position: Int) {
        val device = myDevices[position]
        device.on = !device.on
        callback?.onItemChanged(position)
        database.child(device.id).child("on").setValue(device.on)
    }

    override fun onFavouriteClicked(position: Int) {
        TODO("Not yet implemented")
    }

    override fun onDeviceLevelChange(position: Int) {
        callback?.onItemChanged(position)
        val device = myDevices[position]
        database.child(device.id).child("level").setValue(device.level)
    }

    fun setRoomFragmentCallback(callback: RoomFragmentCallback){
        this.callback = callback
    }

    fun onItemChanged(position: Int){
        adapter.notifyItemChanged(position)
    }

    interface RoomFragmentCallback{
        fun onItemChanged(position: Int)
    }

    fun getRoomName() = roomName

}

interface DialogCallbacks{
    fun onPowerButtonClicked(position: Int)
    fun onDeviceLevelChange(position: Int)
}