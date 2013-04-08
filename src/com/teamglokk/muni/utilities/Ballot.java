/*
 * This ballot class allows the loading and easy passing of ballot information for the Muni voting manager
 * and database functions. 
 */
package com.teamglokk.muni.utilities;

import java.util.List;

/**
 *
 * @author shieldsr
 */
public class Ballot {
    
    private int ballotID;
    private String townName;
    private String action;
    private String description;
    private boolean status;
    private boolean result; 
    private Long endTime;
    private boolean binaryVote;
    
    /**
     * Full data constructor
     * @param ballotID
     * @param townName
     * @param action
     * @param description
     * @param status
     * @param result
     * @param endtime 
     */
    public Ballot(int ballotID, String townName, String action, String description, 
            boolean status, boolean result, Long endTime){
        this.ballotID = ballotID;
        this.townName = townName;
        this.action = action;
        this.description = description;
        this.status = status;
        this.result = result;
        this.endTime = endTime;
    }
    
    /**
     * Copy constructor
     * @param copy 
     */
    public Ballot(Ballot copy){
        this.ballotID = copy.getBallotID();
        this.townName = copy.getTownName();
        this.action = copy.getAction();
        this.description = copy.getDescription();
        this.status = copy.getStatus();
        this.result = copy.getResult();
        this.endTime = copy.getEndTime();
    }
    
    public int getBallotID(){ return ballotID; }
    public String getTownName() { return townName; }
    public String getAction() { return action; }
    public String getDescription() { return description; }
    public boolean getStatus() { return status; }
    public boolean getResult() { return result; }
    public Long getEndTime() { return endTime; }
    public boolean getBinaryVote() {return binaryVote; }
    public void setBinaryVote(boolean binaryVote){
        this.binaryVote = binaryVote;
    }
    
    public boolean settleVote(List<Vote> votes ) {
        
        return true; 
    }
}
