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

import com.teamglokk.muni.Citizen;
import com.teamglokk.muni.Muni;
import com.teamglokk.muni.Town;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;

//import org.sqlite.JDBC;
//import com.mysql.jdbc.Driver;

//import java.util.logging.Level;
//import java.util.logging.Logger;


//import com.teamglokk.muni;
/**
 * Wraps the database functions to provide simple functions
 * @author BobbShields
 */
public class dbWrapper extends Muni {

    private Muni plugin;
    
    private Connection conn = null;
    private Statement stmt = null;
    public ResultSet rs = null;
    
    public dbWrapper( Muni instance ){
        plugin = instance;
    }
    
    /**
     * Opens a connection to the database
     * @throws SQLException 
     */
    public void db_open() throws SQLException {
        if(plugin.isSQLdebug()){
            String temp = plugin.useMysql() ? "Opening DB (mysql)":"Opening DB (sqlite)" ;
            plugin.getLogger().info(temp);
        }
        String driver = plugin.useMysql() ?"org.mysql.jdbc.Driver" :"org.sqlite.JDBC" ;
        try {
            Class.forName(driver);
            
            } catch (ClassNotFoundException ex){
                plugin.getLogger().severe("db_open (db driver " +driver+ " not found): "+ ex.getMessage() );
            }
            conn = DriverManager.getConnection(plugin.getDB_URL(),plugin.getDB_user(),plugin.getDB_pass());
            // MySQL is not yet tested!!! 31 Jan 2013 RJS
        
        stmt = conn.createStatement();
    }
    
    /**
     * Closes the connection to the database
     * @throws SQLException 
     */
    public void db_close() throws SQLException {
        if ( rs != null) { rs.close(); }
        if ( stmt != null) { stmt.close(); }
        if ( conn != null ) { conn.close();}
        if(plugin.isSQLdebug()){plugin.getLogger().info("Closed DB");}
    }
    
    /**
     * Checks to see if a field exists in a certain table
     * @param table     the table to be used
     * @param col       column name 
     * @param value     check to see if this value exists in the column of the table
     * @return true if exists, false if not
     */
    public boolean checkExistence ( String table, String col, String value ){
        boolean rtn = false;
        String SQL = "SELECT "+col+" FROM "+plugin.getDB_prefix()+table+
                    " WHERE "+col+"='"+value +"';";
        try {
            
            db_open();
            if(plugin.isSQLdebug() ){plugin.getLogger().info(SQL);}
            rs = stmt.executeQuery(SQL); 
            //rs.next();
            String temp = rs.getString(1);
            if (value.equalsIgnoreCase(temp) ){
            rtn = true ;
            if(this.isSQLdebug() ){plugin.getLogger().info("checkExistence: value = "+temp);}
            } 
        } catch (SQLException ex){
            if(this.isSQLdebug() ){plugin.getLogger().info( "checkExistence: Value not found: "+table+"."+col+"="+value ); }
            rtn = false;
        } finally {
            try { db_close();
            } catch (SQLException ex) {
                plugin.getLogger().warning( "checkExistence: "+ex.getMessage() ); 
                rtn = false;
            } finally{}
        }
        return rtn;
    }
    
    /**
     * Get an ArrayList of strings for a whole column 
     * @param table     the table to be used
     * @param column    the column name for the values to return
     * @return          all the results for the specified column, in an array list
     */
    public ArrayList<String> getSingleCol (String table, String column ){
        ArrayList<String> rtn = new ArrayList<String>();
        String SQL = "SELECT "+column+" FROM "+plugin.getDB_prefix()+table+" ORDER BY "+column+";";
        try {
            
            db_open();
            if(plugin.isSQLdebug() ){plugin.getLogger().info(SQL);}
            rs = stmt.executeQuery(SQL); 
            
            while ( rs.next() ){
               String temp = rs.getString(column);
               rtn.add( temp );
               if (plugin.isSQLdebug()) {plugin.getLogger().info("getSingleCol getting: "+temp);}
           }
            
        } catch (SQLException ex){
            plugin.getLogger().severe( "getSingleCol: "+ex.getMessage() ); 
            rtn = null;
        } finally {
            try { db_close();
            } catch (SQLException ex) {
                plugin.getLogger().warning( "checkExistence: "+ex.getMessage() ); 
                rtn = null;
            } finally{}
        }
        return rtn;
    }
    
    /**
     * Gets a list of all the citizens in the specified town
     * @param townName
     * @return      array list of all the citizens in the town
     */
    public ArrayList<String> getTownCits ( String townName ){
        ArrayList<String> rtn = new ArrayList<String>();
        String SQL = "SELECT playerName FROM "+plugin.getDB_prefix()+"citizens WHERE townName='"+townName+"' ORDER BY playerName DESC;";
        try {
            db_open();
            if(plugin.isSQLdebug() ){plugin.getLogger().info(SQL);}
            rs = stmt.executeQuery(SQL); 
            
            while ( rs.next() ){
               String temp = rs.getString( "playerName" );
               rtn.add( temp );
               if (plugin.isSQLdebug()) { plugin.getLogger().info("getSingleCol getting: "+temp); }
           }
            
        } catch (SQLException ex){
            plugin.getLogger().severe( "getSingleCol: "+ex.getMessage() ); 
            rtn = null;
        } finally {
            try { db_close();
            } catch (SQLException ex) {
                plugin.getLogger().warning( "checkExistence: "+ex.getMessage() ); 
                rtn = null;
            } finally{}
        }
        return rtn;
    }
    /**
     * Gets a list of all the citizens in the specified town in the specified role
     * @param townName
     * @return      array list of all the citizens in the town
     */
    public ArrayList<String> getTownCits ( String townName, String role ){
        ArrayList<String> rtn = new ArrayList<String>();
        String srch = "";
        if (role.equalsIgnoreCase( "mayor" ) ) {
            srch = "mayor";
        } else if (role.equalsIgnoreCase( "deputy" ) ) {
            srch = "deputy";
        } else if (role.equalsIgnoreCase( "citizen" ) ) {
            srch = "citizen";
        } else if (role.equalsIgnoreCase( "invitee" ) ) {
            srch = "invitee";
        } else if (role.equalsIgnoreCase( "applicant" ) ) {
            srch = "applicant";
        } else {plugin.getLogger().warning("getTownCits: failure to specify role"); }
        
        String SQL = "SELECT playerName FROM "+plugin.getDB_prefix()+
                "citizens WHERE townName='"+townName+"' AND role='"+srch+"' "+
                "ORDER BY playerName DESC;";
        try {
            db_open();
            if(plugin.isSQLdebug() ){plugin.getLogger().warning(SQL);}
            rs = stmt.executeQuery(SQL); 
            
            while ( rs.next() ){
               String temp = rs.getString( "playerName" );
               rtn.add( temp );
               if (plugin.isSQLdebug()) { plugin.getLogger().info("getTownCits getting: "+temp); }
           }
            
        } catch (SQLException ex){
            plugin.getLogger().severe( "getTownCits: "+ex.getMessage() ); 
            rtn = null;
        } finally {
            try { db_close();
            } catch (SQLException ex) {
                plugin.getLogger().warning( "getTownCits: "+ex.getMessage() ); 
                rtn = null;
            } finally{}
        }
        return rtn;
    }
    /**
     * Gets a list of all the citizens in the specified town in the specified role
     * @param townName
     * @return      array list of all the citizens in the town
     */
    public boolean updateRole ( Town t, String cit ){
        boolean rtn = false;
        
        String SQL = "UPDATE "+plugin.getDB_prefix()+
                "citizens SET role='"+t.getRole(cit)+
                "' WHERE playername='"+cit +"';";
        try {
            db_open();
            if(plugin.isSQLdebug() ){plugin.getLogger().warning(SQL);}
            int resultCount = stmt.executeUpdate(SQL); 
            if (resultCount == 1){
                return true;
            } 
            return false;
            
        } catch (SQLException ex){
            plugin.getLogger().severe( "getTownCits: "+ex.getMessage() ); 
            rtn = false;
        } finally {
            try { db_close();
            } catch (SQLException ex) {
                plugin.getLogger().warning( "getTownCits: "+ex.getMessage() ); 
                rtn = false;
            } finally{}
        }
        return rtn;
    }
    /**
     * Gets a list of all the citizens in the specified town in the specified role
     * @param townName
     * @return      array list of all the citizens in the town
     */
    public boolean deleteCitizen ( String cit ){
        boolean rtn = false;
        
        String SQL = "DELETE FROM  "+plugin.getDB_prefix()+
                "citizens WHERE playername='"+cit +"';";
        try {
            db_open();
            if(plugin.isSQLdebug() ){plugin.getLogger().warning(SQL);}
            int resultCount = stmt.executeUpdate(SQL); 
            if (resultCount == 1){
                return true;
            } 
            return false;
            
        } catch (SQLException ex){
            plugin.getLogger().severe( "getTownCits: "+ex.getMessage() ); 
            rtn = false;
        } finally {
            try { db_close();
            } catch (SQLException ex) {
                plugin.getLogger().warning( "getTownCits: "+ex.getMessage() ); 
                rtn = false;
            } finally{}
        }
        return rtn;
    }
    /**
     * Get all the town data from only the town name 
     * @param townName
     * @return          copy of the specified town
     */
    public Town getTown(String townName){
        Town temp = new Town (plugin) ;
        String SQL = "SELECT "+temp.toDB_Cols()+" FROM "+plugin.getDB_prefix()+"towns WHERE townName='"+townName+"';";
        try {
            db_open();
            if (plugin.isSQLdebug() ){plugin.getLogger().info(SQL); }
            rs = stmt.executeQuery(SQL);
            temp = new Town(plugin,rs.getString("townName"),rs.getString("mayor"),
                    rs.getInt("townRank"),rs.getDouble("bankBal"),rs.getDouble("taxRate") );
        } catch (SQLException ex){
            plugin.getLogger().info ( "getTown: "+ townName+" not found in database" );
        } finally {
            try { db_close();
            } catch (SQLException ex) {
                plugin.getLogger().warning( "getTown: "+ ex.getMessage() );
            } finally{}
        }
        return temp;
    }
    
    /**
     * Gets a citizen from the playerName
     * @param playerName
     * @return              copy of the specified citizen 
     */
    public Citizen getCitizen(String playerName){
        Citizen temp = new Citizen (plugin);
        String SQL = "SELECT "+temp.toDB_Cols() +" FROM "+plugin.getDB_prefix()+"citizens WHERE playerName='"+playerName+"';";
        try {
            db_open();
            rs = stmt.executeQuery(SQL);
            if (plugin.isSQLdebug() ){plugin.getLogger().info(SQL); }
            temp = new Citizen(plugin, rs.getString("townName"), rs.getString("playerName"),
                    rs.getString("role"), rs.getString("invitedBy") ); //,rs.getDate("sentDate") ); also missing lastLogin
        } catch (SQLException ex){
            plugin.getLogger().info( "getCitzien: "+playerName+" not found in database" );
        } finally {
            try { db_close();
            } catch (SQLException ex) {
                plugin.getLogger().warning( "getCitzien: "+ ex.getMessage() );
            } finally{}
        }
        return temp;
    }
    
    /**
     * Insert a single row into the database
     * @param table
     * @param cols
     * @param values
     * @return          true if worked properly
     */
    public boolean insert(String table, String cols, String values) {
        boolean rtn = true;
        String SQL = "INSERT INTO "+plugin.getDB_prefix()+table+" ("+cols+
                ") VALUES ("+values+");";
            
        try {
            db_open();
            if(plugin.isSQLdebug() ){plugin.getLogger().info(SQL);}
            stmt.executeUpdate(SQL); 
        } catch (SQLException ex){
            plugin.getLogger().severe( "dbInsert: "+ex.getMessage() ); 
            rtn = false;
        } finally {
            try { db_close();
            } catch (SQLException ex) {
                plugin.getLogger().warning( "dbInsert: "+ex.getMessage() ); 
                rtn = false;
            } finally{}
        }
        return rtn;
    }
    
    /**
     * Updates a single row for a single key
     * @param table
     * @param key_col
     * @param key
     * @param col
     * @param value
     * @return 
     */
    public boolean update(String table, String key_col, String key, String col, String value) {
        
        boolean rtn = true;
        String SQL = "UPDATE "+plugin.getDB_prefix()+table+" SET "+col+"="+value+" WHERE "
                +key_col+"='"+key+"';";
        try {
            db_open();
            if(plugin.isSQLdebug() ){plugin.getLogger().info(SQL);}
            stmt.executeUpdate(SQL); 
        } catch (SQLException ex){
            plugin.getLogger().severe("db_update: "+ ex.getMessage() ); 
            rtn = false;
        } finally {
            try { db_close();
            } catch (SQLException ex) {
                plugin.getLogger().warning("db_update: "+ ex.getMessage() ); 
                rtn = false;
            } 
        }
        return rtn;
    }
    
    /**
     * Updates a row, requires special colsANDvals in SQL format
     * @param table
     * @param key_col
     * @param key
     * @param colsANDvals
     * @return 
     */
    public boolean updateRow(String table, String key_col, String key, String colsANDvals) {
        
        boolean rtn = true;
        String SQL = "UPDATE "+plugin.getDB_prefix()+table+" SET "+colsANDvals+" WHERE "
                +key_col+"='"+key+"';";
        try {
            db_open();
            if(plugin.isSQLdebug() ){plugin.getLogger().info(SQL);}
            stmt.executeUpdate(SQL); 
        } catch (SQLException ex){
            plugin.getLogger().severe("db_updateRow: "+ ex.getMessage() ); 
            rtn = false;
        } finally {
            try { db_close();
            } catch (SQLException ex) {
                plugin.getLogger().warning("db_updateRow: "+ ex.getMessage() ); 
                rtn = false;
            } 
        }
        return rtn;
    }

    /**
     * Creates the database specifically for Muni 
     * @param drops true means the tables will be dropped before creating them again
     * @return      false if there was a problem
     */
    public boolean createDB (boolean drops) { 
        boolean rtn = false;
        String serial;
        String mpk = "";
        String spk = "";
        if (plugin.useMysql() ){
            serial = "AUTO_INCREMENT " ;
            mpk = ", Primary Key (id) " ;
        } else{
            serial = "AUTOINCREMENT " ;
            spk = " Primary Key ";
        }
        String prefix = plugin.getDB_prefix();
        String DROP1 = "DROP TABLE IF EXISTS "+prefix+"towns;";
        String DROP2 = "DROP TABLE IF EXISTS "+prefix+"citizens;";
        String DROP3 = "DROP TABLE IF EXISTS "+prefix+"transactions;";
        String DROP4 = "DROP TABLE IF EXISTS "+prefix+"votes;";
        String SQL0 = "CREATE DATABASE IF NOT EXISTS minecraft;";
        String SQL00= "GRANT ALL PRIVILEGES ON minecraft.* TO user@host BY 'password';";
        String SQL1 = "CREATE TABLE IF NOT EXISTS "+prefix+"towns ( " + 
            "id INTEGER "+ spk + serial +", " + 
            "townName VARCHAR(30) UNIQUE, mayor VARCHAR(16), townRank INTEGER, " + 
            "bankBal DOUBLE, taxRate DOUBLE, tcX INTEGER, tcY INTEGER, "+
            "tcZ INTEGER "+ mpk + ");";
        String SQL2 = "CREATE TABLE IF NOT EXISTS "+prefix+"citizens ( " + 
            "id INTEGER "+ spk + serial +", " + 
            "playerName VARCHAR(16) UNIQUE, " +"townName VARCHAR(25), " +
            "role VARCHAR(10), invitedBy VARCHAR(16), "+
            "sentDate DATETIME, lastLogin DATETIME "+ mpk +");";
        String SQL3 = "CREATE TABLE IF NOT EXISTS "+prefix+"transactions ( "  + 
            "id INTEGER "+ spk + serial +", " + 
            "playerName VARCHAR(16), " +
            "townName VARCHAR(30), timestamp DATETIME,  " +
            "type VARCHAR(30), amount DOUBLE, item_amount INTEGER, " +
            "notes VARCHAR(350) "+ mpk +");";
        String SQL4 = "CREATE TABLE IF NOT EXISTS "+prefix+"votes ( "  + 
            "id INTEGER "+ spk + serial +", " + 
            "playerName VARCHAR(16), townName VARCHAR(30), timestamp DATETIME,  " +
            "issue VARCHAR(30), answer BOOLEAN "+ mpk +");";
        try {
            db_open();
            if (drops){ 
                plugin.getLogger().warning("Dropping all tables");
                stmt.executeUpdate(DROP1);
                stmt.executeUpdate(DROP2);
                stmt.executeUpdate(DROP3);
                stmt.executeUpdate(DROP4);
            } // could do an else: check existence here
            if (plugin.useMysql() ){ 
                stmt.executeUpdate(SQL0); 
                stmt.executeUpdate(SQL00); 
                if(plugin.isSQLdebug()){plugin.getLogger().info("Made the DB (mysql)");}
            }
            if(plugin.isSQLdebug()){plugin.getLogger().info("Making towns table if doesn't exist. ");}
            stmt.executeUpdate(SQL1);
            if(plugin.isSQLdebug()){plugin.getLogger().info("Making citizens table if doesn't exist. ");}
            stmt.executeUpdate(SQL2);
            if(plugin.isSQLdebug()){plugin.getLogger().info("Making transactions table if doesn't exist. ");}
            stmt.executeUpdate(SQL3);
            rtn = true;
            if(plugin.isSQLdebug()){plugin.getLogger().info("Making voting table if doesn't exist. ");}
            stmt.executeUpdate(SQL4);
            rtn = true;
            
        } catch (SQLException ex){
            plugin.getLogger().severe( "createDB: "+ex.getMessage() );
            rtn = false;
        } finally {
            try { db_close();
            } catch (SQLException ex) {
                plugin.getLogger().warning( "createDB: "+ex.getMessage() );
                rtn = false;
            } finally{}
        }
        return rtn;
    }
}
