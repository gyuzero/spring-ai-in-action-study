package com.example.springaiinactionstudy;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.FactCheckingEvaluator;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SelfEvaluatingBoardGameServiceTests {

    @Autowired
    @Qualifier("selfEvaluatingBoardGameService")
    private BoardGameService boardGameService;

    @Autowired
    private ChatClient.Builder chatClientBuilder;

    private FactCheckingEvaluator factCheckingEvaluator;

    @BeforeEach
    public void setup() {
        this.factCheckingEvaluator = new FactCheckingEvaluator(chatClientBuilder);
    }

    @Test
    public void evaluateFactualAccuracy() {
        var userText = "1 + 1 = ?";
        var question = new Question(userText);
        var answer = boardGameService.askQuestion(question);

        var evaluationRequest = new EvaluationRequest(userText, answer.answer());

        var response = factCheckingEvaluator.evaluate(evaluationRequest);

        Assertions.assertThat(response.isPass())
                .withFailMessage("""
                        ==================================
                        답변 "%s"는
                        질문 "%s"에 대한
                        정확한 답이 아닌 것으로 간주됨.
                        ==================================
                        """, answer.answer(), userText)
                .isTrue();
    }
}
