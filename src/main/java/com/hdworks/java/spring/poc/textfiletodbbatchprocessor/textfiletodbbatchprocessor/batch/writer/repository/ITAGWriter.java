package com.hdworks.java.spring.poc.textfiletodbbatchprocessor.textfiletodbbatchprocessor.batch.writer.repository;

import com.hdworks.java.spring.poc.textfiletodbbatchprocessor.textfiletodbbatchprocessor.batch.model.ITAGEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

@RequiredArgsConstructor
public class ITAGWriter implements ItemWriter<ITAGEntity> {

    private ITAGRepository repository;

    @Override
    public void write(Chunk<? extends ITAGEntity> chunk) throws Exception {
        repository.saveAll(chunk);
    }
}
