/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.linereflection.db.DBController;

import com.lineReflection.db.DBManager.DBManager;
import com.lineReflection.db.DBModel.PostDBModel;
import com.lineReflection.db.DBModel.UserDBModel;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author PC3
 */
public class DBController {
    DBManager dbManager = DBManager.getDBManager();
    
    

    public void getUser() {

        List<UserDBModel> uList = dbManager.getUserDB();
        for (UserDBModel udbModel : uList) {
            System.out.println(udbModel.getId());
            System.out.println(udbModel.getEmail());
            System.out.println(udbModel.getPassword());
        }
    }
    public void getPost(){
        List<PostDBModel> pList = dbManager.getPostDB();
        for(PostDBModel postDbModel : pList){
            System.out.println(postDbModel.getId());
            System.out.println(postDbModel.getUrl());
            System.out.println(postDbModel.getTitle());
            System.out.println(postDbModel.getLikes());
            System.out.println(postDbModel.getReplies());
            System.out.println(postDbModel.getViews());
            System.out.println(postDbModel.getDiscussion());
            System.out.println(postDbModel.getDescription());
            System.out.println(postDbModel.getUserId());
            System.out.println(postDbModel.getTags());
        }
    }
    public void updatePost(List list){
        List<PostDBModel> updateList = list;
        try {
            dbManager.updatePostDB(updateList);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DBController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
