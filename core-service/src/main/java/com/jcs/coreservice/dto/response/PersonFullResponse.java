package com.jcs.coreservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PersonFullResponse {
    private UUID publicID;
    private String names;
    private String surnamePaternal;
    private String surnameMaternal;
    private Date birthDate;
    private Character gender;
    private String documentNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
