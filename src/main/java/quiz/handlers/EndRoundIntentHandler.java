package quiz.handlers;

import static com.amazon.ask.request.Predicates.intentName;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;

import quiz.model.Player;
import quiz.model.QuizGame;
import quiz.model.QuizRound;

public class EndRoundIntentHandler implements RequestHandler {
	@Override
	public boolean canHandle(HandlerInput input) {
		if (input.matches(intentName("AMAZON.YesIntent").or(intentName("AMAZON.NoIntent")))) {
			Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes();
			QuizGame game = QuizGame.fromSessionAttributes(sessionAttributes);
			
			return game.round != null && game.round.askedQuestions.length == 0;
		}
		return false;
	}

	@Override
	public Optional<Response> handle(HandlerInput input) {
		StringBuilder speechText = new StringBuilder();
		Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes();
		QuizGame game = QuizGame.fromSessionAttributes(sessionAttributes);
		QuizRound round = game.round; // asserted to exist in canHandle

		if (input.matches(intentName("AMAZON.NoIntent"))) {
			speechText.append(" Ok. Dann sag ich " + (round.players.length == 1 ? "dir noch dein" : "euch noch euer") + " Level. ");
			for (Player player: round.players) {
				int averagePoints = (int) Math.round(player.getScore() / (double) game.roundCount);
				if (averagePoints == 0) {
					speechText.append("Schade "+ player.name +", du hast leider keine Frage richtig beantwortet. "
							+ "Versuch es doch gleich noch einmal. "
							+ "Beim nächsten mal klappt's bestimmt besser. "
							+ "Hier dein Level: "
							+ "<audio src='soundbank://soundlibrary/cartoon/amzn_sfx_boing_long_1x_01'/>"
							+ "Du bist ein Tourist");
				} else if (averagePoints == 1) {
					speechText.append("Schade "+ player.name +". "
							+ "Versuch es doch gleich noch einmal. "
							+ "Beim nächsten mal klappt's bestimmt besser. "
							+ "Hier dein Level: "
							+ "<audio src='soundbank://soundlibrary/cartoon/amzn_sfx_boing_long_1x_01'/>"
							+ "Du bist ein Tourist");
				} else if (averagePoints == 2 || averagePoints == 3) {
					speechText.append(player.name +", Du musst noch ein bisschen üben. Hier dein Level: "
							+ "<audio src='soundbank://soundlibrary/musical/amzn_sfx_trumpet_bugle_01'/>"
							+ "Du bist ein Zugezogener");
				} else if (averagePoints == 4) {
					speechText.append("Super "+player.name +". Das ist schon richtig gut. Hier dein Level: "
							+ "<audio src='soundbank://soundlibrary/musical/amzn_sfx_trumpet_bugle_03'/>"
							+ "Du bist ein Stadtführer");
				} else if(averagePoints == 5) {
					speechText.append("Sehr gut"+ player.name +", du hast alle Fragen richtig beantwortet! Du weißt ja wirklich alles."
							+ " Hier ist dein Level:"
							+ "<audio src='soundbank://soundlibrary/human/amzn_sfx_large_crowd_cheer_01'/>"
							+ " Du bist ein Einheimischer");
				}
			}
			return input.getResponseBuilder().withSpeech(speechText.toString()).withShouldEndSession(true).build();
		} else {
			String[] yeah = new String[] {"Großartig! ", "Spitze! ", "Toll! "};
			int num = new Random().nextInt(yeah.length);
			speechText.append(yeah[num]);
			// TODO: Möchtest du weitere Fragen zu [REGION] spielen oder dir eine neue Region aussuchen?
			round.askNewQuestion(speechText);
			game.intoSessionAttributes(sessionAttributes);
			return input.getResponseBuilder().withSpeech(speechText.toString()).withReprompt(speechText.toString()).build();
		}
	}
}
