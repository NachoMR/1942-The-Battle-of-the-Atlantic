package com.codeoftheweb.salvo;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.el.parser.BooleanNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.time.LocalDateTime;
import java.util.*;
import static java.util.stream.Collectors.toList;


@RestController
@RequestMapping("/api")
public class SalvoController {

    // All @Autowired req'd
    @Autowired
    private GameRepository gameRepo;
    @Autowired
    private GamePlayerRepository gamePlayerRepo;
    @Autowired
    private PlayerRepository playerRepo;
    @Autowired
    private ShipRepository shipRepo;
    @Autowired
    private SalvoRepository salvoRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @RequestMapping("/leaderboard")
    public List<Map> getLeaderboard(){
        return  playerRepo.findAll()
                .stream()
                .map(player -> playerInfoDTO(player))
                //.sorted((b1, b2) -> b2.get("total") - b1.get("total"))
                .collect(toList());
    }
    public Map<String, Object> playerInfoDTO(Player player){
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("name", player.getUserName());
        info.put("won", player.getScores().stream().filter(b -> b.getPoints() == 1.0).map(b -> b.getPoints()).collect(toList()));
        info.put("lost", player.getScores().stream().filter(b -> b.getPoints() == 0.0).map(b -> b.getPoints()).collect(toList()));
        info.put("tied", player.getScores().stream().filter(b -> b.getPoints() == 0.5).map(b -> b.getPoints()).collect(toList()));
        info.put("total_points", player.getScores().stream().filter(b -> b.getPoints() == 1.0).count() + 0.5 * player.getScores().stream().filter(b -> b.getPoints() == 0.5).count());
        info.put("total_played", player.getScores().size());
        return info;
    }

    @RequestMapping(path = "/game/{gameId}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGame (@PathVariable Long gameId, Authentication authentication){
        Game currentGame = gameRepo.findById(gameId).orElse(null);
        if(!isGuest(authentication)){
            if(currentGame != null ){
                if(currentGame.getGamePlayers().size() < 2){
                    //if(  currentGame.getPlayers().stream().anyMatch(player -> player != getLoggedUser(authentication)) ) {
                    if(  currentGame.getPlayers().get(0) != getLoggedUser(authentication)  ) {
                        Map<String, Object> info = new LinkedHashMap<>();
                        Game gameToJoin = gameRepo.findAll().stream().filter(game -> game.getId().equals(gameId)).findFirst().orElse(null);
                        GamePlayer newGamePlayer = new GamePlayer(gameToJoin, getLoggedUser(authentication));
                        gamePlayerRepo.save(newGamePlayer);
                        return new ResponseEntity<>(makeMap("gpid", newGamePlayer.getId()), HttpStatus.ACCEPTED);
                    }
                    else {
                        return new ResponseEntity<>(makeMap("error", "You cannot be your own opponent"), HttpStatus.FORBIDDEN);
                    }
                }
                else{
                    return new ResponseEntity<>(makeMap("error", "This game is already full"), HttpStatus.FORBIDDEN);
                }
            }
            else{
                return new ResponseEntity<>(makeMap("error", "The game you're searching for doesn't exist in the Games Repository"), HttpStatus.FORBIDDEN);
            }
        }
        else{
            return  new ResponseEntity<>(makeMap("error", "You need to Log In, please Enter your email and password"), HttpStatus.UNAUTHORIZED);
        }
        }

    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createGame(Authentication authentication) {
        if (!isGuest(authentication)) {
            //getLoggedUser(authentication) brings back the Player who is logged in.
            Game newGame = new Game(LocalDateTime.now());
            GamePlayer newGamePlayer = new GamePlayer(newGame, getLoggedUser(authentication));
            gameRepo.save(newGame);
            gamePlayerRepo.save(newGamePlayer);
            return new ResponseEntity<>(makeMap("gpid", newGamePlayer.getId()), HttpStatus.CREATED);
        }
        else {
            return new ResponseEntity<>(makeMap("error", "You need to Log In, please Enter your email and password"), HttpStatus.UNAUTHORIZED);
        }
    }
    @RequestMapping(path = "/games", method = RequestMethod.GET)
    public Map<String, Object> getGames(Authentication authentication){
        Map<String, Object> info = new LinkedHashMap<>();
        if(!isGuest(authentication)) {
            //getLoggedUser(authentication) brings back the Player who is logged in.
            info.put("logged_player", currentUsertDTO(getLoggedUser(authentication)));
        }
        else{
            info.put("logged_player", "guest");
        }
        info.put("games", gameRepo.findAll().stream()
                .map(game -> gameDTO(game)).collect(toList()));
        return info;
    }
    @RequestMapping(path = "/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> addShips(@PathVariable Long gamePlayerId, Authentication authentication, @RequestBody Set<Ship> shipsList) {
        GamePlayer currentGamePlayer = gamePlayerRepo.findById(gamePlayerId).orElse(null);

        if(!isGuest(authentication)){
            if(currentGamePlayer != null){
                if(currentGamePlayer.getPlayer().equals( getLoggedUser(authentication))){
                    if(currentGamePlayer.getShips().size() == 0) {
                        if(shipsList.size() == 5){
                            if(isLegalShips(shipsList)){
                                for (Ship ship : shipsList) {
                                    currentGamePlayer.addShip(ship);
                                    shipRepo.save(ship);
                                }
                                return new ResponseEntity<>(makeMap("Set of Ships", currentGamePlayer.getShips()), HttpStatus.CREATED);
                            }
                            else{
                                return new ResponseEntity<>(makeMap("error", "Your ships are mispositioned onto your grid. Drag and Drop them onto your Grid!"), HttpStatus.FORBIDDEN);
                            }
                        }
                        else{
                            return new ResponseEntity<>(makeMap("error", "You must place All five Warships on your Grid before start firing!"), HttpStatus.FORBIDDEN);
                        }
                    }
                    else{
                        return new ResponseEntity<>(makeMap("error", "Ships already in place"), HttpStatus.FORBIDDEN);
                    }
                }
                else{
                    return new ResponseEntity<>(makeMap("error", "You can only manage your own ships"), HttpStatus.UNAUTHORIZED);
                }
            }
            else {
                return new ResponseEntity<>(makeMap("error", "No GamePlayer found for you"), HttpStatus.UNAUTHORIZED);
            }
        }
        else{
            return new ResponseEntity<>(makeMap("error", "You need to Log In, please Enter your email and password"), HttpStatus.UNAUTHORIZED);
        }
    }

    private Boolean isLegalShips(Set<Ship> shipsList){
        Boolean result = true;
        for (Ship ship : shipsList) {
            if (!checkLength(ship) || !checkAlignement(ship)) {
                result = false;
            }
        }
        return result;
    }
    private Boolean checkLength(Ship ship){
        Boolean result = false;
        switch(ship.getType()){
            case "carrier":
                if(ship.getLocations().size() == 5){
                    result = true;
                }
                break;
            case "battleship":
                if(ship.getLocations().size() == 4){
                    result = true;
                }
                break;
            case "destroyer":
                if(ship.getLocations().size() == 3){
                    result = true;
                }
                break;
            case "submarine":
                if(ship.getLocations().size() == 3){
                    result = true;
                }
                break;
            case "patrol_boat":
                if(ship.getLocations().size() == 2){
                    result = true;
                }
                break;
            default:
                result = false;
                break;
        }
        return result;
    }
    private Boolean checkAlignement(Ship ship){
        //Boolean result = true;
        //return result;
        return true;
    }
//    private Boolean checkAlignement(Ship ship){
//        Boolean result = false;
//        List<String> letterRow = new ArrayList<>();
//        List<String> numberCol = new ArrayList<>();
//        ship.getLocations().forEach(loc -> {
//            letterRow.add(loc.substring(0,1));
//            numberCol.add(loc.substring(1,2));
//        });
//        if( identicalItems(letterRow) || identicalItems(numberCol) ){
//            result = true;
//        }
//        return result;
//    }

//    private Boolean identicalItems(List<String> sampleList){
//        Boolean result = false;
//        String firstItem = sampleList.get(0);
//        Integer length = sampleList.size();
//        Integer lengthFiltered = sampleList.stream().filter(item -> item.equals(firstItem)).collect(toList()).size();
//        if(length == lengthFiltered){
//            result = true;
//        }
//        return result;
//    }


    @RequestMapping(path = "/games/players/{gamePlayerId}/salvoes", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> addSalvo(@PathVariable Long gamePlayerId, Authentication authentication, @RequestBody Salvo thisSalvo){
        GamePlayer currentGamePlayer = gamePlayerRepo.findById(gamePlayerId).orElse(null);
        if(!isGuest(authentication)){
            if(currentGamePlayer != null){
                if(currentGamePlayer.getPlayer().equals(getLoggedUser(authentication))){
                    if(getOpponentGp(currentGamePlayer).orElse(null) != null) {
                        if (currentGamePlayer.getShips().size() > 0 && getOpponentGp(currentGamePlayer).orElse(null).getShips().size() > 0) {

                            if (thisSalvo.getLocations().size() <= 5) {

                                if (currentGamePlayer.getSalvoes().size() <= getOpponentGp(currentGamePlayer).orElse(null).getSalvoes().size()) {
                                    thisSalvo.setTurn(currentGamePlayer.getSalvoes().size() + 1);
                                    currentGamePlayer.addSalvo(thisSalvo);
                                    salvoRepo.save(thisSalvo);
                                    return new ResponseEntity<>(makeMap("Salvo Created", "Five Locations corresponding to the Salvo in this turn have been created"), HttpStatus.CREATED);
                                } else {
                                    return new ResponseEntity<>(makeMap("error", "Wait for your opponent to place his/her salvoes"), HttpStatus.FORBIDDEN);
                                }

                            } else {
                                return new ResponseEntity<>(makeMap("error", "You can only shoot at five locations each salvo"), HttpStatus.UNAUTHORIZED);
                            }

                        } else {
                            return new ResponseEntity<>(makeMap("error", "You need to wait for both Fleets to be in place before start firing"), HttpStatus.UNAUTHORIZED);
                        }
                    } else{
                        return new ResponseEntity<>(makeMap("error", "Wait for your opponent to join the Game"), HttpStatus.FORBIDDEN);
                    }
                }
                else{
                    return new ResponseEntity<>(makeMap("error", "You can only manage your own salvoes"), HttpStatus.UNAUTHORIZED);
                }
            }
            else{
                return new ResponseEntity<>(makeMap("error", "No GamePlayer found for you"), HttpStatus.UNAUTHORIZED);
            }
        }
        else{
            return new ResponseEntity<>(makeMap("error", "You need to Log In, please Enter your email and password"), HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping("/game_view/{gamePlayerId}")
    public Object getGameInfo(@PathVariable Long gamePlayerId, Authentication authentication) {
        if (!isGuest(authentication)) {
            //getLoggedUser(authentication) brings back the Player who is logged in.
            if (getLoggedUser(authentication).getGamePlayers().stream().anyMatch(player -> player.getId() == gamePlayerId)) {
                //returns the Map<String, Object> we had previously.....
                Map<String, Object> info = new LinkedHashMap<>();
                GamePlayer currentGp = gamePlayerRepo.findById(gamePlayerId).orElse(null);
                // alternatively:
                // GamePlayer currentGp = gamePlayerRepo.getOne(gamePlayerId);
                info.put("id", currentGp.getGame().getId());
                info.put("created", currentGp.getGame().getCreated());
                info.put("gamePlayers", gamePlayersDTO(currentGp));
                //if(getOpponentGp(currentGp).orElse(null) != null) {
                if(currentGp.getShips() != null) {
                    info.put("ships", currentGp.getShips()
                            .stream()
                            .map(ship -> shipDetailDTO(ship))
                            .collect(toList()));
                }
                else{
                    info.put("ships", EMPTY_ARRAY);
                }
                if(currentGp.getSalvoes() != null) {

                    info.put("salvoes", salvoesDTO(currentGp));
                }
                else{
                    info.put("salvoes", EMPTY_ARRAY);
                }
                info.put("gameStatus", "Show the current Status of the Game...");
                return info;
            }
            else {
                return new ResponseEntity<>(makeMap("error", "You're very smart but cheating is not permitted"), HttpStatus.UNAUTHORIZED);
            }
        }
        else {
            return new ResponseEntity<>(makeMap("error", "You need to Log In, please Enter your email and password"), HttpStatus.UNAUTHORIZED);
        }
    }
    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody Player player) {
        if (player.getFirstName().isEmpty() || player.getLastName().isEmpty() || player.getUserName().isEmpty() || player.getPassword().isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "Missing Data!, please Enter all four fields"), HttpStatus.FORBIDDEN);
        }

        if (playerRepo.findByUserName(player.getUserName()) != null) {
            return new ResponseEntity<>(makeMap("error", "UserName already exists"), HttpStatus.CONFLICT);
        }
        player.setPassword(passwordEncoder.encode(player.getPassword()));
        Player newPlayer = playerRepo.save(player);
        return new ResponseEntity<>(makeMap("id", newPlayer.getUserName()), HttpStatus.CREATED);
    }

    public Map<String, Object> currentUsertDTO(Player player){
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("pid", player.getId());
        info.put("name", player.getUserName());
        return info;
    }
    public Map<String, Object> gameDTO (Game game){
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("id", game.getId());
        info.put("created", game.getCreated());
        //info.put("players", game.getGamePlayers().stream().map(gamePlayer -> playersDTO(gamePlayer)).collect(toList()));
        info.put("gameplayers", game.getGamePlayers()
                .stream().map(gamePlayer -> gamePlayerDTO(gamePlayer))
                .collect(toList()));
        info.put("scores", game.getGamePlayers()
                .stream().map(gamePlayer -> scoresDTO(gamePlayer))
                .collect(toList()));
        return info;
    }
    public Map<String, Object> playersDTO (GamePlayer gamePlayer){
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("gpid", gamePlayer.getId());
        info.put("pid", gamePlayer.getPlayer().getId());
        info.put("name", gamePlayer.getPlayer().getUserName());
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
    public List<Object> gamePlayersDTO(GamePlayer currentGp){
            List<Object> info = new ArrayList<>();
            Optional<GamePlayer> oponentGp = getOpponentGp(currentGp);
            //we only need to add two players to the list of players in this game.
            info.add(playerDTO(currentGp));
            if(oponentGp.orElse(null) != null) {
                info.add(playerDTO(oponentGp.orElse(null)));
            }
            return info;
    }
    public Map<String, Object> playerDTO(GamePlayer gp){
            Map<String, Object> info = new LinkedHashMap<>();
            info.put("id", gp.getId());
            if(getOpponentGp(gp).orElse(null) != null) {
                info.put("lostShips", lostShipsDTO(gp));
            }
            else{
                info.put("lostShips", EMPTY_ARRAY);
            }
            info.put("player", infoPlayerDTO(gp));
            return info;
        }
    public List<String> lostShipsDTO (GamePlayer gp){
        List<String> info = new ArrayList<>();
        GamePlayer opponentGp = getOpponentGp(gp).orElse(null);
        List<String> allSalvoLocations = new ArrayList<>();
         //opponentGp.getSalvoes().stream().forEach(  salvo -> salvo.getLocations().stream().forEach(loc -> allSalvoLocations.add(loc))  );
         opponentGp.getSalvoes().forEach(  salvo -> salvo.getLocations().forEach(loc -> allSalvoLocations.add(loc))  );

        gp.getShips()
                .forEach(   ship -> {if(ship.getLocations().stream().allMatch(loc -> allSalvoLocations.contains(loc))){
                                        info.add(ship.getType());
                                    }
                                }
                        );

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
            if(getOpponentGp(currentGp).orElse(null) != null) {
                getOpponentGp(currentGp).orElse(null).getSalvoes().forEach(salvo -> info.add(salvoDTO(salvo)));
            }
            return info;
        }
    public Map<String, Object> salvoDTO(Salvo salvo){
            Map<String, Object> info = new LinkedHashMap<>();
            info.put("turn", salvo.getTurn());
            info.put("gamePlayer", salvo.getGamePlayer().getId());
            //alternatively (both ways we shoud get the same Id number:
            //info.put("gamePlayer", argumentGp.getId());
            info.put("locations", salvo.getLocations());
            info.put("hits", salvo.getLocations().stream().map(loc -> hitsDTO(loc, salvo)).filter(map -> !map.isEmpty()).collect(toList()));
            return info;
        }

    public Map<String, Object> hitsDTO(String loc, Salvo salvo){
            Map<String, Object> info = new LinkedHashMap<>();
            GamePlayer opponentGp = getOpponentGp(salvo.getGamePlayer()).orElse(null);
            if(opponentGp != null){
                Ship hitShip = opponentGp.getShips().stream().filter(ship -> ship.getLocations().contains(loc)).findFirst().orElse(null);
                if(hitShip != null){
                    info.put(loc, hitShip.getType());
                }
            }
            return info;
    }

    private Player getLoggedUser(Authentication authentication) {
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
        //alternatively, if you want to define your opponent as a GamePlayer type (not an Optional) then you would write:
        //GamePlayer opponentGp = currentGp.getGame().getGamePlayers().stream().filter(b -> b.getId() != currentGp.getId()).findFirst().orElse(null);
        //alternatively:
        //GamePlayer oponentGp = currentGp.getGame().getGamePlayers().stream().filter(b -> b.getId() != currentGp.getId()).collect(toList()).get(0);
        return opponentGp;
    }
    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
    private static final String[] EMPTY_ARRAY = new String[0];

}
