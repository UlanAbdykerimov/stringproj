package com.amigoscode;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Entity
@Getter
@Setter
public class Game {
    @Id
    @SequenceGenerator(
            name = "game_number_sequence",
            sequenceName = "game_number_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "game_number_sequence"
    )
    private Integer game_number;
    private Integer player1_id;
    private Integer player2_id;
    private Integer player3_id;
    private Integer player1_points;
    private Integer player2_points;
    private Integer player3_points;
    private Integer question_id;
    private String answer;
    private String guessedLetters;
    private boolean isFinished = false;
    private boolean player1ShouldGuess;
    private boolean player2ShouldGuess;
    private boolean player3ShouldGuess;
}
