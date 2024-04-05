package com.amigoscode.Repositories;

import com.amigoscode.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository
        extends JpaRepository<Game, Integer> {
}