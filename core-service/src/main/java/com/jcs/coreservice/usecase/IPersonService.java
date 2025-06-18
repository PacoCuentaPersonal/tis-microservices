package com.jcs.coreservice.usecase;

import com.jcs.coreservice.dto.request.CreatePersonRequest;
import com.jcs.coreservice.dto.response.PersonResponse;
import com.jcs.coreservice.exception.ApiProviderException;
import com.jcs.coreservice.model.entity.PersonEntity;

public interface IPersonService {
    PersonResponse getPersonByDni(String dni) throws ApiProviderException;
    PersonEntity createPerson(CreatePersonRequest person);
}
