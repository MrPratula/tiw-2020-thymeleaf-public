package servlet;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/logOut")
public class LogOut extends HttpServlet {

    private static final long serialVersionUID = 1L;

    public void init() {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        ServletContext servletContext = getServletContext();

        HttpSession session = request.getSession();
        session.removeAttribute("user");
        session.invalidate();

        response.sendRedirect(servletContext.getContextPath()+"/index.html");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doGet (request, response);
    }

    public void destroy() {

    }
}
