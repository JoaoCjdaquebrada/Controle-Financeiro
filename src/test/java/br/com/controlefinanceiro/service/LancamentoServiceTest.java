
package br.com.controlefinanceiro.service;

import br.com.controlefinanceiro.domain.*;
import br.com.controlefinanceiro.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LancamentoServiceTest {

    @Mock
    private LancamentoRepository lancamentoRepository;

    @Mock
    private ContaFinanceiraRepository contaRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private OrcamentoMensalRepository orcamentoRepository;

    @InjectMocks
    private LancamentoService service;

    private ContaFinanceira conta;
    private Categoria categoriaDespesa;



    @BeforeEach
    void configurarDados() {

        conta = new ContaFinanceira(
                "Conta Principal",
                TipoConta.CORRENTE,
                new BigDecimal("500.00")
        );

        categoriaDespesa = mock(Categoria.class);
    }



    @Test
    void naoDevePermitirValorZero() {

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        when(categoriaRepository.findById(2L))
                .thenReturn(Optional.of(categoriaDespesa));

        assertThrows(
                IllegalArgumentException.class,
                () -> service.criar(
                        "Compra",
                        BigDecimal.ZERO,
                        LocalDate.of(2026, 11, 10),
                        TipoLancamento.DESPESA,
                        1L,
                        2L
                )
        );

        verify(lancamentoRepository, never())
                .save(any(Lancamento.class));
    }

    @Test
    void naoDevePermitirCategoriaIncompativel() {

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        Categoria salario = new Categoria(
                "Salário",
                TipoLancamento.RECEITA
        );

        when(categoriaRepository.findById(2L))
                .thenReturn(Optional.of(salario));

        assertThrows(
                IllegalArgumentException.class,
                () -> service.criar(
                        "Compra",
                        new BigDecimal("100.00"),
                        LocalDate.of(2026, 11, 10),
                        TipoLancamento.DESPESA,
                        1L,
                        2L
                )
        );

        verify(lancamentoRepository, never())
                .save(any(Lancamento.class));
    }

    @Test
    void naoDevePermitirDespesaAcimaDoOrcamento() {

        when(categoriaDespesa.getId()).thenReturn(2L);

        when(categoriaDespesa.getTipo())
                .thenReturn(TipoLancamento.DESPESA);

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        when(categoriaRepository.findById(2L))
                .thenReturn(Optional.of(categoriaDespesa));

        OrcamentoMensal orcamento = new OrcamentoMensal(
                11,
                2026,
                new BigDecimal("600.00"),
                categoriaDespesa
        );

        when(orcamentoRepository
                .findByCategoria_IdAndMesAndAno(2L, 11, 2026))
                .thenReturn(Optional.of(orcamento));

        Lancamento existente = new Lancamento(
                "Compra no mercado",
                new BigDecimal("205.00"),
                LocalDate.of(2026, 11, 10),
                TipoLancamento.DESPESA,
                conta,
                categoriaDespesa
        );

        when(lancamentoRepository.findByCategoria_Id(2L))
                .thenReturn(List.of(existente));

        assertThrows(
                IllegalArgumentException.class,
                () -> service.criar(
                        "Compra grande",
                        new BigDecimal("450.00"),
                        LocalDate.of(2026, 11, 10),
                        TipoLancamento.DESPESA,
                        1L,
                        2L
                )
        );

        verify(lancamentoRepository, never())
                .save(any(Lancamento.class));
    }

    @Test
    void devePermitirDespesaDentroDoOrcamento() {

        when(categoriaDespesa.getId()).thenReturn(2L);

        when(categoriaDespesa.getTipo())
                .thenReturn(TipoLancamento.DESPESA);

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        when(categoriaRepository.findById(2L))
                .thenReturn(Optional.of(categoriaDespesa));

        OrcamentoMensal orcamento = new OrcamentoMensal(
                11,
                2026,
                new BigDecimal("600.00"),
                categoriaDespesa
        );

        when(orcamentoRepository
                .findByCategoria_IdAndMesAndAno(2L, 11, 2026))
                .thenReturn(Optional.of(orcamento));

        when(lancamentoRepository.findByCategoria_Id(2L))
                .thenReturn(List.of());

        when(lancamentoRepository.save(any(Lancamento.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Lancamento resultado = service.criar(
                "Compra pequena",
                new BigDecimal("100.00"),
                LocalDate.of(2026, 11, 10),
                TipoLancamento.DESPESA,
                1L,
                2L
        );

        assertNotNull(resultado);
        assertEquals("Compra pequena", resultado.getDescricao());

        verify(lancamentoRepository, times(1))
                .save(any(Lancamento.class));
    }
}
