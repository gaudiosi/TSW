package it.spacecomix.control;


import it.spacecomix.model.Carrello;
import it.spacecomix.model.DAO;
import it.spacecomix.model.ProductBean;
import it.spacecomix.model.ProductDAO;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet("/carrello")
public class CarrelloServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    static DAO<ProductBean> model = new ProductDAO();

    public CarrelloServlet()
    {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Carrello cart = (Carrello)  request.getSession().getAttribute("cart");

        if(cart== null)
        {
            cart = new Carrello();
            request.getSession().setAttribute("cart", cart);


        }
        RequestDispatcher dispatcher = request.getServletContext().getRequestDispatcher("/carrello.jsp");
        dispatcher.forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Carrello cart = (Carrello) request.getSession().getAttribute("cart");
        if (cart == null) {
            cart = new Carrello();
            request.getSession().setAttribute("cart", cart);
        }

        String action = request.getParameter("action");

        try {
             if(action!= null)
             {
                 if(action.equals("add"))
                 {
                     int id = Integer.parseInt(request.getParameter("id"));
                     cart.addProdotto(model.doRetrieveByKey(id));
                     response.sendRedirect( request.getServletContext().getContextPath() + "/Prodotto?id="+ request.getParameter("id"));

                 }
                 else if (action.equals("remove"))
                 {
                     int id = Integer.parseInt(request.getParameter("id"));
                     cart.removeProdotto(model.doRetrieveByKey(id));
                     response.sendRedirect( request.getServletContext().getContextPath() + "/carrello");

                 }
                 else if (action.equals("decrease"))
                 {
                     int id = Integer.parseInt(request.getParameter("id"));
                     cart.decreaseProdotto(model.doRetrieveByKey(id));



                 }
                 else if(action.equals("update"))
                 {
                     int id = Integer.parseInt(request.getParameter("id"));
                     int quantity = Integer.parseInt(request.getParameter("quantity"));
                     cart.updateProdotto(model.doRetrieveByKey(id),quantity);
                     response.sendRedirect( request.getServletContext().getContextPath() + "/carrello");


                 }
                 else {
                     response.sendError(404);
                 }


             }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


}
