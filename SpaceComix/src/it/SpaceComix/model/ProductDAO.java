package it.SpaceComix.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedList;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class ProductDAO implements DAO<ProductBean> {

    private static DataSource ds;

    static {
        try {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");

            ds = (DataSource) envCtx.lookup("jdbc/SpaceComix");

        } catch (NamingException e) {
            System.out.println("Error:" + e.getMessage());
        }
    }

    private static final String TABLE_NAME = "Prodotto";

    @Override
    public synchronized void doSave(ProductBean product) throws SQLException,NullPointerException {

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        String insertSQL = "INSERT INTO " + ProductDAO.TABLE_NAME
                + " (quantita, iva, prezzo, titolo, descrizione, autore, editore, isbn, sconto, immagine , image_alt) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String insert2SQL = "INSERT INTO Appartenenza (idCategoria,idProdotto) VALUES (?,?)";

        try {
            connection = ds.getConnection();
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, product.getQuantita());
            preparedStatement.setInt(2, product.getIva());
            preparedStatement.setFloat(3, product.getPrezzo());
            preparedStatement.setString(4, product.getTitolo());
            preparedStatement.setString(5, product.getDescrizione());
            preparedStatement.setString(6, product.getAutore());
            preparedStatement.setString(7, product.getEditore());
            preparedStatement.setString(8, product.getIsbn());
            preparedStatement.setInt(9, product.getSconto());
            preparedStatement.setString(10, product.getImage());
            preparedStatement.setString(11, product.getImage_alt());

            preparedStatement.executeUpdate();


            if(product.getGeneri().size()>0)
            {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                int InsertedId = -1;
                if (generatedKeys.next()) {
                    InsertedId = generatedKeys.getInt(1);
                }


                try (PreparedStatement preparedStatement2 = connection.prepareStatement(insert2SQL)) {
                	for (CategoriaBean categoria : product.getGeneri())
                	{
                    	preparedStatement2.setString(1, categoria.getNome());
                    	preparedStatement2.setInt(2, InsertedId);
                    	preparedStatement2.addBatch();

                	}
                	preparedStatement2.executeBatch();
                }

            }


            connection.commit();

        } finally {
            try {
            	if (preparedStatement != null)
                    preparedStatement.close();
            } finally {
                if (connection != null)
                    connection.close();
            }
        }
    }

    public synchronized Collection<ProductBean> doRetrieveByKey(String code) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        Collection<ProductBean> products = new LinkedList<ProductBean>();

        String selectSQL = "SELECT * FROM " + TABLE_NAME +
                " AS P LEFT JOIN Appartenenza AS A ON P.id = A.idProdotto" +
                " LEFT JOIN Categoria AS C ON A.idCategoria=C.nome WHERE P.titolo LIKE ?";

        try {
            connection = ds.getConnection();
            preparedStatement = connection.prepareStatement(selectSQL);
            preparedStatement.setString(1,"%" + code + "%");

            ResultSet rs = preparedStatement.executeQuery();

            boolean currentnext = rs.next();

            while (currentnext) {       //Finché esiste una riga corrente crea un nuovo prodotto


                ProductBean bean = new ProductBean();

                bean.setID(rs.getInt("id"));
                bean.setQuantita(rs.getInt("quantita"));
                bean.setIva(rs.getInt("iva"));
                bean.setPrezzo(rs.getFloat("prezzo"));
                bean.setTitolo(rs.getString("titolo"));
                bean.setDescrizione(rs.getString("descrizione"));
                bean.setAutore(rs.getString("autore"));
                bean.setEditore(rs.getString("editore"));
                bean.setIsbn(rs.getString("isbn"));
                bean.setSconto(rs.getInt("sconto"));
                bean.setImage(rs.getString("immagine"));
                bean.setImage_alt(rs.getString("image_alt"));

                products.add(bean);

                if(rs.getString("C.nome") != null)
                {
                    do {  //Crea una nuova categoria
                        CategoriaBean c = new CategoriaBean();
                        c.setNome(rs.getString("C.nome"));
                        c.setDescrizione(rs.getString("C.descrizione"));
                        bean.addCategoria(c);
                        currentnext = rs.next();
                    } while(currentnext && rs.getInt("id")== bean.getID());
                    //FinchÃ© la nuova riga corrente ha lo stesso prodotto
                }
                else {
                    currentnext = rs.next();
                }

            }

        } finally {
            try {
                if (preparedStatement != null)
                    preparedStatement.close();
            } finally {
                if (connection != null)
                    connection.close();
            }
        }
        
        return products;
    }

    @Override
    public synchronized boolean doDelete(int code) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        int result = 0;

        String deleteSQL = "DELETE FROM " + ProductDAO.TABLE_NAME + " WHERE id = ?";

        try {
            connection = ds.getConnection();
            preparedStatement = connection.prepareStatement(deleteSQL);
            preparedStatement.setInt(1, code);

            result = preparedStatement.executeUpdate();

        } finally {
            try {
                if (preparedStatement != null)
                    preparedStatement.close();
            } finally {
                if (connection != null)
                    connection.close();
            }
        }
        return (result != 0);
    }
    
    public synchronized ProductBean doRetrieveByKey(int code) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        ProductBean bean = new ProductBean();

        String selectSQL = "SELECT * FROM " + TABLE_NAME +
                " AS P LEFT JOIN Appartenenza AS A ON P.id = A.idProdotto" +
                " LEFT JOIN Categoria AS C ON A.idCategoria=C.nome WHERE P.id = ?";

        try {
            connection = ds.getConnection();
            preparedStatement = connection.prepareStatement(selectSQL);
            preparedStatement.setInt(1, code);

            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                bean.setID(rs.getInt("id"));
                bean.setQuantita(rs.getInt("quantita"));
                bean.setIva(rs.getInt("iva"));
                bean.setPrezzo(rs.getFloat("prezzo"));
                bean.setTitolo(rs.getString("titolo"));
                bean.setDescrizione(rs.getString("descrizione"));
                bean.setAutore(rs.getString("autore"));
                bean.setEditore(rs.getString("editore"));
                bean.setIsbn(rs.getString("isbn"));
                bean.setSconto(rs.getInt("sconto"));
                bean.setImage(rs.getString("immagine"));
                bean.setImage_alt(rs.getString("image_alt"));


                if(rs.getString("C.nome") != null)
                {
                    do {

                        CategoriaBean c = new CategoriaBean();
                        c.setNome(rs.getString("C.nome"));
                        c.setDescrizione(rs.getString("C.descrizione"));
                        bean.addCategoria(c);

                    } while (rs.next());

                }


            }


        } finally {
            try {
                if (preparedStatement != null)
                    preparedStatement.close();
            } finally {
                if (connection != null)
                    connection.close();
            }
        }
        return bean;
    }

    @Override
    public synchronized Collection<ProductBean> doRetrieveAll(String order) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        Collection<ProductBean> products = new LinkedList<ProductBean>();

        String selectSQL = "SELECT * FROM " + TABLE_NAME +
                " AS P LEFT JOIN Appartenenza AS A ON P.id = A.idProdotto" +
                " LEFT JOIN Categoria AS C ON A.idCategoria=C.nome";

        if (order != null && !order.equals("")) {
            selectSQL += " ORDER BY ?";
        }

        try {
            connection = ds.getConnection();
            preparedStatement = connection.prepareStatement(selectSQL);
            
            if (order != null && !order.equals("")) {
            	preparedStatement.setString(1, order);
            }

            ResultSet rs = preparedStatement.executeQuery();

            boolean currentnext = rs.next();

            while (currentnext) {       //Finché esiste una riga corrente crea un nuovo prodotto


                ProductBean bean = new ProductBean();

                bean.setID(rs.getInt("id"));
                bean.setQuantita(rs.getInt("quantita"));
                bean.setIva(rs.getInt("iva"));
                bean.setPrezzo(rs.getFloat("prezzo"));
                bean.setTitolo(rs.getString("titolo"));
                bean.setDescrizione(rs.getString("descrizione"));
                bean.setAutore(rs.getString("autore"));
                bean.setEditore(rs.getString("editore"));
                bean.setIsbn(rs.getString("isbn"));
                bean.setSconto(rs.getInt("sconto"));
                bean.setImage(rs.getString("immagine"));
                bean.setImage_alt(rs.getString("image_alt"));

                products.add(bean);

                if(rs.getString("C.nome") != null)
                {
                    do {  //Crea una nuova categoria
                        CategoriaBean c = new CategoriaBean();
                        c.setNome(rs.getString("C.nome"));
                        c.setDescrizione(rs.getString("C.descrizione"));
                        bean.addCategoria(c);
                        currentnext = rs.next();
                    } while(currentnext && rs.getInt("id")== bean.getID());
                    //FinchÃ© la nuova riga corrente ha lo stesso prodotto
                }
                else {
                    currentnext = rs.next();
                }

            }

        } finally {
            try {
                if (preparedStatement != null)
                    preparedStatement.close();
            } finally {
                if (connection != null)
                    connection.close();
            }
        }
        
        return products;
    }

	@Override
	public ProductBean doRetrieveByKey(String code, String code1) throws SQLException {
		return null;
	}
	
	
	public synchronized void doUpdate(ProductBean product) throws SQLException {

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        String updateSQL = "UPDATE " + TABLE_NAME
                + " SET quantita = ?, iva = ?, prezzo = ?, titolo = ?, descrizione = ?, autore = ?, editore = ?, isbn = ?, sconto = ?  WHERE id = ?";

        try {
            connection = ds.getConnection();
            preparedStatement = connection.prepareStatement(updateSQL);
            preparedStatement.setInt(1, product.getQuantita());
            preparedStatement.setInt(2, product.getIva());
            preparedStatement.setFloat(3, product.getPrezzo());
            preparedStatement.setString(4, product.getTitolo());
            preparedStatement.setString(5, product.getDescrizione());
            preparedStatement.setString(6, product.getAutore());
            preparedStatement.setString(7, product.getEditore());
            preparedStatement.setString(8, product.getIsbn());
            preparedStatement.setInt(9, product.getSconto());
            preparedStatement.setInt(10, product.getID());

            preparedStatement.executeUpdate();

        } finally {
        	try {
                if (preparedStatement != null)
                    preparedStatement.close();
            } finally {
                if (connection != null)
                    connection.close();
            }
        }
    }
}
