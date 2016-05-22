package com.che;

import com.sun.org.apache.bcel.internal.generic.RETURN;
import jdk.internal.org.objectweb.asm.tree.analysis.Value;

import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.TimeZone.*;


/**
 * Created by Alexx on 14.05.2016.
 */
public class DatabaseManager {

    private Connection c;
    private static final String DB_URL = "jdbc:sqlite:C:\\Users\\Alexx\\IdeaProjects\\Wortime_server\\DB_WTS.s3db";

    public DatabaseManager() {
        c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(DB_URL);

        } catch (Exception ex) {
            System.err.println(ex.getClass().getName() + ":"
                    + ex.getMessage());
            System.exit(0);
        }
        System.out.println("Database is connected successfully");
    }


    private String formatSqlString(String o) {
        return "\'" + o + "\'";
    }
    /****REGISTRATION ***/
    public void insert(String _login) {
        Statement statement = null;
                    //new session
            try {
                c = DriverManager.getConnection(DB_URL);
                c.setAutoCommit(false);
                System.out.println("Database opened successfully");
                statement = c.createStatement();
                String sql = "INSERT INTO users" + " (LOGIN, TOKEN) " +
                "VALUES (" + formatSqlString(_login) + ", "  + formatSqlString(new SessionIdentifierGenerator().generateString()) + " )";
                statement.executeUpdate(sql);
                CLog.log_console("login: " + _login + " and his parameters were added to database");
                statement.close();
                c.commit();
                c.close();

                System.out.println("Database closed successfully");

            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println(ex.getClass().getName() + ": " + ex.getMessage());
                System.exit(0);
            } finally {
                try {
                    c.close();
                    System.out.println("Database closed successfully");

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

            System.out.println(" Records created successfully");
        }


    public void insert_STime(String _STime, String _CDate, String _login) {
        CLog.log_console("*******insert_STARTTime*******");
        Statement statement = null;
        String _Token = getToken(_login);

        CLog.log_console("Token= " + _Token + "for login " + _login);
            try {
                c = DriverManager.getConnection(DB_URL);
                c.setAutoCommit(false);
                System.out.println("Database opened succesfully");
                statement = c.createStatement();

                String sql = "UPDATE users SET CDATE=" + formatSqlString(_CDate) + ", STIME=" + formatSqlString(_STime) +
                        " WHERE LOGIN=" + formatSqlString(_login) + " AND TOKEN=" + formatSqlString(_Token);
                statement.executeUpdate(sql);
                CLog.log_console("current date: " + _CDate + " and sTime " + _STime + " at login " + _login + " were added to database");
                statement.close();
                c.commit();
                c.close();
                System.out.println("Database closed succesfully");

            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println(ex.getClass().getName() + ": " + ex.getMessage());
                System.exit(0);
            } finally {
                try {
                    c.close();
                    System.out.println("Database closed succesfully");

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

            System.out.println(" Records created successfully");


    }

    public void insert_ETime(String _ETime, String _CDate, String _login) {
        CLog.log_console("*******insert_ENDTime*******");
        PreparedStatement sql = null;

        if (isDateExist(_CDate, _login)) {
            try {
                c = DriverManager.getConnection(DB_URL);
                c.setAutoCommit(false);
                System.out.println("Database opened succesfully");
                sql = c.prepareStatement("UPDATE users SET ETIME = ? WHERE CDATE = ? AND LOGIN =?");
                sql.setString(1, _ETime);
                sql.setString(2, _CDate);
                sql.setString(3, _login);
                sql.executeUpdate();

                CLog.log_console("exist_login: " + _login + "exist_date: " + _CDate + "new_eTime" + _ETime +" was added to database");
                sql.close();
                c.commit();
                c.close();

                System.out.println("Database closed succesfully");

            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println(ex.getClass().getName() + ": " + ex.getMessage());
                System.exit(0);
            } finally {
                try {
                    c.close();
                    System.out.println("Database closed succesfully");

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

            System.out.println(" Records created successfully");
        }
    }

    public String GetWThours(String login) {
        Statement statement = null;


        try {
            /******* 1-st statement *********/
            c = DriverManager.getConnection(DB_URL);
            c.setAutoCommit(false);
            statement = c.createStatement();
            System.out.println("Database is opened successfully");
            String sql1 = "DELETE FROM users_wt";
            statement.executeUpdate(sql1);
            statement.close();
            c.commit();
            c.close();
            System.out.println("Database closed succesfully");
            CLog.log_console("2-nd table was succsessfully cleared!!!");

            /******* 2-nd statement *********/

            c = DriverManager.getConnection(DB_URL);
            c.setAutoCommit(false);
            statement = c.createStatement();
            System.out.println("Database is opened successfully");
            String sql2 = "INSERT INTO users_wt (LOGIN, STIME, ETIME) SELECT LOGIN, STIME, ETIME FROM users " +
                    "WHERE LOGIN=(" + formatSqlString(login) + ")" ;            //we get all data about user_name
            statement.executeUpdate(sql2);
            statement.close();
            c.commit();
            c.close();
            System.out.println("Database closed succesfully");
            CLog.log_console("2-nd table was succsessfully updated frop table_1!!!");

            /*****  3-rd statement ****/
            c = DriverManager.getConnection(DB_URL);
            c.setAutoCommit(false);
            statement = c.createStatement();
            System.out.println("Database is opened successfully");
            String sql3 = "SELECT * FROM users_wt";
            ResultSet rs = statement.executeQuery(sql3);
            CLog.log_console("ALL data was succsessfully SELECTED from table_2!!!");
            String WTime = null;
            int i = 0;
            long result = 0;
            while (rs.next()) {
                String STime = rs.getString("STIME");
                String ETime = rs.getString("ETIME");
                WTime =  wtimerah(STime, ETime);
                result += Long.parseLong(WTime);
                System.out.println("New Wtime for user_name" + login + "i=" + i++);
            }
            statement.close();
            c.commit();
            c.close();
            System.out.println("Database closed succesfully");
            CLog.log_console("ALL WTIME was succsessfully CALKED from table_2!!!");
            return Long.toString(result);

        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println(ex.getClass().getName() + ": " + ex.getMessage());
            System.exit(0);
        } finally {
            try {
                c.close();
                System.out.println("Database closed succesfully");

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        System.out.println(" Records created successfully");
        return null;
        }

    public String wtimerah (String STime, String ETime){
        int ETime_num = Integer.parseInt(ETime);
        int STime_num = Integer.parseInt(STime);
        int WTime_num = ETime_num - STime_num;
        return Integer.toString(WTime_num);

        }

    public String ID_max()    {
        Statement statement;
        try  {
            c = DriverManager.getConnection(DB_URL);
            c.setAutoCommit(false);
            System.out.println("Database opened succesfully");
            statement = c.createStatement();

                 ResultSet rs = statement.executeQuery("SELECT MAX(ID) FROM users");
                 String ID_max = rs.getString("MAX(ID)");
                 rs.close();
                 statement.close();
                 c.close();
                 System.out.println("Database closed succesfully");
            return ID_max;
             }
        catch (Exception ex) {
            ex.printStackTrace();
            System.err.println(ex.getClass().getName() + ": " + ex.getMessage());
            System.exit(0);
        } finally {
            try {
                c.close();
                System.out.println("Database closed succesfully");

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return null;
    }

    /***** getToken gets token for FIRst user in table ********/
    public String getToken(String login) {                    //mb add password field near login in
        CLog.log_console("*******GET_TOKEN_METHOD*******");
        if (!isExist(login)) {
            CLog.log_console("User not exist. Return null");
            return null;
        } else {
            CLog.log_console("Getting token for " + login);
            Statement statement;
            try {
                c = DriverManager.getConnection(DB_URL);
                c.setAutoCommit(false);
                CLog.log_console("Database opened succesfully");
                statement = c.createStatement();
                ResultSet rs = statement.executeQuery("SELECT * FROM users");
                // ArrayList<String> list = new ArrayList<>();
                while (rs.next()) {
                    String _name = rs.getString("LOGIN");
//                    if(!list.contains(_name))
//                        list.add(_name);
                    if (_name.equals(login))
                        return rs.getString("TOKEN");
                }
                rs.close();
                statement.close();
                c.close();
                System.out.println("Database closed successfully");
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println(ex.getClass().getName() + ": " + ex.getMessage());
                System.exit(0);
            } finally {
                try {
                    c.close();
                    System.out.println("Database closed successfully");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }
    /***** ISsESSIONfIN CHECKES STATUS OF PREVIOUS SESSION FOR USER_NAME ********/
    public boolean isSessionFin (String _login)  {
        Statement statement;
        try  {
            c = DriverManager.getConnection(DB_URL);
            c.setAutoCommit(false);
            System.out.println("Database opened succesfully");
            statement = c.createStatement();
            String sql = ("SELECT * FROM users WHERE LOGIN=" + formatSqlString(_login));

            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()){
            String _CDate = rs.getString("CDATE");
            String _STime = rs.getString("STIME");
            String _ETime = rs.getString("ETIME");
            String _ID = rs.getString("ID");
                CLog.log_console("Date from session: " + _ID + " Start Time: " + _STime + " End Time: " + _ETime);


                if (Value_is_null(_STime) ||  Value_is_null(_ETime) || Value_is_null(_CDate))   //true
                    return false;
                CLog.log_console("cycle:" + _ID + "_Stime" + _STime);
            }
            rs.close();
            statement.close();
            c.close();
            System.out.println("Database closed succesfully");
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.err.println(ex.getClass().getName() + ": " + ex.getMessage());
            System.exit(0);
        } finally {
            try {
                c.close();
                System.out.println("Database closed succesfully");

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return true;
    }


    public String getLast_C_Token(String _login)   {
        Statement statement;

        try  {
            c = DriverManager.getConnection(DB_URL);
            c.setAutoCommit(false);
            System.out.println("Database opened succesfully");
            statement = c.createStatement();
            String id_max = ID_max();
            int Id_mx = Integer.parseInt(id_max);
            ResultSet rs = statement.executeQuery("SELECT * FROM users WHERE LOGIN=" + formatSqlString(_login) + "AND ID=" + Id_mx);
            String _ID = rs.getString("ID");
            String _Login = rs.getString("LOGIN");
            String _ETime = rs.getString("ETIME");
            String _CurToken = rs.getString("TOKEN");
            if (!_CurToken.isEmpty())
            {
                return rs.getString("TOKEN");
            }

            rs.close();
            statement.close();
            c.close();
            CLog.log_console("Recent token was returned: " + _CurToken);
            System.out.println("Database closed succesfully");

        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.err.println(ex.getClass().getName() + ": " + ex.getMessage());
            System.exit(0);
        } finally {
            try {
                c.close();
                System.out.println("Database closed succesfully");

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        CLog.log_console("Recent token was returned: " );
        return null;
    }


    public String GetDoc(String _login) {
        Statement statement = null;
        try {

            c = DriverManager.getConnection(DB_URL);
            c.setAutoCommit(false);
            statement = c.createStatement();
            System.out.println("Database is opened successfully");
            String sql3 = "SELECT * FROM docum";
            ResultSet rs = statement.executeQuery(sql3);
            String Doc = rs.getString("DOCFILE");
            statement.close();
            c.commit();
            c.close();
            System.out.println("Database closed succesfully");
            return Doc;

        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println(ex.getClass().getName() + ": " + ex.getMessage());
            System.exit(0);
        } finally {
            try {
                c.close();
                System.out.println("Database closed succesfully");

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        System.out.println(" Records created successfully");
        return null;
    }


/*********************** Is Exist Checks   **************************************/

    public boolean isExist(String name, String password) {
            Statement statement;

            try {
                c = DriverManager.getConnection(DB_URL);
                c.setAutoCommit(false);
                System.out.println("Database opened succesfully");
                statement = c.createStatement();

                ResultSet rs = statement.executeQuery("SELECT * FROM users;");

                while (rs.next()) {
                    String _name = rs.getString("LOGIN");
                    String _password = rs.getString("PASSWORD");

                    if (_name.equals(name) && _password.equals(password))
                        return true;
                }
                rs.close();
                statement.close();
                c.close();
                System.out.println("Database closed succesfully");

            } catch (Exception ex) {

                ex.printStackTrace();
                System.err.println(ex.getClass().getName() + ": " + ex.getMessage());
                System.exit(0);
            } finally {
                try {
                    c.close();
                    System.out.println("Database closed succesfully");

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

            return false;
        }

    public boolean isExist(String login) {
        CLog.log_console("Checking " + login + " for availability");
        Statement statement;

        try {
            c = DriverManager.getConnection(DB_URL);
            c.setAutoCommit(false);
            System.out.println("Database opened successfully");
            statement = c.createStatement();

            ResultSet rs = statement.executeQuery("SELECT * FROM users;");

            while (rs.next()) {
                String _login = rs.getString("LOGIN");

                if (_login.equals(login)) {
                    CLog.log_console(login + " :_login_name is true (isExist) ");
                    return true;
                }
            }
            rs.close();
            statement.close();
            c.close();
            System.out.println("Database closed succesfully");

        } catch (Exception ex) {

            ex.printStackTrace();
            System.err.println(ex.getClass().getName() + ": " + ex.getMessage());
            System.exit(0);
        } finally {
            try {
                c.close();
                System.out.println("Database closed succesfully");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

    public boolean isDateExist(String CDate, String login) {
        CLog.log_console("Checking for Start WDay at date: " + CDate + " of user " + login + " for existence in DB");
        Statement statement;

        try {
            c = DriverManager.getConnection(DB_URL);
            c.setAutoCommit(false);
            System.out.println("Database opened succesfully");
            statement = c.createStatement();

            ResultSet rs = statement.executeQuery("SELECT * FROM users WHERE LOGIN= " + formatSqlString(login));

            while (rs.next()) {
                String _CDate = rs.getString("CDATE");
                String _login = rs.getString("LOGIN");

                CLog.log_console("For user"+ _login + " at date: " + _CDate + " result check was opened ");

                if (CDate.equalsIgnoreCase(_CDate) && login.equalsIgnoreCase(_login)) {
                    CLog.log_console("For user" + login + " at date: " + CDate + " : true (isExist) ");
                    return true;

                }
            }
            rs.close();
            statement.close();
            c.close();
            System.out.println("Database closed succesfully");

        } catch (Exception ex) {

            ex.printStackTrace();
            System.err.println(ex.getClass().getName() + ": " + ex.getMessage());
            System.exit(0);
        } finally {
            try {
                c.close();
                System.out.println("Database closed succesfully");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }


    public boolean isDateAndStimeExist(String _CDate, String _STime, String _login) {
        CLog.log_console("*******isDateNStimeExist*******");
        CLog.log_console("Checking for " + _login + " exist at date: " + _CDate + " of time " + _STime + " for existance in DB");
        Statement statement;

        try {
            c = DriverManager.getConnection(DB_URL);
            c.setAutoCommit(false);
            System.out.println("Database opened succesfully");
            statement = c.createStatement();

            ResultSet rs = statement.executeQuery("SELECT * FROM users WHERE LOGIN = " + formatSqlString(_login));

            while (rs.next()) {
                String _CDateRS = rs.getString("CDATE");
                String _STimeRS = rs.getString("STIME");

                CLog.log_console("For time " + _STimeRS + " at date: " + _CDateRS + " USER " + _login + " result check was opened ");

                if (Value_is_null(_STimeRS) & Value_is_null(_CDateRS)) {

                    CLog.log_console("Session for user "+ _login + "STIME" + _STime + " at date: " + _CDate + " : true (isExist) ");
                    return false;
                }
            }
            rs.close();
            statement.close();
            c.close();
            System.out.println("Database closed succesfully");

        } catch (Exception ex) {

            ex.printStackTrace();
            System.err.println(ex.getClass().getName() + ": " + ex.getMessage());
            System.exit(0);
        } finally {
            try {
                c.close();
                System.out.println("Database closed succesfully");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return true;
    }

    public boolean Value_is_null(String value)
    {
        return value == null;
    }

}
/*******************************************************/

/*

    public void load_WTTime(String _CDate, String _login) {

        Statement statement1 = null;
        Statement statement2 = null;
        Statement statement3 = null;
        Statement statement4 = null;
        try {
            c = DriverManager.getConnection(DB_URL);
            c.setAutoCommit(false);
            System.out.println("Database opened succesfully");
            statement1 = c.createStatement();

            ResultSet rs = statement1.executeQuery("SELECT * FROM users;");

            while (rs.next()) {
                int _ID = rs.getInt("ID");
                String _name = rs.getString("LOGIN");
                String _date = rs.getString("CDATE");
                String _STime = rs.getString("STIME");
                String _ETime = rs.getString("ETIME");
                String _Token = rs.getString("TOKEN");

                if (_name.equals(_login) & _date.equals(_CDate)) {

                    String sql = "INSERT INTO wt_request" + " (STime, ETime) " +
                            "VALUES ("+ formatSqlString(_STime) + ", " +
                            formatSqlString(_ETime)  + " )";
                    statement1.executeUpdate(sql);
                    CLog.log_console("Table wt_request was inserted ");
                    return ;
                }
            }
            rs.close();
            statement1.close();
            c.commit();
            System.out.println("Table wt_request was updated succesfully");

            statement4 = c.createStatement();
            ResultSet _rs = statement4.executeQuery("SELECT * FROM wt_request");

            while (rs.next()) {

                String _stime = rs.getString("STime");
                String _etime = rs.getString("ETime");
                int St = Integer.parseInt(_stime);
                int En = Integer.parseInt(_etime);
                int Res = (En-St)/3600;

            }
            CLog.log_console("Table wt_request was inserted ");
            c.close();
            System.out.println("Database closed succesfully");



            /***********************/
   /*         statement2 = c.createStatement();
            statement2.executeUpdate("SELECT * FROM wt_request ORDER BY ID DESK");
            statement2.close();

            CLog.log_console("Table wt_request was ordered ");

            System.out.println("Table wt_request was ordered succesfully");
            */

/***********************/

//  statement3 = c.createStatement();
//    statement2.executeUpdate("A")

/***********************/
/*
} catch (Exception ex) {

        ex.printStackTrace();
        System.err.println(ex.getClass().getName() + ": " + ex.getMessage());
        System.exit(0);
        } finally {
        try {
        c.close();
        System.out.println("Database closed succesfully");
        } catch (SQLException ex) {
        ex.printStackTrace();
        }
        }
        System.out.println(" Records created successfully");
        }




        */











/*    public boolean insertMessage(RChecksum.Pair<String, String> packet, String hash) {
        Statement statement = null;
        try {
            c = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\Alexx\\Documents\\IPZ\\messio_server\\lib" +
                    "/rschat-server/src\\..\\..\\..\\Desktop\\users.s3db");
            c.setAutoCommit(false);
            System.out.println("Database opened succesfully");
            statement = c.createStatement();
            String login = packet.getLeft();
            String message = packet.getRight();


            String sql = "INSERT INTO messages" + " (LOGIN, MESSAGE, HASHCODE) " +
                    "VALUES (" + formatSqlString(login) + ", " + formatSqlString(message) + ", "
                    + formatSqlString(hash) + " )";


            statement.executeUpdate(sql);
            RLog.log_console("message " + message + " from" + login + " was added to database");
            c.commit();
            statement.close();
            c.close();
            RLog.log_console("Database closed succesfully");
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println(ex.getClass().getName() + ": " + ex.getMessage());
            System.exit(0);
        } finally {
            try {
                c.close();
                RLog.log_console("Database closed succesfully");

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

    /**
     * @param token Security Token
     * @return Login of token`s owner
     */
/*
    public String isTokenExist(String token) {
        CLog.log_console("Checking token: " + token);

        Statement statement;
        try {
            c = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\Alexx\\Documents\\IPZ\\messio_server\\lib" +
                    "/rschat-server/src\\..\\..\\..\\Desktop\\users.s3db");
            c.setAutoCommit(false);
            CLog.log_console("Database opened succesfully");
            statement = c.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM users;");
            while (rs.next()) {
                String _token = rs.getString("TOKEN");
                CLog.log_console("Token <" + _token + "> is checking");
                if (_token.equals(token)) {
                    String login = rs.getString("LOGIN");
                    CLog.log_console("Current login : " + login);
                    return login;
                }
            }
            rs.close();
            statement.close();
            c.close();
            System.out.println("Database closed successfully");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println(ex.getClass().getName() + ": " + ex.getMessage());
            System.exit(0);
        } finally {
            try {
                c.close();
                System.out.println("Database closed successfully");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

}


*/
/*    public ArrayList<RChecksum.Pair<String,String>> getAllMessages()
    {
        ArrayList<RChecksum.Pair<String,String>> messagesList = new ArrayList<RChecksum.Pair<String, String>>();

        CLog.log_console("Getting all messages: ");

        Statement statement;
        try {
            c = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\Alexx\\Documents\\IPZ\\messio_server\\lib" +
                    "/rschat-server/src\\..\\..\\..\\Desktop\\users.s3db");
            c.setAutoCommit(false);
            RLog.log_console("Database opened succesfully");
            statement = c.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM messages;");
            while (rs.next())
                messagesList.add(new RChecksum.Pair<String, String>(rs.getString("LOGIN"),rs.getString("MESSAGE")));
            rs.close();
            statement.close();
            c.close();
            RLog.log_console("Database closed successfully");
            return messagesList;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println(ex.getClass().getName() + ": " + ex.getMessage());
            System.exit(0);
        } finally {
            try {
                c.close();
                RLog.log_console("Database closed successfully");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public String getHashOfLastMessage()
    {
        String hash = null;
        Statement statement;
        try {
            c = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\Alexx\\Documents\\IPZ\\messio_server\\lib" +
                    "/rschat-server/src\\..\\..\\..\\Desktop\\users.s3db");
            c.setAutoCommit(false);
            RLog.log_console("Database opened succesfully");
            statement = c.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM messages ORDER BY ID DESC LIMIT 1;");
            hash = rs.getString("HASHCODE");
            rs.close();
            statement.close();
            c.close();
            RLog.log_console("Database closed successfully");
            return hash;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println(ex.getClass().getName() + ": " + ex.getMessage());
            System.exit(0);
        } finally {
            try {
                c.close();
                RLog.log_console("Database closed successfully");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }


    public ArrayList<RChecksum.Pair<String,String>> getMessageSinceHash(String hash)
    {
        ArrayList<RChecksum.Pair<String,String>> messagesList = new ArrayList<RChecksum.Pair<String, String>>();

        RLog.log_console("Getting  messages since hash " + hash);

        Statement statement;
        try {
            c = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\Alexx\\Documents\\IPZ\\messio_server\\lib" +
                    "/rschat-server/src\\..\\..\\..\\Desktop\\users.s3db");
            c.setAutoCommit(false);
            RLog.log_console("Database opened succesfully");
            statement = c.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM messages WHERE HASHCODE BETWEEN \"" + hash + "\" AND " +
                    "\"" + getHashOfLastMessage() + "\";");
            while (rs.next()) {
                if(!hash.equals(rs.getString("HASHCODE")))
                    messagesList.add(new RChecksum.Pair<String, String>(rs.getString("LOGIN"), rs.getString("MESSAGE")));
            }
            rs.close();
            statement.close();
            c.close();
            RLog.log_console("Database closed successfully");
            return messagesList;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println(ex.getClass().getName() + ": " + ex.getMessage());
            System.exit(0);
        } finally {
            try {
                c.close();
                RLog.log_console("Database closed successfully");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return null;
}
*/

    /*
    public boolean insertMessage(Message.Pair<String, String> object) {
        ArrayList<Message.Pair<String, String>> result = new ArrayList<>();
        Statement statement = null;
        try {
            c = DriverManager.getConnection("jdbc:sqlite:C:/Users/rsv/IdeaProjects" +
                    "/rschat-server/src\\..\\..\\..\\Desktop\\users.s3db");
            c.setAutoCommit(false);
            System.out.println("Database opened succesfully");
            statement = c.createStatement();
            String sql = "INSERT INTO messages" + " (login, message) " +
                    "VALUES (" + formatSqlString(object.getLeft()) + ", " + formatSqlString(object.getRight()) + " )";

            statement.executeUpdate(sql);
            statement.close();
            c.commit();
            c.close();
            System.out.println("Database closed succesfully");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println(ex.getClass().getName() + ": " + ex.getMessage());
            System.exit(0);
        } finally {
            try {
                c.close();
                System.out.println("Database closed succesfully");

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        System.out.println(" Records created successfully");
        return true;
    }

    public ArrayList<Message.Pair<String,String>> getAllMessagesHistory()
    {
        Statement statement;
        ArrayList<Message.Pair<String,String>> result = new ArrayList<>();
        try {
            c = DriverManager.getConnection("jdbc:sqlite:C:/Users/rsv/IdeaProjects" +
                    "/rschat-server/src\\..\\..\\..\\Desktop\\users.s3db");
            c.setAutoCommit(false);
            System.out.println("Database opened succesfully");
            statement = c.createStatement();

            ResultSet rs = statement.executeQuery("SELECT * FROM messages;");

            while (rs.next()) {


                String login = rs.getString("login");
                String message = rs.getString("message");
                RLog.log_console("Message is reading from DB. " + login + " " + message);
                result.add(new Message.Pair<String,String>(login,message));
            }
            rs.close();
            statement.close();
            c.close();
            System.out.println("Database closed succesfully");

        } catch (Exception ex) {

            ex.printStackTrace();
            System.err.println(ex.getClass().getName() + ": " + ex.getMessage());
            System.exit(0);
        } finally {
            try {
                c.close();
                System.out.println("Database closed succesfully");

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return result;    }

*/