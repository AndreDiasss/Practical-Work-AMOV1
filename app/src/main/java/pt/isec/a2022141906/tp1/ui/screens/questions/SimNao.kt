package pt.isec.a2022141906.tp1.ui.screens.questions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pt.isec.a2022141906.tp1.R

@Composable
fun SimNao(
    texto: String,
    onTextoChange: (String) -> Unit,
    opcaoCerta: String?,
    onOpcaoCertaChange: (String) -> Unit
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }

    Column {
        Text(text = stringResource(R.string.write_question) + ":", style = MaterialTheme.typography.bodyLarge)
        // Texto da pergunta
        OutlinedTextField(
            value = texto,
            onValueChange = { onTextoChange(it) } ,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(R.string.question)) },
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Dropdown para selecionar a resposta correta
        Text(text = stringResource(R.string.choose_correct_answer) + ":", style = MaterialTheme.typography.bodyLarge)
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = opcaoCerta!!,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = stringResource(R.string.correct_answer)) },
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
                listOf(stringResource(R.string.yes_true),stringResource(R.string.no_false)).forEach { resposta ->
                    DropdownMenuItem(
                        onClick = {
                            onOpcaoCertaChange(resposta)
                            isDropdownExpanded = false // Fecha o menu
                        },
                        text = { Text(resposta) }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(72.dp))
    }
}