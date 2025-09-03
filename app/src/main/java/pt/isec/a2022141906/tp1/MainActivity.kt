package pt.isec.a2022141906.tp1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pt.isec.a2022141906.tp1.ui.screens.GerirQuestionariosScreen
import pt.isec.a2022141906.tp1.ui.screens.LoginScreen
import pt.isec.a2022141906.tp1.ui.screens.RegisterScreen
import pt.isec.a2022141906.tp1.ui.theme.FirebaseTheme
import pt.isec.a2022141906.tp1.ui.viewmodels.UserViewModel
import pt.isec.a2022141906.tp1.ui.viewmodels.PerguntaViewModel
import pt.isec.a2022141906.tp1.ui.viewmodels.QuestionarioViewModel
import pt.isec.a2022141906.tp1.ui.viewmodels.RespostasViewModel

class MainActivity : ComponentActivity() {
    val fireBaseViewModel : UserViewModel by viewModels()
    val questionarioViewModel : QuestionarioViewModel by viewModels()
    val perguntaViewModel : PerguntaViewModel by viewModels()
    val respostasViewModel : RespostasViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            FirebaseTheme {
                Surface {
                    NavHost(
                        navController = navController,
                        startDestination = "login",
                    ) {
                        composable("login") {
                            LoginScreen(
                                viewModel = fireBaseViewModel,
                                onSuccess = {
                                    navController.navigate("manageQuizes") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                navController = navController,
                            )
                        }
                        composable("register") {
                            RegisterScreen(
                                viewModel = fireBaseViewModel,
                                onSuccess = {
                                    navController.navigate("manageQuizes") {
                                        popUpTo("register") { inclusive = true }
                                    }
                                },
                                navController = navController,
                            )
                        }
                        composable("manageQuizes") {
                            GerirQuestionariosScreen(
                                userViewModel = fireBaseViewModel,
                                questionarioViewModel = questionarioViewModel,
                                perguntaViewModel = perguntaViewModel,
                                respostasViewModel = respostasViewModel,
                                onSignOut = {
                                    fireBaseViewModel.signOut()
                                    navController.navigate("login") {
                                        popUpTo("manageQuizes") { inclusive = true }
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}