package pt.isec.a2022141906.tp1.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import pt.isec.a2022141906.tp1.models.Pergunta

class PerguntaViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _pergunta = MutableLiveData<Pergunta?>()
    val pergunta: LiveData<Pergunta?> get() = _pergunta

    // Buscar uma pergunta pelo ID
    fun buscarPergunta(perguntaId: String) {
        firestore.collection("Perguntas")
            .document(perguntaId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    _pergunta.value = document.toObject(Pergunta::class.java)
                } else {
                    _pergunta.value = null
                }
            }
            .addOnFailureListener {
                Log.e("Firestore", "Erro ao buscar pergunta: ${it.message}")
                _pergunta.value = null
            }
    }

    // Eliminar uma pergunta pelo ID
    fun eliminarPergunta(perguntaId: String, questionarioId: String) {
        firestore.collection("Perguntas")
            .document(perguntaId)
            .delete()
            .addOnSuccessListener {
                Log.i("Firestore", "Pergunta $perguntaId eliminada com sucesso.")

                firestore.collection("Questionarios").document(questionarioId).update("perguntas", FieldValue.arrayRemove(perguntaId))
                    .addOnSuccessListener {
                        Log.i("Firestore", "ID da pergunta removido do questionário $questionarioId.")
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Erro ao atualizar questionário $questionarioId: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Erro ao eliminar pergunta: ${e.message}")
            }

    }

    fun editarPergunta(perguntaId: String, pergunta: Pergunta) {
        val collection = FirebaseFirestore.getInstance().collection("Perguntas")
        collection.document(perguntaId)
            .set(pergunta)
            .addOnSuccessListener {
                Log.d("Firestore", "Pergunta editada com sucesso!")
            }
            .addOnFailureListener {
                Log.e("Firestore", "Erro ao editar pergunta: ${it.message}")
            }
    }

    fun carregarPerguntas(perguntaIds: List<String>, onSuccess: (List<Pergunta>) -> Unit) {
        firestore.collection("Perguntas")
            .whereIn(FieldPath.documentId(), perguntaIds)
            .get()
            .addOnSuccessListener { snapshot ->
                val perguntas = snapshot.toObjects(Pergunta::class.java)
                onSuccess(perguntas)
            }
            .addOnFailureListener { e ->
                Log.e("Quiz", "Erro ao carregar perguntas: ${e.message}")
            }
    }
}
