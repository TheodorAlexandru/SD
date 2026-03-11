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


public class UpdateStudentNameServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

// se citesc parametrii din cererea de tip POST
        int id = Integer.parseInt(request.getParameter("id"));
        String nume = request.getParameter("nume");
        String prenume = request.getParameter("prenume");
        int varsta = Integer.parseInt(request.getParameter("varsta"));

        int anCurent = Year.now().getValue();
        int anNastere = anCurent - varsta;

        String sql = "UPDATE studenti SET nume = ?, prenume = ?, varsta = ?, an_nastere = ? where id = ?";

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try(Connection conn = DriverManager.getConnection(DB.getURL());
            PreparedStatement pstmt = conn.prepareStatement(sql);) {

            pstmt.setString(1, nume);
            pstmt.setString(2, prenume);
            pstmt.setInt(3, varsta);
            pstmt.setInt(4, anNastere);
            pstmt.setInt(5, id);

            pstmt.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
            response.sendError(500, "Nu s-a putut modifica studentul");
            return;
        }
        request.getRequestDispatcher("./index.jsp").forward(request, response);
    }
}
