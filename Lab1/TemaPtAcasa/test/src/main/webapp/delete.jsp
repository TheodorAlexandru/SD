<html xmlns:jsp="http://java.sun.com/JSP/Page">
    <head>
        <title>Sterge student</title>
        <meta charset="UTF-8">
    </head>
    <body>
        <h3>Sterge student</h3>
        Introduceti ID-ul studentului care va fi sters:
        <form action="./delete-student" method="post">
            ID: <input type="text" name="id" />
            <br />
            <br />
            <button type="submit" name="submit">Sterge</button>
        </form>
    </body>
</html>