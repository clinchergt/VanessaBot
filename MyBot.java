import org.jibble.pircbot.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;
import java.io.*;
import java.util.HashMap;
import java.util.ArrayDeque;

import java.util.Collections;
import java.util.Arrays;

public class MyBot extends PircBot {
	private ArrayList<String[]> messages;
	private ArrayList<String> whispered_messages;
	private HashMap<String, ArrayDeque<Long>> spamMap; //Map to check if the user is spammin
	private	ArrayDeque<Long> lastMessagesArray;		//temporary array to store the dates from the last messages
	private boolean whispering;
	private String network;

	// Steve is working here
	private boolean pokerStart = false;
	private boolean pokerPlay = false;
	private String[] players = new String[6];
	private String[] deck = new String[52];
	private int numPlayers = 0;
	// </Steve>


	public MyBot() {
		this.setName("Vanessa");
		messages = new ArrayList<String[]>();
		whispered_messages = new ArrayList<String>();
		whispering = false;
		spamMap = new HashMap<String, ArrayDeque<Long>>();
		lastMessagesArray = null;
	}

	/*Added to avoid login/registration crap*/
	public MyBot(String network){
		this();
		this.network = network;
		this.setName("`Vanessa");
	}

	public void onConnect() {
		if(network == null)
			identify("iProgram");
	}

	private String timePassed(Date d) {
		String str = "";
		long days, hours, minutes, seconds;
		long diff = new Date().getTime() - d.getTime();
		days = diff / (1000 * 60 * 60 * 24);
		diff %= (1000 * 60 * 60 * 24);
		hours = diff / (1000 * 60 * 60);
		diff %= (1000 * 60 * 60);
		minutes = diff / (1000 * 60);
		diff %= (1000 * 60);
		seconds = diff / 1000;

		if(days > 0)
			str += days + " days, ";
		if(days > 0 || hours > 0)
			str += hours + " hours, ";
		if(days > 0 || hours > 0 || minutes > 0)
			str += minutes + " minutes, ";
		str += seconds + " seconds ago.";

		return str;
	}

	public void onPrivateMessage(String sender, String login, String hostname, String message) {
		message = message.trim();

		if(sender.equals("steadshot") || sender.equals("steadshot_")) {
			if(message.indexOf("#queue") == 0) {
				whispered_messages.add(message.replace("#queue", ""));
				return;
			}
			if(message.indexOf("#remove") == 0) {
				if(whispered_messages.size() > 0) {
					whispered_messages.remove(0);
				} else {
					sendMessage(sender, "Queue is empty.");
				}
				return;
			}
			if(message.indexOf("#help") == 0) {
				sendMessage(sender, "#queue, #remove, #help, #pause, #list");
				return;
			}
			if(message.indexOf("#pause") == 0) {
				whispering = !whispering;
				sendMessage(sender, whispering ? "Whispering is now on." : "Whispering is now off.");
				return;
			}
			if(message.indexOf("#list") == 0) {
				for(int i = 0; i < whispered_messages.size(); i++) {
					sendMessage(sender, i + ": " + whispered_messages.get(i));
				}
				return;
			}
			//no commands used
			if(whispering) {
				sendMessage("#insomnia", message);
			}
		}
	}

	public void onMessage(String channel, String sender, String login,
		String hostname, String message) {
		/*Method to prevent spamming in channel
		if something goes wrong, blame clinch*/
		if(spamMap.containsKey(sender)){
			lastMessagesArray = spamMap.get(sender);
			if(lastMessagesArray.size() == 10 && System.currentTimeMillis() - lastMessagesArray.poll().longValue() < 15000){
				sendMessage(channel, "Bye, " + sender);
				ban(channel, hostname);
				spamMap.remove(sender);
			}else{
				lastMessagesArray.add(new Long(System.currentTimeMillis()));
			}
		}else{
			lastMessagesArray = new ArrayDeque<Long>(10); //4 Messages are the max i'll allow per second
			lastMessagesArray.push(new Long(System.currentTimeMillis()));
			spamMap.put(sender, new ArrayDeque<Long>(lastMessagesArray));
		}
		lastMessagesArray = null; //a lame attempt at saving memory


		/*End of method that prevents spamming/flooding*/

		if(whispered_messages.size() > 0 && whispering) {
			if(sender.equals("steadshot") || sender.equals("steadshot_")) {
				sendMessage(channel, whispered_messages.get(0));
				whispered_messages.remove(0);
			}
		}

		if(message.equalsIgnoreCase("!date")) {
			if(sender.equals("steadshot") || sender.equals("steadshot_")) {
				String time = new java.util.Date().toString();
				sendMessage(channel, time);
			} else {
				sendMessage(channel, "I'm not your bitch.");
			}
		}
		if(message.equalsIgnoreCase("!cf") || message.equalsIgnoreCase("!coinflip")) {
			int random_bit = (int) (Math.random() * 2);
			String heads_or_tails = (random_bit == 0) ? "Heads" : "Tails";
			sendMessage(channel, heads_or_tails);
		}
		if(message.equalsIgnoreCase("!server")) {
			if(sender.equals("steadshot") || sender.equals("steadshot_")) {
				sendMessage(channel, "Visit http://www.digitalno.de");
			} else {
				sendMessage(channel, "I'm not your bitch.");
			}
		}
		if(message.equalsIgnoreCase("!help")) {
			sendMessage(channel, "No one can help you now.");
		}
		if(message.equalsIgnoreCase("!dismiss")) {
			if(sender.equals("steadshot") || sender.equals("steadshot_")) {
				sendMessage(channel, "I guess this means goodbye.");
				sendAction(channel, "hugs " + sender);
				while(getOutgoingQueueSize() > 0) {

				}
				partChannel(channel, "I'm gonna miss you, " + sender + ".");
				disconnect();
				System.exit(0);
			}
		}
		if(message.equalsIgnoreCase("tits or gtfo")) {
			disconnect();
			System.exit(0);
		}

		if(message.equals("!ping")) {
			sendMessage(channel, "PAWNG!");
		}
		if(Pattern.matches("^(!tell)(\\s)(\\S)+(\\s).+", message)) {
			sendMessage(channel, "Consider it done, Sir.");
			String[] tell = new String[4];
			String[] temp = new String[3];
			temp = message.split(" ", 3);
			tell[1] = temp[1];
			tell[2] = temp[2];
			tell[0] = sender;
			tell[3] = "" + new java.util.Date().getTime();
			messages.add(tell);
		}
		if(message.equals("!list")) {
			for(int i = 0; i < messages.size(); i++) {
				sendMessage(channel, ((String[]) messages.get(i))[0] + " " +
				((String[]) messages.get(i))[1] + " " +
				((String[]) messages.get(i))[2] + " " +
				((String[]) messages.get(i))[3]);
			}
		}

		//deals with the !phrase add-on
		if(Pattern.matches("^(!phrase)(\\s)(\\S)+(\\s).+", message)) {
			String[] temp = new String[3];
			temp = message.split(" ", 3);
			String keyword = temp[1];
			String phrase = temp[2];

			//adding phrases
			if(keyword.equals("add")) {
				writeToFile(sender, phrase, channel);
				return;
			}

			//finding phrases
			if(keyword.equals("find")) {
				//clinch
				findInFile(sender, phrase, channel);
				return;
			}

			//editing last phrase
			if(keyword.equals("edit") || keyword.equals("editlast")){
				deleteLastLine(channel, "phrases/" + sender);
				writeToFile(sender, phrase, channel, true);
				return;
			}

			//deleting last phrase
			if(keyword.equals("del") || keyword.equals("delete")) {
				deleteLastLine(channel, "phrases/" + sender);
				sendMessage(channel, "Last phrase deleted.");
			}

			//generating anki deck - clinch
			if(keyword.equals("anki")) {
				generateAnkiDeck(sender);
				sendMessage(channel, "Anki deck generated.");
			}
			sendMessage(channel, "I don't know that keyword.");

		}

		// Steve is working here

		if(message.equalsIgnoreCase("!poker")) {
			if((pokerStart == true) || (pokerPlay == true)) {
				sendMessage(channel, "A poker game is already in session.");
			}
			else {
				sendMessage(channel, sender + " wishes to start a poker game.");
				pokerStart = true;
				players[numPlayers] = sender;
				numPlayers++;
				sendMessage(channel, "Type \"!join\" to play.");
				sendMessage(channel, sender + ", type \"!start\" to start the game.");
			}
		}
		if((pokerStart == true) && (message.equalsIgnoreCase("!join"))) {
			// check to make sure player isn't already playing
			if(numPlayers < 6) {
				players[numPlayers] = sender;
				numPlayers++;
				sendMessage(channel, sender + " added as Player " + numPlayers);
			}
			else {
				sendMessage(channel, "Sorry, there can only be max 6 players.");
			}
		}
		if((pokerStart == true) && (sender.equals(players[0])) && (message.equalsIgnoreCase("!start"))) {
			if(numPlayers <= 1) {
				sendMessage(channel, "You can't play poker with yourself!");
				// Maybe later add AI to play vs. computer?
				pokerStart = false;
				numPlayers = 0;
				players = new String[6];
			}
			else {
				pokerStart = false;
				pokerPlay = true;
				sendMessage(channel, "The players are -");
				for(int i = 0; i < numPlayers; i++) {
					int j = i + 1;
					sendMessage(channel, "Player " + j + ": " + players[i]);
				}
			}
		}
		// </Steve>
	}

	// <Steve>
	public void createDeck() {
		char[] values = new char[] {'2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A'};
		char[] suits = new char[] {'S', 'H', 'D', 'C'};
		int k = 0;
		deck = new String[52];
		for(int i = 0; i < values.length; i++) {
			for(int j = 0; j < suits.length; j++) {
				deck[k] = "" + values[i] + suits[j];
				k++;
			}
		}
		Collections.shuffle(Arrays.asList(deck));
	}
	// </Steve>


	public void generateAnkiDeck(String sender) {

	}

	public void deleteLastLine(String channel, String filename) {
		try{
			RandomAccessFile f = new RandomAccessFile(filename, "rw");
			long length = f.length() - 1;
			byte b;
			do {
				length -= 1;
				f.seek(length);
				b = f.readByte();
			} while(b != 10);
			f.setLength(length+1);
			f.close();
		} catch(Exception e){
			sendMessage(channel, "Something went wrong. To blame: " + "clinch");
		}
	}

	public void writeToFile(String sender, String phrase, String channel) {
		writeToFile(sender, phrase, channel, false);
	}

	public void writeToFile(String sender, String phrase, String channel, boolean edit) {
		//adds a new phrase
		String owner = findOwner(sender);
		//checks for possible alias in the file 'aliases'
		String alias_msg = "";
		String real_sender = sender;
		//if alias is found, then the variable 'sender' is changed
		//however, to highlight 'real_sender' is necessary

		if(!owner.isEmpty() && !owner.equals(sender)) {
			//alias_msg = "(alias for " + owner + ") ";
			sender = owner;
		}

		//check if file exists for user
		boolean fileExists = true;
		String filePath = "phrases/" + sender;
		File f = new File(filePath);
		if(!f.isFile()) {
			sendMessage(channel, "Warning: File \"" + sender + "\" doesn't exist yet.");
			fileExists = false;
		}

		//write phrase into user's file
		try {
			FileWriter fstream = new FileWriter(filePath, true);
			if(!fileExists) {
				sendMessage(channel, "File \"" + sender + "\" was created.");
			}
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(phrase + "\n");

			out.close();
			if(!edit) {
				sendMessage(channel, alias_msg + "Phrase added.");
			} else {
				sendMessage(channel, alias_msg + "Last phrase edited.");
			}
		} catch(Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	public void findInFile(String sender, String phrase, String channel) {
		String owner = findOwner(sender);
		String real_sender = sender;
		String alias_msg = "";

		if(!owner.isEmpty() && !owner.equals(sender)) {
			//alias_msg = "(alias for " + owner + ") ";
			sender = owner;
		}

		String filePath = "phrases/" + sender;
		if(!(new File(filePath)).isFile()) {
			sendMessage(channel, real_sender + ": You haven't added any phrases yet.");
			return;
		}
		try {
			FileReader inFile = new FileReader(filePath);
			BufferedReader in = new BufferedReader(inFile);
			String currentLine;
			String stringToWrite = "";
			int matches = 0;
			while((currentLine = in.readLine()) != null) {
				if(currentLine.toLowerCase().indexOf(phrase.toLowerCase()) >= 0 || phrase.equals("*")) {
					//if(++matches >= 5) {
						//sendMessage(channel, sender + ": Your search returned too many matches, please be more specific.");
						//return;
					//}
					matches++;
					stringToWrite = stringToWrite.concat(currentLine + "\n");
				}
			}
			if(matches == 0) {
				sendMessage(channel, real_sender + ": " + alias_msg + "No matches found.");
				return;
			}
			String[] results = stringToWrite.split("\n");
			//too many results -> generate URL
			if(matches >= 5) {
				sendMessage(channel, real_sender + ": " + alias_msg + results.length + " results found -> " +
					generateURL(channel, results));
				return;
			}
			for(int i = 0; i < results.length; i++)
				sendMessage(channel, real_sender + ": " + alias_msg + results[i]);
		}catch(Exception e) {
			sendMessage(channel, "oops");
		}
	}

	public String generateURL(String channel, String[] results) {
		//generates random file (format: 'query{random-3-digit-number}.txt'),
		//writes given results into that file and returns a URL to that file
		//tbd: delete older files at some point
		int x = (int) (100 + (999 - 100) * Math.random());
		String path = "queries/";
		String file = "query" + x + ".txt";
		String url = "Couldn't generate URL"; 

		try {
			Writer out = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(path + file), "UTF-8"));

			for(int i = 0; i < results.length; i++) {
				out.write(results[i] + "\n");
			}

			url = "http://digitalno.de/queries/" + file;

			out.close();
		} catch(Exception e) {
			sendMessage(channel, "Oops!");
		}

		return url;
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

	public void onJoin(String channel, String sender, String login,
		String hostname) {
		String msg_sender, msg_recipient, msg, msg_date;
		for(int i = 0; i < messages.size(); i++) {
			msg_sender = ((String[]) messages.get(i))[0];
			msg_recipient = ((String[]) messages.get(i))[1];
			msg = ((String[]) messages.get(i))[2];
			msg_date = ((String[]) messages.get(i))[3];
			if(sender.toLowerCase().contains(msg_recipient.toLowerCase())) {
				sendMessage(channel, "Hey " + sender + ", " +
					msg_sender + " left a message for you: \"" + msg +
					"\" - " + timePassed(new Date(Long.parseLong(msg_date))));
				messages.remove(i);
				i--;
			}
		}
	}

	public void onDisconnect() {
		while(!isConnected()) {
			try {
				reconnect();
			}
			catch(Exception e) {
				try {
					Thread.sleep(10000);
				}
				catch(InterruptedException ie) {

				}
			}
		}
		joinChannel("#insomnia", "meatspin");
	}

	public void onKick(String channel, String kickerNick, String kickerLogin,
		String kickerHostname, String recipientNick, String reason) {
		if(recipientNick.equalsIgnoreCase(getNick())) {
			joinChannel(channel, "meatspin");
			sendMessage(channel, kickerNick + ": Go fuck yourself.");
		}
	}
}
