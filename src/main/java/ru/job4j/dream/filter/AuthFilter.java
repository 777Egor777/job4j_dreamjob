package ru.job4j.dream.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Egor Geraskin(yegeraskin13@gmail.com)
 * @version 1.0
 * @since 18.01.2021
 */
public class AuthFilter implements Filter {

    private final List<String> acceptablePageEndings;

    public AuthFilter() {
        acceptablePageEndings = List.of(
                "reg.do",
                "auth.do"
        );
    }

    private boolean isAcceptableUri(String uri) {
        boolean result = false;
        for (String ending : acceptablePageEndings) {
            if (uri.endsWith(ending)) {
                result = true;
                break;
            }
        }
        return result;
    }


    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        String uri = req.getRequestURI();
        System.out.println("!!" + uri + "!!");
        System.out.println(uri.endsWith("reg.do"));
        if (isAcceptableUri(uri)) {
            System.out.println("!!!");
            filterChain.doFilter(servletRequest, servletResponse);
            System.out.println("!!!!");
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
