package br.insper.pi.exception;

import br.insper.pi.projeto.ProjetoFinalizadoException;
import br.insper.pi.projeto.ProjetoNaoEncontradoException;
import br.insper.pi.usuario.UsuarioNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProjetoNaoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleProjetoNaoEncontrado(ProjetoNaoEncontradoException e) {
        return e.getMessage();
    }

    @ExceptionHandler(ProjetoFinalizadoException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleProjetoFinalizado(ProjetoFinalizadoException e) {
        return e.getMessage();
    }

    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleUsuarioNaoEncontrado(UsuarioNaoEncontradoException e) {
        return e.getMessage();
    }
}
