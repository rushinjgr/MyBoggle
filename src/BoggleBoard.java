import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

/**
 * Created by wille on 9/4/14.
 */
public class BoggleBoard {

    private char[][] board2d;
    public boolean ast;

    public BoggleBoard() {
        this.board2d = new char[4][4];
    }

    //sets every character of the board to "0"
    //used to keep track of used characters
    public void clearBoard() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                board2d[i][j] = '0';
            }
        }
    }

    ///Generates a new board, gets new random letters
    public void newBoard(){
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                board2d[i][j] = randomChar();
            }
        }
    }

    //Returns the character at the specified position in the board.
    //Ex to get the bottom right corner:
    //getChar(3,3);
    public char getChar(int x, int y){
        return board2d[x][y];
    }

    public void setChar(int x, int y, char z){
        board2d[x][y] = z;
    }

    //Returns a random alphanumerical character.
    public char randomChar(){
        char result='0';
        Random r = new Random();
        result = (char)(r.nextInt(26) + 'a');
        return result;
    }


    public void printBoard(){
        System.out.println("__________");
        for (int i = 0; i < 4; i++) {
        System.out.print("|");
            for (int j = 0; j < 4; j++) {
                System.out.print(board2d[i][j]);
                System.out.print(" ");
            }
            System.out.print("|\n");
        }
        System.out.println("----------");
    }

    public void loadBoard(char[] input_board){
        int i = 0;
        ast = false;
        char curr;
        for (int j = 0; j < 4; j++) {
            for (int k = 0; k < 4; k++) {
                curr = input_board[i];
                if(curr == '*'){
                    ast = true;
                }else{
                }
                board2d[j][k] = curr;
                i++;
            }
        }
    }

    public void loadBoardFromTxt(String URI){
        String board = null;
        File file = new File(URI); //for ex foo.txt
        try {
            FileReader reader = new FileReader(file);
            char letters[] = new char[((int) file.length())];
            reader.read(letters);
            loadBoard(letters);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
