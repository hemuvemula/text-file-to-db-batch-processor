package com.hdworks.java.spring.poc.textfiletodbbatchprocessor.textfiletodbbatchprocessor.batch.config;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class FileRangePartitioner implements Partitioner {

    private Resource inputFile;
    private int partitions;

    public FileRangePartitioner(Resource inputFile, int partitions) {
        this.inputFile = inputFile;
        this.partitions = partitions;
    }

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> result = new HashMap<>();
        // Example logic to partition file by lines
        // Adjust based on how you can split your file, e.g., by lines, bytes, etc.
        try (Stream<String> lines = Files.lines(inputFile.getFile().toPath())) {
            long totalLines = lines.count();
            long linesPerPartition = totalLines / partitions;
            long remainder = totalLines % partitions;

            for (int i = 0; i < partitions; i++) {
                ExecutionContext context = new ExecutionContext();
                context.putLong("minValue", i * linesPerPartition + Math.min(i, remainder));
                context.putLong("maxValue", (i + 1) * linesPerPartition + Math.min(i + 1, remainder) - 1);
                result.put("partition" + i, context);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error when partitioning", e);
        }
        return result;
    }
}

