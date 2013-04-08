/*
 * This Vote class allows for easy data transfer of Muni citizen votes.
 */
package com.teamglokk.muni.utilities;

/**
 *
 * @author shieldsr
 */
public class Vote {
    
    private int voteID;
    private String playerName;
    private int ballotID; 
    private String answer; 
    private Long timeStamp;
    
    public Vote (int voteID, String playerName, int ballotID, String answer, Long timeStamp){
        this.voteID = voteID;
        this.playerName = playerName;
        this.ballotID = ballotID;
        this.answer = answer;
        this.timeStamp = timeStamp;
    }
    public Vote (Vote copy){
        this.voteID = copy.getVoteID();
        this.playerName = copy.getPlayerName();
        this.ballotID = copy.getBallotID();
        this.answer = copy.getAnswer();
        this.timeStamp = copy.getTimeStamp();
    }
    
    public int getVoteID(){ return voteID; }
    public String getPlayerName() { return playerName; }
    public int getBallotID() { return ballotID; }
    public String getAnswer() { return answer; }
    public Long getTimeStamp() { return timeStamp; }
    
    public boolean getBinaryAnswer() { 
        if (answer.equalsIgnoreCase("for") ){
            return true;
        } else if ( answer.equalsIgnoreCase("against") ){
            return false;
        } else {
            // need to get access to logger to say an invalid answer was entered
            return false; 
        }
    }
    
}
