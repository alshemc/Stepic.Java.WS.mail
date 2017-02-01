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
 * Задача: Добавить в сервер класс resources.TestResource
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
 * Написать ResourceServer, который будет содержать ссылку на TestResource.
 *  Вывести ResourceServer в JMX с именем:
 *      Admin:type=ResourceServerController
 * Сделать переменные "name" и "age" доступными для чтения из jmx клиента.
 * 
 * Написать сервлет, который будет обрабатывать запросы на /resources
 * При получении POST запроса с параметром path=path_to_resource прочитает ресурс
 *  TestResource из указанного в параметре файла и сохранит ссылку в ResourceService
 * 
 * После чтения, значения name и age должны быть доступны по JMX.
 * 
 * Инструкция подготовки к локальной проверке: Соберите сервер со всеми зависимостями на библиотеки в server.jar
 * Для этого запустите Maven projects/<Project name>/Plugins/assembly/assembly:single либо assembly.sh (assembly.bat)
 * 
 * Скопируйте server.jar на уровень src и запустите java -jar server.jar
 * 
 * В логах консоли вы должны увидеть сообщения о старте сервера. Проверьте, что сервер отвечает на запросы браузера.
 * 
 * Инструкция подготовки к автоматической проверке: Добавьте в лог сообщение "Server started".
 * По появлению в логе этого сообщения тестирующая система пойдет, что к вашему серверу можно обращаться.
 * Соберите server.jar содержащий все библиотеки.
 * 
 * Во время проверки тестовая система: запустит ваш сервер, подождет пока "Server started",
 * Создаст файл resource.xml в локальной файловой системе, Пришлет по POST запросу имя этого файла в сервер,
 *  проверит через JMX, что значение name и age совпадают с записанными
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
