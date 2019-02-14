package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
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

    // Controller for /api/games
    @RequestMapping("/games")
    public List<Map> getGames() {
         return gameRepo.findAll().stream()
                 .map(game -> gameDTO(game)).collect(toList());
    }
    public Map<String, Object> gameDTO (Game game){
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("id", game.getId());
        info.put("created", game.getCreated());
        info.put("gameplayers", game.getGamePlayers().stream()
                                .map(gamePlayer -> gamePlayerDTO(gamePlayer)).collect(toList()));
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


    // Controller for /api/game_view/nn
    @RequestMapping("/game_view/{gamePlayerId}")
    public Map<String, Object> getGameInfo(@PathVariable Long gamePlayerId){
        Map<String, Object> info = new LinkedHashMap<>();
        GamePlayer currentGp = gamePlayerRepo.findById(gamePlayerId).orElse(null);
        // the below line of code will do the same as the one above
        // GamePlayer currentGp = gamePlayerRepo.getOne(gamePlayerId);
        info.put("id", currentGp.getGame().getId());
        info.put("created", currentGp.getGame().getCreated());
        info.put("gamePlayers", gamePlayersDTO(currentGp));
        info.put("ships", currentGp.getShips()
        .stream()
        .map(ship -> shipDetailDTO(ship))
        .collect(toList()));
        return info;
    }

    public List<Object> gamePlayersDTO(GamePlayer currentGp){
        List<Object> currentGamePlayers = new ArrayList<>();
        GamePlayer oponentGp = currentGp.getGame().getGamePlayers().stream().filter(b -> b.getId() != currentGp.getId()).collect(toList()).get(0);
        //we only need to add two players to the list of players in this game.
        currentGamePlayers.add(playerDTO(currentGp));
        currentGamePlayers.add(playerDTO(oponentGp));
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

}
