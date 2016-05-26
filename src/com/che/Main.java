package com.che;

/**
 * Created by Alexx on 13.05.2016.
 */

import com.google.gson.*;
import org.json.JSONObject;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Main {
    public static CLog cLog;

    public static Executor executor;

    public static void main(String[] args) {


        cLog = new CLog();
        executor = Executors.newFixedThreadPool(1000);
        try {
            ServerSocket ss = new ServerSocket(9876);
            cLog.log("Socket created at " + ss.getLocalPort() + " port");
            while (true) {
                Thread.sleep(1);
                Socket socket = ss.accept();
                cLog.log("Client connected from <-- " + socket.getInetAddress().getHostAddress());
                executor.execute(new WortimeConnection(socket));
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

    }

//
//      WortimeObject.GetMessageResponse response = new WortimeObject.GetMessageResponse();
//      response.setHash(new SessionIdentifierGenerator().generateString());
//      response.setType(WortimeObject.GETALLMSG_RESPONSE);
//      response.setResult(true);
//      response.setInfo("Pizdarikiy");
//      Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().serializeNulls()
//              .create();
//      System.out.println(gson.toJson(response));
//


//  // outputStream.writeUTF(gson.toJson(databaseManager.getAllUsers()));      to klient



    static class  WortimeConnection implements Runnable
    {
        private Socket client;

        public WortimeConnection(Socket client)
        {
            this.client = client;
        }

        public void run()
        {

            try {
                DataOutputStream outputStream = new DataOutputStream(client.getOutputStream());
                outputStream.flush();
                DataInputStream inputStream = new DataInputStream(client.getInputStream());
                cLog.log("Streams are opened to " + client.getInetAddress().getHostAddress());
                DatabaseManager databaseManager = new DatabaseManager();
                Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting()
                        .serializeNulls().create();

                while(true) {
                    String request = inputStream.readUTF();
                    cLog.log("Client " + client.getInetAddress().getHostAddress() + " send request");
                    JSONObject jsonObject = new JSONObject(request);
                    String type = jsonObject.getString("type");
                    cLog.log("Type of request - " + type);



                    if (type.equals(WortimeObject.AUTH_REQUEST)) {

                        String login = jsonObject.getString("login");
                        String password = jsonObject.getString("password");

                        if (!login.equals(null) & !password.equals(null)) {
                            if (databaseManager.isExist(login))
                            {
                                if (!databaseManager.isSessionFin(login)) { //session fin/null, start new

                                    WortimeObject.AuthResponse response = new WortimeObject.AuthResponse();
                                    response.setType(WortimeObject.AUTH_RESPONSE);
                                    response.setInfo("User was authorised successfully");
                                    response.setResult(true);
                                    String jsResponse = gson.toJson(response);
                                    outputStream.writeUTF(jsResponse);
                                    cLog.log("User was authorised successfully");
                                    outputStream.flush();
                                }

                            } else {
                                WortimeObject.AuthResponse response = new WortimeObject.AuthResponse();
                                response.setType(WortimeObject.AUTH_RESPONSE);
                                response.setInfo("User wasn`t found. Check registration`s data.");
                                response.setResult(false);
                                String jsResponse = gson.toJson(response);
                                outputStream.writeUTF(jsResponse);
                                cLog.log("User wasn`t found. Check registration`s data.");
                                outputStream.flush();
                            }
                        } else {
                            WortimeObject.AuthResponse response = new WortimeObject.AuthResponse();
                            response.setType(WortimeObject.AUTH_RESPONSE);
                            response.setInfo("Login and password fields must be valid and non-zero");
                            response.setResult(false);
                            String jsResponse = gson.toJson(response);
                            outputStream.writeUTF(jsResponse);
                            cLog.log("Login and password fields must be valid and non-zero");
                            outputStream.flush();
                        }

                    } else if (type.equals(WortimeObject.REG_REQUEST)) {
                        String login = jsonObject.getString("login");
                        String password = jsonObject.getString("password");

                        if (!login.equals(null) & !password.equals(null)) {
                            if (!databaseManager.isExist(login)) {    // do if _login doesnt exist in DB
                                databaseManager.insert(login, password);
                                cLog.log("User " + login + " was registered");
                                WortimeObject.RegResponse response = new WortimeObject.RegResponse();
                                response.setInfo("Registered success!!!");
                                response.setResult(true);
                                response.setType(WortimeObject.REG_RESPONSE);
                                String jsResponse = gson.toJson(response);
                                outputStream.writeUTF(jsResponse);
                                outputStream.flush();
                            } else {
                                if (!databaseManager.isSessionFin(login)) { //false - not fin,

                                    cLog.log("User " + login + " wasn`t registered, because Previous session is **NOT** finished!!!");
                                    cLog.log("PLEASE, AUTHORISE TO FINISH PREVIOUS SESSION");
                                    WortimeObject.RegResponse response = new WortimeObject.RegResponse();
                                    response.setInfo("User was already registered, session in progress");
                                    response.setResult(false);
                                    response.setType(WortimeObject.REG_RESPONSE);
                                    String jsResponse = gson.toJson(response);
                                    outputStream.writeUTF(jsResponse);
                                    outputStream.flush();
                                } else {                                        //true - fin
                                    databaseManager.insert(login, password);
                                    cLog.log("User " + login + " was registered");
                                    WortimeObject.RegResponse response = new WortimeObject.RegResponse();
                                    response.setInfo("Registration success!!!");
                                    response.setResult(true);
                                    response.setType(WortimeObject.REG_RESPONSE);
                                    String jsResponse = gson.toJson(response);
                                    outputStream.writeUTF(jsResponse);
                                    outputStream.flush();
                                }
                            } if (login.equals(null) & password.equals(null)) {
                                WortimeObject.RegResponse response = new WortimeObject.RegResponse();
                                response.setInfo("Login and password fields must be valid and non-zero");
                                response.setResult(false);
                                response.setType(WortimeObject.REG_RESPONSE);
                                String jsResponse = gson.toJson(response);
                                outputStream.writeUTF(jsResponse);
                                outputStream.flush();
                            }
                        }
                                /****LAGGING SD****/
                    } else if (type.equals(WortimeObject.SD_REQUEST)) {
                        String login = jsonObject.getString("login");
                        String STime = jsonObject.getString("STime");
                        String CDate = jsonObject.getString("CDate");
                        String ID_max_cur = databaseManager.ID_max_Users();
                        String Token_cur = databaseManager.getLast_C_Token_Users(login, ID_max_cur);
                        cLog.log("get from UI login= " + login + " STime= " + STime + " CDate= " + CDate + " were received");

                                if (!login.equals(null) & !STime.equals(null) & !CDate.equals(null)){
//                            if (!databaseManager.isDateAndStimeExist(CDate, STime, login)) {
                                    if (!databaseManager.isDateExist(CDate, login)) {

                                        cLog.log("ID_max_cur= "+ID_max_cur);

                                CLog.log_console("current token is = "+Token_cur+ " start func insert_stime");

                                databaseManager.insert_STime(STime, CDate, login, Token_cur);
                                cLog.log("Users_start_day " + login + " STime " + STime + " CDate " + CDate + " were added");

                                WortimeObject.SDResponse response = new WortimeObject.SDResponse();

                                response.setInfo("Start of WorkDay was successful!!!");
                                response.setResult(true);
                                response.setType(WortimeObject.SD_RESPONSE);
                                String jsResponse = gson.toJson(response);
                                outputStream.writeUTF(jsResponse);
                                outputStream.flush();
                            } else {
                                cLog.log("Users_start_day " + login + " STime " + STime + " CDate " + CDate + " wasn`t added");
                                WortimeObject.SDResponse response = new WortimeObject.SDResponse();
                                response.setInfo("User already started WorkDay");
                                response.setResult(false);
                                response.setType(WortimeObject.SD_RESPONSE);
                                String jsResponse = gson.toJson(response);
                                outputStream.writeUTF(jsResponse);
                                outputStream.flush();
                            }
                        }

                        //!!!!Next block is useless, but i`m afraid to delete it
                        else {
                            WortimeObject.SDResponse response = new WortimeObject.SDResponse();
                            response.setInfo("Login and password fields must be valid and non-zero");
                            response.setResult(false);
                            response.setType(WortimeObject.SD_RESPONSE);
                            String jsResponse = gson.toJson(response);
                            outputStream.writeUTF(jsResponse);
                            outputStream.flush();
                        }
                    } else if (type.equals(WortimeObject.ED_REQUEST)) {
                        String login = jsonObject.getString("login");
                        String ETime = jsonObject.getString("ETime");
                        String CDate = jsonObject.getString("CDate");

                        cLog.log("get from UI login= " + login + " ETime= " + ETime + " CDate= " + CDate + " were received");

                        if (!login.equals(null) & !ETime.equals(null) & !CDate.equals(null)) {
                            if (databaseManager.isDateExist(CDate, login)) {                        //IF DATE EXIST, INSERT, ELSE - BREAK
                                databaseManager.insert_ETime(ETime, CDate, login);
                                cLog.log("Users_end_day for user= " + login + " ETime= " + ETime + " CDate= " + CDate + " were added");

                                WortimeObject.EDResponse response = new WortimeObject.EDResponse();
                                response.setInfo("End of WorkDay was successful!!!");
                                response.setResult(true);
                                response.setType(WortimeObject.ED_RESPONSE);
                                String jsResponse = gson.toJson(response);
                                outputStream.writeUTF(jsResponse);
                                outputStream.flush();
                            } else {
                                cLog.log("Users_end_day " + login + " ETime " + ETime + " CDate " + CDate + " wasn`t added");
                                WortimeObject.EDResponse response = new WortimeObject.EDResponse();
                                response.setInfo("User already ended WorkDay");
                                response.setResult(false);
                                response.setType(WortimeObject.ED_RESPONSE);
                                String jsResponse = gson.toJson(response);
                                outputStream.writeUTF(jsResponse);
                                outputStream.flush();
                            }
                        }

                        //!!!!Next block is useless, but i`m afraid to delete it
                        else {
                            WortimeObject.EDResponse response = new WortimeObject.EDResponse();
                            response.setInfo("Login and password fields must be valid and non-zero");
                            response.setResult(false);
                            response.setType(WortimeObject.ED_RESPONSE);
                            String jsResponse = gson.toJson(response);
                            outputStream.writeUTF(jsResponse);
                            outputStream.flush();
                        }
                    }


                    else if (type.equals(WortimeObject.DOC_REQUEST)) {
                        String login = jsonObject.getString("login");
                        String DOC = databaseManager.GetDoc(login);
                        CLog.log_console("********DOC = " + DOC + "**********");
                        WortimeObject.DOCResponse response = new WortimeObject.DOCResponse();
                        response.setInfo("DOC was loaded!!!");
                        response.setResult(DOC);
                        response.setResult(true);
                        response.setType(WortimeObject.DOC_RESPONSE);
                        String jsResponse = gson.toJson(response);
                        outputStream.writeUTF(jsResponse);
                        outputStream.flush();
                    }

                    else if (type.equals(WortimeObject.WT_REQUEST))  {
                        String login = jsonObject.getString("login");
                        if (!login.equals(null)) {
                            if (databaseManager.isWT_Available(login))
                            {
                                String WTime = databaseManager.GetWThours(login);
                                CLog.log_console("********worked time = " + WTime + "**********");
                                WortimeObject.WTResponse response = new WortimeObject.WTResponse();
                                response.setInfo("Wtime was calculated!!!");
                                response.setResult(WTime);
                                response.setResult(true);
                                response.setType(WortimeObject.WT_RESPONSE);
                                String jsResponse = gson.toJson(response);
                                outputStream.writeUTF(jsResponse);
                                outputStream.flush();
                            } else {
                                cLog.log("WTIME doesnt exist -- User doesnt exist");
                                WortimeObject.WTResponse response = new WortimeObject.WTResponse();
                                response.setInfo("WTIME doesnt exist ** User dont exist");
                                response.setResult(false);
                                response.setType(WortimeObject.WT_RESPONSE);
                                String jsResponse = gson.toJson(response);
                                outputStream.writeUTF(jsResponse);
                                outputStream.flush();
                            }
                        }

                        //!!!!Next block is useless, but i`m afraid to delete it
                        else {
                            WortimeObject.WTResponse response = new WortimeObject.WTResponse();
                            response.setInfo("Login and password fields must be valid and non-zero");
                            response.setResult(false);
                            response.setType(WortimeObject.WT_RESPONSE);
                            String jsResponse = gson.toJson(response);
                            outputStream.writeUTF(jsResponse);
                            outputStream.flush();
                        }
                    }

//                    else if (type.equals(WortimeObject.DOC_REQUEST)) {
//                        String login = jsonObject.getString("login");
//                        String DOC = databaseManager.GetDoc(login);
//                        CLog.log_console("********DOC = " + DOC + "**********");
//                        WortimeObject.DOCResponse response = new WortimeObject.DOCResponse();
//                        response.setInfo("DOC was loaded!!!");
//                        response.setResult(DOC);
//                        response.setResult(true);
//                        response.setType(WortimeObject.DOC_RESPONSE);
//                        String jsResponse = gson.toJson(response);
//                        outputStream.writeUTF(jsResponse);
//                        outputStream.flush();
//                    }




                    /*
                    else if(type.equals(MessioObject.GETALLMSG_REQUEST))
                    {
                        String token = jsonObject.getString("token");
                        if(databaseManager.isTokenExist(token).equals(null))
                        {
                            MessioObject.GetMessageResponse response = new MessioObject.GetMessageResponse();
                            response.setInfo("Token is invalid");
                            response.setResult(false);
                            response.setType(MessioObject.GETALLMSG_RESPONSE);
                            outputStream.writeUTF(gson.toJson(response));
                            outputStream.flush();
                        }
                        else
                        {
                            MessioObject.GetMessageResponse response = new MessioObject.GetMessageResponse();
                            response.setType(MessioObject.GETALLMSG_RESPONSE);
                            response.setInfo("");
                            response.setResult(true);
                            response.setMessagesList(databaseManager.getAllMessages());
                            response.setHash(databaseManager.getHashOfLastMessage());
                            outputStream.writeUTF(gson.toJson(response));
                            outputStream.flush();
                        }
                    }
                    else if(type.equals(MessioObject.GETMSGSNCHASH_REQUEST))
                    {
                        String token = jsonObject.getString("token");
                        String hash = jsonObject.getString("hash");

                        if(databaseManager.isTokenExist(token).equals(null) |
                                hash.equals(null))
                        {
                            MessioObject.GetMessageResponse response = new MessioObject.GetMessageResponse();
                            response.setInfo("Token and hash must be valid.");
                            response.setResult(false);
                            response.setType(MessioObject.GETMSGSNCHASH_RESPONSE);
                            outputStream.writeUTF(gson.toJson(response));
                            outputStream.flush();
                        }

                        else
                        {
                            MessioObject.GetMessageResponse response = new MessioObject.GetMessageResponse();
                            response.setInfo("");
                            response.setHash(databaseManager.getHashOfLastMessage());
                            response.setResult(true);
                            response.setType(MessioObject.GETMSGSNCHASH_RESPONSE);
                            response.setMessagesList(databaseManager.getMessageSinceHash(hash));
                            outputStream.writeUTF(gson.toJson(response));
                            outputStream.flush();

                        }
                    }
                    */


                    /*
                    else if(type.equals(MessioObject.SENDMSG_REQUEST))
                    {
                        String token = jsonObject.getString("token");
                        String hash = jsonObject.getString("hash");
                        String message = jsonObject.getString("message");

                        if(databaseManager.isTokenExist(token).equals(null)|token.equals(null)
                                | hash.equals(null))
                        {
                            MessioObject.SendMessageResponse response = new MessioObject.SendMessageResponse();
                            response.setInfo("Data ( token and hash ) must be valid and non-null. Token can be invalid");
                            response.setResult(false);
                            response.setType(MessioObject.SENDMSG_RESPONSE);
                            outputStream.writeUTF(gson.toJson(response));
                            outputStream.flush();
                        }
                        else
                        {

                            databaseManager.insertMessage(
                                    new RChecksum.Pair<String, String>(databaseManager.isTokenExist(token),
                                            message),hash);

                            MessioObject.SendMessageResponse response = new MessioObject.SendMessageResponse();
                            response.setInfo("");
                            response.setResult(true);
                            response.setType(MessioObject.SENDMSG_RESPONSE);
                            outputStream.writeUTF(gson.toJson(response));
                            outputStream.flush();
                        }
                    }

                    else
                    {
                        MessioObject.Response response = new MessioObject.Response();
                        response.setType(MessioObject.INFOMESSAGE);
                        response.setInfo("Invalid request");
                        response.setResult(false);
                        outputStream.writeUTF(gson.toJson(response));
                        outputStream.flush();
                    }

                }

*/

                }
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }

        }
    }
}


