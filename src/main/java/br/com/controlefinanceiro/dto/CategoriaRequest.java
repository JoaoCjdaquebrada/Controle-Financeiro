
package br.com.controlefinanceiro.dto;

import br.com.controlefinanceiro.domain.TipoLancamento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoriaRequest(

        @NotBlank(message = "O nome da categoria é obrigatório.")
        String nome,

        @NotNull(message = "O tipo da categoria é obrigatório.")
        TipoLancamento tipo

) {
}
