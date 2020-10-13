package dclient.model;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.jasypt.properties.EncryptableProperties;

import com.google.common.collect.BiMap;

import dclient.Key;
import dclient.db.ConMan;
import dclient.db.dao.AccountDAO;
import dclient.db.dao.JobDAO;

public class Model {
	public static final String CONFIG_PATH = System.getProperty("user.home") + "/.dclient/";
	Properties config;

	private ConMan conMan;

	private BiMap<String, String> accountCategories;
	private Map<String, Set<String>> jobCategories;

	public Model(String userPassword) throws IOException {
		config = new Properties();
		config.load(new FileInputStream(CONFIG_PATH + "config.properties"));
		config = new EncryptableProperties((new Key(userPassword, config)).getEnc());
		config.load(new FileInputStream(CONFIG_PATH + "config.properties"));

		this.conMan = new ConMan(config);

		accountCategories = AccountDAO.getAccountCategories(conMan.getDBConnection());
		jobCategories = JobDAO.getJobCategories(conMan.getDBConnection());
		
		this.conMan.closeDBConnection();
	}
	
	public ConMan getConMan() {
		return conMan;
	}

	public BiMap<String, String> getAccountCategories() {
		return accountCategories;
	}

	public Collection<String> getJobCategories() {
		return jobCategories.keySet();
	}

	public Collection<String> getTypesOfCategory(String category){
		Collection<String> types = new TreeSet<>();
		types.addAll(jobCategories.get(category));
		return types;
	}

	public void refreshSession() {
		if (!conMan.getSSHConnection().isConnected())
			conMan.getSSHConnection();
	}

	public String closeSession() {
		return conMan.closeSSHConnection();
	}

	public Properties getConfig() {
		return config;
	}

	public static String getConfigPath() {
		return CONFIG_PATH;
	}

	public String getLocalDateFormat(){
		return config.getProperty("dateFormat", "dd/MM/yyyy");
	}
}