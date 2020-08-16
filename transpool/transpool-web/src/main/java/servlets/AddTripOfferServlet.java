package servlets;

import org.transpool.engine.Engine;
import org.transpool.engine.ds.Time;
import utilis.ServletUtils;
import utilis.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;


@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
@WebServlet(name = "AddTripOfferServlet", urlPatterns = {"/Trip/addTripOffer"})
public class AddTripOfferServlet extends HttpServlet {
    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html;charset=UTF-8");
        Engine engine = ServletUtils.getEngine(getServletContext());
        String userName = SessionUtils.getUserName(request);
        String routeString = readFromInputStream(request.getPart("route").getInputStream());
        String mapName = readFromInputStream(request.getPart("mapName").getInputStream());

        List<String> route = Arrays.stream(routeString.split(",")).collect(Collectors.toList());
        boolean success = true;

        if (!engine.isValidRoute(route, mapName))
            success = false;

        try (PrintWriter out = response.getWriter()) {
            out.print(success);
            out.flush();
        }
        Time time = new Time(Integer.parseInt(readFromInputStream(request.getPart("minutes").getInputStream())),
                Integer.parseInt(readFromInputStream(request.getPart("hour").getInputStream())),
                Integer.parseInt(readFromInputStream(request.getPart("dayStart").getInputStream())));

        engine.addTransPoolTrip(mapName,userName,route,time,readFromInputStream(request.getPart("recurrences").getInputStream()),
                Integer.parseInt(readFromInputStream(request.getPart("ppk").getInputStream())),
                Integer.parseInt(readFromInputStream(request.getPart("capacity").getInputStream())));

    }





    private String readFromInputStream(InputStream inputStream) {
        return new Scanner(inputStream).useDelimiter("\\Z").next();
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
