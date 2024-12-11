package com.smb.jc_mylogin.screens.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class LoginScreenViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val _loading = MutableLiveData(false)
    val errorMessage = MutableLiveData<String?>(null)

    fun signInWithGoogleCredential(credential: AuthCredential, home: () -> Unit) =
        viewModelScope.launch {
            try {
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("MyLogin", "Google logueado!!!!")
                            home()
                        } else {
                            Log.d("MyLogin", "signInWithGoogle failed: ${task.exception?.message}")
                            errorMessage.value = "Inicio de sesión con Google fallido."
                        }
                    }
            } catch (ex: Exception) {
                Log.d("MyLogin", "Error al loguear con Google: ${ex.message}")
                errorMessage.value = "Error inesperado: ${ex.message}"
            }
        }

    fun signInWithEmailAndPassword(email: String, password: String, home: () -> Unit) =
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("MyLogin", "Usuario logueado con email y contraseña.")
                            home()
                        } else {
                            Log.d("MyLogin", "Inicio de sesión fallido: ${task.exception?.message}")
                            errorMessage.value = "Usuario o contraseña incorrectos."
                        }
                    }
            } catch (ex: Exception) {
                Log.d("MyLogin", "Error al iniciar sesión: ${ex.message}")
                errorMessage.value = "Error inesperado: ${ex.message}"
            }
        }

    fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        name: String,
        home: () -> Unit
    ) {
        Log.d("MyLogin", "Iniciando proceso de creación de usuario.")
        if (_loading.value == false) {
            _loading.value = true
            Log.d("MyLogin", "Realizando validación de correo.")

            // Validar existencia previa
            val db = FirebaseFirestore.getInstance()
            db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { emailResult ->
                    Log.d("MyLogin", "Validación de correo completada.")
                    if (!emailResult.isEmpty) {
                        errorMessage.value = "El correo ya está registrado."
                        _loading.value = false
                        return@addOnSuccessListener
                    }

                    db.collection("users")
                        .whereEqualTo("name", name)
                        .get()
                        .addOnSuccessListener { nameResult ->
                            Log.d("MyLogin", "Validación de nombre completada.")
                            if (!nameResult.isEmpty) {
                                errorMessage.value = "El nombre ya está registrado."
                                _loading.value = false
                                return@addOnSuccessListener
                            }

                            // Crear usuario después de validar que no existe
                            Log.d("MyLogin", "Creando usuario en Firebase Auth.")
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val user = task.result.user
                                        if (user != null) {
                                            createUserInFirestore(email, name) {
                                                home()
                                            }
                                        }
                                    } else {
                                        Log.d("MyLogin", "Error al crear usuario: ${task.exception?.message}")
                                        errorMessage.value = "Error al crear usuario: ${task.exception?.message}"
                                    }
                                    _loading.value = false
                                }
                        }
                        .addOnFailureListener { exception ->
                            errorMessage.value = "Error al validar nombre: ${exception.message}"
                            _loading.value = false
                        }
                }
                .addOnFailureListener { exception ->
                    errorMessage.value = "Error al validar correo: ${exception.message}"
                    _loading.value = false
                }
        }
    }


    private fun createUserInFirestore(email: String, name: String, home: () -> Unit) {
        val db = FirebaseFirestore.getInstance()

        val userData = hashMapOf(
            "name" to name,
            "puntos" to 0
        )

        db.collection("users")
            .document(email)
            .set(userData)
            .addOnSuccessListener {
                Log.d("Firestore", "Usuario añadido a Firestore.")
                home()
            }
            .addOnFailureListener { exception ->
                errorMessage.value = "Error al guardar en Firestore: ${exception.message}"
            }
    }
}

