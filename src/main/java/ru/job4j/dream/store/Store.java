package ru.job4j.dream.store;

import ru.job4j.dream.model.Post;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton store
 *
 * @author Geraskin Egor
 * @version 1.0
 * @since 09.01.2021
 */
public final class Store {
    private final static Store INSTANCE = new Store();
    private final Map<Integer, Post> posts = new ConcurrentHashMap<>();

    private Store() {
        posts.put(1, new Post(1, "Junior Java Job"));
        posts.put(2, new Post(2, "Middle Java Job"));
        posts.put(3, new Post(3, "Senior Java Job"));
    }

    public static Store instOf() {
        return INSTANCE;
    }

    public Collection<Post> findAll() {
        return posts.values();
    }
}
