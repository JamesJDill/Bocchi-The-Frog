package com.kessoku.bocchifrog.player;

public class PlayerConfig {
    private String name = "";
    private PlayerCharacter character = PlayerCharacter.NULL;
    private Difficulty difficulty = Difficulty.NULL;

    public PlayerConfig() { }

    public PlayerConfig(String name, PlayerCharacter character, Difficulty difficulty) {
        this.name = name;
        this.character = character;
        this.difficulty = difficulty;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PlayerCharacter getCharacter() {
        return character;
    }

    public void setCharacter(PlayerCharacter character) {
        this.character = character;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }
}
