
package br.com.controlefinanceiro.dto;

import br.com.controlefinanceiro.domain.TipoConta;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ContaRequest(

        @NotBlank(message = "O nome da conta é obrigatório.")
        String nome,

        @NotNull(message = "O tipo da conta é obrigatório.")
        TipoConta tipoConta,

        @NotNull(message = "O saldo inicial é obrigatório.")
        @DecimalMin(value = "0.00",
                message = "O saldo não pode ser negativo.")
        BigDecimal saldoInicial

) {
}
