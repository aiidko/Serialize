import java.util.Scanner;
import java.util.logging.Level;

import com.clu.TestClass;



public class main {

	public static void main(String[] args){
		Scanner in = new Scanner(System.in);
		TestClass t = new TestClass();
    	
		while (true){
        System.out.println("(S)erialize / (D)eserialize / (E)xit?");
            char answer = Character.toLowerCase(in.nextLine().charAt(0));
            if (answer == 's') {
            	t.serializeTest();
            } else if (answer == 'd') {
            	t.deserializeTest();
            }
            else if (answer == 'e') {
            	break;
            }
            else 
            	System.out.println("Choose one of the options");
		}
	}
}
