package ru.job4j.dream.servlet;

import ru.job4j.dream.model.User;
import ru.job4j.dream.store.PsqlCandidateStore;
import ru.job4j.dream.store.PsqlPostStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * Сервлет для загрузки
 * данных на JSP (index.jsp)
 *
 * @author Geraskin Egor
 * @version 1.0
 * @since 11.01.2021
 */
public class IndexServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("posts", PsqlPostStore.instOf().findAllByToday());
        req.setAttribute("candidates", PsqlCandidateStore.instOf().findAllByToday());
        req.setAttribute("user", req.getSession().getAttribute("user"));
        req.getRequestDispatcher("index.jsp").forward(req, resp);
    }
}
