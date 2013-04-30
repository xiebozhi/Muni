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
package com.teamglokk.muni;

import org.bukkit.entity.Player;

public class VotingManager {
    Muni plugin;
    
    public VotingManager(Muni instance) {
        plugin = instance; 
    }
    // Needing a global vote system?
    // How to do automatic actions when votes pass (such as replace mayor, no confidence deputy)
    /**
     * Allows the player to vote on a town issue
     * @param town
     * @param voter
     * @param ballot
     * @param answer 
     */
    public void vote(Town town, Player voter, int ballot, boolean answer){
        //check has voted
        //insert into votes (townName,playerName, ballotID, answer
    }
    
    /**
     * Allows the player to change his/her vote
     * @param town
     * @param voter
     * @param ballot
     * @param answer 
     */
    public void changeVote(Town town, Player voter, int ballot, boolean answer){
        //update votes set vote=answer where playerName, townName, ballotID
    }
    
    /**
     * Checks the vote table to ensure no double voting has occured
     * @param town
     * @param voter
     * @param ballot
     * @return 
     */
    public boolean hasVoted(Town town, Player voter, int ballot ){
        // select from votes where townName, playerName, ballotID
        return true; 
    }
    
    /**
     * Adds an issue to be voted upon 
     * @param town
     * @param officer
     * @param ballot
     * @param description 
     */
    public void addBallot(Town town, Player officer, int ballot, String action, String description, int daysToVote){
        //set action
        //set description
        //set status = true
        //set result = null
        
    }
    
    /**
     * Allows the officer to stop the voting process before it finishes
     * @param ballot 
     */
    public void removeBallot(int ballot ){
        //db delete from ballots against PK = ballot
    }
    
    /**
     * Will display to the town the final result of the ballot 
     * @param town
     * @param ballot 
     */
    public void finishBallot(Town town, int ballot){
        //set status to false
        //set result to result of voting
    }
    
}
