public class WordScorer {
    private Player player;
    private WordCalculator wc;
    private Board board;

    public WordScorer(Player player, WordCalculator wc){
        this.player = player;
        this.wc = wc;
    }

    public int getScore(String formattedWord, int[] xyPos, String direction){
        int score = 0;
        char[][] parsedWord = wc.parseWord(formattedWord);
        score += getMainScore(formattedWord, xyPos, direction);
        int xCheck = xyPos[0];
        int yCheck = xyPos[1];
        for(int i = 0; i < wc.getWord(formattedWord).length(); i++){
            if(parsedWord[i][1] != 'p') {
                score += getSecondaryScore(wc.getWord(formattedWord).charAt(i), xCheck, yCheck, direction);
            }
            if(direction.equals("right")){
                yCheck++;
            }else{
                xCheck++;
            }
        }
        return score;
    }

    private int getMainScore(String formattedWord, int[] xyPos, String direction){
        int xCheck = xyPos[0];
        int yCheck = xyPos[1];
        int score = 0;
        int wordMultiplier = 1;
        char[][] parsedWord = wc.parseWord(formattedWord);
        for(int i = 0; i < wc.getWord(formattedWord).length(); i++){
            switch(parsedWord[i][1]){
                case 'n':
                    switch(board.getSpecials()[xCheck][yCheck]){
                        case "3W":
                            wordMultiplier *= 3;
                            score += wc.getLetterScore(parsedWord[i][0]);
                            break;
                        case "2W":
                            wordMultiplier *= 2;
                            score += wc.getLetterScore(parsedWord[i][0]);
                            break;
                        case "3L":
                            score += wc.getLetterScore(parsedWord[i][0]) * 3;
                            break;
                        case "2L":
                            score += wc.getLetterScore(parsedWord[i][0]) * 2;
                            break;
                        default:
                            score += wc.getLetterScore(parsedWord[i][0]);
                            break;
                    }
                    break;
                case 'p':
                    score += wc.getLetterScore(parsedWord[i][0]);
                    break;
            }
            if(direction.equals("right")){
                yCheck++;
            }else{
                xCheck++;
            }
        }
        score *= wordMultiplier;
        return score;
    }

    private int getSecondaryScore(char letterToReplace, final int letterXPos, final int letterYPos, String directionOfMain){
        if(directionOfMain.equals("right")){
            //change in first index
            //search down
            int upperXLimit = letterXPos;
            while(true){
                if(upperXLimit < 14){
                    if(board.getBoard()[upperXLimit + 1][letterYPos] != ' '){
                        upperXLimit++;
                    }else{
                        break;
                    }
                }else{
                    break;
                }
            }
            //search up
            int lowerXLimit = letterXPos;
            while(true){
                if(lowerXLimit > 0){
                    if(board.getBoard()[lowerXLimit - 1][letterYPos] != ' '){
                        lowerXLimit--;
                    }else{
                        break;
                    }
                }else{
                    break;
                }
            }
            //get score if more than one letter
            if(lowerXLimit != upperXLimit) {
                int score = 0;
                int wordMultiplier = 1;
                for (int j = lowerXLimit; j <= upperXLimit; j++) {
                    if(j == letterXPos){
                        switch(board.getSpecials()[letterXPos][letterYPos]){
                            case "3W":
                                wordMultiplier *= 3;
                                score += wc.getLetterScore(letterToReplace);
                                break;
                            case "2W":
                                wordMultiplier *= 2;
                                score += wc.getLetterScore(letterToReplace);
                                break;
                            case "3L":
                                score += wc.getLetterScore(letterToReplace) * 3;
                                break;
                            case "2L":
                                score += wc.getLetterScore(letterToReplace) * 2;
                                break;
                            default:
                                score += wc.getLetterScore(letterToReplace);
                                break;
                        }
                    }else{
                        score += wc.getLetterScore(board.getBoard()[j][letterYPos]);
                    }
                }
                score *= wordMultiplier;
                return score;
            }else{
                return 0;
            }
        }else{
            //change in second index
            //search right
            int upperYLimit = letterYPos;
            while(true){
                if(upperYLimit < 14){
                    if(board.getBoard()[letterXPos][upperYLimit + 1] != ' '){
                        upperYLimit++;
                    }else{
                        break;
                    }
                }else{
                    break;
                }
            }
            //search up
            int lowerYLimit = letterYPos;
            while(true){
                if(lowerYLimit > 0){
                    if(board.getBoard()[letterXPos][lowerYLimit - 1] != ' '){
                        lowerYLimit--;
                    }else{
                        break;
                    }
                }else{
                    break;
                }
            }
            //get score if more than one letter
            if(lowerYLimit != upperYLimit) {
                int score = 0;
                int wordMultiplier = 1;
                for (int j = lowerYLimit; j <= upperYLimit; j++) {
                    if(j == letterYPos){
                        switch(board.getSpecials()[letterXPos][letterYPos]){
                            case "3W":
                                wordMultiplier *= 3;
                                score += wc.getLetterScore(letterToReplace);
                                break;
                            case "2W":
                                wordMultiplier *= 2;
                                score += wc.getLetterScore(letterToReplace);
                                break;
                            case "3L":
                                score += wc.getLetterScore(letterToReplace) * 3;
                                break;
                            case "2L":
                                score += wc.getLetterScore(letterToReplace) * 2;
                                break;
                            default:
                                score += wc.getLetterScore(letterToReplace);
                                break;
                        }
                    }else{
                        score += wc.getLetterScore(board.getBoard()[letterXPos][j]);
                    }
                }
                score *= wordMultiplier;
                return score;
            }else{
                return 0;
            }
        }
    }

    public void setBoard(Board board) {
        this.board = board;
    }

}