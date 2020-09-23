package dclient;

import dclient.installer.KeyEncrypt;

public class Launcher {

	public static void main(String[] args) {
		if (args.length > 0) {
			if (args[0].equals("installer"))
				KeyEncrypt.main(args);
		}
		
		else
			DClient.main(args);
	}

}
