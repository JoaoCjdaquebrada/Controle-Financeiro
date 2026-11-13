
package br.com.controlefinanceiro.dto;

import java.math.BigDecimal;

public record OrcamentoResponse(

        Long id,
        Integer mes,
        Integer ano,
        BigDecimal limite,
        Long categoriaId,
        String categoriaNome

) {
}
