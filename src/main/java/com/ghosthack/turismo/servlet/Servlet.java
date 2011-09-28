package com.ghosthack.turismo.servlet;

import static com.ghosthack.turismo.util.ClassForName.createInstance;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ghosthack.turismo.Resolver;
import com.ghosthack.turismo.Routes;
import com.ghosthack.turismo.action.ActionException;
import com.ghosthack.turismo.util.ClassForName.ClassForNameException;


/**
 * Action servlet.
 * <p>
 * Resolves an action based on the request, Each route executes an
 * action. On init configures the {@link Routes} {@link Resolver}.
 * 
 * <pre>
 * 	&lt;servlet&gt;
 * 		&lt;servlet-name&gt;app-action-servlet&lt;/servlet-name&gt;
 * 		&lt;servlet-class&gt;action.Servlet&lt;/servlet-class&gt;
 * 		&lt;init-param&gt;
 * 			&lt;param-name&gt;routes&lt;/param-name&gt;
 * 			&lt;param-value&gt;example.Routes&lt;/param-value&gt;
 * 		&lt;/init-param&gt;
 * 	&lt;/servlet&gt;
 * 	&lt;servlet-mapping&gt;
 * 		&lt;servlet-name&gt;app-action-servlet&lt;/servlet-name&gt;
 * 		&lt;url-pattern&gt;/app/*&lt;/url-pattern&gt;
 * 	&lt;/servlet-mapping&gt;
 * </pre>
 */
public class Servlet extends HttpServlet {

    private static final String ROUTES = "routes";
    private static final long serialVersionUID = 1L;
    
    private transient Routes routes;
    private transient ServletContext context;

    @Override
    public void service(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        Env.create(req, res, context);
        try {
            final Runnable action = routes.getResolver().resolve();
            action.run();
        
        } catch (ActionException e) {
            throw new ServletException(e);
        } finally {
            Env.destroy();
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init();
        context = config.getServletContext();
        final String routesParam = config.getInitParameter(ROUTES);
        try {
            routes = createInstance(routesParam, Routes.class);
        } catch (ClassForNameException e) {
            throw new ServletException(e);
        }
    }

}
