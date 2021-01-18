package ru.job4j.dream.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Egor Geraskin(yegeraskin13@gmail.com)
 * @version 1.0
 * @since 18.01.2021
 */
public class AuthFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        String uri = req.getRequestURI();
        if (uri.endsWith("auth.do")) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else if (req.getSession().getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/auth.do");
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {

    }
}
