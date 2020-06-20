/*
 * Application by Kavin Nguyen
 * 2016
 * This class takes a requested number of puzzle cube turns and generates
 * a random sequence of moves that a user can use to scramble the cube
 */

import java.util.ArrayList;
import java.util.Random;

public class Scramble {
    
    
    
    // Takes the number of desired turns as a parameter to generate a sequence of numbers which
    // are then turned into a string of letters.
    public String getScramble(int turns) {
        Random r = new Random();
        ArrayList<Integer> numSeq = new ArrayList<Integer>();
        numSeq.add(r.nextInt(6) + 1);  // Adds first value to avoid exception index out of bounds
        
        for (int i = 1; i < turns; i++) {
            
            numSeq.add(getNum(r, numSeq, i));
        }
        
        String finalScramble = "";
        for (int i = 0; i < numSeq.size(); i++) {
            finalScramble = finalScramble + translate(numSeq.get(i), r);
        }
        
        finalScramble = finalScramble.substring(0, finalScramble.length() - 2); // cuts off space at the end
        
        return finalScramble;
        
    }
    
    // Takes a random object, the arraylist numSeq and an int from the for loop to generate a
    // number that will be turned into a turn. Also checks if the move makes sense with respect
    // to previous moves. Returns the generated integer
    public static int getNum(Random r, ArrayList<Integer> numSeq, int i) {
        int num;
        
        boolean check = true;
        
        do {
            num = r.nextInt(6) + 1;
            
            // Makes sure that two numbers don't end up next to each other
            if (numSeq.get(numSeq.size() - 1) == num) {
                check = true;
            } else {
                check = false;
            }
            
            numSeq.add(num);
            String seq = numSeq.toString();
            // Remove moves like R, L, R
            if (seq.contains("1, 2, 1") || seq.contains("2, 1, 2") || seq.contains("3, 4, 3") 
                    || seq.contains("4, 3, 4") || seq.contains("5, 6, 5") || seq.contains("6, 5, 6")) {
                check = true;
            }
            numSeq.remove(numSeq.size() - 1);
            
        } while (check);
        
        return num;
    }
    
    // Takes an integer from the numSeq array and a random object to return a cube turn
    public static String translate(int n, Random r) {
        String turn;
        
        // Takes an integer and gives a corresponding string
        switch (n) {
            case 1:
                turn = "R";
                break;
            case 2:
                turn = "L";
                break;
            case 3:
                turn = "U";
                break;
            case 4:
                turn = "D";
                break;
            case 5:
                turn = "F";
                break;
            case 6:
                turn = "B";
                break;
            default:
                return "error";
        }
        // Randomly chooses among 3 modifiers: prime, 2, or none
        int mod = r.nextInt(3);
        if (mod == 0) {
            turn = turn + "'  ";
        } else if (mod == 1) {
            turn = turn + "2  ";
        } else {
            turn = turn + "  ";
        }
        
        return turn;
        
    }
}
