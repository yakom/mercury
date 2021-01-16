package yakom.mercury;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;

public class GeneralInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) {

        var springContext = new AnnotationConfigWebApplicationContext();
        springContext.register(MainContextConfiguration.class);

        var dispatcherServlet = new DispatcherServlet(springContext);
        dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);

        var registration = servletContext.addServlet(
                "dispatcher", dispatcherServlet);
        registration.setLoadOnStartup(1);
        registration.addMapping("/");
    }
}
