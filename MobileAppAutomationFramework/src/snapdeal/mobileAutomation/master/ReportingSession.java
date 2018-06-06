package snapdeal.mobileAutomation.master;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.WebDriver;

import testCaseReporting.Reporting;
import testCaseReporting.SuiteReporting;

/**
 * This Class is used to maintain a session of the parameters used while reporting for the current suite 
 * @author Khagesh Kapil
 * @see ExecutionSession
 */
public class ReportingSession extends Session{
	
	static boolean consolidatedFolderCreated = false;
	
	static boolean warNameComputed = false;
	
	public static String warFileName = "";
	
	static String mailSubject = "Automation execution report : "+Calendar.getInstance().getTime();
	
	private final String NAME_OF_CONSOLIDATE_FOLDER = "Snapdeal_AutomationResults_";
	
	private final String NAME_OF_SUITE_FOLDER = "ExecutionResults_";
	
	private static final String NAME_OF_WORKSPACE_FOLDER = "snapdealResult";
	
	private SuiteReporting suiteReporting;

	/**
	 * A reference to the execution driver used to perform actions during the course of execution
	 * @see WebDriver
	 */
	private WebDriver executionDriver = null;
	
	/**
	 * Path to the folder containing results of all suites executed
	 */
	private String pathToConsolidateResult = System.getProperty("user.home");
	
	/**
	 * Path to the folder containing results for current suite execution
	 */
	private String pathToSuiteResult = "";

	private String flavour = "";
	
	private String deviceName = "";
	
	private String appVersion = "";
	
	private String environment  = "";  //added by ankit for Reporting
	
	private String branch ="";     //added by ankit for Reporting
	
	private String insertReport =""; //added by ankit for Reporting
	
	private String platformVerison = "";
    
	private String snapshotStrategy;
	
	private String suiteName;
	
	private static String basePath = null;
	
	private static String autoDeployPath = null;
	
	private static String mailingList = null;
	
	public String getBasePath() {
		return basePath;
	}

	private String computeBasePath(String autoDeployPath, String resultServerPort) throws UnknownHostException {
		String warName = "";
		Long timeStamp;
		int attempt = 0;
		do {
			timeStamp = System.currentTimeMillis();
			File warFile = new File(autoDeployPath+"/"+"ExecutionResults_"+timeStamp);
			System.out.println(autoDeployPath+"/"+"ExecutionResults_"+timeStamp);
			if(!warFile.exists()) {
				basePath = "http://"+InetAddress.getLocalHost().getHostAddress()+":"+resultServerPort+"/ExecutionResults_"+timeStamp+"/";
				warName = warFile.getName();
			}
		}
		while(++attempt<10 && !warName.endsWith(""));
		return warName;
	}
	
	public ReportingSession(String suiteName, AppiumSession appiumSession, String autoDeployPath, String mailingList, String resultServerPort, String insertReport, Properties masterAppiumOpts) throws UnknownHostException {
		if(!(autoDeployPath==null) && !autoDeployPath.equals("")) {
			ReportingSession.autoDeployPath = autoDeployPath;
			ReportingSession.mailingList = mailingList;
			if(!warNameComputed) {
				warFileName = computeBasePath(autoDeployPath,resultServerPort);
				warNameComputed = true;
			}
		}
		System.out.println(warFileName);
        this.flavour = appiumSession.getAppiumThread().getAppiumConfig().getProperty("flavour");
        this.deviceName = appiumSession.getAppiumThread().getAppiumConfig().getProperty("deviceName");
        this.platformVerison = appiumSession.getAppiumThread().getAppiumConfig().getProperty("platformVersion");
        this.appVersion = appiumSession.getAppiumThread().getAppiumConfig().getProperty("appVersion");
        this.environment = appiumSession.getAppiumThread().getAppiumConfig().getProperty("environment");
        this.branch = appiumSession.getAppiumThread().getAppiumConfig().getProperty("branch");
        this.insertReport = insertReport;
        
        try {
        	if((String)masterAppiumOpts.getProperty("ReportsDirectory") != null)
        		pathToConsolidateResult=pathToConsolidateResult+"/"+(String)masterAppiumOpts.getProperty("ReportsDirectory");
			pathToConsolidateResult = Reporting.createDir(pathToConsolidateResult, NAME_OF_CONSOLIDATE_FOLDER).getAbsolutePath();
//			if(!(basePath==null))
//				basePath += new File(pathToConsolidateResult).getName()+"/";
		} catch (IOException e) {
			e.printStackTrace();
		}
        this.suiteName = suiteName;
    }
	
	public SuiteReporting getSuiteReporting() {
		return suiteReporting;
	}

	public void startSuiteReporting(WebDriver driver) {
		suiteReporting = new SuiteReporting(suiteName, flavour,appVersion, deviceName, platformVerison, driver,
        		pathToConsolidateResult, flavour+"_versionInfo_"+NAME_OF_SUITE_FOLDER,
        		new File(System.getProperty("user.dir") + "/" + NAME_OF_WORKSPACE_FOLDER), basePath, environment, branch, insertReport);
		setExecutionDriver(driver);
        pathToSuiteResult = suiteReporting.getPathToSuiteFolder();
	}

	/**
	 * Getter method for {@link #executionDriver}
	 * @return {@link WebDriver}
	 */
	public WebDriver getExecutionDriver() {
		return executionDriver;
	}
	
	/**
	 * Setter Method for {@link #executionDriver}
	 * @param executionDriver
	 */
	public void setExecutionDriver(WebDriver executionDriver){
		this.executionDriver = executionDriver;
		suiteReporting.updateExecutionDriver(executionDriver);
	}
	
	/**
	 * Getter method for {@link #pathToConsolidateResult}
	 * @return
	 */
	public String getPathToConsolidateResult(){
		return this.pathToConsolidateResult;
	}
	
	/**
	 * Setter method for {@link #pathToConsolidateResult}
	 * @param pathToConsolidateResult
	 */
	public void setPathToConsolidateResult(String pathToConsolidateResult){
		this.pathToConsolidateResult = pathToConsolidateResult;
	}
	
	/**
	 * Getter method for {@link #pathToSuiteResult}
	 * @return 
	 */
	public  String getPathToSuiteFolder(){
		return this.pathToSuiteResult;
	}
	
	/**
	 * Setter method for {@link #pathToSuiteResult}
	 * @param pathToSuiteResult
	 */
	public void setPathToSuiteFolder(String pathToSuiteResult){
		this.pathToSuiteResult = pathToSuiteResult;
	}
	
	public String getFlavour() {
        return flavour;
    }
 
    public void setFlavour(String flavour) {
        this.flavour = flavour;
    }
    
    
	
    public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getPlatformVerison() {
		return platformVerison;
	}

	public void setPlatformVerison(String platformVerison) {
		this.platformVerison = platformVerison;
	}

	public String getSnapshotStrategy() {
		return snapshotStrategy;
	}

	public void setSnapshotStrategy(String snapshotStrategy) {
		this.snapshotStrategy = snapshotStrategy;
	}

	public static void concludeReports() {
//		MimeMultipart multipart = new MimeMultipart("related");
		String mailBody = "";
		File resultFolder = new File(System.getProperty("user.dir") + "/" + NAME_OF_WORKSPACE_FOLDER);
		String[] files = resultFolder.list();
		File webInfFile = new File(resultFolder.getAbsolutePath() + "/WEB-INF");
		webInfFile.mkdir();
		System.out.println("Debug log 1 for Conclude report");
		String welcomeFilesList = "<web-app><welcome-file-list>";
		for(String folder : files) {
			welcomeFilesList += "<welcome-file>" + folder + "/suite.html" + "</welcome-file>";
			File suitFile = new File(resultFolder.getAbsolutePath() + "/" + folder + "/suite.html");
	        try {
//	        	BodyPart messageBodyPart = new MimeBodyPart();
//		        DataSource fds = new FileDataSource
//		          (resultFolder.getAbsolutePath() + "/" + folder + "/suite.html");
//		        messageBodyPart.setDataHandler(new DataHandler(fds));
//				messageBodyPart/*.setContent(fds, "text/html");*/.setHeader("Content-Type","text/html");
//				multipart.addBodyPart(messageBodyPart);
	        	
	        	BufferedReader br = new BufferedReader(new FileReader(suitFile));
	        	String line;
	        	while((line = br.readLine())!=null){
	        		mailBody += line;
	        	}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        mailBody += "<br><br><br>";
		}
		System.out.println("Debug log 2 for Conclude report");
		welcomeFilesList += "</welcome-file-list>";
		welcomeFilesList += "</web-app>";
		
		OutputStream webXmlFileStream = null;
		OutputStream zipShellFileStream = null;
		OutputStream warShellFileStream = null;
		OutputStream deployShellFileStream = null;
		try {
			webXmlFileStream = new FileOutputStream(new File(
					webInfFile.getAbsolutePath() + "/web.xml"), true);
			PrintStream webXmlPrintStream = new PrintStream(webXmlFileStream);
			webXmlPrintStream.println(welcomeFilesList);
			webXmlPrintStream.flush();
			webXmlPrintStream.close();
			webXmlFileStream.close();
			
			File zipShellFile = new File(System.getProperty("user.dir")+"/zipFiles.sh");
			System.out.println("Debug log 3 for Conclude report");
			if(zipShellFile.exists())
				zipShellFile.delete();
			zipShellFileStream = new FileOutputStream(new File(
					System.getProperty("user.dir")+"/zipFiles.sh"), false);
			PrintStream zipShellFilePrintStream = new PrintStream(zipShellFileStream);
			zipShellFilePrintStream.println("cd " + resultFolder.getAbsolutePath().replaceAll("\\\\", "/") + " && jar cvf " + warFileName + ".zip *");
			System.out.println("Debug log 4 for Conclude report");
			zipShellFilePrintStream.flush();
			zipShellFilePrintStream.close();
			zipShellFileStream.close();
			System.out.println("Debug log 5 for Conclude report");
			File warShellFile = new File(System.getProperty("user.dir")+"/warFiles.sh");
			if(warShellFile.exists())
				warShellFile.delete();
			warShellFileStream = new FileOutputStream(new File(
					System.getProperty("user.dir")+"/warFiles.sh"), false);
			System.out.println("Debug log 6 for Conclude report");
			PrintStream warShellFilePrintStream = new PrintStream(warShellFileStream);
			warShellFilePrintStream.println("cd " + resultFolder.getAbsolutePath().replaceAll("\\\\", "/") + " && jar cvf " + warFileName + ".war *");
			warShellFilePrintStream.flush();
			warShellFilePrintStream.close();
			warShellFileStream.close();
			System.out.println("Debug log 7 for Conclude report");
			if(warNameComputed && autoDeployPath!=null && !autoDeployPath.equals("")) {
				File deployShellFile = new File(System.getProperty("user.dir")+"/autoDeploy.sh");
				if(deployShellFile.exists())
					deployShellFile.delete();
				deployShellFileStream = new FileOutputStream(new File(
						System.getProperty("user.dir")+"/autoDeploy.sh"), false);
				PrintStream deployShellFilePrintStream = new PrintStream(deployShellFileStream);
				System.out.println("Debug log 8 for Conclude report");
				deployShellFilePrintStream.println("cp " + resultFolder.getAbsolutePath().replaceAll("\\\\", "/") + "/" + warFileName + ".war " + autoDeployPath);
				deployShellFilePrintStream.flush();
				deployShellFilePrintStream.close();
				deployShellFileStream.close();
				System.out.println("Debug log 9 for Conclude report");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			if(warNameComputed) {
//				Runtime.getRuntime().exec("bash " + System.getProperty("user.dir") + "/zipFiles.sh").waitFor();
//				Runtime.getRuntime().exec("bash " + System.getProperty("user.dir") + "/warFiles.sh").waitFor();
				
				Process process = Runtime.getRuntime().exec("bash " + System.getProperty("user.dir") + "/zipFiles.sh");
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				while ((reader.readLine()) != null) {}
				process.waitFor();
				System.out.println("Debug log 10 for Conclude report");
				process = Runtime.getRuntime().exec("bash " + System.getProperty("user.dir") + "/warFiles.sh");
				reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				while ((reader.readLine()) != null) {}
				process.waitFor();
				System.out.println("Debug log 11 for Conclude report");
				
				if(autoDeployPath!=null && !autoDeployPath.equals(""))
//					Runtime.getRuntime().exec("bash " + System.getProperty("user.dir") + "/autoDeploy.sh").waitFor();
					process = Runtime.getRuntime().exec("bash " + System.getProperty("user.dir") + "/autoDeploy.sh");
					reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
					while ((reader.readLine()) != null) {}
					process.waitFor();
					System.out.println("Debug log 12 for Conclude report");
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		if(mailingList==null || mailingList.equals("[]"))
			return;
		
		String[] recipients = mailingList.substring(1, mailingList.length()-1).split(",");
		System.out.println("Debug log 13 for Conclude report");
		final String username = "mobileautomationreport@snapdeal.com";
		final String password = "qatesting";

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
//		props.put("mail.smtp.port", "465");
		props.put("mail.smtp.ssl.trust", "smtp.gmail.com"); // added by ankit

		javax.mail.Session session = javax.mail.Session.getInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				});
		MimeMessage message = new MimeMessage(session);
		System.out.println("Debug log 14 for Conclude report");
		try {
			message.setFrom(new InternetAddress("mobileautomationreport@snapdeal.com"));
			for(String rec : recipients){
				message.addRecipients(Message.RecipientType.TO,InternetAddress.parse(rec.trim()));
				System.out.println("rec :"+rec);
			}
			message.setSubject(mailSubject);
	
			message.setContent(mailBody, "text/html");
			
			
			Transport.send(message);
			System.out.println("Debug log 15 for Conclude report");
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		
	}
	
}
