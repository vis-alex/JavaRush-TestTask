package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.exception_handling.Exception400;
import com.game.exception_handling.Exception404;
import com.game.exception_handling.PlayerIncorrectData;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    public PlayerController() {

    }

    @GetMapping("/players")
    public List<Player> getPlayersList(@RequestParam(value = "name", required = false) String name,
                                       @RequestParam(value = "title", required = false) String title,
                                       @RequestParam(value = "race", required = false) Race race,
                                       @RequestParam(value = "profession", required = false) Profession profession,
                                       @RequestParam(value = "after", required = false) Long after,
                                       @RequestParam(value = "before", required = false) Long before,
                                       @RequestParam(value = "banned", required = false) Boolean banned,
                                       @RequestParam(value = "minExperience", required = false) Integer minExperience,
                                       @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
                                       @RequestParam(value = "minLevel", required = false) Integer minLevel,
                                       @RequestParam(value = "maxLevel", required = false) Integer maxLevel,
                                       @RequestParam(value = "order", required = false, defaultValue = "ID") PlayerOrder order,
                                       @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                                       @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize
    ) {



        List<Player> allPlayers = playerService.getPlayersList(
                name, title, race, profession, after, before,
                banned, minExperience, maxExperience, minLevel,
                maxLevel, order, pageNumber, pageSize
        );

        return allPlayers;
    }

    @GetMapping("/players/count")
    public int getPlayersCount(@RequestParam(value = "name", required = false) String name,
                               @RequestParam(value = "title", required = false) String title,
                               @RequestParam(value = "race", required = false) Race race,
                               @RequestParam(value = "profession", required = false) Profession profession,
                               @RequestParam(value = "after", required = false) Long after,
                               @RequestParam(value = "before", required = false) Long before,
                               @RequestParam(value = "banned", required = false) Boolean banned,
                               @RequestParam(value = "minExperience", required = false) Integer minExperience,
                               @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
                               @RequestParam(value = "minLevel", required = false) Integer minLevel,
                               @RequestParam(value = "maxLevel", required = false) Integer maxLevel) {

        return playerService.getPlayersCount( name, title, race, profession, after, before,
                banned, minExperience, maxExperience, minLevel,
                maxLevel);
    }

    @GetMapping("/players/{id}")
    public Player getPlayer(@PathVariable Long id) {

        Player player = playerService.getPlayer(id);

        return player;
    }

    @PostMapping("/players")
    public Player addPlayer(@RequestBody Player player) {

        playerService.addPlayer(player);

        return player;
    }

    @PostMapping("/players/{id}")
    public Player updatePlayer(@PathVariable Long id, @RequestBody Player player) {

        playerService.updatePlayer(id, player);

        return player;
    }

    @DeleteMapping("/players/{id}")
    public void deleteEmployee(@PathVariable Long id) {

        playerService.deletePlayer(id);
    }

    @ExceptionHandler
    public ResponseEntity<PlayerIncorrectData> handleException(Exception404 exception) {
        PlayerIncorrectData data = new PlayerIncorrectData();
        data.setInfo(exception.getMessage());

        return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<PlayerIncorrectData> handleException(Exception400 exception) {
        PlayerIncorrectData data = new PlayerIncorrectData();
        data.setInfo(exception.getMessage());

        return new ResponseEntity<>(data, HttpStatus.NOT_FOUND);
    }
}
