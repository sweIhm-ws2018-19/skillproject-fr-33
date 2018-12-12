package quiz;

import static org.junit.Assert.*;

import org.junit.Test;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;

import io.klerch.alexa.test.client.AlexaClient;
import io.klerch.alexa.test.client.endpoint.AlexaEndpoint;
import io.klerch.alexa.test.client.endpoint.AlexaLambdaEndpoint;
import io.klerch.alexa.test.client.endpoint.AlexaSimulationApiEndpoint;
import quiz.handlers.StartQuizIntentHandler;

public class StartQuizIntentHandlerTest {

	@Test
	public void test() {
	// alexa-skill-kit tester (siehe kay lerch)
	// 
	// noch nicht implementiert
	//	StartQuizIntentHandler handler = new StartQuizIntentHandler();
	//	handler.handle(HandlerInput.builder().build());
	//	fail("Not yet implemented");
		
		
		final AlexaEndpoint lambdaEndpoint = AlexaLambdaEndpoint.create("amzn1.ask.skill.774d3264-2fb4-40e0-bd19-a7da6f847862")
				.build();
		final AlexaClient client = AlexaClient.create(lambdaEndpoint).build();
		
		client.startSession().say("help").done();
	}

}
