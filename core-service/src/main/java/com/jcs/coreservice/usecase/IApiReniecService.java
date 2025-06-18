package com.jcs.coreservice.usecase;

import com.jcs.coreservice.model.rest.ResponseApiPeruDevs;

public interface IApiReniecService {
    ResponseApiPeruDevs getApiReniecData(String dni);
}
