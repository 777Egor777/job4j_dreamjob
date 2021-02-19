package ru.job4j.dream.store;

import ru.job4j.dream.model.Post;

/**
 * @author Geraskin Egor
 * @version 1.0
 * @since 11.01.2021
 */
public class PsqlMain {
    private final static PostStore STORE = PsqlPostStore.instOf();

    private static void init() {
        STORE.save(new Post(0, "Java Trainee Job"));
        STORE.save(new Post(0, "Java Junior Job"));
        STORE.save(new Post(0, "Java Middle Job"));
        STORE.save(new Post(0, "Java Senior Job"));
        STORE.save(new Post(0, "Java Lead Job"));
        STORE.save(new Post(0, "Java Architecture Job"));
    }

    private static void clear() {
        STORE.clear();
    }

    public static void main(String[] args) {
        //clear();
        init();
        //findAllPosts();
//        findPostById();
    }

    public static void findAllPosts() {
        for (Post post: STORE.findAll()) {
            System.out.println(post.getId() + " " + post.getName());
        }
    }

    public static void findPostById() {
        int id = 16;//Java Middle Job
        System.out.println(STORE.findById(id));
    }
}
