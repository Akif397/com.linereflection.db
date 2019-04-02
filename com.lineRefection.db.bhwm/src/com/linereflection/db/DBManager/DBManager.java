package com.lineReflection.db.DBManager;


import com.lineReflection.db.DBModel.PostDetails;
import com.lineReflection.db.DBModel.User;
import java.sql.Connection;
//import com.mysql.jdbc.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DBManager {

    private static final Log LOGGER = LogFactory.getLog(DBManager.class);
    private static Connection connection = null;

    private User user = new User();
    private PostDetails postDetails = new PostDetails();

    private static DBManager dbManager = null;

//    private DBConnection dBConnection = new DBConnection();
//    private Connection connection = dBConnection.getConnection();

    public static enum TABLE {

        TABLE_BHW_USER("bhwUser"),
        TABLE_BHW_POST("posttable");

        private String tableName = "";

        TABLE(String tableName) {
            this.tableName = tableName;  
        }

        public String toValue() {
            return this.tableName;
        }

        public String toString() {
            return String.valueOf(this.tableName);
        }
    }

    private DBManager() {

    }

    public static DBManager getDBManager() {
        if (dbManager == null) {
            dbManager = new DBManager();
        }
        return dbManager;
    }

    public void closeConnection() throws SQLException {
        getConDBConnection().close();
    }

    public void resetConnection() throws SQLException {
        this.closeConnection();
        connection = null;
    }

    public Connection getConDBConnection() throws SQLException {

        if (connection != null) {
            return connection; 
        }

        String PUBLIC_DNS = "nexcommdb.cfjr04dcs1uw.us-east-1.rds.amazonaws.com";
        String DATABASE = "BHW";
        String REMOTE_DATABASE_USERNAME = "nexcommdbuser";
        String DATABASE_USER_PASSWORD = "N3xK0MdB2Ol8POOP";
        String PORT = "3306";

        try {
            connection = (Connection) DriverManager.
                    getConnection("jdbc:mysql://" + PUBLIC_DNS + ":" + PORT + "/" + DATABASE, REMOTE_DATABASE_USERNAME, DATABASE_USER_PASSWORD);
        } catch (SQLException ex) {
            throw ex;
        }
        return connection;
    }

    public User getLoggedInUser() {
        return user ;
    }

    
    
    public PostDetails getPostDetails(){
        return postDetails;
    }

    public boolean login(String email, String password) {
        boolean checkForUser = false;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            ps = DBManager.getDBManager().getConDBConnection().prepareStatement("select * from " + TABLE.TABLE_BHW_USER + " where email = ?");
            ps.setString(1, email);
            rs = ps.executeQuery();
            
            if (rs.equals("null")) {
                checkForUser = false;
                return checkForUser;
            } else {
                while (rs.next()) {
                    user.setEmail(rs.getString(2));
                    if (rs.getString(3).equals(password)) {
                        user.setPassword(rs.getString(3));
                        checkForUser = true;
                        return checkForUser;
                    } else {
                        checkForUser = false;
                        return checkForUser;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return checkForUser;
    }
}
