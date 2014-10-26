import org.jibble.pircbot.*;

public class MyBotMain {
	public static void main(String[] args) throws Exception {
		//MyBot bot = new MyBot();
		//bot.setVerbose(true);
		//bot.connect("irc.swiftirc.net");
		//bot.joinChannel("#insomnia", "tetris");

		MyBot bot = new MyBot();
		bot.setVerbose(true);
		bot.connect("irc.rizon.net");
		bot.joinChannel("#insomnia", "meatspin");
		//bot.joinChannel("#testingVanessa");

		MyBot imaginaryBot = new MyBot("freenode");
		imaginaryBot.setVerbose(true);
		imaginaryBot.connect("irc.freenode.net");
		imaginaryBot.joinChannel("##imaginary");

	}
}
