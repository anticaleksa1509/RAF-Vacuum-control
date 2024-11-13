package rs.raf.ProbaVebProjekat.security;

import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class HashPassword {

    //nije mogla da se pozove metoda koja je static jer se static odnosni na klasu
    //a ne na objekat, zato smo sklonili static
    public String hashSifre(String sifra) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(sifra.getBytes());

            // Pretvaranje bajtova u heksadecimalni format
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
