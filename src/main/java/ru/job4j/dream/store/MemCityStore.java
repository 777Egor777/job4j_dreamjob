package ru.job4j.dream.store;

import java.util.ArrayList;
import java.util.List;

/**
 * Хранилище данных для городов,
 * использующее оперативную память
 * для хранения.
 *
 * Класс является Синглтоном - во время
 * работы приложения будет создан и будет
 * использоваться всего один
 * его объект.
 *
 * Синглтон инициализируется лениво,
 * используется шаблон "Holder"
 *
 * @author Egor Geraskin(yegeraskin13@gmail.com)
 * @version 1.0
 * @since 21.01.2021
 */
public class MemCityStore implements CityStore {
    /**
     * Список всех городов.
     */
    private final List<String> items = new ArrayList<>();

    /**
     * В классе только один приватный
     * конструктор - так как это
     * синглтон.
     *
     * В конструкторе добавляются
     * все необходимые города
     * в список.
     */
    private MemCityStore() {
        items.add("Homeless");
        items.add("Saratov");
        items.add("Moscow");
        items.add("Spb");
        items.add("Tokio");
        items.add("London");
        items.add("NewYork");
    }

    /**
     * Класс для ленивой загрузки
     * объекта внешнего класса-
     * синглтона.
     *
     * Применяется шаблон "Holder".
     */
    private final static class Holder {
        private final static CityStore INSTANCE = new MemCityStore();
    }

    /**
     * Метод возвращает
     * объект синглтона
     * @return - объект синглтона
     */
    public static CityStore instOf() {
        return Holder.INSTANCE;
    }

    /**
     * Вернуть список всех городов,
     * находящихся в хранилище.
     * @return список всех городов
     *         из хранилища
     */
    @Override
    public List<String> getAll() {
        return items;
    }

    /**
     * Вернуть идентификатор города
     * с конкретным именем
     * @param name - имя города
     * @return идентификатор города
     */
    @Override
    public int getIdByName(String name) {
        return items.indexOf(name);
    }

    /**
     * Вернуть имя города, с
     * конкретным индентификатором
     * @param id - идентификатор города
     * @return - имя города
     */
    @Override
    public String getNameById(int id) {
        return (id >= 0 && id < items.size()) ? items.get(id) : null;
    }
}
