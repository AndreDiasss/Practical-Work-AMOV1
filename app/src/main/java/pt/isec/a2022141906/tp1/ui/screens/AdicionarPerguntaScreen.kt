package pt.isec.a2022141906.tp1.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import pt.isec.a2022141906.tp1.R
import pt.isec.a2022141906.tp1.models.Pergunta
import pt.isec.a2022141906.tp1.models.TipoPergunta
import pt.isec.a2022141906.tp1.ui.screens.questions.Correspondencia
import pt.isec.a2022141906.tp1.ui.screens.questions.EscolhaMultipla
import pt.isec.a2022141906.tp1.ui.screens.questions.Ordenacao
import pt.isec.a2022141906.tp1.ui.screens.questions.Preenchimento
import pt.isec.a2022141906.tp1.ui.screens.questions.SimNao

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdicionarPerguntaScreen(
    onSalvarPergunta: (Pergunta) -> Unit,
    navController: NavController,
    pergunta: Pergunta? = null
) {
    var titulo by remember { mutableStateOf(pergunta?.titulo ?: "") }
    var tipoSelecionado by remember { mutableStateOf(pergunta?.tipo) }
    var texto by remember { mutableStateOf(pergunta?.texto ?: "") }
    var respostas by remember { mutableStateOf(pergunta?.opcoes ?: emptyList()) }
    var respostasEsquerda by remember { mutableStateOf<List<String>>(emptyList()) }
    var respostasDireita by remember { mutableStateOf<List<String>>(emptyList()) }
    var opcaoCerta by remember { mutableStateOf("") }
    var opcoesCertas by remember { mutableStateOf<List<String>>(emptyList()) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var imagemUri by remember { mutableStateOf(pergunta?.imagemUri?.let { Uri.parse(it) }) }

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imagemUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (pergunta == null) {
                            stringResource(R.string.add_question)
                        } else {
                            stringResource(R.string.edit_question) + " '${pergunta.titulo}'"
                        }
                    )
                },
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (titulo.isBlank() || texto.isBlank() || tipoSelecionado == null || (respostas.any { it.isBlank() } && (respostasEsquerda.any { it.isBlank() } || respostasDireita.any { it.isBlank() }) ) || (opcaoCerta.isBlank() && (opcoesCertas.isEmpty() || opcoesCertas.any {it.isBlank()}))) {
                        Toast.makeText(context, context.getString(R.string.fill_mandatory_fields) + "!", Toast.LENGTH_SHORT).show()
                    } else {
                        val novasOpcoesCertas = if (opcaoCerta.isNotBlank()) {
                            listOf(opcaoCerta)
                        } else {
                            opcoesCertas
                        }

                        respostas = respostas.ifEmpty {
                            respostasEsquerda + respostasDireita
                        }

                        val novaPergunta = Pergunta(
                            id = pergunta?.id ?: "", // Use o ID existente para edição ou deixe vazio para uma nova pergunta
                            titulo = titulo,
                            texto = texto,
                            tipo = tipoSelecionado!!,
                            opcoes = respostas,
                            opcoesCertas = novasOpcoesCertas,
                            imagemUri = imagemUri?.toString()
                        )

                        if (pergunta != null) {
                            // Atualizar a pergunta existente
                            onSalvarPergunta(novaPergunta)
                            Toast.makeText(context, context.getString(R.string.question_edited) + "!", Toast.LENGTH_SHORT).show()
                        } else {
                            // Criar uma nova pergunta
                            onSalvarPergunta(novaPergunta)
                            Toast.makeText(context, context.getString(R.string.question_added) + "!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            ) {
                Icon(imageVector = Icons.Default.Done, contentDescription = stringResource(R.string.save_question))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Título
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text(text = stringResource(R.string.question_title)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Tipo de pergunta
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = tipoSelecionado?.name ?: "",
                    onValueChange = {},
                    label = { Text(text = stringResource(R.string.question_type)) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = pergunta == null, // Desabilita o campo se pergunta não for null
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = stringResource(R.string.open_menu),
                            modifier = Modifier.clickable(
                                enabled = pergunta == null, // Só permite abrir o dropdown se pergunta for null
                                onClick = { isDropdownExpanded = !isDropdownExpanded }
                            )
                        )
                    }
                )

                // Condicionalmente exibe o DropdownMenu, apenas se pergunta for null
                if (pergunta == null) {
                    DropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false }
                    ) {
                        TipoPergunta.entries.forEach { tipo ->
                            DropdownMenuItem(
                                onClick = {
                                    tipoSelecionado = tipo
                                    isDropdownExpanded = false
                                    respostas = emptyList()
                                    opcaoCerta = ""
                                },
                                text = { Text(text = tipo.name) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Imagem
            Text(text = stringResource(R.string.image_optional) + ":", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imagemUri != null) {
                    AsyncImage(
                        model = imagemUri,
                        contentDescription = stringResource(R.string.image_selected),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(text = stringResource(R.string.press_select_image))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Respostas e tipo
            when (tipoSelecionado) {
                TipoPergunta.SIM_NAO -> {
                    respostas = listOf(
                        stringResource(R.string.yes_true),
                        stringResource(R.string.no_false)
                    )
                    SimNao(
                        texto = texto,
                        onTextoChange = { texto = it },
                        opcaoCerta = opcaoCerta,
                        onOpcaoCertaChange = { opcaoCerta = it }
                    )
                }
                TipoPergunta.ESCOLHA_MULTIPLA -> {
                    EscolhaMultipla(
                        texto = texto,
                        onTextoChange = { texto = it },
                        respostas = respostas,
                        onRespostasChange = { respostas = it },
                        opcoesCertas = opcoesCertas,
                        onOpcoesCertasChange = { opcoesCertas = it },
                    )
                }
                TipoPergunta.CORRESPONDENCIA -> {
                    if (respostas.isNotEmpty()) {
                        val metade = respostas.size / 2
                        respostasEsquerda = respostas.subList(0, metade)
                        respostasDireita = respostas.subList(metade, respostas.size)
                    }
                    Correspondencia(
                        texto = texto,
                        onTextoChange = { texto = it },
                        respostasEsquerda = respostasEsquerda,
                        respostasDireita = respostasDireita,
                        onRespostasEsquerdaChange = { respostasEsquerda = it },
                        onRespostasDireitaChange = { respostasDireita = it },
                        opcoesCertas = opcoesCertas,
                        onOpcoesCertasChange = { opcoesCertas = it },
                    )
                }
                TipoPergunta.ORDENACAO -> {
                    Ordenacao(
                        texto = texto,
                        onTextoChange = { texto = it },
                        respostas = respostas,
                        onRespostasChange = { respostas = it },
                        ordem = opcaoCerta,
                        onOrdemChange = { opcaoCerta = it }
                    )
                }
                TipoPergunta.PREENCHIMENTO -> {
                    Preenchimento(
                        texto = texto,
                        onTextoChange = { texto = it },
                        respostas = respostas,
                        onRespostasChange = { respostas = it },
                        opcoesCertas = opcaoCerta,
                        onOpcoesCertasChange = { opcaoCerta = it },
                    )
                }
                TipoPergunta.ASSOCIACAO -> {

                }
                TipoPergunta.INDICACAO_PALAVRAS -> {

                }
                else -> {}
            }
        }
    }
}