
package br.com.controlefinanceiro.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrcamentoMensalTest {

    @Test
    void deveCriarOrcamentoComDadosValidos() {

        Categoria categoria = new Categoria(
                "Alimentação",
                TipoLancamento.DESPESA
        );

        OrcamentoMensal orcamento = new OrcamentoMensal(
                11,
                2026,
                new BigDecimal("600.00"),
                categoria
        );

        assertNotNull(orcamento);
        assertEquals(600.00,
                orcamento.getLimite().doubleValue());
    }

    @Test
    void naoDevePermitirLimiteZero() {

        Categoria categoria = new Categoria(
                "Alimentação",
                TipoLancamento.DESPESA
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new OrcamentoMensal(
                        11,
                        2026,
                        BigDecimal.ZERO,
                        categoria
                )
        );
    }

    @Test
    void naoDevePermitirLimiteNegativo() {

        Categoria categoria = new Categoria(
                "Alimentação",
                TipoLancamento.DESPESA
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new OrcamentoMensal(
                        11,
                        2026,
                        new BigDecimal("-100.00"),
                        categoria
                )
        );
    }

    @Test
    void naoDevePermitirCategoriaDeReceita() {

        Categoria categoria = new Categoria(
                "Salário",
                TipoLancamento.RECEITA
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new OrcamentoMensal(
                        11,
                        2026,
                        new BigDecimal("600.00"),
                        categoria
                )
        );
    }
}
