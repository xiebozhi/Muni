package com.teamglokk.muni;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Set;
import java.util.List;
import java.util.Arrays;

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
    
    public boolean checkExistence ( String table, String pk, String value ){
        boolean rtn = true;
        String SQL = "SELECT "+pk+" FROM "+plugin.db_prefix+table+
                    " WHERE "+pk+"='"+value +"';";
        if(plugin.isDebug() ){plugin.getLogger().info(SQL);}
        try {
            
            db_open();
            rs = stmt.executeQuery(SQL); 
            if (value.equalsIgnoreCase(rs.getString(1) ) ){
            rtn = true ;
            } else {return false;}
        } catch (SQLException ex){
            plugin.getLogger().severe( "checkExistence: "+ex.getMessage() ); 
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
    public List<String> getTowns (){
        List<String> rtn = null;
        String SQL = "SELECT townName FROM "+plugin.db_prefix+"towns;";
        if(plugin.isDebug() ){plugin.getLogger().info(SQL);}
        try {
            
            db_open();
            rs = stmt.executeQuery(SQL); 
            
           while ( rs.next() ){
               rtn.add( rs.getString("townName") );
           }
            
        } catch (SQLException ex){
            plugin.getLogger().severe( "checkExistence: "+ex.getMessage() ); 
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
    public void db_open() throws SQLException {
        if(plugin.isDebug()){plugin.getLogger().info("Starting DB");}
        conn = DriverManager.getConnection(plugin.db_URL,plugin.db_user,plugin.db_pass);
        stmt = conn.createStatement();
    }
    public void db_close() throws SQLException {
        if(plugin.isDebug()){plugin.getLogger().info("Closing DB");}
        if ( conn != null ) {
            conn.close();
        }
    }
    public boolean db_insert(String table, String cols, String values) {
        boolean rtn = true;
        String SQL = "INSERT INTO "+plugin.db_prefix+table+" ("+cols+
                ") VALUES ("+values+");";
            if(plugin.isDebug() ){plugin.getLogger().info(SQL);}
        try {
            db_open();
            stmt.executeUpdate(SQL); //not sure if need ; in SQL
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
    public boolean db_update(String table, String key_col, String key, String col, String value) {
        
        boolean rtn = true;
        try {
            db_open();
            rs = stmt.executeQuery("UPDATE "+plugin.db_prefix+table+" SET "+col+"="+value+" WHERE "
                +key_col+"="+key); //not sure if need ; in SQL
        } catch (SQLException ex){
            plugin.getLogger().severe( ex.getMessage() ); 
            rtn = false;
        } finally {
            try { db_close();
            } catch (SQLException ex) {
                plugin.getLogger().warning( ex.getMessage() ); 
                rtn = false;
            } finally{}
        }
        return rtn;
    }
    public ResultSet runSQL (String SQL) throws SQLException { 
        ResultSet rtn = null;
        
        try {
            db_open();
            rs = stmt.executeQuery(SQL);
          rtn = rs;
        } catch (SQLException ex){
            plugin.getLogger().severe( ex.getMessage() );
        } finally {
            try { db_close();
            } catch (SQLException ex) {
                plugin.getLogger().warning( ex.getMessage() );
            } finally{}
        }
        return rtn;
    }
    public boolean createDB (boolean drops) { 
        boolean rtn = false;
        String prefix = plugin.db_prefix;
        String DROP1 = "DROP TABLE IF EXISTS "+prefix+"towns;";
        String DROP2 = "DROP TABLE IF EXISTS "+prefix+"citizens;";
        String DROP3 = "DROP TABLE IF EXISTS "+prefix+"transactions;";
        String SQL0 = "CREATE DATABASE IF NOT EXISTS minecraft;";
        String SQL00= "GRANT ALL PRIVILEGES ON minecraft.* TO user@host BY 'password';";
        String SQL1 = "CREATE TABLE IF NOT EXISTS "+prefix+"towns ( " + 
            "townName VARCHAR(25), " +  "townRank INTEGER, " + 
            "bankBal DOUBLE, " +  "taxRate DOUBLE, " + 
            "PRIMARY KEY (townName) ); "; 
        String SQL2 = "CREATE TABLE IF NOT EXISTS "+prefix+"citizens ( " +
            "playerName VARCHAR(16), " +"townName VARCHAR(25), " +
            "mayor BINARY, " + "citizen BINARY, " +
            "deputy BINARY, " + "applicant BINARY, " +
            "invitee BINARY, " + "PRIMARY KEY (playerName) ); " ;
        String SQL3 = "CREATE TABLE IF NOT EXISTS "+prefix+"transactions ( " +
            "id INT AUTO_INCREMENT, " + "playerName VARCHAR(16), " +
            "townName VARCHAR(25), " + "trans_date DATE,  " +
            "trans_time TIME, " + "trans_type VARCHAR(30), " +
            "trans_amount DOUBLE, " + "notes VARCHAR(350), " +
            "PRIMARY KEY (id) ); " ; 
        try {
            db_open();
            if (drops){ 
                if(plugin.isDebug()){plugin.getLogger().info("DroppingDB");}
                stmt.executeUpdate(DROP1); 
                stmt.executeUpdate(DROP2);
                stmt.executeUpdate(DROP3);
            }
            if (plugin.useMYSQL){ 
                stmt.executeUpdate(SQL0); 
                stmt.executeUpdate(SQL00); 
                if(plugin.isDebug()){plugin.getLogger().info("Made the DB (mysql)");}
            }
            if(plugin.isDebug()){plugin.getLogger().info("Making towns table");}
            stmt.executeUpdate(SQL1);
            if(plugin.isDebug()){plugin.getLogger().info("Making citizens table");}
            stmt.executeUpdate(SQL2);
            if(plugin.isDebug()){plugin.getLogger().info("Making transactions table");}
            stmt.executeUpdate(SQL3);
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
    }
    public  void SQLLite(){
        // load the sqlite-JDBC driver using the current class loader
        
        try {
        Class.forName("org.sqlite.JDBC");
        }  catch (ClassNotFoundException e)
        { plugin.getLogger().info("JDBC class not found");}

        
        Connection connection = null;
        try
        {
          // create a database connection
          connection = DriverManager.getConnection("jdbc:sqlite:sample.db");
          Statement statement = connection.createStatement();
          statement.setQueryTimeout(30);  // set timeout to 30 sec.

          statement.executeUpdate("drop table if exists person");
          statement.executeUpdate("create table person (id integer, name string)");
          statement.executeUpdate("insert into person values(1, 'leo')");
          statement.executeUpdate("insert into person values(2, 'yui')");
          ResultSet rs = statement.executeQuery("select * from person");
          while(rs.next())
          {
            // read the result set
            System.out.println("name = " + rs.getString("name"));
            System.out.println("id = " + rs.getInt("id"));
          }
        }
        catch(SQLException ex)
        {
          // if the error message is "out of memory", 
          // it probably means no database file is found
            plugin.getLogger().warning( ex.getMessage() );
        }
        finally
        {
          try
          {
            if(connection != null){
              connection.close();
            }
          }
          catch(SQLException ex)
          {
            // connection close failed.
            plugin.getLogger().warning( ex.getMessage() );
          }
        }
    }
}
