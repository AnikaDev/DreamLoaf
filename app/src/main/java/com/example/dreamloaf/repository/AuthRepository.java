package com.example.dreamloaf.repository;

import android.app.Application;
import com.example.dreamloaf.data.AppDatabase;
import com.example.dreamloaf.data.User;
import com.example.dreamloaf.data.UserDao;
import java.util.concurrent.Executors;

public class AuthRepository {
    private final UserDao userDao;

    public AuthRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        userDao = db.userDao();
    }

    public boolean isLoginExists(String login) {
        return userDao.getUserByLogin(login) != null;
    }

    public void register(User user) {
        Executors.newSingleThreadExecutor().execute(() -> userDao.insert(user));
    }

    public User login(String login, String password) {
        return userDao.getUser(login, password);
    }
}
