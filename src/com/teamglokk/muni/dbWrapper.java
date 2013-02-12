package com.teamglokk.muni;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;

//import org.sqlite.JDBC;
//import com.mysql.jdbc.Driver;

//import java.util.logging.Level;
//import java.util.logging.Logger;


//import com.teamglokk.muni;
/**
 * Wraps the database functions to be easier to work with
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
    public void db_open() throws SQLException {
        if(plugin.isDebug()){
            String temp = plugin.useMYSQL ? "Opening DB (mysql)":"Opening DB (sqlite)" ;
            plugin.getLogger().info(temp);
        }
        if (plugin.useMYSQL){  //Using MySQL
            try {
            Class.forName("org.mysql.jdbc.Driver");
            } catch (ClassNotFoundException ex){
                plugin.getLogger().severe("db_open (db driver not found): "+ ex.getMessage() );
            }
            conn = DriverManager.getConnection(plugin.db_URL,plugin.db_user,plugin.db_pass);
            // MySQL is not yet tested!!! 31 Jan 2013 RJS
        } else { //Using SQLite
            try {
            Class.forName("org.sqlite.JDBC");
            } catch (ClassNotFoundException ex){
                plugin.getLogger().severe("db_open (db driver not found): "+ ex.getMessage() );
            }
            conn = DriverManager.getConnection(plugin.db_URL);
        }
        stmt = conn.createStatement();
    }
    public void db_close() throws SQLException {
        if(plugin.isDebug()){plugin.getLogger().info("Closing DB");}
        if ( rs != null) { rs.close(); }
        if ( stmt != null) { stmt.close(); }
        if ( conn != null ) { conn.close();}
    }
    public boolean checkExistence ( String table, String pk, String value ){
        boolean rtn = false;
        String SQL = "SELECT "+pk+" FROM "+plugin.db_prefix+table+
                    " WHERE "+pk+"='"+value +"';";
        try {
            
            db_open();
            if(plugin.isDebug() ){plugin.getLogger().info(SQL);}
            rs = stmt.executeQuery(SQL); 
            //rs.next();
            String temp = rs.getString(1);
            if (value.equalsIgnoreCase(temp) ){
            rtn = true ;
            if(this.isDebug() ){plugin.getLogger().info("checkExistence: value = "+temp);}
            } 
        } catch (SQLException ex){
            plugin.getLogger().info( "checkExistence: Value not found: "+table+"."+pk+"="+value ); 
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
    public ArrayList<String> getSingleCol (String table, String column ){
        ArrayList<String> rtn = new ArrayList<String>();
        String SQL = "SELECT "+column+" FROM "+plugin.db_prefix+table+" ORDER BY "+column+";";
        try {
            
            db_open();
            if(plugin.isDebug() ){plugin.getLogger().info(SQL);}
            rs = stmt.executeQuery(SQL); 
            
            while ( rs.next() ){
               String temp = rs.getString(column);
               rtn.add( temp );
               if (plugin.isDebug()) {plugin.getLogger().info("getSingleCol getting: "+temp);}
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
    public ArrayList<String> getTownCits ( String townName ){
        ArrayList<String> rtn = new ArrayList<String>();
        String SQL = "SELECT playerName FROM "+plugin.db_prefix+"citizens WHERE townName='"+townName+"' ORDER BY playerName DESC;";
        try {
            db_open();
            if(plugin.isDebug() ){plugin.getLogger().info(SQL);}
            rs = stmt.executeQuery(SQL); 
            
            while ( rs.next() ){
               String temp = rs.getString( "playerName" );
               rtn.add( temp );
               if (plugin.isDebug()) { plugin.getLogger().info("getSingleCol getting: "+temp); }
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
    public Town getTown(String townName){
        Town temp = new Town (plugin) ;
        String SQL = "SELECT "+temp.toDB_Cols()+" FROM "+plugin.db_prefix+"towns WHERE townName='"+townName+"';";
        try {
            db_open();
            if (plugin.isDebug() ){plugin.getLogger().info(SQL); }
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
    public Citizen getCitizen(String playerName){
        Citizen temp = new Citizen (plugin);
        String SQL = "SELECT "+temp.toDB_Cols() +" FROM "+plugin.db_prefix+"citizens WHERE playerName='"+playerName+"';";
        try {
            db_open();
            rs = stmt.executeQuery(SQL);
            if (plugin.isDebug() ){plugin.getLogger().info(SQL); }
            temp = new Citizen(plugin, rs.getString("townName"), rs.getString("playerName"),
                    rs.getBoolean("mayor"), rs.getBoolean("deputy"), rs.getBoolean("applicant"),
                    rs.getBoolean("invitee"),rs.getString("invitedBy") ); //,rs.getDate("sentDate") ); also missing lastLogin
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
    public boolean insert(String table, String cols, String values) {
        boolean rtn = true;
        String SQL = "INSERT INTO "+plugin.db_prefix+table+" ("+cols+
                ") VALUES ("+values+");";
            
        try {
            db_open();
            if(plugin.isDebug() ){plugin.getLogger().info(SQL);}
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
     * 
     * @author bobbshields
     * @param table test test
     * @return 
     */
    public boolean update(String table, String key_col, String key, String col, String value) {
        
        boolean rtn = true;
        String SQL = "UPDATE "+plugin.db_prefix+table+" SET "+col+"="+value+" WHERE "
                +key_col+"='"+key+"';";
        try {
            db_open();
            if(plugin.isDebug() ){plugin.getLogger().info(SQL);}
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
        public boolean updateRow(String table, String key_col, String key, String colsANDvals) {
        
        boolean rtn = true;
        String SQL = "UPDATE "+plugin.db_prefix+table+" SET "+colsANDvals+" WHERE "
                +key_col+"='"+key+"';";
        try {
            db_open();
            if(plugin.isDebug() ){plugin.getLogger().info(SQL);}
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

    public boolean createDB (boolean drops) { 
        boolean rtn = false;
        String serial;
        String mpk = "";
        String spk = "";
        if (plugin.useMYSQL){
            serial = "AUTO_INCREMENT " ;
            mpk = ", Primary Key (id) " ;
        } else{
            serial = "AUTOINCREMENT " ;
            spk = "Primary Key ";
        }
        String prefix = plugin.db_prefix;
        String DROP1 = "DROP TABLE IF EXISTS "+prefix+"towns;";
        String DROP2 = "DROP TABLE IF EXISTS "+prefix+"citizens;";
        String DROP3 = "DROP TABLE IF EXISTS "+prefix+"transactions;";
        String SQL0 = "CREATE DATABASE IF NOT EXISTS minecraft;";
        String SQL00= "GRANT ALL PRIVILEGES ON minecraft.* TO user@host BY 'password';";
        String SQL1 = "CREATE TABLE IF NOT EXISTS "+prefix+"towns ( " + 
            "id INTEGER "+ spk + serial +", " + 
            "townName VARCHAR(30) UNIQUE, mayor VARCHAR(16), townRank INTEGER, " + 
            "bankBal DOUBLE, taxRate DOUBLE "+ mpk + ");";
            //, PRIMARY KEY (townName) ); "; 
        String SQL2 = "CREATE TABLE IF NOT EXISTS "+prefix+"citizens ( " + 
            "id INTEGER "+ spk + serial +", " + 
            "playerName VARCHAR(16) UNIQUE, " +"townName VARCHAR(25), " +
            "mayor BINARY, deputy BINARY, applicant BINARY, " +
            "invitee BINARY, invitedBy VARCHAR(16), "+
            "sentDate DATETIME, lastLogin DATETIME "+ mpk +");";
                //, PRIMARY KEY (playerName) ); " ;
        String SQL3 = "CREATE TABLE IF NOT EXISTS "+prefix+"transactions ( "  + 
            "id INTEGER "+ spk + serial +", " + 
            "playerName VARCHAR(16), " +
            "townName VARCHAR(30), timestamp DATETIME,  " +
            "type VARCHAR(30), amount DOUBLE, item_amount INTEGER, " +
            "notes VARCHAR(350) "+ mpk +");";
            //, PRIMARY KEY (id) ); " ; 
        try {
            db_open();
            if (drops){ 
                plugin.getLogger().warning("Dropping all tables");
                stmt.executeUpdate(DROP1);
                stmt.executeUpdate(DROP2);
                stmt.executeUpdate(DROP3);
            } // could do an else: check existence here
            if (plugin.useMYSQL){ 
                stmt.executeUpdate(SQL0); 
                stmt.executeUpdate(SQL00); 
                if(plugin.isDebug()){plugin.getLogger().info("Made the DB (mysql)");}
            }
            if(plugin.isDebug()){plugin.getLogger().info("Making towns table if doesn't exist. ");}
            stmt.executeUpdate(SQL1);
            //if (plugin.isDebug() ) {this.getLogger().info(stmt.getWarnings().toString() ); }
            if(plugin.isDebug()){plugin.getLogger().info("Making citizens table if doesn't exist. ");}
            stmt.executeUpdate(SQL2);
            //if (plugin.isDebug() ) {this.getLogger().info(stmt.getWarnings().toString() ); }
            if(plugin.isDebug()){plugin.getLogger().info("Making transactions table if doesn't exist. ");}
            stmt.executeUpdate(SQL3);
            //if (plugin.isDebug() ) {this.getLogger().info(stmt.getWarnings().toString() ); }
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
    
    /*
    public void MySQL(String[] args) {

        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        String url = "jdbc:mysql://localhost:3306/testdb";
        String user = "testuser";
        String password = "test623";

        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            rs = st.executeQuery("SELECT VERSION()");

            if (rs.next()) {
                System.out.println(rs.getString(1));
            }

        } catch (SQLException ex) {
                plugin.getLogger().severe( ex.getMessage() );

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                plugin.getLogger().warning( ex.getMessage() );
            }
        }
    } */
}
