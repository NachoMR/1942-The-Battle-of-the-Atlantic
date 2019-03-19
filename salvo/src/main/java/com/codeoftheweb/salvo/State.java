package com.codeoftheweb.salvo;

public enum State {
    SHIP(true),
    SALVO(true),
    GAMEOVER(true);


    //FIELDS
    private Boolean bool;


    //CONSTRUCTOR
    //private State(){}
    private State(Boolean bool){
        this.bool = bool;
    }


    //METHOD
    public Boolean getState(){
        return this.bool;
    }

    public void setState(Boolean bool){
        this.bool = bool;
    }
}
