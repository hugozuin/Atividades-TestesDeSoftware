package com.teste.spring.teste;


import com.teste.spring.teste.model.Cliente;
import com.teste.spring.teste.exception.*;
import com.teste.spring.teste.repository.ClienteRepository;
import com.teste.spring.teste.service.ClienteService;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    ClienteRepository repo;

    @InjectMocks
    ClienteService service;

    @Test
    void criar_deveRetornarClienteSalvo() {
        Cliente c = new Cliente();
        c.setNome("João");
        c.setEmail("j@ex.com");
        c.setTelefone("11987654321");

        Cliente clienteSalvo = new Cliente();
        clienteSalvo.setId(1L);
        clienteSalvo.setNome("João");
        clienteSalvo.setEmail("j@ex.com");
        clienteSalvo.setTelefone("11987654321");

        when(repo.existsByEmail("j@ex.com")).thenReturn(false);
        when(repo.save(any(Cliente.class))).thenReturn(clienteSalvo);

        Cliente resultado = service.criar(c);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNome()).isEqualTo("João");
        assertThat(resultado.getEmail()).isEqualTo("j@ex.com");
        verify(repo, times(1)).save(any(Cliente.class));
    }

    @Test
    void criar_deveLancarSeEmailJaExiste() {
        Cliente c = new Cliente();
        c.setNome("João");
        c.setEmail("j@ex.com");
        when(repo.existsByEmail("j@ex.com")).thenReturn(true);

        assertThatThrownBy(() -> service.criar(c))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Email já cadastrado");
        verify(repo, never()).save(any());
    }

    @Test
    void buscar_deveRetornarClientePorId() {
        Cliente c = new Cliente();
        c.setId(1L);
        c.setNome("Ana");
        c.setEmail("ana@ex.com");

        when(repo.findById(1L)).thenReturn(Optional.of(c));

        Cliente resultado = service.buscar(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNome()).isEqualTo("Ana");
        verify(repo, times(1)).findById(1L);
    }

    @Test
    void buscar_deveLancarNotFoundParaIdInvalido() {
        when(repo.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscar(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Cliente não encontrado");
    }

    @Test
    void atualizar_deveAtualizarCamposBasicos() {
        Cliente antigo = new Cliente();
        antigo.setId(1L);
        antigo.setNome("Antigo");
        antigo.setEmail("a@ex.com");
        antigo.setTelefone("11");

        when(repo.findById(1L)).thenReturn(Optional.of(antigo));
        when(repo.findByEmail("novo@ex.com")).thenReturn(Optional.of(antigo)); // mesmo cliente
        when(repo.existsByEmail("novo@ex.com")).thenReturn(true);
        when(repo.save(any(Cliente.class))).thenAnswer(i -> i.getArgument(0));

        Cliente dados = new Cliente();
        dados.setNome("Novo");
        dados.setEmail("novo@ex.com");
        dados.setTelefone("22");

        Cliente atualizado = service.atualizar(1L, dados);

        assertThat(atualizado.getNome()).isEqualTo("Novo");
        assertThat(atualizado.getEmail()).isEqualTo("novo@ex.com");
        assertThat(atualizado.getTelefone()).isEqualTo("22");
    }

    @Test
    void atualizar_deveLancarSeEmailJaExisteEmOutroCliente() {
        Cliente cliente1 = new Cliente();
        cliente1.setId(1L);
        cliente1.setNome("João");
        cliente1.setEmail("joao@ex.com");

        Cliente cliente2 = new Cliente();
        cliente2.setId(2L);
        cliente2.setNome("Maria");
        cliente2.setEmail("maria@ex.com");

        Cliente dados = new Cliente();
        dados.setNome("João Atualizado");
        dados.setEmail("maria@ex.com");

        when(repo.findById(1L)).thenReturn(Optional.of(cliente1));
        when(repo.existsByEmail("maria@ex.com")).thenReturn(true);
        when(repo.findByEmail("maria@ex.com")).thenReturn(Optional.of(cliente2));

        assertThatThrownBy(() -> service.atualizar(1L, dados))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Email já cadastrado para outro cliente");
        verify(repo, never()).save(any());
    }

    @Test
    void atualizar_deveLancarNotFoundParaClienteInexistente() {
        when(repo.findById(999L)).thenReturn(Optional.empty());

        Cliente dados = new Cliente();
        dados.setNome("Teste");

        assertThatThrownBy(() -> service.atualizar(999L, dados))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Cliente não encontrado");
    }

    @Test
    void excluir_deveExcluirClienteComSucesso() {
        Cliente c = new Cliente();
        c.setId(1L);
        c.setNome("João");

        when(repo.findById(1L)).thenReturn(Optional.of(c));

        service.excluir(1L);

        verify(repo, times(1)).findById(1L);
        verify(repo, times(1)).delete(c);
    }

    @Test
    void excluir_deveLancarNotFoundParaIdInvalido() {
        when(repo.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.excluir(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Cliente não encontrado");
        verify(repo, never()).delete(any());
    }
}
