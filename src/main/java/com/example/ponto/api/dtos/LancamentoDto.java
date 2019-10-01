package com.example.ponto.api.dtos;

import javax.validation.constraints.NotEmpty;

public class LancamentoDto {
  private Long id;
  private String data;
  private String tipo;
  private String descricao;
  private String localizacao;
  private Long funcionarioId;

  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  @NotEmpty(message = "Data não pode ser vazia")
  public String getData() {
    return data;
  }

  public void setData(final String data) {
    this.data = data;
  }

  public String getTipo() {
    return tipo;
  }

  public void setTipo(final String tipo) {
    this.tipo = tipo;
  }

  public String getDescricao() {
    return descricao;
  }

  public void setDescricao(final String descricao) {
    this.descricao = descricao;
  }

  public String getLocalizacao() {
    return localizacao;
  }

  public void setLocalizacao(final String localizacao) {
    this.localizacao = localizacao;
  }

  public Long getFuncionarioId() {
    return funcionarioId;
  }

  public void setFuncionarioId(final Long funcionarioId) {
    this.funcionarioId = funcionarioId;
  }

  @Override
  public String toString() {
    return "LancamentoDto{"
        + "id="
        + id
        + ", data='"
        + data
        + '\''
        + ", tipo='"
        + tipo
        + '\''
        + ", descricao='"
        + descricao
        + '\''
        + ", localizacao='"
        + localizacao
        + '\''
        + ", funcionarioId="
        + funcionarioId
        + '}';
  }
}
