package com.hdworks.java.spring.poc.textfiletodbbatchprocessor.textfiletodbbatchprocessor.batch.processor;

import com.hdworks.java.spring.poc.textfiletodbbatchprocessor.textfiletodbbatchprocessor.batch.model.ITAGEntity;
import com.hdworks.java.spring.poc.textfiletodbbatchprocessor.textfiletodbbatchprocessor.batch.model.ITAGFileData;
import org.springframework.batch.item.ItemProcessor;

public class ITAGFileToEntityProcessor implements ItemProcessor<ITAGFileData, ITAGEntity> {
    @Override
    public ITAGEntity process(ITAGFileData fileRecord) {
        ITAGEntity itagEntity = new ITAGEntity();
        itagEntity.setTAG_AGENCY_ID(fileRecord.TAG_AGENCY_ID());
        itagEntity.setTAG_STATUS(fileRecord.TAG_STATUS());
        itagEntity.setTAG_SERIAL_NUMBER(fileRecord.TAG_SERIAL_NUMBER());
        itagEntity.setTAG_ACCT_INFO(fileRecord.TAG_ACCT_INFO());
        return itagEntity;
    }
}
