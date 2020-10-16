package servlet;

import beans.Folder;
import beans.User;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/goToHome")

public class GoToHome extends HttpServlet {

    private Connection connection;
    private static final long serialVersionUID = 1L;
    private TemplateEngine templateEngine;

    public void init() {
        ServletContext servletContext = getServletContext();
        connection = new SqlConnectionHandler(servletContext).connect();
        templateEngine = new TemplateEngineHandler().setUp(servletContext);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        ServletContext servletContext = getServletContext();
        SqlQueryHandler sqlQueryHandler = new SqlQueryHandler(connection);
        User user = (User) request.getSession().getAttribute("user");

        // put user folder into web context
        List<Folder> folders = sqlQueryHandler.getFolders(user.getId());

        final WebContext webContext = new WebContext(request, response, servletContext, request.getLocale());
        webContext.setVariable("folders", folders);
        templateEngine.process("/home.html", webContext, response.getWriter());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doGet (request, response);
    }

    public void destroy() {
        try {
            if (connection != null)
                connection.close();
        } catch (SQLException ignore){}
    }
}
