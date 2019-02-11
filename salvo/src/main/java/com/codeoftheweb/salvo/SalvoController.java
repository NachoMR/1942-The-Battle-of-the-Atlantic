package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository gameRepo;

    @RequestMapping("/games")
    public List<Map> getGames() {
         return gameRepo.findAll().stream()
                 .map(game -> gameDTO(game)).collect(toList());
    }

    public Map<String, Object> gameDTO (Game game){
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("id", game.getId());
        info.put("created", game.getLocalDateTime());
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

}
