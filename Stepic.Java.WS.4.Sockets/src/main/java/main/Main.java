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
 * Задача: Написать сокет, который будет обрабатывать запросы на /chat
 * При получении сообщения сокет должен отправит это сообщение обратно.

 * Инструкция подготовки к локальной проверке:
 * Соберите сервер со всеми зависимостями на библиотеки в server.jar
 * Для этого запустите Maven projects/<Project name>/Plugins/assembly/assembly:single либо assembly.sh (assembly.bat)
 * 
 * Скопируйте server.jar на уровень src и запустите java -jar server.jar
 * 
 * В логах консоли вы должны увидеть сообщения о старте сервера.
 * Проверьте, что сервер отвечает на запросы браузера.
 * 
 * Инструкция подготовки к автоматической проверке:Добавьте в лог сообщение "Server started".
 * По появлению в логе этого сообщения тестирующая система пойдет, что к вашему серверу можно обращаться. 
 * Соберите server.jar содержащий все библиотеки.
 * 
 * Во время проверки тестовая система: запускает ваш сервер, ждет пока "Server started", подсоединяется к ws:
 * //localhost:8080/chat
 * отправляет сообщение и ждет ответа (5 секунд)
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


