package ru.job4j.dream.store;

import ru.job4j.dream.model.Candidate;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Egor Geraskin(yegeraskin13@gmail.com)
 * @version 1.0
 * @since 16.01.2021
 */
public class MemCandidateStore implements CandidateStore {
    private final static MemCandidateStore INSTANCE = new MemCandidateStore();
    private final Map<Integer, Candidate> candidates = new ConcurrentHashMap<>();
    private static final AtomicInteger CANDIDATE_ID = new AtomicInteger(11);

    private MemCandidateStore() {
        candidates.put(1, new Candidate(1, "Junior Java", 0));
        candidates.put(2, new Candidate(2, "Middle Java", 0));
        candidates.put(3, new Candidate(3, "Senior Java", 0));
    }

    public static CandidateStore instOf() {
        return INSTANCE;
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidates.values();
    }

    @Override
    public Candidate save(Candidate candidate) {
        if (candidate.getId() == 0) {
            candidate = candidate.setId(CANDIDATE_ID.incrementAndGet());
        }
        candidates.put(candidate.getId(), candidate);
        return candidate;
    }

    @Override
    public Candidate findById(int id) {
        return candidates.get(id);
    }

    @Override
    public void clear() {

    }
}
