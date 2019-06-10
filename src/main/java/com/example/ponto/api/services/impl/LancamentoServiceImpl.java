package com.example.ponto.api.services.impl;

import com.example.ponto.api.entities.Lancamento;
import com.example.ponto.api.repositories.EmpresaRepository;
import com.example.ponto.api.repositories.LancamentoRepository;
import com.example.ponto.api.services.LancamentoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LancamentoServiceImpl implements LancamentoService {

    private static final Logger log = LoggerFactory.getLogger(LancamentoServiceImpl.class);

    @Autowired
    private LancamentoRepository lancamentoRepository;


    @Override
    public Page<Lancamento> buscarPorFuncionarioId(Long funcionarioId, PageRequest pageRequest) {
        log.info("Buscando lançamentos para o Funcionário {}", funcionarioId);
        return lancamentoRepository.findByFuncionarioId(funcionarioId, pageRequest);
    }

    @Override
    public Optional<Lancamento> buscarPorId(Long id) {
        log.info("Buscando lançamentos para o ID {}", id);
        return lancamentoRepository.findById(id);
    }

    @Override
    public Lancamento persistir(Lancamento lancamento) {
        log.info("Salvando o lançamento: {}", lancamento);
        return lancamentoRepository.save(lancamento);
    }

    @Override
    public void remover(Long id) {
        log.info("Removendo o lançamento: {}", id);
        lancamentoRepository.deleteById(id);
    }
}
