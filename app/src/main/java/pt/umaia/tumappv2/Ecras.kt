package pt.umaia.tumappv2
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast








@Composable
fun Ecra03(
    listA: MutableState<String>, // Only pass list as a parameter
//        option1Text: MutableState<String>,  // Parameter to hold Option 1 label
//        option2Text: MutableState<String> // Parameter to hold Option 2 label

) {
    Column(
        modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center)
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

        // Display the list's value
        Text(
            text = listA.value,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.padding(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
        /*
                    // Text Box to update Option 1 label
                    TextField(
                        value = option1Text.value,
                        onValueChange = { option1Text.value = it },
                        label = { Text("Update Option 1 Label") },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Text Box to update Option 2 label
                    TextField(
                        value = option2Text.value,
                        onValueChange = { option2Text.value = it },
                        label = { Text("Update Option 2 Label") },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Text Box to update Option 3 label
        */
    }
}



@Composable
fun Ecra04(
    navController: NavController,
    Dias: MutableState<String>,
    Local: MutableState<String>,
    selectedOption: MutableState<String>,
    option1Text: MutableState<String>,  // Parameter to hold Option 1 label
    option2Text: MutableState<String>, // Parameter to hold Option 2 label
    onClick: (String, String, String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center)
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

        // First Text Box
        TextField(
            value = Dias.value,
            onValueChange = { Dias.value = it },
            label = { Text("Insira o Dia da Atuação") },
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Second Text Box
        TextField(
            value = Local.value,
            onValueChange = { Local.value = it },
            label = { Text("Insira o Local da Atuação") },
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Radio Buttons for options with dynamic labels
        Text(text = "Tipo de atuação:")

        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = selectedOption.value == option1Text.value,
                onClick = { selectedOption.value = option1Text.value }
            )
            Text(text = option1Text.value)  // Use dynamic label for Option 1

            Spacer(modifier = Modifier.width(16.dp))

            RadioButton(
                selected = selectedOption.value == option2Text.value,
                onClick = { selectedOption.value = option2Text.value }
            )
            Text(text = option2Text.value)  // Use dynamic label for Option 2
        }

        Spacer(modifier = Modifier.height(16.dp))


        Button(onClick = { onClick(Dias.value, Local.value, selectedOption.value)
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





