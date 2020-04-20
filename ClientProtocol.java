package com.example.pa2;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class ClientProtocol {
    private static InputStream input;
    private static PublicKey CertificatePublic;
    private static PublicKey ServerPublic;
    private static X509Certificate CAcert;
    private static X509Certificate Server_certificate;
    private static CertificateFactory cf;
    private static byte[] decryptedNonce;
    private static byte[] nonce ;
    private static byte[] encryptedNonce = new byte[16];
    // the size of the array is not sure
    private  static Cipher cipherEncrypt;
    private static Cipher cipherDecrypt;
    public ClientProtocol(String input) throws IOException {
        this.input = new FileInputStream(input);
        try{
            cf = CertificateFactory.getInstance("X.509");
            CAcert = (X509Certificate)cf.generateCertificate(this.input);
            CertificatePublic = CAcert.getPublicKey();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        this.input.close();
    }

public String RandomString (int n){

    // chose a Character random from this String
    String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            + "0123456789"
            + "abcdefghijklmnopqrstuvxyz";

    // create StringBuffer size of AlphaNumericString
    StringBuilder sb = new StringBuilder(n);

    for (int i = 0; i < n; i++) {

        // generate a random number between
        // 0 to AlphaNumericString variable length
        int index
                = (int)(AlphaNumericString.length()
                * Math.random());

        // add Character one by one in end of sb
        sb.append(AlphaNumericString
                .charAt(index));
    }

    return sb.toString();
}
public byte[] GenerateNonce(int n){
    String RandomSting = RandomString(n);
    nonce = RandomSting.getBytes();
    return nonce;
}
public byte[] getNonce(){
    return nonce;
}
public byte[] DecryptedNonce(byte[] encryptedNonce) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
    cipherDecrypt = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    cipherDecrypt.init(Cipher.DECRYPT_MODE,ServerPublic);
    return cipherDecrypt.doFinal(encryptedNonce);

}
public boolean ComparedNonceWithDecryptedMessage(byte[] decryptedNonce){
    return Arrays.equals(nonce,decryptedNonce);
}
public void getCertificate(InputStream certificate) throws CertificateException {
    Server_certificate = (X509Certificate)cf.generateCertificate(certificate);
}
public void getPublicKey(){
    ServerPublic = Server_certificate.getPublicKey();
}
public void verify() throws NoSuchProviderException, CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
    //Server_certificate.checkValidity();
    Server_certificate.verify(CertificatePublic);
}
public byte[] getEncryptedNonce(){
    return encryptedNonce;
}
public byte[] enccryptFile(byte[] file_Byte) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        cipherEncrypt = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipherEncrypt.init(Cipher.ENCRYPT_MODE,ServerPublic);
        return cipherEncrypt.doFinal(file_Byte);
}


}
