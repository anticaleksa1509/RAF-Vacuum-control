package rs.raf.ProbaVebProjekat.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import rs.raf.ProbaVebProjekat.model.Status;
import rs.raf.ProbaVebProjekat.model.User;
import rs.raf.ProbaVebProjekat.model.Vacuum;
import rs.raf.ProbaVebProjekat.repository.UserRepository;
import rs.raf.ProbaVebProjekat.repository.VacuumRepository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

@Component//kada smo anotirali ovu klasu sa component
//spring moze da upravlja njome prilikom pokretanja same aplikacije
//znaci da ce pozvati metodu run prilikom pokretanja
public class BoostrapClass  implements CommandLineRunner {

    @Autowired
    UserRepository userRepository;
    @Autowired
    VacuumRepository vacuumRepository;

    public static String hashSifre(String sifra) {
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

    @Override
    public void run(String... args) throws Exception {

        User user1 = new User();
        user1.setEmail("admin@example.com");
        user1.setName("Admin");
        user1.setLastName("Adminovic");
        user1.setLozinka(hashSifre("admincar123"));
        user1.setCan_delete(true);
        user1.setCan_read(true);
        user1.setCan_update(true);
        user1.setCan_create(true);
        user1.setCan_add_vacuum(true);
        user1.setCan_search_vacuum(true);
        user1.setCan_remove_vacuum(true);
        user1.setCan_start_vacuum(true);
        userRepository.save(user1);

        try {
            BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\alekaa\\korisnici.txt"));
            boolean canStartVac = false;
            boolean canAddVac;
            String line;
            Random random = new Random();
            while((line = br.readLine()) != null){
                String[] parts = line.split(",");
                if(parts.length > 0){
                    User user = new User();
                    user.setName(parts[0]);
                    user.setLastName(parts[1]);
                    user.setEmail(parts[2]);
                    user.setLozinka(hashSifre(parts[3]));

                    user.setCan_delete(random.nextBoolean());
                    user.setCan_create(random.nextBoolean());
                    user.setCan_read(random.nextBoolean());
                    user.setCan_update(random.nextBoolean());

                    boolean rand = random.nextBoolean();
                    user.setCan_add_vacuum(rand);
                    user.setCan_start_vacuum(rand);
                    user.setCan_search_vacuum(random.nextBoolean());
                    user.setCan_remove_vacuum(random.nextBoolean());

                    userRepository.save(user);


                }else{
                    System.out.println("Some error occurred");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
