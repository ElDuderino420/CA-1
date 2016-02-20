/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import Client.IObs;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.rmi.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 *
 * @author Warco
 */
public class Client extends Thread{

    private Socket socket;
    private int port;
    private InetAddress serverAddress;
    private Scanner input;
    private PrintWriter output;
    private List<IObs> gyne = new ArrayList();

    public void connect(String address, int port) throws UnknownHostException, IOException {
        this.port = port;
        serverAddress = InetAddress.getByName(address);
        socket = new Socket(serverAddress, port);
        input = new Scanner(socket.getInputStream());
        output = new PrintWriter(socket.getOutputStream(), true);  //Set to true, to get auto flush behaviour
    }

    public void educateGynecologist(IObs obs){
        this.gyne.add(obs);
    }
    
    public void send(String msg) {
        output.println(msg);
    }

    public void stopNow() throws IOException {
        output.println("LOGOUT#");
    }

    public String receive() {
        String msg = input.nextLine();
        if (msg.equals("LOGOUT#")) {
            try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return msg;
    }

    @Override
    public void run() {
        while(true){
            String msg = receive();
            for (IObs g : gyne) {
                g.dataReady(msg);
            }
        }
    }

    public static void main(String[] args) {
        int port = 9090;
        String ip = "TotalFaggotry.cloudapp.net";
        if (args.length == 2) {
            ip = args[0];
            port = Integer.parseInt(args[1]);
        }
        try {
            Client tester = new Client();
            tester.connect(ip, port);
            System.out.println("Sending 'Hello world'");
            tester.send("USER#yolo");
            System.out.println("Waiting for a reply");
            System.out.println("Received: " + tester.receive()); //Important Blocking call         
            tester.stopNow();
            //System.in.read();      
        } catch (UnknownHostException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("1");
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("2");
        }
    }
}
