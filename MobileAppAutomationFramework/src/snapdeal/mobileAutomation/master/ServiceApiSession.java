package snapdeal.mobileAutomation.master;

import java.lang.reflect.InvocationTargetException;

/**
 * This class is used to maintain a set of API execution parameters for the current execution
 * @author Khagesh Kapil
 * @see ExecutionSession
 */
public class ServiceApiSession extends Session {
	
	/**
	 * 
	 */
	public final String NATIVE_CART_FLAG = "nativeCartFlag";
	
	public final String HERO_FLAG = "heroFlag";
	
	public final String SUPER_HERO_FLAG = "superHeroFlag";
	
	/**
	 * Environment for this session
	 */
	private String environment;
	
	private FlavourDictionary apiDictionary;
	
	public ServiceApiSession(String environment) {
		setEnvironment(environment);
		setApiDictionary(new FlavourDictionary("mobAPI"));
		try {
			getApiDictionary().setApiFlavorEnvironment("setApiEnvironment", getEnvironment());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void repoint(String environment) {
		setEnvironment(environment);
		try {
			getApiDictionary().setApiFlavorEnvironment("setApiEnvironment", getEnvironment());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Getter method for {@link #environment}
	 * @return
	 */
	public String getEnvironment() {
		return environment;
	}

	/**
	 * Setter method for {@link #environment}
	 * @param environment
	 */
	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public FlavourDictionary getApiDictionary() {
		return apiDictionary;
	}

	public void setApiDictionary(FlavourDictionary apiDictionary) {
		this.apiDictionary = apiDictionary;
	}
	
}
