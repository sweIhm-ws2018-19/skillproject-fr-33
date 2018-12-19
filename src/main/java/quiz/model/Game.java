package quiz.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import quiz.QuestionLoader;
import quiz.model.QuizRound;

public class Game implements Serializable{
	//StringBuilder? 
	private int countround = 0;
	public int[] playersAveragePoints;				//points / round = average points for level -> you reached [playersAveragePoints] of [countRound*5] points
	public Region region;
	public Player[] players;
	
static ObjectMapper mapper = new ObjectMapper();

	public Game() {}
	public Game(Region r, Player[] ps) {
		this.region = r;
		this.players = ps;
	}
	
	public static Game fromSessionAttributes(Map<String, Object> sessionAttributes) {
		String game = (String) sessionAttributes.get("game");
		if (game != null)
			try {
				return mapper.readValue(game, Game.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
		return new Game(null, null);
	}
	public void intoSessionAttributes(Map<String, Object> sessionAttributes) {
		try {
			sessionAttributes.put("game", mapper.writeValueAsString(this));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
	
	@JsonIgnore
	public boolean isComplete() {
		return region != null && players != null && players.length > 0;
	}

	public void createPlayers(int count, StringBuilder speechText) {
		if(count <= 0) {
			speechText.append("Netter Versuch. ");
			return;
		}
		if (count >= 6) {
			speechText.append("Es können höchstens fünf Spieler teilnehmen. ");
			return;
		}
		// valid number, we now create the players array:
		players = new Player[count];
		if (count == 1) {
			players[0] = new Player("", 0);
			speechText.append("Alles klar. Dann spielen nur wir beide! ");
			return;
		}
		if (count == 2) { 
			players[0] = new Player("Heidi", 0);
			players[1] = new Player("Peter", 0);
			speechText.append("Spitze. Zu zweit macht's immer mehr Spaß."
					+ " Ich nenn euch jetzt einfach mal "+players[0].name+" und "+players[1].name+". ");
			return;
		} else if (count == 3) {
			players[0] = new Player("Justus Jonas", 0);
			players[1] = new Player("Peter Shaw", 0);
			players[2] = new Player("Bob Andrews", 0);
			speechText.append("Yeih. Ihr seid die drei Fragezeichen. ");
		} else if (count == 4) {
			players[0] = new Player("Mickey", 0);
			players[1] = new Player("Minney", 0);
			players[2] = new Player("Donald", 0);
			players[3] = new Player("Daisy", 0);
			speechText.append("Cool! Vier gewinnt! ");
		} else {
			players[0] = new Player("Harry Potter", 0);
			players[1] = new Player("Hermine", 0);
			players[2] = new Player("Ron", 0);
			players[3] = new Player("Hedwig", 0);
			players[4] = new Player("Sprechender Hut", 0);
			speechText.append("Alles klar. ");
		}
		for (int i=0; i<players.length; i++)
			speechText.append("Spieler "+(i+1)+", du bist "+players[i].name+". ");
	}
	public void selectRegion(String region, StringBuilder speechText) {
		if (region.equals("Berlin") || region.equals("Ostsee") || region.equals("Dresden")) {
			this.region = new Region(region, null);
			new QuestionLoader(this.region).load();
		} else {
			speechText.append("In "+region+" kenne ich mich leider nicht aus. ");
		}
	}
	
	public void CountRound(){
		if(isComplete()) { countround += 1; }
	}
	
	public void AveragePoints(){
		for(int i=0;i<players.length;i++) 
		{ 
			if(players[i].getScore()%2 == 0) { playersAveragePoints[i] = players[i].getScore()/countround; }
			if(players[i].getScore()%2 < 5) {
				{ playersAveragePoints[i] = (players[i].getScore()-1)/countround; }
			} else if(players[i].getScore()%2 >= 5){ playersAveragePoints[i] = (players[i].getScore()+1)/countround; }
		}
	}
}
