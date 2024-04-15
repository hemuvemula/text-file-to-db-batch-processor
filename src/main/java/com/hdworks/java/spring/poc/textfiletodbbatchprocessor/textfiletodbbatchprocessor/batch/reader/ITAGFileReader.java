package com.hdworks.java.spring.poc.textfiletodbbatchprocessor.textfiletodbbatchprocessor.batch.reader;

import com.hdworks.java.spring.poc.textfiletodbbatchprocessor.textfiletodbbatchprocessor.batch.mapper.ITAGFileDataMapper;
import com.hdworks.java.spring.poc.textfiletodbbatchprocessor.textfiletodbbatchprocessor.batch.model.ITAGFileData;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DefaultFieldSet;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.core.io.ClassPathResource;

public class ITAGFileReader {

    public ItemReader<ITAGFileData> reader() {
        FlatFileItemReader<ITAGFileData> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("008_20240414232106.ITAG"));
        reader.setLineMapper(lineMapper());
        reader.setLinesToSkip(1);
        return reader;
    }

    public LineMapper<ITAGFileData> lineMapper() {
        DefaultLineMapper<ITAGFileData> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(fixedLengthTokenizer());
        lineMapper.setFieldSetMapper(fieldSetMapper());
        return lineMapper;
    }

    public FixedLengthTokenizer fixedLengthTokenizer() {
        FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
        tokenizer.setNames("TAG_AGENCY_ID", "TAG_SERIAL_NUMBER", "TAG_STATUS", "TAG_ACCT_INFO");
        tokenizer.setColumns(new Range(1,3), new Range(4,11), new Range(12,12), new Range(13,18));
        return tokenizer;
    }

    public FieldSetMapper<ITAGFileData> fieldSetMapper() {
        return fieldSet -> new ITAGFileData(
                fieldSet.readString("TAG_AGENCY_ID"),
                fieldSet.readString("TAG_SERIAL_NUMBER"),
                fieldSet.readString("TAG_STATUS"),
                fieldSet.readString("TAG_ACCT_INFO")
        );
    }

}
