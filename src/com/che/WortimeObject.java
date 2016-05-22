package com.che;

import java.util.ArrayList;

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

        public String setResult(String result) {
            return result;
        }

        public boolean isResult() {
            return result;
        }

        public void setResult(boolean result) {
            this.result = result;
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
        WTResponse() { }
    }
    public static class DOCResponse extends Response
    {
        DOCResponse() { }
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