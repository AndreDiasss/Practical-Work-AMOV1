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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import pt.isec.a2022141906.tp1.ui.viewmodels.UserViewModel
import pt.isec.a2022141906.tp1.models.Questionario
import pt.isec.a2022141906.tp1.ui.viewmodels.QuestionarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CriarQuestionarioScreen(
    onSalvar: () -> Unit,
    fireBaseViewModel: UserViewModel,
    questionarioViewModel: QuestionarioViewModel,
    navController: NavController
) {
    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    val perguntas by remember { mutableStateOf(listOf<String>()) }
    var imagemUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> imagemUri = uri }
    )

    Column(modifier = Modifier.padding(8.dp)) {
        TopAppBar(
            title = { Text(text = stringResource(R.string.add_quiz)) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                }
            }
        )
        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text(stringResource(R.string.title)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = descricao,
            onValueChange = { descricao = it },
            label = { Text(stringResource(R.string.description)) }
        )
        Spacer(modifier = Modifier.padding(8.dp))

        // Botão para selecionar imagem
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

        // Mostrar a imagem selecionada (se houver)
        imagemUri?.let { uri ->
            Spacer(modifier = Modifier.height(16.dp))
            AsyncImage(
                model = uri,
                contentDescription = stringResource(R.string.quiz_image),
                modifier = Modifier
                    .size(150.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }

        Spacer(modifier = Modifier.padding(16.dp))
        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                if (titulo.isNotBlank()) {
                    val questionario = Questionario(
                        id = questionarioViewModel.generateUniqueId(),
                        criadorEmail = fireBaseViewModel.user.value?.email ?: "",
                        titulo = titulo.trim(),
                        descricao = descricao.trim(),
                        perguntas = perguntas, // IDs de perguntas
                        imagemUri = imagemUri?.toString() // Salva URI como String
                    )
                    questionarioViewModel.salvarQuestionario(questionario)
                    onSalvar()
                    Toast.makeText(context, context.getString(R.string.quiz_added) + "!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, context.getString(R.string.fill_mandatory_fields) + "!", Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            Text(text = stringResource(R.string.save_quiz))
        }
    }
}