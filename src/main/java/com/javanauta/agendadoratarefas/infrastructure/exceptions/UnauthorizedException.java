package com.javanauta.agendadoratarefas.infrastructure.exceptions;


import javax.security.sasl.AuthenticationException;

public class UnauthorizedException extends AuthenticationException {


    public UnauthorizedException(String mensagem){
        super(mensagem);
    }

    public UnauthorizedException(String mensagem, Throwable throwable){
        super(mensagem);
    }
}
