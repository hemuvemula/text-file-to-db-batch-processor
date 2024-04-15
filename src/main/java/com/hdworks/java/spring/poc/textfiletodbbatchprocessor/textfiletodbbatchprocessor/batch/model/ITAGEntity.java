package com.hdworks.java.spring.poc.textfiletodbbatchprocessor.textfiletodbbatchprocessor.batch.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ITAGEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "itagentity_gen")
    @SequenceGenerator(name = "itagentity_gen", sequenceName = "itagentity_seq", allocationSize = 10000)
    private Integer id;
    private String TAG_AGENCY_ID;
    private String TAG_SERIAL_NUMBER;
    private String TAG_STATUS;
    private String TAG_ACCT_INFO;

}
