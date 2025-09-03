package pt.isec.a2022141906.tp1.ui.screens.questions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
fun Ordenacao(
    texto: String,
    onTextoChange: (String) -> Unit,
    respostas: List<String>,
    onRespostasChange: (List<String>) -> Unit,
    ordem: String,
    onOrdemChange: (String) -> Unit
) {
    // Garante que a lista começa com pelo menos duas opções
    LaunchedEffect(Unit) {
        if (respostas.isEmpty()) {
            onRespostasChange(listOf("", ""))
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
        // Lista de campos para editar as respostas
        respostas.forEachIndexed { index, resposta ->
            OutlinedTextField(
                value = resposta,
                onValueChange = {
                    val newRespostas = respostas.toMutableList()
                    newRespostas[index] = it
                    onRespostasChange(newRespostas)
                },
                label = { Text(text = stringResource(R.string.option) + " ${index + 1}") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )
        }

        // Botão para adicionar uma nova resposta
        if (respostas.size < 6) {
            Button(
                onClick = { onRespostasChange(respostas + "") },
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(text = stringResource(R.string.add_option))
            }
        }

        // Botão para remover uma resposta (se houver mais de 2)
        if (respostas.size > 2) {
            Button(
                onClick = { onRespostasChange(respostas.dropLast(1)) },
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(text = stringResource(R.string.remove_option))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Escolher a ordem certa
        Text(text = stringResource(R.string.choose_correct_order) + ":", style = MaterialTheme.typography.bodyLarge)
        val ordem = remember { mutableStateListOf<String>() }
        respostas.forEachIndexed { index, _ ->
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
                            contentDescription = stringResource(R.string.open_menu),
                            modifier = Modifier.clickable { isDropdownExpanded = true }
                        )
                    }
                )
                DropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false }
                ) {
                    respostas.forEach { resposta ->
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
        onOrdemChange(ordem.toString())

        Spacer(modifier = Modifier.height(72.dp))
    }
}