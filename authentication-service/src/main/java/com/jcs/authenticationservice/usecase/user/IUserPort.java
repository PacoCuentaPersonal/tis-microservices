package com.jcs.authenticationservice.usecase.user;

import com.jcs.authenticationservice.dto.user.request.LoginDto;
import com.jcs.authenticationservice.dto.user.response.ResponseAuthenticationDto;

public interface IUserPort {

    ResponseAuthenticationDto login (LoginDto dto);
}
