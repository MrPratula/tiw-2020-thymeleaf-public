package servlet;

import beans.Folder;
import beans.User;
import beans.File;
import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

@WebServlet("/fileMove")

public class ChooseLocation extends HttpServlet {

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

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        int fileId = Integer.parseInt(StringEscapeUtils.escapeJava(request.getParameter("fileId")));

        List<Folder> folders = sqlQueryHandler.getFolders(user.getId());
        File file = sqlQueryHandler.getFile(fileId, user.getId());

        if (file==null){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        final WebContext webContext = new WebContext(request, response, servletContext, request.getLocale());
        webContext.setVariable("folders", folders);
        session.setAttribute("file", file);

        templateEngine.process("/fileMove.html", webContext, response.getWriter());

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doGet (request, response);
    }

    public void destroy() {

    }
}
