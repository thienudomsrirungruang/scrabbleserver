import java.net.Socket;

public class ClientInfo {
    private Socket socket;
    private ClientListener cListener;
    private ClientSender cSender;
    private Player player;
    private ServerDispatcher sd;
    private int playerNumber;
    private int sql_id;
    public ClientInfo(ServerDispatcher sd, int playerNumber){
        this.sd = sd;
        player = new Player();
        player.setcInfo(this);
        this.playerNumber = playerNumber;
    }
    public void initPlayer(Game game, Board board){
        player.setGame(game);
        player.initWordPlacer();
    }
    public Socket getSocket() {
        return socket;
    }
    public void setSocket(Socket socket) {
        this.socket = socket;
    }
    public void setClientListener(ClientListener cListener) {
        this.cListener = cListener;
    }
    public ClientSender getClientSender() {
        return cSender;
    }
    public void setClientSender(ClientSender cSender) {
        this.cSender = cSender;
    }
    public Player getPlayer(){
        return player;
    }
    public String getNextMessage() throws InterruptedException{
        while(true){
            Thread.sleep(50);
            if(cListener.getMessages().size() > 0){
                break;
            }
        }
        String output = cListener.getMessages().get(cListener.getMessages().size() - 1);
        cListener.getMessages().remove(cListener.getMessages().size() - 1);
        return output;
    }
    public synchronized String sendRequestToClient(String request, int lines) throws InterruptedException{
        cSender.sendMessage(lines + "\r\nRequest~" + request);
        return getNextMessage();
    }
    public synchronized void sendCommandToClient(String command, int lines) throws InterruptedException{
        cSender.sendMessage(lines + "\r\nCommand~" + command);
    }
    public synchronized String sendRequestToOtherPlayer(String request, int lines) throws InterruptedException{
        return sd.getClientInfoList().get(1 - playerNumber).sendRequestToClient(request, lines);
    }
    public synchronized void sendCommandToOtherPlayer(String command, int lines) throws InterruptedException{
        sd.getClientInfoList().get(1 - playerNumber).sendCommandToClient(command, lines);
    }

    public ServerDispatcher getSd() {
        return sd;
    }
    public int getPlayerNumber(){
        return playerNumber;
    }
    public void setSql_id(int sql_id){
        this.sql_id = sql_id;
    }
    public int getSql_id(){
        return sql_id;
    }
}
