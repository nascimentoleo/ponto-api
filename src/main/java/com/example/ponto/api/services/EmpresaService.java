package com.example.ponto.api.services;

import com.example.ponto.api.entities.Empresa;

import java.util.Optional;

public interface EmpresaService {

    /**
     * Retorna uma empresa dado um cnpj.
     * @param cnpj
     * @return Optional<Empresa>
     */
    Optional<Empresa> buscarPorCnpj(String cnpj);

    /**
     * CAdastar uma nova empresa na base de dados.
     * @param empresa
     * @return Empresa
     */
    Empresa persistir(Empresa empresa);

}
