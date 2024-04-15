package com.hdworks.java.spring.poc.textfiletodbbatchprocessor.textfiletodbbatchprocessor.batch.mapper;

import com.hdworks.java.spring.poc.textfiletodbbatchprocessor.textfiletodbbatchprocessor.batch.model.ITAGFileData;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class ITAGFileDataMapper implements FieldSetMapper<ITAGFileData> {

    @Override
    public ITAGFileData mapFieldSet(FieldSet fieldSet) throws BindException {
        if (fieldSet == null) {
            return null;
        }
        return new ITAGFileData(
                fieldSet.readString("TAG_AGENCY_ID"),
                fieldSet.readString("TAG_SERIAL_NUMBER"),
                fieldSet.readString("TAG_STATUS"),
                fieldSet.readString("TAG_ACCT_INFO")
        );
    }
}