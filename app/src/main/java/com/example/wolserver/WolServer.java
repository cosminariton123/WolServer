package com.example.wolserver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class WolServer {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;


    public void startServer(Integer port) throws java.io.IOException {

        serverSocket = new ServerSocket(port);

    }

    public void listenForClient() throws java.io.IOException {
        clientSocket = serverSocket.accept();

        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        String inputCommand;
        while ((inputCommand = in.readLine()) != null) {

            if (inputCommand.equals("start")){
                startHostServer();
            }

            if (inputCommand.equals("stop")){
               stopHostServer(inputCommand);
            }

            if (inputCommand.equals("status")){
                Boolean reachable = getStatusOfHostServer();
                if (reachable)
                    out.println("Serverul este pornit");
                else
                    out.println("Serverul este oprit");
            }

            if ("exit".equals(inputCommand)) {
                out.println("Se inchide conexiunea");
                closeCurrentConnection();
            }
        }
    }

    private void startHostServer() throws java.io.IOException{
        if (getStatusOfHostServer())
            out.println("Serverul este deja pornit");
        else {
            WakeOnLan.main(Config.broadcastIp, Config.mac);
            out.println("Serverul porneste");
        }
    }

    private void stopHostServer(String inputCommand) throws java.io.IOException{
        if (!getStatusOfHostServer())
            out.println("Serverul este deja oprit");
        else{
            Socket serverClientSocket = new Socket(Config.serverIp, Config.serverPort);
            OutputStream serverOut = serverClientSocket.getOutputStream();

            serverOut.write(inputCommand.getBytes(StandardCharsets.UTF_8));
            serverOut.flush();
            serverOut.close();
            serverClientSocket.close();
            out.println("Serverul se inchide");
        }
    }

    private Boolean getStatusOfHostServer() throws java.io.IOException {
        InetAddress address = InetAddress.getByName(Config.serverIp);
        return address.isReachable(1000);
    }

    public void closeCurrentConnection() throws java.io.IOException{
            in.close();
            out.close();
            clientSocket.close();
    }

    public void stopServer() {
        try {
            in.close();
        }
        catch (java.io.IOException ignored){

        }

        out.close();

        try {
            clientSocket.close();
        }
        catch (java.io.IOException ignored){

        }

        try {
            serverSocket.close();
        }
        catch (java.io.IOException ignored){

        }
    }
}
