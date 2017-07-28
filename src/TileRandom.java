import java.util.ArrayList;
import java.util.Random;

public class TileRandom {
    Random random = new Random();
    ArrayList<Character> bag;
    public TileRandom() {
        bag = new ArrayList<>();
        char[] chars = {'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'B', 'B', 'C', 'C', 'D', 'D', 'D', 'D', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'F', 'F', 'G', 'G', 'G', 'H', 'H', 'I', 'I', 'I', 'I', 'I', 'I', 'I', 'I', 'I', 'J', 'K', 'L', 'L', 'L', 'L', 'M', 'M', 'N', 'N', 'N', 'N', 'N', 'N', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'P', 'P', 'Q', 'R', 'R', 'R', 'R', 'R', 'R', 'S', 'S', 'S', 'S', 'T', 'T', 'T', 'T', 'T', 'T', 'U', 'U', 'U', 'U', 'V', 'V', 'W', 'W', 'X', 'Y', 'Y', 'Z', '▓', '▓'};
        for(char c : chars){
            bag.add(c);
        }
    }
    public char randomTile() {
        if(!bag.isEmpty()) {
            return bag.remove(random.nextInt(bag.size()));
        }
        return '1';
    }
    public int getBagSize(){
        return bag.size();
    }
    public char putInBag(char tile){
        bag.add(tile);
        return tile;
    }
}
