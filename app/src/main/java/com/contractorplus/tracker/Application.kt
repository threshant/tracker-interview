package com.contractorplus.tracker

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Application: android.app.Application() {
    companion object{
        const val CHANNEL_ID = "locationServiceChannel"
        const val SERVICE_COMMAND = "COMMAND"
        const val SERVICE_START_COMMAND = "START"
        const val SERVICE_STOP_COMMAND = "STOP"
        var database: FirebaseDatabase? = null
        fun getDatabaseReference(): com.google.firebase.database.DatabaseReference {
            if(database == null){
                database = FirebaseDatabase.getInstance()
                database!!.setPersistenceEnabled(true)
            }
            val ref = database!!.getReference("locations/${FirebaseAuth.getInstance().currentUser!!.uid}")
            return ref
        }

    }
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }
    fun createNotificationChannel(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val serviceChannel = android.app.NotificationChannel(
                CHANNEL_ID,
                "Location Service Channel",
                android.app.NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(android.app.NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }
}