package com.example.ponto.api.dtos;

public class EmpresaDto {

    private Long id;
    private String razaoSocial;
    private String cnpj;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(final String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(final String cnpj) {
        this.cnpj = cnpj;
    }
}
