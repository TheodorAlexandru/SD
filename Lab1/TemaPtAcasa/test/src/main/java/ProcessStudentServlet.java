import DataBase.DB;
import beans.StudentBean;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Year;


public class ProcessStudentServlet extends HttpServlet {

    @Override
    public void init() throws ServletException{
        DB.initializeTable();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
// se citesc parametrii din cererea de tip POST
        String nume = request.getParameter("nume");
        String prenume = request.getParameter("prenume");
        int varsta = Integer.parseInt(request.getParameter("varsta"));
/*
procesarea datelor - calcularea anului nasterii
*/
        int anCurent = Year.now().getValue();
        int anNastere = anCurent - varsta;

       String sql = "INSERT INTO studenti (nume, prenume, varsta, an_nastere) VALUES (?, ?, ?, ?)";

       try(Connection conn = DriverManager.getConnection(DB.getURL());
           PreparedStatement pstmt = conn.prepareStatement(sql);) {
           pstmt.setString(1, nume);
           pstmt.setString(2, prenume);
           pstmt.setInt(3, varsta);
           pstmt.setInt(4, anNastere);

           pstmt.executeUpdate();
       }catch (SQLException e){
           e.printStackTrace();
           response.sendError(500, "Nu s-a putut salva studentul");
           return;
       }
        request.getRequestDispatcher("./index.jsp").forward(request, response);
    }
}
