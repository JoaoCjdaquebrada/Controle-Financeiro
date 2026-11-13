
package br.com.controlefinanceiro.repository;

import br.com.controlefinanceiro.domain.OrcamentoMensal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrcamentoMensalRepository
        extends JpaRepository<OrcamentoMensal, Long> {

    List<OrcamentoMensal> findByMesAndAno(
            Integer mes,
            Integer ano
    );

    Optional<OrcamentoMensal> findByCategoria_IdAndMesAndAno(
            Long categoriaId,
            Integer mes,
            Integer ano
    );
}
