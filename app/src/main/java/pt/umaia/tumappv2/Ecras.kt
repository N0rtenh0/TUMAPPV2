package pt.umaia.tumappv2
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext




@Composable
fun Ecra03(
    listA: MutableState<String>
) {
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    val userId = "user123" // Replace with actual user ID
    val isGoing = remember { mutableStateOf(false) }
    val goingCount = remember { mutableStateOf(0) }

    // Fetch Firestore data on screen load
    LaunchedEffect(Unit) {
        db.collection("atuacoes")
            .get()
            .addOnSuccessListener { result ->
                val events = result.documents.joinToString("\n") { doc ->
                    val dia = doc.getString("Dia") ?: "N/A"
                    val local = doc.getString("Local") ?: "N/A"
                    val tipo = doc.getString("Tipo de atuação") ?: "N/A"

                    "\n Dia: $dia \n Local: $local \n Tipo de atuação: $tipo"
                }
                listA.value = events
            }
            .addOnFailureListener {
                listA.value = "Erro ao carregar dados."
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        Text(
            text = stringResource(id = R.string.ecra03),
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = listA.value,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.padding(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (!isGoing.value) {
            Button(
                onClick = {
                    val eventData = mapOf(
                        "userId" to userId,
                        "timestamp" to System.currentTimeMillis()
                    )

                    db.collection("reactions")
                        .add(eventData)
                        .addOnSuccessListener {
                            goingCount.value++
                            isGoing.value = true
                            Toast.makeText(context, "Confirmado! Você está indo.", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Erro ao confirmar presença.", Toast.LENGTH_SHORT).show()
                        }
                }
            ) {
                Text(text = "Confirmar Presença")
            }
        } else {
            Button(
                onClick = {
                    db.collection("reactions")
                        .whereEqualTo("userId", userId)
                        .get()
                        .addOnSuccessListener { result ->
                            result.documents.firstOrNull()?.reference?.delete()?.addOnSuccessListener {
                                goingCount.value--
                                isGoing.value = false
                                Toast.makeText(context, "Você cancelou sua presença.", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Erro ao cancelar presença.", Toast.LENGTH_SHORT).show()
                        }
                }
            ) {
                Text(text = "Cancelar Presença")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Número de confirmações: ${goingCount.value}",
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}




@Composable
fun Ecra04(
    navController: NavController,
    Dias: MutableState<String>,
    Local: MutableState<String>,
    selectedOption: MutableState<String>,
    option1Text: MutableState<String>,
    option2Text: MutableState<String>,
) {
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current  // Access context from Compose

    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        Text(
            text = stringResource(id = R.string.ecra04),
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = Dias.value,
            onValueChange = { Dias.value = it },
            label = { Text("Insira o Dia da Atuação") },
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = Local.value,
            onValueChange = { Local.value = it },
            label = { Text("Insira o Local da Atuação") },
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Tipo de atuação:")

        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = selectedOption.value == option1Text.value,
                onClick = { selectedOption.value = option1Text.value }
            )
            Text(text = option1Text.value)

            Spacer(modifier = Modifier.width(16.dp))

            RadioButton(
                selected = selectedOption.value == option2Text.value,
                onClick = { selectedOption.value = option2Text.value }
            )
            Text(text = option2Text.value)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val event = hashMapOf(
                "Dia" to Dias.value,
                "Local" to Local.value,
                "Tipo de atuação" to selectedOption.value
            )

            db.collection("atuacoes")
                .add(event)
                .addOnSuccessListener {
                    Toast.makeText(context, "Atuação Criada com Sucesso!", Toast.LENGTH_SHORT).show()

                    navController.navigate("Ecra03")
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Erro ao criar atuação.", Toast.LENGTH_SHORT).show()
                }
        }) {
            Text("Adicionar Evento")
        }
    }
}



@Composable
fun MeuRadioButton(
    selecionado: Boolean,
    resId: Int,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        RadioButton(
            selected = selecionado,
            onClick = { onClick(resId) }
        )
        Text(
            text = stringResource(resId),
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}





