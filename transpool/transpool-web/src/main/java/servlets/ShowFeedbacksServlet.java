package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet(name = "ShowFeedbacksServlet", urlPatterns = {"/map/showFeedbacks"})
public class ShowFeedbacksServlet extends HttpServlet {
    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Engine engine = ServletUtils.getEngine(getServletContext());
        String userName = SessionUtils.getUserName(request);
        List<FeedBack> feedBackList = new ArrayList<>();
        double rank = engine.getUser(userName).getRank();
        Map<String,String> feedbackMap  = engine.getUser(userName).getFeedbackList();
        for(String user:feedbackMap.keySet())
            feedBackList.add(new FeedBack(feedbackMap.get(user),user));

        try (PrintWriter out = response.getWriter()) {
            if (feedBackList.isEmpty())
                out.print("false");
             else {
                ObjectMapper mapper = new ObjectMapper();
                mapper.enable(SerializationFeature.INDENT_OUTPUT);
                mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
                String json = mapper.writeValueAsString(feedBackList);
                out.print(json);
            }

            out.flush();
        }


    }

    public static class FeedBack{
        private String feedback;
        private String user;

        public FeedBack(String feedback, String user) {
            this.feedback = feedback;
            this.user = user;
        }


        public String getFeedback() {
            return feedback;
        }

        public String getUser() {
            return user;
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
