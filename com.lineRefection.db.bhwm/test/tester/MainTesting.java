/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tester;

import com.lineReflection.db.DBManager.DBManager;
import com.lineReflection.db.DBModel.PostDbModel;
import com.lineReflection.db.DBModel.UserDBModel;
import com.linereflection.db.DBController.DBController;
import java.util.LinkedList;
import java.util.List;


public class MainTesting {
    public static void main(String[] args) throws ClassNotFoundException {
        
        DBManager dBManager = new DBManager();
//        
        DBController dbController = new DBController();
//        
        PostDbModel postDbModel = new  PostDbModel();
        postDbModel.setId(4);
        postDbModel.setUrl("newUrl");
        postDbModel.setTitle("updated again");
        postDbModel.setLikes("llllllllllllll");
        postDbModel.setReplies("kkkkkkk");
        postDbModel.setViews("vvvvv");
        postDbModel.setDiscussion("ddddddddd");
        postDbModel.setDescription("description updated");
        postDbModel.setUserId(1);  
        postDbModel.setTags("tagssss") ;
        List<PostDbModel> list = new LinkedList<>() ;
        list.add(postDbModel);
        dBManager.updatePostDB(list);
          dbController.getPost();

//          List<UserDBModel> list = new LinkedList<>();
//          UserDBModel userDBModel = new UserDBModel();
//          userDBModel.setId(2);
//          userDBModel.setEmail("hhhhh@g.c");
//          userDBModel.setPassword("jjjjjjj");
//          list.add(userDBModel);
//          dBManager.insertUserDB(list);
//          dbController.getUser();
          
        
    }
    
}
