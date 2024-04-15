package com.hdworks.java.spring.poc.textfiletodbbatchprocessor.textfiletodbbatchprocessor.batch.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ITAGEntity {
    @Id
    @GeneratedValue
    private Integer id;
    private String TAG_AGENCY_ID;
    private String TAG_SERIAL_NUMBER;
    private String TAG_STATUS;
    private String TAG_ACCT_INFO;

}
