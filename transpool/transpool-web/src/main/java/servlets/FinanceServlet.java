package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import json.MapDes;
import org.transpool.engine.Engine;
import org.transpool.engine.ds.ActionAccount;
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
import java.util.List;

@WebServlet(name = "FinanceServlet", urlPatterns = {"/user/finance"})
public class FinanceServlet extends HttpServlet {
    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Engine engine = ServletUtils.getEngine(getServletContext());
        String userName = SessionUtils.getUserName(request);
        List<ActionAccount> allActions = engine.getUser(userName).getAccount().getHistoryAction();
        int row = ServletUtils.getIntParameter(request, Constants.ROW);
        if (row == -1 || row == allActions.size())
            return;
        List<ActionAccount> newActions = allActions.subList(row,allActions.size());

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);


        try (PrintWriter out = response.getWriter()) {
            String json = mapper.writeValueAsString(new ActionsAndRow(newActions,allActions.size()));
            out.print(json);
            out.flush();
        }

    }

    private static class ActionsAndRow {
        private List<ActionAccount> actions;
        private int row;

        public ActionsAndRow(List<ActionAccount> actions, int row) {
            this.actions = actions;
            this.row = row;
        }

        public List<ActionAccount> getActions() {
            return actions;
        }

        public int getRow() {
            return row;
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
