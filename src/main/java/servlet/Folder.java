package servlet;

import beans.File;
import beans.SubFolder;
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
import java.sql.SQLException;
import java.util.List;

@WebServlet("/folderView")

public class Folder extends HttpServlet {

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
        int subFolderId = Integer.parseInt(StringEscapeUtils.escapeJava(request.getParameter("subFolderId")));
        User user = (User) request.getSession().getAttribute("user");

        List<File> files = sqlQueryHandler.getSubFolderFiles(subFolderId, user.getId());

        if (files == null)
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);

        else {
            SubFolder subFolder = new SubFolder();
            subFolder.setName(request.getParameter("subFolderName"));

            final WebContext webContext = new WebContext(request, response, servletContext, request.getLocale());
            webContext.setVariable("subFolder", subFolder);
            webContext.setVariable("files", files);
            templateEngine.process("/folderView.html", webContext, response.getWriter());
        }
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
