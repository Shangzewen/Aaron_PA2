package com.example.pa2;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class ServerProtocol {
    private static InputStream server_input;
    //private static PublicKey CertificatePublic;
    private static PrivateKey ServerPrivate;
    private static PublicKey ServerPublic;
    private static X509Certificate CAcert;
    private static X509Certificate Server_certificate;
    private static CertificateFactory cf;
    //private static byte[] decryptedNonce;
    private static byte[] certificate;
    private static byte[] nonce ;
    private static byte[] encryptedNonce = new byte[16];
    // the size of the array is not sure
    private  static Cipher cipherEncrypt;
    private static Cipher cipherDecrypt;
    // we want to get the private key
    public ServerProtocol(String server_input) throws CertificateException, NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        try{
        cf = CertificateFactory.getInstance("X.509");
        Server_certificate = (X509Certificate)cf.generateCertificate(this.server_input);
        certificate = Server_certificate.getEncoded();
        ServerPrivate = privateKeyReader("/Users/huaihaizi/Desktop/Term_5/50.005/ProgrammingAssignment2/PA2/private_key.der");
        ServerPublic = Server_certificate.getPublicKey();
        }catch(CertificateException e){
            e.printStackTrace();
        }catch(NoSuchAlgorithmException e){
            e.printStackTrace();
        }catch(InvalidKeySpecException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }


    }
    public PrivateKey privateKeyReader(String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Files.readAllBytes(Paths.get(filename));
        PKCS8EncodedKeySpec spec  = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    // we want to encrypt the nonce
    public void encryptedNonce() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        cipherEncrypt = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipherEncrypt.init(Cipher.ENCRYPT_MODE,ServerPrivate);
        encryptedNonce = cipherEncrypt.doFinal(nonce);

    }
    public byte[] getNonce(){
        return nonce;

    }
    public byte[] getEncryptedNonce(){
        return encryptedNonce;
    }
    public byte[] getCertificate(){
        return certificate;
    }
}
