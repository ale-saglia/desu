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
		enc.setPassword(Hashing.sha256().hashString(password.concat(System.getenv().get(config.getProperty("env.varName"))), StandardCharsets.UTF_8).toString());
		enc.setIvGenerator(new RandomIvGenerator());
	}

	public Key(String password, String envVariablePassword, String encAlgorithm) {
		enc = new StandardPBEStringEncryptor();
		enc.setAlgorithm(encAlgorithm);
		enc.setPassword(
				Hashing.sha512().hashString((password.concat(envVariablePassword)), StandardCharsets.UTF_8).toString());
		enc.setIvGenerator(new RandomIvGenerator());
	}

	public StandardPBEStringEncryptor getEnc() {
		return enc;
	}
}
