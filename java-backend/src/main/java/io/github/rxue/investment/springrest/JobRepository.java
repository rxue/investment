package io.github.rxue.investment.springrest;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Service
class JobRepository {
    private static Map<Long,Job> jobs = new ConcurrentHashMap<>();

    public Job save(Job job) {
        jobs.put(job.id(), job);
        return job;
    }
    public Job findById(Long id) {
        return jobs.get(id);
    }
}
