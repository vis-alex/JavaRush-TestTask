package com.game.repository;

import com.game.entity.Player;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PlayerDAOImpl implements PlayerDAO{

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<Player> getPlayersList(String query) {

        Session session = sessionFactory.getCurrentSession();

        List<Player> allPlayers = session.createQuery(query, Player.class)
                .getResultList();

        return allPlayers;
    }

    @Override
    public int getPlayersCount(String query) {

        Session session = sessionFactory.getCurrentSession();

        List<Player> allPlayers = session.createQuery(query, Player.class)
                .getResultList();

        return allPlayers.size();
    }

    @Override
    public Player getPlayer(Long id) {

        Session session = sessionFactory.getCurrentSession();

        Player player = session.get(Player.class, id);

        return player;
    }

    @Override
    public Player updatePlayer(Player player) {
        Session session = sessionFactory.getCurrentSession();

        session.saveOrUpdate(player);


        return player;
    }

    @Override
    public void deletePlayer(Long id) {
        Session session = sessionFactory.getCurrentSession();

        org.hibernate.query.Query<Player> query = session.createQuery("delete from Player where id =: employeeId");
        query.setParameter("employeeId", id);
        query.executeUpdate();


    }

    @Override
    public Player addPlayer(Player player) {
        Session session = sessionFactory.getCurrentSession();

        session.save(player);

        return player;
    }
}
