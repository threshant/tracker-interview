package com.contractorplus.tracker.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.contractorplus.tracker.Application
import com.contractorplus.tracker.model.LocationInfo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener

class MainActivityViewModel: ViewModel() {
    private val locationsMutableLiveData = MutableLiveData<ArrayList<LocationInfo>>()
    private val switchState = MutableLiveData<Boolean>()
    private val databaseReference = Application.getDatabaseReference()
    val _speed = MutableLiveData<String>()
    val _latitude = MutableLiveData<String>()
    val _longitude = MutableLiveData<String>()
    init{
        switchState.value = false
        getLocationUpdatesFromFirebase()
        _speed.value = "0.0"
        _latitude.value = "0.0"
        _longitude.value = "0.0"
    }
    fun getLocationUpdatesFromFirebase(){
        databaseReference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val itemType = object : GenericTypeIndicator<HashMap<String, Any>>() {}
                if(snapshot.value is HashMap<*, *> == false){
                    return
                }
                val itemList = snapshot.value as HashMap<String, Any>
                val items = ArrayList<LocationInfo>()
                itemList.forEach { (_, value) ->
                    val itemData = value as? HashMap<String, String>
                    Log.d("MainActivityViewModel", itemData.toString())
                    Log.d("MainActivityViewModel", itemData!!.values.toString())
                    val item = itemData!!.values
                    items.add(LocationInfo(item.elementAt(0), item.elementAt(1), item.elementAt(2), item.elementAt(3), item.elementAt(4)))
                }
                locationsMutableLiveData.value = items
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    fun locations(): MutableLiveData<ArrayList<LocationInfo>>{
        return locationsMutableLiveData
    }
    fun switchState(): MutableLiveData<Boolean>{
        return switchState
    }

    fun setSwitchState(state: Boolean){
        switchState.value = state
    }
}