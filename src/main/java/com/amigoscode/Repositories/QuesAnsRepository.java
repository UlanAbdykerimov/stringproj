package com.amigoscode.Repositories;
import com.amigoscode.QuesAns;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuesAnsRepository extends JpaRepository<QuesAns, Integer> {
    // здесь могут быть добавлены дополнительные методы, если необходимо
}