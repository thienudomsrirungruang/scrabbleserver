import java.util.ArrayList;

public class Player {
    private ArrayList<Character> hand;
    private ClientInfo cInfo;
    private Game game;
    private ScrabbleDao sd;
    private WordCalculator wc;
    private WordPlacer wp;
    private int points;

    public Player(){
        hand = new ArrayList<>();
        sd = ScrabbleDao.getInstantDao();
        wc = new WordCalculator();
        wp = new WordPlacer(this, sd);
        points = 0;
    }

    public String turn(boolean firstTurn, int passes) throws InterruptedException {
        cInfo.sendCommandToClient("Print~\r\n\r\n-----Your turn-----\r\nScore: " + points + " : "
                + cInfo.getSd().getClientInfoList().get((1 - cInfo.getPlayerNumber())).getPlayer().getPoints()
                + "\r\n\r\n", 6);
        cInfo.sendCommandToOtherPlayer("Print~\r\n\r\n-----Opponent's turn-----\r\nScore: "
                + cInfo.getSd().getClientInfoList().get((1 - cInfo.getPlayerNumber())).getPlayer().getPoints() + " : " + points
                + "\r\n\r\n", 6);
        while(hand.size() < 7 && game.getTr().getBagSize() > 0){
            char tile = game.getTr().randomTile();
            hand.add(tile);
            cInfo.sendCommandToClient("Print~You draw " + wc.subscriptLetterFormat(tile) + " from the bag.\r\n", 2);
        }
        printBoard(hand);
        cInfo.getSd().getClientInfoList().get((1-cInfo.getPlayerNumber())).getPlayer().printBoard(cInfo.getSd().getClientInfoList().get((1-cInfo.getPlayerNumber())).getPlayer().getHand());
        if(passes == 2){
            cInfo.sendCommandToClient("Print~WARNING: If you pass this turn and your opponent passes theirs, the game will end.\r\n", 2);
        }
        else if(passes == 3){
            cInfo.sendCommandToClient("Print~WARNING: If you pass this turn, the game will end.\r\n", 2);
        }
        return enterWord(firstTurn);
    }

    private String enterWord(boolean firstTurn) throws InterruptedException {
        String command = cInfo.sendRequestToClient("GetInput~Input your word, check a word's validity and points or print the board and rack again. Type \"help\" for formats allowed.\r\n", 2);
        String[] parsedCommand = command.split("[ ]+");
        switch(parsedCommand[0]){
            case "help":
                cInfo.sendCommandToClient("Print~\r\nCommands:\r\n\r\n" +
                        "help: View this list\r\n\r\n" +
                        "word <formatted word> [position] [direction]:\r\nEnter a word in the format given. Fails when the word isn't valid, you don't have the tiles, no position is valid or more than one position is valid.\r\n" +
                        "    Formatted word:\r\n" +
                        "        Non-blank letters must be UPPERCASE\r\n" +
                        "        Blank letters must be lowercase\r\n" +
                        "        Letters in (brackets) are letters which are already placed (case insensitive)\r\n" +
                        "    Example: word S(C)r(AB)BLe B4 down\r\n" +
                        "    Position (Optional): A square from A1 to O15 where the top-leftmost letter will be placed (e.g. D8, H13, O14, B1)\r\n" +
                        "    Direction (Optional): \"right\" or \"down\" to specify the direction of the word\r\n\r\n" +
                        "check <word>: Check a word's validity and points\r\n" +
                        "    word: Word you want to check (UPPERCASE for non-blanks, lowercase for blanks, brackets for letters already on the board)\r\n\r\n" +
                        "exchange <letters>: Exchange specified tiles for new ones from the bag after putting them in.\r\n" +
                        "    letters: All letters you want to exchange. Use UPPERCASE and ? for blanks. No gaps.\r\n\r\n" +
                        "pass: Pass your turn.\r\n\r\n" +
                        "board: Print the board and your rack again\r\n\r\n", 25);
                return enterWord(firstTurn);
            case "word":
                if(parsedCommand.length > 1){
                    switch(parsedCommand.length){
                        case 2:
                            if(!wp.checkWordPlacement(parsedCommand[1], firstTurn)){
                                return enterWord(firstTurn);
                            }else{
                                return "place";
                            }
                        case 3:
                            if(!wp.checkWordPlacement(parsedCommand[1], parsedCommand[2], firstTurn)){
                                return enterWord(firstTurn);
                            }else{
                                return "place";
                            }
                        case 4:
                            if(!wp.checkWordPlacement(parsedCommand[1], parsedCommand[2], parsedCommand[3], firstTurn, true, true, true)){
                                return enterWord(firstTurn);
                            }else{
                                return "place";
                            }
                        default:
                            cInfo.sendCommandToClient("Print~Too many arguments. Type \"help\" for more info.\r\n", 2);
                            return enterWord(firstTurn);
                    }
                }else{
                    cInfo.sendCommandToClient("Print~Correct syntax: word <formatted word> [position] [direction]. Please type \"help\" for more info.\r\n", 2);
                    return enterWord(firstTurn);
                }
            case "check":
                if(parsedCommand.length > 1) {
                    if (sd.isExist(wc.getWord(parsedCommand[1]))) {
                        int points = wc.getWordScore(parsedCommand[1]);
                        if(points >= 0) {
                            cInfo.sendCommandToClient("Print~This is a valid word and has a score of " + points + ".\r\n", 2);
                        }else{
                            cInfo.sendCommandToClient("Print~This is not a valid format. Type \"help\" for more info.\r\n", 2);
                        }
                    } else {
                        cInfo.sendCommandToClient("Print~This is not a valid word.\r\n", 2);
                    }
                    return enterWord(firstTurn);
                }else{
                    cInfo.sendCommandToClient("Print~Correct syntax: check <letters>. Please type \"help\" for more info.\r\n", 2);
                    return enterWord(firstTurn);
                }
            case "board":
                printBoard(hand);
                return enterWord(firstTurn);
            case "exchange":
                if(parsedCommand.length > 1) {
                    if (!exchange(parsedCommand[1])) {
                        return enterWord(firstTurn);
                    } else {
                        return "exchange";
                    }
                }else{
                    cInfo.sendCommandToClient("Print~Correct syntax: exchange <word>. Please type \"help\" for more info.\r\n", 2);
                    return enterWord(firstTurn);
                }
            case "pass":
                cInfo.sendCommandToClient("Print~You pass your turn.\r\n", 2);
                cInfo.sendCommandToOtherPlayer("Print~Your opponent passes.\r\n", 2);
                return "pass";
            default:
                cInfo.sendCommandToClient("Print~This is not a valid command. Please type \"help\" for more info.\r\n", 2);
                return enterWord(firstTurn);
        }
    }

    private boolean exchange(String letters) throws InterruptedException {
        char[] letterArray = letters.toCharArray();
        for (int i = 0; i < letterArray.length; i++){
            if (letterArray[i] == '?') {
                letterArray[i] = '▓';
            }
        }
        ArrayList<Character> handCopy = new ArrayList<>();
        handCopy.addAll(hand);
        for (char c : letterArray) {
            if (hand.contains(c)) {
                handCopy.remove(handCopy.indexOf(c));
            } else {
                cInfo.sendCommandToClient("Print~One or more of your letters isn't on the rack.\r\n", 2);
                return false;
            }
        }
        if(game.getTr().getBagSize() > 7) {
            for (char c : letterArray) {
                hand.remove(hand.indexOf(game.getTr().putInBag(c)));
            }
            while (hand.size() < 7 && game.getTr().getBagSize() > 0) {
                char tile = game.getTr().randomTile();
                hand.add(tile);
                cInfo.sendCommandToClient("Print~You draw " + wc.subscriptLetterFormat(tile) + " from the bag.\r\n", 2);
            }
            cInfo.sendCommandToClient("Print~You have successfully exchanged " + letters.length() + " tiles.\r\n", 2);
            cInfo.sendCommandToOtherPlayer("Print~Your opponent exchanges " + letters.length() + " tiles.\r\n", 2);
            return true;
        }else{
            cInfo.sendCommandToClient("Print~You cannot exchange tiles when the bag has less than 7 tiles left.\r\n", 2);
            return false;
        }
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public ClientInfo getcInfo() {
        return cInfo;
    }

    public void setcInfo(ClientInfo cInfo) {
        this.cInfo = cInfo;
    }

    private void printBoard(ArrayList<Character> hand) throws InterruptedException {
        game.getBoard().printBoard(this);
        StringBuilder rack = new StringBuilder();
        for(char letter : hand){
            rack.append(wc.subscriptLetterFormat(letter) + " ");
        }
        cInfo.sendCommandToClient("Print~Rack: " + rack.toString() + "\r\nTiles in bag: " + game.getTr().getBagSize() + "\r\n", 3);
    }

    public void initWordPlacer() {
        wp.setBoard(game.getBoard());
    }

    public ArrayList<Character> getHand() {
        return hand;
    }

    public int getPoints(){
        return points;
    }
    public int addPoints(int pointsToAdd){
        return points +=  pointsToAdd;
    }
    public int setPoints(int newPoints){
        return points = newPoints;
    }
    public void removeHand(char c) {
        if(hand.contains(c)){
            hand.remove(hand.indexOf(c));
        }
    }
}