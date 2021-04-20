package com.example.myhome

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myhome.adapters.RoomAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), RoomFragment.RoomFragmentCallback {

    companion object {
        private const val TAG = "MainActivity"
        const val ROOM_ALL = "All"
        const val ROOM_FAVOURITE = "Favourites"
    }

    private val favourites = ArrayList<String>()
    private val tabNames = ArrayList<String>()

    private val fragmentsMap = LinkedHashMap<String, RoomFragment>()
    private val fragmentsList = ArrayList<RoomFragment>()

    private lateinit var viewModel: MainActivityViewModel

    private val roomAdapter by lazy {
        RoomAdapter(fragmentsList, supportFragmentManager, lifecycle)
    }

    private val database by lazy {
        FirebaseDatabase.getInstance().reference.child("user1")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()

        loadDevices()

        viewModel.getTabNames().observe(this, {
            it.forEach { name ->
                if (!tabNames.contains(name)) {
                    addNewTab(name, savedInstanceState)
                    if(name== ROOM_FAVOURITE){
                        tabLayout.getTabAt(1)?.select()
                    }
                }
            }
        })

    }

    private fun addNewTab(name: String, savedInstanceState: Bundle?) {
        tabNames.add(name)

        //Add new Tab
        val tab = tabLayout.newTab()
        tabLayout.addTab(tab)

        //Add new Fragment
        val fragment = if(savedInstanceState?.containsKey(name) == true){
            supportFragmentManager.getFragment(savedInstanceState, name) as RoomFragment
        }else{
            RoomFragment(name)
        }
        fragment.setRoomFragmentCallback(this)
        fragmentsMap[name] = fragment
        fragmentsList.add(fragment)

        roomAdapter.notifyItemInserted(tabNames.size - 1)
    }

    private fun init() {
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[MainActivityViewModel::class.java]

        viewPager.adapter = roomAdapter
        TabLayoutMediator(tabLayout, viewPager, true) { tab: TabLayout.Tab, i: Int ->
            tab.text = tabNames[i]
        }.attach()

        viewModel.init()

    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        fragmentsMap.keys.forEach {
            supportFragmentManager.putFragment(outState, it, fragmentsMap[it] as Fragment)
        }
    }

    private fun loadDevices() {
        database.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val device = snapshot.getValue(Device::class.java)
                if (device != null) {
                    Log.d(TAG, "onChildAdded: $device")
                    viewModel.addDevice(device)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildChanged: $snapshot")
                val device = snapshot.getValue(Device::class.java)
                if (device != null) {
                    viewModel.onValueChanged(device)
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    override fun onItemChanged(position: Int) {
        fragmentsList.forEach {
            it.onItemChanged(position)
        }
    }
}