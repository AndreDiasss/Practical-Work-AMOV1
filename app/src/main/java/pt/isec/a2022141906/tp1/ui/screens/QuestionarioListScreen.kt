package pt.isec.a2022141906.tp1.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import pt.isec.a2022141906.tp1.R
import pt.isec.a2022141906.tp1.ui.viewmodels.QuestionarioViewModel
import pt.isec.a2022141906.tp1.ui.viewmodels.UserViewModel

@Composable
fun QuestionarioListScreen(
    viewModel: QuestionarioViewModel = viewModel(),
    onQuestionarioClick: (String) -> Unit,
    onCriarQuestionarioClick: () -> Unit,
    criadorEmail: String, // Adicionado parâmetro para o e-mail do usuário logado
    userViewModel: UserViewModel,
    navController: NavController
) {
    val questionarios by viewModel.questionarios.collectAsState()

    // Filtra os questionários pelo criadorEmail do usuário logado
    val questionariosDoUsuario = questionarios.filter { it.criadorEmail == criadorEmail }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { onCriarQuestionarioClick() }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(R.string.create_quiz))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.my) + " Quizzes:",
                    modifier = Modifier.padding(8.dp),
                    fontSize = 25.sp
                )
                Button(
                    onClick = { navController.navigate("joinQuiz") },
                    modifier = Modifier.padding(end = 16.dp),
                ) {
                    Text(text = stringResource(R.string.play)  + " Quiz")
                }
            }
            if (questionariosDoUsuario.isEmpty()) {
                Text(
                    text = "no quizzes", //stringResource(R.string.no_quizzes_found)
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                LazyColumn {
                    items(questionariosDoUsuario.sortedBy { it.titulo.uppercase() }) { questionario ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable { onQuestionarioClick(questionario.id) }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = questionario.titulo,
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                    Text(
                                        text = questionario.descricao,
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
