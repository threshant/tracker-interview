package com.contractorplus.tracker.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.contractorplus.tracker.Application
import com.contractorplus.tracker.R
import com.contractorplus.tracker.model.LocationInfo
import com.google.android.gms.location.ActivityRecognitionClient
import com.google.android.gms.location.ActivityTransitionRequest


class LocationService: Service(), LocationListener {
    private lateinit var locationManager: LocationManager
    private var locationInfo: LocationInfo? = LocationInfo()
    private var isLocationUpdating = false
    private lateinit var activityRecognition: ActivityRecognitionClient
    private lateinit var activityTransitionRequest: ActivityTransitionRequest


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("LocationService", "Service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("LocationService", "Service started")
        startForeground(1, getNotification())
        startLocationUpdates()
        return START_STICKY
    }

    override fun onDestroy() {
        Log.d("LocationService", "Service destroyed")
        super.onDestroy()
        stopLocationUpdates()
        stopForeground(STOP_FOREGROUND_DETACH)
        stopSelf()

    }

    override fun onLocationChanged(p0: Location) {
        sendBroadcastToActivity(p0.latitude.toString(), p0.longitude.toString(), p0.speed.toString())
        if(p0.speed>0.0f && !isLocationUpdating){
            locationInfo!!.startX = p0.longitude.toString()
            locationInfo!!.startY = p0.latitude.toString()
            isLocationUpdating = true
        }
        else if (p0.speed == 0.0f && isLocationUpdating){
            locationInfo!!.endX = p0.longitude.toString()
            locationInfo!!.endY = p0.latitude.toString()
            locationInfo!!.time = p0.time.toString()
            isLocationUpdating = false
            updateLocationToFirebase()
        }
    }

    fun sendBroadcastToActivity(lat:String, lng:String, speed:String){
        val broadcastIntent = Intent("LOCATION_INFO")
        broadcastIntent.putExtra("lat", lat.toString())
        broadcastIntent.putExtra("lng", lng.toString())
        broadcastIntent.putExtra("speed", speed.toString())
        sendBroadcast(broadcastIntent)
    }

    fun getNotification(): Notification{
        val notification = NotificationCompat.Builder(this, Application.CHANNEL_ID)
            .setContentTitle("Location service running...")
            .setContentText("Location service is running in the background")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        return notification
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates(){
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            1000, 0.5f,
            this
        )
    }
    fun stopLocationUpdates(){
        locationManager.removeUpdates(this)
    }

    fun updateLocationToFirebase(){
        var databaseRef = Application.getDatabaseReference()
        databaseRef.push().setValue(locationInfo)
    }

}