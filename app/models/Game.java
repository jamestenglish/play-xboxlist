package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
public class Game extends Model {
	
	private static final long serialVersionUID = 1L;

	@Id
	public Long id;
	
	@Required
	/** Name of the game */
	public String title;
	
	/** Is the game owned */
	public Boolean owned = false;
	
	public Date created = new Date();
	
	/** List of votes for the game */
	@OneToMany(cascade=CascadeType.ALL)
	private List<Vote> votes = new ArrayList<Vote>();
	
	public static Finder<Long, Game> find = new Finder<Long, Game>(Long.class, Game.class);
	
	/**
	 * Create a game in the DB
	 * @param game to create
	 */
	public static void create(Game game) {
		game.save();
	}
	
	/**
	 * @return all games in the DB
	 */
	public static List<Game> all() {
		return find.all();
	}
	
	/**
	 * @return All owned games
	 */
	public static List<Game> owned() {
		return find.where()
				.eq("owned", true)
				.orderBy("title asc")
				.findList();
	}
	
	/**
	 * @return All candidate games to be voted on
	 */
	public static List<Game> candidates() {
		
		//You could do this in SQL
		//but it would require RAW sql with ebeans
		//this is more readable/updatable
		List<Game> gamesList = find.fetch("votes")
				.where()
				.eq("owned", false)
				.findList();
		
		//Sort the games by vote count
		Collections.sort(gamesList, new Comparator<Game>() {

			@Override
			public int compare(Game o1, Game o2) {
				return o2.votes.size() - o1.votes.size();
			}
			
		});
		
		return gamesList;
	}
	
	/**
	 * 
	 * @return votes for this game
	 */
	public List<Vote> getVotes() {
		return votes;
	}
	
	/**
	 * 
	 * @param votes to be set on this game
	 */
	public void setVotes(List<Vote> votes) {
		this.votes = votes;
	}


}
