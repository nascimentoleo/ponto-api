package com.example.ponto.api.controllers;

import com.example.ponto.api.entities.Empresa;
import com.example.ponto.api.services.EmpresaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class EmpresaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmpresaService empresaService;

    private static final String BUSCAR_EMPRESA_CNPJ_URL = "/api/empresas/cnpj/";
    private static final Long ID = 1L;
    private static final String CNPJ = "51463645000100";
    private static final String RAZAO_SOCIAL = "Qualquer";

    @Test
    public void testBuscarEmpresaCNpjInvalido() throws Exception {
        given(empresaService.buscarPorCnpj(anyString())).willReturn(Optional.empty());
        mockMvc.perform(
                MockMvcRequestBuilders
                        .get(BUSCAR_EMPRESA_CNPJ_URL + CNPJ)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Empresa não encontrada para o CNPJ " + CNPJ));
    }

    @Test
    public void testBuscarEmpresaCnpjValido() throws Exception {
        given(empresaService.buscarPorCnpj(anyString())).willReturn(Optional.of(obterDadosEmpresa()));

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get(BUSCAR_EMPRESA_CNPJ_URL + CNPJ)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(ID))
                .andExpect(jsonPath("$.data.razaoSocial").value(RAZAO_SOCIAL))
                .andExpect(jsonPath("$.data.cnpj").value(CNPJ))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    private Empresa obterDadosEmpresa() {
        final Empresa empresa = new Empresa();
        empresa.setId(ID);
        empresa.setRazaoSocial(RAZAO_SOCIAL);
        empresa.setCnpj(CNPJ);
        return empresa;
    }
}
