package com.gesturex.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Tipos de gesto suportados */
object TipoGesto {
    const val GIRO_CIMA    = "giro_cima"
    const val GIRO_BAIXO   = "giro_baixo"
    const val AGITAR       = "agitar"
    const val INCLINAR_ESQ = "inclinar_esq"
    const val INCLINAR_DIR = "inclinar_dir"
    const val VIRAR_BAIXO  = "virar_baixo"
    const val PERSONALIZADO = "personalizado"

    fun label(tipo: String) = when (tipo) {
        GIRO_CIMA    -> "2× Giro para cima"
        GIRO_BAIXO   -> "2× Giro para baixo"
        AGITAR       -> "Agitar 2×"
        INCLINAR_ESQ -> "Inclinar para esquerda"
        INCLINAR_DIR -> "Inclinar para direita"
        VIRAR_BAIXO  -> "Virar de cabeça pra baixo"
        else         -> "Gesto personalizado"
    }
}

/** Ações que um gesto pode executar */
object AcaoGesto {
    const val ABRIR_APP  = "abrir_app"
    const val LANTERNA   = "lanterna"
    const val SILENCIAR  = "silenciar"
    const val LIGAR_PARA = "ligar_para"

    fun label(acao: String) = when (acao) {
        ABRIR_APP  -> "Abrir app"
        LANTERNA   -> "Ligar/desligar lanterna"
        SILENCIAR  -> "Silenciar / ativar som"
        LIGAR_PARA -> "Ligar para contato"
        else       -> acao
    }
}

@Entity(tableName = "gestures")
data class Gesture(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nome: String,
    val tipoGesto: String,
    val acaoDestino: String,
    val appDestino: String = "",      // packageName para ABRIR_APP
    val appNome: String = "",         // nome legível do app
    val contatoNumero: String = "",   // número para LIGAR_PARA
    val contatoNome: String = "",     // nome do contato
    val ativo: Boolean = true,
    val dadosSensor: String = ""      // JSON com leituras capturadas
)
