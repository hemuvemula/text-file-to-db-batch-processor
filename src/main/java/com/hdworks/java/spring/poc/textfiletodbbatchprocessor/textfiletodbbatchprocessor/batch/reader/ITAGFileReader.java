package com.hdworks.java.spring.poc.textfiletodbbatchprocessor.textfiletodbbatchprocessor.batch.reader;

import com.hdworks.java.spring.poc.textfiletodbbatchprocessor.textfiletodbbatchprocessor.batch.model.ITAGFileData;
import com.hdworks.java.spring.poc.textfiletodbbatchprocessor.textfiletodbbatchprocessor.batch.validator.TagStatusFileValidator;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DefaultFieldSet;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.core.io.Resource;
import org.springframework.validation.BindException;

public class ITAGFileReader extends FlatFileItemReader<ITAGFileData> {

    public ITAGFileReader(Resource resource) {
        setResource(resource);
        setLinesToSkip(1); // Skip the header line
        setSkippedLinesCallback(TagStatusFileValidator::validateHeader); // Handle the header line if needed
        setLineMapper(new ITAGLineMapper());
    }

    private static class ITAGLineMapper implements LineMapper<ITAGFileData> {
        @Override
        public ITAGFileData mapLine(String line, int lineNumber) throws Exception {

            // Process detail lines
            if (TagStatusFileValidator.validateDetail(line)) {
                String tagAgencyId = line.substring(0, 3);
                String tagSerialNumber = line.substring(3, 11);
                String tagStatus = line.substring(11, 12);
                String tagAcctInfo = line.substring(12, 18);
                return new ITAGFileData(tagAgencyId, tagSerialNumber, tagStatus, tagAcctInfo);
            } else {
                throw new RuntimeException("Detail validation failed");
            }

        }
    }
}
