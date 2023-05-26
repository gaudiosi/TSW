<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="it.SpaceComix.model.ProductBean" %>
<%@ page import="it.SpaceComix.model.ProductDAO" %>
<%@ page import="it.SpaceComix.model.CategoriaDAO" %>
<%@ page import="it.SpaceComix.model.CategoriaBean" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Product</title>
</head>
<body>
    <h1>Add Product</h1>
    <form method="post" action="AddProduct">
        <label for="quantita">QUANTITA:</label>
        <input type="number" id="quantita" name="quantita" required><br>

        <label for="iva">IVA:</label>
        <input type="number" id="iva" name="iva" required><br>

        <label for="prezzo">Prezzo:</label>
        <input type="number" step="0.01" id="prezzo" name="prezzo" required><br>

        <label for="titolo">Titolo:</label>
        <input type="text" id="titolo" name="titolo" required><br>

        <label for="descrizione">Descrizione:</label>
        <textarea id="descrizione" name="descrizione" rows="4" cols="50" required></textarea><br>

        <label for="autore">Autore:</label>
        <input type="text" id="autore" name="autore" required><br>

        <label for="editore">Editore:</label>
        <input type="text" id="editore" name="editore" required><br>

        <label for="isbn">ISBN:</label>
        <input type="text" id="isbn" name="isbn" required><br>

        <label for="sconto">Sconto:</label>
        <input type="number" id="sconto" name="sconto" required><br>
		
		
		
		<!--  SELEZIONE CATEGORIE -->
		<div class="categorie">
		<h4>Categorie</h4>
		<%
		CategoriaDAO cdao = new CategoriaDAO();
		String ord = "nome DESC";
		Collection<CategoriaBean> categorie = cdao.doRetrieveAll(ord);

		for (CategoriaBean c : categorie) {
		%>
  			<div class="checkBoxs-label">
  				<input type="checkbox" id="<%= c.getNome() %>" name="categorie" value="<%= c.getNome() %>" onclick="submitForm()">
  				<label for="<%= c.getNome() %>"> <%= c.getNome() %> </label><br>
  			</div>
		<%  
		} %>
		</div>
		<% String[] categorieSelezionate = new String[0]; %>
		<input type="hidden" id="categorieSelezionate" name="categorieSelezionate" value="<%= categorieSelezionate %>">
		<!--  SELEZIONE CATEGORIE -->
		
		
		
		
		
		
        <input type="submit" value="Add Product">
    </form>
    
    
<script>
  var categorieSelezionate = [];

  function submitForm() {
    var checkboxes = document.getElementsByName("categorie");

    categorieSelezionate = [];

    for (var i = 0; i < checkboxes.length; i++) {
      if (checkboxes[i].checked) {
        categorieSelezionate.push(checkboxes[i].value);
      }
    }
  }
</script>
    
</body>
</html>