package com.codeoftheweb.salvo;

public enum State {
    SHIP("Waiting for your opponent to place his/her Ships"),
    GAMEOVER("Game is over"),
    TURN("Waiting for your opponent to fire his/her Salvo"),
    OPPONENT("Waiting for opponent to Join the Game"),
    GOAHEAD("Go Ahead playing");


    //FIELDS
    //private Boolean booleanField;
    private String stateMessage;


    //CONSTRUCTOR
    private State(String stateMessage){}


    //METHOD
    public String getStateMessage() {
        return stateMessage;
    }

    public void setStateMessage(String stateMessage) {
        this.stateMessage = stateMessage;
    }
}
