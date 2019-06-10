package com.example.ponto.api.services;

import com.example.ponto.api.entities.Funcionario;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface FuncionarioService {

    /**
     * Persiste um funcionário na base de dados.
     * @param funcionario
     * @return Funcionario
     */
    Funcionario persistir(Funcionario funcionario);

    /**
     * Buscar e retorna um funcionário por cpf.
     * @param cpf
     * @return Optional<Funcionario>
     */
    Optional<Funcionario> buscarPorCpf(String cpf);

    /**
     * Buscar e retorna um funcionário por email.
     * @param email
     * @return Optional<Funcionario>
     */
    Optional<Funcionario> buscarPorEmail(String email);

    /**
     * Buscar e retorna um funcionário por ID.
     * @param id
     * @return Optional<Funcionario>
     */
    Optional<Funcionario> buscarPorId(Long id);
}
