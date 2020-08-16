package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.transpool.engine.Engine;
import org.transpool.engine.ds.MapDb;
import org.transpool.engine.ds.Match;
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

@WebServlet(name = "MatchesServlet", urlPatterns = {"/map/matches"})
public class MatchesServlet extends HttpServlet {
    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Engine engine = ServletUtils.getEngine(getServletContext());
        String userName = SessionUtils.getUserName(request);
        int requestId = ServletUtils.getIntParameter(request, "requestId");
        String mapName = request.getParameter("mapName");
        int limit = ServletUtils.getIntParameter(request, "limit");

        List<Match> matches = engine.getMatches(requestId, mapName, limit);
        List<MatchforSend> matchforSends = new ArrayList<>();
        for (Match match : matches)
            matchforSends.add(new MatchforSend(match.getRoadStory(), match.getId()));


        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        try (PrintWriter out = response.getWriter()) {
            if (matchforSends.size() == 0)
                out.print("false");
            else {

                String json = mapper.writeValueAsString(matchforSends);
                out.print(json);
                out.flush();
            }
        }
    }

    private static class MatchforSend {
        private String roadStory;
        private int matchId;

        public MatchforSend(String roadStory, int matchId) {
            this.roadStory = roadStory;
            this.matchId = matchId;
        }

        public String getRoadStory() {
            return roadStory;
        }

        public int getMatchId() {
            return matchId;
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
