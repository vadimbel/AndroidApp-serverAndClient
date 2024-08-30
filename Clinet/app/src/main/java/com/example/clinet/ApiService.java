package com.example.clinet;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    // Users endpoints
    @GET("api/v1/users")
    Call<List<Users>> getUsers();

    @POST("api/v1/users/create")
    Call<Void> createUser(@Body Users user);

    @POST("api/v1/users/login")
    Call<Users> login(@Query("username") String username, @Query("password") String password);

    @POST("api/v1/users/logout")
    Call<Void> logout(@Query("username") String username);

    @DELETE("api/v1/users/delete")
    Call<Void> delete(@Query("username") String username);

    // GameRequest endpoints
    @POST("api/games/request")
    Call<Void> createGameRequest(@Body GameRequest gameRequest);

    @GET("api/games/request")
    Call<List<GameRequest>> getAllGameRequests();

    @DELETE("api/games/request")
    Call<Void> deleteGameRequest(@Query("id") String id);

    @GET("api/games/session")
    Call<GameSessions> checkGameSession(@Query("username") String username);
}




