package pt.isec.a2022141906.tp1.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pt.isec.a2022141906.tp1.R
import pt.isec.a2022141906.tp1.ui.viewmodels.UserViewModel
import pt.isec.a2022141906.tp1.ui.viewmodels.PerguntaViewModel
import pt.isec.a2022141906.tp1.ui.viewmodels.QuestionarioViewModel
import pt.isec.a2022141906.tp1.ui.viewmodels.RespostasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GerirQuestionariosScreen(
    onSignOut: () -> Unit,
    navController: NavHostController = rememberNavController(),
    userViewModel: UserViewModel,
    questionarioViewModel: QuestionarioViewModel,
    perguntaViewModel: PerguntaViewModel,
    respostasViewModel: RespostasViewModel
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Quizec") },
                actions = {
                    IconButton(onClick = { onSignOut() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = stringResource(R.string.sign_out)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "questionarioList",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = "questionarioList") {
                QuestionarioListScreen(
                    onQuestionarioClick = { questionarioId ->
                        navController.navigate("questionarios/$questionarioId")
                    },
                    onCriarQuestionarioClick = {
                        navController.navigate("criarQuestionario")
                    },
                    criadorEmail = userViewModel.user.value?.email ?: "",
                    userViewModel = userViewModel,
                    navController = navController,
                )
            }
            composable(route = "criarQuestionario") {
                CriarQuestionarioScreen(
                    onSalvar = {
                        navController.popBackStack()
                    },
                    questionarioViewModel = questionarioViewModel,
                    fireBaseViewModel = userViewModel,
                    navController = navController
                )
            }
            composable(route = "questionarios/{questionarioId}") { backStackEntry ->
                val questionarioId = backStackEntry.arguments?.getString("questionarioId") ?: return@composable
                PerguntaListScreen(
                    questionarioId = questionarioId,
                    questionarioViewModel = questionarioViewModel,
                    onAdicionarPerguntaClick = {
                        navController.navigate("adicionarPergunta/$questionarioId")
                    },
                    navController = navController,
                    onPerguntaClick = { perguntaId ->
                        navController.navigate("questionarios/$questionarioId/$perguntaId")
                    },
                )
            }
            composable(route = "questionarios/{questionarioId}/{perguntaId}") { backStackEntry ->
                val questionarioId = backStackEntry.arguments?.getString("questionarioId") ?: return@composable
                val perguntaId = backStackEntry.arguments?.getString("perguntaId") ?: return@composable
                PerguntaScreen(
                    perguntaId = perguntaId,
                    questionarioId = questionarioId,
                    navController = navController,
                    perguntaViewModel = perguntaViewModel
                )
            }
            composable(route = "adicionarPergunta/{questionarioId}?edit={edit}") { backStackEntry ->
                val questionarioId = backStackEntry.arguments?.getString("questionarioId") ?: return@composable
                val isEdit = backStackEntry.arguments?.getString("edit") == "true"
                val pergunta = if (isEdit) {
                    perguntaViewModel.pergunta.value // Obtendo a pergunta para editar
                } else null
                AdicionarPerguntaScreen(
                    onSalvarPergunta = { question ->
                        if (isEdit) {
                            perguntaViewModel.editarPergunta(question.id, question) // Aqui chama a função de edição
                        } else {
                            questionarioViewModel.adicionarPergunta(questionarioId, question)
                        }
                        navController.popBackStack()
                    },
                    navController = navController,
                    pergunta = pergunta // Passa a pergunta se for edição
                )
            }
            composable(route = "configurarQuestionario/{questionarioId}") { backStackEntry ->
                val questionarioId = backStackEntry.arguments?.getString("questionarioId") ?: return@composable
                ConfigurarQuestionarioScreen(
                    questionarioId = questionarioId,
                    navController = navController,
                    questionarioViewModel = questionarioViewModel,
                )
            }
            composable("joinQuiz") {
                JoinQuizScreen(
                    userViewModel = userViewModel,
                    questionarioViewModel = questionarioViewModel,
                    navController = navController,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable("jogarQuiz/{questionarioId}/{perguntaNumber}") { backStackEntry ->
                val questionarioId = backStackEntry.arguments?.getString("questionarioId") ?: return@composable
                val perguntaNumber = backStackEntry.arguments?.getString("perguntaNumber")?.toIntOrNull() ?: 0
                JogarQuizScreen(
                    questionarioId = questionarioId,
                    perguntaNumber = perguntaNumber,
                    userViewModel = userViewModel,
                    questionarioViewModel = questionarioViewModel,
                    perguntaViewModel = perguntaViewModel,
                    respostasViewModel = respostasViewModel,
                    navController = navController
                )
            }
            composable("resultadosJogador/{questionarioId}") { backStackEntry ->
                val questionarioId = backStackEntry.arguments?.getString("questionarioId") ?: ""
                ResultadosJogadorScreen(
                    questionarioId = questionarioId,
                    userViewModel = userViewModel,
                    questionarioViewModel = questionarioViewModel,
                    perguntaViewModel = perguntaViewModel,
                    navController = navController
                )
            }
            composable("resultadosCriador/{questionarioId}") { backStackEntry ->
                val questionarioId = backStackEntry.arguments?.getString("questionarioId") ?: ""
                ResultadosCriadorScreen(
                    questionarioId = questionarioId,
                    userViewModel = userViewModel,
                    questionarioViewModel = questionarioViewModel,
                    perguntaViewModel = perguntaViewModel,
                    navController = navController
                )
            }
        }
    }
}
