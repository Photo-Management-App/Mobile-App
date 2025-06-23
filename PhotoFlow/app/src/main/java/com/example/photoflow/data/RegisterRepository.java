package com.example.photoflow.data;

import com.example.photoflow.data.model.RegisteredUser;

public class RegisterRepository {

    private static volatile RegisterRepository instance;

    private RegisterDataSource dataSource;

    private RegisteredUser user = null;

    private RegisterRepository(RegisterDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static RegisterRepository getInstance(RegisterDataSource dataSource) {
        if (instance == null) {
            instance = new RegisterRepository(dataSource);
        }
        return instance;
    }

    private void setRegisteredUser(RegisteredUser user) {
        this.user = user;
        // You can save user locally encrypted if needed
    }

    public void register(String username, String password, String email, RegisterDataSource.RegisterCallback callback) {
        dataSource.register(username, password, email, new RegisterDataSource.RegisterCallback() {
            @Override
            public void onSuccess(Result<RegisteredUser> result) {
                setRegisteredUser(((Result.Success<RegisteredUser>) result).getData());
                callback.onSuccess(result);
            }

            @Override
            public void onError(Result.Error error) {
                callback.onError(error);
            }
        });
    }
}

