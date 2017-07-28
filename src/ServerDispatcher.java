import java.net.ServerSocket;
import java.util.ArrayList;

public class ServerDispatcher extends Thread {
    private ArrayList<ClientInfo> clientInfoList = new ArrayList<>();
    private ServerSocket port;
    private boolean open = true;
    private PlayerFinder pf;
    public ServerDispatcher(ServerSocket port) {
        this.port = port;
    }
    public void run(){
        pf = new PlayerFinder(this, port, clientInfoList);
        pf.start();
        while(clientInfoList.size() < 2) {
            try{
                Thread.sleep(200);
            }catch (InterruptedException e){
                System.out.print("Interrupted");
            }
        }
        System.out.println("Closed");
        open = false;
        System.out.println("game init");
        Game game = new Game(clientInfoList);
        try{
            game.startSQL();
            game.startGame();
//        }catch(IOException e){
//            System.out.println("Lost connection");
        }catch(InterruptedException e){
            System.out.println("Interrupted");
        }
    }
    public ArrayList<ClientInfo> getClientInfoList(){
        return clientInfoList;
    }
    public boolean shouldBeOpen(){
        return open;
    }
}