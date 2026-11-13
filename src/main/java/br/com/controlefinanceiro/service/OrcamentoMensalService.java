
package br.com.controlefinanceiro.service;

import br.com.controlefinanceiro.domain.Categoria;
import br.com.controlefinanceiro.domain.OrcamentoMensal;
import br.com.controlefinanceiro.domain.TipoLancamento;

import br.com.controlefinanceiro.repository.CategoriaRepository;
import br.com.controlefinanceiro.repository.OrcamentoMensalRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class OrcamentoMensalService {

    private final OrcamentoMensalRepository repository;
    private final CategoriaRepository categoriaRepository;

    public OrcamentoMensalService(
            OrcamentoMensalRepository repository,
            CategoriaRepository categoriaRepository) {

        this.repository = repository;
        this.categoriaRepository = categoriaRepository;
    }

    // Cadastrar orçamento
    public OrcamentoMensal criar(
            Integer mes,
            Integer ano,
            BigDecimal limite,
            Long categoriaId) {

        validarPeriodo(mes, ano);

        Categoria categoria = buscarCategoria(categoriaId);

        validarCategoria(categoria);
        validarLimite(limite);

        boolean jaExiste = repository
                .findByCategoria_IdAndMesAndAno(
                        categoriaId, mes, ano
                ).isPresent();

        if (jaExiste) {
            throw new IllegalArgumentException(
                    "Já existe um orçamento para essa categoria nesse mês."
            );
        }

        OrcamentoMensal orcamento = new OrcamentoMensal(
                mes,
                ano,
                limite,
                categoria
        );

        return repository.save(orcamento);
    }

    // Listar todos os orçamentos
    @Transactional(readOnly = true)
    public List<OrcamentoMensal> listar() {
        return repository.findAll();
    }

    // Buscar orçamento pelo ID
    @Transactional(readOnly = true)
    public OrcamentoMensal buscarPorId(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Orçamento não encontrado."
                        )
                );
    }

    // Consultar orçamentos de determinado mês
    @Transactional(readOnly = true)
    public List<OrcamentoMensal> listarPorPeriodo(
            Integer mes,
            Integer ano) {

        validarPeriodo(mes, ano);

        return repository.findByMesAndAno(mes, ano);
    }

    // Atualizar orçamento
    public OrcamentoMensal atualizar(
            Long id,
            Integer mes,
            Integer ano,
            BigDecimal limite,
            Long categoriaId) {

        OrcamentoMensal orcamento = buscarPorId(id);

        validarPeriodo(mes, ano);

        Categoria categoria = buscarCategoria(categoriaId);

        validarCategoria(categoria);
        validarLimite(limite);

        var existente = repository.findByCategoria_IdAndMesAndAno(
                categoriaId, mes, ano
        );

        if (existente.isPresent()
                && !existente.get().getId().equals(id)) {

            throw new IllegalArgumentException(
                    "Já existe outro orçamento para essa categoria nesse mês."
            );
        }

        orcamento.atualizarDados(mes, ano, limite, categoria);

        return repository.save(orcamento);
    }

    // Excluir orçamento
    public void excluir(Long id) {

        OrcamentoMensal orcamento = buscarPorId(id);

        repository.delete(orcamento);
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

    // Validar período
    private void validarPeriodo(Integer mes, Integer ano) {

        if (mes == null || mes < 1 || mes > 12) {
            throw new IllegalArgumentException(
                    "O mês deve estar entre 1 e 12."
            );
        }

        if (ano == null || ano < 2000) {
            throw new IllegalArgumentException(
                    "O ano deve ser igual ou superior a 2000."
            );
        }
    }

    // Validar categoria
    private void validarCategoria(Categoria categoria) {

        if (categoria.getTipo() != TipoLancamento.DESPESA) {
            throw new IllegalArgumentException(
                    "Orçamentos devem utilizar categorias de despesa."
            );
        }
    }

    // Validar limite
    private void validarLimite(BigDecimal limite) {

        if (limite == null ||
                limite.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "O limite deve ser maior que zero."
            );
        }
    }
}
