package pt.umaia.tumappv2
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import com.google.firebase.auth.FirebaseAuth


@Composable
fun Ecra03(
    listA: MutableState<String>,
    username: String // Use the passed username here
) {
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    val isGoing = remember { mutableStateOf(false) } // Estado para controlar presença
    var currentUsername by remember { mutableStateOf(username) } // Local state for username
    val eventReactions = remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) } // Estado para controlar as reações por evento
    val eventParticipants = remember { mutableStateOf<Map<String, List<String>>>(emptyMap()) } // Estado para armazenar os participantes

    // Função para atualizar dados
    fun refreshData() {
        // Re-fetch the username from Firestore (if needed)
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let {
            db.collection("users").document(it).get()
                .addOnSuccessListener { document ->
                    currentUsername = document.getString("username") ?: "Unknown User"
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Erro ao carregar o nome de usuário.", Toast.LENGTH_SHORT).show()
                }
        }

        // Re-fetch events data
        db.collection("atuacoes")
            .get()
            .addOnSuccessListener { result ->
                val events = result.documents.map { doc ->
                    val dia = doc.getString("Dia") ?: "N/A"
                    val local = doc.getString("Local") ?: "N/A"
                    val tipo = doc.getString("Tipo de atuação") ?: "N/A"
                    val eventKey = "$dia - $local - $tipo"
                    eventKey
                }
                // Update listA with the events
                listA.value = events.joinToString("\n") { it }

                // Fetch participants for each event
                events.forEach { event ->
                    db.collection("reactions")
                        .whereEqualTo("event", event)
                        .get()
                        .addOnSuccessListener { snapshot ->
                            val participants = snapshot.documents.mapNotNull { doc ->
                                doc.getString("username")
                            }
                            eventParticipants.value = eventParticipants.value.toMutableMap().apply {
                                put(event, participants)
                            }

                            // Check if the current user has already reacted to the event
                            val hasReacted = snapshot.documents.any { doc ->
                                doc.getString("username") == currentUsername
                            }
                            eventReactions.value = eventReactions.value.toMutableMap().apply {
                                put(event, hasReacted)
                            }
                        }
                }
            }
            .addOnFailureListener {
                listA.value = "Erro ao carregar dados."
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
        ) {
            Text(
                text = stringResource(id = R.string.ecra03),
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .background(Color.White),
                textAlign = TextAlign.Center,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Display the updated username
            Text(
                text = "Olá, $currentUsername", // Directly use the updated username
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.padding(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Iterate over each event and create a button for it
            val eventsList = listA.value.split("\n").filter { it.isNotBlank() }
            eventsList.forEach { event ->
                val hasConfirmed = eventReactions.value[event] ?: false
                val participants = eventParticipants.value[event] ?: emptyList()

                Button(
                    onClick = {
                        if (hasConfirmed) {
                            // Remove reaction if confirmed
                            db.collection("reactions")
                                .whereEqualTo("username", currentUsername)
                                .whereEqualTo("event", event)
                                .get()
                                .addOnSuccessListener { snapshot ->
                                    val documentReference = snapshot.documents.firstOrNull()?.reference
                                    documentReference?.delete()
                                        ?.addOnSuccessListener {
                                            // Atualiza a lista de participantes no evento
                                            eventParticipants.value = eventParticipants.value.toMutableMap().apply {
                                                val updatedParticipants = participants.filter { it != currentUsername }
                                                put(event, updatedParticipants)
                                            }

                                            // Atualiza a reação para 'false' e remove o nome da lista de participantes
                                            eventReactions.value = eventReactions.value.toMutableMap().apply {
                                                put(event, false)
                                            }
                                            Toast.makeText(context, "Presença cancelada para o evento: $event", Toast.LENGTH_SHORT).show()
                                        }
                                        ?.addOnFailureListener {
                                            Toast.makeText(context, "Erro ao cancelar presença.", Toast.LENGTH_SHORT).show()
                                        }
                                }
                        } else {
                            // Add reaction if not confirmed
                            val eventData = mapOf(
                                "username" to currentUsername,
                                "event" to event,
                                "timestamp" to System.currentTimeMillis()
                            )

                            db.collection("reactions")
                                .add(eventData)
                                .addOnSuccessListener {
                                    // Atualiza a lista de participantes no evento
                                    eventParticipants.value = eventParticipants.value.toMutableMap().apply {
                                        val updatedParticipants = participants + currentUsername
                                        put(event, updatedParticipants)
                                    }

                                    // Atualiza a reação para 'true'
                                    eventReactions.value = eventReactions.value.toMutableMap().apply {
                                        put(event, true)
                                    }

                                    Toast.makeText(context, "Confirmado para o evento: $event", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Erro ao confirmar presença para o evento.", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                ) {
                    Text(text = if (hasConfirmed) "Cancelar presença para $event" else "Confirmar presença para $event")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Display participants for this event
                Text(
                    text = "Participantes: ${participants.joinToString(", ")}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Refresh Button at the top-right corner
        Button(
            onClick = { refreshData() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Text(text = "Refresh")
        }
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
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .background(Color.White),
            textAlign = TextAlign.Center,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = Dias.value,
            onValueChange = { Dias.value = it },
            label = { Text("Insira o Dia da Atuação",color = Color.Black) },
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = Local.value,
            onValueChange = { Local.value = it },
            label = { Text("Insira o Local da Atuação",color = Color.Black) },
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Tipo de atuação:",color = Color.Black)

        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = selectedOption.value == option1Text.value,
                onClick = { selectedOption.value = option1Text.value }
            )
            Text(text = option1Text.value,color = Color.Black)

            Spacer(modifier = Modifier.width(16.dp))

            RadioButton(
                selected = selectedOption.value == option2Text.value,
                onClick = { selectedOption.value = option2Text.value }
            )
            Text(text = option2Text.value,color = Color.Black)
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





