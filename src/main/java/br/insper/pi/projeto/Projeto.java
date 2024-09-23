package br.insper.pi.projeto;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Document
public class Projeto {

    @Id
    private String id;
    private String nome;
    private String descricao;
    private StatusProjeto status;
    private String gerenteCpf;
    private ArrayList<String> membrosCpf;

    // Construtor padrão
    public Projeto() {
        this.membrosCpf = new ArrayList<>();
    }

    // Getters e Setters
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public StatusProjeto getStatus() {
        return status;
    }
    public void setStatus(StatusProjeto status) {
        this.status = status;
    }

    public String getGerenteCpf() {
        return gerenteCpf;
    }
    public void setGerenteCpf(String gerenteCpf) {
        this.gerenteCpf = gerenteCpf;
    }

    public ArrayList<String> getMembrosCpf() {
        return membrosCpf;
    }
    public void setMembrosCpf(ArrayList<String> membrosCpf) {
        this.membrosCpf = membrosCpf;
    }
}
