package dclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;

import dclient.model.Model;

public class KeyEncrypt {
	private final int PASSWORD_LENGHT = 64;
	
	Key key;
	
	private String userPassword;
	private String sshPassword;
	private String envPassword;

	public static void main(String[] args) {
		KeyEncrypt keyEncrypt = new KeyEncrypt();
		keyEncrypt.userInput();
		keyEncrypt.setEnvVar("");
		keyEncrypt.initKey();
	}
	
	private void userInput(){
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);

		try {
			System.out.print("Inserisci la password => ");
			userPassword = br.readLine();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void initKey() {
		key = new Key(userPassword, "");
	}

	private void setEnvVar(String varName) {
		envPassword = passwordGenerator();
		try {
			Runtime.getRuntime().exec("setx " + varName + " " + envPassword);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private void setSSH() {
		sshPassword = passwordGenerator();
		try {
			Runtime.getRuntime().exec(
					"ssh-keygen -f " + Model.getConfigPath() + "/" + "id_dclient.rsa" + " -t rsa  -b 4096 -C " + System.getProperty("user.name" + " -N " + sshPassword)

			);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	private void movePublicKey() {
		
	}

	private String passwordGenerator() {
		return RandomStringUtils.randomAlphanumeric(PASSWORD_LENGHT);
	}
}
