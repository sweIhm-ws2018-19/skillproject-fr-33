package quiz.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import com.amazon.ask.model.Response;
import com.amazon.ask.response.ResponseBuilder;
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

	public static enum GameState {
		INQUIRE_REGION,
		INQUIRE_PLAYER_COUNT,
		QUIZ_QUESTION,
		PROMPT_CONTINUE_GAME
	}
	public String regionId;
	public int playerCount;
	public int roundCount;
	public GameState state;
	
	public QuizRound round;
	
	public QuizGame() {
		state = GameState.INQUIRE_REGION;
	}

	public void checkComplete(StringBuilder speechText) {
		if (regionId == null) {
			state = GameState.INQUIRE_REGION;
			return;
		}
		if (playerCount == 0) {
			state = GameState.INQUIRE_PLAYER_COUNT;
			return;
		}
		Region region = new Region(regionId, null);
		QuestionLoader loader = new QuestionLoader(region);
		loader.load(); // regionAvailable() had been checked before
		round = new QuizRound(region, createPlayers(speechText));
		round.toNextQuestion(speechText);
		state = GameState.QUIZ_QUESTION;
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
			players[0] = new Player("", 0, 0);
			speechText.append("Alles klar. Dann spielen nur wir beide! ");
			return players;
		}
		if (playerCount == 2) {
			players[0] = new Player("Heidi");
			players[1] = new Player("Peter");
			speechText.append("Spitze. Zu zweit macht's immer mehr Spaß."
					+ " Ich nenn euch jetzt einfach mal "+players[0].name+" und "+players[1].name+". ");
			return players;
		} else if (playerCount == 3) {
			players[0] = new Player("Justus Jonas");
			players[1] = new Player("Peter Shaw");
			players[2] = new Player("Bob Andrews");
			speechText.append("Yeih. Ihr seid die drei Fragezeichen. ");
		} else if (playerCount == 4) {
			players[0] = new Player("Mickey");
			players[1] = new Player("Minney");
			players[2] = new Player("Donald");
			players[3] = new Player("Daisy");
			speechText.append("Cool! Vier gewinnt! ");
		} else {
			players[0] = new Player("Harry Potter");
			players[1] = new Player("Hermine");
			players[2] = new Player("Ron");
			players[3] = new Player("Hedwig");
			players[4] = new Player("Sprechender Hut");
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
	public void selectAnswer(int answerIndex, StringBuilder speechText) {
		if (round == null) {
			speechText.append("Wir spielen doch noch gar nicht. ");
		} else {
			round.selectAnswer(answerIndex, speechText);
			if (round.askedQuestions.length < round.players.length * QuizRound.LENGTH) {
				round.toNextQuestion(speechText);
				state = GameState.QUIZ_QUESTION;
			} else {
				speechText.append("Die Runde ist zu Ende. Das war die letzte Frage in dieser Runde. ");
				round.askedQuestions = new Question[0]; // round = new QuizRound(round.region, round.players)
				roundCount += 1;
				for (Player player: round.players) {
					int roundScore = player.endRound();
					speechText.append(player.name + ", du hast " + roundScore + " Punkte erreicht. ");
				}
				state = GameState.PROMPT_CONTINUE_GAME;
			}
		}
	}
	public void end(StringBuilder speechText) {
		speechText.append(" Ok. Dann sag ich " + (round.players.length == 1 ? "dir noch dein" : "euch noch euer") + " Level. ");
		for (Player player: round.players) {
			int averagePoints = (int) Math.round(player.getTotalScore() / (double) roundCount);
			if (averagePoints == 0) {
				speechText.append("Schade "+ player.name +", du hast leider keine Frage richtig beantwortet. "
						+ "Versuch es doch gleich noch einmal. "
						+ "Beim nächsten mal klappt's bestimmt besser. "
						+ "Hier dein Level: "
						+ "<audio src='soundbank://soundlibrary/cartoon/amzn_sfx_boing_long_1x_01'/>"
						+ "Du bist ein Tourist. ");
			} else if (averagePoints == 1) {
				speechText.append("Schade "+ player.name +". "
						+ "Versuch es doch gleich noch einmal. "
						+ "Beim nächsten mal klappt's bestimmt besser. "
						+ "Hier dein Level: "
						+ "<audio src='soundbank://soundlibrary/cartoon/amzn_sfx_boing_long_1x_01'/>"
						+ "Du bist ein Tourist. ");
			} else if (averagePoints == 2 || averagePoints == 3) {
				speechText.append(player.name +", Du musst noch ein bisschen üben. Hier dein Level: "
						+ "<audio src='soundbank://soundlibrary/musical/amzn_sfx_trumpet_bugle_01'/>"
						+ "Du bist ein Zugezogener. ");
			} else if (averagePoints == 4) {
				speechText.append("Super "+player.name +". Das ist schon richtig gut. Hier dein Level: "
						+ "<audio src='soundbank://soundlibrary/musical/amzn_sfx_trumpet_bugle_03'/>"
						+ "Du bist ein Stadtführer. ");
			} else if(averagePoints == 5) {
				speechText.append("Sehr gut"+ player.name +", du hast alle Fragen richtig beantwortet! Du weißt ja wirklich alles."
						+ " Hier ist dein Level:"
						+ "<audio src='soundbank://soundlibrary/human/amzn_sfx_large_crowd_cheer_01'/>"
						+ " Du bist ein Einheimischer. ");
			}
		}
	}
	public void cont(StringBuilder speechText) {
		String[] yeah = new String[] {"Großartig! ", "Spitze! ", "Toll! "};
		int num = new Random().nextInt(yeah.length);
		speechText.append(yeah[num]);
		// TODO: Möchtest du weitere Fragen zu [REGION] spielen oder dir eine neue Region aussuchen?
		round.toNextQuestion(speechText);
		state = GameState.QUIZ_QUESTION;
	}
	public Optional<Response> respond(ResponseBuilder builder, StringBuilder speechText) {
		String question = "";
		switch (state) {
			case INQUIRE_PLAYER_COUNT:
				question = "Wieviele Spieler wollen mitspielen?";
				// only works when responding to StartRoundIntent
				// return builder.withSpeech(speechText.toString()).addElicitSlotDirective("Anzahl", null).build();)
				break;
			case INQUIRE_REGION:
				question = "Über welche Region möchtest du spielen?"; // reprompt = "Nenne mir deine Wunschregion."
				// only works when responding to StartRoundIntent
				// return builder.withSpeech(speechText.toString()).addElicitSlotDirective("Region", null).build();
				break;
			case QUIZ_QUESTION:
				// TODO: assert that QUIZ_QUESTION state is only entered with an askedQuestion
				question = round.askedQuestions[round.askedQuestions.length - 1].ask();
				break;
			case PROMPT_CONTINUE_GAME:
				question = "Willst du weiterspielen?"; // TODO: oder das Quiz beenden
				break;
			default:
				return builder.withSpeech(speechText.toString()).withShouldEndSession(false).build();
		}
		return builder.withSpeech(speechText.append(question).toString()).withReprompt(question).build();
	}
}
