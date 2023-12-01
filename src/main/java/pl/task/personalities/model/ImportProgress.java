package pl.task.personalities.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImportProgress {
    private String id = UUID.randomUUID().toString();
    private int processedRows = 0;
    private boolean finished = false;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public ImportProgress(String id) {
        this.id = id;
    }

    public void incrementProcessedRows() {
        this.processedRows += 1;
    }
}
