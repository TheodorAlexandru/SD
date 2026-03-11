<html xmlns:jsp="http://java.sun.com/JSP/Page">
    <head>
        <title>Modifica nume student</title>
        <meta charset="UTF-8">
    </head>
    <body>
        <h3>Modifica nume  student</h3>
        Introduceti datele noi ale studentului:
        <form action="./update-student" method="post">
            NUME NOU: <input type="text" name="nume" />
            <br />
            PRENUME NOU: <input type="text" name="prenume" />
            <br />
            VARSTA NOUA: <input type="text" name="varsta" />
            <br />
            ID: <input type="text" name="id" />
            <br />
            <br />
            <button type="submit" name="submit">Modifica</button>
        </form>
    </body>
</html>