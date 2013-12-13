package libs;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import play.mvc.Controller;

/**
 * 
 * This class handles the logic of authenticating a session.
 * Every time an action is performed (creating a game or casting a vote)
 * the user must wait until the next day until another action can be performed
 * 
 * Additionally the user is unable to perform any actions on the weekend.
 * 
 * This class checks the user's session and determines if they are authorized to perform an action
 *
 */
public class SessionAuthenticator {

	/** session key to store the last action time **/
	private static final String SESSION_LAST_ACTION = "lastAction";

	/**
	 * Determines if the user is authorized to perform an action
	 * @return if the user is authorized
	 */
	public static Boolean isAuthorized() {

		Calendar currentCalendar = ClockImpl.getInstance().getCalendarInstance();
		
		int dayOfTheWeek = currentCalendar.get(Calendar.DAY_OF_WEEK);
		if(dayOfTheWeek == Calendar.SUNDAY || dayOfTheWeek == Calendar.SATURDAY) {
			return false;
		}
		
    	String lastAction = Controller.session(SESSION_LAST_ACTION);
    	if(lastAction != null && !lastAction.isEmpty()) {
    		Calendar lastActionCalendar = getLastActionCalendar(lastAction);
    		boolean isCurrentDate = isCurrentDay(lastActionCalendar);
    		if(isCurrentDate) {
    			return false;
    		}
    	}
    	return true;
    }
	
	/**
	 * This should be called every time the user performs an action
	 */
	public static void actionPerformed() {
		String currentTimeInMillis = String.valueOf(ClockImpl.getInstance().getNewDate().getTime());
		Controller.session(SESSION_LAST_ACTION, currentTimeInMillis);
	}

	/**
	 * Checks if the date given by lastActionCalendar is in the same
	 * day as the current time
	 * @param lastActionCalendar calendar with time of the user's last action
	 * @return if lastActionCalendar's time is with the current day
	 */
	private static boolean isCurrentDay(Calendar lastActionCalendar) {
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date currentDate = ClockImpl.getInstance().getNewDate();
		Date lastActionDate = lastActionCalendar.getTime();
		return dateFormat.format(currentDate).equals(dateFormat.format(lastActionDate));
	}

	/**
	 * Since the session can only hold Strings this method
	 * converts the string time in millis to a calendar object
	 * 
	 * @param lastAction string representation of time in millis
	 * @return
	 */
	private static Calendar getLastActionCalendar(String lastAction) {
		
		Calendar lastActionCalendar = ClockImpl.getInstance().getCalendarInstance();
		try {
			lastActionCalendar.setTimeInMillis(Long.valueOf(lastAction));
		} catch(NumberFormatException e) {
			//Session appears to be corrupt
			//reset it
			Controller.session().remove(SESSION_LAST_ACTION);
		}
		return lastActionCalendar;
	}
}
