package main;

import accountServer.ResourceServer;
import accountServer.ResourceServerController;
import accountServer.ResourceServerControllerMBean;
import accountServer.ResourceServerImpl;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import servlets.ResourcePageServlet;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;

/**
 * @author Alesha
 * 
 * ������: �������� � ������ ����� resources.TestResource
 * 
 * public class TestResource {
 *  private String name;
 *  private int age;
 * 
 *  public TestResource(String name, int age) {
 *      this.name = name;
 *      this.age = age;
 *  }
 *  public TestResource() {
 *      this.name = "";
 *      this.age = 0;
 *  }
 *  public String getName() {
 *      return name;
 *  }
 *  public int getAge() {
 *      return age;
 *  }
 * }
 * 
 * �������� ResourceServer, ������� ����� ��������� ������ �� TestResource.
 *  ������� ResourceServer � JMX � ������:
 *      Admin:type=ResourceServerController
 * ������� ���������� "name" � "age" ���������� ��� ������ �� jmx �������.
 * 
 * �������� �������, ������� ����� ������������ ������� �� /resources
 * ��� ��������� POST ������� � ���������� path=path_to_resource ��������� ������
 *  TestResource �� ���������� � ��������� ����� � �������� ������ � ResourceService
 * 
 * ����� ������, �������� name � age ������ ���� �������� �� JMX.
 * 
 * ���������� ���������� � ��������� ��������: �������� ������ �� ����� ������������� �� ���������� � server.jar
 * ��� ����� ��������� Maven projects/<Project name>/Plugins/assembly/assembly:single ���� assembly.sh (assembly.bat)
 * 
 * ���������� server.jar �� ������� src � ��������� java -jar server.jar
 * 
 * � ����� ������� �� ������ ������� ��������� � ������ �������. ���������, ��� ������ �������� �� ������� ��������.
 * 
 * ���������� ���������� � �������������� ��������: �������� � ��� ��������� "Server started".
 * �� ��������� � ���� ����� ��������� ����������� ������� ������, ��� � ������ ������� ����� ����������.
 * �������� server.jar ���������� ��� ����������.
 * 
 * �� ����� �������� �������� �������: �������� ��� ������, �������� ���� "Server started",
 * ������� ���� resource.xml � ��������� �������� �������, ������� �� POST ������� ��� ����� ����� � ������,
 *  �������� ����� JMX, ��� �������� name � age ��������� � �����������
 */
public class Main {
    private static final Logger logger = configureLog4j();

    private static Logger configureLog4j() {
        LoggerContext context = (LoggerContext) LogManager.getContext();
        Configuration config = context.getConfiguration();

        PatternLayout layout = PatternLayout.createLayout("%m%n", null, null, Charset.defaultCharset(), false, false, null, null);
        Appender appender = ConsoleAppender.createAppender(layout, null, null, "CONSOLE_APPENDER", null, null);
        appender.start();
        AppenderRef ref = AppenderRef.createAppenderRef("CONSOLE_APPENDER", null, null);
        AppenderRef[] refs = new AppenderRef[]{ref};
        LoggerConfig loggerConfig = LoggerConfig.createLogger("false", Level.INFO, "CONSOLE_LOGGER", "com", refs, null, null, null);
        loggerConfig.addAppender(appender, null, null);

        config.addAppender(appender);
        config.addLogger("Main.class", loggerConfig);
        context.updateLoggers(config);
        return LogManager.getContext().getLogger("Main.class");
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;

        logger.info("Starting at http://127.0.0.1:" + port);

        ResourceServer resourceServer = new ResourceServerImpl();

        createMBean(resourceServer);

        Server server = new Server(port);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.addServlet(new ServletHolder(new ResourcePageServlet(resourceServer)), ResourcePageServlet.PAGE_URL);

        ResourceHandler resource_handler = new ResourceHandler();

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resource_handler, context});
        server.setHandler(handlers);

        server.start();
        logger.info("Server started");

        server.join();
    }

    private static void createMBean(ResourceServer resourceServer) throws MalformedObjectNameException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
        ResourceServerControllerMBean serverStatistics = new ResourceServerController(resourceServer);
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = new ObjectName("Admin:type=ResourceServerController");
        mbs.registerMBean(serverStatistics, name);
    }
}
