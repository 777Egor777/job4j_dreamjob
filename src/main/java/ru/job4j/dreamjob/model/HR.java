package ru.job4j.dreamjob.model;

import net.jcip.annotations.Immutable;

/**
 * @author Geraskin Egor
 * @version 1.0
 * @since 05.01.2021
 */
@Immutable
public final class HR {
    private final int id;
    private final String company;
    private final String name;

    public HR(int id, String company, String name) {
        this.id = id;
        this.company = company;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getCompany() {
        return company;
    }

    public String getName() {
        return name;
    }

    public HR setId(int id) {
        return new HR(id, company, name);
    }

    public HR setName(String name) {
        return new HR(id, company, name);
    }

    public HR setCompany(String company) {
        return new HR(id, company, name);
    }
}
