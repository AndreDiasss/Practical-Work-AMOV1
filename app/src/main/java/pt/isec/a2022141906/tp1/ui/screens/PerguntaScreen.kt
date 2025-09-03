package pt.isec.a2022141906.tp1.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import pt.isec.a2022141906.tp1.R
import pt.isec.a2022141906.tp1.ui.viewmodels.PerguntaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerguntaScreen(
    perguntaId: String,
    questionarioId: String,
    navController: NavHostController,
    perguntaViewModel: PerguntaViewModel
) {
    val pergunta by perguntaViewModel.pergunta.observeAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(perguntaId) {
        perguntaViewModel.buscarPergunta(perguntaId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.question) + " '${pergunta?.titulo}'") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
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
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.padding(start = 32.dp)
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = stringResource(R.string.delete))
                }
                FloatingActionButton(
                    onClick = {
                        if (pergunta != null)
                            navController.navigate("adicionarPergunta/${perguntaId}?edit=true")
                    }
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = stringResource(R.string.edit))
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (pergunta == null) {
                Text(text = stringResource(R.string.loading_question) + "...")
            } else {
                Text(text = stringResource(R.string.title) + ": ${pergunta!!.titulo}")
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = stringResource(R.string.type) + ": ${pergunta!!.tipo.name}")
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = stringResource(R.string.text) + ": ${pergunta!!.texto}")
                Spacer(modifier = Modifier.height(16.dp))
                if (pergunta!!.tipo.toString() == "SIM_NAO") {
                    Text(text = stringResource(R.string.options) + ": ${pergunta!!.opcoes!!.joinToString(", ")}")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = stringResource(R.string.right_answer) + ": ${pergunta!!.opcoesCertas!!.joinToString(", ")}")
                } else if (pergunta!!.tipo.toString() == "ESCOLHA_MULTIPLA") {
                    Text(text = stringResource(R.string.options) + ": ${pergunta!!.opcoes!!.joinToString(", ")}")
                    Spacer(modifier = Modifier.height(16.dp))
                    if (pergunta!!.opcoesCertas!!.size > 1)
                        Text(text = stringResource(R.string.right_answers) + ": ${pergunta!!.opcoesCertas!!.joinToString(", ")}")
                    else
                        Text(text = stringResource(R.string.right_answer) + ": ${pergunta!!.opcoesCertas?.get(0)}")
                } else if (pergunta!!.tipo.toString() == "PREENCHIMENTO") {
                    Text(text = stringResource(R.string.options) + ": ${pergunta!!.opcoes!!.joinToString(", ")}")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = stringResource(R.string.right_answers) + ": ${pergunta!!.opcoesCertas!!.joinToString(", ")}")
                } else if (pergunta!!.tipo.toString() == "CORRESPONDENCIA") {
                    Text(text = stringResource(R.string.left_options) + ": ${pergunta!!.opcoes!!.subList(0, pergunta!!.opcoes!!.size / 2).joinToString(", ")}")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = stringResource(R.string.right_options) + ": ${pergunta!!.opcoes!!.subList(pergunta!!.opcoes!!.size / 2, pergunta!!.opcoes!!.size).joinToString(", ")}")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = stringResource(R.string.connections) + ": ${pergunta!!.opcoesCertas!!.joinToString(", ")}")
                } else if (pergunta!!.tipo.toString() == "ORDENACAO") {
                    Text(text = stringResource(R.string.options) +  ": ${pergunta!!.opcoes!!.joinToString(", ")}")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = stringResource(R.string.correct_order) +  ": ${pergunta!!.opcoesCertas?.get(0)}")
                }
                Spacer(modifier = Modifier.height(16.dp))
                pergunta!!.imagemUri?.let {
                    AsyncImage(
                        model = it,
                        contentDescription = stringResource(R.string.question_image),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Diálogo de confirmação para deletar
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text(stringResource(R.string.confirmation)) },
                text = { Text(stringResource(R.string.confirm_delete_question)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            perguntaViewModel.eliminarPergunta(perguntaId, questionarioId)
                            navController.popBackStack() // Volta para a lista
                        }
                    ) {
                        Text(stringResource(R.string.delete))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
    }
}
