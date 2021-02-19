package ru.job4j.dream.store;

import java.util.List;

/**
 * Интерфейс, описывающий
 * хранилище данных для
 * городов.
 *
 * @author Egor Geraskin(yegeraskin13@gmail.com)
 * @version 1.0
 * @since 17.01.2021
 */
public interface CityStore {
    /**
     * Вернуть список всех городов,
     * находящихся в хранилище.
     * @return список всех городов
     *         из хранилища
     */
    List<String> getAll();

    /**
     * Вернуть идентификатор города
     * с конкретным именем
     * @param name - имя города
     * @return идентификатор города
     */
    int getIdByName(String name);

    /**
     * Вернуть имя города, с
     * конкретным индентификатором
     * @param id - идентификатор города
     * @return - имя города
     */
    String getNameById(int id);
}
