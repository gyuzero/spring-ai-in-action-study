package com.example.springaiinactionstudy;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("springAIBoardGameService")
public class SpringAIBoardGameService implements BoardGameService {

    private final ChatClient chatClient;

    public SpringAIBoardGameService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public Answer askQuestion(Question question) {
        var answerText = chatClient.prompt()
                .user(question.question())
                .call()
                .content();

        return new Answer(answerText);
    }
}
