package testCaseReporting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;

public class Reporting {

	private WebDriver driver = null;
	
	//protected ReportingSession reportingSession = null;
	
//	public File destFolderPath = new File(System.getProperty("user.dir")
//			+ "/snapdealResult");
	
	Reporting(WebDriver driver){
		this.driver = new Augmenter().augment(driver);
		//this.reportingSession = reportingSession;
	}
	
	public String getSystemTime(long time) {
		DateFormat dateFormat = null;
		try {
			dateFormat = new SimpleDateFormat("HH:mm:ss");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return dateFormat.format(time);
	}

	public static long getTime() {
		Date date = new Date();
		return date.getTime();
	}

	public String getSystemDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	public String getName(String fullName) {
	    if (fullName.length() == 0)
	      System.out.println("Empty String Exception");
	    int index = fullName.lastIndexOf(".");
	    if (index != -1)
	    	 return fullName.substring(index+1, fullName.length());
	    else
	    	return fullName;
	  }
	
	public String captureImage(String pathToSnapshot, WebDriver driver){
		File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		File newImageFile = null;
		try {
			newImageFile = new File(pathToSnapshot + "/screenShot");
			newImageFile = File.createTempFile("screenShot", ".png",newImageFile);
			FileUtils.copyFile(scrFile, newImageFile);
		}
		catch (IOException e) {
		}
		return newImageFile.getName();
	}
	
	/**
	 * Take screenshot using ADB on Android device
	 * @author roopesh.sharma
	 * @param pathToSnapshot, driver, device_udid
	 * @return Image name 
	 */
	public String captureImageAndroid(String pathToSnapshot, WebDriver driver, String udid){
		String screenshotFileName = "appium_test.png";
		String screenshotFilePath = System.getProperty("user.dir");
		
		if(System.getProperty("os.name").startsWith("Mac")) {
			Properties bashProfile = new Properties();
			String path = "/Users/" + System.getProperty("user.name")+ "/.bash_profile";
			try {
				bashProfile.load(new FileInputStream(path));
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			String sdkPath = bashProfile.getProperty("ANDROID_HOME");
			
			executeCommand(new String[] { sdkPath+"/platform-tools/adb", "-s", udid, "shell",
		            "screencap", "-p", "/sdcard/" + screenshotFileName });
		    executeCommand(new String[] { sdkPath+"/platform-tools/adb", "-s", udid, "pull",
		            "/sdcard/" + screenshotFileName, screenshotFilePath });
		    executeCommand(new String[] { sdkPath+"/platform-tools/adb", "-s", udid, "shell",
		            "rm", "/sdcard/" + screenshotFileName });
		}else{
			executeCommand(new String[] { "adb", "-s", udid, "shell",
		            "screencap", "-p", "/sdcard/" + screenshotFileName });
		    executeCommand(new String[] { "adb", "-s", udid, "pull",
		            "/sdcard/" + screenshotFileName, screenshotFilePath });
		    executeCommand(new String[] { "adb", "-s", udid, "shell",
		            "rm", "/sdcard/" + screenshotFileName });
		}
		
		File scrFile = new File("appium_test.png");
		
		File newImageFile = null;
		try {
			newImageFile = new File(pathToSnapshot + "/screenShot");
			newImageFile = File.createTempFile("screenShot", ".png",newImageFile);
			FileUtils.copyFile(scrFile, newImageFile);
		}
		catch (IOException e) {
		}
		return newImageFile.getName();
	}
	
	/**
	 * Execute shell script
	 * @author roopesh.sharma
	 * @param args array
	 */
	private void executeCommand(String[] args) {
	    ProcessBuilder pb = new ProcessBuilder(args);
	    Process pc;
	    try {
	        pc = pb.start();
	        pc.waitFor();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public static File createDir(String pathTofolder, String folderName)
			throws IOException {
		final File sysDir = new File(pathTofolder);
		File newTempDir;
		final int maxAttempts = 9;
		int attemptCount = 0;
		do {
			attemptCount++;
			if (attemptCount > maxAttempts) {
				throw new IOException(
						"The highly improbable has occurred! Failed to "
								+ "create a unique temporary directory after "
								+ maxAttempts + " attempts.");
			}
			String dirName = folderName + getTime();
			newTempDir = new File(sysDir, dirName);
		} while (newTempDir.exists());

		if (newTempDir.mkdirs()) {
			return newTempDir;
		} else {
			throw new IOException("Failed to create temp dir named "
					+ newTempDir.getAbsolutePath());
		}
	}

	public static void deleteExistingFolder(File pathFolder)
	{
		if (pathFolder.exists()) {
			try {
				FileUtils.deleteDirectory(pathFolder);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void uploadingResult(String userName,String password,String uploadingJarName,String pathToFolder,String serverSideFolderName){
		try{		
		String jarPath = System.getProperty("user.dir")+ "/src/testCaseReporting/uploadResultJar/"+uploadingJarName;
		String serverParentFolder = serverSideFolderName +"/"+ getSystemDate().replace(":", "-");
		Process ps=Runtime.getRuntime().exec(new String[]{"java","-jar",jarPath,pathToFolder,"--username",userName,"--password",password,"--recursive","--remote-folder",serverParentFolder,"--without-conversion","--add-all","--protocol","https"});
	     
		boolean processComplete = false;
	     int trial = 0;
	     while(!processComplete){
	    	Thread.sleep(30000);
	    	java.io.InputStream is=ps.getInputStream();
	        byte b[]=new byte[is.available()];
	        is.read(b,0,b.length);        
	       System.out.println("Length:"+ b.length);
	        if(b.length < 1){
	        	trial++;
	        	System.out.println("Try to upload:" + trial);
	        	if(trial == 3){
	        		processComplete = true;
	        		ps.destroy();
	        		System.out.println("Uploading Done" + trial);
	        	}
	        }
	        else{
	        	trial = 0;
	        }
	       
	        }
	}
		catch(Exception ex){
			
		}
	}

	public WebDriver getDriver() {
		return driver;
	}
	
	protected void setDriver(WebDriver driver) {
		this.driver = driver;
	}
}
