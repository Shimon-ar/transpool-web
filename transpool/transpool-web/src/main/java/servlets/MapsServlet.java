package servlets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import json.MapDes;
import org.transpool.engine.Engine;
import org.transpool.engine.ds.FullMap;
import utilis.Constants;
import utilis.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "MapsServlet", urlPatterns = {"/user/getMaps"})
public class MapsServlet extends HttpServlet {
    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Engine engine = ServletUtils.getEngine(getServletContext());
        List<FullMap> allMaps = engine.getMaps();
        List<MapDes> newMaps = new ArrayList<>();
        for (FullMap map:allMaps) {
            newMaps.add(new MapDes(map.getMapName(), map.getUserUploaded(), map.getMap().getTotalRoads(),
                    map.getMap().getTotalStops(), map.getTransPoolTrips().size(), map.getRequestTrips().size(),
                    map.getMatchRequested().size()));
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);



        try (PrintWriter out = response.getWriter()) {

            String json = mapper.writeValueAsString(newMaps);
            out.print(json);
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
