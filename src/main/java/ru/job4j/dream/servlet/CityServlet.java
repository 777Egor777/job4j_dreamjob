package ru.job4j.dream.servlet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ru.job4j.dream.store.MemCityStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Сервлет выполняет одну задачу -
 * передаёт список всех городов,
 * преобразованный в формат JSON
 * в качестве ответа на запрос.
 *
 * @author Egor Geraskin(yegeraskin13@gmail.com)
 * @version 1.0
 * @since 21.01.2021
 */
public class CityServlet extends HttpServlet {
    /**
     * Передача списка всех
     * городов в формате
     * JSON в качестве ответа.
     *
     * @param req - объект запроса(HttpServletRequest)
     * @param resp - объект ответа(HttpServletResponse)
     *               В его OutputStream и записывается
     *               список городов в формате JSON.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        JSONObject obj = new JSONObject();
        JSONArray arr = new JSONArray();
        for (String city : MemCityStore.instOf().getAll()) {
            JSONObject cityJSON = new JSONObject();
            cityJSON.put("city", city);
            arr.add(cityJSON);
        }
        obj.put("items", arr);
        writer.print(obj.toJSONString());
        writer.flush();
    }
}
