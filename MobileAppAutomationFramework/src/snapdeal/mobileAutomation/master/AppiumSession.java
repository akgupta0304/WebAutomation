package snapdeal.mobileAutomation.master;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.lang.Process;

import snapdeal.mobileAutomation.exceptions.AppiumNotResetException;
import snapdeal.mobileAutomation.master.TestSuite;

/**
 * This class represents a session of appium server with one of the
 * attached(Android/iOS) devices
 * 
 * @author Khagesh Kapil
 */
public class AppiumSession extends Session {

	/**
	 * Keeps all the properties for the current appium session
	 */
	private Properties appiumFlags;

	public Properties getAppiumFlags() {
		return appiumFlags;
	}

	/**
	 * Reference to the thread to update this appium session logs to console
	 * 
	 * @see AppiumThread
	 */
	private AppiumThread appiumThread;

	/**
	 * Getter method for the current appium session thread
	 * 
	 * @return AppiumThread
	 */
	public AppiumThread getAppiumThread() {
		return appiumThread;
	}

	/**
	 * 
	 * @param masterAppiumOpts
	 *            Collection of all appium config file properties
	 * @param filterSequence
	 *            Sequence no. of the device as per appium config file for this
	 *            appium session
	 * @return Appium Flags for this appium session
	 */
	private Properties generateThisAppiumFlags(Properties masterAppiumOpts,
			int filterSequence) {
		Properties filteredFlags = new Properties();
		try {
			Iterator<Map.Entry<Object, Object>> masterAppiumOptsItr = masterAppiumOpts
					.entrySet().iterator();
			while (masterAppiumOptsItr.hasNext()) {
				Map.Entry<Object, Object> masterAppiumOptsEntry = masterAppiumOptsItr
						.next();
				String masterAppiumOption = (String) masterAppiumOptsEntry
						.getKey();
				String masterAppiumOptionValue = (String) masterAppiumOptsEntry
						.getValue();
				if (masterAppiumOption.endsWith(String.valueOf(filterSequence)))
					filteredFlags.put(
							masterAppiumOption.substring(0,
									masterAppiumOption.length() - 1),
							masterAppiumOptionValue);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filteredFlags;
	}

	/**
	 * Constructor for Appium Session
	 * 
	 * @param masterAppiumOpts
	 *            Collection of all appium config file properties
	 * @param filterSequence
	 *            Sequence no. of the device as per appium config file for this
	 *            appium session
	 */
	AppiumSession(Properties masterAppiumOpts, int filterSequence,
			TestSuite testSuite) {
		appiumFlags = generateThisAppiumFlags(masterAppiumOpts, filterSequence);
		appiumThread = new AppiumThread(appiumFlags, testSuite);
	}
}

/**
 * This class represents a thread to update this appium session logs to console
 * 
 * @author Khagesh Kapil
 */
class AppiumThread extends Thread {

	/**
	 * Flag to indicate if the current instance of appium has been fully started
	 */
	private volatile boolean appiumStarted = false;

	TestSuite testSuite;

	/**
	 * Reference to the collection of this appium flags
	 */
	private Properties appiumConfig;

	/**
	 * Getter method for {@link #appiumStarted}
	 * 
	 * @return appiumStarted flag indicating if the current appium instance has
	 *         been fully started
	 */
	public boolean isAppiumStarted() {
		return appiumStarted;
	}

	/**
	 * To detect and update a free port to {@link #appiumConfig}
	 * 
	 * @param appiumConfig
	 *            {@link #appiumConfig}
	 * @return {@link #appiumConfig} with updated port no to be used during
	 *         execution
	 */
	public Properties updateAvailablePort(Properties appiumConfig) {
		Properties newAppiumConfig = appiumConfig;
		try {
			ServerSocket s = new ServerSocket(Integer.parseInt(appiumConfig
					.getProperty("flag--port")));
			s.close();
			System.out.println("port " + appiumConfig.getProperty("flag--port")
					+ " is available");
		} catch (Exception e) {
			System.err.println("port " + appiumConfig.getProperty("flag--port")
					+ " is not available");
			try {
				ServerSocket s = new ServerSocket(0);
				int port = s.getLocalPort();
				newAppiumConfig.setProperty("flag--port", "" + port);
				s.close();
				System.out.println("Switching to port " + port);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return newAppiumConfig;
	}

	/**
	 * Constructor for this Appium Thread
	 * 
	 * @param appiumConfig
	 *            {@link #appiumConfig}
	 */
	AppiumThread(Properties appiumConfig, TestSuite testSuite) {
		this.appiumConfig = updateAvailablePort(appiumConfig);
		this.testSuite = testSuite;
		this.start();
	}

	/**
	 * Getter method for {@link #appiumConfig}
	 * 
	 * @return appiumConfig a reference to the collection of this appium flags
	 */
	public Properties getAppiumConfig() {
		return appiumConfig;
	}

	/**
	 * Method to search for the given filename on the local machine
	 * 
	 * @param appium
	 *            Name of the file to be searched
	 * @return Path of the file to be searched or an empty string if the file
	 *         was not found
	 * @throws IOException
	 */
	public String serachMachineForFile(String appium) throws IOException {
		String appiumPath = "";
		File[] roots = File.listRoots();
		findingAppium: for (int i = 0; i < roots.length; i++) {
			try {
				for (File f : roots[i].listFiles()) {
					if (f.isDirectory()) {
						appiumPath = searchDirectory(f, appium);
						if (!appiumPath.equals(""))
							break findingAppium;
					}
					if (f.getName().equals(appium)) {
						try {
							appiumPath = f.getCanonicalPath();
							break findingAppium;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			} catch (Exception e) {
				continue;
			}
		}
		return appiumPath;
	}

	/**
	 * Method to search for the given filename in a specific directory
	 * 
	 * @param f
	 *            Object of the directory to be searched into
	 * @param filename
	 *            Name of the file to be searched for
	 * @return Path of the file to be searched or an empty string if the file
	 *         was not found
	 */
	public String searchDirectory(File f, String filename) {
		File[] subFiles = f.listFiles();
		if (subFiles == null) {
			return "";
		}
		String path = "";
		for (File fi : subFiles) {
			if (fi.isDirectory()) {
				path = searchDirectory(fi, filename);
				if (!path.equals(""))
					break;
			} else if (fi.getName().equals(filename)) {
				try {
					path = fi.getCanonicalPath();
					System.out.println("Match found : " + path);
					break;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return path;
	}

	/**
	 * Method to kill a previously running instance of appium on Windows/Mac
	 * machine
	 * 
	 * @throws AppiumNotResetException
	 *             #{@link AppiumNotResetException}
	 */
	public void killAppium() throws AppiumNotResetException {
		String platform = System.getProperty("os.name");
		boolean isRunning = false;
		if (platform.startsWith("Windows")) {
			try {
				Process p = Runtime.getRuntime().exec(
						System.getenv("windir") + "\\system32\\"
								+ "tasklist.exe");
				BufferedReader bfr = new BufferedReader(new InputStreamReader(
						p.getInputStream()));
				String line = bfr.readLine();
				String headers = null;
				String match = "PID";
				int start = 0, end = 0;
				int itr = 0;
				String pid = "";
				while (line != null) {
					if (line.length() > 0 && itr == 0) {
						headers = line;
						itr++;
						line = bfr.readLine();
						continue;
					}
					findingPID: if (itr == 1) {
						StringTokenizer st = new StringTokenizer(line, " ");
						while (st.hasMoreTokens()) {
							String nextToken = st.nextToken();
							end = start + nextToken.length() - 1;
							if (headers.substring(start, end + 1).trim()
									.equalsIgnoreCase(match)) {
								itr = 2;
								break findingPID;
							} else
								start += nextToken.length() + 1;
						}
					}
					if (line.startsWith("node.exe")) {
						pid = line.substring(start, end + 1).trim();
						isRunning = true;
						break;
					}
					line = bfr.readLine();
				}
				bfr.close();
				if (!pid.equals("")) {
					p = Runtime.getRuntime().exec(
							"taskkill /pid " + pid + " /f");
					bfr = new BufferedReader(new InputStreamReader(
							p.getInputStream()));
					line = bfr.readLine();
					while (line != null) {
						if (line.contains("The process with PID " + pid
								+ " has been terminated"))
							isRunning = false;
						line = bfr.readLine();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				if (isRunning)
					throw new AppiumNotResetException();
			}
			if (isRunning)
				System.out.println("Appium couldn't be reset");
			else
				System.out.println("Appium reset successfully");
		}
		if (platform.startsWith("Mac")) {
			try {
				Process p = Runtime.getRuntime().exec("killall node");
				BufferedReader bfr = new BufferedReader(new InputStreamReader(
						p.getInputStream()));
				String line = bfr.readLine();
				if (line != null)
					isRunning = true;
				else
					isRunning = false;
			} catch (Exception e) {
				e.printStackTrace();
				if (isRunning)
					throw new AppiumNotResetException();
			}
		}
	}

	/**
	 * Method the invoke appium with the given flag String
	 * {@link #prepareInputFlagString(Properties)}
	 * 
	 * @param flagString
	 *            the string mentioning all the flags to run this instance of
	 *            appium
	 */
	public void invokeAppium(String flagString) {
		try {
			//killAppium();
			String appiumPath = null;
			String nodePath = null;
			if (System.getProperty("os.name").startsWith("Mac")) {
				Properties bashProfile = new Properties();
				String path = "/Users/" + System.getProperty("user.name") + "/.bash_profile";
				try {
					bashProfile.load(new FileInputStream(path));
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				appiumPath = bashProfile.getProperty("APPIUM_HOME");
				System.out.println("Appium Path is : " + appiumPath + "\n");
				nodePath = bashProfile.getProperty("NODE_HOME");
				System.out.println("Node Path is : " + nodePath + "\n");

				System.out.println("Finding Appium path from bash profile ...............!!!");
			} else {
				nodePath = serachMachineForFile("node.exe");
				appiumPath = serachMachineForFile("appium.js");
			}
			
			String invokeAdbString="\""+nodePath+"\" \""+appiumPath+"\" "+flagString;
			System.out.println(invokeAdbString);
			Process p = null;
			if(System.getProperty("os.name").startsWith("Mac")) {
				invokeAdbScript(invokeAdbString);
				String adbScript = "adbScript.sh";
				p = Runtime.getRuntime().exec("bash " + adbScript);
			}
			else {
			 p = Runtime.getRuntime().exec(invokeAdbString);
			}
			BufferedReader bfr = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String line = bfr.readLine();
			System.out.println("Appim invoked, thread state is : "
					+ testSuite.getState());
			if (line == null) {
				bfr = new BufferedReader(new InputStreamReader(
						p.getErrorStream()));
				line = bfr.readLine();
				System.out.println(line);
			}
			while (line != null) {
				System.out.println(line);
				if (line.contains("Welcome to Appium")) {
					appiumStarted = true;
					if (testSuite.getState().equals(Thread.State.RUNNABLE)
							|| testSuite.getState().equals(
									Thread.State.TIMED_WAITING))
						testSuite.resume();
				}
				line = bfr.readLine();
				if (!p.isAlive()) {
					testSuite.setAppiumRestarted(true);
					testSuite.suspend();
					invokeAppium(flagString);
				}
				/*
				 * else if(testSuite.getState().equals(Thread.State.BLOCKED))
				 * testSuite.resume();
				 */
			}
			bfr.close();
		}
		/*
		 * catch(AppiumNotResetException anre) {
		 * System.err.println(anre.getMessage()); System.exit(0); }
		 */
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Preapares a string of all the flags for the current appium instance using
	 * {@link #appiumConfig}
	 * 
	 * @param appiumConfig
	 *            {@link #appiumConfig}
	 * @return a string mentioning all the flags to run this instance of appium
	 */
	public String prepareInputFlagString(Properties appiumConfig) {
		String flags = "";
		try {
			Iterator<Map.Entry<Object, Object>> appiumConfigItr = appiumConfig
					.entrySet().iterator();
			while (appiumConfigItr.hasNext()) {
				String key = (String) appiumConfigItr.next().getKey();
				if (key.startsWith("flag")) {
					flags += key.substring(4, key.length());
					if (!appiumConfig.getProperty(key).equals("-"))
						flags += " " + appiumConfig.getProperty(key);
					flags += " ";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flags;
	}

	public void invokeAdbScript(String invokeAppiumString) {
		File file = new File("adbScript.sh");
		Properties bashProfile = new Properties();
		String path = "/Users/"+System.getProperty("user.name")+"/.bash_profile";
		try {
			bashProfile.load(new FileInputStream(path));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		String sdkPath = bashProfile.getProperty("ANDROID_HOME");
		System.out.println("Sdk Path:"+ sdkPath);
		if (file.exists()) {
			file.delete();
		}
		try {
			file.createNewFile();
			FileWriter fw = new FileWriter(file);
			fw.write("export ANDROID_HOME="+sdkPath+" && "+invokeAppiumString);
			System.out.println(invokeAppiumString);
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void searchDirectory(){
		
	}

	/**
	 * Method to specifying the tasks to be run in parallel
	 */
	@Override
	public void run() {
		invokeAppium(prepareInputFlagString(appiumConfig));
	}
}