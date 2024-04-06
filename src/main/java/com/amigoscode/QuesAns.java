package com.amigoscode;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Getter
@Setter
public class QuesAns {
    @Id
    private Integer id;
    private String question;
    private String answer;
    private String additional_info;
}