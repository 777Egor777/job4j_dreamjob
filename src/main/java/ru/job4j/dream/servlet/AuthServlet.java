package ru.job4j.dream.servlet;

import ru.job4j.dream.model.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author Egor Geraskin(yegeraskin13@gmail.com)
 * @version 1.0
 * @since 18.01.2021
 */
public class AuthServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        if (email.equals("root@local") && password.equals("root")) {
            HttpSession session = req.getSession();
            User admin = new User();
            admin = admin.setName("Admin");
            admin = admin.setEmail(email);
            admin = admin.setPassword(password);
            session.setAttribute("user", admin);
            resp.sendRedirect(req.getContextPath() + "/posts.do");
        } else {
            req.setAttribute("error", "Incorrect auth data");
            req.getRequestDispatcher("auth.jsp").forward(req, resp);
        }
    }
}
