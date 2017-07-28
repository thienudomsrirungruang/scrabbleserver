import java.util.ArrayList;

public class WordCalculator {
    public int getWordScore(String word){
        char[][] parsedWord = parseWord(word);
        int score = 0;
        if(parsedWord == null){
            return -1;
        }
        for(char[] letterInfo : parsedWord){
            if(letterInfo[1] != 'b'){
                score += getLetterScore(letterInfo[0]);
            }
        }
        return score;
    }
    public char[][] parseWord(String formattedWord){
        /*
        Output Structure:
        char[formattedWord.length()][2] {{letters, status},...}
            char letters: letters in the word (uppercase)
            char status: corresponds to letter with the same index in letters
                Values:
                'n': not blank
                'b': blank
                'p': already placed

        Returns null when the word isn't in the right format.
        */

        char[][] output = new char[lettersInWord(formattedWord)][2];
        boolean bracket = false;
        int letterPosition = 0;
        for(int i = 0; i < formattedWord.length(); i++){
            switch(formattedWord.charAt(i)){
                case '(':
                    if(bracket){
                        return null;
                    }else{
                        bracket = true;
                    }
                    break;
                case ')':
                    if(!bracket){
                        return null;
                    }else{
                        bracket = false;
                    }
                    break;
                default:
                    if(bracket) {
                        output[letterPosition][0] = Character.toUpperCase(formattedWord.charAt(i));
                        output[letterPosition][1] = 'p';
                        letterPosition++;
                    }else {
                        boolean[] letterInfo = letterCheck(formattedWord.charAt(i));
                        if (!letterInfo[0]) {
                            return null;
                        }
                        if (letterInfo[1]) {
                            output[letterPosition][0] = Character.toUpperCase(formattedWord.charAt(i));
                            output[letterPosition][1] = 'n';
                            letterPosition++;
                        }else{
                            output[letterPosition][0] = Character.toUpperCase(formattedWord.charAt(i));
                            output[letterPosition][1] = 'b';
                            letterPosition++;
                        }
                    }
            }
        }
        return output;
    }
    public String getWord(String formattedWord){
        StringBuilder output = new StringBuilder();
        for(int i = 0; i < formattedWord.length(); i++){
            if(formattedWord.charAt(i) != '(' && formattedWord.charAt(i) != ')'){
                output.append(Character.toString(formattedWord.charAt(i)).toUpperCase());
            }
        }
        return output.toString();
    }
    private boolean[] letterCheck(char letter){
        /*
        Output Structure:
        {<valid>, [uppercase]}
            char valid: Whether the letter is valid (uppercase or lowercase)
            char uppercase: Whether the letter is uppercase. Only if valid == true.
        */
        char[] uppercase = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        char[] lowercase = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        ArrayList<Character> uppercaseArrayList = new ArrayList<>();
        ArrayList<Character> lowercaseArrayList = new ArrayList<>();
        for(int i = 0; i < 26; i++){
            uppercaseArrayList.add(uppercase[i]);
            lowercaseArrayList.add(lowercase[i]);
        }
        if(uppercaseArrayList.contains(letter)){
            return new boolean[]{true, true};
        }
        else if(lowercaseArrayList.contains(letter)){
            return new boolean[]{true, false};
        }
        else{
            return new boolean[]{false};
        }
    }
    private int lettersInWord(String formattedWord){
        String[] parsedWord = formattedWord.split("[()]+");
        int letters = 0;
        for(String s : parsedWord){
            letters += s.length();
        }
        return letters;
    }



    public int getLetterScore(char letter){
        letter = Character.toString(letter).toUpperCase().charAt(0);
        switch(letter){
            case 'L':
            case 'S':
            case 'U':
            case 'N':
            case 'R':
            case 'T':
            case 'O':
            case 'A':
            case 'I':
            case 'E':
                return 1;
            case 'G':
            case 'D':
                return 2;
            case 'B':
            case 'C':
            case 'M':
            case 'P':
                return 3;
            case 'F':
            case 'H':
            case 'V':
            case 'W':
            case 'Y':
                return 4;
            case 'K':
                return 5;
            case 'J':
            case 'X':
                return 8;
            case 'Q':
            case 'Z':
                return 10;
            default:
                return 0;
        }
    }
    public String subscriptLetterFormat(char letter){
        return letter + intToSubscript(getLetterScore(letter));
    }
    private String intToSubscript(int number){
        char[] subscripts = {'₀','₁','₂','₃','₄','₅','₆','₇','₈','₉'};
        String numberAsString = Integer.toString(number);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numberAsString.length(); i++) {
            sb.append(subscripts[Integer.parseInt(Character.toString(numberAsString.charAt(i)))]);
        }
        return sb.toString();
    }
}
