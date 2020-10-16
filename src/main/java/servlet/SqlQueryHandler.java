package servlet;

import beans.File;
import beans.Folder;
import beans.User;
import dao.FileDao;
import dao.FoldersDao;
import dao.SubFolderDao;
import dao.UserDao;
import org.apache.commons.lang.StringEscapeUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.util.List;
import java.util.regex.Pattern;

public class SqlQueryHandler {

    private final Connection connection;

    public SqlQueryHandler(Connection connection) {
        this.connection = connection;
    }

    public User authenticateUser(HttpServletRequest request) throws NullPointerException{

        String userEmail = StringEscapeUtils.escapeJava(request.getParameter("logInMail"));
        String userPassword  = StringEscapeUtils.escapeJava(request.getParameter("logInPassword"));

        if (!isAGoodMail(userEmail) || !isAGoodPassword(userPassword))
            throw new NullPointerException();

        UserDao userDao = new UserDao(connection);
        User user = userDao.checkCredentials(userEmail, userPassword);

        if (user == null)
            throw new NullPointerException();
        else {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            return user;
        }
    }

   public List<Folder> getFolders(int userId){
        FoldersDao foldersDao = new FoldersDao(connection);
        return foldersDao.getFolders(userId);
   }

   public List<File> getSubFolderFiles(int subFolderId, int userId){

        SubFolderDao subFolderDao = new SubFolderDao(connection);

        if (subFolderDao.isItMine(subFolderId, userId))
            return subFolderDao.getSubFolderFiles(subFolderId);
        else
            return null;
   }

    public File getFile(int id, int ownerId) {

        FileDao fileDao = new FileDao(connection);

        if (fileDao.isItMine(id, ownerId))
            return fileDao.getFile(id);
        else
            return null;
    }

   public int moveFile(HttpServletRequest request){

        int id;
        int newPosition;

        File file = (File) request.getSession().getAttribute("file");
        id = file.getId();

        newPosition = Integer.parseInt(request.getParameter("selection"));

        FileDao fileDao = new FileDao(connection);
        fileDao.updatePosition(id, newPosition);

       request.getSession().removeAttribute("file");

       return newPosition;
   }

    public String getSubFolderName(int subFolderId, int userId){

        SubFolderDao subFolderDao = new SubFolderDao(connection);

        if (subFolderDao.isItMine(subFolderId, userId))
            return subFolderDao.getSubFolderName(subFolderId);
        else
            return null;
    }

    public boolean isAGoodMail (String mail) {

        if (mail==null)
            return false;

        if (mail.isEmpty())
            return false;

        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        return pat.matcher(mail).matches();
    }

    public boolean isAGoodPassword(String password) {

        if (password == null)
            return false;

        return !password.isEmpty();
    }
}
