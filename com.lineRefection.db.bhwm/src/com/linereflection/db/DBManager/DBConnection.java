/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.linereflection.db.DBManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author PC3
 */
public class DBConnection {
    Connection connection = null;
    
    public Connection getConnection()  {

        
        if (connection == null) {
            try {
//                Class.forName("com.mysql.jdbc.Driver");
//                connection = DriverManager.getConnection("jdbc:mysql://localhost/db.bhw", "root", "");
 
                  connection = DriverManager.getConnection("jdbc:mysql://nexcommdb.cfjr04dcs1uw.us-east-1.rds.amazonaws.com/BHW", "nexcommdbuser", "N3xK0MdB2Ol8POOP");
            } catch (SQLException ex) {

                JOptionPane.showMessageDialog(null, "Error!!!" + ex);
            }

        }

        return connection;

    }
    
    public void closeConncetion(){
        if(connection != null){
            try {
                connection.close();
            } catch (SQLException ex) {
                Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
