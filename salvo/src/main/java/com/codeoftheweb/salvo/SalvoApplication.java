package com.codeoftheweb.salvo;

import org.aspectj.apache.bcel.util.Play;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner init(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository) {
		return (args) -> {

			Player p1 = new Player("Jack", "Bauer", "j.bauer@ctu.gov", "passJack");
			Player p2 = new Player("Chloe", "O'Brian", "c.obrian@ctu.gov", "passChloe");
			Player p3 = new Player("Kim", "Bauer", "kim_bauer@gmail.com", "passKim");
			Player p4 = new Player("Tony", "Almeida", "t.almeida@ctu.gov", "PassTony");
			Game g1 = new Game(LocalDateTime.now());
			Game g2 = new Game(LocalDateTime.of(2019, Month.FEBRUARY, 04, 14, 30));
			Game g3 = new Game(LocalDateTime.parse("2019-02-07T17:45:00"));
			Game g4 = new Game(LocalDateTime.parse("2019-01-02T17:45:00"));
			Game g5 = new Game(LocalDateTime.parse("2019-01-01T17:45:00"));
			Game g6 = new Game(LocalDateTime.parse("2019-02-02T17:45:00"));
			Game g7 = new Game(LocalDateTime.parse("2019-01-14T17:45:00"));
			Game g8 = new Game(LocalDateTime.parse("2019-01-30T17:45:00"));

			playerRepository.save(p1);
			playerRepository.save(p2);
			playerRepository.save(p3);
			playerRepository.save(p4);
			gameRepository.save(g1);
			gameRepository.save(g2);
			gameRepository.save(g3);
			gameRepository.save(g4);
			gameRepository.save(g5);
			gameRepository.save(g6);
			gameRepository.save(g7);
			gameRepository.save(g8);
			gamePlayerRepository.save(new GamePlayer(g1, p1));
			gamePlayerRepository.save(new GamePlayer(g1, p2));
			gamePlayerRepository.save(new GamePlayer(g2, p1));
			gamePlayerRepository.save(new GamePlayer(g2, p2));
			gamePlayerRepository.save(new GamePlayer(g3, p2));
			gamePlayerRepository.save(new GamePlayer(g3, p4));
			gamePlayerRepository.save(new GamePlayer(g4, p2));
			gamePlayerRepository.save(new GamePlayer(g4, p1));
			gamePlayerRepository.save(new GamePlayer(g5, p4));
			gamePlayerRepository.save(new GamePlayer(g5, p1));
			gamePlayerRepository.save(new GamePlayer(g6, p3));
			gamePlayerRepository.save(new GamePlayer(g7, p4));
			gamePlayerRepository.save(new GamePlayer(g8, p3));
			gamePlayerRepository.save(new GamePlayer(g8, p4));
		};
	}

}