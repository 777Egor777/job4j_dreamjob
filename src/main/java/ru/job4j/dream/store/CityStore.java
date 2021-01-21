package ru.job4j.dream.store;

import java.util.List;

public interface CityStore {
    List<String> getAll();
    int getIdByName(String name);
    String getNameById(int id);
}
