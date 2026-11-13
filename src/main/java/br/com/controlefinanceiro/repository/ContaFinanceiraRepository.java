
package br.com.controlefinanceiro.repository;

import br.com.controlefinanceiro.domain.ContaFinanceira;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContaFinanceiraRepository
        extends JpaRepository<ContaFinanceira, Long> {

}
