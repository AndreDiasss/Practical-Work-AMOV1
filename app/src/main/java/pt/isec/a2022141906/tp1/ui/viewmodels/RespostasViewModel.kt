package pt.isec.a2022141906.tp1.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import pt.isec.a2022141906.tp1.models.Resposta

class RespostasViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    // Mapa de respostas acumuladas (ID da pergunta -> respostas)
    private val respostasMap = mutableMapOf<String, List<String>>()

    private val _resposta = MutableLiveData<Resposta?>()
    val resposta: LiveData<Resposta?> get() = _resposta

    /**
     * Adiciona uma resposta ao mapa acumulado.
     *
     * @param perguntaId ID da pergunta respondida.
     * @param resposta Lista de respostas associadas à pergunta.
     */
    fun adicionarResposta(perguntaId: String, resposta: List<String>) {
        respostasMap[perguntaId] = resposta
        Log.d("Quiz", "Respostas acumuladas: $respostasMap")
    }

    /**
     * Submete as respostas acumuladas ao Firestore.
     *
     * @param questionarioId ID do questionário.
     * @param userEmail Email do usuário que realizou o quiz.
     */
    fun submeterRespostas(questionarioId: String, userEmail: String) {
        // Cria o objeto Resposta com os dados acumulados.
        val respostaFinal = Resposta(
            questionarioId = questionarioId,
            userEmail = userEmail,
            respostas = respostasMap
        )

        // Salva a resposta no Firestore.
        val respostaRef = firestore.collection("Respostas").document()
        respostaRef.set(respostaFinal)
            .addOnSuccessListener {
                Log.d("Quiz", "Respostas submetidas com sucesso: $respostaFinal")
                limparRespostas() // Limpa o mapa após a submissão
            }
            .addOnFailureListener { e ->
                Log.e("Quiz", "Erro ao submeter respostas: ${e.message}")
            }
    }

    /**
     * Limpa o mapa de respostas acumuladas.
     */
    private fun limparRespostas() {
        respostasMap.clear()
        Log.d("Quiz", "Mapa de respostas limpo.")
    }
}
