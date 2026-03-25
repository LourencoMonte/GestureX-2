package com.gesturex

import com.gesturex.data.model.AcaoGesto
import com.gesturex.data.model.Gesture
import com.gesturex.data.model.TipoGesto
import org.junit.Assert.*
import org.junit.Test

/**
 * Testes unitários para o modelo Gesture e suas constantes.
 */
class GestureModelTest {

    @Test
    fun `TipoGesto label retorna descricao correta para cada tipo`() {
        assertEquals("2× Giro para cima", TipoGesto.label(TipoGesto.GIRO_CIMA))
        assertEquals("2× Giro para baixo", TipoGesto.label(TipoGesto.GIRO_BAIXO))
        assertEquals("Agitar 2×", TipoGesto.label(TipoGesto.AGITAR))
        assertEquals("Inclinar para esquerda", TipoGesto.label(TipoGesto.INCLINAR_ESQ))
        assertEquals("Inclinar para direita", TipoGesto.label(TipoGesto.INCLINAR_DIR))
        assertEquals("Virar de cabeça pra baixo", TipoGesto.label(TipoGesto.VIRAR_BAIXO))
        assertEquals("Gesto personalizado", TipoGesto.label(TipoGesto.PERSONALIZADO))
    }

    @Test
    fun `TipoGesto label retorna padrao para tipo desconhecido`() {
        assertEquals("Gesto personalizado", TipoGesto.label("tipo_invalido"))
    }

    @Test
    fun `AcaoGesto label retorna descricao correta para cada acao`() {
        assertEquals("Abrir app", AcaoGesto.label(AcaoGesto.ABRIR_APP))
        assertEquals("Ligar/desligar lanterna", AcaoGesto.label(AcaoGesto.LANTERNA))
        assertEquals("Silenciar / ativar som", AcaoGesto.label(AcaoGesto.SILENCIAR))
        assertEquals("Ligar para contato", AcaoGesto.label(AcaoGesto.LIGAR_PARA))
    }

    @Test
    fun `AcaoGesto label retorna valor original para acao desconhecida`() {
        assertEquals("acao_custom", AcaoGesto.label("acao_custom"))
    }

    @Test
    fun `Gesture data class valores padrao corretos`() {
        val g = Gesture(nome = "Teste", tipoGesto = TipoGesto.AGITAR, acaoDestino = AcaoGesto.LANTERNA)
        assertEquals(0, g.id)
        assertEquals("Teste", g.nome)
        assertEquals(TipoGesto.AGITAR, g.tipoGesto)
        assertEquals(AcaoGesto.LANTERNA, g.acaoDestino)
        assertEquals("", g.appDestino)
        assertEquals("", g.appNome)
        assertEquals("", g.contatoNumero)
        assertEquals("", g.contatoNome)
        assertTrue(g.ativo)
        assertEquals("", g.dadosSensor)
    }

    @Test
    fun `Gesture data class igualdade por conteudo`() {
        val g1 = Gesture(id = 1, nome = "A", tipoGesto = "t", acaoDestino = "a")
        val g2 = Gesture(id = 1, nome = "A", tipoGesto = "t", acaoDestino = "a")
        assertEquals(g1, g2)
    }

    @Test
    fun `Gesture data class copy funciona`() {
        val original = Gesture(id = 1, nome = "Original", tipoGesto = TipoGesto.AGITAR, acaoDestino = AcaoGesto.LANTERNA)
        val copia = original.copy(nome = "Copia", ativo = false)
        assertEquals("Copia", copia.nome)
        assertFalse(copia.ativo)
        assertEquals(original.id, copia.id)
        assertEquals(original.tipoGesto, copia.tipoGesto)
    }
}

