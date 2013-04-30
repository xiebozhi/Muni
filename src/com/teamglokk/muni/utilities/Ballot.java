/* 
 * Muni 
 * Copyright (C) 2013 bobbshields <https://github.com/xiebozhi/Muni> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Binary releases are available freely at <http://dev.bukkit.org/server-mods/muni/>.
*/
package com.teamglokk.muni.utilities;

import java.util.List;

/**
 * A data container for the ballot data.  Used for pulling out of the database
 * @author Bobb
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
