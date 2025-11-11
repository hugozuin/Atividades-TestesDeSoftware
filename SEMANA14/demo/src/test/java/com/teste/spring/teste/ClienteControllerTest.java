package com.teste.spring.teste;


import com.teste.spring.teste.controller.ClienteController;
import com.teste.spring.teste.dto.ClienteDto;
import com.teste.spring.teste.exception.BusinessException;
import com.teste.spring.teste.exception.NotFoundException;
import com.teste.spring.teste.model.Cliente;
import com.teste.spring.teste.repository.ClienteRepository;
import com.teste.spring.teste.service.ClienteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ClienteController.class)
public class ClienteControllerTest {
    @Autowired
    MockMvc mvc;

    @MockBean
     ClienteRepository repo;

    @MockBean
    ClienteService service;

    @Test
    void listar_deveRetornarPaginaDeClientes() throws Exception {
        // 1️⃣ Simular dados retornados pelo repositório
        Cliente cliente1 = new Cliente();
        cliente1.setId(1L);
        cliente1.setNome("Ana");
        cliente1.setEmail("ana@ex.com");

        Cliente cliente2 = new Cliente();
        cliente2.setId(2L);
        cliente2.setNome("Bruno");
        cliente2.setEmail("bruno@ex.com");

        Page<Cliente> paginaSimulada = new PageImpl<>(
                List.of(cliente1, cliente2),
                PageRequest.of(0, 10, Sort.by("id").ascending()),
                2
        );

        // 2️⃣ Definir comportamento do mock
        when(repo.findAll(any(Pageable.class))).thenReturn(paginaSimulada);

        // 3️⃣ Executar requisição GET simulada
        mvc.perform(get("/api/clientes")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "id,asc")
                        .accept(MediaType.APPLICATION_JSON))
                // 4️⃣ Verificar status HTTP e estrutura JSON
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].nome").value("Ana"))
                .andExpect(jsonPath("$.content[1].email").value("bruno@ex.com"))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    void listar_deveRetornarListaVazia() throws Exception {
        Page<Cliente> paginaVazia = new PageImpl<>(
                List.of(),
                PageRequest.of(0, 10),
                0
        );
        when(repo.findAll(any(Pageable.class))).thenReturn(paginaVazia);

        mvc.perform(get("/api/clientes")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void buscar_deveRetornarClientePorId() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João");
        cliente.setEmail("joao@ex.com");
        cliente.setTelefone("11987654321");

        when(service.buscar(1L)).thenReturn(cliente);

        mvc.perform(get("/api/clientes/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João"))
                .andExpect(jsonPath("$.email").value("joao@ex.com"))
                .andExpect(jsonPath("$.telefone").value("11987654321"));

        verify(service, times(1)).buscar(1L);
    }

    @Test
    void buscar_deveRetornar404QuandoClienteNaoExiste() throws Exception {
        when(service.buscar(999L)).thenThrow(new NotFoundException("Cliente não encontrado"));

        mvc.perform(get("/api/clientes/999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Cliente não encontrado"));
    }

    @Test
    void criar_deveSalvarClienteComSucesso() throws Exception {
        ClienteDto dto = new ClienteDto();
        dto.setNome("Maria");
        dto.setEmail("maria@ex.com");
        dto.setTelefone("11999999999");

        Cliente clienteSalvo = new Cliente();
        clienteSalvo.setId(1L);
        clienteSalvo.setNome("Maria");
        clienteSalvo.setEmail("maria@ex.com");
        clienteSalvo.setTelefone("11999999999");

        when(service.criar(any(Cliente.class))).thenReturn(clienteSalvo);

        mvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Maria\",\"email\":\"maria@ex.com\",\"telefone\":\"11999999999\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Maria"))
                .andExpect(jsonPath("$.email").value("maria@ex.com"));

        verify(service, times(1)).criar(any(Cliente.class));
    }

    @Test
    void criar_deveRetornar422QuandoEmailJaExiste() throws Exception {
        when(service.criar(any(Cliente.class)))
                .thenThrow(new BusinessException("Email já cadastrado"));

        mvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Pedro\",\"email\":\"pedro@ex.com\",\"telefone\":\"11988888888\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string("Email já cadastrado"));
    }

    @Test
    void atualizar_deveAtualizarClienteComSucesso() throws Exception {
        Cliente clienteAtualizado = new Cliente();
        clienteAtualizado.setId(1L);
        clienteAtualizado.setNome("João Atualizado");
        clienteAtualizado.setEmail("joao.novo@ex.com");
        clienteAtualizado.setTelefone("11987654322");

        when(service.atualizar(eq(1L), any(Cliente.class))).thenReturn(clienteAtualizado);

        mvc.perform(put("/api/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"João Atualizado\",\"email\":\"joao.novo@ex.com\",\"telefone\":\"11987654322\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João Atualizado"))
                .andExpect(jsonPath("$.email").value("joao.novo@ex.com"));

        verify(service, times(1)).atualizar(eq(1L), any(Cliente.class));
    }

    @Test
    void atualizar_deveRetornar404QuandoClienteNaoExiste() throws Exception {
        when(service.atualizar(eq(999L), any(Cliente.class)))
                .thenThrow(new NotFoundException("Cliente não encontrado"));

        mvc.perform(put("/api/clientes/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Teste\",\"email\":\"teste@ex.com\",\"telefone\":\"11999999999\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Cliente não encontrado"));
    }

    @Test
    void excluir_deveExcluirClienteComSucesso() throws Exception {
        mvc.perform(delete("/api/clientes/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(service, times(1)).excluir(1L);
    }

    @Test
    void excluir_deveRetornar404QuandoClienteNaoExiste() throws Exception {
        doThrow(new NotFoundException("Cliente não encontrado")).when(service).excluir(999L);

        mvc.perform(delete("/api/clientes/999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Cliente não encontrado"));
    }
}
