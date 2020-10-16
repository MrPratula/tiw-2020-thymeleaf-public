package servlet;

import beans.File;
import beans.SubFolder;
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
import java.util.List;


@WebServlet("/MoveFile")

public class MoveFile extends HttpServlet {

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

        int destination;

        // if npe is catch user refresh the page after move a file
        try {
            destination = sqlQueryHandler.moveFile(request);
        } catch (Exception e){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        List<File> files;
        SubFolder subFolder = new SubFolder();
        User user = (User) request.getSession().getAttribute("user");

        try {
            files = sqlQueryHandler.getSubFolderFiles(destination, user.getId());
            subFolder.setName(sqlQueryHandler.getSubFolderName(destination, user.getId()));
        } catch (Exception e){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        final WebContext webContext = new WebContext(request, response, servletContext, request.getLocale());

        webContext.setVariable("subFolder", subFolder);
        webContext.setVariable("files", files);
        templateEngine.process("/folderView.html", webContext, response.getWriter());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doGet (request, response);
    }

    public void destroy() {

    }
}
