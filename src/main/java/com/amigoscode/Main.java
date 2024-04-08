package com.amigoscode;

import com.amigoscode.Repositories.GameRepository;
import com.amigoscode.Repositories.PlayerRepository;
import com.amigoscode.Repositories.QuesAnsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;
import org.springframework.http.ResponseEntity;

@SpringBootApplication
@RestController
@RequestMapping("api/v1/players")
public class Main {

    private final PlayerRepository playerRepository;
    private final QuesAnsRepository quesAnsRepository;
    private final GameRepository gameRepository;
    @Autowired
    public Main(PlayerRepository playerRepository, QuesAnsRepository quesAnsRepository, GameRepository gameRepository) {
        this.playerRepository = playerRepository;
        this.quesAnsRepository = quesAnsRepository;
        this.gameRepository = gameRepository;
    }
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
    @GetMapping("/questions")
    public List<QuesAns> getAllQuestions() {
        return quesAnsRepository.findAll();
    }
    @PostMapping("/newGame")
    public ResponseEntity<String> addPlayers(@RequestBody addPlayersRequest addRequest) {
        Optional<QuesAns> randomQuestion = quesAnsRepository.findById(random_id());
        String question = randomQuestion.isPresent() ? randomQuestion.get().getQuestion() : "Unknown";
        Integer question_id = randomQuestion.isPresent() ? randomQuestion.get().getId() : 2;
        String answer = randomQuestion.isPresent() ? randomQuestion.get().getAnswer() : "Unknown";
        int answerLength= answer.length();
        Game newGame = new Game();
        newGame.setPlayer1ShouldGuess(true);
        newGame.setPlayer1_id(addRequest.player1_id());
        newGame.setPlayer2_id(addRequest.player2_id());
        newGame.setPlayer3_id(addRequest.player3_id());
        newGame.setQuestion_id(question_id);
        newGame.setAnswer(answer);
        char[] gl = new char[answer.length()];
        for (int i=0; i<answer.length(); i++) {
            gl[i] = '*';
        }
        String gls = new String(gl);
        newGame.setGuessedLetters(gls);
        Game savedGame = gameRepository.save(newGame);
        Optional<Player> player1 = playerRepository.findById(savedGame.getPlayer1_id());
        Optional<Player> player2 = playerRepository.findById(savedGame.getPlayer2_id());
        Optional<Player> player3 = playerRepository.findById(savedGame.getPlayer3_id());
        Integer gameNumber = savedGame.getGame_number();
        String player1Name = player1.isPresent() ? player1.get().getName() : "Unknown";
        String player2Name = player2.isPresent() ? player2.get().getName() : "Unknown";
        String player3Name = player3.isPresent() ? player3.get().getName() : "Unknown";
        String message = "New game is started. Game number - " + gameNumber + "\n"
                 + "Players: " + player1Name + ", " + player2Name + ", " + player3Name + "\n"
                 + "Question: " + question + "\n"
                 + "The length of answer: " + answerLength + "\n"
                 + player1Name + " it is Your turn to guess" + "\n";
        return ResponseEntity.ok().body(message);
    }
    @PostMapping("/{gameId}/{playerId}/guessLetter")
    public ResponseEntity<String> guessLetter(@PathVariable("gameId") Integer gameId,
                                              @PathVariable("playerId") Integer playerId,
                                              @RequestBody GuessLetterRequest request) {
        Optional<Game> game = gameRepository.findById(gameId);
        Player optionalPlayer = playerRepository.findById(playerId)
                .orElseThrow(() -> new NoSuchElementException("Player not found"));
        Game optionalGame = gameRepository.findById(gameId)
                .orElseThrow(() -> new NoSuchElementException("Game not found"));
        Optional<Player> player2 = playerRepository.findById(optionalGame.getPlayer2_id());
        Optional<Player> player3 = playerRepository.findById(optionalGame.getPlayer3_id());
        QuesAns optionalQuestion = quesAnsRepository.findById(optionalGame.getQuestion_id())
                .orElseThrow(() -> new NoSuchElementException("Question not found"));
        int player1Id = optionalGame.getPlayer1_id();
        int player2Id = optionalGame.getPlayer2_id();
        int player3Id = optionalGame.getPlayer3_id();
        char enteredLetter = request.letter();
        boolean isFinished = optionalGame.isFinished();
        String message = "";
        String answer = optionalGame.getAnswer();
        char[] letters = answer.toCharArray();
        String guessedLetters = optionalGame.getGuessedLetters();
        char[] isLettersGuessed = guessedLetters.toCharArray();
        int guessedLetCount = 0;
        int playersTurn = 0;
        int random = randomPoints();
        if (playerId == player1Id) {
            playersTurn = 1;
        } else if (playerId == player2Id) {
            playersTurn = 2;
        } else if (playerId == player3Id) {
            playersTurn = 3;
        } else {
            playersTurn = 1;
        }
        if (game.isEmpty()) {
            message = "There is no game under this Id";
        }
        else if (isFinished) message = "This game has been finished";
        else if (playerId==403) message = "Player not found"  + "\n"
                + "Question: " + optionalQuestion.getQuestion() + "\n"
                + "Answer: " + guessedLetters;
        else if (playerId != player1Id && playerId != player2Id && playerId != player3Id) message = "User under id #" + playerId + " is not in the list of players"  + "\n"
                + "Question: " + optionalQuestion.getQuestion() + "\n"
                + "Answer: " + guessedLetters;
        else if (playersTurn==1) {
            if (!optionalGame.isPlayer1ShouldGuess()) message = "Now it's the other player's turn"  + "\n"
                    + "Question: " + optionalQuestion.getQuestion() + "\n"
                    + "Answer: " + guessedLetters;;
        }
        else if (playersTurn==2) {
            if (!optionalGame.isPlayer2ShouldGuess()) message = "Now it's the other player's turn"  + "\n"
                    + "Question: " + optionalQuestion.getQuestion() + "\n"
                    + "Answer: " + guessedLetters;;
        }
        else if (playersTurn==3) {
            if (!optionalGame.isPlayer3ShouldGuess()) message = "Now it's the other player's turn"  + "\n"
                    + "Question: " + optionalQuestion.getQuestion() + "\n"
                    + "Answer: " + guessedLetters;;
        }
        else {
            for (int i=0; i<letters.length; i++) {
                if (enteredLetter==letters[i]) {
                    isLettersGuessed[i] = letters[i];
                    letters[i] = '*';
                    guessedLetCount++;
                    optionalPlayer.setPoints(optionalPlayer.getPoints()+random);
                    if (playersTurn==1) {
                        optionalGame.setPlayer1_points(optionalGame.getPlayer1_points()+random);
                    }
                    else if (playersTurn==2) {
                        optionalGame.setPlayer2_points(optionalGame.getPlayer2_points()+random);
                    }
                    else if (playersTurn==3) {
                        optionalGame.setPlayer3_points(optionalGame.getPlayer3_points()+random);
                    }
                }
                if (guessedLetCount == 0) {
                    if (playersTurn==1) {
                        optionalGame.setPlayer1ShouldGuess(false);
                        optionalGame.setPlayer2ShouldGuess(true);
                        optionalGame.setPlayer3ShouldGuess(false);
                        message = "You have answered wrongly. The turn passes to " + player2.get().getName();
                    }
                    else if (playersTurn==2) {
                        optionalGame.setPlayer1ShouldGuess(false);
                        optionalGame.setPlayer2ShouldGuess(false);
                        optionalGame.setPlayer3ShouldGuess(true);
                        message = "You have answered wrongly. The turn passes to " + player3.get().getName();
                    }
                    else if (playersTurn==3) {
                        optionalGame.setPlayer1ShouldGuess(true);
                        optionalGame.setPlayer2ShouldGuess(false);
                        optionalGame.setPlayer3ShouldGuess(false);
                        message = "You have answered wrongly. The turn passes to " + optionalPlayer.getName();
                    }
                }
                if (guessedLetCount > 0) {
                    message = "You have answered correctly. You have got: " + random*guessedLetCount + "!!!";
                }
            }
            String updatedAnswer = new String(letters);
            String updatedGuessedLetters = new String(isLettersGuessed);
            optionalGame.setAnswer(updatedAnswer);
            optionalGame.setGuessedLetters(updatedGuessedLetters);
        }
        playerRepository.save(optionalPlayer);
        gameRepository.save(optionalGame);
        return ResponseEntity.ok().body(message);
    }
    record GuessLetterRequest (
            char letter
    ){}
    record addPlayersRequest (
            Integer player1_id,
            Integer player2_id,
            Integer player3_id,
            Integer question_id

    ){}


    @GetMapping
    public List<Player> getCustomers() {
        return playerRepository.findAll();
    }

    record NewCustomerRequest (
            String name,
            String email,
            Integer age
    ) {}


    @PostMapping
    public void addCustomer(@RequestBody NewCustomerRequest request) {
        Player player = new Player();
        player.setName(request.name());
        player.setEmail(request.email());
        player.setAge(request.age());
        playerRepository.save(player);
    }

    @DeleteMapping("{customerId}")
    public void deleteCustomer(@PathVariable("customerId") Integer id) {
        playerRepository.deleteById(id);
    }

    @PutMapping("{customerId}")
    public void updateCustomer(@PathVariable("customerId") Integer id,
                               @RequestBody NewCustomerRequest upRequest) {
        Optional<Player> optionalCustomer = playerRepository.findById(id);
        Player player = optionalCustomer.get();
        player.setName(upRequest.name());
        player.setEmail(upRequest.email());
        player.setAge(upRequest.age());
        playerRepository.save(player);
    }

    public Integer random_id(){
        Random random = new Random();
        return random.nextInt(6)+1;
    }
    public int randomPoints(){
        Random random = new Random();
        return (random.nextInt(91)+10) * 10;
    }
}
