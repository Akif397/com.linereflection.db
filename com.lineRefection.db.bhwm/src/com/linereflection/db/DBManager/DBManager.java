package com.linereflection.db.DBManager;

import com.linereflection.db.DBModel.PostDetails;
import com.linereflection.db.DBModel.User;
import java.sql.Connection;
//import com.mysql.jdbc.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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
    private DBController dBController = null;
    private User user = new User();
    private PostDetails postDetails = new PostDetails();

    private static DBManager dbManager = null;

//    private DBConnection dBConnection = new DBConnection();
//    private Connection connection = dBConnection.getConnection();
    public static enum TABLE {

        TABLE_BHW_USER("bhwUser"),
        TABLE_BHW_POST("posttable"),
        TABLE_BHW_AUTHOR("postauthor"),
        TABLE_BHW_TAG("posttag"),
        TABLE_BHW_DISCUSSIONBOARD("discussionboard");

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
            int authorID, discussionBoardID;
            String tagID = null;

            authorID = populatePostAuthorTable(postDetails.getAuthor());
            discussionBoardID = populatePostDiscussionBoardTable(postDetails.getDiscussion());
            dBController = new DBController();
            tagID = dBController.tagNameDivider(postDetails.getTags());
            String tagOKID = tagID.replaceFirst(",", "").trim();
            ps = DBManager.getDBManager().getConDBConnection().prepareStatement("select * from " + TABLE.TABLE_BHW_POST
                    + " where posturl = ?");
            ps.setString(1, postDetails.getUrl());
            rs = ps.executeQuery();

            if (!rs.next()) {
                psForInsert = DBManager.getDBManager().getConDBConnection().prepareStatement("INSERT INTO " + TABLE.TABLE_BHW_POST
                        + " (posttitle, posturl, postauthor, posttag, postdiscussionboard, postlike, postview, postreplie, postdate, posttime, bhwUser_email, discussionboard_id, postauthor_id, posttag_id)  Values"
                        + " (?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
                psForInsert.setString(1, postDetails.getTitle());
                psForInsert.setString(2, postDetails.getUrl());
                psForInsert.setString(3, postDetails.getAuthor());
                psForInsert.setString(4, postDetails.getTags());
                psForInsert.setString(5, postDetails.getDiscussion());
                psForInsert.setInt(6, postDetails.getLikes());
                psForInsert.setInt(7, postDetails.getViews());
                psForInsert.setInt(8, postDetails.getReplies());
                psForInsert.setDate(9, (java.sql.Date) postDetails.getPostdate());
                psForInsert.setString(10, postDetails.getTime());
                psForInsert.setString(11, postDetails.getUserEmail());
                psForInsert.setInt(12, discussionBoardID);
                psForInsert.setInt(13, authorID);
                psForInsert.setString(14, tagOKID);
                boolean result = psForInsert.execute();
                System.out.println(result);
            } else {
                System.out.println("The row already inserted");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ResultSet selectSQL(TABLE tableName, String columnName, String searchValue) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if (DBManager.getDBManager().getConDBConnection().isClosed()) {
                DBManager.getDBManager().getConDBConnection();
            }
            ps = DBManager.getDBManager().getConDBConnection().prepareStatement("select * from " + tableName
                    + " where " + columnName + " = ?");

            ps.setString(1, searchValue);
            rs = ps.executeQuery();
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    public boolean insertSQL(TABLE tablName, String columnName, String insertValue) {
        boolean insertRersult = false;
        try {
            PreparedStatement ps = null;
            ps = DBManager.getDBManager().getConDBConnection().prepareStatement("INSERT INTO " + tablName
                    + " (" + columnName + ") Values (?) ;");
            ps.setString(1, insertValue);
            insertRersult = ps.execute();
            return insertRersult;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return insertRersult;
    }

    public int populatePostAuthorTable(String authorName) {

        try {
            String columnName = "authorname";
            ResultSet rs, rsForSelect = null;
            rs = selectSQL(TABLE.TABLE_BHW_AUTHOR, columnName, authorName);
            if (!rs.next()) {
                insertSQL(TABLE.TABLE_BHW_AUTHOR, columnName, authorName);
                rsForSelect = selectSQL(TABLE.TABLE_BHW_AUTHOR, columnName, authorName);
                if (rsForSelect.next()) {
                    int authorID = rsForSelect.getInt(1);
                    return authorID;
                }
            } else {
                int authorID = rs.getInt(1);
                return authorID;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int populatePostDiscussionBoardTable(String boardName) {

        try {
            String columnName = "boardname";
            ResultSet rs, rsForSelect = null;
            rs = selectSQL(TABLE.TABLE_BHW_DISCUSSIONBOARD, columnName, boardName);
            if (!rs.next()) {
                insertSQL(TABLE.TABLE_BHW_DISCUSSIONBOARD, columnName, boardName);
                rsForSelect = selectSQL(TABLE.TABLE_BHW_DISCUSSIONBOARD, columnName, boardName);
                if (rsForSelect.next()) {
                    int boardID = rsForSelect.getInt(1);
                    return boardID;
                }
            } else {
                int boardID = rs.getInt(1);
                return boardID;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int populatePostTagTable(String tagname) {

        try {
            String columnName = "tagname";
            ResultSet rs, rsForSelect = null;
            rs = selectSQL(TABLE.TABLE_BHW_TAG, columnName, tagname);
            if (!rs.next()) {
                insertSQL(TABLE.TABLE_BHW_TAG, columnName, tagname);
                rsForSelect = selectSQL(TABLE.TABLE_BHW_TAG, columnName, tagname);
                if (rsForSelect.next()) {
                    int tagID = rsForSelect.getInt(1);
                    return tagID;
                }
            } else {
                int tagID = rs.getInt(1);
                return tagID;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String selectColumn(String tag) {
        String searchTag = tag;
        if (searchTag == "likes") {
            searchTag = "postlike";
        } else if (searchTag == "views") {
            searchTag = "postview";
        } else if (searchTag == "replies") {
            searchTag = "postreplie";
        }
        return searchTag;
    }

    public ResultSet searchQuery(TABLE tableName, String columnName, int start, int end) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if (DBManager.getDBManager().getConDBConnection().isClosed()) {
                DBManager.getDBManager().getConDBConnection();
            }
            ps = DBManager.getDBManager().getConDBConnection().prepareStatement("select * from " + tableName
                    + " where " + columnName + " between " + start + " and " + end);

            rs = ps.executeQuery();
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    public ResultSet searchTag(TABLE tableName, String columnName, String tagName) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if (DBManager.getDBManager().getConDBConnection().isClosed()) {
                DBManager.getDBManager().getConDBConnection();
            }
            ps = DBManager.getDBManager().getConDBConnection().prepareStatement("SELECT * FROM " + tableName + " where FIND_IN_SET(?," + columnName + ")");
            ps.setString(1, tagName);
            rs = ps.executeQuery();
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    List<PostDetails> postDetailsList = new LinkedList<>();

    public List<PostDetails> searchByNumber(String tag, int start, int end) {
        String searchTag = tag;
        int startValue = start;
        int endValue = end;
        PreparedStatement ps = null;
        ResultSet resultSet, storeResultSet = null;
        searchTag = selectColumn(searchTag);
        postDetailsList.clear();

        try {
            resultSet = searchQuery(TABLE.TABLE_BHW_POST, searchTag, start, end);

            while (resultSet.next()) {
                PostDetails postDetails = new PostDetails();
                //postDetails.setId(resultSet.getInt(1));
                postDetails.setTitle(resultSet.getString(2));
                postDetails.setUrl(resultSet.getString(3));
                postDetails.setTags(resultSet.getString(5));
                postDetails.setLikes(resultSet.getInt(7));
                postDetails.setViews(resultSet.getInt(8));
                postDetails.setReplies(resultSet.getInt(9));

                postDetails.setDiscussion(resultSet.getString(6));
                //  postDetails.setAuthor(resultSet.getString(9));
                //  postDetails.setPostdate(resultSet.getDate(10));
                //   postDetails.setUserId(resultSet.getInt(11));
                postDetailsList.add(postDetails);

            }
            resultSet.close();
            // ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return postDetailsList;

    }

    public List<PostDetails> searchTagAndAuthor(String sTag, String sString) {
        String searchTag = sTag;
        String searchString = sString;
        String newSeachValue = searchString.trim();
        postDetailsList.clear();
        PreparedStatement ps = null;
        ResultSet resultSet = null, storeResultSet = null;

        if (searchTag == "Tags") {
            searchTag = "posttag";
            resultSet = searchTag(TABLE.TABLE_BHW_POST, searchTag, newSeachValue);

        } else if (searchTag == "author") {
            searchTag = "postauthor";
            resultSet = selectSQL(TABLE.TABLE_BHW_POST, searchTag, newSeachValue);
        }
        try {
            if (DBManager.getDBManager().getConDBConnection().isClosed()) {
                DBManager.getDBManager().getConDBConnection();
            }
            while (resultSet.next()) {
                PostDetails postDetails = new PostDetails();
                //postDetails.setId(resultSet.getInt(1));
                postDetails.setTitle(resultSet.getString(2));
                postDetails.setUrl(resultSet.getString(3));
                postDetails.setTags(resultSet.getString(5));
                postDetails.setLikes(resultSet.getInt(7));
                postDetails.setViews(resultSet.getInt(8));
                postDetails.setReplies(resultSet.getInt(9));
                postDetails.setDiscussion(resultSet.getString(6));             
                postDetailsList.add(postDetails);
            }
            resultSet.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return postDetailsList;
    }

    public void searchByDate(String tag, String start, String end) {
        String column = "postdate";
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        postDetailsList.clear();

        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            if (DBManager.getDBManager().getConDBConnection().isClosed()) {
                DBManager.getDBManager().getConDBConnection();
            }
            ps = DBManager.getDBManager().getConDBConnection().prepareStatement("select * from " + TABLE.TABLE_BHW_POST
                    + " where  " + column + " between '" + startDate + "' and '" + endDate + "'");

            resultSet = ps.executeQuery();

            while (resultSet.next()) {
                PostDetails postDetails = new PostDetails();
              
                postDetails.setTitle(resultSet.getString(2));
                postDetails.setUrl(resultSet.getString(3));
                postDetails.setTags(resultSet.getString(5));
                postDetails.setLikes(resultSet.getInt(7));
                postDetails.setViews(resultSet.getInt(8));
                postDetails.setReplies(resultSet.getInt(9));
                postDetails.setDiscussion(resultSet.getString(6));
                postDetailsList.add(postDetails);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
            }
            ps.close();
            resultSet.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return postDetailsList;
    }

    public List<PostDetails> searchTag() {
        PreparedStatement preparedStatementTag = null;
        List<PostDetails> postDetailsList = new LinkedList<>();
        ResultSet resultSet = null;

        try {
            if (DBManager.getDBManager().getConDBConnection().isClosed()) {
                DBManager.getDBManager().getConDBConnection();
            }
            preparedStatementTag = DBManager.getDBManager().getConDBConnection().prepareStatement("select * from " + TABLE.TABLE_BHW_TAG);

            resultSet = preparedStatementTag.executeQuery();
            while (resultSet.next()) {
                PostDetails postDetails = new PostDetails();

                postDetails.setTags(resultSet.getString(2));
                postDetailsList.add(postDetails);
            }
            preparedStatementTag.close();
            resultSet.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return postDetailsList;
    }

    public List<PostDetails> getSearchTag(List list) {
        List<PostDetails> tagList = list;
        return tagList;
    }

    public List<PostDetails> searchAuthor() {
        PreparedStatement preparedStatementTag = null;
        List<PostDetails> postDetailsList = new LinkedList<>();
        ResultSet resultSet = null;

        try {
            if (DBManager.getDBManager().getConDBConnection().isClosed()) {
                DBManager.getDBManager().getConDBConnection();
            }
            preparedStatementTag = DBManager.getDBManager().getConDBConnection().prepareStatement("select * from " + TABLE.TABLE_BHW_AUTHOR);

            resultSet = preparedStatementTag.executeQuery();
            while (resultSet.next()) {
                PostDetails postDetails = new PostDetails();

                postDetails.setAuthor(resultSet.getString(2));
                postDetailsList.add(postDetails);
            }
            preparedStatementTag.close();
            resultSet.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return postDetailsList;
    }
}
