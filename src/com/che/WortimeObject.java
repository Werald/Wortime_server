package com.che;

/**
 * Created by Alexx on 14.05.2016.
 */

public class WortimeObject {

    public static class MMessage
    {
        public String type;

        public MMessage() { }
        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }


    public static class Response extends MMessage
    {
        private String info;
        private boolean result;

        Response() { }
        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public boolean isResult() {
            return result;
        }

        public void setResult(boolean result) {
            this.result = result;
        }
    }

    public static class SdRequest extends MMessage
    {
        private String login;
        private String STime;
        private String CDate;


        public String getcDate() {
            return CDate;
        }

        public void setcDate(String cDate) {
            this.CDate = cDate;
        }

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public String getSTime() {
            return STime;
        }

        public void setSTime(String STime) {
            this.STime = STime;
        }
    }

    public static class EdRequest extends MMessage
    {
        private String login;
        private String ETime;
        private String CDate;


        public String getcDate() {
            return CDate;
        }

        public void setcDate(String cDate) {
            this.CDate = cDate;
        }

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public String getETime() {
            return ETime;
        }

        public void setETime(String ETime) {
            this.ETime = ETime;
        }
    }

    public static class RegAuthRequest extends MMessage
    {
        private String login;
        private String password;

        public RegAuthRequest() { }

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class WTRequest extends MMessage
    {
        private String login;


        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }
    }
    public static class DOCRequest extends MMessage
    {
        private String login;
        public void setLogin(String login) {
            this.login = login;
        }

    }


    public static class AuthResponse extends Response
    {
        AuthResponse() { }
    }

    public static class RegResponse extends Response
    {
        RegResponse() { }
    }

    public static class SDResponse extends Response
    {
        SDResponse() { }
    }

    public static class EDResponse extends Response
    {
        EDResponse() { }
    }
    public static class WTResponse extends Response
    {
        private String result;

        WTResponse() { }
        public String getResult() {
            return result;
        }
        public void setResult(String result) {
            this.result = result;
        }
    }
    public static class DOCResponse extends Response
    {
        private String result;

        DOCResponse() { }
        public String getResult() {
            return result;
        }
        public void setResult(String result) {
            this.result = result;
        }
    }
    //   public int GetWThours()    {   return Wthours;    }

/*
    public static class GetMessageResponse extends Response
    {
        private String hash;
        private ArrayList<DatabaseManager<String,String>> messagesList;

        public GetMessageResponse() { }

        public String getHash() {
            return hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        public ArrayList<RChecksum.Pair<String, String>> getMessagesList() {
            return messagesList;
        }

        public void setMessagesList(ArrayList<RChecksum.Pair<String, String>> messagesList) {
            this.messagesList = messagesList;
        }
    }

//   public void GetWThours;

//   public ArrayList<String> GetWThours();

//   public void setWTHours(ArrayList<String> gainedHours)




*/

  /*       public static class GetWTResponse extends Response
         {

              private ArrayList<RChecksum.Pair<String,String>> messagesList;

            public GetMessageResponse() { }

          public String getHash() {
           return hash;
        }
*/


    public final static String AUTH_REQUEST = "auth_request";
    public final static String REG_REQUEST = "reg_request";
    public final static String SD_REQUEST = "sd_request";
    public final static String ED_REQUEST = "ed_request";
    public final static String WT_REQUEST = "wt_request";
    public final static String DOC_REQUEST = "doc_request";

    public final static String AUTH_RESPONSE = "auth_request";
    public final static String REG_RESPONSE = "reg_request";
    public final static String SD_RESPONSE = "sd_request";
    public final static String ED_RESPONSE = "ed_request";
    public final static String WT_RESPONSE = "wt_request";
    public final static String DOC_RESPONSE = "doc_request";


}

/*

        public void setHash(String hash) {
            this.hash = hash;
        }

        public ArrayList<RChecksum.Pair<String, String>> getMessagesList() {
            return messagesList;
        }

        public void setMessagesList(ArrayList<RChecksum.Pair<String, String>> messagesList) {
            this.messagesList = messagesList;
        }

    }
    */