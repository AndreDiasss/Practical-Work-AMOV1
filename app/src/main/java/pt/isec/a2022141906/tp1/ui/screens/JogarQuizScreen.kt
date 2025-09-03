package pt.isec.a2022141906.tp1.ui.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pt.isec.a2022141906.tp1.R
import pt.isec.a2022141906.tp1.R.*
import pt.isec.a2022141906.tp1.models.TipoPergunta
import pt.isec.a2022141906.tp1.ui.screens.questions.Elemento
import pt.isec.a2022141906.tp1.ui.viewmodels.PerguntaViewModel
import pt.isec.a2022141906.tp1.ui.viewmodels.QuestionarioViewModel
import pt.isec.a2022141906.tp1.ui.viewmodels.RespostasViewModel
import pt.isec.a2022141906.tp1.ui.viewmodels.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("DefaultLocale")
@Composable
fun JogarQuizScreen(
    questionarioId: String,
    perguntaNumber: Int,
    userViewModel: UserViewModel,
    questionarioViewModel: QuestionarioViewModel,
    perguntaViewModel: PerguntaViewModel,
    respostasViewModel: RespostasViewModel,
    navController: NavController,
) {
    val context = LocalContext.current
    val questionario by questionarioViewModel.questionario.observeAsState()
    val pergunta by perguntaViewModel.pergunta.observeAsState()
    val tempoRestante by questionarioViewModel.tempoRestante.observeAsState(-1)
    var resposta by remember { mutableStateOf("") }
    var respostas by remember { mutableStateOf(listOf<String>()) }

    // Carregar questionário ao iniciar
    LaunchedEffect(questionarioId) {
        questionarioViewModel.buscarQuestionario(questionarioId)
    }

    // Carregar pergunta específica
    LaunchedEffect(perguntaNumber, questionario) {
        val perguntaId = questionario?.perguntas?.getOrNull(perguntaNumber)
        if (perguntaId != null) {
            perguntaViewModel.buscarPergunta(perguntaId)
        }
    }

    // Iniciar timer ao carregar questionário
    LaunchedEffect(questionario?.tempo) {
        questionario?.let {
            if (tempoRestante == -1) {
                questionarioViewModel.iniciarTimer((it.tempo ?: 1) * 60)
            }
        }
    }

    LaunchedEffect(tempoRestante) {
        if(tempoRestante == 0){
            // Submete todas as respostas ao acabar o tempo
            respostasViewModel.submeterRespostas(
                questionarioId = questionario!!.id,
                userEmail = userViewModel.user.value!!.email
            )
            Toast.makeText(context, context.getString(string.time_finished) + "!", Toast.LENGTH_SHORT).show()
            questionarioViewModel.pararTimer()
            navController.navigate("resultadosJogador/${questionario!!.id}")
        }
    }

    val minutos = tempoRestante / 60
    val segundos = tempoRestante % 60
    val tempoFormatado = String.format("%02d:%02d", minutos, segundos)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.time_left) + ": $tempoFormatado") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (questionario != null && pergunta != null) {
                        val perguntaId = pergunta!!.id
                        when (pergunta!!.tipo) {
                            TipoPergunta.SIM_NAO -> {
                                respostasViewModel.adicionarResposta(perguntaId, listOf(resposta))
                            }
                            TipoPergunta.ESCOLHA_MULTIPLA -> {
                                respostasViewModel.adicionarResposta(perguntaId, respostas)
                            }
                            TipoPergunta.CORRESPONDENCIA -> {
                                respostasViewModel.adicionarResposta(perguntaId, respostas)
                            }
                            TipoPergunta.ORDENACAO -> {
                                respostasViewModel.adicionarResposta(perguntaId, listOf(respostas.toString()))
                            }
                            TipoPergunta.PREENCHIMENTO -> {
                                respostasViewModel.adicionarResposta(perguntaId, listOf(respostas.toString()))
                            }
                            TipoPergunta.ASSOCIACAO -> TODO()
                            TipoPergunta.INDICACAO_PALAVRAS -> TODO()
                        }

                        if (perguntaNumber < (questionario!!.perguntas.size - 1)) {
                            navController.navigate("jogarQuiz/${questionario!!.id}/${perguntaNumber + 1}") {
                                popUpTo("jogarQuiz/${questionario!!.id}/$perguntaNumber") { inclusive = true }
                            }
                        } else {
                            // Submete todas as respostas ao finalizar o quiz
                            respostasViewModel.submeterRespostas(
                                questionarioId = questionario!!.id,
                                userEmail = userViewModel.user.value!!.email
                            )
                            Toast.makeText(context, context.getString(R.string.quiz_completed) + "!", Toast.LENGTH_SHORT).show()
                            questionarioViewModel.pararTimer()
                            navController.navigate("resultadosJogador/${questionario!!.id}")
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = stringResource(R.string.next_question)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (pergunta?.tipo != TipoPergunta.PREENCHIMENTO) {
                Text(pergunta?.texto ?: stringResource(R.string.loading_question))
                Spacer(modifier = Modifier.height(16.dp))
            }

            when (pergunta?.tipo) {
                TipoPergunta.SIM_NAO -> {
                    var isDropdownExpanded by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = resposta,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(text = stringResource(string.answer)) },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = stringResource(string.open_menu),
                                    modifier = Modifier.clickable { isDropdownExpanded = true }
                                )
                            }
                        )
                        DropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = { isDropdownExpanded = false }
                        ) {
                            listOf(stringResource(string.yes_true), stringResource(string.no_false)).forEach { opcao ->
                                DropdownMenuItem(
                                    onClick = {
                                        resposta = opcao
                                        isDropdownExpanded = false
                                    },
                                    text = { Text(opcao) }
                                )
                            }
                        }
                    }
                }
                TipoPergunta.ESCOLHA_MULTIPLA -> {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        pergunta?.opcoes?.forEachIndexed { index, opcao ->
                            val letra = ('a' + index).toString()
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Checkbox(
                                    checked = respostas.contains(opcao),
                                    onCheckedChange = { isChecked ->
                                        respostas = if (isChecked) {
                                            respostas + opcao
                                        } else {
                                            respostas - opcao
                                        }
                                    }
                                )
                                Text(text = "$letra) $opcao")
                            }
                        }
                    }
                }
                TipoPergunta.CORRESPONDENCIA -> {
                    val metade = pergunta?.opcoes?.size?.div(2)
                    val respostasEsquerda = pergunta?.opcoes?.subList(0, metade!!)
                    val respostasDireita = pergunta?.opcoes?.subList(metade!!, pergunta?.opcoes?.size!!)
                    val correspondencias = remember { mutableStateListOf<String>() }
                    while (correspondencias.size < metade!!)
                        correspondencias.add("")
                    respostasEsquerda?.forEachIndexed { index, respostaEsquerda ->
                        var isDropdownExpanded by remember { mutableStateOf(false) }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = correspondencias[index],
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text(respostaEsquerda) },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = stringResource(R.string.open_menu),
                                        modifier = Modifier.clickable { isDropdownExpanded = true }
                                    )
                                }
                            )
                            DropdownMenu(
                                expanded = isDropdownExpanded,
                                onDismissRequest = { isDropdownExpanded = false }
                            ) {
                                respostasDireita?.forEach { respostaDireita ->
                                    val resp = "$respostaEsquerda-$respostaDireita"
                                    DropdownMenuItem(
                                        onClick = {
                                            correspondencias[index] = resp
                                            respostas = respostas + resp
                                            isDropdownExpanded = false
                                        },
                                        text = { Text(resp) }
                                    )
                                }
                            }
                        }
                    }
                    respostas = correspondencias.toMutableList()
                }
                TipoPergunta.ORDENACAO -> {
                    val ordem = remember { mutableStateListOf<String>() }
                    pergunta?.opcoes?.forEachIndexed { index, _ ->
                        var isDropdownExpanded by remember { mutableStateOf(false) }
                        if (ordem.size <= index)
                            ordem.add("")

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = ordem[index],
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("${index + 1}: ") },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = stringResource(string.open_menu),
                                        modifier = Modifier.clickable {
                                            isDropdownExpanded = true
                                        }
                                    )
                                }
                            )
                            DropdownMenu(
                                expanded = isDropdownExpanded,
                                onDismissRequest = { isDropdownExpanded = false }
                            ) {
                                pergunta?.opcoes?.forEach { resposta ->
                                    DropdownMenuItem(
                                        onClick = {
                                            ordem[index] = resposta
                                            isDropdownExpanded = false
                                        },
                                        text = { Text(resposta) }
                                    )
                                }
                            }
                        }
                    }
                    respostas = ordem.toMutableList()
                }
                TipoPergunta.PREENCHIMENTO -> {
                    var newTexto = ""
                    val espacos = remember { mutableStateListOf<String>() }
                    var numEspacos = 0
                    pergunta?.texto?.split("_".repeat(8))?.forEachIndexed { index, text ->
                        if (text.isNotBlank()) {
                            newTexto += text
                        }
                        if (index < pergunta?.texto?.split("_".repeat(8))?.size!! - 1) {
                            newTexto += ((++numEspacos).toString() + ".________")
                        }
                    }
                    while (espacos.size < numEspacos)
                        espacos.add("")
                    Column {
                        Text(newTexto)
                        Spacer(modifier = Modifier.height(16.dp))
                        // Renderiza os OutlineTextFields para cada índice encontrado
                        espacos.forEachIndexed { i, _ ->
                            var isDropdownExpanded by remember { mutableStateOf(false) }
                            OutlinedTextField(
                                value = espacos[i],
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("${i+1}: ") },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = stringResource(string.open_menu),
                                        modifier = Modifier.clickable {
                                            isDropdownExpanded = true
                                        }
                                    )
                                }
                            )
                            DropdownMenu(
                                expanded = isDropdownExpanded,
                                onDismissRequest = { isDropdownExpanded = false }
                            ) {
                                pergunta?.opcoes?.forEach { opcao ->
                                    DropdownMenuItem(
                                        onClick = {
                                            espacos[i] = opcao
                                            isDropdownExpanded = false
                                        },
                                        text = { Text(opcao) }
                                    )
                                }
                            }
                        }
                    }
                    respostas = espacos.toMutableList()
                }
                TipoPergunta.ASSOCIACAO -> {
                    // Implementar lógica
                }
                TipoPergunta.INDICACAO_PALAVRAS -> {
                    // Implementar lógica
                }
                else -> {}
            }
        }
    }
}