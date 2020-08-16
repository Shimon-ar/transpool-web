package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.transpool.engine.Engine;
import org.transpool.engine.ds.RequestTrip;
import org.transpool.engine.ds.Time;
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
import java.util.stream.Collectors;

@WebServlet(name = "UserTripsServlet", urlPatterns = {"/map/userTrips"})
public class UserTripsServlet extends HttpServlet {
    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Engine engine = ServletUtils.getEngine(getServletContext());
        String userName = SessionUtils.getUserName(request);
        List<RequestTrip> unmatchesRequests = engine.getRequestsOfUser(userName).stream().filter(requestTrip -> !requestTrip.isMatch()).collect(Collectors.toList());
        List<UnMatchedRequests> myTrips = new ArrayList<>();
        for (RequestTrip requestTrip : unmatchesRequests) {
            myTrips.add(new UnMatchedRequests(requestTrip.getId(), requestTrip.getTo(), requestTrip.getFrom(), requestTrip.getRequestTime().getTime(), requestTrip.getRequestTime().getWhichTime().name(), requestTrip.getMapName()));
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        try (PrintWriter out = response.getWriter()) {
            if (myTrips.size() == 0)
                out.print("false");
            else {

                String json = mapper.writeValueAsString(myTrips);
                out.print(json);
                out.flush();
            }
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

    private static class UnMatchedRequests {
        private int id;
        private String to;
        private String from;
        private Time time;
        private String whichTime;
        private String mapName;

        public UnMatchedRequests(int id, String to, String from, Time time, String whichTime, String mapName) {
            this.id = id;
            this.to = to;
            this.from = from;
            this.time = time;
            this.whichTime = whichTime;
            this.mapName = mapName;
        }

        public String getMapName() {
            return mapName;
        }

        public int getId() {
            return id;
        }

        public String getTo() {
            return to;
        }

        public String getFrom() {
            return from;
        }

        public Time getTime() {
            return time;
        }

        public String getWhichTime() {
            return whichTime;
        }
    }

}
