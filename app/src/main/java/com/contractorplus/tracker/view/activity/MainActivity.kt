package com.contractorplus.tracker.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.contractorplus.tracker.Application.Companion.SERVICE_COMMAND
import com.contractorplus.tracker.Application.Companion.SERVICE_START_COMMAND
import com.contractorplus.tracker.R
import com.contractorplus.tracker.databinding.ActivityMainBinding
import com.contractorplus.tracker.service.LocationService
import com.contractorplus.tracker.view.adapter.LocationsAdapter
import com.contractorplus.tracker.viewmodel.MainActivityViewModel


class MainActivity : AppCompatActivity() {
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var switch: android.widget.Switch
    private lateinit var locationsRecyclerView: RecyclerView
    private lateinit var mainActivityViewModel: MainActivityViewModel
    private lateinit var locationsAdapter: LocationsAdapter
    private lateinit var locationServiceIntent : Intent
    private lateinit var binding: ActivityMainBinding
    private lateinit var locationManager: LocationManager
    private lateinit var locationPermissionRequest: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this  // Set the lifecycle owner for LiveData updates
        binding.viewModel = mainActivityViewModel
        setContentView(binding.root)
        runSetup()
    }

    override fun onResume() {
        super.onResume()
        mainActivityViewModel.setGPSTurnedOn(checkGpsStatus())
        mainActivityViewModel.setLocationPermission(checkLocationPermission())

    }


    fun runSetup(){
        setupViews()
        setupListeners()
        setupObservers()
        locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                mainActivityViewModel.setLocationPermission(true)
            } else {
                mainActivityViewModel.setLocationPermission(false)
            }
        }
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val locationReciever: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(arg0: Context?, arg1: Intent) {
                val lat = arg1.extras!!.getString("lat")
                val lng = arg1.extras!!.getString("lng")
                val speed = arg1.extras!!.getString("speed")
                mainActivityViewModel._speed.value = speed
                mainActivityViewModel._latitude.value = lat
                mainActivityViewModel._longitude.value = lng
            }
        }
        registerReceiver(locationReciever, IntentFilter("LOCATION_INFO"))
    }

    fun setupViews(){
        switch = findViewById(R.id.serviceSwitch)
        locationsRecyclerView = findViewById(R.id.locationsRecyclerView)
        locationsAdapter = LocationsAdapter()
        locationsRecyclerView.adapter = locationsAdapter
    }

    fun setupListeners(){
        binding.turnOnGps.setOnClickListener {
            requestGpsTurnOn()
        }
        binding.turnOnLocation.setOnClickListener {
            requestLocationPermission()
        }
    }


    fun setupObservers(){
        mainActivityViewModel.locations().observe(this) {
            locationsAdapter.updateLocations(it)
            Log.d("MainActivity", "Locations read from Firebas${it[0].startX}")
        }
        mainActivityViewModel.switchState().observe(this) {
            Log.d("MainActivity", "Switch state changed to $it")
            if(it && mainActivityViewModel.permissionsInfoData.value!!.locationPermission == true && mainActivityViewModel.permissionsInfoData.value!!.gpsTurnedOn == true){
                startLocationService()
                Log.d("MainActivity", "Service started")
            }
            else if(this::locationServiceIntent.isInitialized && !it){
                stopLocationService()
                Log.d("MainActivity", "Service stopped")
            }
            else{
                return@observe
            }
        }

        mainActivityViewModel.permissionsInfoData.observe(this) {
            if(it.locationPermission == true && it.gpsTurnedOn == true){
                mainActivityViewModel.setSwitchState(true)
            }
            else{
                mainActivityViewModel.setSwitchState(false)
            }
        }
    }

    fun checkLocationPermission():Boolean{
        return ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    fun requestLocationPermission(){
        locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    fun checkGpsStatus():Boolean{
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
    fun requestGpsTurnOn(){
        val intent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    fun startLocationService(){
        Log.d("MainActivity", "Starting location service")
        locationServiceIntent = Intent(this, LocationService::class.java)
        locationServiceIntent.putExtra(SERVICE_COMMAND, SERVICE_START_COMMAND)
        startService(locationServiceIntent)
    }

    fun stopLocationService(){
        stopService(locationServiceIntent)
    }

}