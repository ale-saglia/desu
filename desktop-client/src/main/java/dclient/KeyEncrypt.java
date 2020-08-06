package dclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class KeyEncrypt {

	public static void main(String[] args) {
		Map<String, String> passwordMap = new HashMap<String, String>();
		String input;
		Key key;

		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);

		try {
			System.out.print("Inserisci la password => ");
			key = new Key(input = br.readLine());

			System.out.println("\nInserisci le righe da criptare");

			while (!(input = br.readLine()).trim().equals("")) {
				passwordMap.put(new String(input), key.getEnc().encrypt(input));
				System.out.println(input);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("\n\n");

		for (String password : passwordMap.keySet()) {
			System.out.println(password + " => " + passwordMap.get(password));
		}
	}
}
