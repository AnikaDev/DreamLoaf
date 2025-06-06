package com.example.dreamloaf.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    void insert(User user);

    @Query("SELECT * FROM users WHERE login = :login AND password = :password")
    User getUser(String login, String password);

    @Query("SELECT * FROM users WHERE role = :role")
    List<User> getUsersByRole(String role);

    @Query("SELECT * FROM users")
    LiveData<List<User>> getAllUsers();

    @Query("SELECT * FROM users WHERE login = :login LIMIT 1")
    User getUserByLogin(String login);
}
