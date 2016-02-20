/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author butwhole
 */
public class ChatThread extends Thread {

    private Socket s;
    private PrintWriter pw;
    private Scanner scan;
    private TreeMap tm;
    private String[] list;
    private String localName;

    // duplicate name entry
    public boolean UserName() throws IOException {
        try {
            String line = scan.nextLine();
            list = line.split("#");
            if (list[0].toLowerCase().equals("user") && list.length == 2 && tm.get(list[1]) == null) {
                tm.put(list[1], s);
                localName = list[1];
                Logger.getLogger(Log.LOG_NAME).log(Level.INFO,("User connected: " + localName));
                Logger.getLogger(Log.LOG_NAME).log(Level.INFO,("Users online: " + makeUsers()));
                Update(makeUsers());
                return true;
            } else {
                Logger.getLogger(Log.LOG_NAME).log(Level.SEVERE,"Connection failed");
                return false;
            }
        } catch (java.lang.ArrayIndexOutOfBoundsException o) {
            Logger.getLogger(Log.LOG_NAME).log(Level.SEVERE,"Connection failed");
            return false;
        }

    }

    public String makeUsers() {
        Iterator it = tm.entrySet().iterator();
        String users = "";
        while (it.hasNext()) {
            Map.Entry user = (Map.Entry) it.next();
            if (!it.hasNext()) {
                users = users + (String) user.getKey();
            }
            if (it.hasNext()) {
                users = users + (String) user.getKey() + ",";
            }

        }
        return users;
    }

    public void Update(String users) throws IOException {
        Iterator it = tm.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry user = (Map.Entry) it.next();
            Socket s = (Socket) user.getValue();
            pw = new PrintWriter(s.getOutputStream(), true);
            pw.println("USERS#" + users);

        }
    }

    public ChatThread(TreeMap tm, Socket s) {
        this.tm = tm;
        this.s = s;
    }

    public void send(Socket s, String msg) throws IOException {
        pw = new PrintWriter(s.getOutputStream(), true);
        pw.println("MESSAGE#" + localName + "#" + msg);
    }

    public void run() {

        try {
            scan = new Scanner(s.getInputStream());
            pw = new PrintWriter(s.getOutputStream(), true);
            boolean stop = false;
            if(!UserName()){
                stop = true;
            }
            while (!stop) {
                String line = scan.nextLine();
//                line = line.replaceAll("/#", "[h]");
                list = line.split("#");
//                for (int i = 0; i < list.length; i++) {
//                    list[i] = list[i].replaceAll("[h]", "#");
//                for somefucking reason all "h" are replaced
//                }
                switch (list[0].toLowerCase()) {
                    case "send":
                        if (list.length == 3) {
                            if (list[1].equals("*")) {
                                list[1] = "All: " + makeUsers();
                                Iterator it = tm.entrySet().iterator();
                                while (it.hasNext()) {

                                    Map.Entry user = (Map.Entry) it.next();
                                    send((Socket) user.getValue(), list[2]);
                                }
                            } else {
                                String[] temp = list[1].split(",");
                                List<String> receiver = new ArrayList();
                                for (String r : temp) {
                                    receiver.add(r.toLowerCase());
                                }

                                Iterator it = tm.entrySet().iterator();
                                while (it.hasNext()) {
                                    Map.Entry user = (Map.Entry) it.next();
                                    String name = (String) user.getKey();

                                    if (receiver.contains(name.toLowerCase())) {
                                        send((Socket) user.getValue(), list[2]);
                                    }
                                }
                            }
                            Logger.getLogger(Log.LOG_NAME).log(Level.INFO,("Message sent from: " + localName + ", to: " + list[1]));
                        }
                        break;
                    case "logout":
                        if (line.contains("#")) {
                            tm.remove(localName);
                            Logger.getLogger(Log.LOG_NAME).log(Level.INFO,("Disconnected: " + localName));
                            Logger.getLogger(Log.LOG_NAME).log(Level.INFO,("Users online: " + makeUsers()));
                            stop = true;
                            Update(makeUsers());
                        }
                    break;
                }

            }

        } catch (java.util.NoSuchElementException | java.lang.ArrayIndexOutOfBoundsException o) {
            try {
                tm.remove(localName);
                Logger.getLogger(Log.LOG_NAME).log(Level.INFO,("Disconnected: " + localName));
                Update(makeUsers());
            } catch (IOException ex) {
                Logger.getLogger(ChatThread.class.getName()).log(Level.SEVERE, ex.getMessage());
            }
        } catch (IOException ex) {
            Logger.getLogger(ChatThread.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
    }
}
