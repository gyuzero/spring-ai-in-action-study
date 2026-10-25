package com.example.springaiinactionstudy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class SpringAiInActionStudyApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiInActionStudyApplication.class, args);
    }

}
