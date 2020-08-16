package servlets;


import org.transpool.engine.Engine;
import utilis.Constants;
import utilis.ServletUtils;
import utilis.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Scanner;

@WebServlet(name = "UploadServlet", urlPatterns = {"/user/upload"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class UploadServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String userName = SessionUtils.getUserName(request);
        Engine engine = ServletUtils.getEngine(getServletContext());

        response.setContentType("text/html");
        String mapName = readFromInputStream(request.getPart("mapName").getInputStream());
        mapName = mapName.trim();
        String fileContent = readFromInputStream(request.getPart("file").getInputStream());
        InputStream fileStream = new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8));



        try (PrintWriter out = response.getWriter()) {

            try {
                if (!engine.loadMap(fileStream, mapName, userName))
                    out.print(engine.getErrorDes());
                else out.print("true");
            } catch (JAXBException e) {
                e.printStackTrace();
            }



        }
    }

    private String readFromInputStream(InputStream inputStream) {
        return new Scanner(inputStream).useDelimiter("\\Z").next();
    }
}
