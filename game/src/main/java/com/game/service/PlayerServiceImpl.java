package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.exception_handling.Exception400;
import com.game.exception_handling.Exception404;
import com.game.repository.PlayerDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Service
public class PlayerServiceImpl implements PlayerService{

    @Autowired
    private PlayerDAO playerDAO;

    @Override
    @Transactional
    public List<Player> getPlayersList(String name, String title, Race race, Profession profession, Long after,
                                       Long before, Boolean banned, Integer minExperience, Integer maxExperience,
                                       Integer minLevel, Integer maxLevel, PlayerOrder order, Integer pageNumber,
                                       Integer pageSize) {


        String query = createQuery(name, title, race, profession, after, before, banned,
                                     minExperience, maxExperience, minLevel, maxLevel);

        List<Player> players = playerDAO.getPlayersList(query);

        sortByOrder(order, players);

        List<Player> result = getSubListByPageNumberAndPageSize(pageNumber, pageSize, players);

        return result;


    }

    private List<Player> getSubListByPageNumberAndPageSize(Integer pageNumber, Integer pageSize, List<Player> players) {
        int start = pageNumber * pageSize;

        if (pageNumber * pageSize + pageSize > players.size()) {
            pageSize = players.size() % pageSize;
        }

        int end = start + pageSize;

        List<Player> result = players.subList(start, end);
        return result;
    }

    private void sortByOrder(PlayerOrder order, List<Player> players) {
        switch (order) {
            case ID:
                players.sort(Comparator.comparing(Player::getId));
                break;
            case NAME:
                players.sort(Comparator.comparing(Player::getName));
                break;
            case EXPERIENCE:
                players.sort(Comparator.comparing(Player::getExperience));
                break;
            case BIRTHDAY:
                players.sort(Comparator.comparing(Player::getBirthday));
                break;
            case LEVEL:
                players.sort(Comparator.comparing(Player::getLevel));
                break;
        }
    }

    @Override
    @Transactional
    public int getPlayersCount(String name, String title, Race race, Profession profession, Long after,
                               Long before, Boolean banned, Integer minExperience, Integer maxExperience,
                               Integer minLevel, Integer maxLevel) {

        String query = createQuery(name, title, race, profession, after, before, banned,
                minExperience, maxExperience, minLevel, maxLevel);

        int count = playerDAO.getPlayersCount(query);

        return count;
    }

    @Override
    @Transactional
    public Player getPlayer(Long id) {

        if (id == 0) throw new Exception404("Not valid id");

        Player player = playerDAO.getPlayer(id);

        if (player == null) {
            throw new Exception400("No such player");
        }

        return player;
    }

    @Override
    @Transactional
    public Player updatePlayer(Long id, Player player) {

        if (id <= 0) throw new Exception404("No valid id");

        Player playerByID  = playerDAO.getPlayer(id);

        if (playerByID == null) throw new Exception400("No such player for update");

        playerByID.setId(id);


        if (player != null) {

            player.setId(id);

            if (player.getName() != null) {
                if (player.getName().length() > 12) {
                    throw new Exception404("Wrong player name");
                }
                playerByID.setName(player.getName());
            } else {
                player.setName(playerByID.getName());
            }

            if (player.getTitle() != null) {

                if (player.getTitle().length() > 30) {
                    throw new Exception404("Wrong player title");
                }
                playerByID.setTitle(player.getTitle());
            } else {
                player.setTitle(playerByID.getTitle());
            }

            if (player.getRace() != null) {
                playerByID.setRace(player.getRace());
            } else {
                player.setRace(playerByID.getRace());
            }

            if (player.getProfession() != null) {
                playerByID.setProfession(player.getProfession());
            } else {
                player.setProfession(playerByID.getProfession());
            }

            if (player.getBirthday() != null) {
                if (player.getBirthday().getTime() < 0) {
                    throw new Exception404("Wrong player`s birthday");
                }
                playerByID.setBirthday(player.getBirthday());
            } else {
                player.setBirthday(playerByID.getBirthday());
            }

            if (player.getExperience() != null) {
                if (player.getExperience() > 10000000 || player.getExperience() < 0) {
                    throw new Exception404("Wrong player`s experience");
                }

                playerByID.setExperience(player.getExperience());

                int level = (int) ((Math.sqrt(2500 + 200 * player.getExperience()) - 50) / 100);
                int untilNextLevel = 50 * (level + 1) * (level + 2) - player.getExperience();

                playerByID.setLevel(level);
                playerByID.setUntilNextLevel(untilNextLevel);

                player.setLevel(level);
                player.setUntilNextLevel(untilNextLevel);
            } else {
                player.setExperience(playerByID.getExperience());
                player.setLevel(playerByID.getLevel());
                player.setUntilNextLevel(playerByID.getUntilNextLevel());
            }

            if (player.getBanned() != null) {
                playerByID.setBanned(player.getBanned());
            } else {
                playerByID.setBanned(false);
                player.setBanned(playerByID.getBanned());
            }
        }

        playerDAO.updatePlayer(playerByID);

        return playerByID;
    }

    @Override
    @Transactional
    public void deletePlayer(Long id) {

        if (id == 0) {
            throw new Exception404("No player with id " + id);
        }

        if (playerDAO.getPlayer(id) == null) {
            throw new Exception400("No player with id " + id);
        }

        playerDAO.deletePlayer(id);
    }

    @Override
    @Transactional
    public Player addPlayer(Player player) {

        if (player.getName() == null || player.getTitle() == null || player.getRace() == null
                || player.getProfession() == null || player.getBirthday() == null || player.getExperience() == null) {
            throw new Exception404("Not full info about new Player");
        }

        if (player.getTitle().length() > 30 || player.getExperience() > 10000000)
            throw new Exception404("Wrong player`s data");

        if (player.getBirthday().getTime() < 0)
            throw new Exception404("Negative birthday");

        int level = (int)(Math.sqrt(2500 + 200 * player.getExperience()) - 50) / 100;
        int untilNextLevel = 50 * (level + 1) * (level + 2) - player.getExperience();

        if (player.getBanned() == null) {
            player.setBanned(false);
        }

        player.setLevel(level);
        player.setUntilNextLevel(untilNextLevel);

        playerDAO.addPlayer(player);

        return player;
    }

    private String createQuery(String name, String title, Race race, Profession profession, Long after,
                               Long before, Boolean banned, Integer minExperience, Integer maxExperience,
                               Integer minLevel, Integer maxLevel) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        String query = "from Player where id like '%' ";
        if (name != null) {
            query += "and name Like '%" + name + "%' ";
        }

        if (title != null) {
            query += "and title Like '%" + title + "%' ";
        }

        if (race != null) {
            query += "and race='" + race.name() + "' ";
        }

        if (profession != null) {
            query += "and profession='" + profession.name() + "' " ;
        }

        if (after != null ) {
            query += "and birthday >= '" + df.format(new Date(after)) +  "' ";
        }

        if (before != null ) {
            query += "and birthday <= '" + df.format(new Date(before)) +  "' ";
        }

        if (minExperience != null ) {
            query += "and experience >= '" + minExperience + "' ";
        }

        if (maxExperience != null ) {
            query += "and experience <= '" + maxExperience + "' ";
        }

        if (minLevel != null ) {
            query += "and level >= '" + minLevel + "' ";
        }

        if (maxLevel != null ) {
            query += "and level <= '" + maxLevel + "' ";
        }

        if (banned != null) {
            query += "and banned =" + banned;
        }
        return query;
    }
}
