package com.example.bilguessr.Models;

import java.util.ArrayList;
import java.util.Random;

public class Room {
    private String ID;
    private String lobbyID;
    private String gameMode;
    private int currentPlayerNo;
    private ArrayList<User> players;

    public String getID() {
        return ID;
    }

    public String getLobbyID() {
        return lobbyID;
    }

    public String getGameMode() {
        return gameMode;
    }

    public int getCurrentPlayerNo() {
        return currentPlayerNo;
    }

    public ArrayList<User> getPlayers() {
        return players;
    }

    public int getMaximumNumberOfPlayer() {
        return maximumNumberOfPlayer;
    }

    private int maximumNumberOfPlayer;
    public Room()
    {}
    public Room(String ID,String gameMode, int maximumNumberOfPlayer) {
        this.ID = ID;
        players = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < 6; i++) {
            lobbyID+=rand.nextInt()*Math.pow(10,i);
        }

        this.currentPlayerNo = players.size();
        this.maximumNumberOfPlayer = maximumNumberOfPlayer;
    }
    public void addPlayer(User u)
    {
        players.add(u);
    }
    public boolean isAvailable()
    {
        return players.size()<maximumNumberOfPlayer;
    }



}
