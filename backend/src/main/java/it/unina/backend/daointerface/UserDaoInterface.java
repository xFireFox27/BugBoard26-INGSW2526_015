package it.unina.backend.daointerface;

import it.unina.backend.entity.User;

public interface UserDaoInterface{

    public User getUsers();
    public User findByEmailAndPassword(String email, String plainPassword);
}