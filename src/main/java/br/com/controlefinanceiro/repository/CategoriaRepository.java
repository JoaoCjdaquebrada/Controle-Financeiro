
package br.com.controlefinanceiro.repository;

import br.com.controlefinanceiro.domain.Categoria;
import br.com.controlefinanceiro.domain.TipoLancamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriaRepository
        extends JpaRepository<Categoria, Long> {

    List<Categoria> findByTipo(TipoLancamento tipo);

    boolean existsByNomeAndTipo(String nome, TipoLancamento tipo);
}
