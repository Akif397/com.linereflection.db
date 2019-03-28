package com.lineReflection.db.DBManager;

import com.lineReflection.db.DBModel.PostDbModel;
import com.lineReflection.db.DBModel.UserDBModel;
import com.linereflection.db.DBManager.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DBManager {

    UserDBModel userDBModel ;
    
    private static DBManager INSTANCE = null;
    
    private DBConnection dBConnection = new DBConnection();
    private Connection connection = dBConnection.getConnection();
    
    public static DBManager getInstance(){
        if(INSTANCE == null){
            INSTANCE = new DBManager();
           
        }
        return INSTANCE;
    }
    

    public  void insertUserDB(List list) {
        userDBModel = new UserDBModel();
        List<UserDBModel> userDBModelList = list;
        PreparedStatement preparedStatement = null;
        for(UserDBModel udbm : userDBModelList){
            userDBModel.setId(udbm.getId());
            userDBModel.setEmail(udbm.getEmail());
            userDBModel.setPassword(udbm.getPassword());
        }
        
        try {
            
            preparedStatement = this.connection.prepareStatement("INSERT INTO `usertableinformation`( `id`, `email`, `password`) VALUES ( ? , ? , ?);");
            preparedStatement.setInt(1, userDBModel.getId());
            preparedStatement.setString(2, userDBModel.getEmail());
            preparedStatement.setString(3, userDBModel.getPassword());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            if(preparedStatement != null){
                try {
                    preparedStatement.close();
                } catch (SQLException ex) {
                    Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
    }
    
    public void insertPostDB(List list) throws ClassNotFoundException {
        List<PostDbModel> postDbModelList = list;
        PostDbModel postDbModel = new PostDbModel();
        for(PostDbModel postDbModels: postDbModelList){
                
                postDbModel.setId(postDbModels.getId());
                postDbModel.setUrl(postDbModels.getUrl());
                postDbModel.setTitle(postDbModels.getTitle());
                postDbModel.setLikes(postDbModels.getLikes());
                postDbModel.setReplies(postDbModels.getReplies());
                postDbModel.setViews(postDbModels.getViews());
                postDbModel.setDiscussion(postDbModels.getDiscussion());
                postDbModel.setDescription(postDbModels.getDescription());
                postDbModel.setUserId(postDbModels.getUserId());
                postDbModel.setTags(postDbModels.getTags());
                
                System.out.println(postDbModel.getUrl());
                
                
            }
        try {
            PreparedStatement preparedStatement;
            preparedStatement = this.connection.prepareStatement("INSERT INTO `posttable`(`id`, `url`, `title`, `likes`, `replies`, `views`, `discussion`, `description`, `userid` , `tags` ) VALUES (? , ? , ? , ? , ? , ? , ? , ? , ? , ? );");
            preparedStatement.setInt(1, postDbModel.getId());
            preparedStatement.setString(2, postDbModel.getUrl());
            preparedStatement.setString(3, postDbModel.getTitle());
            preparedStatement.setString(4, postDbModel.getLikes());
            preparedStatement.setString(5, postDbModel.getReplies());
            preparedStatement.setString(6, postDbModel.getViews());
            preparedStatement.setString(7, postDbModel.getDiscussion());
            preparedStatement.setString(8, postDbModel.getDescription());
            preparedStatement.setInt(9, postDbModel.getUserId());
            preparedStatement.setString(10, postDbModel.getTags());
            preparedStatement.executeUpdate();
            
        } catch (SQLException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void updatePostDB(List list) throws ClassNotFoundException {
        List<PostDbModel> postDbModelList = list;
            PostDbModel postDbModel = new PostDbModel();
            PreparedStatement preparedStatement;
            PreparedStatement updatePreparedStatement;
            ResultSet resultSet = null;
        try {
            
            for(PostDbModel postDbModels: postDbModelList){
                
                System.out.println(postDbModel.getUrl());
                
                postDbModel.setId(postDbModels.getId());
                postDbModel.setUrl(postDbModels.getUrl());
                postDbModel.setTitle(postDbModels.getTitle());
                postDbModel.setLikes(postDbModels.getLikes());
                postDbModel.setReplies(postDbModels.getReplies());
                postDbModel.setViews(postDbModels.getViews());
                postDbModel.setDiscussion(postDbModels.getDiscussion());
                postDbModel.setDescription(postDbModels.getDescription());
                postDbModel.setUserId(postDbModels.getUserId());
                postDbModel.setTags(postDbModels.getTags());
                
                System.out.println(postDbModel.getUrl());
                System.out.println(postDbModels.getUrl());
                
            }
            
            preparedStatement = this.connection.prepareStatement("SELECT * FROM `posttable` WHERE `url`= ? ");
            preparedStatement.setString(1, postDbModel.getUrl());
            resultSet = preparedStatement.executeQuery();
            if (!resultSet.first()){
//                while(resultSet.next()){
//                    postDbModel.setId(resultSet.getInt(1));
//                    System.out.println(resultSet.getString(3));
//                    int i = resultSet.getInt(1);
//                    String s = resultSet.getString(2);
//                }
                insertPostDB(postDbModelList);
            }else{
                updatePreparedStatement = this.connection.prepareStatement("UPDATE `posttable` SET `url`= ? ,`title`= ? ,`likes`= ? ,`replies`= ? ,`views`= ? ,`discussion`= ? ,`description`= ? , `tags` = ? WHERE `url` = ? ;");
                updatePreparedStatement.setString(1, postDbModel.getUrl());
                updatePreparedStatement.setString(2, postDbModel.getTitle());
                updatePreparedStatement.setString(3, postDbModel.getLikes());
                updatePreparedStatement.setString(4, postDbModel.getReplies());
                updatePreparedStatement.setString(5, postDbModel.getViews());
                updatePreparedStatement.setString(6, postDbModel.getDiscussion());
                updatePreparedStatement.setString(7, postDbModel.getDescription());
                updatePreparedStatement.setString(8, postDbModel.getTags());
                updatePreparedStatement.setString(9, postDbModel.getUrl());
                updatePreparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public List<UserDBModel> getUserDB(){
        List<UserDBModel> list = new LinkedList<>();
        
        try {
            PreparedStatement preparedStatement;
            preparedStatement = this.connection.prepareStatement("SELECT * FROM `usertableinformation`");
            
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
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
    public List<PostDbModel> getPostDB(){
        List<PostDbModel> list = new LinkedList<>();
        
        try {
            PreparedStatement preparedStatement;
            preparedStatement = this.connection.prepareStatement("SELECT * FROM `posttable`");
            
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                PostDbModel postDbModel = new PostDbModel();
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
