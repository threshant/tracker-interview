package com.contractorplus.tracker.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this  // Set the lifecycle owner for LiveData updates
        binding.viewModel = mainActivityViewModel
        setContentView(binding.root)
        runSetup()
    }

    fun runSetup(){
        setupViews()
        setupListeners()
        setupObservers()
        if(!checkLocationPermission()){
            requestLocationPermission()
        }
        else{
            startLocationService()
            mainActivityViewModel.setSwitchState(true)
        }
    }

    fun setupViews(){
        switch = findViewById(R.id.serviceSwitch)
        locationsRecyclerView = findViewById(R.id.locationsRecyclerView)
        locationsAdapter = LocationsAdapter()
        locationsRecyclerView.adapter = locationsAdapter
    }

    fun setupListeners(){}

    fun setupObservers(){
        mainActivityViewModel.locations().observe(this) {
            locationsAdapter.updateLocations(it)
            Log.d("MainActivity", "Locations read from Firebas${it[0].startX}")
        }
        mainActivityViewModel.switchState().observe(this) {
            if(it){
                startLocationService()
            }
            else{
                stopLocationService()
            }
        }
    }

    fun checkLocationPermission():Boolean{
        return ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    fun requestLocationPermission(){
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 101)
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