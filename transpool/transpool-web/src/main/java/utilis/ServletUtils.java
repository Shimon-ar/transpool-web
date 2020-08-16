package utilis;


import org.transpool.engine.Engine;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public class ServletUtils {
    private static final Object engineLock = new Object();

    public static Engine getEngine(ServletContext servletContext) {
        synchronized (engineLock) {
            if (servletContext.getAttribute(Constants.ENGINE) == null) {
                servletContext.setAttribute(Constants.ENGINE, new Engine());
            }
        }
        return (Engine) servletContext.getAttribute(Constants.ENGINE);
    }

    public static int getIntParameter(HttpServletRequest request,String parameter) {
        String value = request.getParameter(parameter);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException numberFormatException) {
            }
        }
        return -1;
    }

}
