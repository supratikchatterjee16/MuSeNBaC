package musenbac;

import java.net.Socket;
import java.net.InetAddress;
import java.net.ServerSocket;

import javax.net.ssl.*;

import java.security.Security;
import java.security.PrivilegedActionException;

import java.io.*;

import com.sun.net.ssl.internal.ssl.Provider;

public class ServerManager extends Thread{
    private int port = 80;
    private Thread t;
    private static ServerSocket ss;
    private static boolean exit = false;
    private static int count = 0;
    ServerManager(int p){port=p;}
    public void run(){
        // Registering the JSSE provider
		//Security.addProvider(new Provider());
		//Specifying the Keystore details
		//System.setProperty("javax.net.ssl.keyStore","server.ks");
		//System.setProperty("javax.net.ssl.keyStorePassword","piku1996");
		//SSLServerSocketFactory sslServerSocketfactory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
		//SSLServerSocket sslss =null;
        //try{sslss = (SSLServerSocket)sslServerSocketfactory.createServerSocket(port);}catch(IOException e){e.printStackTrace();}
        if(count>0){
            System.out.print("Only one instance allowed\n>>");
            return;
        }
        else exit = false;
        
        try{
        ss=new ServerSocket(port);
        System.out.print("Connection open at port : "+port+"\n>>");
        }catch(IOException e){
            System.err.print("Problem in starting Server : Run the program as administrator or super user.\n");
            e.printStackTrace();
            System.err.print("\n>>");
            return;
        }
       
        count ++;
        while(!exit){
            try{
                t=new Thread(new Client(ss.accept()));
                t.setName("Client thread");
                if(!exit)t.start();
                else{ss=null;return;}
            }catch(Exception e){System.err.print("Client processes halted\n>>");return;}
        }
    }
    public static void close(){
        count = 0;
        exit = true;
        try{ss.close();}catch(IOException e){return;}
     }
}
