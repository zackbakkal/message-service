package com.zack.projects.chatapp.message.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class UserNameExistsException extends Exception{

    private static final long serialVersionUID = 1L;

    public UserNameExistsException(String message) {
        super(message);
    }

}
