
package br.com.controlefinanceiro.controller;

import br.com.controlefinanceiro.domain.Lancamento;
import br.com.controlefinanceiro.dto.LancamentoRequest;
import br.com.controlefinanceiro.dto.LancamentoResponse;
import br.com.controlefinanceiro.service.LancamentoService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lancamentos")
public class LancamentoController {

    private final LancamentoService service;

    public LancamentoController(LancamentoService service) {
        this.service = service;
    }

    // Cadastrar lançamento
    @PostMapping
    public ResponseEntity<LancamentoResponse> criar(
            @Valid @RequestBody LancamentoRequest request) {

        Lancamento lancamento = service.criar(
                request.descricao(),
                request.valor(),
                request.dataLancamento(),
                request.tipo(),
                request.contaId(),
                request.categoriaId()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(lancamento));
    }

    // Listar todos os lançamentos
    @GetMapping
    public List<LancamentoResponse> listar() {

        return service.listar()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Buscar lançamento pelo ID
    @GetMapping("/{id}")
    public LancamentoResponse buscarPorId(
            @PathVariable Long id) {

        return toResponse(service.buscarPorId(id));
    }

    // Listar lançamentos de uma conta
    @GetMapping("/conta/{contaId}")
    public List<LancamentoResponse> listarPorConta(
            @PathVariable Long contaId) {

        return service.listarPorConta(contaId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Excluir lançamento
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {

        service.excluir(id);
    }

    // Converter entidade em DTO
    private LancamentoResponse toResponse(
            Lancamento lancamento) {

        return new LancamentoResponse(
                lancamento.getId(),
                lancamento.getDescricao(),
                lancamento.getValor(),
                lancamento.getDataLancamento(),
                lancamento.getTipo(),
                lancamento.getConta().getId(),
                lancamento.getConta().getNome(),
                lancamento.getCategoria().getId(),
                lancamento.getCategoria().getNome()
        );
    }
}
