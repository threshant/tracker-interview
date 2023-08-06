package com.contractorplus.tracker.viewmodel

import android.opengl.Visibility
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.contractorplus.tracker.model.PermissionsInfo
import com.contractorplus.tracker.model.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class AuthActivityViewModel: ViewModel() {
    val _email = MutableLiveData<String>()
    val _password = MutableLiveData<String>()
    val _isButtonEnabled = MutableLiveData<Int>()
    val _isLoading = MutableLiveData<Int>()
    val _message = MutableLiveData<String>()
    val _isLoginSuccessful = MutableLiveData<Boolean>()
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    init {
        _email.value = ""
        _password.value = ""
        _isButtonEnabled.value = View.VISIBLE
        _isLoading.value = View.GONE
        _message.value = "Please choose to continue"
        _isLoginSuccessful.value = false
    }
    fun signup() {
        if(validateEmailAndPassword(_email.value!!, _password.value!!)){
            _isLoading.value = View.VISIBLE
            _message.value = "Signing up..."
            firebaseAuth.createUserWithEmailAndPassword(_email.value!!, _password.value!!)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _message.value = "Sign up successful, login to continue"
                        _isLoading.value = View.GONE
                    } else {
                        _message.value = "Sign up failed, user might exist already"
                        _isLoading.value = View.GONE
                    }
                }
        }
        else{
            _message.value = "Please enter a valid email and password"
        }
    }

    fun login(){
        if(validateEmailAndPassword(_email.value!!, _password.value!!)){
            _isLoading.value = View.VISIBLE
            _message.value = "Logging in..."
            firebaseAuth.signInWithEmailAndPassword(_email.value!!, _password.value!!)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _message.value = "Login successful"
                        _isLoading.value = View.GONE
                        _isLoginSuccessful.value = true
                        task.result.user?.let {
                            val email = it.email
                            val uid = it.uid

                        }
                    } else {
                        _message.value = "Login failed, user might not exist"
                        _isLoading.value = View.GONE
                    }
                }
        }
        else{
            _message.value = "Please enter a valid email and password"
        }
    }

    fun validateEmailAndPassword(email: String, password: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        val isEmailValid = email.matches(emailPattern.toRegex())

        val isPasswordValid = password.length >= 8

        return isEmailValid && isPasswordValid
    }
}