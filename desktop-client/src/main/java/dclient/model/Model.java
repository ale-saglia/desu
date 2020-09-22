package dclient.model;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Properties;
import org.jasypt.properties.EncryptableProperties;

import com.google.common.collect.BiMap;

import dclient.Key;
import dclient.db.ConMan;
import dclient.db.dao.AccountDAO;
import dclient.db.dao.JobDAO;

public class Model {
	InputStream cfg;
	public final static String CONFIG_PATH = System.getProperty("user.home") + "/.dclient/";
	Properties config;

	ConMan conMan;

	BiMap<String, String> accountCategories;
	Collection<String> jobCategories;
	Collection<String> jobTypes;

	public Model(String userPassword) throws IOException {
		config = new Properties();
		config.load(new FileInputStream(CONFIG_PATH + "config.properties"));
		config = new EncryptableProperties((new Key(userPassword, config)).getEnc());
		config.load(new FileInputStream(CONFIG_PATH + "config.properties"));

		this.conMan = new ConMan(config);

		accountCategories = AccountDAO.getAccountCategories(conMan.getDBConnection());
		jobCategories = JobDAO.getJobCategories(conMan.getDBConnection());
		jobTypes = JobDAO.getJobTypes(conMan.getDBConnection());
		
		this.conMan.closeDBConnection();
	}
	
	public ConMan getConMan() {
		return conMan;
	}

	public BiMap<String, String> getAccountCategories() {
		return accountCategories;
	}

	public Collection<String> getJobCategories() {
		return jobCategories;
	}

	public Collection<String> getJobTypes() {
		return jobTypes;
	}

	public void refreshSession() {
		if (!conMan.getSession().isConnected())
			conMan.newSession();
	}

	public String closeSession() {
		return conMan.closeSession();
	}

	public Properties getConfig() {
		return config;
	}

	public static String getConfigPath() {
		return CONFIG_PATH;
	}
}