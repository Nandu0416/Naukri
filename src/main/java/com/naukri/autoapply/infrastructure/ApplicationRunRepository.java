package com.naukri.autoapply.infrastructure;

import com.naukri.autoapply.domain.ApplicationRun;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ApplicationRunRepository {
    private final Map<UUID, ApplicationRun> storage = new ConcurrentHashMap<>();

    public ApplicationRun save(ApplicationRun run) {
        storage.put(run.getId(), run);
        return run;
    }

    public Optional<ApplicationRun> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }
}
