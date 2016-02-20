/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Client.Client;
import Client.GUI;
import Server.ChatServer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Warco
 */
public class JUnitTest {

    Client t1;
    Client t2;
    ArrayList<String> list;
    GUI g;
    ChatServer chat;

    public JUnitTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws IOException {
        t1 = new Client();
        t2 = new Client();
        Thread t = new Thread(new Runnable() {
            public void run() {
                chat = new ChatServer();
        chat.tester("localhost", 9045);
                try {
                    chat.startServer();
                } catch (IOException ex) {
                    Logger.getLogger(JUnitTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        
    }

    @After
    public void tearDown() {
    }

    @Test
    public void Users() {
        try {

            String ip = "localhost";
            int port = 9045;
            String n1 = "name1";
            String n2 = "name2";

            t1.connect(ip, port);
            t1.send("USER#" + n1);
            Assert.assertEquals("USERS#" + n1, t1.receive());

            t2.connect(ip, port);
            t2.send("USER#" + n2);
            Assert.assertEquals("USERS#" + n1 + "," + n2, t2.receive());

            t1.send("SEND#" + n2 + "#Hello");
            Assert.assertEquals("MESSAGE#" + n1 + "#Hello", t2.receive());
            t1.send("LOGOUT#");
            Assert.assertEquals("USERS#" + n2, t2.receive());
            t2.send("LOGOUT#");
        } catch (IOException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
