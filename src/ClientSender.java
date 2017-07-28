import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;

public class ClientSender extends Thread{
    private LinkedList messages = new LinkedList<String>();
    private PrintWriter output;
    private ClientInfo cInfo;
    private ServerDispatcher sd;

    public ClientSender(ClientInfo cInfo, ServerDispatcher sd, PrintWriter pw) throws IOException {
        this.cInfo = cInfo;
        this.sd = sd;
        output = pw;
    }
    public void run() {
        try{
            while(!isInterrupted()){
                String message = getNextMessageFromQueue();
                sendMessageToClient(message);
            }
        }catch(IOException e){
            System.out.println("Disconnected");
        }catch(InterruptedException e){
            System.out.println("Interrupted");
        }
    }
    private synchronized String getNextMessageFromQueue() throws IOException, InterruptedException{
        while(messages.size() == 0){
            wait();
        }
        String message = (String) messages.remove(0);
        return message;
    }
    public synchronized void sendMessage(String message){
        messages.add(message);
        notify();
    }
    private synchronized void sendMessageToClient(String message){
        output.println(message);
        output.flush();
    }
}