package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class SalvoController {

    // All @Autowired repositories req'd
    @Autowired
    private GameRepository gameRepo;
    @Autowired
    private GamePlayerRepository gamePlayerRepo;
    @Autowired
    private PlayerRepository playerRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;






    // Controller for /api/leaderboard
    @RequestMapping("/leaderboard")
    public List<Map> getLeaderboard(){
        return  playerRepo.findAll().stream().map(player -> playerInfoDTO(player)).collect(toList());
    }
    public Map<String, Object> playerInfoDTO(Player player){
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("name", player.getUserName());
        info.put("won", player.getScores().stream().filter(b -> b.getPoints() == 1).collect(toList()));
        info.put("lost", player.getScores().stream().filter(b -> b.getPoints() == 0).collect(toList()));
        info.put("tied", player.getScores().stream().filter(b -> b.getPoints() == 0.5).collect(toList()));
        //info.put("total", player.getScores().stream().filter(b -> b.getPoints() >= 0.0).collect(toList()));
        return info;
    }

    // Controller for /api/games
    @RequestMapping("/games")
    public Map<String, Object> getGames(Authentication authentication){
        Map<String, Object> info = new LinkedHashMap<>();
        if(!isGuest(authentication)) {
            info.put("logged_player", currentUsertDTO(getLoggedUser(authentication)));
        }
        else{
            info.put("logged_player", "guest");
        }
        info.put("games", gameRepo.findAll().stream()
                .map(game -> gameDTO(game)).collect(toList()));
        return info;
    }
    public Map<String, Object> currentUsertDTO(Player player){
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("id", player.getId());
        info.put("name", player.getUserName());
        return info;
    }
    public Map<String, Object> gameDTO (Game game){
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("id", game.getId());
        info.put("created", game.getCreated());
        //info.put("gameplayers", gameplayers(game));
        info.put("gameplayers", game.getGamePlayers()
                .stream().map(gamePlayer -> gamePlayerDTO(gamePlayer))
                .collect(toList()));
        info.put("scores", game.getGamePlayers()
                .stream().map(gamePlayer -> scoresDTO(gamePlayer))
                .collect(toList()));
        return info;
    }
    public Map<String, Object> gamePlayerDTO (GamePlayer gamePlayer){
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("id", gamePlayer.getId());
        info.put("player", playerDTO(gamePlayer.getPlayer()));
        return info;
    }
    public Map<String, Object> playerDTO (Player player){
        Map<String, Object> info = new LinkedHashMap<>();
            info.put("id", player.getId());
            info.put("email", player.getUserName());
        return info;
    }
    public Map<String, Object> scoresDTO(GamePlayer gamePlayer){
        Map<String, Object> info = new LinkedHashMap<>();
        //info.put("gp_id", gamePlayer.getId());
        //info.put("p_id", gamePlayer.getPlayer().getUserName());
        info.put("score", gamePlayer.getScore());
        return info;
    }
    public Player getLoggedUser(Authentication authentication) {
        return playerRepo.findByUserName(authentication.getName());
    }
    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;

    }

    public Optional<GamePlayer> getOpponentGp(GamePlayer currentGp){
        Optional<GamePlayer> opponentGp = currentGp
                .getGame()
                .getGamePlayers()
                .stream()
                .filter(gp -> !gp.getId().equals(currentGp.getId()))
                .findFirst();
        //alternatively, if you want to define your opponent as a GamePlayer type then you would write:
        //GamePlayer opponentGp = currentGp.getGame().getGamePlayers().stream().filter(b -> b.getId() != currentGp.getId()).findFirst().orElse(null);
        //alternatively:
        //GamePlayer oponentGp = currentGp.getGame().getGamePlayers().stream().filter(b -> b.getId() != currentGp.getId()).collect(toList()).get(0);
        return opponentGp;
    }
    // Controller for /api/game_view/nn
    @RequestMapping("/game_view/{gamePlayerId}")
    public Map<String, Object> getGameInfo(@PathVariable Long gamePlayerId){
        Map<String, Object> info = new LinkedHashMap<>();
        GamePlayer currentGp = gamePlayerRepo.findById(gamePlayerId).orElse(null);
        // alternatively:
        // GamePlayer currentGp = gamePlayerRepo.getOne(gamePlayerId);
        info.put("id", currentGp.getGame().getId());
        info.put("created", currentGp.getGame().getCreated());
        info.put("gamePlayers", gamePlayersDTO(currentGp));
        info.put("ships", currentGp.getShips()
        .stream()
        .map(ship -> shipDetailDTO(ship))
        .collect(toList()));
        info.put("salvoes", salvoesDTO(currentGp));
        return info;
    }
    public List<Object> gamePlayersDTO(GamePlayer currentGp){
        List<Object> currentGamePlayers = new ArrayList<>();
        Optional<GamePlayer> oponentGp = getOpponentGp(currentGp);
        //we only need to add two players to the list of players in this game.
        currentGamePlayers.add(playerDTO(currentGp));
        currentGamePlayers.add(playerDTO(oponentGp.orElse(null)));
        return currentGamePlayers;
    }
    public Map<String, Object> playerDTO(GamePlayer argumentGp){
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("id", argumentGp.getId());
        info.put("player", infoPlayerDTO(argumentGp));
        return info;
    }
    public Map<String, Object> infoPlayerDTO(GamePlayer argumentGp){
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("id", argumentGp.getPlayer().getId());
        info.put("email", argumentGp.getPlayer().getUserName());
        return info;
    }
    public Map<String, Object> shipDetailDTO(Ship ship){
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("type", ship.getType());
        info.put("locations", ship.getLocations());
        return info;
    }
    public List<Object> salvoesDTO(GamePlayer currentGp){
        List<Object> info = new ArrayList<>();
        //currentGp.getSalvoes().forEach(salvo -> info.add(salvo));
        //getOpponentGp(currentGp).orElse(null).getSalvoes().forEach(salvo -> info.add(salvo));
        currentGp.getSalvoes().forEach(salvo -> info.add(salvoDTO(salvo)));
        getOpponentGp(currentGp).orElse(null).getSalvoes().forEach(salvo -> info.add(salvoDTO(salvo)));
        return info;
    }
    public Map<String, Object> salvoDTO(Salvo salvo){
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("turn", salvo.getTurn());
        info.put("gamePlayer", salvo.getGamePlayer().getId());
        //alternatively (both ways we shoud get the same Id number:
        //info.put("gamePlayer", argumentGp.getId());
        info.put("locations", salvo.getLocations());
        return info;
    }

    //Java-2 Task-1 Point-5 Method for adding new Player...
    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createUser(@RequestParam String firstName, @RequestParam String lastName, @RequestParam String userName, @RequestParam String password) {
        if (firstName.isEmpty() || lastName.isEmpty() || userName.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "Missing Data!, please Enter all four fields"), HttpStatus.FORBIDDEN);
        }
        Player player = playerRepo.findByUserName(userName);
        if (player != null) {
            return new ResponseEntity<>(makeMap("error", "UserName already exists"), HttpStatus.CONFLICT);
        }
        Player newPlayer = playerRepo.save(new Player(firstName, lastName, userName, passwordEncoder.encode(password)));
        return new ResponseEntity<>(makeMap("id", newPlayer.getUserName()), HttpStatus.CREATED);
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

}
