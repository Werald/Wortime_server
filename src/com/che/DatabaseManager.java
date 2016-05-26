package com.che;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;
import java.sql.SQLException;
import java.sql.*;

/**
 * Created by Alexx on 14.05.2016.
 */

public class DatabaseManager {

    private Connection c;
    private Connection d;

    private static final String DB_URL = "jdbc:sqlite:D:\\Projects_program\\IPZ\\Wortime_server\\DB_WTS.s3db";

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
    public void insert(String _login, String _password) {
       CLog.log_console( "**********!!      insert(String _login, String _password)     !!**********");
        Statement statement = null;
                    //new session
            try {
                c = DriverManager.getConnection(DB_URL);
                c.setAutoCommit(false);
                System.out.println("Database opened successfully");
                statement = c.createStatement();
                String sql = "INSERT INTO users" + " (LOGIN, PASSWORD, TOKEN) " +
                "VALUES (" + formatSqlString(_login) + ", " + formatSqlString(_password) + ", " + formatSqlString(new SessionIdentifierGenerator().generateString()) + " )";
                statement.executeUpdate(sql);
                CLog.log_console("login: " + _login + " password: " + _password + " and his parameters were added to database");
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
        }                       //working, commit

    public void insert_STime(String _STime, String _CDate, String _login, String _Token) {
        CLog.log_console("*******insert_STARTTime*******");
        Statement statement = null;
        CLog.log_console("Token= " + _Token + "for login " + _login);

            try {
                c = DriverManager.getConnection(DB_URL);
                c.setAutoCommit(false);
                System.out.println("Database opened succesfully");
                statement = c.createStatement();
               String sql = "UPDATE users SET CDATE=" + formatSqlString(_CDate) + ", STIME=" + formatSqlString(_STime) +
                       " WHERE TOKEN=" + formatSqlString(_Token);
                statement.executeUpdate(sql);
                c.commit();
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
            System.out.println(" Records created successfully");
    }     //changed

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

    public void insert_WTime(String _login) {
        Statement statement = null;
        //new session
        try {
            c = DriverManager.getConnection(DB_URL);
            c.setAutoCommit(false);
            System.out.println("Database opened successfully");
            statement = c.createStatement();
            String _WTime = GetWThours(_login);
            if (Value_is_null(_WTime)) {
                CLog.log_console("WTime is empty");
            } else  {
            String sql = "INSERT INTO users_wt (WTIME) VALUES ("+formatSqlString(_WTime)+")";
            statement.executeUpdate(sql);}
            CLog.log_console("login: " + _login + " WTime: " + _WTime + "  were added to database");
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

        System.out.println(" Records created successfully");
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
            CLog.log_console("wt_users table was succsessfully cleared!!!");

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
            CLog.log_console("wt_users table was succsessfully updated frop table users!!!");

            /*****  3-rd statement ****/
            c = DriverManager.getConnection(DB_URL);
            c.setAutoCommit(false);
            statement = c.createStatement();
            System.out.println("Database is opened successfully");
            String sql3 = "SELECT * FROM users_wt";
            ResultSet rs = statement.executeQuery(sql3);
            CLog.log_console("ALL data was succsessfully SELECTED from table wt_users!!!");
            String WTime = null;
            int i = 0;
            long result = 0;
            while (rs.next()) {
                String STime = rs.getString("STIME");
                String ETime = rs.getString("ETIME");
                WTime =  wtimerah(STime, ETime);
                result += Long.parseLong(WTime);
                System.out.println("New Wtime " + WTime + " for user_name " + login + " i= " + i++);
            }
            statement.close();
            c.commit();
            c.close();
            System.out.println("Database closed succesfully");
            CLog.log_console("ALL WTIME was succsessfully CALKED from wt_users!!!");
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
        CLog.log_console("*******      isSessionFin         ******* ");
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
    }                       //perfect

//last token
    public String getLast_C_Token(String _login)   {
        Statement statement;
        String _ID_max_STR_tok;
        try  {

            /******* 1-st statement *********/
            c = DriverManager.getConnection(DB_URL);
            c.setAutoCommit(false);
            statement = c.createStatement();
            String sql1 = "DELETE FROM toktable";
            statement.executeUpdate(sql1);
            statement.close();
            c.close();
            c.commit();
            CLog.log_console("token_table was cleared!!!");
 //           deleteTOKtable(_login); CLog.log_console("deletok in funkTOK executed");

            /******* 2-nd statement *********/
    //        insertTOKtable(_login); CLog.log_console("insertok in funkTOK executed");
            c = DriverManager.getConnection(DB_URL);
            c.setAutoCommit(false);
            statement = c.createStatement();

            String sql2 = "INSERT INTO toktable (ID, LOGIN, TOKEN) SELECT ID, LOGIN, TOKEN FROM users " +
                    "WHERE LOGIN=" + formatSqlString(_login);
            statement.executeUpdate(sql2);
            statement.close();
            c.close();
            c.commit();
            CLog.log_console("toktable table was succsessfully updated from table_users!");

            /*****  3-rd statement ****/
            c = DriverManager.getConnection(DB_URL);
            c.setAutoCommit(false);
            statement = c.createStatement();


            _ID_max_STR_tok = ID_max_Toktable();
            if (Value_is_null(_ID_max_STR_tok)) {
                CLog.log_console("ID_max_Users = 0, user dont exist");
                return null;
            }
            int _Id_max_INT_tok = Integer.parseInt(_ID_max_STR_tok);
          //  String sql3 = "SELECT * FROM users_wt WHERE ID=" + _Id_max_INT_tok;
            String sql3 = "SELECT * FROM toktable WHERE ID=" + _Id_max_INT_tok;
            ResultSet rs = statement.executeQuery(sql3);
            CLog.log_console("Data loaded for ID= " + _Id_max_INT_tok + " and login: " + _login);
            String Login = rs.getString("LOGIN");
            String Token = rs.getString("TOKEN");
            if (!Token.equals(_ID_max_STR_tok)){
                return Token;
            }
            System.out.println("recent token for user_name " + Login + " was delivered: token=" + Token);
            statement.close();
            c.close();
            c.commit();
            System.out.println("Database closed succesfully");
            CLog.log_console("Token was succsessfully delivered from toktable");

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

    public String getLast_C_Token_Users(String _login, String _Id_max)   {
        CLog.log_console("*******getLast_C_Token_Users*******");
        Statement statement;
        String _ID_max_STR_tok = null;
        String _Token;
        try  {

            /******* 1-st statement *********/System.out.println("******* 1-t statement *********");
            c = DriverManager.getConnection(DB_URL);
            c.setAutoCommit(false);
            statement = c.createStatement();
            System.out.println("Database is opened successfully");
            String sql1 = "DELETE FROM toktable";
            statement.executeUpdate(sql1);
            statement.close();
            c.commit();
            c.close();
            System.out.println("Database closed succesfully");
            CLog.log_console("toktable table was succsessfully cleared!!!");

            /******* 2-nd statement *********/System.out.println("******* 2-nd statement *********");


            c = DriverManager.getConnection(DB_URL);
            c.setAutoCommit(false);
            statement = c.createStatement();
            System.out.println("Database is opened successfully");
            String sql2 = "INSERT INTO toktable (ID, LOGIN, TOKEN) SELECT ID, LOGIN, TOKEN FROM users " +
                    "WHERE LOGIN=" + formatSqlString(_login);
            statement.executeUpdate(sql2);
            statement.close();
            c.commit();
            c.close();
            System.out.println("Database closed succesfully");
            CLog.log_console("toktable table was succsessfully updated from table_users!!!");


            /*****  3-rd statement ****/System.out.println("******* 3-d statement *********");
            c = DriverManager.getConnection(DB_URL);
            c.setAutoCommit(false);
            statement = c.createStatement();
            System.out.println("Database is opened successfully");

            int _Id_max_INT_tok = Integer.parseInt(_Id_max);
            String sql3 = "SELECT * FROM toktable WHERE ID=" + _Id_max_INT_tok;
            CLog.log_console("Data loaded for ID= " + _Id_max_INT_tok);

            ResultSet rs1 = statement.executeQuery(sql3);
            String Token = rs1.getString("TOKEN");
            System.out.println("current token for user_name " + _login + " was delivered: token= " + Token);
            statement.close();
            c.commit();
            c.close();
            System.out.println("Database closed succesfully");
            CLog.log_console("Token was succsessfully delivered from toktable");
            return Token;
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
        CLog.log_console("Recent token was returned: null" );
        return null;

    }  // working

    public String GetDoc(String _login) {
        Statement statement = null;
        try {
            c = DriverManager.getConnection(DB_URL);
            c.setAutoCommit(false);
            statement = c.createStatement();
            System.out.println("Database is opened successfully");
            String sql3 = "SELECT * FROM doc_box";
            ResultSet rs = statement.executeQuery(sql3);
            String Doc = rs.getString("DOCFILE");
            statement.close();
            c.close();
            System.out.println("Database closed succesfully");
            insert_WTime(_login);
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
    }   //working

    public void deleteTOKtable(String _login) {
        Statement statement = null;
        //new session
        try {
            c = DriverManager.getConnection(DB_URL);
            c.setAutoCommit(false);
            statement = c.createStatement();
            String sql = "DELETE FROM users_wt";
          //String sql = "DELETE FROM toktable";
            statement.executeUpdate(sql);
            CLog.log_console("table //TOKENSTABLE//    !USERS_WT!      was cleared");
            statement.close();




        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println(ex.getClass().getName() + ": " + ex.getMessage());
            System.exit(0);
        } finally {
            try {
                c.close();


            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        System.out.println("successfully");
    }

    public void insertTOKtable(String _login) {
        Statement statement = null;
        //new session
        try {
            c = DriverManager.getConnection(DB_URL);
            c.setAutoCommit(false);
            statement = c.createStatement();

            String sql2 = "INSERT INTO users_wt (ID, LOGIN, TOKEN) SELECT ID, LOGIN, TOKEN FROM users " +
                    "WHERE LOGIN=" + formatSqlString(_login);
          //String sql2 = "INSERT INTO toktable (ID, LOGIN, TOKEN) SELECT ID, LOGIN, TOKEN FROM users " +
          //  "WHERE LOGIN=" + formatSqlString(_login);
            statement.executeUpdate(sql2);
            CLog.log_console("toktable       table !!!USERS_WT!!! was succsessfully updated from table_users!");
            statement.close();
            c.close();


        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println(ex.getClass().getName() + ": " + ex.getMessage());
            System.exit(0);
        } finally {
            try {
                c.close();


            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public String ID_max_Toktable()    {
        CLog.log_console("*******ID_max_Toktable*******");
        Statement statement;
        try  {
            c = DriverManager.getConnection(DB_URL);
            c.setAutoCommit(false);
            System.out.println("Database opened succesfully");
            statement = c.createStatement();

            //ResultSet rs = statement.executeQuery("SELECT MAX(ID) FROM users_wt");
            ResultSet rs = statement.executeQuery("SELECT MAX(ID) FROM toktable");
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

    public String ID_max_Users()    {
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




/*********************** Is Exist Checks   **************************************/

    public boolean isWT_Available(String _login) {
            Statement statement;

            try {
                c = DriverManager.getConnection(DB_URL);
                c.setAutoCommit(false);
                System.out.println("Database opened succesfully");
                statement = c.createStatement();

                ResultSet rs = statement.executeQuery("SELECT * FROM users WHERE LOGIN=" + formatSqlString(_login));

                while (rs.next()) {
                    String _etime = rs.getString("ETIME");
                    if (Value_is_null(_etime))
                        return false;
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

    public boolean isExist(String login) {
        CLog.log_console("*******        isExist(String login)       *******");
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
    }    //working

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

                CLog.log_console("For user"+ _login + " at DBdate: " + _CDate + " and UIdate: "+CDate+" existance check was opened ");

                if (CDate.equalsIgnoreCase(_CDate) && login.equalsIgnoreCase(_login)) {
                    CLog.log_console("For user" + login + " at date: " + CDate + " : true (isDateExist) ");
                    CLog.log_console("User allready EXIST in base for such date");
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
        CLog.log_console("For user" + login + " at date: " + CDate + " : false (dontExist) ");
        CLog.log_console("User DONT EXIST in base for such date");
        return false;
    }       //50/50б t/f in end??  -- crash for 2-nd reg

    public boolean isDate__STime_Exist(String CDate, String login) {
        CLog.log_console("Checking for Start WDay at date: " + CDate + " of user " + login + " for existence in DB");
        Statement statement;

        try {
            c = DriverManager.getConnection(DB_URL);
            c.setAutoCommit(false);
            System.out.println("Database opened succesfully");
            statement = c.createStatement();

            ResultSet rs = statement.executeQuery("SELECT * FROM users WHERE LOGIN= " + formatSqlString(login));
            int i =0;
            while (rs.next()) {
                String _CDate = rs.getString("CDATE");
                String _login = rs.getString("LOGIN");

                CLog.log_console("For user"+ _login + " at date: " + _CDate + " DATE & TIME check_loop was opened, i= "+i++);

                if (CDate.equalsIgnoreCase(_CDate) && login.equalsIgnoreCase(_login)) {
                    CLog.log_console("For user" + login + " at date: " + CDate + " : true (isDateExist) ");
                    CLog.log_console("User allready EXIST in base for such date");
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
        CLog.log_console("For user" + login + " at date: " + CDate + " : false (dontExist) ");
        CLog.log_console("User DONT EXIST in base for such date");
        return false;
    }

//    public boolean isDateAndStimeExist(String _CDate, String _STime, String _login) {
//        CLog.log_console("*******isDateNStimeExist*******");
//        CLog.log_console("Checking for " + _login + " exist at date: " + _CDate + " of time " + _STime + " for existance in DB");
//        Statement statement;
//
//
//        try {
//            c = DriverManager.getConnection(DB_URL);
//            c.setAutoCommit(false);
//            System.out.println("Database opened succesfully");
//            statement = c.createStatement();
//            String sql = "SELECT * FROM users WHERE LOGIN=" + formatSqlString(_login);
//            ResultSet rs = statement.executeQuery(sql);
//            int i = 0;
//
//             while (rs.next()) {
//                String _CDateRS = rs.getString("CDATE");
//                String _STimeRS = rs.getString("STIME");
//
//                CLog.log_console("For time " + _STimeRS + " at date: " + _CDateRS + " USER " + _login + " i= " + i++);
//
//                if (Value_is_null(_CDateRS) && Value_is_null(_STimeRS)) break;
//
//
//
//            }
//            CLog.log_console("CONNECTION WASN`t COMMITED!!");
//
//            rs.close();
//            statement.close();
//            c.close();
//
//            System.out.println("Database closed succesfully");
//
//            return false;
//        } catch (Exception ex) {
//
//            ex.printStackTrace();
//            System.err.println(ex.getClass().getName() + ": " + ex.getMessage());
//            System.exit(0);
//        } finally {
//            try {
//                c.close();
//                System.out.println("Database closed succesfully");
//            } catch (SQLException ex) {
//                ex.printStackTrace();
//            }
//        }
//
//        CLog.log_console("date and time exist for login: " + _login);
//        return true;
//    }

    public boolean Value_is_null(String value)
    {
        return value == null;
    }


}
/*******************************************************/

