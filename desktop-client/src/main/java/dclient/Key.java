package dclient;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;

import com.google.common.hash.Hashing;

public class Key {
	StandardPBEStringEncryptor enc;
	Properties config;

	public Key(String password, Properties config) {
		this.config = config;
		enc = new StandardPBEStringEncryptor();
		enc.setAlgorithm(config.getProperty("enc.algorithm"));
		enc.setPassword(hashPassword(password, System.getenv().get(config.getProperty("env.varName").toString())));
		enc.setIvGenerator(new RandomIvGenerator());
	}

	public Key(String password, String envVariablePassword, String encAlgorithm) {
		enc = new StandardPBEStringEncryptor();
		enc.setAlgorithm(encAlgorithm);
		enc.setPassword(hashPassword(password, envVariablePassword));
		enc.setIvGenerator(new RandomIvGenerator());
	}

	public StandardPBEStringEncryptor getEnc() {
		return enc;
	}
	
	private String hashPassword(String userPassword, String envPassword) {
		String hashed = Hashing.sha512().hashString((userPassword.concat(envPassword)), StandardCharsets.UTF_8).toString();
		return hashed;
	}
}
