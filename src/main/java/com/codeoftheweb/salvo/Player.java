package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import static java.util.stream.Collectors.toList;

@Entity
public class Player {

    //FIELDS
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;
    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    //@JsonIgnore
    @OneToMany(mappedBy="player", fetch= FetchType.EAGER)
    Set<GamePlayer> gamePlayers = new LinkedHashSet<>();
    @OneToMany(mappedBy="player", fetch= FetchType.EAGER)
    Set<Score> scores = new LinkedHashSet<>();


        //CONSTRUCTOR
    public Player(){}
    public Player(String firstName, String lastName, String userName, String password)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;
    }

    //METHODS

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @JsonIgnore
    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

        public Set<Score> getScores() {
        return scores;
    }

    public void setScores(Set<Score> scores) {
        this.scores = scores;
    }

    //MY METHODS

    public void addGamePlayer(GamePlayer gamePlayer) {
        gamePlayer.setPlayer(this);
        gamePlayers.add(gamePlayer);
    }

    //Task-2 Point-2
    @JsonIgnore
    public List<Game> getGames() {
        return gamePlayers.stream().map(sub -> sub.getGame()).collect(toList());
    }

    public void addScore(Score score) {
        //score.setPlayer(this);
        scores.add(score);
    }

    public Score getScore(Game game){
        Score playerScore = this.scores.stream().filter(score -> score.getGame().equals(game)).findFirst().orElse(null);
        return playerScore;
    }


}
