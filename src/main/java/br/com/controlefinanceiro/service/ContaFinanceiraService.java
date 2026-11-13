
package br.com.controlefinanceiro.service;

import br.com.controlefinanceiro.domain.ContaFinanceira;
import br.com.controlefinanceiro.domain.TipoConta;
import br.com.controlefinanceiro.repository.ContaFinanceiraRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class ContaFinanceiraService {

    private final ContaFinanceiraRepository repository;

    public ContaFinanceiraService(
            ContaFinanceiraRepository repository) {
        this.repository = repository;
    }

    // Cadastrar uma conta
    public ContaFinanceira criar(
            String nome,
            TipoConta tipoConta,
            BigDecimal saldoInicial) {

        validarDados(nome, tipoConta, saldoInicial);

        ContaFinanceira conta = new ContaFinanceira(
                nome.trim(),
                tipoConta,
                saldoInicial
        );

        return repository.save(conta);
    }

    // Listar todas as contas
    @Transactional(readOnly = true)
    public List<ContaFinanceira> listar() {
        return repository.findAll();
    }

    // Buscar conta pelo ID
    @Transactional(readOnly = true)
    public ContaFinanceira buscarPorId(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Conta financeira não encontrada."
                        )
                );
    }

    // Atualizar uma conta
    public ContaFinanceira atualizar(
            Long id,
            String nome,
            TipoConta tipoConta) {

        validarDados(
                nome,
                tipoConta,
                BigDecimal.ZERO
        );

        ContaFinanceira conta = buscarPorId(id);

        conta.atualizarDados(nome.trim(), tipoConta);

        return repository.save(conta);
    }

    // Excluir uma conta
    public void excluir(Long id) {

        ContaFinanceira conta = buscarPorId(id);

        repository.delete(conta);
    }

    // Validar os dados recebidos
    private void validarDados(
            String nome,
            TipoConta tipoConta,
            BigDecimal saldoInicial) {

        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException(
                    "O nome da conta é obrigatório."
            );
        }

        if (tipoConta == null) {
            throw new IllegalArgumentException(
                    "O tipo da conta é obrigatório."
            );
        }

        if (saldoInicial == null ||
                saldoInicial.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "O saldo inicial não pode ser negativo."
            );
        }
    }
}
