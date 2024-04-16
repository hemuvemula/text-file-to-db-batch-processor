package com.hdworks.java.spring.poc.textfiletodbbatchprocessor.textfiletodbbatchprocessor.batch.config;

import com.hdworks.java.spring.poc.textfiletodbbatchprocessor.textfiletodbbatchprocessor.batch.model.ITAGEntity;
import com.hdworks.java.spring.poc.textfiletodbbatchprocessor.textfiletodbbatchprocessor.batch.model.ITAGFileData;
import com.hdworks.java.spring.poc.textfiletodbbatchprocessor.textfiletodbbatchprocessor.batch.processor.ITAGFileToEntityProcessor;
import com.hdworks.java.spring.poc.textfiletodbbatchprocessor.textfiletodbbatchprocessor.batch.reader.ITAGFileReader;
import com.hdworks.java.spring.poc.textfiletodbbatchprocessor.textfiletodbbatchprocessor.batch.writer.repository.ITAGRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.*;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ITAGBatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final ITAGRepository repository;

    @Bean
    public ItemReader<ITAGFileData> reader() {
        return new ITAGFileReader().reader();
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
    public TaskExecutor concurrentTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);  // Set the number of threads
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(500);
        executor.initialize();
        return executor;
    }




    public ItemProcessor<ITAGFileData, ITAGFileData> itagValidationsProcessor() {

        return null;
    }

    @Bean
    public Tasklet concurrentProcessingTasklet(){
        return (contribution, chunkContext) -> {
            StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();
            JobExecution jobExecution = stepExecution.getJobExecution();
            ExecutionContext executionContext = jobExecution.getExecutionContext();
            List<ITAGFileData> fileData = (List<ITAGFileData>) executionContext.get("validatedData");

            List<ITAGEntity> processedData = new ArrayList<>();
            for (ITAGFileData item : fileData) {
                ITAGEntity processedItem = processor().process(item);
                if (processedItem != null) {
                    processedData.add(processedItem);
                }
            }
            // Optionally store processed data back into the ExecutionContext if further processing is needed
            executionContext.put("validatedData", processedData);
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step validationStep() {
        return new StepBuilder("itag-file-validation-step", jobRepository)
                .<ITAGFileData, ITAGFileData>chunk(10000, platformTransactionManager)
                .reader(reader())
                .processor(itagValidationsProcessor())
                .writer(chunk -> {
                    ExecutionContext executionContext = StepSynchronizationManager.getContext().getStepExecution().getExecutionContext();
                    executionContext.put("validatedData", chunk);
                })
                .taskExecutor(taskExecutor())
                .build();

    }

    @Bean
    public Step processingStep() {
        return new StepBuilder("itag-file-processing-step",jobRepository)
                .tasklet(concurrentProcessingTasklet(),platformTransactionManager)
                .build();
    }
    @Bean
    public Step itagFileDBIngestionsStep() {
        return new StepBuilder("itag-file-db-ingestion-step", jobRepository)
                .<ITAGFileData, ITAGEntity>chunk(10000, platformTransactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .taskExecutor(taskExecutor())
                .build();
    }


    @Bean
    public Job runJob() {
        return new JobBuilder("itagFileIngestionJob", jobRepository)
                .start(validationStep())
                .next(processingStep())
                .next(itagFileDBIngestionsStep())
                .build();

    }
}
