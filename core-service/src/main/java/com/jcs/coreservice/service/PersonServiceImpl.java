package com.jcs.coreservice.service;

import com.jcs.coreservice.dto.request.CreatePersonRequest;
import com.jcs.coreservice.dto.request.PersonRequest;
import com.jcs.coreservice.exception.ApiProviderException;
import com.jcs.coreservice.model.entity.DistrictEntity;
import com.jcs.coreservice.model.entity.DocumentTypeEntity;
import com.jcs.coreservice.model.entity.PersonEntity;
import com.jcs.coreservice.dto.response.PersonResponse;
import com.jcs.coreservice.model.entity.PersonId;
import com.jcs.coreservice.model.rest.ResponseApiPeruDevs;
import com.jcs.coreservice.repository.JpaPersonRepository;
import com.jcs.coreservice.service.reniec.ApiProviderClient;
import com.jcs.coreservice.usecase.IDistrictService;
import com.jcs.coreservice.usecase.IDocumentTypeService;
import com.jcs.coreservice.usecase.IPersonService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PersonServiceImpl implements IPersonService {
    private final JpaPersonRepository jpaPersonRepository;
    private final ApiProviderClient apiProviderClient;

    private final IDistrictService districtService;
    private final IDocumentTypeService documentTypeService;

    public PersonServiceImpl(JpaPersonRepository jpaPersonRepository, ApiProviderClient apiProviderClient, IDistrictService districtService, IDocumentTypeService documentTypeService) {
        this.jpaPersonRepository = jpaPersonRepository;
        this.apiProviderClient = apiProviderClient;
        this.districtService = districtService;
        this.documentTypeService = documentTypeService;
    }
    @Override
    public PersonResponse getPersonByDni(String dni) throws ApiProviderException {

        Optional<PersonEntity> byDni = jpaPersonRepository.findByDni(dni);
        if ( byDni.isEmpty()){
            return getPersonByApi(dni);
        }
        PersonEntity person = byDni.get();
        return new PersonResponse(
                person.getId().getDocumentNumber(),
                String.format("%s %s %s",
                        person.getNames(),
                        person.getPaternalSurname(),
                        person.getMaternalSurname()
                ),
                person.getNames(),
                person.getPaternalSurname(),
                person.getMaternalSurname(),
                person.getGender().toString(),
                person.getBirthdate().toString()
        );
    }
    private PersonResponse getPersonByApi(String dni) throws ApiProviderException {
        ResponseApiPeruDevs response = apiProviderClient.getProviderData(dni, ResponseApiPeruDevs.class);
        ResponseApiPeruDevs.PersonaResultado apiReniecData = response.getResultado();
        return new PersonResponse(
                apiReniecData.getId(),
                apiReniecData.getNombreCompleto(),
                apiReniecData.getNombres(),
                apiReniecData.getApellidoPaterno(),
                apiReniecData.getApellidoMaterno(),
                apiReniecData.getGenero(),
                apiReniecData.getFechaNacimiento()
        ) ;
    }

    @Override
    public PersonEntity createPerson(CreatePersonRequest request) {
        PersonRequest personRq = request.getPersonRequest();
        DistrictEntity districtRef = districtService.getDistrictByPublicId(request.getPIdDistrict());
        DocumentTypeEntity docTypeRef = documentTypeService.getDocumentTypeByPublicId(request.getPIdDocumentType());

        PersonId buildId = new PersonId(
                docTypeRef.getId(),
                personRq.getDni()
        );
        PersonEntity personEntity = PersonEntity
                .builder()
                .id(buildId)
                .names(personRq.getNames())
                .paternalSurname(personRq.getSurnamePaternal())
                .maternalSurname(personRq.getSurnameMaternal())
                .birthdate(personRq.getBirthDate())
                .gender(personRq.getGender().charAt(0))
                .active(Boolean.TRUE)
                .build();

        return personEntity;
    }

}
