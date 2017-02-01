package main;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import static org.eclipse.jetty.util.log.Log.getLogger;
import servlets.MirrorServlet;

/**
 * @author Alesha
 * 
 * Задача:
 * Написать сервлет, который будет обрабатывать запросы на /mirror
 * При получении GET запроса с параметром key=value сервлет должен вернуть в response строку содержащую value.
 * Например, при GET запросе /mirror?key=hello сервер должен вернуть страницу, на которой есть слово "hello".
 * 
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
 * Во время проверки тестовая система запускает ваш сервер, ждет пока "Server started", посылает GET запрос на
 * http://localhost:8080/mirror?key=value
 * и проверяет, что в ответ пришла страница содержащая только value
 * 
 */
public class Main {
    public static void main(String[] args) throws Exception {

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.addServlet(new ServletHolder(new MirrorServlet()), "/mirror");

        Server server = new Server(8080);
        server.setHandler(context);

        server.start();
        //getLogger(server.getClass()).info("Server started");
        System.out.println("Server started");
        server.join();
    }
}