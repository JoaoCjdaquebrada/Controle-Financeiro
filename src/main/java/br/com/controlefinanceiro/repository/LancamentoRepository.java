
package br.com.controlefinanceiro.repository;

import br.com.controlefinanceiro.domain.Lancamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LancamentoRepository
        extends JpaRepository<Lancamento, Long> {

    List<Lancamento> findByConta_Id(Long contaId);

    List<Lancamento> findByCategoria_Id(Long categoriaId);

    List<Lancamento> findByDataLancamentoBetween(
            LocalDate inicio,
            LocalDate fim
    );
}
