package si.ijs.mobis.service;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "EventDescriptionServlet", urlPatterns = {"/EventDescriptionServlet"})
public class EventDescriptionServlet extends HttpServlet {

    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String parameter = request.getParameter("a");

        // manipulate whatever

        response.getWriter().write("Got parameter: " + parameter);
        response.getWriter().flush();
        response.getWriter().close(); 
    }

   
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response); 
    }
}