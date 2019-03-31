package com.lineReflection.db.DBManager;


import com.lineReflection.db.DBModel.UserDBModel;
import com.linereflection.db.DBModel.PostDBModel;
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

    private UserDBModel userDBModel = new UserDBModel();
    private PostDBModel postDbModel = new PostDBModel();

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
//        try {
//            Class.forName("com.mysql.jdbc.Driver");
//        } catch (ClassNotFoundException e) {
//            LOGGER.fatal(e.getMessage());
//        }

        String PUBLIC_DNS = "nexcommdb.cfjr04dcs1uw.us-east-1.rds.amazonaws.com";
//        String DATABASE = "tinder_testing";
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

    public UserDBModel getLoggedInUser() {
        return userDBModel ;
    }

    public void setPostDetails(List list) {
        List<PostDBModel> postDBModelList = list ;
        for (PostDBModel postDbModels : postDBModelList) {

                System.out.println(postDbModel.getUrl());

//                postDbModel.setId(postDbModels.getId());
                postDbModel.setUrl(postDbModels.getUrl());
                postDbModel.setTitle(postDbModels.getTitle());
                postDbModel.setLikes(postDbModels.getLikes());
                postDbModel.setReplies(postDbModels.getReplies());
                postDbModel.setViews(postDbModels.getViews());
                postDbModel.setDiscussion(postDbModels.getDiscussion());
//                postDbModel.setDescription(postDbModels.getDescription());
                postDbModel.setUserId(postDbModels.getUserId());
                postDbModel.setTags(postDbModels.getTags());

                System.out.println(postDbModel.getUrl());
                System.out.println(postDbModels.getUrl());

            }
    }
    public void setLoggedInUser(List list){
        List<UserDBModel> userDBModelList =list;
        for (UserDBModel udbm : userDBModelList) {
            userDBModel.setId(udbm.getId());
            userDBModel.setEmail(udbm.getEmail());
            userDBModel.setPassword(udbm.getPassword());
        }

    }
    
    public PostDBModel getPostDetails(){
        return postDbModel;
    }

    public boolean checkForUserWhileLogin(String email, String password) {
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
                    userDBModel.setEmail(rs.getString(2));
                    if (rs.getString(3).equals(password)) {
                        userDBModel.setPassword(rs.getString(3));
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

    public void insertUserDB(List list) {
        
        List<UserDBModel> userDBModelList = list;
        setLoggedInUser(userDBModelList);
        PreparedStatement preparedStatement = null;
        
        try {

            preparedStatement = DBManager.getDBManager().getConDBConnection().prepareStatement("Insert Into " + TABLE.TABLE_BHW_USER + "(  `email`, `password`) VALUES ( ? , ?);");
            
            preparedStatement.setString(1, getLoggedInUser().getEmail());
            preparedStatement.setString(2, getLoggedInUser().getPassword());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException ex) {
                    Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    public void insertPostDB(List list) throws ClassNotFoundException {
        List<PostDBModel> postDbModelList = list;
        PostDBModel postDbModel = new PostDBModel();
        for (PostDBModel postDbModels : postDbModelList) {

//            postDbModel.setId(postDbModels.getId());
            postDbModel.setUrl(postDbModels.getUrl());
            postDbModel.setTitle(postDbModels.getTitle());
            postDbModel.setLikes(postDbModels.getLikes());
            postDbModel.setReplies(postDbModels.getReplies());
            postDbModel.setViews(postDbModels.getViews());
            postDbModel.setDiscussion(postDbModels.getDiscussion());
            
            postDbModel.setUserId(postDbModels.getUserId());
            postDbModel.setTags(postDbModels.getTags());

            System.out.println(postDbModel.getUrl());

        }
        try {
            PreparedStatement preparedStatement;
            preparedStatement = DBManager.getDBManager().getConDBConnection().prepareStatement("insert " + TABLE.TABLE_BHW_POST + " ( `url`, `title`, `likes`, `replies`, `views`, `discussion`, `tags` , `userid`  ) VALUES ( ? , ? , ? , ? , ? , ? , ? , ?  );");
//            preparedStatement.setInt(1, postDbModel.getId());
            preparedStatement.setString(1, postDbModel.getUrl());
            preparedStatement.setString(2, postDbModel.getTitle());
            preparedStatement.setString(3, postDbModel.getLikes());
            preparedStatement.setString(4, postDbModel.getReplies());
            preparedStatement.setString(5, postDbModel.getViews());
            preparedStatement.setString(6, postDbModel.getDiscussion());
            preparedStatement.setString(7, postDbModel.getTags());
            preparedStatement.setInt(8, postDbModel.getUserId());
            
            preparedStatement.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updatePostDB(List list) throws ClassNotFoundException {
        List<PostDBModel> postDbModelList = list;
        setPostDetails(postDbModelList);
//        PostDBModel postDbModel = new PostDBModel();
        PreparedStatement preparedStatement;
        PreparedStatement updatePreparedStatement;
        ResultSet resultSet = null;
        try {

            

            preparedStatement = DBManager.getDBManager().getConDBConnection().prepareStatement("SELECT * FROM " + TABLE.TABLE_BHW_POST + " WHERE `url`= ? ");
            preparedStatement.setString(1, getPostDetails().getUrl());
            resultSet = preparedStatement.executeQuery();
            if (!resultSet.first()) {
//                while(resultSet.next()){
//                    postDbModel.setId(resultSet.getInt(1));
//                    System.out.println(resultSet.getString(3));
//                    int i = resultSet.getInt(1);
//                    String s = resultSet.getString(2);
//                }
                insertPostDB(postDbModelList);
            } else {
                updatePreparedStatement = DBManager.getDBManager().getConDBConnection().prepareStatement("UPDATE " + TABLE.TABLE_BHW_POST + " SET `url`= ? ,`title`= ? ,`likes`= ? ,`replies`= ? ,`views`= ? ,`discussion`= ? , `tags` = ? WHERE `url` = ? ;");
                updatePreparedStatement.setString(1, getPostDetails().getUrl());
                updatePreparedStatement.setString(2, getPostDetails().getTitle());
                updatePreparedStatement.setString(3, getPostDetails().getLikes());
                updatePreparedStatement.setString(4, getPostDetails().getReplies());
                updatePreparedStatement.setString(5, getPostDetails().getViews());
                updatePreparedStatement.setString(6, getPostDetails().getDiscussion());
               
                updatePreparedStatement.setString(7, getPostDetails().getTags());
                updatePreparedStatement.setString(8, getPostDetails().getUrl());
                updatePreparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<UserDBModel> getUserDB() {
        List<UserDBModel> list = new LinkedList<>();

        try {
            PreparedStatement preparedStatement;
            preparedStatement = DBManager.getDBManager().getConDBConnection().prepareStatement("SELECT * FROM " +TABLE.TABLE_BHW_USER);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                userDBModel = new UserDBModel();
                userDBModel.setId(resultSet.getInt(1));
                userDBModel.setEmail(resultSet.getString(2));
                userDBModel.setPassword(resultSet.getString(3));
                list.add(userDBModel);
            }

        } catch (SQLException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public List<PostDBModel> getPostDB() {
        List<PostDBModel> list = new LinkedList<>();

        try {
            PreparedStatement preparedStatement;
            preparedStatement = DBManager.getDBManager().getConDBConnection().prepareStatement("SELECT * FROM " + TABLE.TABLE_BHW_POST);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                PostDBModel postDbModel = new PostDBModel();
                postDbModel.setId(resultSet.getInt(1));
                postDbModel.setUrl(resultSet.getString(2));
                postDbModel.setTitle(resultSet.getString(3));
                postDbModel.setLikes(resultSet.getString(4));
                postDbModel.setReplies(resultSet.getString(5));
                postDbModel.setViews(resultSet.getString(6));
                postDbModel.setDiscussion(resultSet.getString(7));
                postDbModel.setDescription(resultSet.getString(8));
                postDbModel.setUserId(resultSet.getInt(9));
                postDbModel.setTags(resultSet.getString(10));
                list.add(postDbModel);
            }

        } catch (SQLException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

}
