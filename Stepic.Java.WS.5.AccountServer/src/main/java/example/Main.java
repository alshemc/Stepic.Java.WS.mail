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
 * ������: �������� AccountServer, ������� ����� ��������� ���������� "����������� ���������� ������������� �� �������" (usersLimit).
 * ������ �������� ���� ���������� �� ��������� -- 10.

 * �������� �������, ������� ����� ������������ ������� �� /admin
 * ��� ��������� GET ������� ���������� �������� usersLimit.
 * �� ����, ����� ����� ������ ������ �� GET ������ �� /admin ������ ��������:
 * 10

 * ������� �������� usersLimit � JMX � ������: Admin:type=AccountServerController.usersLimit
 * ������� �������� ���� ���������� ����������.

 * ���������� ���������� � ��������� ��������: �������� ������ �� ����� ������������� �� ���������� � server.jar
 * ��� ����� ��������� Maven projects/<Project name>/Plugins/assembly/assembly:single ���� assembly.sh (assembly.bat)

 * ���������� server.jar �� ������� src � ��������� java -jar server.jar

 * � ����� ������� �� ������ ������� ��������� � ������ �������. ���������, ��� ������ �������� �� ������� ��������.
 * 
 * ���������� ���������� � �������������� ��������: �������� � ��� ��������� "Server started".
 * �� ��������� � ���� ����� ��������� ����������� ������� ������, ��� � ������ ������� ����� ����������.
 * �������� server.jar ���������� ��� ����������.
 * 
 * �� ����� �������� �������� �������: �������� ��� ������, �������� ���� "Server started", 
 * �������� ����� JMX, ��� �������� �� ��������� == 10
 * �������� ������ �� /admin � ��������, ��� ����� == 10
 * ������� �������� �� �����-�� ����� N (0 < N < Integer.MAX_VALUE)>>
 * �������� ����� JMX, ��� �������� == N 
 * �������� ������ �� /admin � ��������, ��� ����� == N
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
