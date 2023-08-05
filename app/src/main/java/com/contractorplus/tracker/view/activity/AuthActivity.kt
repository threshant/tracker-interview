package com.contractorplus.tracker.view.activity

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.contractorplus.tracker.R
import com.contractorplus.tracker.databinding.ActivityAuthBinding
import com.contractorplus.tracker.viewmodel.AuthActivityViewModel
import com.contractorplus.tracker.viewmodel.MainActivityViewModel
import com.google.firebase.auth.FirebaseAuth

class AuthActivity : AppCompatActivity() {
    lateinit var authActivityViewModel: AuthActivityViewModel
    lateinit var binding: ActivityAuthBinding
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authActivityViewModel = ViewModelProvider(this).get(AuthActivityViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_auth)
        binding.lifecycleOwner = this  // Set the lifecycle owner for LiveData updates
        binding.viewModel = authActivityViewModel
        setContentView(binding.root)
        sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
        if(FirebaseAuth.getInstance().currentUser != null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        authActivityViewModel._isLoginSuccessful.observe(this) {
            if (it) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }
}