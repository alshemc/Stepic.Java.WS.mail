package example;

import accountServer.AccountServer;
import accountServer.AccountServerController;
import accountServer.AccountServerControllerMBean;
import accountServer.AccountServerI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import servlets.HomePageServlet;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

/**
 * @author Alesha
 * 
 * «адача: Ќаписать AccountServer, который будет содержать переменную "разрешенное количество пользователей на сервере" (usersLimit).
 * «адать значение этой переменной по умолчанию -- 10.

 * Ќаписать сервлет, который будет обрабатывать запросы на /admin
 * ѕри получении GET запроса возвращать значение usersLimit.
 * “о есть, сразу после старта сервер на GET запрос на /admin вернет страницу:
 * 10

 * ¬ывести значение usersLimit в JMX с именем: Admin:type=AccountServerController.usersLimit
 * —делать значение этой переменной измен€емым.

 * »нструкци€ подготовки к локальной проверке: —оберите сервер со всеми зависимост€ми на библиотеки в server.jar
 * ƒл€ этого запустите Maven projects/<Project name>/Plugins/assembly/assembly:single либо assembly.sh (assembly.bat)

 * —копируйте server.jar на уровень src и запустите java -jar server.jar

 * ¬ логах консоли вы должны увидеть сообщени€ о старте сервера. ѕроверьте, что сервер отвечает на запросы браузера.
 * 
 * »нструкци€ подготовки к автоматической проверке: ƒобавьте в лог сообщение "Server started".
 * ѕо по€влению в логе этого сообщени€ тестирующа€ система пойдет, что к вашему серверу можно обращатьс€.
 * —оберите server.jar содержащий все библиотеки.
 * 
 * ¬о врем€ проверки тестова€ система: запустит ваш сервер, подождет пока "Server started", 
 * проверит через JMX, что значение по умолчанию == 10
 * отправит запрос на /admin и проверит, что ответ == 10
 * изменит значение на какое-то число N (0 < N < Integer.MAX_VALUE)>>
 * проверит через JMX, что значение == N 
 * отправит запрос на /admin и проверит, что ответ == N
 * 
 * This is example to check capabilities of the testing application
 * 
 */
public class Main {
    static final Logger logger = LogManager.getLogger(Main.class.getName());

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            logger.error("Use port as the first argument!");
            System.exit(1);
        }

        String portString = args[0];
        int port = Integer.valueOf(portString);

        logger.info("Starting at http://127.0.0.1:" + portString);

        //create Management environment
        AccountServerI accountServer = new AccountServer(1);

        AccountServerControllerMBean serverStatistics = new AccountServerController(accountServer);
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = new ObjectName("ServerManager:type=AccountServerController");
        mbs.registerMBean(serverStatistics, name);

        //create server and add servlets
        Server server = new Server(port);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.addServlet(new ServletHolder(new HomePageServlet(accountServer)), HomePageServlet.PAGE_URL);

        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(true);
        resource_handler.setResourceBase("static");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resource_handler, context});
        server.setHandler(handlers);

        server.start();
        logger.info("Server started!");

        server.join();
    }
}
