package controllers;

import static play.libs.Json.toJson;

import java.util.List;

import libs.SessionAuthenticator;
import models.Game;
import models.Vote;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
/**
 * This is the controller for the application
 *
 */
public class Application extends Controller {

	/** the key for the message stored in the flash session */
	private static final String FLASH_MESSAGE = "flashMessage";
	
	/** the key for the error stored in the flash session */
	private static final String FLASH_ERROR = "flashError";
	
	/** standard form to use for game input */
	static Form<Game> gameForm = Form.form(Game.class);
	
	/** Display index */
    public static Result index() {
        return ok(views.html.index.render(Game.all(), gameForm));
    }
    
    /** Display all games */
    public static Result games() {
    	
    	List<Game> games = Game.find.all();
    	return ok(toJson(games));
    }
    
    /** Creates a new game */
    public static Result newGame() {
    	
    	if(!SessionAuthenticator.isAuthorized()) {
    		flash(FLASH_ERROR, "You can not add another game yet!");
    		return redirect(routes.Application.index());
    	}
    	
    	Form<Game> filledForm = gameForm.bindFromRequest();
    	
    	if(filledForm.hasErrors()) {
    		return badRequest(views.html.index.render(Game.all(), filledForm));
    	} else {
    		Game game = filledForm.get();
    		
    		if(isUnique(game)) {
    			Game.create(filledForm.get());
    			flash(FLASH_MESSAGE, "Game successfully added!");
    			SessionAuthenticator.actionPerformed();
    		}
    		return redirect(routes.Application.index());
    	}
    }
    
    /**
     *  Determines if a game's title is unique
     * @param game entity to determine uniqueness
     * @return true if the game's title is unique
     */
    private static boolean isUnique(Game game) {
    	//Play doesn't seem to have a built-in unique validator
		//If this wasn't demo code I'd invest some time in creating one
		//This will do the trick for now
		List<Game> existingGames = Game.find.where().eq("title", game.title).findList();
		
		if(existingGames != null && existingGames.size() > 0) {
			flash(FLASH_ERROR, "That game already exists!");
			return false;
		}
		return true;
	}

    /** Displays list of games owned */
	public static Result gamesOwned() {
    	List<Game> games = Game.owned();
    	return ok(toJson(games));
    }
    
	/** Displays list of games to vote on */
    public static Result gamesCandidates() {
    	List<Game> games = Game.candidates();
    	return ok(toJson(games));
    }
    
    /** Handles game voting */
    public static Result voteGame(Long id) {
    	if(!SessionAuthenticator.isAuthorized()) {
    		flash(FLASH_ERROR, "You can not vote yet!");
    		
    		return redirect(routes.Application.index());
    	}
    	Game game = Game.find.ref(id);
    	//If they are submitting bad game ids
    	//Don't do anything, there is no reason to display an error
    	if(game != null) {
    		Vote vote = new Vote();
    		game.getVotes().add(vote);
    		game.save();
    	}
    	flash(FLASH_MESSAGE, "Vote tallied!");
    	SessionAuthenticator.actionPerformed();
    	return redirect(routes.Application.index());
    }
}
