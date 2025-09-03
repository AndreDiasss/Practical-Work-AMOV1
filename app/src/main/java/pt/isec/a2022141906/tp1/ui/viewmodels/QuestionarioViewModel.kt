package pt.isec.a2022141906.tp1.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import pt.isec.a2022141906.tp1.models.EstadoQuestionario
import pt.isec.a2022141906.tp1.models.Pergunta
import pt.isec.a2022141906.tp1.models.Questionario

class QuestionarioViewModel : ViewModel() {
    private val firestore = Firebase.firestore

    private val _questionario = MutableLiveData<Questionario?>()
    val questionario: LiveData<Questionario?> get() = _questionario

    val questionarios = MutableStateFlow<List<Questionario>>(emptyList())

    init {
        carregarQuestionarios()
    }

    fun generateUniqueId(): String {
        return (1..6).map { ('A'..'Z').random() }.joinToString("")
    }

    fun salvarQuestionario(questionario: Questionario) {
        firestore.collection("Questionarios")
            .document(questionario.id)
            .set(questionario)
            .addOnSuccessListener {
                Log.d("Firestore", "Questionário salvo com sucesso!")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Erro ao salvar questionário: ${e.message}")
            }
    }

    fun buscarPerguntasDoQuestionario(perguntaIds: List<String>, onComplete: (List<Pergunta>) -> Unit) {
        if (perguntaIds.isNotEmpty()) { // Verifica se a lista não está vazia
            firestore.collection("Perguntas")
                .whereIn(FieldPath.documentId(), perguntaIds)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val perguntas = querySnapshot.toObjects(Pergunta::class.java)
                    onComplete(perguntas)
                }
                .addOnFailureListener {
                    Log.e("Firestore", "Erro ao buscar perguntas do questionário: ${it.message}")
                    onComplete(emptyList())
                }
        } else {
            Log.d("Firestore", "Lista de IDs de perguntas está vazia.")
            onComplete(emptyList()) // Retorna uma lista vazia para evitar falha
        }
    }

    fun abrirQuestionario(questionarioId: String) {
        val questionarioRef = firestore.collection("Questionarios").document(questionarioId)
        questionarioRef.update("estado", EstadoQuestionario.OPEN)
            .addOnSuccessListener {
                Log.d("Quiz", "Questionário aberto com sucesso.")
            }
            .addOnFailureListener { e ->
                Log.e("Quiz", "Erro ao abrir questionário: ${e.message}")
            }
    }

    fun buscarQuestionario(questionarioId: String) {
        firestore.collection("Questionarios")
            .document(questionarioId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    _questionario.value = document.toObject(Questionario::class.java)
                } else {
                    _questionario.value = null
                }
            }
            .addOnFailureListener {
                Log.e("Firestore", "Erro ao buscar questionário: ${it.message}")
                _questionario.value = null
            }
    }

    fun carregarQuestionarios() {
        firestore.collection("Questionarios")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("Firestore", "Erro ao carregar questionários", e)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val lista = snapshot.documents.mapNotNull { it.toObject(Questionario::class.java) }
                    questionarios.value = lista
                }
            }
    }

    fun adicionarPergunta(questionarioId: String, pergunta: Pergunta) {
        val questionario = questionarios.value.find { it.id == questionarioId } ?: return
        val novaPerguntaId = firestore.collection("Perguntas").document().id
        pergunta.id = novaPerguntaId

        firestore.collection("Perguntas")
            .document(novaPerguntaId)
            .set(pergunta)
            .addOnSuccessListener {
                val perguntasAtualizadas = questionario.perguntas + novaPerguntaId

                firestore.collection("Questionarios")
                    .document(questionarioId)
                    .update("perguntas", perguntasAtualizadas)
                    .addOnSuccessListener {
                        val questionariosAtualizados = questionarios.value.map {
                            if (it.id == questionarioId) it.copy(perguntas = perguntasAtualizadas) else it
                        }
                        questionarios.value = questionariosAtualizados
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Erro ao atualizar perguntas no questionário: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Erro ao salvar pergunta: ${e.message}")
            }
    }

    fun carregarQuestionario(
        questionarioId: String,
        onSuccess: (Questionario) -> Unit,
        onFail: (String) -> Unit
    ) {
        val questionarioRef = firestore.collection("Questionarios").document(questionarioId)
        questionarioRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val estado = document.getString("estado")
                    if (estado == EstadoQuestionario.OPEN.name) {
                        val questionario = document.toObject(Questionario::class.java)
                        if (questionario != null) {
                            onSuccess(questionario)
                        } else {
                            Log.e("Firestore", "Questionário não encontrado!")
                        }
                    } else {
                        onFail("Questionário não está aberto!") // Questionário não está OPEN
                    }
                } else {
                    onFail("Questionário não encontrado!") // Documento não existe
                }
            }
            .addOnFailureListener {
                Log.e("Firestore", "Erro ao buscar questionário: ${it.message}")
            }
    }

    fun configurarQuestionario(questionarioId: String, tempo: Int) {
        firestore.collection("Questionarios")
            .document(questionarioId)
            .update(mapOf("tempo" to tempo, "estado" to EstadoQuestionario.OPEN.name))
            .addOnSuccessListener {
                Log.d("Firestore", "Questionário configurado com sucesso!")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Erro ao configurar questionário: ${e.message}")
            }
    }

    fun fecharQuestionario(questionarioId: String) {
        firestore.collection("Questionarios")
            .document(questionarioId)
            .update("estado", EstadoQuestionario.CLOSED)
            .addOnSuccessListener {
                Log.d("Firestore", "Questionário fechado com sucesso!")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Erro ao fechar questionário: ${e.message}")
            }
    }

    private val _utilizadoresConectados = MutableLiveData<Int>()
    val utilizadoresConectados: LiveData<Int> get() = _utilizadoresConectados

    fun observarUtilizadoresConectados(questionarioId: String) {
        firestore.collection("Questionarios")
            .document(questionarioId)
            .collection("UtilizadoresConectados")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("Firestore", "Erro ao observar utilizadores conectados", e)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    _utilizadoresConectados.value = snapshot.size()
                }
            }
    }

    fun adicionarUtilizadorConectado(questionarioId: String, userId: String, email: String) {
        val utilizadorRef = firestore.collection("Questionarios")
            .document(questionarioId)
            .collection("UtilizadoresConectados")
            .document(userId)

        val data = mapOf(
            "email" to email,
            "conectadoEm" to FieldValue.serverTimestamp()
        )

        utilizadorRef.set(data)
            .addOnSuccessListener {
                Log.d("Firestore", "Utilizador adicionado à subcoleção de conectados.")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Erro ao adicionar utilizador conectado: ${e.message}")
            }
    }

    fun removerUtilizadorConectado(questionarioId: String, userId: String) {
        val utilizadorRef = firestore.collection("Questionarios")
            .document(questionarioId)
            .collection("UtilizadoresConectados")
            .document(userId)

        utilizadorRef.delete()
            .addOnSuccessListener {
                Log.d("Firestore", "Utilizador removido da subcoleção de conectados.")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Erro ao remover utilizador conectado: ${e.message}")
            }
    }

    fun buscarUtilizadoresConectados(questionarioId: String, onComplete: (List<String>) -> Unit) {
        firestore.collection("Questionarios")
            .document(questionarioId)
            .collection("UsuariosConectados")
            .get()
            .addOnSuccessListener { snapshot ->
                val emails = snapshot.documents.mapNotNull { it.getString("email") }
                onComplete(emails)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Erro ao buscar utilizadores conectados: ${e.message}")
                onComplete(emptyList())
            }
    }

    private var _tempoRestante = MutableLiveData<Int>()
    val tempoRestante: LiveData<Int> = _tempoRestante
    private var timerJob: Job? = null // Job do timer

    fun iniciarTimer(tempoInicial: Int) {
        if (_tempoRestante.value == null) {
            _tempoRestante.value = tempoInicial
        }

        timerJob?.cancel() // Cancela qualquer timer anterior
        timerJob = viewModelScope.launch {
            while (_tempoRestante.value!! > 0) {
                delay(1000)
                _tempoRestante.value = (_tempoRestante.value ?: 0) - 1
            }
        }
    }

    fun pararTimer() {
        timerJob?.cancel() // Cancela o loop do timer
    }

    private val _resultados = MutableLiveData<Map<String, Map<String, Int>>>()
    val resultados: LiveData<Map<String, Map<String, Int>>> get() = _resultados

    private val _jogadoresTerminados = MutableLiveData<Int>()
    val jogadoresTerminados: LiveData<Int> get() = _jogadoresTerminados

    private val _jogadoresTotais = MutableLiveData<Int>()
    val jogadoresTotais: LiveData<Int> get() = _jogadoresTotais

    fun observarResultados(questionarioId: String) {
        firestore.collection("Respostas")
            .whereEqualTo("questionarioId", questionarioId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    Log.e("Resultados", "Erro ao observar resultados: ${error?.message}")
                    return@addSnapshotListener
                }

                val respostasAgrupadas = mutableMapOf<String, MutableMap<String, Int>>()
                var jogadoresContados = 0

                snapshot.documents.forEach { documento ->
                    val respostas = documento.get("respostas") as? Map<String, List<String>> ?: emptyMap()
                    jogadoresContados++

                    respostas.forEach { (perguntaId, respostas) ->
                        val agregados = respostasAgrupadas.getOrPut(perguntaId) { mutableMapOf() }
                        respostas.forEach { resposta ->
                            agregados[resposta] = (agregados[resposta] ?: 0) + 1
                        }
                    }
                }

                _resultados.postValue(respostasAgrupadas)
                _jogadoresTerminados.postValue(jogadoresContados)
                _jogadoresTotais.postValue(snapshot.size())
            }
    }
}
