package com.hdworks.java.spring.poc.textfiletodbbatchprocessor.textfiletodbbatchprocessor.batch.config;

import com.hdworks.java.spring.poc.textfiletodbbatchprocessor.textfiletodbbatchprocessor.batch.model.ITAGEntity;
import com.hdworks.java.spring.poc.textfiletodbbatchprocessor.textfiletodbbatchprocessor.batch.model.ITAGFileData;
import com.hdworks.java.spring.poc.textfiletodbbatchprocessor.textfiletodbbatchprocessor.batch.processor.ITAGFileToEntityProcessor;
import com.hdworks.java.spring.poc.textfiletodbbatchprocessor.textfiletodbbatchprocessor.batch.reader.ITAGFileReader;
import com.hdworks.java.spring.poc.textfiletodbbatchprocessor.textfiletodbbatchprocessor.batch.writer.repository.ITAGRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class ITAGBatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final ITAGRepository repository;

    @Bean
    public ITAGFileReader itagFileReader() {
        return new ITAGFileReader(new ClassPathResource("008_20240414232106.ITAG"));
    }

    @Bean
    public ITAGFileToEntityProcessor processor() {
        return new ITAGFileToEntityProcessor();
    }

    @Bean
    public RepositoryItemWriter<ITAGEntity> writer() {
        RepositoryItemWriter<ITAGEntity> writer = new RepositoryItemWriter<>();
        writer.setRepository(repository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
        asyncTaskExecutor.setConcurrencyLimit(10);
        return asyncTaskExecutor;
    }
    @Bean
    public Step slaveStep() {
        return  new StepBuilder("slaveStep",jobRepository)
                .<ITAGFileData, ITAGEntity>chunk(10000,platformTransactionManager) // Smaller chunks might be better for performance tuning
                .reader(itagFileReader()) // Ensure your reader is partition-aware and can read a specific range
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean
    public Step masterStep() {
        return new StepBuilder("masterStep", jobRepository)
                .partitioner(slaveStep().getName(), new FileRangePartitioner(new ClassPathResource("008_20240414232106.ITAG"), 25))
                .step(slaveStep())
                .gridSize(10)
                .taskExecutor(taskExecutor())
                .build();
    }


    @Bean
    public Step itagFileIngestionsStep() {
        return new StepBuilder("itag-file-ingestion-step", jobRepository)
                .<ITAGFileData, ITAGEntity>chunk(10000, platformTransactionManager)
                .reader(itagFileReader())
                .processor(processor())
                .writer(writer())
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public Job runJob() {
        return new JobBuilder("itagFileIngestionJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(itagFileIngestionsStep())
                .build();
    }
}
