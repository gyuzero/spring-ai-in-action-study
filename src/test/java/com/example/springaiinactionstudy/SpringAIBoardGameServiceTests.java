package com.example.springaiinactionstudy;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.FactCheckingEvaluator;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SpringAIBoardGameServiceTests {

    @Autowired
    private BoardGameService boardGameService;

    @Autowired
    private ChatClient.Builder chatClientBuilder;

    private RelevancyEvaluator relevancyEvaluator;

    private FactCheckingEvaluator factCheckingEvaluator;

    @BeforeEach
    public void setup() {
        this.relevancyEvaluator = new RelevancyEvaluator(chatClientBuilder);
        this.factCheckingEvaluator = new FactCheckingEvaluator(chatClientBuilder);
    }

    @Test
    public void evaluateRelevancy() {
        String userText = "하늘은 왜 파란가요?";
        Question question = new Question(userText);
        Answer answer = boardGameService.askQuestion(question); // 질문을 제출

        EvaluationRequest evaluationRequest = new EvaluationRequest(userText, answer.answer());

        EvaluationResponse response = relevancyEvaluator.evaluate(evaluationRequest); // 응답을 평가

        Assertions.assertThat(response.isPass()) // 유관성을 확인
                .withFailMessage("""
                        ==================================
                        답변 "%s"는
                        질문 "%s"와 유관하지 않은 것으로 간주됨.
                        ==================================
                        """, answer.answer(), userText)
                .isTrue();
    }

    @Test
    public void evaluateFactualAccuracy() {
        var userText = "하늘은 왜 파란가요?";
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
