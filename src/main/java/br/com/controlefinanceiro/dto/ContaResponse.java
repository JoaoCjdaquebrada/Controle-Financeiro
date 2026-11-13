
package br.com.controlefinanceiro.dto;

import br.com.controlefinanceiro.domain.TipoConta;

import java.math.BigDecimal;

public record ContaResponse(

        Long id,
        String nome,
        TipoConta tipoConta,
        BigDecimal saldoInicial

) {
}
