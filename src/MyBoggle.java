import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

public class MyBoggle extends BoggleBoard {

    static final String dictionaryURI = "/home/wille/Documents/fall 2014/CS 1501/Projects/MyBoggle/src/dictionary.txt";

    static final char[] alphabet = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    static TreeSet<String> foundWords = new TreeSet<String>();

    public static void main(String args[]) {
        boolean board_specified = false;
        boolean dict_specified = false;
        boolean dlb = false;
        String boardURI = "";
        String arg;
        if (args.length == 0) {
            System.out.println("ERROR: NO INPUT PARAMETERS");
            return;
        }
        for (int i = 0; i < args.length; i++) {
            arg = args[i];
            //Board specified
            if ((arg.compareToIgnoreCase("-b") == 0) && (i < args.length)) {
                //Get the path specified for the board file
                i++;
                arg = args[i];
                boardURI = args[i];
                board_specified = true;
            }
            //Dictionary specified
            else if ((arg.compareToIgnoreCase("-d") == 0) && (i < args.length)) {
                i++;
                arg = args[i];
                dict_specified = true;
                if (arg.compareToIgnoreCase("simple") == 0) {
                    dlb = false;
                } else if (arg.compareToIgnoreCase("dlb") == 0) {
                    dlb = true;
                }
                //an invalid specification has been entered
                else {
                    dlb = false;
                }
            }
            //An invalid parameter has been entered
            else {
                System.out.println("ERROR: BAD INPUT PARAMETERS");
                return;
            }
        }

        //parameters have been processed; initialize accordingly
        DictionaryInterface dict;       //this is the main dictionary into which we will load words from the txt file
        DictionaryInterface gameDict;   //this is the game dictionary; when we search for words on the board they go here.
        if (dlb) {
            dict = new DLBDictionary();
            gameDict = new DLBDictionary();
        } else {
            dict = new SimpleDictionary();
            gameDict = new SimpleDictionary();
        }
        if (board_specified) {
            try {
                initialize(boardURI, dict, gameDict);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            //board not specified
        }
    }

    public static void initialize(String boardURI, DictionaryInterface dict, DictionaryInterface gameDict) throws FileNotFoundException {
        //scoring variables
        int guessed=0;
        int correct=0;
        int incorrect=0;
        int found=0;

        //load board
        BoggleBoard board = new BoggleBoard();
        board.loadBoardFromTxt(boardURI);
        board.printBoard();
        //load dictionary data
        populateFromTxt(dict, dictionaryURI);
        //exhaustive search board for words
        gameDict = exhaustiveSearch(board, dict, gameDict);
        //prompt the user to enter as many words as they can find in that board
        System.out.println("Enter as many words are you can find in the board.");
        System.out.println("Type the word and hit enter. Enter 0 when finished.");
        BufferedReader cli = new BufferedReader(new InputStreamReader(System.in));
        boolean cont = true;
        StringBuilder search = new StringBuilder("");
        TreeSet<String> correctWords = new TreeSet<String>();

        while (cont == true) {
            try {
                search.setLength(0);
                search.append(cli.readLine().toLowerCase());
                if (search.toString().compareToIgnoreCase("0") != 0) {
                    cont = true;
                    //For each word entered, you should inform the user whether or not the word they entered was valid.
                    if (gameDict.search(search) >= 2) {
                        if(correctWords.contains(search.toString())){
                            //already gussed
                            System.out.println("YOU ALREADY GUESSED THIS WORD");
                        }
                        else {
                            //correct word
                            System.out.println("VALID");
                            correct++;
                            guessed++;
                            correctWords.add(search.toString());
                        }
                    } else {
                        //incorrect word
                        System.out.println("INVALID");
                        incorrect++;
                        guessed++;
                    }
                } else {
                    cont = false;
                }
            } catch (IOException ioe) {
                System.out.println("IO error");
                System.exit(1);
            }
        }

        //user indicates they have finished entering words
        System.out.println("Possible words");
        for(String s:foundWords){
            System.out.println(s);
            found++;
        }
        System.out.println("Correct words:");
        for (String s: correctWords){
            System.out.println(s);
        }
        System.out.print("# of correct words: ");
        System.out.println(Integer.toString(correct));
        System.out.print("# of possible words: ");
        System.out.println(Integer.toString(found));
        //TODO round percentage
        DecimalFormat twoDForm = new DecimalFormat("##.##");
        System.out.print("% of possible words: ");
        double percent = ((double)correct / (double)found);
        percent = Math.round(percent * 100.0);
        System.out.print((twoDForm.format(percent)));
        foundWords.clear();
        found = 0;
    }

    public static DictionaryInterface exhaustiveSearch(BoggleBoard board, DictionaryInterface dict, DictionaryInterface gameDict) {
        StringBuilder word = new StringBuilder("");
        BoggleBoard used = new BoggleBoard(); //this will keep track of whether we have used a letter or not
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if(!(board.ast)) {
                    //~IMPORTANT~ used MUST be reset each time
                    used.clearBoard();
                    word.append(Character.toLowerCase(board.getChar(i, j)));
                    gameDict = exhaustiveSearch(i, j, board, dict, gameDict, used, word);
                    word.deleteCharAt(word.length()-1);
                }
                else {
                    if(board.getChar(i,j) != '*'){
                        //~IMPORTANT~ used MUST be reset each time
                        used.clearBoard();
                        word.append(Character.toLowerCase(board.getChar(i, j)));
                        gameDict = exhaustiveSearchAst(i, j, board, dict, gameDict, used, word);
                        word.deleteCharAt(word.length()-1);
                    }
                    else{
                       for(char c : alphabet){
                           board.setChar(i,j,c);
                           //~IMPORTANT~ used MUST be reset each time
                           used.clearBoard();
                           word.append(Character.toLowerCase(board.getChar(i, j)));
                           gameDict = exhaustiveSearchAst(i, j, board, dict, gameDict, used, word);
                           word.deleteCharAt(word.length()-1);
                       }
                        board.setChar(i,j,'*');
                    }
                }
            }
        }
        return gameDict;
    }

    public static DictionaryInterface exhaustiveSearchAst(int x, int y, BoggleBoard board, DictionaryInterface dict, DictionaryInterface gameDict, BoggleBoard used, StringBuilder word) {

        //the arrays we'll use for the the offset (search all the surrounding cells)
        int[] xoff = {-1, 0, 1, -1, 1, -1, 0, 1};
        int[] yoff = {-1, -1, -1, 0, 0, 1, 1, 1};
        //these will function as our new coordinates
        int newx;
        int newy;
        int status;
        char next;

        //get the current character, add it to the word
        used.setChar(x,y,'1');
        char current = board.getChar(x,y);
            //examine all the surrounding coordinates
            for (int i = 0; i < 8; i++) {
                newx = x + xoff[i];
                newy = y + yoff[i];
                //three part check:
                //make sure x is in bounds
                //make sure y is in bounds
                //make sure we haven't used this letter before
                if (((newx >= 0 && newx <= 3) && (newy >= 0 && newy <= 3)) && (used.getChar(newx, newy) == '0')) {
                    //if it's a valid coordinate, check to see if adding the letter results in a word or prefix
                    next = Character.toLowerCase(board.getChar(newx, newy));
                    if (next != '*') {
                        word.append(next);
                        //it's a word or a prefix
                        status = dict.search((word));
                        if (status > 0) {
                            //valid word, add to dictionary
                            //minimum length is 3
                            if ((status >= 2) && (word.length() >= 3)) {
                                gameDict.add(word.toString());
                                foundWords.add(word.toString());
                            }
                            //1 or 3 means that we have a valid prefix
                            if (!(status % 2 == 0)) {
                                used.setChar(newx, newy, '1');    //mark the character as used before passing it down the stack
                                gameDict = exhaustiveSearchAst(newx, newy, board, dict, gameDict, used, word);
                            }
                        }
                        //to backtrack, remove the character we added and reset the character as used
                        word.deleteCharAt(word.length() - 1);
                        used.setChar(newx, newy, '0');
                    }
                    //if we are dealing with an asterisk
                    else {
                        for (char c : alphabet) {
                            word.append(c);
                            //it's a word or a prefix
                            status = dict.search((word));
                            if (status > 0) {
                                //valid word, add to dictionary
                                //minimum length is 3
                                if ((status >= 2) && (word.length() >= 3)) {
                                    gameDict.add(word.toString());
                                    foundWords.add(word.toString());
                                }
                                //1 or 3 means that we have a valid prefix
                                if (!(status % 2 == 0)) {
                                    used.setChar(newx, newy, '1');    //mark the character as used before passing it down the stack
                                    gameDict = exhaustiveSearchAst(newx, newy, board, dict, gameDict, used, word);
                                }
                            }
                            //to backtrack, remove the character we added and reset the character as used
                            word.deleteCharAt(word.length() - 1);
                            used.setChar(newx, newy, '0');
                        }
                    }
                }
            }
        return gameDict;
    }

    public static DictionaryInterface exhaustiveSearch(int x, int y, BoggleBoard board, DictionaryInterface dict, DictionaryInterface gameDict, BoggleBoard used, StringBuilder word) {


        //get the current character, add it to the word
        used.setChar(x,y,'1');
        char current = board.getChar(x,y);
        char next;
        //the arrays we'll use for the the offset (search all the surrounding cells)
        int[] xoff = {-1, 0, 1,-1,1,-1,0,1};
        int[] yoff = {-1,-1,-1, 0,0, 1,1,1};
        //these will function as our new coordinates
        int newx;
        int newy;
        int status;
        //examine all the surrounding coordinates
        for (int i = 0; i < 8; i++) {
            newx = x + xoff[i];
            newy = y + yoff[i];
            //three part check:
            //make sure x is in bounds
            //make sure y is in bounds
            //make sure we haven't used this letter befpre
            if(((newx >= 0 && newx <=3)&&(newy >= 0 && newy <=3)) && (used.getChar(newx,newy) == '0')){
                //if it's a valid coordinate, check to see if adding the letter results in a word or prefix
                next = Character.toLowerCase(board.getChar(newx,newy));
                word.append(next);
                //it's a word or a prefix
                status = dict.search((word));
                if (status > 0){
                    //valid word, add to dictionary
                    //minimum length is 3
                    if ((status >= 2)&& (word.length()>=3)){
                        gameDict.add(word.toString());
                        foundWords.add(word.toString());
                    }
                    //1 or 3 means that we have a valid prefix
                    if (!(status % 2 ==0)){
                        used.setChar(newx,newy,'1');    //mark the character as used before passing it down the stack
                        gameDict = exhaustiveSearch(newx,newy,board,dict,gameDict,used,word);
                    }
                }
                //to backtrack, remove the character we added and reset the character as used
                word.deleteCharAt(word.length()-1);
                used.setChar(newx,newy,'0');
            }
        }
        return gameDict;

    }

    public static DictionaryInterface populateFromTxt(DictionaryInterface dict, String URI) {
        String word = null;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(URI));
            String s = "";
            while ((s = reader.readLine()) != null) {
                dict.add(s);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dict;
    }
}
