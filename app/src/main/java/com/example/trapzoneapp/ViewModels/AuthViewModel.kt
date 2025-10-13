package com.example.trapzoneapp.ViewModels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.trapzoneapp.classes.AuthState
import com.example.trapzoneapp.functions.firebase.setUserActive
import com.example.trapzoneapp.functions.firebase.setUserInactive
import com.example.trapzoneapp.functions.uploadToCloudinary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthViewModel: ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db :DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
    private val _authState = MutableLiveData<AuthState?>()
    val authState: LiveData<AuthState?> = _authState

    init {
        checkAuthStatus()
    }
    private fun checkAuthStatus(){
        if(auth.currentUser==null)
        {
            _authState.value = AuthState.Unauthenticated
        }
        else
        {
            _authState.value = AuthState.Authenticated
        }
    }
    fun login(email :String, password: String){
        if(email.isEmpty()){
            _authState.value = AuthState.Error("Polje za email je prazno!")
            return
        }
        if(password.isEmpty()){
            _authState.value = AuthState.Error("Polje za lozinku je prazno!")
            return
        }

        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener{task->
                if(task.isSuccessful){
                    _authState.value = AuthState.Authenticated
                    setUserActive()
                }else{
                    _authState.value = AuthState.Error(task.exception?.message?:"Došlo je do neke greške")
                }
            }
    }
    fun signup(email: String , password: String,name:String,surname:String,phone:String,photo:Uri?,context:android.content.Context){
        if(email.isEmpty() || password.isEmpty()||name.isEmpty()||surname.isEmpty()||phone.isEmpty()){
            _authState.value = AuthState.Error("Sva polja moraju biti popunjena!")
            return
        }
        if(photo==null)
        {
            _authState.value = AuthState.Error("Morate odabrati sliku!")
            return
        }
        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener{task->
                if(task.isSuccessful){
                    _authState.value = AuthState.Authenticated
                    setUserActive()
                    val uid = auth.currentUser!!.uid
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val inputStream =
                                context.contentResolver.openInputStream(photo)
                                    ?: throw Exception("Ne mogu da otvorim fajl")

                            val requestBody = inputStream.readBytes()
                            val uploadResult = uploadToCloudinary(requestBody)
                            val imageUrl = uploadResult ?: ""
                            val userMap = mapOf(
                                "name" to name,
                                "surname" to surname,
                                "phone" to phone,
                                "photoURL" to imageUrl

                            )
                            db.child(uid).child("data").setValue(userMap)
                                .addOnSuccessListener {
                                    _authState.value = AuthState.Authenticated
                                }
                                .addOnFailureListener {e->
                                    _authState.value = AuthState.Error("Greška pri čuvanju podataka: ${e.message}")
                                }
                        }catch (e:Exception) {
                            _authState.postValue(AuthState.Error("Greška pri upload-u slike: ${e.message}"))
                        }
                    }
                }else{
                    _authState.value = AuthState.Error(task.exception?.message?:"Došlo je do neke greške")
                }
            }
    }
    fun signout(){
        setUserInactive()
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }
    fun clearState() {
        _authState.value = null
    }
}
