<html xmlns:jsp="http://java.sun.com/JSP/Page">
	<head>
		<title>Formular student</title>
		<meta charset="UTF-8" />
	</head>
	<body>
		<h3>Formular student</h3>
		Introduceti noile date despre student:
		<form action="./update-student" method="post">
		    <input type="hidden" name="id" value="<%= request.getParameter("id") %>" />
			Nume: <input type="text" name="nume_nou" />
			<br />
			Prenume: <input type="text" name="prenume_nou" />
			<br />
			Varsta: <input type="number" name="varsta_noua" />
			<br />
			<br />
			<button type="submit" name="submit">Actualizeaza</button>
		</form>
	</body>
</html>