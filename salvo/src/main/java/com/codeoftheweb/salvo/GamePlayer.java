package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class GamePlayer {

    //FIELDS
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;
    private LocalDateTime joinedGameDate;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;


    //CONSTRUCTOR
    public GamePlayer(){}
    public GamePlayer(Game game, Player player){
        this.game = game;
        this.player = player;
    }

    //METHODS
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getJoinedGameDate() {
        return joinedGameDate;
    }

    public void setJoinedGameDate(LocalDateTime joinedGameDate) {
        this.joinedGameDate = joinedGameDate;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }


    //MY METHODS
}
