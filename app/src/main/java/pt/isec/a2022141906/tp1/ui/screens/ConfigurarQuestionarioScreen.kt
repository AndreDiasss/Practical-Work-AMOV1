package pt.isec.a2022141906.tp1.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pt.isec.a2022141906.tp1.R
import pt.isec.a2022141906.tp1.ui.viewmodels.QuestionarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurarQuestionarioScreen(
    questionarioId: String,
    navController: NavController,
    questionarioViewModel: QuestionarioViewModel
) {
    var tempo by remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(questionarioId) {
        questionarioViewModel.buscarQuestionario(questionarioId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.set_up_quiz)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(text = stringResource(R.string.quiz_key) + ": $questionarioId")

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = tempo,
                onValueChange = { novoValor ->
                    if (novoValor.all { it.isDigit() }) {
                        tempo = novoValor
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = stringResource(R.string.quiz_time)) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (tempo.isNotEmpty()) {
                        questionarioViewModel.configurarQuestionario(questionarioId, tempo.toInt())
                        navController.navigate("resultadosCriador/$questionarioId")
                    } else {
                        Toast.makeText(context, "Preencha o tempo!", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Text(stringResource(R.string.start_quiz))
            }
        }
    }
}
