import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

public class DeleteStudentServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Indicăm calea exactă către fișierul XML pe care vrem să-l ștergem
        File file = new File("/home/theo/Documents/SD/Lab1/JEE-Test/test/student.xml");

        // Verificăm dacă fișierul există și îl ștergem
        if (file.exists()) {
            file.delete();
        }

        request.getRequestDispatcher("./index.jsp").forward(request, response);
    }
}