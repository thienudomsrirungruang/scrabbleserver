import java.io.IOException;
import java.net.ServerSocket;

public class ScrabbleServer {
    private final int PORT = 2468;
    private static ScrabbleServer instance = new ScrabbleServer();
    public static void main(String[] args) throws IOException {
        instance = new ScrabbleServer();
        instance.start();
    }
    private void start() throws IOException {
        ServerSocket port = new ServerSocket(instance.PORT);
        ServerDispatcher sd = new ServerDispatcher(port);
        sd.start();
    }
}
