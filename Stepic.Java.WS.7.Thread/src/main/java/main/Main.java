package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Alesha
 * 
 * Задача: 
 * Написать серверную часть клиент-серверного приложения на сокетах (не веб сокетах, а обычных сокетах).
 * Тестирующее приложение будет имитировать клиентские приложения. 
 * Сервер должен слушать обращения клиентов на localhost:5050 и отправлять обратно все пришедшие от них сообщения.
 * То есть, если клиент присылает "Hello!" ему обратно должно быть отправлено "Hello!".
 * 
 * В процессе тестирования к серверу будут одновременно подключаться 10 клиентов.
 * Каждый клиент будет отправлять по сообщению каждую миллисекунду в течение 5 секунд.
 * Сервер должен отвечать всем клиентам одновременно. Время на обработку сообщений от всех клиентов не более 10 секунд.
 * Если сервер отвечает дольше - увеличьте число потоков для обработки сообщений.
 * 
 * Вы можете решить задачу либо через создание потока на каждый Socket, либо использую неблокирующие сокеты из NIO.
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
 * Во время проверки тестовая система: запустит ваш сервер, подождет пока "Server started",
 *  создаст 10 сокетов в 10 потоках и каждым из них обратиться к серверу на localhost:5050
 * Каждый клиент будет отправлять одно сообщение в миллисекунду на сервер, и ждать пока оно вернется обратно.
 * После отправки 5000 сообщений клиент отправит на сервер "Bue.",
 *  по этому сигналу сервер должен разорвать соединение с сокетом.
 * После того как все клиенты отработают, тестирующая система оценит время, которое ушло на проверку.
 * Общее время работы должно быть менее 10 секунд.
 * 
 */
public class Main {
    private static final long TIME_TO_WORK = TimeUnit.SECONDS.toMillis(50);
    private static final int CLIENTS_NUMBER = 10;

    public static void main(String[] args) throws Exception {
        int port = 5050;
        ExecutorService executor = Executors.newFixedThreadPool(CLIENTS_NUMBER);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started");//.. on port: " + port);
            long startTime = new Date().getTime();
            while (new Date().getTime() < startTime + TIME_TO_WORK) {
                executor.submit(new MirrorSocketRunnable(serverSocket.accept()));
            }
        }
        executor.awaitTermination(1, TimeUnit.MINUTES);
        executor.shutdown();
        System.out.println("Bye.");
    }

    private static class MirrorSocketRunnable implements Runnable {
        private final Socket clientSocket;

        private MirrorSocketRunnable(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                String inputLine;
                String outputLine;
                int lineIndex = 0;
                while ((inputLine = in.readLine()) != null) {
                    //System.out.println("Message from client: " + inputLine);
                    outputLine = inputLine;
                    out.println(outputLine);
                    ++lineIndex;
                    if (outputLine.equals("Bye."))
                        break;
                }
                System.out.println("Lines processed: " + lineIndex);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                System.exit(1);
            }
        }
    }
}
