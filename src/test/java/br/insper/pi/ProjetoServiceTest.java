package br.insper.pi;

import br.insper.pi.projeto.Projeto;
import br.insper.pi.projeto.ProjetoFinalizadoException;
import br.insper.pi.projeto.ProjetoNaoEncontradoException;
import br.insper.pi.projeto.ProjetoRepository;
import br.insper.pi.projeto.ProjetoService;
import br.insper.pi.projeto.StatusProjeto;
import br.insper.pi.usuario.RetornarUsuarioDTO;
import br.insper.pi.usuario.UsuarioNaoEncontradoException;
import br.insper.pi.usuario.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProjetoServiceTest {

    @InjectMocks
    private ProjetoService projetoService;

    @Mock
    private ProjetoRepository projetoRepository;

    @Mock
    private UsuarioService usuarioService;

    private Projeto projeto;
    private RetornarUsuarioDTO usuarioDTO;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        projeto = new Projeto();
        projeto.setId("1");
        projeto.setNome("Projeto Teste");
        projeto.setDescricao("Descrição do projeto teste");
        projeto.setStatus(StatusProjeto.PLANEJAMENTO);
        projeto.setGerenteCpf("12345678");
        projeto.setMembrosCpf(new ArrayList<>());

        usuarioDTO = new RetornarUsuarioDTO();
        usuarioDTO.setNome("Gerente Teste");
        usuarioDTO.setCpf("12345678");
    }

    @Test
    public void testSalvarComGerenteExistente() {
        // Mock do usuário existente
        when(usuarioService.getUsuario(projeto.getGerenteCpf()))
                .thenReturn(new ResponseEntity<>(usuarioDTO, HttpStatus.OK));

        // Mock do método save
        when(projetoRepository.save(projeto)).thenReturn(projeto);

        Projeto resultado = projetoService.salvar(projeto);

        assertNotNull(resultado);
        assertEquals(projeto.getNome(), resultado.getNome());
        verify(projetoRepository, times(1)).save(projeto);
    }

    @Test
    public void testSalvarComGerenteNaoExistente() {
        // Mock do usuário não existente: retorna 404 Not Found
        when(usuarioService.getUsuario(projeto.getGerenteCpf()))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        UsuarioNaoEncontradoException exception = assertThrows(
                UsuarioNaoEncontradoException.class,
                () -> projetoService.salvar(projeto)
        );

        assertEquals("Gerente não encontrado.", exception.getMessage());
        verify(projetoRepository, never()).save(projeto);
    }


    @Test
    public void testListar() {
        List<Projeto> projetos = Arrays.asList(projeto);

        when(projetoRepository.findAll()).thenReturn(projetos);

        List<Projeto> resultado = projetoService.listar();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(projetoRepository, times(1)).findAll();
    }

    @Test
    public void testListarPorStatus() {
        List<Projeto> projetos = Arrays.asList(projeto);

        when(projetoRepository.findByStatus(StatusProjeto.PLANEJAMENTO)).thenReturn(projetos);

        List<Projeto> resultado = projetoService.listarPorStatus(StatusProjeto.PLANEJAMENTO);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(projetoRepository, times(1)).findByStatus(StatusProjeto.PLANEJAMENTO);
    }

    @Test
    public void testAdicionarPessoaComSucesso() {
        String cpfMembro = "90210";
        RetornarUsuarioDTO membroDTO = new RetornarUsuarioDTO();
        membroDTO.setNome("Travis Scott");
        membroDTO.setCpf(cpfMembro);

        // Mock do projeto existente
        when(projetoRepository.findById(projeto.getId())).thenReturn(Optional.of(projeto));

        // Mock do usuário existente
        when(usuarioService.getUsuario(cpfMembro))
                .thenReturn(new ResponseEntity<>(membroDTO, HttpStatus.OK));

        // Mock do método save
        when(projetoRepository.save(projeto)).thenReturn(projeto);

        Projeto resultado = projetoService.adicionarPessoa(projeto.getId(), cpfMembro);

        assertNotNull(resultado);
        assertTrue(resultado.getMembrosCpf().contains(cpfMembro));
        verify(projetoRepository, times(1)).save(projeto);
    }

    @Test
    public void testAdicionarPessoaProjetoNaoEncontrado() {
        String cpfMembro = "90210";

        when(projetoRepository.findById("2")).thenReturn(Optional.empty());

        ProjetoNaoEncontradoException exception = assertThrows(
                ProjetoNaoEncontradoException.class,
                () -> projetoService.adicionarPessoa("2", cpfMembro)
        );

        assertEquals("Projeto não encontrado", exception.getMessage());
        verify(projetoRepository, never()).save(any());
    }

    @Test
    public void testAdicionarPessoaProjetoFinalizado() {
        String cpfMembro = "90210";
        projeto.setStatus(StatusProjeto.FINALIZADO);

        when(projetoRepository.findById(projeto.getId())).thenReturn(Optional.of(projeto));

        ProjetoFinalizadoException exception = assertThrows(
                ProjetoFinalizadoException.class,
                () -> projetoService.adicionarPessoa(projeto.getId(), cpfMembro)
        );

        assertEquals("Não é possível adicionar membros a um projeto finalizado.", exception.getMessage());
        verify(projetoRepository, never()).save(any());
    }

    @Test
    public void testAdicionarPessoaUsuarioNaoEncontrado() {
        String cpfMembro = "00000000";

        when(projetoRepository.findById(projeto.getId())).thenReturn(Optional.of(projeto));

        // Mock do usuário não existente: retorna 404 Not Found
        when(usuarioService.getUsuario(cpfMembro))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        UsuarioNaoEncontradoException exception = assertThrows(
                UsuarioNaoEncontradoException.class,
                () -> projetoService.adicionarPessoa(projeto.getId(), cpfMembro)
        );

        assertEquals("Usuário não encontrado.", exception.getMessage());
        verify(projetoRepository, never()).save(any());
    }


    @Test
    public void testDeletarComSucesso() {
        when(projetoRepository.findById(projeto.getId())).thenReturn(Optional.of(projeto));

        projetoService.deletar(projeto.getId());

        verify(projetoRepository, times(1)).deleteById(projeto.getId());
    }

    @Test
    public void testDeletarProjetoNaoEncontrado() {
        when(projetoRepository.findById("2")).thenReturn(Optional.empty());

        ProjetoNaoEncontradoException exception = assertThrows(
                ProjetoNaoEncontradoException.class,
                () -> projetoService.deletar("2")
        );

        assertEquals("Projeto não encontrado", exception.getMessage());
        verify(projetoRepository, never()).deleteById(any());
    }

    @Test
    public void testAdicionarPessoaProjetoNaoFinalizado() {
        String cpfMembro = "90210";
        RetornarUsuarioDTO membroDTO = new RetornarUsuarioDTO();
        membroDTO.setNome("Travis Scott");
        membroDTO.setCpf(cpfMembro);

        projeto.setStatus(StatusProjeto.EXECUCAO);

        when(projetoRepository.findById(projeto.getId())).thenReturn(Optional.of(projeto));

        when(usuarioService.getUsuario(cpfMembro))
                .thenReturn(new ResponseEntity<>(membroDTO, HttpStatus.OK));

        when(projetoRepository.save(projeto)).thenReturn(projeto);

        Projeto resultado = projetoService.adicionarPessoa(projeto.getId(), cpfMembro);

        assertNotNull(resultado);
        assertTrue(resultado.getMembrosCpf().contains(cpfMembro));
        verify(projetoRepository, times(1)).save(projeto);
    }

}