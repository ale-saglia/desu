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
	
	public Key installationKey(String password, String envPassword) {
		Key key = new Key(null, config);
		key.enc.setPassword(mergeHashPass(password, envPassword));
		return key;
	}

	public StandardPBEStringEncryptor getEnc() {
		return enc;
	}

	private String mergeHashPass(String password) {
		password = password + getEnvPass();
		return System.getenv(password);
	}
	
	private String mergeHashPass(String password, String envPassword) {
		return Hashing.sha512().hashString((password.concat(envPassword)), StandardCharsets.UTF_8).toString();
	}
	
	private String getEnvPass() {
		return System.getenv(config.getProperty("env.varName"));
	}
}
