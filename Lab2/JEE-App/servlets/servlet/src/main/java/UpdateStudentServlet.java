import ejb.StudentEntity;

import javax.persistence.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UpdateStudentServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // se citesc parametrii din cererea de tip POST
        int id = Integer.parseInt(request.getParameter("id"));
        String nume_nou = request.getParameter("nume_nou");
        String prenume_nou = request.getParameter("prenume_nou");
        int varsta_noua = Integer.parseInt(request.getParameter("varsta_noua"));


        // pregatire EntityManager
        EntityManagerFactory factory =   Persistence.createEntityManagerFactory("bazaDeDateSQLite");
        EntityManager em = factory.createEntityManager();

        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        Query query = em.createQuery("UPDATE StudentEntity s SET s.nume = :nume_nou, s.prenume = :prenume_nou, s.varsta = :varsta_noua WHERE s.id = :id");
        query.setParameter("nume_nou", nume_nou);
        query.setParameter("prenume_nou", prenume_nou);
        query.setParameter("varsta_noua", varsta_noua);
        query.setParameter("id", id);
        query.executeUpdate();
        transaction.commit();

        // inchidere EntityManager
        em.close();
        factory.close();

        response.sendRedirect("./fetch-student-list");
    }
}
