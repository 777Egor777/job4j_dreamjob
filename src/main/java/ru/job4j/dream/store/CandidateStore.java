package ru.job4j.dream.store;

import ru.job4j.dream.model.Candidate;

import java.util.Collection;

public interface CandidateStore {
    Collection<Candidate> findAll();
    Candidate save(Candidate candidate);
    Candidate findById(int id);
    void clear();
}
