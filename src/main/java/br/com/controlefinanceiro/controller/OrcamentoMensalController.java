
package br.com.controlefinanceiro.controller;

import br.com.controlefinanceiro.domain.OrcamentoMensal;
import br.com.controlefinanceiro.dto.OrcamentoRequest;
import br.com.controlefinanceiro.dto.OrcamentoResponse;
import br.com.controlefinanceiro.service.OrcamentoMensalService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orcamentos")
public class OrcamentoMensalController {

    private final OrcamentoMensalService service;

    public OrcamentoMensalController(
            OrcamentoMensalService service) {
        this.service = service;
    }

    // Cadastrar orçamento
    @PostMapping
    public ResponseEntity<OrcamentoResponse> criar(
            @Valid @RequestBody OrcamentoRequest request) {

        OrcamentoMensal orcamento = service.criar(
                request.mes(),
                request.ano(),
                request.limite(),
                request.categoriaId()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(orcamento));
    }

    // Listar todos os orçamentos
    @GetMapping
    public List<OrcamentoResponse> listar() {

        return service.listar()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Buscar orçamento pelo ID
    @GetMapping("/{id}")
    public OrcamentoResponse buscarPorId(
            @PathVariable Long id) {

        return toResponse(service.buscarPorId(id));
    }

    // Consultar orçamentos por mês e ano
    @GetMapping("/periodo/{mes}/{ano}")
    public List<OrcamentoResponse> listarPorPeriodo(
            @PathVariable Integer mes,
            @PathVariable Integer ano) {

        return service.listarPorPeriodo(mes, ano)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Atualizar orçamento
    @PutMapping("/{id}")
    public OrcamentoResponse atualizar(
            @PathVariable Long id,
            @Valid @RequestBody OrcamentoRequest request) {

        OrcamentoMensal orcamento = service.atualizar(
                id,
                request.mes(),
                request.ano(),
                request.limite(),
                request.categoriaId()
        );

        return toResponse(orcamento);
    }

    // Excluir orçamento
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {

        service.excluir(id);
    }

    // Converter entidade em DTO
    private OrcamentoResponse toResponse(
            OrcamentoMensal orcamento) {

        return new OrcamentoResponse(
                orcamento.getId(),
                orcamento.getMes(),
                orcamento.getAno(),
                orcamento.getLimite(),
                orcamento.getCategoria().getId(),
                orcamento.getCategoria().getNome()
        );
    }
}
