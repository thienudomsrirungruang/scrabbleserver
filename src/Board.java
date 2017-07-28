public class Board {
    /*
    Board Notation:
    Letters - Columns - Second Index
    Numbers - Rows - First Index
    e.g. [1][2] -> B1

    Specials:
    "3W" - Triple Word
    "2W" - Double Word
    "3L" - Triple Letter
    "2L" - Double Letter
    "" - Normal
     */
    private char[][] board;
    private String[][] specials;
    private boolean[][] blanks;
    public Board(){
        board = new char[15][15];
        blanks = new boolean[15][15];
        for(int i = 0; i < 15; i++){for(int j = 0; j < 15; j++){
            board[i][j] = ' ';
            blanks[i][j] = false;
        }}
        specials = new String[][]
                {{"3W","","","2L","","","","3W","","","","2L","","","3W"},
                 {"","2W","","","","3L","","","","3L","","","","2W",""},
                 {"","","2W","","","","2L","","2L","","","","2W","",""},
                 {"2L","","","2W","","","","2L","","","","2W","","","2L"},
                 {"","","","","2W","","","","","","2W","","","",""},
                 {"","3L","","","","3L","","","","3L","","","","3L",""},
                 {"","","2L","","","","2L","","2L","","","","2L","",""},
                 {"3W","","","2L","","","","2W","","","","2L","","","3W"},
                 {"","","2L","","","","2L","","2L","","","","2L","",""},
                 {"","3L","","","","3L","","","","3L","","","","3L",""},
                 {"","","","","2W","","","","","","2W","","","",""},
                 {"2L","","","2W","","","","2L","","","","2W","","","2L"},
                 {"","","2W","","","","2L","","2L","","","","2W","",""},
                 {"","2W","","","","3L","","","","3L","","","","2W",""},
                 {"3W","","","2L","","","","3W","","","","2L","","","3W"}};
    }
    public void printBoard(Player player) throws InterruptedException {
        StringBuilder boardString = new StringBuilder();
        boardString.append("   A B C D E F G H I J K L M N O\r\n");
        for(int i = 0; i < 15; i++){
            boardString.append((char)27).append("[0m");
            if(i < 9){
                boardString.append(" ");
            }
            boardString.append(i + 1);
            for(int j = 0; j < 15; j++){
                boardString.append((char)27).append("[0m ");
                switch(specials[i][j]){
                    case "3W":
                        boardString.append((char)27).append("[41;36m");
                        break;
                    case "2W":
                        boardString.append((char)27).append("[45;32m");
                        break;
                    case "3L":
                        boardString.append((char)27).append("[44;37m");
                        break;
                    case "2L":
                        boardString.append((char)27).append("[46m");
                        break;
                }
                if(blanks[i][j]){
                    boardString.append((char)27).append("[4;1m");
                }
                if(board[i][j] == ' '){
                    boardString.append("-");
                }else{
                    boardString.append(board[i][j]);
                }
            }
            boardString.append((char)27).append("[0m ").append(i + 1).append("\r\n");
        }
        boardString.append("   A B C D E F G H I J K L M N O\r\n");
        player.getcInfo().sendCommandToClient("Print~" + boardString.toString(), 18);
    }
    public void setSquare(int row, int col, char letter, boolean blank){
        board[row][col] = letter;
        blanks[row][col] = blank;
    }

    public char[][] getBoard() {
        return board;
    }

    public String[][] getSpecials() {
        return specials;
    }

    public boolean[][] getBlanks() {
        return blanks;
    }
}
