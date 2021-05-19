package com.game.repository;

import com.game.entity.Player;

import java.util.List;

public interface PlayerDAO {
    List<Player> getPlayersList(String query);

    int getPlayersCount(String query);

    Player getPlayer(Long id);

    Player updatePlayer(Player player);

    void deletePlayer(Long id);

    Player addPlayer (Player player);
}
