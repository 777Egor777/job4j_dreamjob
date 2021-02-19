package ru.job4j.dream.servlet;

import ru.job4j.dream.model.User;
import ru.job4j.dream.store.PsqlUserStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Сервлет для страницы авторизации
 * пользователей.
 *
 * Поддерживает валидацию, если
 * введены неверные данные, пользователь
 * будет переведён на страницу
 * с соответствующим сообщением ошибки.
 *
 * @author Egor Geraskin(yegeraskin13@gmail.com)
 * @version 1.0
 * @since 18.01.2021
 */
public class AuthServlet extends HttpServlet {
    /**
     * Переопределяет метод doGet
     * класса HttpServlet.
     *
     * Перенаправляет запрос на JSP
     * @param req - объект запроса(HttpServletRequest)
     * @param resp - объект ответа(HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("auth.jsp").forward(req, resp);
    }

    /**
     * Переопределяет метод doPost
     * класса HttpServlet
     *
     * Получает данные пользователя
     * с формы JSP.
     * Валидирует их.
     * Добавляет объект пользователя
     * в сессию.
     *
     * @param req - объект запроса(HttpServletRequest)
     * @param resp - объект ответа(HttpServletResponse)
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        User user = PsqlUserStore.instOf().findByEmail(email);

        if (user.getId() == -1) {
            req.getRequestDispatcher("auth_no_such_email.jsp").forward(req, resp);
        } else
        if (user.getPassword().equals(password)) {
            HttpSession session = req.getSession();
            session.setAttribute("user", user);
            resp.sendRedirect(req.getContextPath() + "/index.do");
        } else {
            req.getRequestDispatcher("auth_incorrect_password.jsp").forward(req, resp);
        }
    }
}
