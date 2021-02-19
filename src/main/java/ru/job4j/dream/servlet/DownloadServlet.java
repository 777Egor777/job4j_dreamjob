package ru.job4j.dream.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Сервлет выполняет единственную
 * задачу - получает в качестве
 * параметра запроса имя файла,
 * и записывает файл с таким
 * именем в поток вывода
 * ответа HttpServletResponse.
 *
 * @author Egor Geraskin(yegeraskin13@gmail.com)
 * @version 1.0
 * @since 17.01.2021
 */
public class DownloadServlet extends HttpServlet {
    /**
     * Выводит в объект ответа запрашиваемый
     * файл.
     *
     * @param req - объект запроса(HttpServletRequest)
     *              Содержит имя файла
     * @param resp - объект ответа(HttpServletResponse).
     *               В его поток вывода выводится
     *               запрашиваемый файл
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        resp.setContentType("name" + name);
        resp.setContentType("image/png");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + name + "\"");
        File file = new File("images" + File.separator + name);
        try (FileInputStream input = new FileInputStream(file)) {
            resp.getOutputStream().write(input.readAllBytes());
        }
    }
}
