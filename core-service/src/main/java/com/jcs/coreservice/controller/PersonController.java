package com.jcs.coreservice.controller;

import com.jcs.coreservice.dto.request.CreatePersonRequest;
import com.jcs.coreservice.dto.request.PersonRequest;
import com.jcs.coreservice.dto.response.PersonFullResponse;
import com.jcs.coreservice.dto.response.PersonResponse;
import com.jcs.coreservice.exception.ApiProviderException;
import com.jcs.coreservice.model.entity.PersonEntity;
import com.jcs.coreservice.usecase.IPersonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/v1/reniec/person")
public class PersonController {

    private final IPersonService personService;

    public PersonController(IPersonService personService) {
        this.personService = personService;
    }

    @GetMapping(value = "")
    public ResponseEntity<?> findPerson(@RequestParam(value = "dni") String dni) throws ApiProviderException {
        PersonResponse personByDni= personService.getPersonByDni(dni);
        return ResponseEntity.ok().body(personByDni);
    }
    @PostMapping(value = "")
    public ResponseEntity<PersonFullResponse> createPerson(@RequestBody CreatePersonRequest personRequest) {
        PersonEntity createdPerson = personService.createPerson(personRequest);
        //Map to secured dto
        PersonFullResponse personFullResponse = new PersonFullResponse(
                createdPerson.getPublicId(),
                createdPerson.getNames(),
                createdPerson.getPaternalSurname(),
                createdPerson.getMaternalSurname(),
                createdPerson.getBirthdate(),
                createdPerson.getGender(),
                createdPerson.getId().getDocumentNumber(),
                createdPerson.getCreatedAt(),
                createdPerson.getUpdatedAt()
        );
        return ResponseEntity.ok().body(personFullResponse);
    }

}
