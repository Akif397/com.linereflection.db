/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.linereflection.db.DBManager;

/**
 *
 * @author Akif
 */
public class DBController {
    public String tagNameDivider(String tagNames){
        String tagIDString = " ";
        String[] tagNameArray = tagNames.split(",");
        
        for (String tag : tagNameArray) {
            int result = DBManager.getDBManager().populatePostTagTable(tag);
            if(result != 0){
                tagIDString = tagIDString + result;
            }
            else{
                tagIDString = null;
            }
        }
        return tagIDString;
    }
    
}
