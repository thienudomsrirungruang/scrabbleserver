import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class PlayerFinder extends Thread {
    private ServerDispatcher sd;
    private ServerSocket port;
    private ArrayList<ClientInfo> clientInfos;
    private int playerNumber;
    public PlayerFinder(ServerDispatcher serverDispatcher, ServerSocket port, ArrayList<ClientInfo> clientInfos) {
        this.sd = serverDispatcher;
        this.port = port;
        this.clientInfos = clientInfos;
    }
    public synchronized void run(){
        try{
            playerNumber = 0;
            System.out.println("Now finding players...");
            while(sd.shouldBeOpen()){
                Socket socket = port.accept();
                System.out.print("Someone connected! Port: ");
                System.out.println(socket.getInetAddress());
                ClientInfo cInfo = new ClientInfo(sd, playerNumber);
                cInfo.setSocket(socket);
                ClientListener cListener = new ClientListener(cInfo, sd);
                ClientSender cSender = new ClientSender(cInfo, sd, new PrintWriter(new OutputStreamWriter(cInfo.getSocket().getOutputStream())));
                cInfo.setClientListener(cListener);
                cInfo.setClientSender(cSender);
                cListener.start();
                cSender.start();
                Thread.sleep(10);
                clientInfos.add(cInfo);
                playerNumber++;
            }
        }catch(IOException e){
            System.out.println("Lost connection");
        }catch(InterruptedException e){
            System.out.println("Interrupted");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
