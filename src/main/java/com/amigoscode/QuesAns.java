package com.amigoscode;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class QuesAns {
    @Id
    private Integer id;
    private String question;
    private String answer;
    private String additional_info;

    public String getAdditional_info() {
        return additional_info;
    }

    public void setAdditional_info(String additional_info) {
        this.additional_info = additional_info;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public QuesAns(
            String question,
            Integer id,
            String answer,
            String additional_info) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.additional_info = additional_info;
    }
    public QuesAns() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuesAns quesAns = (QuesAns) o;
        return Objects.equals(id, quesAns.id) && Objects.equals(question, quesAns.question) && Objects.equals(answer, quesAns.answer) && Objects.equals(additional_info, quesAns.additional_info);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, question, answer, additional_info);
    }
}
