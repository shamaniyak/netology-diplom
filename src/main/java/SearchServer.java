import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SearchServer {
    private final int port;
    private final BooleanSearchEngine engine;

    public SearchServer(int port, BooleanSearchEngine engine) {
        this.port = port;
        this.engine = engine;
    }

    public void start() throws IOException {
        System.out.println("Starting server at " + port + "...");
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                try (Socket socket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
                ) {
                    out.println("Введите поисковый запрос.");
                    // прочитать поисковый запрос
                    String message = in.readLine();
                    //System.out.println(message);
                    // запустить поиск
                    var result = engine.search(message);
                    // Создаем объект результата поиска, передаем ему список найденных страниц.
                    // У SearchResult мы написали функцию toJson, поэтому мы его здесь используем.
                    var searchResult = new SearchResult(result);
                    //Возвращаем результат клиенту в формате JSON
                    out.println(searchResult.toJson());
                } catch (IOException e) {
                    System.out.println("Не могу стартовать сервер");
                    e.printStackTrace();
                    break;
                }
            }
        }
    }
}
