package testCaseReporting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestCaseReporting extends Reporting{

	public OutputStream htmlfile, logfile;
	public PrintStream printhtml, printlog;
	public FileInputStream fis = null;
	public BufferedReader reader = null;
	private int PassCount = 0;
	private int FailCount = 0;
	private int infoCount = 0;
	public long startTime = 0;
	public long lastTime = 0;
	public String testCaseName = "";
	SuiteReporting suiteReporting;
	private String snapshotStrategy;
	private boolean logging = false;
	private boolean shouldReportBeShipped = false;
	private String testDataId;
	private boolean footerMade = false;
	private int apiRow = 0;
	private String flavour;
	private String udid;
//	WebDriver lastExecutionDriver = null;
//	ReportingSession reportingSession = null;
	
	public TestCaseReporting(String testCaseName, SuiteReporting suiteReporting, String snapshotStrategy, boolean logging, boolean isLastTestCase, String testDataId, String flavour, String udid){
		super(suiteReporting.getDriver());
		this.testDataId = testDataId;
		setSuiteReporting(suiteReporting);
		setTestCaseName(testCaseName);
		this.snapshotStrategy = snapshotStrategy;
		shouldReportBeShipped = isLastTestCase;
		this.flavour = flavour;
		this.udid = udid;
		this.logging = logging;
//		this.reportingSession = reportingSession;
	}
	
	public void setSuiteReporting(SuiteReporting suiteReporting) {
		this.suiteReporting = suiteReporting;
	}

	public void setTestCaseName(String tcName) {
		this.testCaseName = getName(tcName);
	}

	public void Openfile() {
		try {
			htmlfile = new FileOutputStream(new File(suiteReporting.getPathToSuiteFolder()
					+ "/TestCase/" + testCaseName + "_"+testDataId+".html"), true);
			printhtml = new PrintStream(htmlfile);
			
			if (logging) {
				logfile = new FileOutputStream(new File(
						suiteReporting.getPathToSuiteFolder() + "/Logs/" + testCaseName + "_" + testDataId + ".txt"),
						true);
				printlog = new PrintStream(logfile);
				System.setOut(printlog);
				System.setErr(printlog);
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void addStepReport(String actual,String expected,boolean status) {
		String stat = null;
		if(status)
			stat = "PASS";
		else
			stat = "FAIL";
		teststepreporting(actual,stat, expected);
	}
	
	public void teststepreporting(String strActualResult, String strPassFail,
			String sExpectedMessage) {
		boolean snapshotPermitted = snapshotStrategy.equalsIgnoreCase("only on failure")?strPassFail.equalsIgnoreCase("FAIL"):
			snapshotStrategy.equalsIgnoreCase("everytime");
		try {
			String nameOfScreenShot ="";
			String imgLink = "";
			Openfile();

			int TeststepCount = PassCount + FailCount + infoCount + 1;

			printhtml.append("<tr>");

			printhtml
					.append("<td width='13%' bgcolor='#FFFFDC' valign='middle' align='center' ><font color='#000000' face='Tahoma' size='2'>"
							+ TeststepCount + "</font></td>");
			printhtml
					.append("<td width='22%' bgcolor='#FFFFDC' valign='top' align='justify' ><font color='#000000' face='Tahoma' size='2'>"
							+ strActualResult + "</font></td>");
			printhtml
					.append("<td width='22%' bgcolor='#FFFFDC' valign='top' align='justify' ><font color='#000000' face='Tahoma' size='2'>"
							+ sExpectedMessage + "</font></td>");
			if (strPassFail.toUpperCase().equals("PASS")) {
				printhtml
						.append("<td width='18%' bgcolor='#FFFFDC' valign='middle' align='center'><b><font color='#000000' face='Tahoma' size='2'>"
								+ strPassFail.toUpperCase

								() + "</font></b></td>");
				PassCount = PassCount + 1;
			} else if (strPassFail.toUpperCase().equals("INFO")) {
				printhtml
						.append("<td width='18%' bgcolor='#FFFFDC' valign='middle' align='center'><b><font color='#000000' face='Tahoma' size='2'>"
								+ strPassFail.toUpperCase

								() + "</font></b></td>");
				infoCount = infoCount + 1;
			} else {
				printhtml
						.append("<td width='18%' bgcolor='#FFFFDC' valign='middle' align='center'><b><font color='Red' face='Tahoma' size='2'>"
								+ strPassFail.toUpperCase() +

								"</font></b></td>");

				FailCount = FailCount + 1;
			}
			if(snapshotPermitted) {
				String suiteFlavour = this.flavour;
				if(suiteFlavour.equalsIgnoreCase("ANDROIDAPP") && System.getProperty("os.name").startsWith("Mac"))
					nameOfScreenShot = captureImageAndroid(suiteReporting.getPathToSuiteFolder(), suiteReporting.getDriver(), this.udid);
				else
					nameOfScreenShot = captureImage(suiteReporting.getPathToSuiteFolder(), suiteReporting.getDriver());
				imgLink = "<a href=\"" + "../ScreenShot/"
						+ nameOfScreenShot + "\">Snapshot</a>";
			}
			else {
				nameOfScreenShot = "No Snapshot";
				imgLink = nameOfScreenShot;
			}
			//"<a href='" + nameOfScreenShot + "'>" + "Snapshot</a>"
			printhtml
			.append("<td width='18%' bgcolor='#FFFFDC' valign='middle' align='center'><b><font color='#000000' face='Tahoma' size='2'>"
				 + imgLink +
					"</font></b></td>");
			printhtml
			.append("<td width='13%' bgcolor='#FFFFDC' valign='middle' align='center' ><font color='#000000' face='Tahoma' size='2'>"
					+ getCurrentTime() + "</font></td>");	
			printhtml.append("</tr>");
       
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		
	}

	public void teststepreporting(String strActualResult, String strPassFail,
			String sExpectedMessage, boolean takeScreenshot) {
		boolean snapshotPermitted = takeScreenshot;
		
		try {
			String nameOfScreenShot ="";
			String imgLink = "";
			Openfile();

			int TeststepCount = PassCount + FailCount + infoCount + 1;

			printhtml.append("<tr>");

			printhtml
					.append("<td width='13%' bgcolor='#FFFFDC' valign='middle' align='center' ><font color='#000000' face='Tahoma' size='2'>"
							+ TeststepCount + "</font></td>");
			printhtml
					.append("<td width='22%' bgcolor='#FFFFDC' valign='top' align='justify' ><font color='#000000' face='Tahoma' size='2'>"
							+ strActualResult + "</font></td>");
			printhtml
					.append("<td width='22%' bgcolor='#FFFFDC' valign='top' align='justify' ><font color='#000000' face='Tahoma' size='2'>"
							+ sExpectedMessage + "</font></td>");
			if (strPassFail.toUpperCase().equals("PASS")) {
				printhtml
						.append("<td width='18%' bgcolor='#FFFFDC' valign='middle' align='center'><b><font color='#000000' face='Tahoma' size='2'>"
								+ strPassFail.toUpperCase

								() + "</font></b></td>");
				PassCount = PassCount + 1;
			} else if (strPassFail.toUpperCase().equals("INFO")) {
				printhtml
						.append("<td width='18%' bgcolor='#FFFFDC' valign='middle' align='center'><b><font color='#000000' face='Tahoma' size='2'>"
								+ strPassFail.toUpperCase

								() + "</font></b></td>");
				infoCount = infoCount + 1;
			} else {
				printhtml
						.append("<td width='18%' bgcolor='#FFFFDC' valign='middle' align='center'><b><font color='Red' face='Tahoma' size='2'>"
								+ strPassFail.toUpperCase() +

								"</font></b></td>");

				FailCount = FailCount + 1;
			}
			if(snapshotPermitted) {
				String suiteFlavour = this.flavour;
				if(suiteFlavour.equalsIgnoreCase("ANDROIDAPP") && System.getProperty("os.name").startsWith("Mac"))
					nameOfScreenShot = captureImageAndroid(suiteReporting.getPathToSuiteFolder(), suiteReporting.getDriver(), this.udid);
				else
					nameOfScreenShot = captureImage(suiteReporting.getPathToSuiteFolder(), suiteReporting.getDriver());
				imgLink = "<a href=\"" + "../ScreenShot/"
						+ nameOfScreenShot + "\">Snapshot</a>";
			}
			else {
				nameOfScreenShot = "No Snapshot";
				imgLink = nameOfScreenShot;
			}
			//"<a href='" + nameOfScreenShot + "'>" + "Snapshot</a>"
			printhtml
			.append("<td width='18%' bgcolor='#FFFFDC' valign='middle' align='center'><b><font color='#000000' face='Tahoma' size='2'>"
				 + imgLink +
					"</font></b></td>");
			printhtml
			.append("<td width='13%' bgcolor='#FFFFDC' valign='middle' align='center' ><font color='#000000' face='Tahoma' size='2'>"
					+ getCurrentTime() + "</font></td>");
			
			printhtml.append("</tr>");

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		
	}
	
	public static String getCurrentTime(){
	       DateFormat df = new SimpleDateFormat("HH:mm:ss");
	       Date dateobj = new Date();
	       return df.format(dateobj);
	}

	
	public void header() {
		try {
			startTime = getTime();
			Openfile();
			String complementryInfo = " for <span style=\"color:red\">"+suiteReporting.getFlavor()+"</span>";
			printhtml.println("</table>");
			printhtml.println("<html>");
			printhtml.println("<title> Test Script Report </title>");
			printhtml.println("<head></head>");
			printhtml.println("<body>");
			printhtml.println("<script type=\"text/javascript\">"+

    "function toggle_visibility(id,id1) {"+
       "var e = document.getElementById(id);"+
       
       
       "if(e.style.display == 'block') {"+
       "document.getElementById(id1).innerHTML = \"+\";"+
          "e.style.display = 'none';"+
          
       "}"+
       "else {"+
       "document.getElementById(id1).innerHTML = \"-\";"+
          "e.style.display = 'block';"+
          
       "}"+
    "}"+
"</script>");
			printhtml.println("<font face='Tahoma'size='2'>");
			printhtml.println("<h2 align='center'><span style=\"color:red\">"+ testCaseName + "</span> Execution Report"+complementryInfo+"</h2>");
			printhtml
					.println("<h3 align='right' ><font color='#000000' face='Tahoma' size='3'></font></h3>");
			printhtml.println("<h3 align='center'>TestData ID : <span style=\"color:red\">"+testDataId+"</span> </h3>");
			printhtml.println("<table border='0' width='100%' height='47'>");
			printhtml.println("<tr>");
			printhtml
					.println("<td width='2%' bgcolor='#CCCCFF' align='center'><b><font color='#000000' face='Tahoma' size='2'>TestCaseID</font></b></td>");
			printhtml
					.println("<td width='52%' bgcolor='#CCCCFF'align='center'><b><font color='#000000' face='Tahoma' size='2'>Actual Result</font></b></td>");
			printhtml
					.println("<td width='52%' bgcolor='#CCCCFF'align='center'><b><font color='#000000' face='Tahoma' size='2'>Expected Result</font></b></td>");
			printhtml
					.println("<td width='28%' bgcolor='#CCCCFF' align='center'><b><font color='#000000' face='Tahoma' size='2'>Pass/Fail</font></b></td>");
			printhtml
			.println("<td width='28%' bgcolor='#CCCCFF' align='center'><b><font color='#000000' face='Tahoma' size='2'>ScreenShot</font></b></td>");
			printhtml
			.println("<td width='28%' bgcolor='#CCCCFF' align='center'><b><font color='#000000' face='Tahoma' size='2'>CurrentTime</font></b></td>");

			printhtml.println("</tr>");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public String getTotalExecutionTime(long starttime, long endtime) {
		long diff = endtime - starttime;
		long diffSeconds = diff / 1000 % 60;
		long diffMinutes = diff / (60 * 1000) % 60;
		long diffHours = diff / (60 * 60 * 1000) % 24;
		// long diffDays = diff / (24 * 60 * 60 * 1000);
		return (diffHours + ":" + diffMinutes + ":" + diffSeconds);
	}

	public void footer() {
		try {
			if(footerMade)
				return;
			footerMade = true;
			lastTime = getTime();
			Openfile();
			int SuccessRate = getSuccessRate();
			String FailRate = Integer.toString(100 - SuccessRate);
			int Passwidth = (300 * SuccessRate) / 100;
			String Failwidth = Integer.toString(300 - Passwidth);
			printhtml.println("<hr>");
			printhtml.println("<table border='0' width='50%'>");
			printhtml
					.println("<tr><td width='100%' colspan='2' bgcolor='#000000'><b><font face='Tahoma' size='2' color='#FFFFFF'>Test Case Details :</font></b></td></tr>");
			printhtml
					.println("<tr><td width='45%' bgcolor='#FFFFDC'><b><font face='Tahoma' size='2'>Total Tests Passed</font></b></td><td width='55%' bgcolor='#FFFFDC'><font face='Tahoma' size='2'>"
							+ getTotoalTestStepCount() + "</td></tr>");
			printhtml
					.println("<tr><td width='45%' bgcolor='#FFFFDC'><b><font face='Tahoma' size='2'>Total Tests Failed</font></b></td><td width='55%' bgcolor='#FFFFDC'><font face='Tahoma' size='2'>"
							+ FailCount + "</td></tr>");
			printhtml
					.println("<tr><td width='45%' bgcolor='#FFFFDC'><b><font face='Tahoma' size='2'>Executed On (DD.MM.YYYY)</font></b></td><td width='55%' bgcolor= '#FFFFDC'><font face='Tahoma' size='2'>"
							+ getSystemDate() + "</td></tr>");
			printhtml
					.println("<tr><td width='45%' bgcolor='#FFFFDC'><b><font face='Tahoma' size='2'>Start Time (HH:MM:SS)</font></b></td><td width='55%' bgcolor= '#FFFFDC'><font face='Tahoma' size='2'>"
							+ getSystemTime(startTime) + "</td></tr>");
			printhtml
					.println("<tr><td width='45%' bgcolor='#FFFFDC'><b><font face='Tahoma' size='2'>End Time (HH:MM:SS)</font></b></td><td width='55%' bgcolor= '#FFFFDC'><font face='Tahoma' size='2'>"
							+ getSystemTime(lastTime) + "</td></tr>");
			printhtml
					.println("<tr><td width='45%' bgcolor='#FFFFDC'><b><font face='Tahoma' size='2'>Execution Time (MM.SS)</font></b></td><td width='55%' bgcolor= '#FFFFDC'><font face='Tahoma' size='2'>"
							+ getTotalExecutionTime(startTime, lastTime)
							+ "</td></tr>");
			printhtml.println("</table>");
			printhtml
					.println("<table border=0 cellspacing=1 cellpadding=1 ></table>");
			printhtml
					.println("<table border=0 cellspacing=1 cellpadding=1 ><tr><td width='100%' colspan='2' bgcolor='#000000'><b><font face='Tahoma' size='2' color='#FFFFFF'>Test Result Summary :</font></b></td></tr></table>");
			printhtml
					.println("<table border=0 cellspacing=1 cellpadding=1 ><tr>  <td width=70 bgcolor= '#FFFFDC'><FONT  FACE='Tahoma' SIZE=2.75 ><b>Total Test</b></td> <td width=10 bgcolor= '#FFFFDC'><FONT  FACE='Tahoma' SIZE=2.75><b>:</b></td>     <td width=35 bgcolor= '#FFFFDC'><FONT FACE='Tahoma' SIZE=2.75><b>"
							+ getTotoalTestStepCount()
							+ "</b></td>  <td width=300 bgcolor='#E7A1B0'></td>  <td width=20><FONT COLOR='#000000' FACE='Tahoma' SIZE=1><b>100%</b></td></tr></table>");
			printhtml
					.println("<table border=0 cellspacing=1 cellpadding=1 ><tr>  <td width=70 bgcolor= '#FFFFDC'><FONT  FACE='Tahoma' SIZE=2.75 ><b>Total Pass</b></td> <td width=10 bgcolor= '#FFFFDC'><FONT  FACE='Tahoma' SIZE=2.75><b>:</b></td>     <td width=35 bgcolor= '#FFFFDC'><FONT FACE='Tahoma' SIZE=2.75><b>"
							+ PassCount
							+ "</b></td>  <td width= "
							+ Passwidth
							+ " bgcolor='#008000'></td>  <td width=20><FONT COLOR='#000000' FACE='Tahoma' SIZE=1><b>"
							+ SuccessRate + "%</b></td></tr></table>");
			printhtml
					.println("<table border=0 cellspacing=1 cellpadding=1 ><tr> <td width=70 bgcolor= '#FFFFDC'><FONT   FACE='Tahoma' SIZE=2.75 ><b>Total Fail</b></td>  <td width=10 bgcolor= '#FFFFDC'><FONT  FACE='Tahoma' SIZE=2.75><b>:</b></td>     <td width=35 bgcolor= '#FFFFDC'><FONT  FACE='Tahoma' SIZE=2.75><b>"
							+ FailCount
							+ "</b></td>   <td width= "
							+ Failwidth
							+ " bgcolor='#FF0000'></td>     <td width=20><FONT COLOR='#000000' FACE='Tahoma' SIZE=1><b>"
							+ FailRate + "%</b></td> </tr></table>");
			printhtml.println("</font>");
			printhtml.println("</body>");
			printhtml.println("</html>");
			String testCaseHtmlName = testCaseName + "_" + testDataId;
			suiteReporting.addTestToConsolidate(FailCount>0?"FAIL":"PASS",testCaseName,"TestCase/"
					+ testCaseHtmlName + ".html\" id=\""+ testCaseHtmlName +"\" onfocus=\"demoDisplay1('"+ testCaseHtmlName + "')\""
					+">TestCaseResultFile</a>",String.valueOf(SuccessRate), testDataId);
			if(shouldReportBeShipped)
				suiteReporting.consolidateResultFooter();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public int getTotoalTestStepCount() {
		return (PassCount + FailCount);
	}
	
	public int getSuccessRate() {
		int stepCount = getTotoalTestStepCount();
		if(stepCount == 0)
			return 0;
		else
			return (PassCount * 100 / (getTotoalTestStepCount()));
	}
	
	public void testStepApiReporting(String apiDetails,
			String response) {
		try {
			Openfile();

			printhtml.append("<tr>");

			printhtml
					.append("<td width='13%' bgcolor='#D3D3D3' valign='middle' align='center' ><font color='#000000' face='Tahoma' size='2'>API HIT</font></td>");
			apiDetails = apiDetails.replaceAll("\n", "<br/>");
			printhtml
					.append("<td width='22%' bgcolor='#D3D3D3' valign='top' align='left' ><font color='#000000' face='Tahoma' size='2'><b>Api Request<b/><br/>"
							+ apiDetails + "</font></td>");
			
			printhtml
				.append("<td width='22%' bgcolor='#D3D3D3' valign='top' align='justify' ><font color='#000000' face='Tahoma' size='2'><b>Api Response </b>");
			setApiDetail(response);
			printhtml.append("</font></td>");

			printhtml
				.append("<td width='18%' bgcolor='#D3D3D3' valign='middle' align='center'><b><font color='#000000' face='Tahoma' size='2'>INFO</font></b></td>");
			infoCount = infoCount + 1;
			printhtml
				.append("<td width='18%' bgcolor='#D3D3D3' valign='middle' align='center'><b><font color='#000000' face='Tahoma' size='2'>No Snapshot</font></b></td>");
			printhtml
			.append("<td width='13%' bgcolor='#D3D3D3' valign='middle' align='center' ><font color='#000000' face='Tahoma' size='2'>"
					+ getCurrentTime() + "</font></td>");
			printhtml.append("</tr>");
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void setApiDetail(String apiResponse){
		try {
			Openfile();
			apiResponse = apiResponse.replaceAll("\n", "<br/>");
			apiResponse = apiResponse.replaceAll("\"", "\\\\\"");
			printhtml.append("<button id=\"expandCollapse"+apiRow+"\" onclick=\"toggle_visibility('response"+apiRow+"','expandCollapse"+apiRow+"');\">+</button>"+
			"<div id=\"response"+apiRow+"\" style=\"display:none;\">"+
			apiResponse+
			"</div>");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		apiRow++;
	} 
	
}
