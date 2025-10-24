package com.fatec.cliente_backv2.persistencia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.fatec.cliente_backv2.model.Cliente;
import com.fatec.cliente_backv2.model.ClienteDTO;
import com.fatec.cliente_backv2.service.ClienteRepository;
import com.fatec.cliente_backv2.service.ClienteService;
import com.fatec.cliente_backv2.service.EnderecoService;
import com.fatec.cliente_backv2.service.IEnderecoService;

import java.util.List;
import static org.mockito.Mockito.*;
@DataJpaTest
class Req10ConcultaClientePeloCPFTests {

	private Cliente cliente;
	@Autowired

	@Mock
    private ClienteRepository clienteRepository;

    @Mock
    private IEnderecoService enderecoService;
  
    @InjectMocks
    private ClienteService clienteService;

	public void setup() {
		MockitoAnnotations.openMocks(this);
		cliente = new Cliente();
		cliente.setCpf("80983098000");
		cliente.setNome("Jose da Silva");
		cliente.setCep("01310-100");
		cliente.setEndereco("Av. Paulista");
		cliente.setComplemento("123");
		cliente.setEmail("jose@gmail.com");
		cliente.setDataCadastro();
		clienteRepository.save(cliente);
	}
	
	@Test
	void ct01_quando_cliente_cadastrado_retorna_detalhes() {
		//Dado - que o cpf esta cadastrado
		setup();
		//Quando - consulto o cliente pelo cpf
		Optional<Cliente> c = clienteRepository.findByCpf("80983098000");
		//Entao - retorna os detalhes do cliente
		assertTrue (c.isPresent());
	}
	@Test
	void ct02_quando_cliente_nao_cadastrado_retorna_vazio() {
		//Dado - que o cpf nao esta cadastrado
		//Quando - consulto o cpf
		Optional<Cliente> c = clienteRepository.findByCpf("80983098001");
		//Entao - retorna vazio
		assertTrue (c.isEmpty());
	}
	@Test
	void ct03_quando_cpf_null_retorna_vazio() {
		//Dado - que o cpf nao esta cadastrado
		//Quando - consulto o cpf
		Optional<Cliente> c = clienteRepository.findByCpf(null);
		//Entao - retorna vazio
		assertTrue (c.isEmpty());
	}
	@Test
	void ct04_quando_cpf_maior_que_11() {
		//Dado - que o cpf nao esta cadastrado
		//Quando - consulto o cpf
		Optional<Cliente> c = clienteRepository.findByCpf("1111111110560");
		//Entao - retorna vazio
		assertTrue (c.isEmpty());
	}
		@Test
	void ct05_quando_cpf_menor_que_11_retorna () {
		//Dado - que o cpf nao esta cadastrado
		//Quando - consulto o cpf
		Optional<Cliente> c = clienteRepository.findByCpf("110560");
		//Entao - retorna vazio
		assertTrue (c.isEmpty());
	}
		@Test
	void ct06_quando_cpf_com_letra_retorna_vazio() {
		//Dado - que o cpf nao esta cadastrado
		//Quando - consulto o cpf
		Optional<Cliente> c = clienteRepository.findByCpf("110560");
		//Entao - retorna vazio
		assertTrue (c.isEmpty());
	}

		@Test
    void consultaTodos_deveRetornarListaDeClientes() {
        // Arrange
        Cliente cliente1 = new Cliente();
        Cliente cliente2 = new Cliente();
        when(clienteRepository.findAll()).thenReturn(List.of(cliente1, cliente2));

        // Act
        List<Cliente> clientes = clienteService.consultaTodos();

        // Assert
        assertEquals(2, clientes.size());
        verify(clienteRepository, times(1)).findAll();
    }

  @Test
    void cadastrar_deve_cadastrar_com_sucesso() {
        ClienteDTO clienteDTO = new ClienteDTO("41377905829", "João", "12345-678", "Apt 101", "joao@gmail.com");
        when(clienteRepository.findByCpf(clienteDTO.cpf())).thenReturn(Optional.empty());
        when(enderecoService.obtemLogradouroPorCep(clienteDTO.cep())).thenReturn(Optional.of("Rua A"));
        Cliente clienteSalvo = new Cliente();
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteSalvo);

        // Act
        Cliente cliente = clienteService.cadastrar(clienteDTO);

        // Assert
        assertNotNull(cliente);
        verify(clienteRepository, times(1)).findByCpf(clienteDTO.cpf());
        verify(enderecoService, times(1)).obtemLogradouroPorCep(clienteDTO.cep());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

  @Test
    void cadastrar_deveLancarExcecaoQuandoCpfDuplicado() {
        // Arrange
        ClienteDTO clienteDTO = new ClienteDTO("12345678900", "João", "12345-678", "Apt 101", "joao@gmail.com");
        when(clienteRepository.findByCpf(clienteDTO.cpf())).thenReturn(Optional.of(new Cliente()));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            clienteService.cadastrar(clienteDTO);
        });
        assertEquals("Cliente com este CPF já cadastrado.", exception.getMessage());
        verify(clienteRepository, times(1)).findByCpf(clienteDTO.cpf());
        verify(clienteRepository, never()).save(any(Cliente.class));
    }
	@Test
    void ct02_quando_cadastrar_cliente_com_cpf_duplicado_lanca_excecao() {
        // Dado (Arrange)
        ClienteDTO dto = new ClienteDTO("Nome Teste", "12345678900", "teste@email.com", "01001000", "Apto 101");
        Cliente clienteExistente = new Cliente(); // Um objeto cliente qualquer para simular a existência

        // 1. Simula que o CPF JÁ EXISTE
        when(clienteRepository.findByCpf(dto.cpf())).thenReturn(Optional.of(clienteExistente));

        // Quando (Act) & Então (Assert)
        // Verificamos se a exceção correta é lançada
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            clienteService.cadastrar(dto);
        });

        // Verificamos a mensagem da exceção
        assertEquals("Cliente com este CPF já cadastrado.", exception.getMessage());

        // Garante que o serviço parou e não tentou chamar o serviço de endereço ou salvar
        verify(enderecoService, never()).obtemLogradouroPorCep(anyString());
        verify(clienteRepository, never()).save(any(Cliente.class));
    }
 
	
}
