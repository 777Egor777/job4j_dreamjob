package ru.job4j.dream.store;

import net.jcip.annotations.ThreadSafe;
import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.Post;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Singleton store
 *
 * @author Geraskin Egor
 * @version 1.0
 * @since 09.01.2021
 */
@ThreadSafe
public final class MemStore implements Store {
    private final static MemStore INSTANCE = new MemStore();
    private final Map<Integer, Post> posts = new ConcurrentHashMap<>();
    private final Map<Integer, Candidate> candidates = new ConcurrentHashMap<>();
    private static final AtomicInteger POST_ID = new AtomicInteger(4);
    private static final AtomicInteger CANDIDATE_ID = new AtomicInteger(11);

    private MemStore() {
        posts.put(1, new Post(1, "Junior Java Job"));
        posts.put(2, new Post(2, "Middle Java Job"));
        posts.put(3, new Post(3, "Senior Java Job"));
        candidates.put(1, new Candidate(1, "Junior Java"));
        candidates.put(2, new Candidate(2, "Middle Java"));
        candidates.put(3, new Candidate(3, "Senior Java"));
    }

    public static Store instOf() {
        return INSTANCE;
    }

    @Override
    public Collection<Post> findAllPosts() {
        return posts.values();
    }

    @Override
    public Collection<Candidate> findAllCandidates() {
        return candidates.values();
    }

    @Override
    public Post save(Post post) {
        if (post.getId() == 0) {
            post = post.setId(POST_ID.incrementAndGet());
        }
        posts.put(post.getId(), post);
        return post;
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
    public Post findPostById(int id) {
        return posts.get(id);
    }

    @Override
    public Candidate findCandidateById(int id) {
        return candidates.get(id);
    }

    @Override
    public void clear() {

    }
}
