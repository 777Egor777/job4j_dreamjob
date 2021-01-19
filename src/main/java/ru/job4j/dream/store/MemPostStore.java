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
    private final Map<Integer, Post> posts = new ConcurrentHashMap<>();
    private static final AtomicInteger POST_ID = new AtomicInteger(4);

    private static final class Holder {
        private final static PostStore INSTANCE = new MemPostStore();
    }

    private MemPostStore() {
        posts.put(1, new Post(1, "Junior Java Job"));
        posts.put(2, new Post(2, "Middle Java Job"));
        posts.put(3, new Post(3, "Senior Java Job"));
    }

    public static PostStore instOf() {
        return Holder.INSTANCE;
    }

    @Override
    public Collection<Post> findAll() {
        return posts.values();
    }

    @Override
    public Post save(Post post) {
        if (post.getId() == 0) {
            post = post.setId(POST_ID.getAndIncrement());
        }
        posts.put(post.getId(), post);
        return post;
    }

    @Override
    public Post findById(int id) {
        return posts.get(id);
    }

    @Override
    public Post findByName(String name) {
        Post result = null;
        for (Post post : posts.values()) {
            if (post.getName().equals(name)) {
                result = post;
                break;
            }
        }
        return  result;
    }

    @Override
    public void clear() {

    }
}
