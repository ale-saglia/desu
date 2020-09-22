package dclient.model;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import org.jasypt.properties.EncryptableProperties;

import com.google.common.collect.BiMap;

import dclient.Key;
import dclient.db.dao.AccountDAO;
import dclient.db.dao.JobDAO;
import dclient.db.dao.MainDAO;

public class Model {
	InputStream cfg;
	public final static String CONFIG_PATH = System.getProperty("user.home") + "/.dclient/";
	Properties config;

	MainDAO dao;

	BiMap<String, String> accountCategories;
	List<String> jobCategories;
	List<String> jobTypes;

	public Model(String userPassword) throws IOException {
		config = new Properties();
		config.load(new FileInputStream(CONFIG_PATH + "config.properties"));
		config = new EncryptableProperties((new Key(userPassword, config)).getEnc());
		config.load(new FileInputStream(CONFIG_PATH + "config.properties"));

		this.dao = new MainDAO(config);

		accountCategories = AccountDAO.getAccountCategories(dao.getDBConnection());
		jobCategories = JobDAO.getJobCategories(dao.getDBConnection());
		jobTypes = JobDAO.getJobTypes(dao.getDBConnection());
		
		this.dao.closeDBConnection();
	}
	
	public MainDAO getDAO() {
		return dao;
	}

	public BiMap<String, String> getAccountCategories() {
		return accountCategories;
	}

	public List<String> getJobCategories() {
		return jobCategories;
	}

	public List<String> getJobTypes() {
		return jobTypes;
	}

	public void refreshSession() {
		if (!dao.getSession().isConnected())
			dao.newSession();
	}

	public String closeSession() {
		return dao.closeSession();
	}

	public Properties getConfig() {
		return config;
	}

	public static String getConfigPath() {
		return CONFIG_PATH;
	}
}