package com.ur91k.jdiep.ecs.components.gameplay;

import com.ur91k.jdiep.ecs.core.Component;

public class PlayerComponent extends Component {
    private final String playerId;  // Unique network ID
    private boolean isLocalPlayer;
    private String playerName;
    
    public PlayerComponent(String playerId, boolean isLocalPlayer, String playerName) {
        this.playerId = playerId;
        this.isLocalPlayer = isLocalPlayer;
        this.playerName = playerName;
    }
    
    public String getPlayerId() { return playerId; }
    public boolean isLocalPlayer() { return isLocalPlayer; }
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
} 