import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Encourage {
    private ArrayList<String> msg;
    private Random r;
    
    public Encourage() throws FileNotFoundException {
        msg = new ArrayList<String>();
        r = new Random();
        // gets a text file in the current directory with encouraging phrases and attaches a scanner
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("encourage.txt");
        Scanner input = new Scanner(in);
        
        // Adds the lines from the text file into an arrraylist
        while (input.hasNextLine()) {
            msg.add(input.nextLine());
        }
    }
    
    // Returns a random line from the text file
    public String getMSG() {
        return msg.get(r.nextInt(msg.size()));
    }
    

}

