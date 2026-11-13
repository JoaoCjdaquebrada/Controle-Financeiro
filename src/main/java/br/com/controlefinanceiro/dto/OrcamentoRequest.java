
package br.com.controlefinanceiro.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record OrcamentoRequest(

        @NotNull(message = "O mês é obrigatório.")
        @Min(value = 1, message = "O mês deve ser no mínimo 1.")
        @Max(value = 12, message = "O mês deve ser no máximo 12.")
        Integer mes,

        @NotNull(message = "O ano é obrigatório.")
        @Min(value = 2000, message = "Ano inválido.")
        Integer ano,

        @NotNull(message = "O limite é obrigatório.")
        @DecimalMin(value = "0.01",
                message = "O limite deve ser maior que zero.")
        @Digits(integer = 10, fraction = 2)
        BigDecimal limite,

        @NotNull(message = "A categoria é obrigatória.")
        @Positive(message = "O ID da categoria deve ser positivo.")
        Long categoriaId

) {
}
