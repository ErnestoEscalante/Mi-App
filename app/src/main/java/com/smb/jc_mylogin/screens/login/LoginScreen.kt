package com.smb.jc_mylogin.screens.login

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults.colors
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

import com.smb.jc_mylogin.R
import com.smb.jc_mylogin.navigation.Screens

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val showLoginForm = rememberSaveable {
        mutableStateOf(true)
    }

    // Google Sign-In
    val token = "102189252440-i2b5v33u25544iiispcsq8mn9nm93fm1.apps.googleusercontent.com"
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)

            viewModel.signInWithGoogleCredential(credential) {
                // Firebase Auth exitoso, obtenemos el email del usuario
                val email = account.email ?: ""
                val db = FirebaseFirestore.getInstance()

                // Comprobar si el usuario ya existe en Firestore
                db.collection("users").document(email).get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            Log.d("Firestore", "El usuario ya existe en Firestore.")
                        } else {
                            // Si no existe, creamos el usuario en Firestore
                            val userData = hashMapOf(
                                "email" to email,
                                "puntos" to 0
                            )
                            db.collection("users").document(email).set(userData)
                                .addOnSuccessListener {
                                    Log.d("Firestore", "Usuario añadido a Firestore.")
                                }
                                .addOnFailureListener { e ->
                                    Log.e("Firestore", "Error al añadir usuario: ", e)
                                }
                        }

                        navController.navigate(Screens.HomeScreen.name)
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Error al verificar usuario en Firestore: ", e)
                    }
            }
        } catch (ex: Exception) {
            Log.d("My Login", "GoogleSignIn falló: ${ex.localizedMessage}")
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.fotoaplicacion),
                contentDescription = "Logo de Triviando",
                modifier = Modifier
                    .size(150.dp)
                    .padding(bottom = 20.dp)
            )

            // Mostrar formulario según el estado
            if (showLoginForm.value) {
                Text(
                    text = "Inicia sesión",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White
                )
                UserForm(isCreateAccount = false) { email, password, _ ->
                    Log.d("My Login", "Iniciando sesión con $email y $password")
                    viewModel.signInWithEmailAndPassword(email, password) {
                        navController.navigate(Screens.HomeScreen.name)
                    }
                }

            } else {
                Text(
                    text = "Crear una cuenta",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White
                )
                UserForm(isCreateAccount = true) { email, password, name ->
                    Log.d("My Login", "Creando cuenta con $email, $password y $name")
                    viewModel.createUserWithEmailAndPassword(email, password, name) {
                        navController.navigate(Screens.HomeScreen.name)
                    }
                }


            }

            Spacer(modifier = Modifier.height(15.dp))

            // Alternar entre Iniciar sesión y Crear cuenta
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val text1 = if (showLoginForm.value) "¿No tienes cuenta?" else "¿Ya tienes cuenta?"
                val text2 = if (showLoginForm.value) "Regístrate" else "Inicia sesión"
                Text(
                    text = text1,
                    color = Color.White
                )
                Text(
                    text = text2,
                    modifier = Modifier
                        .clickable { showLoginForm.value = !showLoginForm.value }
                        .padding(start = 5.dp),
                    color = Color.White
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable {
                        val opciones = GoogleSignInOptions
                            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(token)
                            .requestEmail()
                            .build()
                        val googleSingInCliente = GoogleSignIn.getClient(context, opciones)
                        launcher.launch(googleSingInCliente.signInIntent)
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "Login con GOOGLE",
                    modifier = Modifier
                        .padding(10.dp)
                        .size(40.dp)
                )
                Text(
                    text = "Login con Google",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun UserForm(
    isCreateAccount: Boolean,
    onDone: (String, String, String) -> Unit = { email, pwd, name -> }
) {
    val email = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }
    val name = rememberSaveable { mutableStateOf("") } // Campo para el nombre
    val passwordVisible = rememberSaveable { mutableStateOf(false) }

    val valido = remember(email.value, password.value, name.value) {
        email.value.trim().isNotEmpty() && password.value.trim().isNotEmpty() && (!isCreateAccount || name.value.trim().isNotEmpty())
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmailInput(emailState = email)

        PasswordInput(passwordState = password, passwordVisible = passwordVisible)

        // Si estamos creando una cuenta, mostramos el campo de nombre
        if (isCreateAccount) {
            NameInput(nameState = name)
        }

        SubmitButton(
            textId = if (isCreateAccount) "Crear cuenta" else "Iniciar sesión",
            inputValido = valido
        ) {
            onDone(email.value.trim(), password.value.trim(), name.value.trim())
            keyboardController?.hide()
        }
    }
}

@Composable
fun NameInput(nameState: MutableState<String>, labelId: String = "Nombre") {
    InputField(
        valuestate = nameState,
        labelId = labelId,
        keyboardType = KeyboardType.Text
    )
}



@Composable
fun SubmitButton(
    textId: String,
    inputValido: Boolean,
    onClic: () -> Unit
) {
    Button(
        onClick = onClic,
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp),
        shape = CircleShape,
        enabled = inputValido
    ) {
        Text(
            text = textId,
            modifier = Modifier.padding(5.dp)
        )
    }
}


@Composable
fun EmailInput(
    emailState: MutableState<String>,
    labelId: String = "Email"
) {
    InputField(
        valuestate = emailState,
        labelId = labelId,
        keyboardType = KeyboardType.Email
    )
}

@Composable
fun InputField(
    valuestate: MutableState<String>,
    labelId: String,
    keyboardType: KeyboardType,
    isSingleLine: Boolean = true,
) {
    OutlinedTextField(
        value = valuestate.value,
        onValueChange = { valuestate.value = it },
        label = { Text(text = labelId) },
        singleLine = isSingleLine,
        modifier = Modifier
            .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
            .fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = colors(
            focusedTextColor = Color.White,
            focusedBorderColor = Color.Green,
            unfocusedBorderColor = Color.Gray
        )
    )
}


@Composable
fun PasswordInput(
    passwordState: MutableState<String>,
    labelId: String = "Password",
    passwordVisible: MutableState<Boolean>
) {
    val visualTransformation = if (passwordVisible.value)
        VisualTransformation.None
    else PasswordVisualTransformation()

    OutlinedTextField(
        value = passwordState.value,
        onValueChange = { passwordState.value = it },
        label = { Text(text = labelId) },
        singleLine = true,
        modifier = Modifier
            .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
            .fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = visualTransformation,
        trailingIcon = {
            if (passwordState.value.isNotBlank()) {
                PasswordVisibleIcon(passwordVisible)
            } else null
        },
        colors = colors(
            focusedTextColor = Color.White,
            focusedBorderColor = Color.Green,
            unfocusedBorderColor = Color.Gray
        )
    )
}

@Composable
fun PasswordVisibleIcon(
    passwordVisible: MutableState<Boolean>
) {
    val image = if (passwordVisible.value)
        Icons.Default.VisibilityOff
    else
        Icons.Default.Visibility

    IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
        Icon(
            imageVector = image,
            contentDescription = ""
        )
    }
}
