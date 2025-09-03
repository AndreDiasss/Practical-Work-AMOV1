package pt.isec.a2022141906.tp1.ui.screens.questions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pt.isec.a2022141906.tp1.R

@Composable
fun Preenchimento(
    texto: String,
    onTextoChange: (String) -> Unit,
    respostas: List<String>,
    onRespostasChange: (List<String>) -> Unit,
    opcoesCertas: String,
    onOpcoesCertasChange: (String) -> Unit
) {
    // Estado local para armazenar os elementos dinâmicos
    val elementos = remember { mutableStateListOf<Elemento>() }
    var textDone by remember { mutableStateOf(false) }

    if (texto.isNotBlank() && !textDone) {
        texto.split("_".repeat(8)).forEachIndexed { index, text ->
            if (text.isNotBlank()) {
                elementos.add(Elemento.Texto(text.trim()))
            }
            if (index < texto.split("_".repeat(8)).size - 1) {
                elementos.add(Elemento.Espaco("", respostas))
            }
        }
    }
    textDone = true

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Lista de elementos dinâmicos
        elementos.forEachIndexed { index, elemento ->
            when (elemento) {
                is Elemento.Texto -> {
                    OutlinedTextField(
                        value = elemento.texto,
                        onValueChange = {
                            elementos[index] = Elemento.Texto(it)
                            val novoTexto = elementos.joinToString(separator = "") { elemento ->
                                when (elemento) {
                                    is Elemento.Texto -> elemento.texto
                                    is Elemento.Espaco -> " ________ "
                                }
                            }
                            onTextoChange(novoTexto)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        label = { Text(text = stringResource(R.string.text)) }
                    )
                }
                is Elemento.Espaco -> {
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Text(text = stringResource(R.string.space), style = MaterialTheme.typography.bodyMedium)
                        var isDropdownExpanded by remember { mutableStateOf(false) }

                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = elemento.valor,
                                onValueChange = {}, // Desabilita edição direta
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text(text = stringResource(R.string.correct_answer)) },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        modifier = Modifier.clickable { isDropdownExpanded = true }
                                    )
                                },
                                readOnly = true
                            )
                            DropdownMenu(
                                expanded = isDropdownExpanded,
                                onDismissRequest = { isDropdownExpanded = false }
                            ) {
                                respostas.forEach { opcao ->
                                    DropdownMenuItem(
                                        text = { Text(opcao) },
                                        onClick = {
                                            elementos[index] = Elemento.Espaco(opcao, respostas)
                                            val novoTexto = elementos.joinToString(separator = "") { elemento ->
                                                when (elemento) {
                                                    is Elemento.Texto -> elemento.texto
                                                    is Elemento.Espaco -> " ________ "
                                                }
                                            }
                                            onTextoChange(novoTexto)

                                            // Atualiza automaticamente as respostas corretas
                                            val novasOpcoesCertas = elementos.filterIsInstance<Elemento.Espaco>().map { it.valor }
                                            onOpcoesCertasChange(novasOpcoesCertas.toString())

                                            isDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botão para adicionar novo texto
        Button(
            onClick = {
                elementos.add(Elemento.Texto(""))
            },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(text = stringResource(R.string.add_text))
        }

        // Botão para adicionar novo espaço
        Button(
            onClick = {
                elementos.add(Elemento.Espaco("", respostas))
            },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(text = stringResource(R.string.add_space))
        }

        // Botão para remover o último elemento, se houver
        if (elementos.isNotEmpty()) {
            Button(
                onClick = {
                    elementos.removeLastOrNull()
                    // Atualiza automaticamente as respostas corretas ao remover elementos
                    val novasOpcoesCertas = elementos.filterIsInstance<Elemento.Espaco>().map { it.valor }
                    onOpcoesCertasChange(novasOpcoesCertas.toString())
                },
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(text = stringResource(R.string.remove_element))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Campo para editar respostas disponíveis
        Text(text = stringResource(R.string.write_options) + ":", style = MaterialTheme.typography.bodyLarge)
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

        // Botão para adicionar uma nova opção
        Button(
            onClick = { onRespostasChange(respostas + "") },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(text = stringResource(R.string.add_option))
        }

        // Botão para remover a última opção (se houver mais de 2)
        if (respostas.size > 2) {
            Button(
                onClick = { onRespostasChange(respostas.dropLast(1)) },
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(text = stringResource(R.string.remove_option))
            }
        }

        Spacer(modifier = Modifier.height(72.dp))
    }
}

// Modelos para representar os elementos
sealed class Elemento {
    data class Texto(val texto: String) : Elemento()
    data class Espaco(val valor: String, val opcoes: List<String>) : Elemento()
}