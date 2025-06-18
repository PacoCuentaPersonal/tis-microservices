package com.jcs.coreservice.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class PersonResponse {
    private String documentNumber;
    private String fullName;
    private String names;
    private String surnamePaternal;
    private String surnameMaternal;
    private String gender;
    private String birthDate;
}
