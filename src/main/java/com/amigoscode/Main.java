package com.amigoscode;

import com.amigoscode.Repositories.GameRepository;
import com.amigoscode.Repositories.PlayerRepository;
import com.amigoscode.Repositories.QuesAnsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.springframework.http.ResponseEntity;
import java.util.Scanner;

@SpringBootApplication
@RestController
@RequestMapping("api/v1/customers")
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
    @PostMapping("/newgame")
    public ResponseEntity<String> addPlayers(@RequestBody addPlayersRequest addRequest) {
        Optional<QuesAns> randomQuestion = quesAnsRepository.findById(random_id());
        String question = randomQuestion.isPresent() ? randomQuestion.get().getQuestion() : "Unknown";
        Integer question_id = randomQuestion.isPresent() ? randomQuestion.get().getId() : 2;
        Game newGame = new Game();
        newGame.setPlayer1ShouldGuess(true);
        newGame.setPlayer1_id(addRequest.player1_id());
        newGame.setPlayer2_id(addRequest.player2_id());
        newGame.setPlayer3_id(addRequest.player3_id());
        newGame.setQuestion_id(question_id);
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
                 + "Question: " + question;
        return ResponseEntity.ok().body(message);
    }
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
        int ran = random.nextInt(6)+1;
        return ran;
    }
}
