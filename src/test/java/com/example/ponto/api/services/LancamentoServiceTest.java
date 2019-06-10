package com.example.ponto.api.services;

import com.example.ponto.api.entities.Lancamento;
import com.example.ponto.api.repositories.LancamentoRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class LancamentoServiceTest {

    @MockBean
    private LancamentoRepository lancamentoRepository;
    @Autowired
    private LancamentoService lancamentoService;

    @Test
    public void testBuscarLancamentoPorFuncionarioId() {
        given(lancamentoRepository.findByFuncionarioId(anyLong(), any(PageRequest.class))).willReturn(new PageImpl<>(new ArrayList<>()));

        Page<Lancamento> lancamentos = lancamentoService.buscarPorFuncionarioId(1L, new PageRequest(0, 10));

        assertNotNull(lancamentos);

    }

    @Test
    public void testBuscarLancamentoPorId() {
        given(lancamentoRepository.findById(anyLong())).willReturn(Optional.of(new Lancamento()));

        Optional<Lancamento> lancamento = lancamentoService.buscarPorId(1L);

        assertTrue(lancamento.isPresent());
    }

    @Test
    public void testPersistirLancamento() {
        given(lancamentoRepository.save(any(Lancamento.class))).willReturn(new Lancamento());

        Lancamento lancamento = lancamentoService.persistir(new Lancamento());

        assertNotNull(lancamento);
    }
}
