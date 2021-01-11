package ru.job4j.dream.model;

import net.jcip.annotations.Immutable;

/**
 * @author Geraskin Egor
 * @version 1.0
 * @since 11.01.2021
 */
@Immutable
public class Item {
    private final int id;
    private final String name;

    public Item(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Item setId(int id) {
        return new Item(id, name);
    }

    public Item setName(String name) {
        return new Item(id, name);
    }
}
