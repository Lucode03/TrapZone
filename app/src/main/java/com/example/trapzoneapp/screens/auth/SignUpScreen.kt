package com.example.trapzoneapp.screens.auth

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.trapzoneapp.AuthViewModel
import com.example.trapzoneapp.R
import com.example.trapzoneapp.classes.AuthState
import com.example.trapzoneapp.screens.auth.fields.CustomTextField

@Composable
fun SignUpScreen(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel)
{
    var name by remember {
        mutableStateOf("")
    }
    var surname by remember {
        mutableStateOf("")
    }
    var phone by remember {
        mutableStateOf("")
    }
    var email by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    var photoUri by remember {
       mutableStateOf<Uri?>(null)
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        photoUri = uri
    }
    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Authenticated -> navController.navigate("main")
            is AuthState.Error -> {
                Toast.makeText(context,
                (authState.value as AuthState.Error).message,Toast.LENGTH_SHORT).show()
                authViewModel.clearState()
            }
            else -> Unit
        }
    }
    val focusManager = LocalFocusManager.current
    Box(modifier = modifier.fillMaxSize()) {
        Image(painter = painterResource(id= R.drawable.login_background),
            contentDescription = "Login background",
            modifier = Modifier.fillMaxSize())

        Column(
            modifier = modifier.fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .clickable(indication = null,
                                        interactionSource = remember { MutableInteractionSource() }
                            ) {
                                focusManager.clearFocus()
                            },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Registracija",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(text = "Dobrodošli u aplikaciju!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height((30.dp)))
            HorizontalDivider(thickness = 1.dp, color = Color.Gray, modifier = Modifier.fillMaxWidth(0.9f))
            Spacer(modifier = Modifier.height((15.dp)))

            CustomTextField(
                value = email,
                onValueChange = { email = it },
                label = stringResource(R.string.email),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height((20.dp)))

            CustomTextField(
                value = password,
                onValueChange = { password = it },
                label = stringResource(R.string.password),
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height((20.dp)))

            CustomTextField(
                value = name,
                onValueChange = { input ->
                    if (input.all { it.isLetter() || it.isWhitespace() }) {
                        name = input
                    }
                },
                label = "Ime",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            )
            Spacer(modifier = Modifier.height((20.dp)))

            CustomTextField(
                value = surname,
                onValueChange = { input ->
                    if (input.all { it.isLetter() || it.isWhitespace() }) {
                        surname = input
                    }
                },
                label = "Prezime",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
            Spacer(modifier = Modifier.height((20.dp)))

            CustomTextField(
                value = phone,
                onValueChange = { input ->
                    if (input.all { it.isDigit() }) {
                        phone = input
                    }
                },
                label = "Broj telefona",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            Spacer(modifier = Modifier.height((20.dp)))

            Button(onClick = { launcher.launch("image/*") }) {
                Text(text = if (photoUri == null) "Izaberi sliku" else "Promeni sliku")
            }
            photoUri?.let { uri ->
                Spacer(modifier = Modifier.height(16.dp))
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = "Izabrana slika",
                    modifier = Modifier
                        .size(120.dp)
                )
            }
            Spacer(modifier = Modifier.height((15.dp)))
            HorizontalDivider(thickness = 1.dp, color = Color.Gray, modifier = Modifier.fillMaxWidth(0.9f))
            Spacer(modifier = Modifier.height((15.dp)))

            Button(onClick = {
                    authViewModel.signup(email, password, name, surname, phone, photoUri, context)
            },
                enabled = authState.value != AuthState.Loading
            ) {
                Text(text="Registracija")
            }
            Spacer(modifier = Modifier.height((10.dp)))
            Text(text="Imate nalog? Prijavite se",
                modifier = Modifier.clickable {
                    navController.navigate("login")
                }
            )


        }
    }
}