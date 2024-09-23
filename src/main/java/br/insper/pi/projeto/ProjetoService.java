package br.insper.pi.projeto;

import br.insper.pi.usuario.RetornarUsuarioDTO;
import br.insper.pi.usuario.UsuarioNaoEncontradoException;
import br.insper.pi.usuario.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProjetoService {

    @Autowired
    private ProjetoRepository projetoRepository;

    @Autowired
    private UsuarioService usuarioService;

    public Projeto salvar(Projeto projeto) {
        // Verifica se o gerente existe
        ResponseEntity<RetornarUsuarioDTO> response = usuarioService.getUsuario(projeto.getGerenteCpf());
        if (response.getStatusCode().is2xxSuccessful()) {
            return projetoRepository.save(projeto);
        } else {
            throw new UsuarioNaoEncontradoException("Gerente não encontrado.");
        }
    }

    public List<Projeto> listar() {
        return projetoRepository.findAll();
    }

    public List<Projeto> listarPorStatus(StatusProjeto status) {
        return projetoRepository.findByStatus(status);
    }

    public Projeto adicionarPessoa(String idProjeto, String cpf) {
        Optional<Projeto> op = projetoRepository.findById(idProjeto);
        if (op.isEmpty()) {
            throw new ProjetoNaoEncontradoException("Projeto não encontrado");
        }
        Projeto projeto = op.get();

        if (projeto.getStatus() == StatusProjeto.FINALIZADO) {
            throw new ProjetoFinalizadoException("Não é possível adicionar membros a um projeto finalizado.");
        }

        ResponseEntity<RetornarUsuarioDTO> response = usuarioService.getUsuario(cpf);
        if (response.getStatusCode().is2xxSuccessful()) {
            ArrayList<String> membros = projeto.getMembrosCpf();
            membros.add(cpf);
            projeto.setMembrosCpf(membros);
            return projetoRepository.save(projeto);
        } else {
            throw new UsuarioNaoEncontradoException("Usuário não encontrado.");
        }
    }

    public void deletar(String id) {
        Optional<Projeto> projeto = projetoRepository.findById(id);
        if (projeto.isPresent()) {
            projetoRepository.deleteById(id);
        } else {
            throw new ProjetoNaoEncontradoException("Projeto não encontrado");
        }
    }
}
