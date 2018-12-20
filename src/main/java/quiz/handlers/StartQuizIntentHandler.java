package quiz.handlers;

import static com.amazon.ask.request.Predicates.intentName;

import java.awt.List;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;
import com.amazon.ask.model.slu.entityresolution.StatusCode;

import quiz.model.QuizRound;

public class StartQuizIntentHandler implements RequestHandler {
	@Override
	public boolean canHandle(HandlerInput input) {
		return input.matches(intentName("StartQuizIntent"));
	}

	@Override
	public Optional<Response> handle(HandlerInput input) {
		IntentRequest intentRequest = (IntentRequest) input.getRequestEnvelope().getRequest();
		Intent intent = intentRequest.getIntent();
		Map<String, Slot> slots = intent.getSlots();
		
		// Get Device-Id and API-Access Token
		String deviceId = input.getRequestEnvelope().getContext().getSystem().getDevice().getDeviceId();
		String accessToken = input.getRequestEnvelope().getContext().getSystem().getApiAccessToken();
		String response = "NOTHING"+deviceId+"   XXXXXXXXXXXXX   "+accessToken;
		try { 
			
			HttpClient builder = HttpClientBuilder.create().build();
			HttpGet get = new HttpGet(new URI("https://api.amazonalexa.com/v1/devices/"+deviceId+"/settings/address/countryAndPostalCode"));
				    get.addHeader("Accept", "application/json");
				    get.addHeader("Authorization","Bearer "+accessToken);
				    
				    HttpResponse resp = builder.execute(get);
				    String stat = resp.getStatusLine().toString();
			/*
			URL url = new URL("https://api.amazonalexa.com/v1/devices/"+deviceId+"/settings/address/countryAndPostalCode&Accept=application/json&Authorization="+
								accessToken);
			HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
			connection.setDoOutput(true);
			connection.connect(); */
		//	connection.setDoInput(true);
			/*
			connection.setRequestMethod("GET");
			connection.addRequestProperty("Accept", "application/json");
			connection.addRequestProperty("Authorization", accessToken);
			connection.addRequestProperty("Host", "api.amazonalexa.com");
			*/
			//int status = connection.getResponseCode();
			
			response = stat;
		/*	
			BufferedReader in = new BufferedReader(
					  new InputStreamReader(connection.getInputStream()));
					String inputLine;
					StringBuffer content = new StringBuffer();
					while ((inputLine = in.readLine()) != null) {
					    content.append(inputLine);
					}
					
					response = content.toString();
					in.close(); */
		//	connection.disconnect();
		
		}catch(IOException e) {} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		StringBuilder speechText = new StringBuilder();
		Map<String, Object> sessionAttributes = input.getAttributesManager().getSessionAttributes();
		QuizRound round = QuizRound.fromSessionAttributes(sessionAttributes);

		
		sessionAttributes.put("response", response);
		
		// Get the player count slot from the list of slots.
		Slot playerCountSlot = slots.get("Anzahl");
		if (playerCountSlot != null
			&& playerCountSlot.getValue() != null // WTF
			&& !playerCountSlot.getValue().equals("?") // WTFFFFF????
		) {
			try {
				int playerCount = Integer.parseInt(playerCountSlot.getValue());
				round.createPlayers(playerCount, speechText);
			} catch (NumberFormatException e) {
				speechText.append("Das ist leider keine gültige Spielerzahl. ");
			}
		} else if (round.players == null) { // playerCountSlot == null
			speechText.append("Mit wie vielen Spielern möchtest du spielen? ");
		}

		Slot regionSlot = slots.get("Region");
		if (regionSlot != null
			&& regionSlot.getResolutions() != null
			&& regionSlot.getResolutions().getResolutionsPerAuthority().size() > 0
			// answerSlot.getResolutions().getResolutionsPerAuthority().get(0).getAuthority().equals("amzn1.er-authority.echo-sdk.<skill_id>.KnownRegion")
			&& regionSlot.getResolutions().getResolutionsPerAuthority().get(0).getStatus().getCode() == StatusCode.ER_SUCCESS_MATCH
			&& regionSlot.getResolutions().getResolutionsPerAuthority().get(0).getValues().size() > 0
		) {
			String region = regionSlot.getResolutions().getResolutionsPerAuthority().get(0).getValues().get(0).getValue().getId();
			round.selectRegion(region, speechText);
		} else if (round.players != null && round.region == null) {
			speechText.append("Über welche Region möchtest du spielen? ");
		}

		if (round.isComplete())
			round.askNewQuestion(speechText); // TODO: only if completed with the current utterance
		round.intoSessionAttributes(sessionAttributes);

		java.util.List<String> list = new ArrayList<String>();
		list.add("read::alexa:device:all:address");
		return input.getResponseBuilder().withSpeech(speechText.toString()).withAskForPermissionsConsentCard(list).withReprompt(speechText.toString()).build();
	}
}
