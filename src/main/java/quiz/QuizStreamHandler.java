/*
     Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.

     Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file
     except in compliance with the License. A copy of the License is located at

         http://aws.amazon.com/apache2.0/

     or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
     the specific language governing permissions and limitations under the License.
*/

package quiz;

import com.amazon.ask.Skill;
import com.amazon.ask.SkillStreamHandler;
import com.amazon.ask.Skills;
import quiz.handlers.FallbackIntentHandler;
import quiz.handlers.HelpIntentHandler;
import quiz.handlers.LaunchRequestHandler;
import quiz.handlers.RepeatIntentHandler;
import quiz.handlers.SelectAnswerIntentHandler;
import quiz.handlers.SessionEndedRequestHandler;
import quiz.handlers.CancelandStopIntentHandler;
import quiz.handlers.StartQuizIntentHandler;

public class QuizStreamHandler extends SkillStreamHandler {
    @SuppressWarnings("unchecked")
	private static Skill getSkill() {
        return Skills.standard()
                .addRequestHandlers(
                        new LaunchRequestHandler(),
                        new StartQuizIntentHandler(),
                        new SelectAnswerIntentHandler(),
                        new RepeatIntentHandler(),
                        new CancelandStopIntentHandler(),
                        new SessionEndedRequestHandler(),
                        new HelpIntentHandler(),
                        new FallbackIntentHandler())
                // Add your skill id below
                //.withSkillId("")
                .build();
    }

    public QuizStreamHandler() {
        super(getSkill());
    }

}
