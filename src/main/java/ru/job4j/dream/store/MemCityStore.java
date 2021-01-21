package ru.job4j.dream.store;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Egor Geraskin(yegeraskin13@gmail.com)
 * @version 1.0
 * @since 21.01.2021
 */
public class MemCityStore implements CityStore {
    private final List<String> items = new ArrayList<>();

    private MemCityStore() {
        items.add("Homeless");
        items.add("Saratov");
        items.add("Moscow");
        items.add("Spb");
        items.add("Tokio");
        items.add("London");
        items.add("NewYork");
    }

    private final static class Holder {
        private final static CityStore INSTANCE = new MemCityStore();
    }

    public static CityStore instOf() {
        return Holder.INSTANCE;
    }

    @Override
    public List<String> getAll() {
        return items;
    }

    @Override
    public int getIdByName(String name) {
        return items.indexOf(name);
    }

    @Override
    public String getNameById(int id) {
        return (id >= 0 && id < items.size()) ? items.get(id) : null;
    }
}
