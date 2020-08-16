package servlets;

import org.transpool.engine.Engine;
import org.transpool.engine.ds.Alert;
import utilis.ServletUtils;
import utilis.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "AddFeedbackServlet", urlPatterns = {"/map/addFeedback"})
public class AddFeedbackServlet extends HttpServlet {
    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Engine engine = ServletUtils.getEngine(getServletContext());
        String userName = SessionUtils.getUserName(request);
        int rank = ServletUtils.getIntParameter(request, "rank");
        String feedback = request.getParameter("feedback");
        String userNameTo = request.getParameter("name");
        engine.getUser(userNameTo).addFeedback(userName, feedback);

        List<String> alertContent = new ArrayList<>();
        alertContent.add(userName);
        alertContent.add(Integer.toString(rank));
        if (feedback != null && !feedback.isEmpty())
            alertContent.add(feedback);

        boolean success = engine.getUser(userNameTo).addRank(userName, rank);
        if (success)
            engine.getUser(userNameTo).addAlert(new Alert(alertContent, Alert.Type.rank));

        try (PrintWriter out = response.getWriter()) {
            if (success) {
                out.print("true");
            } else out.print("false");

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
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
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
