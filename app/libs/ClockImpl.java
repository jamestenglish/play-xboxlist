package libs;

import java.util.Calendar;
import java.util.Date;

/**
 * 
 * This class is an abstraction to handle getting the current date and calendar
 * This abstraction makes it easier to handle unit testing of the time sensitive
 * restrictions and is based off of the answer of this stack overflow question:
 * 
 * http://stackoverflow.com/questions/2001671/override-java-system-currenttimemillis-for-testing-time-sensitive-code
 */
public class ClockImpl implements Clock {

	/** singleton instance */
	private static Clock instance;
	
	/**
	 * Singleton, don't want anyone to instantiate
	 */
	private ClockImpl() {
		//do nothing;
	}
	
	/**
	 * 
	 * @return an instance to the clock singleton
	 */
	public static Clock getInstance() {
		if(instance == null) {
			instance = new ClockImpl();
		}
		return instance;
	}
	
	/**
	 * Sets the instance to be returned,
	 * if no instance is set a default will be created
	 * @param instance instance to be returned
	 */
	public static void setInstance(Clock instance) {
		ClockImpl.instance = instance;
	}
	
	@Override
	public Calendar getCalendarInstance() {
		return Calendar.getInstance();
	}
	
	@Override
	public Date getNewDate() {
		return new Date();
	}
}
