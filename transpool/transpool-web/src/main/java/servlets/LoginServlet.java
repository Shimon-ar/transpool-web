package servlets;


import org.transpool.engine.Engine;
import org.transpool.engine.ds.User;
import utilis.Constants;
import utilis.ServletUtils;
import utilis.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        String userNameFromSession = SessionUtils.getUserName(request);
        Engine engine = ServletUtils.getEngine(getServletContext());
        boolean success = false;
        if (userNameFromSession == null) {
            String userName = request.getParameter("username");
            String offerRole = request.getParameter("offer");
            userName = userName.trim();
            if (userName != null && !userName.isEmpty() && !engine.isUserExist(userName)) {
                if (offerRole == null)
                    engine.addUser(userName, User.Role.request);
                else
                    engine.addUser(userName, User.Role.offer);
                request.getSession(true).setAttribute(Constants.USERNAME, userName);
                success = true;
            }

        } else success = true;

        try (PrintWriter out = response.getWriter()) {
            out.print(success);
            out.flush();
        }

    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>


}
