
import javax.persistence.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DeleteStudentServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // se citesc parametrii din cererea de tip GET
        int id = Integer.parseInt(request.getParameter("id"));

        // pregatire EntityManager
        EntityManagerFactory factory =   Persistence.createEntityManagerFactory("bazaDeDateSQLite");
        EntityManager em = factory.createEntityManager();


        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        Query query = em.createQuery("DELETE FROM StudentEntity s WHERE s.id = :studentId");
        query.setParameter("studentId", id);
        query.executeUpdate();
        transaction.commit();

        // inchidere EntityManager
        em.close();
        factory.close();

        response.sendRedirect("./fetch-student-list");
    }
}
