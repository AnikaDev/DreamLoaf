package com.example.dreamloaf.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.dreamloaf.data.User;
import com.example.dreamloaf.repository.AuthRepository;
import java.util.concurrent.Executors;
public class AuthViewModel extends AndroidViewModel {
    private final AuthRepository authRepo;
    private final MutableLiveData<String> currentUserRole = new MutableLiveData<>();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        authRepo = new AuthRepository(application);
    }

    public LiveData<Boolean> checkLoginAvailable(String login) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        Executors.newSingleThreadExecutor().execute(() -> {
            result.postValue(!authRepo.isLoginExists(login));
        });
        return result;
    }

    public LiveData<Boolean> registerUser(User user) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        Executors.newSingleThreadExecutor().execute(() -> {
            if (authRepo.isLoginExists(user.getLogin())) {
                result.postValue(false);
            } else {
                authRepo.register(user);
                currentUserRole.postValue(user.getRole());
                result.postValue(true);
            }
        });
        return result;
    }

    public LiveData<User> loginUser(String login, String password) {
        MutableLiveData<User> result = new MutableLiveData<>();
        Executors.newSingleThreadExecutor().execute(() -> {
            User user = authRepo.login(login, password);
            result.postValue(user);
        });
        return result;
    }
}