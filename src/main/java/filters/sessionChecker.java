package filters;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(filterName = "sessionChecker")
public class sessionChecker implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest servletRequest = (HttpServletRequest) request;
        HttpServletResponse servletResponse = (HttpServletResponse) response;
        String loginPath = servletRequest.getServletContext().getContextPath() + "/index.html";

        HttpSession session = servletRequest.getSession();

        if (session.isNew() || session.getAttribute("user") == null) {
            servletResponse.sendRedirect(loginPath);
            return;
        }

        filterChain.doFilter(request, response);

    }

    @Override
    public void destroy() {

    }
}
