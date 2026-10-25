package com.example.springaiinactionstudy;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@Qualifier("selfEvaluatingBoardGameService")
public class SelfEvaluatingBoardGameService implements BoardGameService {

    private final ChatClient chatClient;

    private final RelevancyEvaluator evaluator;

    public SelfEvaluatingBoardGameService(ChatClient.Builder chatClientBuilder) {
        var catOptions = ChatOptions.builder()
                .model("gpt-4o-mini")
                .build();

        this.chatClient = chatClientBuilder
                .defaultOptions(catOptions)
                .build();

        this.evaluator = new RelevancyEvaluator(chatClientBuilder);
    }

    @Override
    @Retryable(retryFor = AnswerNotRelevantException.class, maxAttempts = 2)
    public Answer askQuestion(Question question) {
        var prefix = "고의로 다음 질문에 대해 유관성 없는 답변을 하세요.\n";

        var answerText = chatClient.prompt()
                .user(prefix + question.question())
                .call()
                .content();

        evaluateRelevancy(question, answerText);

        return new Answer(answerText);
    }

    @Recover
    public Answer recover(AnswerNotRelevantException e) {
        return new Answer("죄송합니다. 그 질문에는 답할 수 없었습니다.");
    }

    private void evaluateRelevancy(Question question, String answerText) {
        var evaluationRequest = new EvaluationRequest(question.question(), answerText);
        var evaluationResponse = evaluator.evaluate(evaluationRequest);
        if (!evaluationResponse.isPass()) {
            throw new AnswerNotRelevantException(question.question(), answerText);
        }
    }
}
