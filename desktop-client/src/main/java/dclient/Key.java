package dclient;

import java.nio.charset.StandardCharsets;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;
import org.jasypt.properties.EncryptableProperties;

import com.google.common.hash.Hashing;

public class Key {
	StandardPBEStringEncryptor enc;
	EncryptableProperties config;

	public Key(String password, EncryptableProperties config) {
		this.config = config;
		enc = new StandardPBEStringEncryptor();
		enc.setAlgorithm(config.getProperty("enc.algorithm"));
		enc.setPassword(mergeHashPass(password));
		enc.setIvGenerator(new RandomIvGenerator());
	}
	
	public Key(String password, String envVariablePassword, String encAlgorithm) {
		enc = new StandardPBEStringEncryptor();
		enc.setAlgorithm(encAlgorithm);
		enc.setPassword(Hashing.sha512().hashString((password.concat(envVariablePassword)), StandardCharsets.UTF_8).toString());
		enc.setIvGenerator(new RandomIvGenerator());
	}
	
	public StandardPBEStringEncryptor getEnc() {
		return enc;
	}

	private String mergeHashPass(String password) {
		password = password + getEnvPass();
		return System.getenv(password);
	}
	
	private String getEnvPass() {
		return System.getenv(config.getProperty("env.varName"));
	}
}
