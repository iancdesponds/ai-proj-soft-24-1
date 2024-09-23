package br.insper.pi.projeto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/projeto")
public class ProjetoController {

    @Autowired
    private ProjetoService projetoService;

    // Cadastro de Projeto
    @PostMapping
    public Projeto salvar(@RequestBody Projeto projeto) {
        return projetoService.salvar(projeto);
    }

    // Listagem de Projetos
    @GetMapping
    public List<Projeto> listar() {
        return projetoService.listar();
    }

    // Listagem de Projetos por Status
    @GetMapping("/status/{status}")
    public List<Projeto> listarPorStatus(@PathVariable StatusProjeto status) {
        return projetoService.listarPorStatus(status);
    }

    // Adicionar Pessoa no Projeto
    @PostMapping("/{idProjeto}/membro")
    public Projeto adicionarPessoa(@PathVariable String idProjeto, @RequestBody Map<String, String> payload) {
        String cpf = payload.get("cpf");
        return projetoService.adicionarPessoa(idProjeto, cpf);
    }


    // Deletar Projeto
    @DeleteMapping("/{id}")
    public void deletar(@PathVariable String id) {
        projetoService.deletar(id);
    }
}
