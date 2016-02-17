/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.TreeMap;

/**
 *
 * @author butwhole
 */
public class ChatServer {

    static String ip;
    static int port;
    java.net.ServerSocket serverSock;
    private TreeMap<String,Socket> tm = new TreeMap();       
    
    
    
    public void startServer() throws IOException{
        serverSock = new ServerSocket();
        serverSock.bind(new InetSocketAddress(ip,port));
        System.out.println("server started, listening on port: "+port);
        while(true){
            java.net.Socket socket = serverSock.accept();//Remember Blocking Call
            ChatThread st = new ChatThread(tm,socket);
            
            st.start();
//            java.io.OutputStream os = socket.getOutputStream();
//            java.io.PrintWriter pw = new java.io.PrintWriter(os);
//            pw.println(new java.util.Date().toString());
//            pw.flush();
        }
    }
    
    public static void main(String[] args) throws IOException {
        port = 8088;
        ip = "localhost";
        
        new ChatServer().startServer();
        
    }
}