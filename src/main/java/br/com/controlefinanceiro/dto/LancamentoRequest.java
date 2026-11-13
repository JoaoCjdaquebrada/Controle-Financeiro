
package br.com.controlefinanceiro.dto;

import br.com.controlefinanceiro.domain.TipoLancamento;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LancamentoRequest(

        @NotBlank(message = "A descrição é obrigatória.")
        String descricao,

        @NotNull(message = "O valor é obrigatório.")
        @DecimalMin(value = "0.01",
                message = "O valor deve ser maior que zero.")
        @Digits(integer = 10, fraction = 2)
        BigDecimal valor,

        @NotNull(message = "A data é obrigatória.")
        LocalDate dataLancamento,

        @NotNull(message = "O tipo é obrigatório.")
        TipoLancamento tipo,

        @NotNull(message = "A conta é obrigatória.")
        @Positive(message = "O ID da conta deve ser positivo.")
        Long contaId,

        @NotNull(message = "A categoria é obrigatória.")
        @Positive(message = "O ID da categoria deve ser positivo.")
        Long categoriaId

) {
}
