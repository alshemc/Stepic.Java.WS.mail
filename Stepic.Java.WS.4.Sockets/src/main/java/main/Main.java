package main;

import chat.WebSocketChatServlet;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * @author Alesha
 * 
 * This is example to check capabilities of the web sokets application
 * 
 * ������: �������� �����, ������� ����� ������������ ������� �� /chat
 * ��� ��������� ��������� ����� ������ �������� ��� ��������� �������.

 * ���������� ���������� � ��������� ��������:
 * �������� ������ �� ����� ������������� �� ���������� � server.jar
 * ��� ����� ��������� Maven projects/<Project name>/Plugins/assembly/assembly:single ���� assembly.sh (assembly.bat)
 * 
 * ���������� server.jar �� ������� src � ��������� java -jar server.jar
 * 
 * � ����� ������� �� ������ ������� ��������� � ������ �������.
 * ���������, ��� ������ �������� �� ������� ��������.
 * 
 * ���������� ���������� � �������������� ��������:�������� � ��� ��������� "Server started".
 * �� ��������� � ���� ����� ��������� ����������� ������� ������, ��� � ������ ������� ����� ����������. 
 * �������� server.jar ���������� ��� ����������.
 * 
 * �� ����� �������� �������� �������: ��������� ��� ������, ���� ���� "Server started", �������������� � ws:
 * //localhost:8080/chat
 * ���������� ��������� � ���� ������ (5 ������)
 * 
 */
public class Main {
    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);

        context.addServlet(new ServletHolder(new WebSocketChatServlet()), "/chat");

        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(true);
        resource_handler.setResourceBase("public_html");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resource_handler, context});
        server.setHandler(handlers);

        server.start();
        server.join();
    }
}


