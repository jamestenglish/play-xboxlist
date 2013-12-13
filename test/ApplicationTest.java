import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.GET;
import static play.test.Helpers.POST;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.header;
import static play.test.Helpers.redirectLocation;
import static play.test.Helpers.route;
import static play.test.Helpers.running;
import static play.test.Helpers.status;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import libs.Clock;
import libs.ClockImpl;
import models.Game;

import org.junit.Before;
import org.junit.Test;

import play.mvc.Http.HeaderNames;
import play.mvc.Http.Status;
import play.mvc.Result;


/**
*
* Simple (JUnit) tests that can call all parts of a play app.
* If you are interested in mocking a whole application, see the wiki for more details.
*
*/
public class ApplicationTest {
	
	/**
	 * Clock for December 12, 2013 (a Thursday)
	 */
	private Clock dec12Clock = new Clock() {

		private Calendar getCalendar() {
			Calendar result = Calendar.getInstance();
			result.set(2013, 11, 12);
			return result;
		}

		@Override
		public Calendar getCalendarInstance() {
			return getCalendar();
		}

		@Override
		public Date getNewDate() {
			return getCalendar().getTime();
		}

	};
	
	/**
	 * Clock for December 11, 2013 (a Wednesday)
	 */
	private Clock dec11Clock = new Clock() {

		private Calendar getCalendar() {
			Calendar result = Calendar.getInstance();
			result.set(2013, 11, 11);
			return result;
		}

		@Override
		public Calendar getCalendarInstance() {
			return getCalendar();
		}

		@Override
		public Date getNewDate() {
			return getCalendar().getTime();
		}

	};
	
	/**
	 * Clock for December 14, 2013 (a Saturday)
	 */
	private Clock saturdayClock = new Clock() {

		private Calendar getCalendar() {
			Calendar result = Calendar.getInstance();
			result.set(2013, 11, 14);
			return result;
		}

		@Override
		public Calendar getCalendarInstance() {
			return getCalendar();
		}

		@Override
		public Date getNewDate() {
			return getCalendar().getTime();
		}

	};
	
	/**
	 * Clock for December 15, 2013 (a Sunday)
	 */
	private Clock sundayClock = new Clock() {

		private Calendar getCalendar() {
			Calendar result = Calendar.getInstance();
			result.set(2013, 11, 14);
			return result;
		}

		@Override
		public Calendar getCalendarInstance() {
			return getCalendar();
		}

		@Override
		public Date getNewDate() {
			return getCalendar().getTime();
		}

	};
	
	/**
	 * Session cookie string to maintain session across calls
	 */
	private String sessionCookie = "";


	/**
	 * Clear out the session cookie before each test
	 */
	@Before
	public void onBeforeTest() {
		sessionCookie = "";
	}
	
	/**
	 * Test voting on different days,
	 * this should be allowed and work normally
	 */
	@Test
	public void voteDifferentDays() {
		running(fakeApplication(), new Runnable() {
			public void run() {
				
				ClockImpl.setInstance(dec11Clock);
				
				Game game = new Game();
				game.title = "test game";
				game.id = 1L;
				Game.create(game);

				
			    Result result = postAndFollowRedirect("/games/1/vote");
				assertThat(contentAsString(result)).contains("Vote tallied!");
				
				ClockImpl.setInstance(dec12Clock);
				
				result = postAndFollowRedirect("/games/1/vote");
				assertThat(contentAsString(result)).contains("Vote tallied!");
			}
			
			
		});
	}
	
	/**
	 * Test toggling game ownership
	 */
	@Test
	public void toggleOwnership() {
		running(fakeApplication(), new Runnable() {
			public void run() {
				
				ClockImpl.setInstance(dec11Clock);
				
				Game game = new Game();
				game.title = "test game";
				game.id = 1L;
				Game.create(game);

				
			    Result result = postAndFollowRedirect("/games/1/toggleOwnership");
				assertThat(contentAsString(result)).contains("Ownership status changed!");
				
				game = Game.find.ref(1L);
				assertThat(game.owned).isTrue();
				
				result = postAndFollowRedirect("/games/1/toggleOwnership");
				assertThat(contentAsString(result)).contains("Ownership status changed!");
				
				game = Game.find.ref(1L);
				assertThat(game.owned).isFalse();
			}
			
			
		});
	}
	
	/**
	 * Test voting within the same day,
	 * this should display an error message and not tally the 2nd vote
	 */
	@Test
	public void voteTwiceSameDay() {
		running(fakeApplication(), new Runnable() {
			public void run() {
				
				ClockImpl.setInstance(dec12Clock);
				
				Game game = new Game();
				game.title = "test game";
				game.id = 1L;
				Game.create(game);

				
			    Result result = postAndFollowRedirect("/games/1/vote");
				assertThat(contentAsString(result)).contains("Vote tallied!");
				
				
				result = postAndFollowRedirect("/games/1/vote");
				assertThat(contentAsString(result)).contains("You can not vote yet!");
			}
			
		
		});
	}
	
	/**
	 * Test adding a game in the same day,
	 * this should display an error and not add the 2nd game
	 */
	@Test
	public void addTwiceSameDay() {
		running(fakeApplication(), new Runnable() {
			public void run() {
				
				ClockImpl.setInstance(dec12Clock);
				

				Map<String, String> parameters = new HashMap<String, String>();
	            parameters.put("title", "test11");
			    Result result = postAndFollowRedirect("/games", parameters);
				assertThat(contentAsString(result)).contains("Game successfully added!");
				assertThat(contentAsString(result)).contains("test11");
				
				parameters = new HashMap<String, String>();
	            parameters.put("title", "test22");
				result = postAndFollowRedirect("/games", parameters);
				assertThat(contentAsString(result)).contains("You can not add another game yet!");
				assertThat(contentAsString(result)).contains("test11");
				assertThat(contentAsString(result)).doesNotContain("test22");
			}
			
		
		});
	}
	
	/**
	 * Test adding and voting in the same day,
	 * the add should work but the vote should error
	 */
	@Test
	public void addAndVoteSameDay() {
		running(fakeApplication(), new Runnable() {
			public void run() {
				
				ClockImpl.setInstance(dec12Clock);
				Game game = new Game();
				game.title = "test game";
				game.id = 9999L;
				Game.create(game);


				Map<String, String> parameters = new HashMap<String, String>();
	            parameters.put("title", "test11");
			    Result result = postAndFollowRedirect("/games", parameters);
				assertThat(contentAsString(result)).contains("Game successfully added!");
				assertThat(contentAsString(result)).contains("test11");
				
		
	            result = postAndFollowRedirect("/games/9999/vote");
				assertThat(contentAsString(result)).contains("You can not vote yet!");
				assertThat(contentAsString(result)).contains("test11");
			}
			
		
		});
	}
	
	/**
	 * Test voting and adding in the same day
	 * The vote should work and the add should error
	 */
	@Test
	public void voteAndAddSameDay() {
		running(fakeApplication(), new Runnable() {
			public void run() {
				
				ClockImpl.setInstance(dec12Clock);
				Game game = new Game();
				game.title = "test game";
				game.id = 9999L;
				Game.create(game);


				
				Result result = postAndFollowRedirect("/games/9999/vote");
				assertThat(contentAsString(result)).contains("Vote tallied!");
				
				Map<String, String> parameters = new HashMap<String, String>();
				parameters = new HashMap<String, String>();
	            parameters.put("title", "test22");
				result = postAndFollowRedirect("/games", parameters);
				assertThat(contentAsString(result)).contains("You can not add another game yet!");
			}
			
		
		});
	}
	
	/**
	 * Test the normal case of adding on different days
	 */
	@Test
	public void addTwiceDifferentDay() {
		running(fakeApplication(), new Runnable() {
			public void run() {
				
				ClockImpl.setInstance(dec11Clock);
				

				Map<String, String> parameters = new HashMap<String, String>();
	            parameters.put("title", "test11");
			    Result result = postAndFollowRedirect("/games", parameters);
				assertThat(contentAsString(result)).contains("Game successfully added!");
				assertThat(contentAsString(result)).contains("test11");
				
				ClockImpl.setInstance(dec12Clock);
				
				parameters = new HashMap<String, String>();
	            parameters.put("title", "test22");
				result = postAndFollowRedirect("/games", parameters);
				assertThat(contentAsString(result)).contains("Game successfully added!");
				assertThat(contentAsString(result)).contains("test11");
				assertThat(contentAsString(result)).contains("test22");
			}
			
		
		});
	}
	
	/**
	 * Test that unique name validation works
	 */
	@Test
	public void addSameName() {
		running(fakeApplication(), new Runnable() {
			public void run() {
				
				ClockImpl.setInstance(dec11Clock);
				
				Map<String, String> parameters = new HashMap<String, String>();
	            parameters.put("title", "test11");
			    Result result = postAndFollowRedirect("/games", parameters);
				assertThat(contentAsString(result)).contains("Game successfully added!");
				assertThat(contentAsString(result)).contains("test11");
				
				ClockImpl.setInstance(dec12Clock);
				
				parameters = new HashMap<String, String>();
	            parameters.put("title", "test11");
				result = postAndFollowRedirect("/games", parameters);
				assertThat(contentAsString(result)).contains("That game already exists!");
				assertThat(countOccurences("test11", contentAsString(result))).isSameAs(1);
				
			}
			
		
		});
	}
	
	/** 
	 * User can't vote on the weekends
	 */
	@Test
	public void voteSaturday() {
		running(fakeApplication(), new Runnable() {
			public void run() {
				
				ClockImpl.setInstance(saturdayClock);
				
				Game game = new Game();
				game.title = "test game";
				game.id = 1L;
				Game.create(game);

				Result result = postAndFollowRedirect("/games/1/vote");
				assertThat(contentAsString(result)).contains("You can not vote yet!");
			}
			
			
		});
	}

	/** 
	 * User can't vote on the weekends
	 */
	@Test
	public void voteSunday() {
		running(fakeApplication(), new Runnable() {
			public void run() {
				
				ClockImpl.setInstance(sundayClock);
				
				Game game = new Game();
				game.title = "test game";
				game.id = 1L;
				Game.create(game);


				Result result = postAndFollowRedirect("/games/1/vote");
				assertThat(contentAsString(result)).contains("You can not vote yet!");
			}
			
			
		});
	}
	
	/**
	 * User can't add games on the weekend
	 */
	@Test
	public void addSaturday() {
		running(fakeApplication(), new Runnable() {
			public void run() {
				
				ClockImpl.setInstance(saturdayClock);
				
				Map<String, String> parameters = new HashMap<String, String>();
	            parameters.put("title", "test11");
			    Result result = postAndFollowRedirect("/games", parameters);
				assertThat(contentAsString(result)).contains("You can not add another game yet!");
				assertThat(contentAsString(result)).doesNotContain("test11");
			}
			
			
		});
	}
	
	/**
	 * User can't add games on the weekend
	 */
	@Test
	public void addSunday() {
		running(fakeApplication(), new Runnable() {
			public void run() {
				
				ClockImpl.setInstance(sundayClock);
				
				Map<String, String> parameters = new HashMap<String, String>();
	            parameters.put("title", "test11");
			    Result result = postAndFollowRedirect("/games", parameters);
				assertThat(contentAsString(result)).contains("You can not add another game yet!");
				assertThat(contentAsString(result)).doesNotContain("test11");
			}
			
			
		});
	}
	
	
	/**
	 * Quick and dirty count the number of times a string occurs inside another string 
	 * @param needle string to search for 
	 * @param haystack string to search in
	 * @return number of times subStr occurs in str
	 */
	public static int countOccurences(String needle, String haystack){
		return (haystack.length() - haystack.replace(needle, "").length()) / needle.length();
	}
	
	/**
	 * Post to the location and then follow the very next redirect and return that result.
	 * 
	 * Uses the global cookie string
	 * 
	 * @param location location to post to
	 * @return the result of the 1st redirect after post
	 */
	private Result postAndFollowRedirect(String location) {
		return postAndFollowRedirect(location, null);
	}
	
	
	/**
	 *  Post to the location and then follow the very next redirect and return that result.
	 * 
	 * Uses the global cookie string
	 * 
	 * @param location location location to post to
	 * @param parameters form parameters or null
	 * @return the result of the 1st redirect after post
	 */
	private Result postAndFollowRedirect(String location, Map<String, String> parameters) {
		if(parameters == null) {
			parameters = new HashMap<String, String>();
		}
		Result result;
			
		result = route(fakeRequest(POST, location)
				.withHeader(HeaderNames.COOKIE, sessionCookie).withFormUrlEncodedBody(parameters));
		assertThat(status(result)).isEqualTo(Status.SEE_OTHER);
	    
		sessionCookie = header(HeaderNames.SET_COOKIE, result);
	    result = route(fakeRequest(GET, redirectLocation(result))
	    		  .withHeader(HeaderNames.COOKIE, sessionCookie));

	    assertThat(status(result)).isEqualTo(Status.OK);
	    return result;
	}

}
