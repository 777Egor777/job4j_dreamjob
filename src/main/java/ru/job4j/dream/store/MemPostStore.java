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
public final class MemPostStore implements PostStore {
    private final static MemPostStore INSTANCE = new MemPostStore();
    private final Map<Integer, Post> posts = new ConcurrentHashMap<>();
    private static final AtomicInteger POST_ID = new AtomicInteger(4);

    private MemPostStore() {
        posts.put(1, new Post(1, "Junior Java Job"));
        posts.put(2, new Post(2, "Middle Java Job"));
        posts.put(3, new Post(3, "Senior Java Job"));
    }

    public static PostStore instOf() {
        return INSTANCE;
    }

    @Override
    public Collection<Post> findAll() {
        return posts.values();
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
    public Post findById(int id) {
        return posts.get(id);
    }

    @Override
    public void clear() {

    }
}
