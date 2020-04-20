package com.example.pa2;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.nio.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.spec.*;
import java.io.*;
import java.nio.*;
import java.security.*;
import java.security.spec.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class ServerWithSecurity {
    public static PrivateKey get(String filename)
            throws Exception {

        byte[] keyBytes = Files.readAllBytes(Paths.get("/Users/huaihaizi/Desktop/Term_5/50.005/ProgrammingAssignment2/PA2/private_key.der"));

        PKCS8EncodedKeySpec spec =
                new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    public static void main(String[] args) throws InvalidKeySpecException, CertificateException, NoSuchAlgorithmException, IOException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {

        int port = 4321;
        if (args.length > 0) port = Integer.parseInt(args[0]);

        ServerSocket welcomeSocket = null;
        Socket connectionSocket = null;
        DataOutputStream toClient = null;
        DataInputStream fromClient = null;

        FileOutputStream fileOutputStream = null;
        BufferedOutputStream bufferedFileOutputStream = null;
        BufferedReader inputReader = null;

        try {
            welcomeSocket = new ServerSocket(port);
            connectionSocket = welcomeSocket.accept();
            fromClient = new DataInputStream(connectionSocket.getInputStream());
            toClient = new DataOutputStream(connectionSocket.getOutputStream());

            while (!connectionSocket.isClosed()) {

                int packetType = fromClient.readInt();

                // If the packet is for transferring the filename
                if (packetType == 0) {

                    System.out.println("Receiving file...");

                    int numBytes = fromClient.readInt();
                    byte[] filename = new byte[numBytes];
                    // Must use read fully!
                    // See: https://stackoverflow.com/questions/25897627/datainputstream-read-vs-datainputstream-readfully
                    fromClient.readFully(filename, 0, numBytes);

                    fileOutputStream = new FileOutputStream("recv_" + new String(filename, 0, numBytes));
                    bufferedFileOutputStream = new BufferedOutputStream(fileOutputStream);

                    // If the packet is for transferring a chunk of the file
                } else if (packetType == 1) {

                    int numBytes = fromClient.readInt();
                    byte[] block = new byte[numBytes];
                    fromClient.readFully(block, 0, numBytes);

                    if (numBytes > 0)
                        bufferedFileOutputStream.write(block, 0, numBytes);

                    if (numBytes < 117) {
                        System.out.println("Closing connection...");

                        if (bufferedFileOutputStream != null) bufferedFileOutputStream.close();
                        if (bufferedFileOutputStream != null) fileOutputStream.close();
                        fromClient.close();
                        toClient.close();
                        connectionSocket.close();
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set up protocol
        ServerProtocol serverProtocol = new ServerProtocol("server.crt");

        // Get nonce from client
        System.out.println("Getting nonce from client...");
        fromClient.read(serverProtocol.getNonce());
        System.out.println("Nonce received");

        // Encrypt nonce
        System.out.println("Encrypting nonce...");
        serverProtocol.encryptedNonce();

        // Send nonce to client
        System.out.println("Sending encrypted nonce to client...");
        toClient.write(serverProtocol.getEncryptedNonce());
        toClient.flush();

        // Receive certificate request from client
        while (true){
            String request = inputReader.readLine();
            if (request.equals("Request certificate...")){
                System.out.println("Client: " + request);

                // Send certificate to client
                System.out.println("Sending certificate to client...");
                toClient.write(serverProtocol.getCertificate());
                toClient.flush();
                break;
            }
            else
                System.out.println("Request failed...");
        }

        // Waiting for client to finish verification
        System.out.println("Client: " + inputReader.readLine());

        // Starts file transfer
        System.out.println("AP completes. Receiving file...");

    }
}
