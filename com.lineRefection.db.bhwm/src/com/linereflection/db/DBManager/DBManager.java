package com.lineReflection.db.DBManager;

import com.lineReflection.db.DBModel.PostDetails;
import com.lineReflection.db.DBModel.User;
import java.sql.Connection;
//import com.mysql.jdbc.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
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
        TABLE_BHW_POST("posttable"),
        TABLE_BHW_AUTHOR("postauthortable"),
        TABLE_BHW_LIKE("postlikestable"),
        TABLE_BHW_VIEW("postviewstable"),
        TABLE_BHW_TAG("posttagtable"),
        TABLE_BHW_DATE("postdatetable");

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
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {

            connection = (Connection) DriverManager.
                    getConnection("jdbc:mysql://" + PUBLIC_DNS + ":" + PORT + "/" + DATABASE, REMOTE_DATABASE_USERNAME, DATABASE_USER_PASSWORD);
        } catch (SQLException ex) {
            throw ex;
        }
        return connection;
    }

    public User getLoggedInUser() {
        return user;
    }

    public PostDetails getPostDetails() {
        return postDetails;
    }

    public boolean login(String email, String password) {
        boolean checkForUser = false;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            if (DBManager.getDBManager().getConDBConnection().isClosed()) {
                DBManager.getDBManager().getConDBConnection();
            }
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
            rs.close();
            ps.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return checkForUser;
    }

    public void insertPostDetailsToDatabase(PostDetails postDetails) {

        try {
            if (DBManager.getDBManager().getConDBConnection().isClosed()) {
                DBManager.getDBManager().getConDBConnection();
            }
            PreparedStatement ps, psForInsert = null;
            ResultSet rs = null;
            ps = DBManager.getDBManager().getConDBConnection().prepareStatement("select * from " + TABLE.TABLE_BHW_POST
                    + " where url = ?");
            ps.setString(1, postDetails.getUrl());
            rs = ps.executeQuery();

            if (!rs.next()) {
                psForInsert = DBManager.getDBManager().getConDBConnection().prepareStatement("INSERT INTO " + TABLE.TABLE_BHW_POST
                        + " (url, title, likes, replies, views, discussion, tags, author, postdate, time, userEmail)  Values"
                        + " (?,?,?,?,?,?,?,?,?,?,?);");
                psForInsert.setString(1, postDetails.getUrl());
                psForInsert.setString(2, postDetails.getTitle());
                psForInsert.setInt(3, postDetails.getLikes());
                psForInsert.setInt(4, postDetails.getReplies());
                psForInsert.setInt(5, postDetails.getViews());
                psForInsert.setString(6, postDetails.getDiscussion());
                psForInsert.setString(7, postDetails.getTags());
                psForInsert.setString(8, postDetails.getAuthor());
                psForInsert.setDate(9, (java.sql.Date) postDetails.getPostdate());
                psForInsert.setString(10, postDetails.getTime());
                psForInsert.setString(11, postDetails.getUserEmail());
                psForInsert.execute();
            } else {
                System.out.println("The row already inserted");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void populatePostAuthorTable(PostDetails postDetails) {
        try {
            if (DBManager.getDBManager().getConDBConnection().isClosed()) {
                DBManager.getDBManager().getConDBConnection();
            }
            PreparedStatement ps, psForChecking, psForInsert = null;
            ResultSet rs, rsForCheck = null;
            String author = postDetails.getAuthor();
            
            ps = DBManager.getDBManager().getConDBConnection().prepareStatement("select * from " + TABLE.TABLE_BHW_POST
                    + " where author = ?");
            ps.setString(1, author);
            rs = ps.executeQuery();

            while (rs.next()) {
                int postID = rs.getInt(1);
                psForChecking = DBManager.getDBManager().getConDBConnection().prepareStatement("select * from " + TABLE.TABLE_BHW_AUTHOR
                        + " where author = ? and postid = ?");
                psForChecking.setString(1, author);
                psForChecking.setInt(2, postID);

                rsForCheck = psForChecking.executeQuery();
                if (!rsForCheck.next()) {
                    psForInsert = DBManager.getDBManager().getConDBConnection().prepareStatement("INSERT INTO " + TABLE.TABLE_BHW_AUTHOR
                            + " (author, postid)  Values"
                            + " (?, ?);");
                    psForInsert.setString(1, author);
                    psForInsert.setInt(2, postID);
                    psForInsert.execute();
                } else {
                    System.out.println("The author has already inserted");
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void populatePostDateTable(PostDetails postDetails) {
        try {
            if (DBManager.getDBManager().getConDBConnection().isClosed()) {
                DBManager.getDBManager().getConDBConnection();
            }
            PreparedStatement ps, psForChecking, psForInsert = null;
            ResultSet rs, rsForCheck = null;
            Date postDate = postDetails.getPostdate();
            
            ps = DBManager.getDBManager().getConDBConnection().prepareStatement("select * from " + TABLE.TABLE_BHW_POST
                    + " where postdate = ?");
            ps.setDate(1, (java.sql.Date) postDate);
            rs = ps.executeQuery();

            while (rs.next()) {
                int postID = rs.getInt(1);
                psForChecking = DBManager.getDBManager().getConDBConnection().prepareStatement("select * from " + TABLE.TABLE_BHW_DATE
                        + " where date = ? and postid = ?");
                psForChecking.setDate(1, (java.sql.Date) postDate);
                psForChecking.setInt(2, postID);

                rsForCheck = psForChecking.executeQuery();
                if (!rsForCheck.next()) {
                    psForInsert = DBManager.getDBManager().getConDBConnection().prepareStatement("INSERT INTO " + TABLE.TABLE_BHW_DATE
                            + " (date, postid)  Values"
                            + " (?, ?);");
                    psForInsert.setDate(1, (java.sql.Date) postDate);
                    psForInsert.setInt(2, postID);
                    psForInsert.execute();
                } else {
                    System.out.println("The author has already inserted");
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void populatePostLikesTable(PostDetails postDetails) {
        try {
            if (DBManager.getDBManager().getConDBConnection().isClosed()) {
                DBManager.getDBManager().getConDBConnection();
            }
            PreparedStatement ps, psForChecking, psForInsert = null;
            ResultSet rs, rsForCheck = null;
            int postLikes = postDetails.getLikes();
            
            ps = DBManager.getDBManager().getConDBConnection().prepareStatement("select * from " + TABLE.TABLE_BHW_POST
                    + " where likes = ?");
            ps.setInt(1, postLikes);
            rs = ps.executeQuery();

            while (rs.next()) {
                int postID = rs.getInt(1);
                psForChecking = DBManager.getDBManager().getConDBConnection().prepareStatement("select * from " + TABLE.TABLE_BHW_LIKE
                        + " where likes = ? and postid = ?");
                psForChecking.setInt(1, postLikes);
                psForChecking.setInt(2, postID);

                rsForCheck = psForChecking.executeQuery();
                if (!rsForCheck.next()) {
                    psForInsert = DBManager.getDBManager().getConDBConnection().prepareStatement("INSERT INTO " + TABLE.TABLE_BHW_LIKE
                            + " (likes , postid)  Values"
                            + " (?, ?);");
                    psForInsert.setInt(1, postLikes);
                    psForInsert.setInt(2, postID);
                    psForInsert.execute();
                } else {
                    System.out.println("Likes against the Post has already inserted");
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void populatePostViewsTable(PostDetails postDetails) {
        try {
            if (DBManager.getDBManager().getConDBConnection().isClosed()) {
                DBManager.getDBManager().getConDBConnection();
            }
            PreparedStatement ps, psForChecking, psForInsert = null;
            ResultSet rs, rsForCheck = null;
            int postViews = postDetails.getViews();
            
            ps = DBManager.getDBManager().getConDBConnection().prepareStatement("select * from " + TABLE.TABLE_BHW_POST
                    + " where views = ?");
            ps.setInt(1, postViews);
            rs = ps.executeQuery();

            while (rs.next()) {
                int postID = rs.getInt(1);
                psForChecking = DBManager.getDBManager().getConDBConnection().prepareStatement("select * from " + TABLE.TABLE_BHW_VIEW
                        + " where views = ? and postid = ?");
                psForChecking.setInt(1, postViews);
                psForChecking.setInt(2, postID);

                rsForCheck = psForChecking.executeQuery();
                if (!rsForCheck.next()) {
                    psForInsert = DBManager.getDBManager().getConDBConnection().prepareStatement("INSERT INTO " + TABLE.TABLE_BHW_VIEW
                            + " (views , postid)  Values"
                            + " (?, ?);");
                    psForInsert.setInt(1, postViews);
                    psForInsert.setInt(2, postID);
                    psForInsert.execute();
                } else {
                    System.out.println("Views against the Post has already inserted");
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void populatePostTagTable(PostDetails postDetails) {
        try {
            if (DBManager.getDBManager().getConDBConnection().isClosed()) {
                DBManager.getDBManager().getConDBConnection();
            }
            PreparedStatement ps, psForChecking, psForInsert = null;
            ResultSet rs, rsForCheck = null;
            String tag = postDetails.getTags();
            
            ps = DBManager.getDBManager().getConDBConnection().prepareStatement("select * from " + TABLE.TABLE_BHW_POST
                    + " where tags = ?");
            ps.setString(1, tag);
            rs = ps.executeQuery();

            while (rs.next()) {
                int postID = rs.getInt(1);
                String tagInPostTable = rs.getString(8);
                psForChecking = DBManager.getDBManager().getConDBConnection().prepareStatement("select * from " + TABLE.TABLE_BHW_TAG
                        + " where tag = ? and postid = ?");
                psForChecking.setString(1, tagInPostTable);
                psForChecking.setInt(2, postID);

                rsForCheck = psForChecking.executeQuery();
                if (!rsForCheck.next()) {
                    psForInsert = DBManager.getDBManager().getConDBConnection().prepareStatement("INSERT INTO " + TABLE.TABLE_BHW_TAG
                            + " (tag, postid)  Values"
                            + " (?, ?);");
                    psForInsert.setString(1, tag);
                    psForInsert.setInt(2, postID);
                    psForInsert.execute();
                } else {
                    System.out.println("The Tag has already inserted");
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    List<PostDetails> postDetailsList = new LinkedList<>();

    public List<PostDetails> search(String sString, String sTag) {
        PreparedStatement ps = null;
        String searchTag = sTag;
//        List<PostDetails> postDetailsList = new LinkedList<>();

        String searchString = sString;
        ResultSet resultSet = null;
        try {
            if (DBManager.getDBManager().getConDBConnection().isClosed()) {
                DBManager.getDBManager().getConDBConnection();
            }
            ps = DBManager.getDBManager().getConDBConnection().prepareStatement("select * from " + TABLE.TABLE_BHW_POST + " where " + searchTag + " = ?");
            ps.setString(1, searchString);
            resultSet = ps.executeQuery();
            while (resultSet.next()) {
                PostDetails postDetails = new PostDetails();
                // postDetails.setId(resultSet.getInt(1));
                postDetails.setUrl(resultSet.getString(2));
                postDetails.setTitle(resultSet.getString(3));
                postDetails.setLikes(resultSet.getInt(4));
                postDetails.setReplies(resultSet.getInt(5));
                postDetails.setViews(resultSet.getInt(6));
                postDetails.setDiscussion(resultSet.getString(7));
                postDetails.setTags(resultSet.getString(8));
                //  postDetails.setAuthor(resultSet.getString(9));
                //  postDetails.setPostdate(resultSet.getDate(10));
                //   postDetails.setUserId(resultSet.getInt(11));
                postDetailsList.add(postDetails);
//                System.out.println(postDetails.getDiscussion());
//                System.err.println(postDetails.getPostdate());

            }
            resultSet.close();
            ps.close();
            getSearchDetails(postDetailsList);
        } catch (SQLException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return postDetailsList;
    }

    public List<PostDetails> search(int sString, String sTag) {
        PreparedStatement ps = null;
        String searchTag = sTag;
//        List<PostDetails> postDetailsList = new LinkedList<>();

        int searchString = sString;
        ResultSet resultSet = null;
        try {
            if (DBManager.getDBManager().getConDBConnection().isClosed()) {
                DBManager.getDBManager().getConDBConnection();
            }
            ps = DBManager.getDBManager().getConDBConnection().prepareStatement("select * from " + TABLE.TABLE_BHW_POST + " where " + searchTag + " = ? ");
            //   ps = DBManager.getDBManager().getConDBConnection().prepareStatement("select * from " + TABLE.TABLE_BHW_POST + " where " + searchTag + " >= ? order by " + searchTag +" ASC");
            //    ps.setString(1, searchTag);
            ps.setInt(1, searchString);
            resultSet = ps.executeQuery();
            while (resultSet.next()) {
                PostDetails postDetails = new PostDetails();
                // postDetails.setId(resultSet.getInt(1));
                postDetails.setUrl(resultSet.getString(2));
                postDetails.setTitle(resultSet.getString(3));
                postDetails.setLikes(resultSet.getInt(4));
                postDetails.setReplies(resultSet.getInt(5));
                postDetails.setViews(resultSet.getInt(6));
                postDetails.setDiscussion(resultSet.getString(7));
                postDetails.setTags(resultSet.getString(8));
                //  postDetails.setAuthor(resultSet.getString(9));
                //  postDetails.setPostdate(resultSet.getDate(10));
                //   postDetails.setUserId(resultSet.getInt(11));
                postDetailsList.add(postDetails);
            }
            ps.close();
            resultSet.close();
            getSearchDetails(postDetailsList);
        } catch (SQLException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return postDetailsList;
    }

    public void getSearchDetails(List list) {
//        search(List list);
    }

    public List<PostDetails> displayTable() {
        return postDetailsList;
    }

    public List<PostDetails> searchAuthorName() {
        PreparedStatement ps = null;

        List<PostDetails> postDetailsList = new LinkedList<>();
        ResultSet resultSet = null;
        try {
            if (DBManager.getDBManager().getConDBConnection().isClosed()) {
                DBManager.getDBManager().getConDBConnection();
            }
            // ps = DBManager.getDBManager().getConDBConnection().prepareStatement("select * from " + TABLE.TABLE_BHW_POST + " where author = ?");
            ps = DBManager.getDBManager().getConDBConnection().prepareStatement("select * from " + TABLE.TABLE_BHW_POST);

            resultSet = ps.executeQuery();
            while (resultSet.next()) {
                PostDetails postDetails = new PostDetails();
                postDetails.setAuthor(resultSet.getString(9));

                postDetailsList.add(postDetails);

                // System.out.println(postDetails.getAuthor());
            }
            ps.close();
            resultSet.close();
            getSearchDetails(postDetailsList);
        } catch (SQLException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return postDetailsList;
    }
}
