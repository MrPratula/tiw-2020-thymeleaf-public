package servlet;


import beans.User;
import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;

@WebServlet("/fileView")

public class File extends HttpServlet {

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
        int fileId = Integer.parseInt(StringEscapeUtils.escapeJava(request.getParameter("fileId")));
        User sessionUser = (User) request.getSession().getAttribute("user");

        beans.File file = sqlQueryHandler.getFile(fileId, sessionUser.getId());

        if (file == null)
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error getting file in File.java");

        else {
            final WebContext webContext = new WebContext(request, response, servletContext, request.getLocale());
            webContext.setVariable("file", file);
            templateEngine.process("/fileView.html", webContext, response.getWriter());
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doGet (request, response);
    }

    public void destroy() {

    }
}
