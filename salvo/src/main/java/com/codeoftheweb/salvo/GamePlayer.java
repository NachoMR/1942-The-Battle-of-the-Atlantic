package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

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
    //@JsonIgnore
    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER)
    private Set<Ship> ships = new LinkedHashSet<>();
    //@JsonIgnore
    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER)
    private Set<Salvo> salvoes = new LinkedHashSet<>();

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

    public Set<Ship> getShips() {
        return ships;
    }

    public void setShips(Set<Ship> ships) {
        this.ships = ships;
    }

    public Set<Salvo> getSalvoes() {
        return salvoes;
    }

    public void setSalvoes(Set<Salvo> salvoes) {
        this.salvoes = salvoes;
    }

    //MY METHODS
    public void addShip(Ship ship) {
        ship.setGamePlayer(this);
        ships.add(ship);
    }

    public void addSalvo(Salvo salvo) {
        salvo.setGamePlayer(this);
        salvoes.add(salvo);
    }

    public Optional<Score> getScore(){
        return this.player.getScore(this.game);
    }

}
