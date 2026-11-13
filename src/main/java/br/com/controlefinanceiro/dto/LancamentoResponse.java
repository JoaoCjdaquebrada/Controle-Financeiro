
package br.com.controlefinanceiro.dto;

import br.com.controlefinanceiro.domain.TipoLancamento;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LancamentoResponse(

        Long id,
        String descricao,
        BigDecimal valor,
        LocalDate dataLancamento,
        TipoLancamento tipo,
        Long contaId,
        String contaNome,
        Long categoriaId,
        String categoriaNome

) {
}
