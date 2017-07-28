import java.util.ArrayList;

public class Game {
    private ArrayList<ClientInfo> clientInfoList;
    private Board board;
    private TileRandom tr;
    private int passes;
    private boolean gameRunning;
    private ScrabbleDao sd;
    public Game(ArrayList<ClientInfo> clientInfoList){
        this.clientInfoList = clientInfoList;
        board = new Board();
        System.out.println("tr init");
        tr = new TileRandom();
        getPlayers(clientInfoList);
        sd = ScrabbleDao.getInstantDao();
    }

    public void startSQL() throws InterruptedException {
        clientInfoList.get(1).sendCommandToClient("Print~Please wait for the other player...\r\n", 2);
        initProfile(clientInfoList.get(0));
        clientInfoList.get(0).sendCommandToClient("Print~Please wait for the other player...\r\n", 2);
        initProfile(clientInfoList.get(1));
    }

    public void initProfile(ClientInfo ci) throws InterruptedException {
        String hasAccount = ci.sendRequestToClient("MakeChoice~true~Do you have an existing account?: ~yes~no", 1);
        if(hasAccount.equalsIgnoreCase("yes")){
            String name = ci.sendRequestToClient("GetInput~Enter your username: ", 1);
            int id = sd.findPlayer(name);
            if(id == 0){
                ci.sendCommandToClient("Print~Username not found. Try again.\r\n", 2);
                initProfile(ci);
            }else{
                ci.sendCommandToClient("Print~Username found.\r\n", 2);
                ci.setSql_id(id);
            }
        }else{
            String name = ci.sendRequestToClient("GetInput~Create a username: ", 1);
            boolean success = sd.createNewPlayer(name);
            if(success){
                int id = sd.findPlayer(name);
                if(id == 0){
                    ci.sendCommandToClient("Print~An error occurred. Please try again.\r\n", 2);
                    initProfile(ci);
                }else{
                    ci.sendCommandToClient("Print~Account creation successful.\r\n", 2);
                    ci.setSql_id(id);
                }
            }else {
                ci.sendCommandToClient("Print~This username is taken.\r\n", 2);
                initProfile(ci);
            }
        }
    }

    public void startGame() throws InterruptedException {
        passes = 0;
        gameRunning = true;
        doTurn(clientInfoList.get(0).getPlayer(), true);
        while(true) {
            doTurn(clientInfoList.get(1).getPlayer(), false);
            if(!gameRunning){
                break;
            }
            doTurn(clientInfoList.get(0).getPlayer(), false);
            if(!gameRunning){
                break;
            }
        }
        endGame();
    }

    public void endGame() throws InterruptedException {
        WordCalculator wc = new WordCalculator();
        if(clientInfoList.get(0).getPlayer().getHand().size() == 0){
            for(char c : clientInfoList.get(1).getPlayer().getHand()){
                clientInfoList.get(0).getPlayer().addPoints(wc.getLetterScore(c));
            }
        }else{
            for(char c : clientInfoList.get(0).getPlayer().getHand()){
                clientInfoList.get(0).getPlayer().addPoints(-wc.getLetterScore(c));
            }
        }
        if(clientInfoList.get(1).getPlayer().getHand().size() == 0){
            for(char c : clientInfoList.get(0).getPlayer().getHand()){
                clientInfoList.get(1).getPlayer().addPoints(wc.getLetterScore(c));
            }
        }else{
            for(char c : clientInfoList.get(1).getPlayer().getHand()){
                clientInfoList.get(1).getPlayer().addPoints(-wc.getLetterScore(c));
            }
        }
        clientInfoList.get(0).sendCommandToClient("Print~The game has ended. Score: " + clientInfoList.get(0).getPlayer().getPoints() + " : " + clientInfoList.get(1).getPlayer().getPoints() + "\r\n", 2);
        clientInfoList.get(1).sendCommandToClient("Print~The game has ended. Score: " + clientInfoList.get(1).getPlayer().getPoints() + " : " + clientInfoList.get(0).getPlayer().getPoints() + "\r\n", 2);
        sd.insertScore(clientInfoList.get(0).getSql_id(), clientInfoList.get(1).getSql_id(), clientInfoList.get(0).getPlayer().getPoints(), clientInfoList.get(1).getPlayer().getPoints());
        sd.close();
    }

    public synchronized void getPlayers(ArrayList<ClientInfo> ClientInfoList){
        for(ClientInfo clientInfo : ClientInfoList){
            clientInfo.initPlayer(this, board);
        }
    }

    public Board getBoard(){
        return board;
    }

    public TileRandom getTr() {
        return tr;
    }

    private void doTurn(Player player, boolean firstTurn) throws InterruptedException {
        String action = player.turn(firstTurn, passes);
        if(action.equals("pass")){
            passes++;
        }else{
            passes = 0;
        }
        if(passes == 4){
            gameRunning = false;
        }
        else if(player.getHand().size() == 0 && tr.getBagSize() + clientInfoList.get(1 - player.getcInfo().getPlayerNumber()).getPlayer().getHand().size() <= 7){
            gameRunning = false;
        }
    }
}