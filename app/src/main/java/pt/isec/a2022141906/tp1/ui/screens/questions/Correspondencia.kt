package pt.isec.a2022141906.tp1.ui.screens.questions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pt.isec.a2022141906.tp1.R

@Composable
fun Correspondencia(
    texto: String,
    onTextoChange: (String) -> Unit,
    respostasEsquerda: List<String>,
    respostasDireita: List<String>,
    onRespostasEsquerdaChange: (List<String>) -> Unit,
    onRespostasDireitaChange: (List<String>) -> Unit,
    opcoesCertas: List<String>,
    onOpcoesCertasChange: (List<String>) -> Unit
) {
    // Garante que a lista começa com pelo menos duas opções
    LaunchedEffect(Unit) {
        if (respostasEsquerda.isEmpty()) {
            onRespostasEsquerdaChange(listOf("", ""))
        }
        if (respostasDireita.isEmpty()) {
            onRespostasDireitaChange(listOf("",""))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(text = stringResource(R.string.write_question) + ":", style = MaterialTheme.typography.bodyLarge)
        // Texto da pergunta
        OutlinedTextField(
            value = texto,
            onValueChange = { onTextoChange(it) } ,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(R.string.question)) },
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(text = stringResource(R.string.write_options) + ":", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Column(modifier = Modifier.weight(1f)) {
                // Lista de campos para editar as respostas
                respostasEsquerda.forEachIndexed { index, resposta ->
                    OutlinedTextField(
                        value = resposta,
                        onValueChange = {
                            val newRespostas = respostasEsquerda.toMutableList()
                            newRespostas[index] = it
                            onRespostasEsquerdaChange(newRespostas)
                        },
                        label = { Text(text = stringResource(R.string.option) + " ${index + 1}" + stringResource(R.string.left)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                // Lista de campos para editar as respostas
                respostasDireita.forEachIndexed { index, resposta ->
                    OutlinedTextField(
                        value = resposta,
                        onValueChange = {
                            val newRespostas = respostasDireita.toMutableList()
                            newRespostas[index] = it
                            onRespostasDireitaChange(newRespostas)
                        },
                        label = { Text(text = stringResource(R.string.option) + " ${index + 1}" + stringResource(R.string.right)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                }
            }
        }

        // Botão para adicionar uma nova resposta
        if (respostasEsquerda.size < 6) {
            Button(
                onClick = {
                    onRespostasEsquerdaChange(respostasEsquerda + "")
                    onRespostasDireitaChange(respostasDireita + "")
                },
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(text = stringResource(R.string.add_option))
            }
        }

        // Botão para remover uma resposta (se houver mais de 2)
        if (respostasEsquerda.size > 2) {
            Button(
                onClick = {
                    onRespostasEsquerdaChange(respostasEsquerda.dropLast(1))
                    onRespostasDireitaChange(respostasDireita.dropLast(1))
                },
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(text = stringResource(R.string.remove_option))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = stringResource(R.string.choose_correct_connections) + ":", style = MaterialTheme.typography.bodyLarge)
        val correspondencias = remember { mutableStateListOf<String>() }
        respostasEsquerda.forEachIndexed { index, respostaEsquerda ->
            var isDropdownExpanded by remember { mutableStateOf(false) }
            if (correspondencias.size <= index)
                correspondencias.add("")
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
                    respostasDireita.forEach { respostaDireita ->
                        val resposta = "$respostaEsquerda-$respostaDireita"
                        DropdownMenuItem(
                            onClick = {
                                correspondencias[index] = resposta
                                isDropdownExpanded = false
                            },
                            text = { Text(resposta) }
                        )
                    }
                }
            }
        }
        onOpcoesCertasChange(correspondencias.toMutableList())

        Spacer(modifier = Modifier.height(72.dp))
    }
}