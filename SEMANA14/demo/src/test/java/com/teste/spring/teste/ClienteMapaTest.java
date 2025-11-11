package com.teste.spring.teste;

import com.teste.spring.teste.dto.ClienteDto;
import com.teste.spring.teste.mapa.ClienteMapa;
import com.teste.spring.teste.model.Cliente;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClienteMapaTest {

    @Test
    void toDTO_deveConverterEntidadeParaDto() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        cliente.setEmail("joao@example.com");
        cliente.setTelefone("11987654321");

        // Act
        ClienteDto dto = ClienteMapa.toDTO(cliente);

        // Assert
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getNome()).isEqualTo("João Silva");
        assertThat(dto.getEmail()).isEqualTo("joao@example.com");
        assertThat(dto.getTelefone()).isEqualTo("11987654321");
    }

    @Test
    void toDTO_deveHandlarClienteComValoresNulos() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome(null);
        cliente.setEmail(null);
        cliente.setTelefone(null);

        // Act
        ClienteDto dto = ClienteMapa.toDTO(cliente);

        // Assert
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getNome()).isNull();
        assertThat(dto.getEmail()).isNull();
        assertThat(dto.getTelefone()).isNull();
    }

    @Test
    void toEntity_deveConverterDtoParaEntidade() {
        // Arrange
        ClienteDto dto = new ClienteDto();
        dto.setId(1L);
        dto.setNome("Maria Silva");
        dto.setEmail("maria@example.com");
        dto.setTelefone("11987654322");

        // Act
        Cliente cliente = ClienteMapa.toEntity(dto);

        // Assert
        assertThat(cliente.getId()).isEqualTo(1L);
        assertThat(cliente.getNome()).isEqualTo("Maria Silva");
        assertThat(cliente.getEmail()).isEqualTo("maria@example.com");
        assertThat(cliente.getTelefone()).isEqualTo("11987654322");
    }

    @Test
    void toEntity_deveHandlarDtoComValoresNulos() {
        // Arrange
        ClienteDto dto = new ClienteDto();
        dto.setId(1L);
        dto.setNome(null);
        dto.setEmail(null);
        dto.setTelefone(null);

        // Act
        Cliente cliente = ClienteMapa.toEntity(dto);

        // Assert
        assertThat(cliente.getId()).isEqualTo(1L);
        assertThat(cliente.getNome()).isNull();
        assertThat(cliente.getEmail()).isNull();
        assertThat(cliente.getTelefone()).isNull();
    }

    @Test
    void copyToEntity_deveCopiarDtoParaEntidadeExistente() {
        // Arrange
        ClienteDto dto = new ClienteDto();
        dto.setNome("Pedro Santos");
        dto.setEmail("pedro@example.com");
        dto.setTelefone("11987654323");

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Nome Antigo");
        cliente.setEmail("antigo@example.com");

        // Act
        ClienteMapa.copyToEntity(dto, cliente);

        // Assert
        assertThat(cliente.getId()).isEqualTo(1L);
        assertThat(cliente.getNome()).isEqualTo("Pedro Santos");
        assertThat(cliente.getEmail()).isEqualTo("pedro@example.com");
        assertThat(cliente.getTelefone()).isEqualTo("11987654323");
    }

    @Test
    void copyToEntity_devePreservarIdAoAtualizar() {
        // Arrange
        ClienteDto dto = new ClienteDto();
        dto.setNome("Ana Costa");
        dto.setEmail("ana@example.com");

        Cliente cliente = new Cliente();
        cliente.setId(99L);

        // Act
        ClienteMapa.copyToEntity(dto, cliente);

        // Assert - ID should remain unchanged
        assertThat(cliente.getId()).isEqualTo(99L);
        assertThat(cliente.getNome()).isEqualTo("Ana Costa");
        assertThat(cliente.getEmail()).isEqualTo("ana@example.com");
    }

    @Test
    void roundTrip_deveConverterClienteParaDtoEViceVersa() {
        // Arrange
        Cliente clienteOriginal = new Cliente();
        clienteOriginal.setId(1L);
        clienteOriginal.setNome("Carlos");
        clienteOriginal.setEmail("carlos@example.com");
        clienteOriginal.setTelefone("11987654324");

        // Act
        ClienteDto dto = ClienteMapa.toDTO(clienteOriginal);
        Cliente clienteRecuperado = ClienteMapa.toEntity(dto);

        // Assert
        assertThat(clienteRecuperado.getId()).isEqualTo(clienteOriginal.getId());
        assertThat(clienteRecuperado.getNome()).isEqualTo(clienteOriginal.getNome());
        assertThat(clienteRecuperado.getEmail()).isEqualTo(clienteOriginal.getEmail());
        assertThat(clienteRecuperado.getTelefone()).isEqualTo(clienteOriginal.getTelefone());
    }
}
