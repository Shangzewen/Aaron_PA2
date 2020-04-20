package com.example.pa2;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.net.Socket;
//import ClientProtocol.java;
public class ClientWithSecurity{


    public static void main(String[] args) {


        String filename = "100.txt";
        if (args.length > 0) filename = args[0];

        String serverAddress = "localhost";
        if (args.length > 1) filename = args[1];

        int port = 4321;
        if (args.length > 2) port = Integer.parseInt(args[2]);

        int numBytes = 0;

        Socket clientSocket = null;

        DataOutputStream toServer = null;
        DataInputStream fromServer = null;

        FileInputStream fileInputStream = null;
        BufferedInputStream bufferedFileInputStream = null;

        long timeStarted = System.nanoTime();

        try {

            System.out.println("Establishing connection to server...");

            // Connect to server and get the input and output streams
            clientSocket = new Socket(serverAddress, port);
            toServer = new DataOutputStream(clientSocket.getOutputStream());
            fromServer = new DataInputStream(clientSocket.getInputStream());

            System.out.println("Sending file...");
            ClientProtocol clientProtocol = new ClientProtocol("cacse.crt");
            //ClientProtocol clientProtocol = new ClientProtocol("/Users/huaihaizi/Downloads/MyApplication/PA2/src/main/java/com/example/pa2/cacse.crt");
            //ServerProtocol serverProtocol = new ServerProtocol("/Users/huaihaizi/Desktop/Term_5/50.005/ProgrammingAssignment2/PA2/example-92c5fee0-7ef8-11ea-ae9d-89114163ae84.crt");
            //Send nonce
            clientProtocol.GenerateNonce(8);
            toServer.writeInt(2);
            toServer.write(clientProtocol.getNonce());

            // receive the encrypted message
            fromServer.read(clientProtocol.getEncryptedNonce());
            System.out.println("received the encrypted message from the sever");
            //Decrypte the message
            byte[] decryptedNonce = clientProtocol.DecryptedNonce(clientProtocol.getEncryptedNonce());

;

            // compare the decrypted nonce with the none
            if(clientProtocol.ComparedNonceWithDecryptedMessage(decryptedNonce)){
                System.out.println("Server is verified");
            }else{
                System.out.println("Server is not verified ");
                System.out.println("Clossing all the connections");
                toServer.close();
                fromServer.close();
                clientSocket.close();
            }


            // send the file
//
//            // Send the filename
//            toServer.writeInt(0);
//            toServer.writeInt(filename.getBytes().length);
//            toServer.write(filename.getBytes());
//            //toServer.flush();
//
//            // Open the file
//            fileInputStream = new FileInputStream(filename);
//            bufferedFileInputStream = new BufferedInputStream(fileInputStream);
//
//            byte[] fromFileBuffer = new byte[117];
//
//            // Send the file
//            for (boolean fileEnded = false; !fileEnded; ) {
//                numBytes = bufferedFileInputStream.read(fromFileBuffer);
//                fileEnded = numBytes < 117;
//
//                toServer.writeInt(1);
//                toServer.writeInt(numBytes);
//                toServer.write(fromFileBuffer);
//                toServer.flush();
//            }
//
//            bufferedFileInputStream.close();
//            fileInputStream.close();

            System.out.println("Closing connection...");

        } catch (Exception e) {
            e.printStackTrace();
        }

        long timeTaken = System.nanoTime() - timeStarted;
        System.out.println("Program took: " + timeTaken / 1000000.0 + "ms to run");
    }
}
