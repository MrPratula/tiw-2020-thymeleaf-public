package servlet;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;

public class TemplateEngineHandler {

    public TemplateEngineHandler(){

    }

    public TemplateEngine setUp(ServletContext servletContext){

        TemplateEngine templateEngine = new TemplateEngine();
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateEngine.setTemplateResolver(templateResolver);
        templateResolver.setSuffix(".html");

        return templateEngine;
    }
}
