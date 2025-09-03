package pt.isec.a2022141906.tp1.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pt.isec.a2022141906.tp1.R
import pt.isec.a2022141906.tp1.models.EstadoQuestionario
import pt.isec.a2022141906.tp1.models.Pergunta
import pt.isec.a2022141906.tp1.ui.viewmodels.QuestionarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerguntaListScreen(
    questionarioId: String,
    questionarioViewModel: QuestionarioViewModel,
    onAdicionarPerguntaClick: () -> Unit,
    navController: NavController,
    onPerguntaClick: (String) -> Unit,
) {
    val questionario = questionarioViewModel.questionarios.collectAsState().value.find { it.id == questionarioId }
    var perguntas by remember { mutableStateOf<List<Pergunta>>(emptyList()) }

    LaunchedEffect(questionario) {
        questionario?.let {
            questionarioViewModel.buscarPerguntasDoQuestionario(it.perguntas) { fetchedPerguntas ->
                perguntas = fetchedPerguntas
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz '${questionario?.titulo}'") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            if (questionario?.estado == EstadoQuestionario.WAITING) {
                                navController.navigate("configurarQuestionario/$questionarioId")
                            } else {
                                navController.navigate("resultadosCriador/$questionarioId")
                            }
                        },
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Text(text =
                        if (questionario?.estado == EstadoQuestionario.WAITING)
                            stringResource(R.string.set_up_quiz)
                        else
                            "Ver Resultados") //stringResource
                    }
                }
            )
        },
        floatingActionButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FloatingActionButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier.padding(start = 32.dp)
                ) {
                    Icon(imageVector = Icons.Default.MailOutline, contentDescription = stringResource(R.string.get_copy_question))
                }
                FloatingActionButton(onClick = onAdicionarPerguntaClick) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_question)
                    )
                }
            }
        }
    ) { innerPadding ->
        if (perguntas.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(R.string.no_questions), style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(modifier = Modifier.padding(innerPadding)) {
                items(perguntas.sortedBy { it.titulo.uppercase() }) { pergunta ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable { onPerguntaClick(pergunta.id) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = pergunta.titulo, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }
}