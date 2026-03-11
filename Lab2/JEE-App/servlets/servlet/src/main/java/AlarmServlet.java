
import javax.persistence.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AlarmServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // trimitere raspuns inapoi la client
        response.setContentType("text/html");
        if(DataBaseMonitor.alarmMessage != null)
            response.getWriter().println("<h1>" + DataBaseMonitor.alarmMessage + "</h1>" +
                    "<br /><br /><a href='./'>Inapoi la meniul principal</a>");
        else
            response.getWriter().println("<h1> Nu exista niciun mesaj de alarma! </h1>" +
                    "<br /><br /><a href='./'>Inapoi la meniul principal</a>");
    }
}
