package com.hdworks.java.spring.poc.textfiletodbbatchprocessor.textfiletodbbatchprocessor.batch.writer.repository;

import com.hdworks.java.spring.poc.textfiletodbbatchprocessor.textfiletodbbatchprocessor.batch.model.ITAGEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITAGRepository extends JpaRepository<ITAGEntity, Integer> {
}
