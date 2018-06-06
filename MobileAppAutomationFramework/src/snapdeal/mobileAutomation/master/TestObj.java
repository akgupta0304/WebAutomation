package snapdeal.mobileAutomation.master;

import io.appium.java_client.MobileBy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * This class represents a {@link WebElement} to be used with its individual {@link ObjectEntry} on the basis of 9 locators i.e.,
 * Name
 * Xpath
 * Id
 * Class
 * TagName
 * PartialTagName
 * Css
 * LinkText
 * IosUIAutomation
 * @author Khagesh Kapil
 * @see TestCase
 */
public class TestObj {

	/**
	 * Set of {@link ObjectEntry} for this {@link TestObj}
	 */
	private SortedSet<ObjectEntry> objectEntries;
	
	/**
	 * Field specifying the highest sequence of entry specified in this {@link TestObj}
	 */
	int maxSeq = 0;
	
	/**
	 * Field specifying the age of this {@link TestObj}
	 */
	private long objAge = 0;
	
	/**
	 * Field specifying the name for this {@link TestObj}
	 */
	String ObjName;

	private String uiAutomationObject;
	
	/**
	 * Default Constructor
	 */
	TestObj() {
		objectEntries = new TreeSet<ObjectEntry>();
		objectEntries.add(new ObjectEntry("_class", null, null, -1,"CLASS_NAME"));
		objectEntries.add(new ObjectEntry("_css", null, null, -2,"CSS_SELECTOR"));
		objectEntries.add(new ObjectEntry("_id", null, null, -3,"ID"));
		objectEntries.add(new ObjectEntry("_linkText", null, null, -4,"LINK_TEXT"));
		objectEntries.add(new ObjectEntry("_xpath", null, null, -5,"XPATH"));
		objectEntries.add(new ObjectEntry("_name", null, null, -6,"NAME"));
		objectEntries.add(new ObjectEntry("_tag", null, null, -7,"TAG_NAME"));
		objectEntries.add(new ObjectEntry("_partialTag", null, null, -8,"PARTIAL_LINK_TEXT"));
		objectEntries.add(new ObjectEntry("_IosUIAutomation", null, null, -9,"IOS_UI_AUTOMATION"));
	}
	
	/**
	 * Getter Method for {@link #objAge}
	 * @return
	 */
	public Long getAge() {
		return objAge;
	}
	
	/**
	 * Setter method for {@link #objAge}
	 * @param age
	 */
	public void setAge(Long age) {
		objAge = Long.valueOf(age);
	}
	
	/**
	 * Method to get {@link Set} of ObjectEntry with not null element list
	 * @return
	 */
	public SortedSet<ObjectEntry> getEntriesSet() {
		SortedSet<ObjectEntry> myObjectEntries = new TreeSet<ObjectEntry> ();
		for(ObjectEntry objectEntry : objectEntries) {
			if(objectEntry.getSeqNum()>0)
				myObjectEntries.add(objectEntry);
		}
		return myObjectEntries;
	}
	
	/**
	 * Getter method for {@link #objectEntries}
	 * @return
	 */
	public SortedSet<ObjectEntry> getEntries() {
		return objectEntries;
	}
	
	/**
	 * Getter method for {@link #maxSeq}
	 * @return
	 */
	public int getMaxSeq() {
		return maxSeq;
	}

	/**
	 * Setter method for {@link #maxSeq}
	 */
	public void setMaxSequence() {
		try {
//			for(ObjectEntry objectEntry : objectEntries) {
//				if(objectEntry.getSeqNum()>0)
//					maxSeq = objectEntry.getSeqNum();
//			}
		maxSeq = objectEntries.last().getSeqNum();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to get {@link String} representation of this {@link TestObj}
	 */
	@Override
	public String toString() {
		String objectEntriesDesc = "";
		Iterator<ObjectEntry> entryIterator = objectEntries.iterator();
		while(entryIterator.hasNext()) {
			objectEntriesDesc += entryIterator.next().toString();
			objectEntriesDesc += "\n\n";
		}
		return "object name = " + ObjName + "\n" + "object age = " + objAge
				+ "\n" + "max sequence = " + maxSeq + "\n"
				+ objectEntriesDesc;
	}

	/**
	 * Method to get the {@link ObjectEntry} from this {@link TestObj} using the given Key name
	 * @param key name of key for {@link ObjectEntry} to be extracted
	 * @return
	 */
	ObjectEntry getObjEntryByKey(String key) {
		ObjectEntry o = null;
		try {
			for(ObjectEntry objectEntry : objectEntries) {
				if(objectEntry.getKey().equals(key)) {
					o = objectEntry;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return o;
	}

	/**
	 * 
	 * @return
	 */
	List<ObjectEntry> getUserObjSequence() {
		return new ArrayList<ObjectEntry>();
	}
	
	public boolean isWaitSuccessful() {
		boolean waitSuccessful = false;
		Iterator<ObjectEntry> objectEntries = getEntriesSet().iterator();
		while(objectEntries.hasNext()) {
			ObjectEntry objectEntry = objectEntries.next();
			if(objectEntry.getWaitInfo()==-1) {
				waitSuccessful = true;
				break;
			}
		}
		return waitSuccessful;
	}
	
	public void setUiAutoObj(String uiAutoObj) {
		uiAutomationObject = uiAutoObj;
	}
	
	public String getUiAutoObj() {
		return uiAutomationObject;
	}
}

/**
 * 
 * @author Khagesh Kapil
 * @see TestObj
 */
final class ObjectEntry implements Map.Entry<String, List<WebElement>>, Comparable<ObjectEntry>, TestObjEntry {
	
	/**
	 * Field specifying type of identifier used
	 */
	private String objIdentifier;
	
	/**
	 * Field specifying List of elements on the basis of {@link #objIdentifier}
	 */
	private List<WebElement> objElem;
	
	/**
	 * A reference of {@link By}
	 */
	private By by;
	
	/**
	 * Field specifying sequence no. of this entry
	 */
	private int seqNum;
	
	/**
	 * Field specifying wait info on the basis of this identifier
	 */
	private int waitInfo = 0;
	
	/**
	 * Field Specifying {@link By} Object for this {@link ObjectEntry}
	 */
	private String byKey; 

	/**
	 * Setter method for {@link #byKey}
	 */
	public void setByKey(String byKey) {
		this.byKey = byKey;
	}
	
	/**
	 * Getter method for {@link #byKey}
	 * @return
	 */
	public String getByKey() {
		return byKey;
	}
	
	/**
	 * Setter method for {@link #waitInfo}
	 * @param wi
	 */
	public void setWaitInfo(int wi) {
		waitInfo = wi;
	}

	/**
	 * Getter method for {@link #waitInfo}
	 * @return
	 */
	public int getWaitInfo() {
		return this.waitInfo;
	}
	
	/**
	 * Method to get textual represntation of this {@link ObjectEntry}
	 */
	@Override
	public String toString() {
		String elemDesc = "";
		if (!(objElem == null)) {
			elemDesc += "("+objElem.size()+"):{";
			Iterator<WebElement> elemItr = objElem.iterator();
			while (elemItr.hasNext()) {
				elemDesc += elemItr.next().toString() + ",";
			}
			elemDesc += "}";
		} else
			elemDesc = null;
		return "key = " + objIdentifier + " elementList = " + elemDesc
				+ " By = " + by + " sequence no. = " + seqNum + " wait info = "
				+ waitInfo + " byKey = "+ byKey;
	}

	/**
	 * Constructor
	 * @param key name of locator for this {@link ObjectEntry} Object
	 * @param objElements List of {@link WebElement}
	 * @param b {@link By} Object
	 * @param orderNo sequence no. of this entry in {@link TestObj}
	 * @param byKey String representation of {@link By} Object
	 */
	ObjectEntry(String key, List<WebElement> objElements, By b, int orderNo, String byKey) {
		objIdentifier = key;
		objElem = objElements;
		by = b;
		seqNum = orderNo;
		this.byKey = byKey;
	}

	/**
	 * Getter method for {@link #seqNum}
	 * @return
	 */
 	public int getSeqNum() {
		return seqNum;
	}

 	/**
 	 * Getter method for {@link #objElem}
 	 */
	public List<WebElement> getValue() {
		return objElem;
	}

	/**
	 * Getter method for {@link #objIdentifier}
	 */
	public String getKey() {
		return objIdentifier;
	}

	/**
	 * Setter method for {@link #objElem}
	 */
	public List<WebElement> setValue(List<WebElement> objElements) {
		this.objElem = objElements;
		return this.objElem;
	}

	/**
	 * Getter method for {@link #objElem}
	 * @param objElements
	 * @return
	 */
	public ObjectEntry getObjEntryWithVal(List<WebElement> objElements) {
		this.objElem = objElements;
		return this;
	}

	/**
	 * Setter method for {@link #seqNum}
	 * @param orderNo
	 * @return
	 */
	public ObjectEntry setSeqNum(int orderNo) {
		this.seqNum = orderNo;
		return this;
	}

	/**
	 * Setter method for {@link #by}
	 * @param b
	 * @return
	 */
	public ObjectEntry setByVal(By b) {
		this.by = b;
		return this;
	}

	/**
	 * Getter method for {@link #byKey}
	 * @return
	 */
	public By getByVal() {
		return by;
	}

	/**
	 * Method to specify algorithm for comparing 2 {@link ObjectEntry} Objects
	 */
	@Override
	public int compareTo(ObjectEntry objectEntry) {
		int sequence1 = this.getSeqNum();
		int sequence2 = objectEntry.getSeqNum();
		if(this.getKey().equals(objectEntry.getKey())) {
			return 0;
		}
		if(sequence1 > sequence2) {
			return 1;
		}
		else
			return -1;
	}
	
//	@Override
//	public int hashCode() {
//		char[] keyArr = objIdentifier.toCharArray();
//		int i=0;
//		for(char c: keyArr) {
//			i += Character.getNumericValue(c);
//		}
//		return i;
//	}
}
