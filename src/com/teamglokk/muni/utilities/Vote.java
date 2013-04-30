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

/*
 * This Vote class allows for easy data transfer of Muni citizen votes.
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
