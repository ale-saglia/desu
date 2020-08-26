package dclient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.text.RandomStringGenerator;

public class KeyEncrypt {
	private final int DEFAULT_PASSWORD_LENGHT = 64;
	private final String DEFAULT_ENV_VARIABLE = "DCLIENT";
	private final String DEFAULT_ENCRYPTION_ALGORITHM = "PBEWithHMACSHA512AndAES_256";
	private final String DEFAULT_PASSWORD_VALID_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%&*()_+-=[]?";

	private int passwordLenght;
	private String passwordValidChars;
	private String installationFolder;

	Key key;

	private String userPassword;
	private String sshFileName;
	private String sshNameIdentifier;
	private String sshPassword;
	private String envName;
	private String envPassword;

	private String envAlgorithm;

	Process sshCreation;

	Properties config;

	Logger logger;

	public static void main(String[] args) {
		KeyEncrypt keyEncrypt = new KeyEncrypt();
		keyEncrypt.initLogFile();
		keyEncrypt.loadInitProperties();
		keyEncrypt.userInput();
		keyEncrypt.setEnvVar();
		keyEncrypt.initKey();
		keyEncrypt.nukeConfigDirectory();
		keyEncrypt.setSSH();
		keyEncrypt.movePublicKey();
		keyEncrypt.createPropertiesFile();
	}

	private void initLogFile() {
		boolean append = true;
		FileHandler handler = null;
		try {
			handler = new FileHandler("installation.log", append);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		logger = Logger.getLogger("dclient");
		logger.addHandler(handler);
	}

	private void loadInitProperties() {
		config = new Properties();
		try {
			config.load(new FileInputStream("./installConfig.properties"));
		} catch (FileNotFoundException e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		passwordLenght = Integer
				.parseInt(config.getProperty("password.lenght", Integer.toString(DEFAULT_PASSWORD_LENGHT)));
		installationFolder = config.getProperty("installation.folder",
				System.getProperty("user.home") + "\\.dclient\\");
		sshFileName = config.getProperty("ssh.keyName", "id_dclient_rsa");
		sshNameIdentifier = config.getProperty("ssh.identifier", System.getProperty("user.name"));
		envName = config.getProperty("env.variable", DEFAULT_ENV_VARIABLE);
		envAlgorithm = config.getProperty("enc.algorithm", DEFAULT_ENCRYPTION_ALGORITHM);
		passwordValidChars  = config.getProperty("password.validChars", DEFAULT_PASSWORD_VALID_CHARS);
	}

	private void userInput() {
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);

		try {
			System.out.print("Inserisci la password => ");
			userPassword = br.readLine();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initKey() {
		key = new Key(userPassword, envPassword, envAlgorithm);
		logger.info("User password: " + userPassword + 
				"\nEnv password: " + envPassword +
				"\nUsed algorythm: " + envAlgorithm +
				"\n" + key.getEnc().toString());
	}

	private void nukeConfigDirectory() {
		try {
			FileUtils.cleanDirectory(new File(installationFolder));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setEnvVar() {
		envPassword = passwordGenerator();
		try {
			Runtime.getRuntime().exec("setx " + envName + " " + envPassword);
		} catch (IOException e) {
			e.printStackTrace();
		}

		String logging = "Added or edited successfully the user environment variable" + envName + " with value "
				+ envPassword + "\n" + "The from the system i can read that the variable " + envName + " equals to "
				+ System.getenv(envName);
		logger.info(logging);
	}

	private void setSSH() {
		sshPassword = passwordGenerator();
		logger.info("Generated password for ssh:" + sshPassword);
		String command = ("ssh-keygen -f " + installationFolder + sshFileName + " -t rsa  -b 4096 -C "
				+ sshNameIdentifier + " -N " + sshPassword);

		ProcessBuilder sshCreationBuilder = new ProcessBuilder("cmd.exe", "/c", command);
		sshCreationBuilder.redirectOutput(Redirect.INHERIT);
		sshCreationBuilder.redirectError(Redirect.INHERIT);
		try {
			sshCreation = sshCreationBuilder.start();
			sshCreation.waitFor();
		} catch (IOException | InterruptedException e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
		}
	}

	private void movePublicKey() {
		try {
			Files.move(Paths.get(installationFolder + "\\" + sshFileName + ".pub"), Paths.get(
					".\\" + System.getProperty("user.name") + "-" + InetAddress.getLocalHost().getHostName() + ".pub"),
					StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String passwordGenerator() {
		return new RandomStringGenerator.Builder().selectFrom(passwordValidChars.toCharArray()).build()
				.generate(passwordLenght);
	}

	private void createPropertiesFile() {
		Properties installProperties = new Properties();

		installProperties.setProperty("rsppTable.daysAdvance", config.getProperty("rsppTable.daysAdvance"));
		installProperties.setProperty("rsppTable.dateFormat", config.getProperty("rsppTable.dateFormat"));

		installProperties.setProperty("db.host", "localhost");
		installProperties.setProperty("db.port", config.getProperty("db.port", "5432"));
		installProperties.setProperty("db.user", "ENC(" + key.getEnc().encrypt(config.getProperty("db.user")) + ")");
		installProperties.setProperty("db.password",
				"ENC(" + key.getEnc().encrypt(config.getProperty("db.password")) + ")");
		installProperties.setProperty("db.database", config.getProperty("db.database"));

		installProperties.setProperty("ssh.host", config.getProperty("ssh.host"));
		installProperties.setProperty("ssh.user", "ENC(" + key.getEnc().encrypt(config.getProperty("ssh.user")) + ")");
		installProperties.setProperty("ssh.keyName", sshFileName);
		installProperties.setProperty("ssh.keyPassword", "ENC(" + key.getEnc().encrypt(sshPassword) + ")");
		installProperties.setProperty("ssh.port", config.getProperty("ssh.port", "22"));

		installProperties.setProperty("env.varName", config.getProperty("env.varName", DEFAULT_ENV_VARIABLE));
		installProperties.setProperty("enc.algorithm",
				config.getProperty("enc.algorithm", DEFAULT_ENCRYPTION_ALGORITHM));

		File file = new File(installationFolder + "/config.properties");
		FileOutputStream fileOut;
		try {
			fileOut = new FileOutputStream(file);
			installProperties.store(fileOut, "This is an autogenerated file for dclient");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
