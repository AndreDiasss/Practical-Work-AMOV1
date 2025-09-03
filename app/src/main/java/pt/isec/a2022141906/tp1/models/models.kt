package pt.isec.a2022141906.tp1.models

data class User(
    val name: String,
    val email: String,
    val photoUrl: String? = null,
    val uid: String = ""
)

data class Questionario(
    val id: String = "",
    val titulo: String = "",
    val descricao: String = "",
    val criadorEmail: String = "",
    val perguntas: List<String> = emptyList(),
    val imagemUri: String? = null,
    var estado: EstadoQuestionario = EstadoQuestionario.WAITING,
    var tempo: Int? = null
)

enum class EstadoQuestionario {
    WAITING,
    OPEN,
    CLOSED
}

data class Pergunta(
    var id: String = "",
    val titulo: String = "",
    val texto: String = "",
    val tipo: TipoPergunta = TipoPergunta.SIM_NAO,
    val opcoes: List<String>? = null,
    val opcoesCertas: List<String>? = null,
    val imagemUri: String? = null
)

enum class TipoPergunta {
    SIM_NAO,
    ESCOLHA_MULTIPLA,
    CORRESPONDENCIA,
    ORDENACAO,
    PREENCHIMENTO,
    ASSOCIACAO,
    INDICACAO_PALAVRAS
}

data class Resposta(
    val questionarioId: String,
    val userEmail: String,
    val respostas: Map<String, List<String>> // ID da pergunta -> respostas
)