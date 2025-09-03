package pt.isec.a2022141906.tp1.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import pt.isec.a2022141906.tp1.R
import pt.isec.a2022141906.tp1.ui.viewmodels.QuestionarioViewModel
import pt.isec.a2022141906.tp1.ui.viewmodels.PerguntaViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pt.isec.a2022141906.tp1.models.TipoPergunta
import pt.isec.a2022141906.tp1.ui.viewmodels.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultadosCriadorScreen(
    questionarioId: String,
    userViewModel: UserViewModel,
    questionarioViewModel: QuestionarioViewModel,
    perguntaViewModel: PerguntaViewModel,
    navController: NavController
) {
    val questionario by questionarioViewModel.questionario.observeAsState()
    val pergunta by perguntaViewModel.pergunta.observeAsState()
    val resultados by questionarioViewModel.resultados.observeAsState()
    val jogadoresTerminados by questionarioViewModel.jogadoresTerminados.observeAsState(0)
    val jogadoresTotais by questionarioViewModel.jogadoresTotais.observeAsState(0)
    val utilizadoresConectados by questionarioViewModel.utilizadoresConectados.observeAsState(0)

    LaunchedEffect(questionarioId) {
        questionarioViewModel.observarResultados(questionarioId)
        questionarioViewModel.observarUtilizadoresConectados(questionarioId)
        questionarioViewModel.buscarQuestionario(questionarioId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.results)) },
                navigationIcon = {
                    IconButton(onClick = {
                        questionarioViewModel.fecharQuestionario(questionarioId)
                        navController.navigate("questionarioList")
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(R.string.players_connected) + ": $utilizadoresConectados",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(8.dp)
            )

            Text(
                text = stringResource(R.string.players_finished) + ": $jogadoresTerminados/$utilizadoresConectados",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            resultados?.forEach { (perguntaId, respostas) ->
                Text(
                    text = stringResource(R.string.question) + ": $perguntaId",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )

                if (pergunta?.tipo == TipoPergunta.SIM_NAO || pergunta?.tipo == TipoPergunta.ESCOLHA_MULTIPLA) {
                    respostas.forEach { (resposta, quantidade) ->
                        Text(
                            text = "$resposta: $quantidade " + stringResource(R.string.answers),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    respostas.entries.sortedByDescending { it.value }.forEach { (resposta, quantidade) ->
                        Text(
                            text = "$resposta: $quantidade " + stringResource(R.string.answers),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
