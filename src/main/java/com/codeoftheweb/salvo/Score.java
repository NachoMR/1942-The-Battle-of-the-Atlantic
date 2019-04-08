package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Score {

    //FIELDS
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;
    private Double points;
    private LocalDateTime finishDate = LocalDateTime.now();

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;

    //CONSTRUCTOR
    public Score(){}
    public Score(Double points, Game game, Player player){
        this.points = points;
        this.game = game;
        this.player = player;
        game.addScore(this);
        player.addScore(this);
    }
    //METHODS

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getPoints() {
        return points;
    }

    public void setPoints(Double points) {
        this.points = points;
    }

    public LocalDateTime getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(LocalDateTime finishDate) {
        this.finishDate = finishDate;
    }
    @JsonIgnore
    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
    @JsonIgnore
    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }


    //MY METHODS


}
