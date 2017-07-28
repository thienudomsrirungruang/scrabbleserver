import java.util.ArrayList;

public class WordPlacer {
    private WordCalculator wc;
    private Board board;
    private Player player;
    private ScrabbleDao sd;
    private WordScorer ws;

    public WordPlacer(Player player, ScrabbleDao sd){
        wc = new WordCalculator();
        ws = new WordScorer(player, wc);
        this.player = player;
        this.sd = sd;
    }

    public boolean checkWordPlacement(String formattedWord, boolean firstTurn) throws InterruptedException {
        if (wc.getWord(formattedWord).length() > 15) {
            player.getcInfo().sendCommandToClient("Print~The word is too long to fit on the board.\r\n", 2);
            return false;
        }
        if(wc.parseWord(formattedWord) == null) {
            player.getcInfo().sendCommandToClient("Print~This is not a valid format. Type \"help\" for more info.\r\n", 2);
            return false;
        }
        if (!sd.isExist(wc.getWord(formattedWord))) {
            player.getcInfo().sendCommandToClient("Print~This is not a valid word.\r\n", 2);
            return false;
        }
        if (!hasTiles(formattedWord)) {
            player.getcInfo().sendCommandToClient("Print~You don't have the letters to make this word.\r\n", 2);
            return false;
        }
        char[] letters = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O'};
        int successes = 0;
        String position = "";
        String direction = "";
        for(int i = 0; i < 15; i++){
            for(int j = 0; j < 15; j++){
                if(checkWordPlacement(formattedWord, Character.toString(letters[j]) + i, "right", firstTurn, false, false, false)){
                    successes++;
                    position = Character.toString(letters[j]) + i;
                    direction = "right";
                }
                if(checkWordPlacement(formattedWord, Character.toString(letters[j]) + i, "down", firstTurn, false, false, false)){
                    successes++;
                    position = Character.toString(letters[j]) + i;
                    direction = "down";
                }
            }
        }
        switch(successes){
            case 0:
                player.getcInfo().sendCommandToClient("Print~There are no valid positions which match the format you entered.\r\n", 2);
                return false;
            case 1:
                checkWordPlacement(formattedWord, position, direction, firstTurn, true, true, false);
                return true;
            default:
                player.getcInfo().sendCommandToClient("Print~There are " + successes + " valid positions which match the format you entered. Please narrow your search by specifying your position (and direction if needed).\r\n", 2);
                return false;
        }
    }

    public boolean checkWordPlacement(String formattedWord, String position, boolean firstTurn) throws InterruptedException{
        int[] xyPos = parsePosition(position);
        if(xyPos == null) {
            player.getcInfo().sendCommandToClient("Print~The position string is not valid. Please type a square from A1 to O15. \"help\" for more details.\r\n", 2);
            return false;
        }
        if (xyPos[0] < 0 || xyPos[0] > 14 || xyPos[1] < 0 || xyPos[1] > 14) {
            player.getcInfo().sendCommandToClient("Print~Invalid position. Please enter a position between A1 and O15.\r\n", 2);
            return false;
        }
        if(xyPos[0] + wc.getWord(formattedWord).length() > 15 && xyPos[1] + wc.getWord(formattedWord).length() > 15){
            player.getcInfo().sendCommandToClient("Print~The word is too long to fit on the board.\r\n", 2);
            return false;
        }
        if(wc.parseWord(formattedWord) == null) {
            player.getcInfo().sendCommandToClient("Print~This is not a valid format. Type \"help\" for more info.\r\n", 2);
            return false;
        }
        if (!sd.isExist(wc.getWord(formattedWord))) {
            player.getcInfo().sendCommandToClient("Print~This is not a valid word.\r\n", 2);
            return false;
        }
        if (!hasTiles(formattedWord)) {
            player.getcInfo().sendCommandToClient("Print~You don't have the letters to make this word.\r\n", 2);
            return false;
        }
        int successes = 0;
        String direction = "";
        if(checkWordPlacement(formattedWord, position, "right", firstTurn, false, false, false)){
            successes++;
            direction = "right";
        }
        if(checkWordPlacement(formattedWord, position, "down", firstTurn, false, false, false)){
            successes++;
            direction = "down";
        }
        switch(successes){
            case 0:
                player.getcInfo().sendCommandToClient("Print~There are no valid positions which match the format you entered.\r\n", 2);
                return false;
            case 1:
                checkWordPlacement(formattedWord, position, direction, firstTurn, true, true, false);
                return true;
            default:
                player.getcInfo().sendCommandToClient("Print~There are " + successes + " valid positions which match the format you entered. Please narrow your search by specifying your position (and direction if needed).\r\n", 2);
                return false;
        }
    }

    public boolean checkWordPlacement(String formattedWord, String position, String direction, boolean firstTurn, boolean sendMessages, boolean placeWord, boolean checkWord) throws InterruptedException {
        int[] xyPos = parsePosition(position);
        if (xyPos == null) {
            if (sendMessages) {
                player.getcInfo().sendCommandToClient("Print~The position string is not valid. Please type a square from A1 to O15. \"help\" for more details.\r\n", 2);
            }
            return false;
        }
        if (xyPos[0] < 0 || xyPos[0] > 14 || xyPos[1] < 0 || xyPos[1] > 14) {
            if (sendMessages) {
                player.getcInfo().sendCommandToClient("Print~Invalid position. Please enter a position between A1 and O15.\r\n", 2);
            }
            return false;
        }
        if (!direction.equals("down") && !direction.equals("right")) {
            if (sendMessages) {
                player.getcInfo().sendCommandToClient("Print~Please type \"down\" or \"right\" to specify the direction. \"help\" for more details.\r\n", 2);
            }
            return false;
        }
        if ((direction.equals("down") && xyPos[0] + wc.getWord(formattedWord).length() > 15) || (direction.equals("right") && xyPos[1] + wc.getWord(formattedWord).length() > 15)) {
            if (sendMessages) {
                player.getcInfo().sendCommandToClient("Print~The word is too long to fit on the board.\r\n", 2);
            }
            return false;
        }
        if (wc.parseWord(formattedWord) == null) {
            if (sendMessages) {
                player.getcInfo().sendCommandToClient("Print~This is not a valid format. Type \"help\" for more info.\r\n", 2);
            }
            return false;
        }
        if(checkWord) {
            if (!sd.isExist(wc.getWord(formattedWord))) {
                if (sendMessages) {
                    player.getcInfo().sendCommandToClient("Print~This is not a valid word.\r\n", 2);
                }
                return false;
            }
        }
        if (!hasTiles(formattedWord)) {
            if (sendMessages) {
                player.getcInfo().sendCommandToClient("Print~You don't have the letters to make this word.\r\n", 2);
            }
            return false;
        }
        if(!checkWordFormat(xyPos, formattedWord, direction)){
            if(sendMessages) {
                player.getcInfo().sendCommandToClient("Print~The already placed tiles don't match your format. \"help\" for more details.\r\n", 2);
            }
            return false;
        }
        if(!firstTurn && !connected(xyPos, wc.getWord(formattedWord).length(), direction)){
            if(sendMessages) {
                player.getcInfo().sendCommandToClient("Print~The word isn't connected to anything. Any word placed must connect to at least one other tile.\r\n", 2);
            }
            return false;
        }
        if(firstTurn && !throughCentre(xyPos, wc.getWord(formattedWord).length(), direction)){
            if(sendMessages) {
                player.getcInfo().sendCommandToClient("Print~The word must go through the centre on the first turn.\r\n", 2);
            }
            return false;
        }
        if(!checkWordSides(xyPos, wc.getWord(formattedWord), direction)){
            if(sendMessages) {
                player.getcInfo().sendCommandToClient("Print~The word seems to touch another word on the start or end. Please type the whole word formed.\r\n", 2);
            }
            return false;
        }
        if(!checkFormedWords(xyPos, wc.getWord(formattedWord), direction)) {
            if(sendMessages) {
                player.getcInfo().sendCommandToClient("Print~One or more of the other perpendicular formed words are invalid.\r\n", 2);
            }
            return false;
        }
        int lettersPutDown = placeWord(formattedWord, xyPos, direction, placeWord);
        if(lettersPutDown == 0){
            if(sendMessages) {
                player.getcInfo().sendCommandToClient("Print~You must put down at least one tile.\r\n", 2);
            }
            return false;
        }
        if(placeWord) {
            int pointsThisTurn = ws.getScore(formattedWord, xyPos, direction);
            if (lettersPutDown == 7) {
                pointsThisTurn += 50;
                player.getcInfo().sendCommandToClient("Print~7 letter BINGO! 50 extra points!\r\n", 2);
            }
            player.getcInfo().sendCommandToClient("Print~You place " + formattedWord + " with a score of " + pointsThisTurn + " points.\r\n", 2);
            player.getcInfo().sendCommandToOtherPlayer("Print~Your opponent places " + formattedWord + " with a score of " + pointsThisTurn + " points.\r\n", 2);
            player.addPoints(pointsThisTurn);
        }
        return true;
    }

    public int placeWord(String formattedWord, int[] xyPos, String direction, boolean placeWord){
        int lettersPutDown = 0;
        int xCheck = xyPos[0];
        int yCheck = xyPos[1];
        char[][] parsedWord = wc.parseWord(formattedWord);
        for(int i = 0; i < wc.getWord(formattedWord).length(); i++){
            switch(parsedWord[i][1]){
                case 'n':
                    if(placeWord) {
                        board.setSquare(xCheck, yCheck, parsedWord[i][0], false);
                        player.removeHand(parsedWord[i][0]);
                    }
                    lettersPutDown++;
                    break;
                case 'b':
                    if(placeWord) {
                        board.setSquare(xCheck, yCheck, parsedWord[i][0], true);
                        player.removeHand('▓');
                    }
                    lettersPutDown++;
                    break;
            }
            if(direction.equals("right")){
                yCheck++;
            }else{
                xCheck++;
            }
        }
        return lettersPutDown;
    }

    public boolean hasTiles(String formattedWord){
        char[][] parsedWord = wc.parseWord(formattedWord);
        ArrayList<Character> handCopy = new ArrayList<>();
        handCopy.addAll(player.getHand());
        for(char[] letterInfo : parsedWord){
            switch(letterInfo[1]){
                case 'n':
                    if(handCopy.contains(letterInfo[0])){
                        handCopy.remove(handCopy.indexOf(letterInfo[0]));
                    }else{
                        return false;
                    }
                    break;
                case 'b':
                    if(handCopy.contains('▓')){
                        handCopy.remove(handCopy.indexOf('▓'));
                    }else{
                        return false;
                    }
                    break;
                case 'p':
                    break;
            }
        }
        return true;
    }

    public void setBoard(Board board) {
        this.board = board;
        ws.setBoard(board);
    }


    private boolean connected(int[] xyPos, int length, String direction){
        int xCheck = xyPos[0];
        int yCheck = xyPos[1];
        for(int i = 0; i < length; i++){
            if(board.getBoard()[xCheck][yCheck] != ' '){
                return true;
            }
            if(xCheck != 0){
                if (board.getBoard()[xCheck - 1][yCheck] != ' '){
                    return true;
                }
            }
            if(xCheck != 14){
                if (board.getBoard()[xCheck + 1][yCheck] != ' '){
                    return true;
                }
            }
            if(yCheck != 0){
                if (board.getBoard()[xCheck][yCheck - 1] != ' '){
                    return true;
                }
            }
            if(yCheck != 14){
                if (board.getBoard()[xCheck][yCheck + 1] != ' '){
                    return true;
                }
            }
            if(direction.equals("right")){
                yCheck++;
            }else{
                xCheck++;
            }
        }
        return false;
    }

    private boolean throughCentre(int[] xyPos, int length, String direction) {
        int xCheck = xyPos[0];
        int yCheck = xyPos[1];
        for(int i = 0; i < length; i++){
            if(xCheck == 7 && yCheck == 7){
                return true;
            }
            if(direction.equals("right")){
                yCheck++;
            }else{
                xCheck++;
            }
        }
        return false;
    }

    private int[] parsePosition(String position){
        try {
            int number = Integer.parseInt(position.substring(1));
            char letter = position.charAt(0);
            char[] letters = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O'};
            ArrayList<Character> letterArrayList = new ArrayList<>();
            for(char c : letters){
                letterArrayList.add(c);
            }
            if(letterArrayList.contains(letter)) {
                return new int[]{number - 1, letterArrayList.indexOf(letter)};
            }else{return null;}
        }
        catch(NumberFormatException e){
            return null;
        }
    }

    private boolean checkFormedWords(int[] xyPos, String word, String direction){
        int xCheck = xyPos[0];
        int yCheck = xyPos[1];
        for(int i = 0; i < word.length(); i++){
            //check formed words perpendicular to the selected square
            if(direction.equals("right")){
                //change in first index
                //search down
                int upperXLimit = xCheck;
                while(true){
                    if(upperXLimit < 14){
                        if(board.getBoard()[upperXLimit + 1][yCheck] != ' '){
                            upperXLimit++;
                        }else{
                            break;
                        }
                    }else{
                        break;
                    }
                }
                //search up
                int lowerXLimit = xCheck;
                while(true){
                    if(lowerXLimit > 0){
                        if(board.getBoard()[lowerXLimit - 1][yCheck] != ' '){
                            lowerXLimit--;
                        }else{
                            break;
                        }
                    }else{
                        break;
                    }
                }
                //form word if more than one letter
                if(lowerXLimit != upperXLimit) {
                    StringBuilder perpendicularWord = new StringBuilder();
                    for (int j = lowerXLimit; j <= upperXLimit; j++) {
                        if(j == xCheck){
                            perpendicularWord.append(word.charAt(i));
                        }else{
                            perpendicularWord.append(board.getBoard()[j][yCheck]);
                        }
                    }
                    if(!sd.isExist(perpendicularWord.toString())){
                        return false;
                    }
                }

                yCheck++;
            }else{
                //change in second index
                //search right
                int upperYLimit = yCheck;
                while(true){
                    if(upperYLimit < 14){
                        if(board.getBoard()[xCheck][upperYLimit + 1] != ' '){
                            upperYLimit++;
                        }else{
                            break;
                        }
                    }else{
                        break;
                    }
                }
                //search up
                int lowerYLimit = yCheck;
                while(true){
                    if(lowerYLimit > 0){
                        if(board.getBoard()[xCheck][lowerYLimit - 1] != ' '){
                            lowerYLimit--;
                        }else{
                            break;
                        }
                    }else{
                        break;
                    }
                }
                //form word if more than one letter
                if(lowerYLimit != upperYLimit) {
                    StringBuilder perpendicularWord = new StringBuilder();
                    for (int j = lowerYLimit; j <= upperYLimit; j++) {
                        if(j == yCheck){
                            perpendicularWord.append(word.charAt(i));
                        }else{
                            perpendicularWord.append(board.getBoard()[xCheck][j]);
                        }
                    }
                    if(!sd.isExist(perpendicularWord.toString())){
                        return false;
                    }
                }
                xCheck++;
            }
        }
        return true;
    }

    private boolean checkWordSides(int[] xyPos, String word, String direction){
        int xCheck = xyPos[0];
        int yCheck = xyPos[1];
        for(int i = 0; i < word.length(); i++){
            if(direction.equals("right")){
                if(yCheck != 0) {
                    if (!(board.getBoard()[xCheck][yCheck - 1] == ' ')) {
                        return false;
                    }
                }
                if(yCheck + word.length() != 15){
                    if(!(board.getBoard()[xCheck][yCheck + word.length()] == ' ')){
                        return false;
                    }
                }
            }else{
                if(xCheck != 0) {
                    if (!(board.getBoard()[xCheck - 1][yCheck] == ' ')) {
                        return false;
                    }
                }if(xCheck + word.length() != 15){
                    if(!(board.getBoard()[xCheck + word.length()][yCheck] == ' ')){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean checkWordFormat(int[] xyPos, String formattedWord, String direction){
        char[][] parsedWord = wc.parseWord(formattedWord);
        int xCheck = xyPos[0];
        int yCheck = xyPos[1];
        for(int i = 0; i < wc.getWord(formattedWord).length(); i++){
            if(parsedWord[i][1] == 'p' && board.getBoard()[xCheck][yCheck] != parsedWord[i][0]){
                return false;
            }
            else if(parsedWord[i][1] != 'p' && board.getBoard()[xCheck][yCheck] != ' '){
                return false;
            }
            if(direction.equals("right")){
                yCheck++;
            }else{
                xCheck++;
            }
        }
        return true;
    }
}