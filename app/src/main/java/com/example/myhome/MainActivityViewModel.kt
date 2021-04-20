package com.example.myhome

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myhome.MainActivity.Companion.ROOM_ALL
import com.example.myhome.MainActivity.Companion.ROOM_FAVOURITE

class MainActivityViewModel : ViewModel() {

    private val _devices = HashMap<String, Device>()
    private val _rooms = LinkedHashMap<String, ArrayList<String>>()
    private val _favourites = HashSet<String>()
    private val _tabNames = ArrayList<String>()

//    private val devices = MutableLiveData<HashMap<String, Device>>()
    private val favourites = MutableLiveData<HashSet<String>>()
    private val tabNames = MutableLiveData<ArrayList<String>>()

    private val rooms = LinkedHashMap<String, MutableLiveData<ArrayList<String>>>()

//    fun getDevices() = devices as LiveData<HashMap<String, Device>>
    fun getRoom(name: String) = rooms[name] as LiveData<ArrayList<String>>?
    fun getFavourites() = favourites as LiveData<HashSet<String>>
    fun getTabNames() = tabNames as LiveData<ArrayList<String>>

    fun init(){
        addNewRoom(ROOM_ALL)
        addNewRoom(ROOM_FAVOURITE)
    }

    fun addDevice(device: Device) {
        if (!_devices.containsKey(device.id)) {
            _devices[device.id] = device

            val allRoom = _rooms[ROOM_ALL]
            allRoom?.add(device.id)
            rooms[ROOM_ALL]?.postValue(allRoom)

            if (!rooms.containsKey(device.room)) {
                addNewRoom(device.room)
            }
            val room = _rooms[device.room]
            room?.add(device.id)
            rooms[device.room]?.postValue(room)

            //TODO remove this
            val favouriteRoom = _rooms[ROOM_FAVOURITE]
            favouriteRoom?.add(device.id)
            rooms[ROOM_FAVOURITE]?.postValue(favouriteRoom)
        }
    }

    private fun addNewRoom(name: String) {
        if(!rooms.containsKey(name)){
            rooms[name] = MutableLiveData()
            val t = ArrayList<String>()
            _rooms[name] = t
            rooms[name]?.postValue(t)

            _tabNames.add(name)
            tabNames.postValue(_tabNames)
        }
    }


    fun addFavourite(deviceId: String) {
        if (!_favourites.contains(deviceId)) {
            _favourites.add(deviceId)
            favourites.postValue(_favourites)
        }
    }

    fun removeFavourite(deviceId: String) {
        if(_favourites.contains(deviceId)){
            _favourites.remove(deviceId)
            favourites.postValue(_favourites)
        }
    }

    fun getDevice(deviceId: String): Device? = _devices[deviceId]
    fun onValueChanged(device: Device) {
        _devices[device.id]?.apply {
            on = device.on
            level = device.level
        }

    }
}