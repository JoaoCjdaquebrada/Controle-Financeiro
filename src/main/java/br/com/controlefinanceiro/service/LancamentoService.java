
package br.com.controlefinanceiro.service;

import br.com.controlefinanceiro.domain.Categoria;
import br.com.controlefinanceiro.domain.ContaFinanceira;
import br.com.controlefinanceiro.domain.Lancamento;
import br.com.controlefinanceiro.domain.OrcamentoMensal;
import br.com.controlefinanceiro.domain.TipoLancamento;

import br.com.controlefinanceiro.repository.CategoriaRepository;
import br.com.controlefinanceiro.repository.ContaFinanceiraRepository;
import br.com.controlefinanceiro.repository.LancamentoRepository;
import br.com.controlefinanceiro.repository.OrcamentoMensalRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class LancamentoService {

    private final LancamentoRepository lancamentoRepository;
    private final ContaFinanceiraRepository contaRepository;
    private final CategoriaRepository categoriaRepository;
    private final OrcamentoMensalRepository orcamentoRepository;

    public LancamentoService(
            LancamentoRepository lancamentoRepository,
            ContaFinanceiraRepository contaRepository,
            CategoriaRepository categoriaRepository,
            OrcamentoMensalRepository orcamentoRepository) {

        this.lancamentoRepository = lancamentoRepository;
        this.contaRepository = contaRepository;
        this.categoriaRepository = categoriaRepository;
        this.orcamentoRepository = orcamentoRepository;
    }

    // Cadastrar receita ou despesa
    public Lancamento criar(
            String descricao,
            BigDecimal valor,
            LocalDate data,
            TipoLancamento tipo,
            Long contaId,
            Long categoriaId) {

        ContaFinanceira conta = buscarConta(contaId);
        Categoria categoria = buscarCategoria(categoriaId);

        validarLancamento(descricao, valor, data, tipo, categoria);

        Lancamento lancamento = new Lancamento(
                descricao.trim(),
                valor,
                data,
                tipo,
                conta,
                categoria
        );

        validarOrcamento(lancamento, null);

        return lancamentoRepository.save(lancamento);
    }

    // Listar todos os lançamentos
    @Transactional(readOnly = true)
    public List<Lancamento> listar() {
        return lancamentoRepository.findAll();
    }

    // Buscar lançamento pelo ID
    @Transactional(readOnly = true)
    public Lancamento buscarPorId(Long id) {

        return lancamentoRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Lançamento não encontrado."
                        )
                );
    }

    // Listar lançamentos de uma conta
    @Transactional(readOnly = true)
    public List<Lancamento> listarPorConta(Long contaId) {

        if (contaId == null ||
                !contaRepository.existsById(contaId)) {
            throw new IllegalArgumentException(
                    "Conta financeira não encontrada."
            );
        }

        return lancamentoRepository.findByConta_Id(contaId);
    }

    // Atualizar lançamento
    public Lancamento atualizar(
            Long id,
            String descricao,
            BigDecimal valor,
            LocalDate data,
            TipoLancamento tipo,
            Long categoriaId) {

        Lancamento lancamento = buscarPorId(id);

        Categoria categoria = buscarCategoria(categoriaId);

        validarLancamento(descricao, valor, data, tipo, categoria);

        lancamento.atualizarDados(
                descricao.trim(),
                valor,
                data,
                tipo,
                categoria
        );

        validarOrcamento(lancamento, id);

        return lancamentoRepository.save(lancamento);
    }

    // Excluir lançamento
    public void excluir(Long id) {

        Lancamento lancamento = buscarPorId(id);

        lancamentoRepository.delete(lancamento);
    }

    // Buscar conta financeira
    private ContaFinanceira buscarConta(Long contaId) {

        if (contaId == null) {
            throw new IllegalArgumentException(
                    "A conta financeira é obrigatória."
            );
        }

        return contaRepository.findById(contaId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Conta financeira não encontrada."
                        )
                );
    }

    // Buscar categoria
    private Categoria buscarCategoria(Long categoriaId) {

        if (categoriaId == null) {
            throw new IllegalArgumentException(
                    "A categoria é obrigatória."
            );
        }

        return categoriaRepository.findById(categoriaId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Categoria não encontrada."
                        )
                );
    }

    // Validar os dados do lançamento
    private void validarLancamento(
            String descricao,
            BigDecimal valor,
            LocalDate data,
            TipoLancamento tipo,
            Categoria categoria) {

        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException(
                    "A descrição é obrigatória."
            );
        }

        if (valor == null ||
                valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "O valor deve ser maior que zero."
            );
        }

        if (data == null) {
            throw new IllegalArgumentException(
                    "A data é obrigatória."
            );
        }

        if (tipo == null) {
            throw new IllegalArgumentException(
                    "O tipo do lançamento é obrigatório."
            );
        }

        if (categoria.getTipo() != tipo) {
            throw new IllegalArgumentException(
                    "A categoria não corresponde ao tipo do lançamento."
            );
        }
    }

    // Validar o limite do orçamento mensal
    private void validarOrcamento(
            Lancamento lancamento,
            Long idIgnorado) {

        if (lancamento.getTipo() != TipoLancamento.DESPESA) {
            return;
        }

        Categoria categoria = lancamento.getCategoria();

        LocalDate data = lancamento.getDataLancamento();

        OrcamentoMensal orcamento = orcamentoRepository
                .findByCategoria_IdAndMesAndAno(
                        categoria.getId(),
                        data.getMonthValue(),
                        data.getYear()
                )
                .orElse(null);

        if (orcamento == null) {
            return;
        }

        BigDecimal totalGasto = BigDecimal.ZERO;

        List<Lancamento> lancamentos =
                lancamentoRepository.findByCategoria_Id(
                        categoria.getId()
                );

        for (Lancamento existente : lancamentos) {

            if (idIgnorado != null &&
                    existente.getId().equals(idIgnorado)) {
                continue;
            }

            LocalDate dataExistente =
                    existente.getDataLancamento();

            if (existente.getTipo() == TipoLancamento.DESPESA
                    && dataExistente.getMonthValue() == data.getMonthValue()
                    && dataExistente.getYear() == data.getYear()) {

                totalGasto = totalGasto.add(existente.getValor());
            }
        }

        BigDecimal totalProjetado =
                totalGasto.add(lancamento.getValor());

        if (totalProjetado.compareTo(orcamento.getLimite()) > 0) {
            throw new IllegalArgumentException(
                    "Essa despesa ultrapassa o limite do orçamento mensal."
            );
        }
    }
}
