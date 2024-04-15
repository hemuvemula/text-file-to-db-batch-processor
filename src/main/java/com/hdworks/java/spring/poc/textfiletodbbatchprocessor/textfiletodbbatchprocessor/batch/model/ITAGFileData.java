package com.hdworks.java.spring.poc.textfiletodbbatchprocessor.textfiletodbbatchprocessor.batch.model;

import lombok.Getter;
import lombok.Setter;


public record ITAGFileData(String TAG_AGENCY_ID, String TAG_SERIAL_NUMBER, String TAG_STATUS, String TAG_ACCT_INFO) {
}
