import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

public class ClientListener extends Thread{
    private ClientInfo cInfo;
    private BufferedReader br;
    private Socket socket;
    private ServerDispatcher sd;
    private ArrayList<String> messages = new ArrayList<String>();
    public ClientListener(ClientInfo cInfo, ServerDispatcher sd) throws IOException {
        socket = cInfo.getSocket();
        this.cInfo = cInfo;
        this.sd = sd;
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
    public void run(){
        try{
            while(!isInterrupted()){
                String message = br.readLine();
                if(message != null){
                    messages.add(message);
                }
            }
        }catch(IOException e){
            System.out.println("Lost connection");
        }
    }
    public ArrayList<String> getMessages(){
        return messages;
    }
}