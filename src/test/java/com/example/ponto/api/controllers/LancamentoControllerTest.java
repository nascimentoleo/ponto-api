package com.example.ponto.api.controllers;

import com.example.ponto.api.dtos.LancamentoDto;
import com.example.ponto.api.entities.Funcionario;
import com.example.ponto.api.entities.Lancamento;
import com.example.ponto.api.enums.TipoEnum;
import com.example.ponto.api.services.FuncionarioService;
import com.example.ponto.api.services.LancamentoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LancamentoControllerTest {

  private MockMvc mockMvc;
  @MockBean private LancamentoService lancamentoService;
  @MockBean private FuncionarioService funcionarioService;
  @Autowired private WebApplicationContext context;

  private static final String URL_BASE = "/api/lancamentos/";
  private static final Long ID_FUNCIONARIO = 1L;
  private static final Long ID_LANCAMENTO = 1L;
  private static final String TIPO = TipoEnum.INICIO_TRABALHO.name();
  private static final Date DATA = new Date();
  private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  @Before
  public void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
  }

  @Test
  @WithMockUser
  public void testCadastrarLancamento() throws Exception {
    final Lancamento lancamento = obterDadosLancamento();
    given(funcionarioService.buscarPorId(anyLong())).willReturn(Optional.of(new Funcionario()));
    given(lancamentoService.persistir(any(Lancamento.class))).willReturn(lancamento);

    mockMvc
        .perform(
            post(URL_BASE)
                .content(this.obterJsonRequisicaoPost())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.id").value(ID_LANCAMENTO))
        .andExpect(jsonPath("$.data.tipo").value(TIPO))
        .andExpect(jsonPath("$.data.data").value(dateFormat.format(DATA)))
        .andExpect(jsonPath("$.data.funcionarioId").value(ID_FUNCIONARIO))
        .andExpect(jsonPath("$.errors").isEmpty())
        .andDo(
            mvcResult -> {
              System.out.println(mvcResult.getResponse().toString());
            });
  }

  @Test
  public void testCadastrarLancamentoFuncionarioInvalido() throws Exception {
    given(funcionarioService.buscarPorId(anyLong())).willReturn(Optional.empty());

    mockMvc
        .perform(
            post(URL_BASE)
                .content(this.obterJsonRequisicaoPost())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors").isNotEmpty())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @Test
  @WithMockUser
  public void testRemoverLancamento() throws Exception {
    given(lancamentoService.buscarPorId(anyLong())).willReturn(Optional.of(new Lancamento()));

    mockMvc
        .perform(
            MockMvcRequestBuilders.delete(URL_BASE + ID_LANCAMENTO)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  private String obterJsonRequisicaoPost() throws JsonProcessingException {
    final LancamentoDto lancamentoDto = new LancamentoDto();
    lancamentoDto.setData(dateFormat.format(DATA));
    lancamentoDto.setTipo(TIPO);
    lancamentoDto.setFuncionarioId(ID_FUNCIONARIO);
    final ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.writeValueAsString(lancamentoDto);
  }

  private Lancamento obterDadosLancamento() {
    final Lancamento lancamento = new Lancamento();
    lancamento.setId(ID_LANCAMENTO);
    lancamento.setData(DATA);
    lancamento.setTipo(TipoEnum.valueOf(TIPO));
    lancamento.setFuncionario(new Funcionario());
    lancamento.getFuncionario().setId(ID_FUNCIONARIO);
    return lancamento;
  }
}
