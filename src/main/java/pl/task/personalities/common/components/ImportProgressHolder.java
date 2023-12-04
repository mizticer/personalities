package pl.task.personalities.common.components;

import org.springframework.stereotype.Component;
import pl.task.personalities.model.ImportProgress;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ImportProgressHolder {
    private final Map<String, ImportProgress> importProgressMap = new ConcurrentHashMap<>();

    public ImportProgress createImportProgress() {
        String taskId = UUID.randomUUID().toString();
        ImportProgress importProgress = new ImportProgress(taskId);
        importProgressMap.put(taskId, importProgress);
        return importProgress;
    }

    public ImportProgress getImportProgress(String taskId) {
        return importProgressMap.get(taskId);
    }

}
