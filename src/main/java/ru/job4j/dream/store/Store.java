package ru.job4j.dream.store;

import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.Item;
import ru.job4j.dream.model.Post;

import java.util.Collection;

/**
 * @author Geraskin Egor
 * @version 1.0
 * @since 11.01.2021
 */
public interface Store {
    Collection<Post> findAllPosts();
    Collection<Candidate> findAllCandidates();
    Post save(Post post);
    Candidate save(Candidate candidate);
    Post findPostById(int id);
    Candidate findCandidateById(int id);
    void clear();
}
