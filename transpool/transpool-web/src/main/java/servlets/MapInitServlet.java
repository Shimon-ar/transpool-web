package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.transpool.engine.Engine;
import org.transpool.engine.ds.MapDb;
import org.transpool.engine.ds.Node;
import org.transpool.engine.ds.User;
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

@WebServlet(name = "MapInitServlet", urlPatterns = {"/map/mapInit"})
public class MapInitServlet extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Engine engine = ServletUtils.getEngine(getServletContext());
        String userName = SessionUtils.getUserName(request);
        MapDb mapDb = engine.getFullMapByName(request.getParameter("mapName")).getMap();
        List<Node> map = mapDb.getAllNode();
        List<StopDes> mapToSend = new ArrayList<>();
        for(Node stop:map){
            List<String> paths = stop.getPaths().stream().map(path -> path.getTo().getName()).collect(Collectors.toList());
            mapToSend.add(new StopDes(stop.getStop().getName(),stop.getStop().getX(),stop.getStop().getY(),paths));
        }


        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);



        try (PrintWriter out = response.getWriter()) {

            String json = mapper.writeValueAsString(new MapRes(engine.getUser(userName).isOffer(),mapToSend,mapDb.getLength(),mapDb.getWidth()));
            out.print(json);
            out.flush();
        }
    }

    private static class MapRes{
        private boolean offer;
        private List<StopDes> myMap;
        private int length;
        private int width;

        public MapRes(boolean offer, List<StopDes> map,int length,int width) {
            this.offer = offer;
            this.myMap = map;
            this.length = length;
            this.width = width;
        }

        public int getLength() {
            return length;
        }

        public int getWidth() {
            return width;
        }

        public boolean isOffer() {
            return offer;
        }

        public List<StopDes> getMyMap() {
            return myMap;
        }
    }



    private static class StopDes{
        private String stopName;
        private int x;
        private int y;
        private List<String> paths;


        public StopDes(String stopName,int x,int y, List<String> paths) {
            this.stopName = stopName;
            this.paths = paths;
            this.x = x;
            this.y = y;
        }

        public String getStopName() {
            return stopName;
        }

        public List<String> getPaths() {
            return paths;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
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
