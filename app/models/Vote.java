package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;
/**
 * 
 * This is the model to hold the votes for a game
 *
 */
@Entity
public class Vote extends Model {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	public Long id;
	
	public Date created = new Date();
	
}
