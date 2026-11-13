
package br.com.controlefinanceiro.dto;

import br.com.controlefinanceiro.domain.TipoLancamento;

public record CategoriaResponse(
        Long id,
        String nome,
        TipoLancamento tipo
) {
}
