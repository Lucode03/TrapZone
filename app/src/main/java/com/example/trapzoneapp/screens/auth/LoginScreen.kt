package com.example.trapzoneapp.screens.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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
import com.example.trapzoneapp.R
import com.example.trapzoneapp.AuthViewModel
import com.example.trapzoneapp.dataclasses.AuthState
import com.example.trapzoneapp.screens.auth.fields.CustomTextField


@Composable
fun LoginScreen(modifier: Modifier=Modifier,navController: NavController,authViewModel: AuthViewModel)
{
    var email by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current
    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Authenticated -> navController.navigate("main")
            is AuthState.Error -> {
                Toast.makeText(context,
                    (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
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
                            .clickable(indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            focusManager.clearFocus()
                        },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(text = "Prijava",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(text = "Dobrodošli nazad u aplikaciju!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height((50.dp)))

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

            Spacer(modifier = Modifier.height((40.dp)))

            Button(onClick = {
                authViewModel.login(email,password)
            },
                enabled = authState.value != AuthState.Loading
            ) {
                Text(text="Prijava")
            }
            Spacer(modifier = Modifier.height((10.dp)))
            Text(text="Nemate nalog? Registrujte se",
                modifier = Modifier.clickable {
                    navController.navigate("signup")
                }
            )


        }
    }
}