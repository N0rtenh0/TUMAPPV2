package pt.umaia.tumappv2

import android.content.Context
import android.content.Intent
import android.provider.Settings.Global.getString
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

fun logAndToast(oContexto: Context, tag: String, aMensagem: String) {
    Log.d("FIRE:$tag", aMensagem)
    Toast.makeText(oContexto, aMensagem, Toast.LENGTH_SHORT).show()
}

@Composable
fun EcraLoginFirebase(navController: NavController) {
    val oContexto = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val emailError = remember { mutableStateOf(false) }
    val passwordError = remember { mutableStateOf(false) }
    val launcher = rememberFirebaseAuthLauncher(
        onAuthComplete = {
                result -> logAndToast(oContexto, "info", oContexto.getString(R.string.firebase_login_success))
            navController.navigate(Destino.EcraSettings.route)
        },
        onAuthError = { error -> logAndToast(oContexto, "error", "signInWithGoogle:failure$error") }
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.firebase_login_title),
            fontSize = 24.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Replace with the actual image resource
        Image(
            painter = painterResource(id = R.drawable.firebase),
            contentDescription = "Firebase Logo"
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError.value = !isValidEmail(it)
            },
            label = { Text(text = stringResource(id = R.string.firebase_enter_email)) },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            isError = emailError.value,
            modifier = Modifier.fillMaxWidth()
        )
        if (emailError.value) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start // Align content to the start
            ) {
                Text(
                    text = stringResource(id = R.string.firebase_enter_valid_email),
                    color = Color.Red
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError.value = !isValidPassword(it)
            },
            label = { Text(text = stringResource(id= R.string.firebase_enter_password)) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            isError = passwordError.value,
            modifier = Modifier.fillMaxWidth()
        )
        if (passwordError.value) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start // Align content to the start
            ) {
                Text(text = stringResource(id = R.string.firebase_password_min6chars),
                    color = Color.Red
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { performLogin(oContexto, email, password, emailError, passwordError, navController) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("LOG IN")
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.firebase_no_account_question))
            TextButton(onClick = { navController.navigate(Destino.EcraRegisterToFirebase.route) }) {
                Text(text = stringResource(id = R.string.firebase_sign_up), color = Color.Blue)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(stringResource(id=R.string.firebase_or))
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { performGoogleAuthentication(launcher, oContexto) },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.firebase_continue_with_google), color = Color.White)
        }
    }
}

private fun performLogin(
    oContexto: Context,
    email: String,
    password: String,
    emailErrorState: MutableState<Boolean>,
    passwordErrorState: MutableState<Boolean>,
    navController: NavController
) {
    val isEmailValid = isValidEmail(email)
    val isPasswordValid = isValidPassword(password)
    emailErrorState.value = !isEmailValid
    passwordErrorState.value = !isPasswordValid

    if (isEmailValid && isPasswordValid) {
        // Implement actual login logic here
        val auth = Firebase.auth
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) { // Sign in success, update UI with the signed-in user's information
                    logAndToast(oContexto, "info", "Firebase login OK")
                    navController.navigate(Destino.EcraSettings.route)
                } else { // If sign in fails, display a message to the user
                    logAndToast(oContexto, "error", "Erro no username ou palavra chave")
                }
            }
    }
}

fun performGoogleAuthentication(launcher: ManagedActivityResultLauncher<Intent, ActivityResult>, context: Context) {
    val token = context.getString(R.string.google_client_id)
    val gso =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(token)
            .requestEmail()
            .build()
    val googleSignInClient = GoogleSignIn.getClient(context, gso)
    launcher.launch(googleSignInClient.signInIntent)
}
// FIXME: this is a duplicated function with a different name, it should be merged into a single function
fun performGoogleReauthentication(launcher: ManagedActivityResultLauncher<Intent, ActivityResult>, context: Context) {
    val token = context.getString(R.string.google_client_id)
    val gso =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(token)
            .requestEmail()
            .build()
    val googleSignInClient = GoogleSignIn.getClient(context, gso)
    launcher.launch(googleSignInClient.signInIntent)
}

@Composable
private fun rememberFirebaseAuthLauncher(
    onAuthComplete: (AuthResult) -> Unit,
    onAuthError: (ApiException) -> Unit
): ManagedActivityResultLauncher<Intent, ActivityResult> {
    val scope = rememberCoroutineScope()
    return rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
            scope.launch {
                val authResult = Firebase.auth.signInWithCredential(credential).await()
                onAuthComplete(authResult)
            }
        } catch (e: ApiException) {
            onAuthError(e)
        }
    }
}

private fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

private fun isValidPassword(password: String): Boolean {
    return password.length >= 6
}