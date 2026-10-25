package com.example.springaiinactionstudy;

public class AnswerNotRelevantException extends RuntimeException {
    public AnswerNotRelevantException(String question, String answer) {
        super("답변 '" + answer + "'는 질문 '" + question + "'과 유관하지 않은 것으로 간주됨.");
    }
}
