import java.io.*;
public class AliasTest {
	public static void main(String[] args) {
		String nick = args[0];
		String owner = findOwner(nick);
		if(owner.isEmpty()) {
			System.out.println("No alias found for " + nick + ".");
		} else {
			if(nick.equals(owner)) {
				System.out.println(nick + " is the owner already.");
			} else {
				System.out.println(nick + " belongs to " + owner + ".");
			}
		}
	}

	public static String findOwner(String nick) {
		try {
			String line;
			int search = -1;
			FileReader fr = new FileReader("aliases");
			BufferedReader in = new BufferedReader(fr);

			while((line = in.readLine()) != null) {
				String[] aliases = line.split(" ");
				for(int i = 0; i < aliases.length; i++) {
					if(aliases[i].equals(nick)) {
						in.close();
						return aliases[0];
					}
				}
			}
			
			in.close();
		} catch(Exception e) {
			System.out.println("Oops.");
			return "";
		}
		return "";
	}
}
