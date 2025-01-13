@file:OptIn(ExperimentalMaterial3Api::class)

package pt.umaia.tumappv2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import pt.umaia.tumappv2.ui.theme.TUMAPPV2Theme
import androidx.compose.material3.Icon
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge()
        setContent {
            TUMAPPV2Theme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ProgramaPrincipal()

                }
            }
        }
    }
}


@Composable
fun ProgramaPrincipal() {
    val navController = rememberNavController()
    var username by remember { mutableStateOf("Unknown User") }
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(userId) {
        userId?.let {
            FirebaseFirestore.getInstance().collection("users").document(it).get()
                .addOnSuccessListener { document ->
                    username = document.getString("username") ?: "Unknown User"
                }
                .addOnFailureListener {
                    username = "Error fetching username"
                }
        }
    }

    Scaffold(
        modifier = Modifier.background(Color.White),
        bottomBar = { BottomNavigationBar(navController = navController, appItems = Destino.toList) },
        content = { padding ->
            Box(modifier = Modifier
                .padding(padding)
                .background(Color.White)) {
                AppNavigation(navController = navController, username = username)
            }
        }
    )
}



@Composable
fun AppNavigation(navController: NavHostController, username: String) {

    // ECRA 04
    val Dia = rememberSaveable { mutableStateOf("") }
    val Local = rememberSaveable { mutableStateOf("") }
    val Op1 = rememberSaveable { mutableStateOf("Option 1") }
    val op1Text = rememberSaveable { mutableStateOf("Pago") }
    val op2Text = rememberSaveable { mutableStateOf("Não Pago") }
    var listA = rememberSaveable { mutableStateOf("") }

    NavHost(navController, startDestination = Destino.EcraLoginFirebase.route) {
        composable(Destino.EcraLoginFirebase.route) {
            EcraLoginFirebase(navController)
        }
        composable(Destino.EcraRegisterToFirebase.route) {
            EcraRegisterToFirebase(navController,)
        }
        composable(Destino.EcraSettings.route) {
            EcraSettings(navController)
        }

        composable(Destino.Ecra03.route) {
            Ecra03(listA, username = username)
        }

        composable(Destino.Ecra04.route) {
            Ecra04(
                navController = navController,
                Dias = Dia,
                Local = Local,
                selectedOption = Op1,
                option1Text = op1Text,
                option2Text = op2Text,
                )
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController, appItems: List<Destino>) {
    BottomNavigation(backgroundColor = colorResource(id = R.color.purple_700),contentColor = Color.White) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        appItems.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(painterResource(id = item.icon), contentDescription = item.title, tint=if(currentRoute == item.route) Color.White else Color.White.copy(.4F)) },
                label = { Text(text = item.title, color = if(currentRoute == item.route) Color.White else Color.White.copy(.4F)) },
                selectedContentColor = Color.White, // esta instrução devia funcionar para o efeito (animação), para o ícone e para a cor do texto, mas só funciona para o efeito
                unselectedContentColor = Color.White.copy(0.4f), // esta instrução não funciona, por isso resolve-se acima no 'tint' do icon e na 'color' da label
                alwaysShowLabel = true, // colocar 'false' significa que o texto só aparece debaixo do ícone selecionado (em vez de debaixo de todos)
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let { route -> popUpTo(route) { saveState = true } }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
