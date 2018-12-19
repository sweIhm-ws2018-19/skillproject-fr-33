package quiz.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import quiz.QuestionLoader;

public class QuizGame implements Serializable {

	static ObjectMapper mapper = new ObjectMapper();
	
	public static QuizGame fromSessionAttributes(Map<String, Object> sessionAttributes) {
		String round = (String) sessionAttributes.get("game");
		if (round != null)
			try {
				return mapper.readValue(round, QuizGame.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
		return new QuizGame();
	}
	public void intoSessionAttributes(Map<String, Object> sessionAttributes) {
		try {
			sessionAttributes.put("game", mapper.writeValueAsString(this));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	public String regionId;
	public int playerCount;
	public int roundCount;
	
	public QuizRound round;
	
	public QuizGame() {}

	@JsonIgnore
	public boolean isComplete() {
		return regionId != null && playerCount != 0;
	}

	public void selectPlayerCount(int count, StringBuilder speechText) {
		if(count <= 0) {
			speechText.append("Netter Versuch. ");
			return;
		}
		if (count >= 6) {
			speechText.append("Es können höchstens fünf Spieler teilnehmen. ");
			return;
		}
		// valid number: 
		playerCount = count;
	}
	public Player[] createPlayers(StringBuilder speechText) {
		Player[] players = new Player[playerCount];
		if (playerCount == 1) {
			players[0] = new Player("", 0);
			speechText.append("Alles klar. Dann spielen nur wir beide! ");
			return players;
		}
		if (playerCount == 2) {
			players[0] = new Player("Heidi", 0);
			players[1] = new Player("Peter", 0);
			speechText.append("Spitze. Zu zweit macht's immer mehr Spaß."
					+ " Ich nenn euch jetzt einfach mal "+players[0].name+" und "+players[1].name+". ");
			return players;
		} else if (playerCount == 3) {
			players[0] = new Player("Justus Jonas", 0);
			players[1] = new Player("Peter Shaw", 0);
			players[2] = new Player("Bob Andrews", 0);
			speechText.append("Yeih. Ihr seid die drei Fragezeichen. ");
		} else if (playerCount == 4) {
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
		return players;
	}
	public void selectRegion(String region, StringBuilder speechText) {
		if (QuestionLoader.isRegionAvailable(region)) {
			this.regionId = region;
		} else {
			this.regionId = null;
			speechText.append("In "+region+" kenne ich mich leider nicht aus. ");
		}
	}
}
