package com.example.ponto.api.services;

import com.example.ponto.api.entities.Lancamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

public interface LancamentoService {

    /**
     * Busca Lancamentos por funcionário.
     * @param funcionarioId
     * @param pageRequest
     * @return Page<Lancamento>
     */
    Page<Lancamento> buscarPorFuncionarioId(Long funcionarioId, PageRequest pageRequest);


    /**
     * Buscar lancamento por id.
     * @param id
     * @return Optional<Lancamento>
     */
    Optional<Lancamento> buscarPorId(Long id);

    /**
     * Salva um lançamento na base de dados.
     * @param lancamento
     * @return Lancamento
     */
    Lancamento persistir(Lancamento lancamento);

    /**
     * Remove um lançamento da base de dados.
     * @param id
     */
    void remover(Long id);
}
