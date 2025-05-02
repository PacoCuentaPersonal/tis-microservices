package com.jcs.authenticationservice.usecase.user;

import com.jcs.authenticationservice.dto.user.request.LoginDto;
import com.jcs.authenticationservice.dto.user.request.RegisterEmployeeUserDto;
import com.jcs.authenticationservice.dto.user.response.ResponseAuthenticationDto;
import com.jcs.authenticationservice.entity.UserEntity;

public interface IEmployeeUserPort {
    UserEntity register(RegisterEmployeeUserDto dto);
    ResponseAuthenticationDto login (LoginDto dto);
}
