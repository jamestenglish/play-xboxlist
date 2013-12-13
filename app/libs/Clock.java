package libs;

import java.util.Calendar;
import java.util.Date;
/**
 * This class is an interface to handle getting the current date and calendar
 * This abstraction makes it easier to handle unit testing of the time sensitive
 * restrictions and is based off of the answer of this stack overflow question:
 * 
 * http://stackoverflow.com/questions/2001671/override-java-system-currenttimemillis-for-testing-time-sensitive-code
 */
public interface Clock {
	
	/** gets and instance to a calendar object */
	public Calendar getCalendarInstance();
	
	/** gets a new Date object */
	public Date getNewDate();
}
