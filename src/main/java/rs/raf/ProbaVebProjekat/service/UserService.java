package rs.raf.ProbaVebProjekat.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.raf.ProbaVebProjekat.dto.LoginCredentials;
import rs.raf.ProbaVebProjekat.model.User;
import rs.raf.ProbaVebProjekat.model.Vacuum;
import rs.raf.ProbaVebProjekat.model.VacuumDTO;
import rs.raf.ProbaVebProjekat.repository.UserRepository;
import rs.raf.ProbaVebProjekat.repository.VacuumRepository;
import rs.raf.ProbaVebProjekat.response.TokenResponse;
import rs.raf.ProbaVebProjekat.security.HashPassword;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    HashPassword hashPassword;
    @Autowired
    TokenService tokenService;
    @Autowired
    VacuumRepository vacuumRepository;
    public void sacuvajKorisnika(User user) throws Exception {

        String encryptedLozinka = user.getLozinka();
        user.setLozinka(hashPassword.hashSifre(user.getLozinka()));
        List<User> users = userRepository.findAll();

        for(User user1: users){
            if(user1.getEmail().equals(user.getEmail())){
                throw new Exception("Postoji korisnik sa istom email adresom!");
            }
        }
        int flag = 0;
        for(int i = 0; i < 10; i++){
            if(encryptedLozinka.contains(String.valueOf(i))){
                flag = 1;
            }
        }
        if(flag == 0){
            throw new Exception("Lozinka mora da sadrzi bar jedan broj");
        }

        if(encryptedLozinka.length() < 8){
            throw new Exception("Lozinka mora da ima minimum 8 karaktera");
        }
        userRepository.save(user);

    }

    public TokenResponse login(LoginCredentials loginCredentials){
        User user = userRepository.findUserByEmailAndLozinka(loginCredentials.getEmail(),
                hashPassword.hashSifre(loginCredentials.getLozinka())).orElseThrow(() ->
                new EntityNotFoundException("Nije pronadjen korisnik sa unetim kredencijalima"));

        Claims claims = Jwts.claims();

        claims.put("id",user.getId_user());
        claims.put("can_read",user.getCan_read());
        claims.put("can_update",user.getCan_update());
        claims.put("can_delete",user.getCan_delete());
        claims.put("can_create",user.getCan_create());
        claims.put("can_add_vacuum",user.getCan_add_vacuum());
        claims.put("can_search_vacuum", user.getCan_search_vacuum());
        claims.put("can_remove_vacuum",user.getCan_remove_vacuum());
        claims.put("can_start_vacuum",user.getCan_start_vacuum());
        return new TokenResponse(tokenService.generateToken(claims));

    }

    public void obrisiKorisnika(Integer id_user){
        User user =  userRepository.findById(Long.valueOf(id_user)).orElseThrow(() ->
                new EntityNotFoundException("Nije pronadjen korisnik sa unetim ID-jem"));
        userRepository.delete(user);
    }

    public void updateUser(Integer id_user,User user) throws Exception {
        User user1 = userRepository.findById(Long.valueOf(id_user)).orElseThrow(()->
                new EntityNotFoundException("Nije pronadjen korisnik sa unetim ID-jem"));

        if(user.getCan_update() == null && user.getCan_delete() == null && user.getCan_create() == null
        && user.getCan_read() == null && user.getName() == null && user.getLastName() == null
        && user.getEmail() == null && user.getLozinka() == null){
            throw new Exception("Korisnik sa unetim ID-jem postoji ali niste azurirali ni jedno polje " +
                    "tako da korisnik ostaje ne izmenjen");
        }

        if(user.getCan_update() != null){
            user1.setCan_update(user.getCan_update());
        }
        if(user.getCan_delete() != null){
            user1.setCan_delete(user.getCan_delete());
        }
        if(user.getCan_create() != null){
            user1.setCan_create(user.getCan_create());
        }
        if(user.getCan_read() != null){
            user1.setCan_delete(user.getCan_delete());
        }
        if(user.getEmail() != null){
            List<User> users = userRepository.findAll();

            for(User user2: users){
                if(user2.getEmail().equals(user.getEmail())){
                    throw new Exception("Postoji korisnik sa istom email adresom!");
                }
            }
            user1.setEmail(user.getEmail());
        }
        if(user.getLozinka() != null){
            if(user.getLozinka().length() < 8){
                throw new Exception("Lozinka mora da ima minimum 8 karaktera");
            }
            int flag = 0;
            for(int i  = 0; i < 10; i++){
                if(user.getLozinka().contains(String.valueOf(i))){
                    flag = 1;
                }
            }
            if(flag == 0){
                throw new Exception("Lozinka mora da ima bar jednu cifru");
            }
            user1.setLozinka(hashPassword.hashSifre(user.getLozinka()));
        }
        if(user.getName() != null){
            user1.setName(user.getName());
        }
        if(user.getLastName() != null){
            user1.setLastName(user.getLastName());
        }
        userRepository.save(user1);
    }

    private static int count = 1;
    public void addVacuum(User user, VacuumDTO vacuum){

        Vacuum vacuum1 = new Vacuum();
        vacuum1.setActive(vacuum.getActive());
        vacuum1.setUser(user);
        vacuum1.setStatus(vacuum.getStatus());
        vacuum1.setDate(LocalDateTime.now());
        vacuum1.setName("Vacuum" + user.getName() + count);
        count++;
        vacuumRepository.save(vacuum1);
        user.getVacuums().add(vacuum1);

    }

    public List<Vacuum> searchVacuum(Integer id_user) throws Exception {
        User user = userRepository.findById(Long.valueOf(id_user)).orElseThrow(()->
                new EntityNotFoundException("Nije pronadjen korisnik sa unetim ID-jem"));
        List<Vacuum> vacuums = user.getVacuums();
        //System.out.println(vacuums);
        List<Vacuum> myVacuums = new ArrayList<>();
        for(Vacuum v: vacuums){
            if(v.getActive() && v.getUser().equals(user)){
                System.out.println("USAO");
                myVacuums.add(v);
            }
        }
        if(myVacuums.isEmpty())
            throw new Exception("Prazna je lista");
        return myVacuums;
    }

    public List<Vacuum> searchByStatus(Integer id_user,String status) throws Exception {

        User user = userRepository.findById(Long.valueOf(id_user)).orElseThrow(()->
                new EntityNotFoundException("Nije pronadjem korisnik sa tim ID-jem"));
        List<Vacuum> vacuums = vacuumRepository.findAll();
        List<Vacuum> myVac = new ArrayList<>();
        if(status.equals("ON") || status.equals("OFF") || status.equals("Discharging")){
            for(Vacuum v: vacuums){
                if(v.getStatus().equals(status) && v.getUser().equals(user) && v.getActive()){
                    myVac.add(v);
                }
            }
            if(myVac.isEmpty())
                throw new Exception("Lista je prazna,nema usisivaca u trazenom statusu za" +
                        "trazenog korisnika, ili ima u trazenom statusu ali nisu aktivni");
            return myVac;
        }else{
            throw new Exception("Status nije jedan od ponudjenih u kojim usisivac moze da bude");
        }
    }
    public List<Vacuum> searchByName(Integer id_user, String name) throws Exception{
        User user = userRepository.findById(Long.valueOf(id_user)).orElseThrow(() ->
                new EntityNotFoundException("Nije pronadjen korisnik sa tim ID-jem"));

        List<Vacuum> vacuums = vacuumRepository.findAll();
        List<Vacuum> myVac = new ArrayList<>();
        for(Vacuum v : vacuums){
            if(v.getUser().equals(user) && v.getName().toLowerCase().contains(name.toLowerCase()) &&
            v.getActive()){
                //moramo sve da pretvorimo u mala slova da bi bila caseInsensitive komparacija
                myVac.add(v);
            }
        }
        if(myVac.isEmpty())
            throw new Exception("Nema usisivaca koji sadrze uneti string");
        return myVac;
    }
    public List<Vacuum> searchByDate(Integer id_user, LocalTime time) throws Exception{
        //promenljiva tipa localTime nam omogucava da imamo prikaz vremena bez datuma

        User user = userRepository.findById(Long.valueOf(id_user)).orElseThrow(()->
                new EntityNotFoundException("Nije pronadjen korisnik sa unetim id-jem"));

        List<Vacuum> vacuums = vacuumRepository.findAll();
        List<Vacuum> myVac = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        System.out.println(vacuums.get(0).getDate());
        for(Vacuum v: vacuums){
            String time1 = String.valueOf(v.getDate());
            LocalDateTime dateTime = LocalDateTime.parse(time1,formatter);
            int hour = dateTime.getHour();//sati usisivaca iz baze
            int minutes = dateTime.getMinute();
            if(hour >= time.getHour() && minutes >= time.getMinute() && v.getActive()){
                myVac.add(v);
            }
        }
        if(myVac.isEmpty())
            throw new Exception("Nema aktivnih usisivaca od pocetka zeljenog vremena");

        return myVac;
    }

    public List<Vacuum> searchByDateFrom(Integer id_user, String time) throws  Exception{
        User user = userRepository.findById(Long.valueOf(id_user)).orElseThrow(()->
                new EntityNotFoundException("Nije pronadjen korisnik sa unetim id-jem"));

        List<Vacuum> vacuums = vacuumRepository.findAll();
        List<Vacuum> myVac = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");//ovom
        //metodom definisemo kako ce se vreme parsirati iz stringa u localTime objekat
        //ofPattern se koristi da vidimo koji format ocekujemo
        LocalDateTime time1 = LocalTime.parse(time, formatter).atDate(LocalDate.now());
        //a metoda parse se koristi za pretvaranje stringa u localTime objekat na nacin koji je
        //definisan gore u formateru
        int hour = time1.getHour();
        int minutes = time1.getMinute();
        for(Vacuum vacuum: vacuums){
            if(vacuum.getDate().getHour() >= hour && vacuum.getDate().getMinute() >= minutes &&
                vacuum.getActive()) {
                    myVac.add(vacuum);
            }
        }
        if(myVac.isEmpty())
            throw new Exception("Prazna je lista usisivaca za trazeno vreme ili nisu aktivni");
        return myVac;
    }
    public List<Vacuum> searchByDateTo(Integer id_user, String time) throws  Exception{
        User user = userRepository.findById(Long.valueOf(id_user)).orElseThrow(()->
                new EntityNotFoundException("Nije pronadjen korisnik sa unetim id-jem"));

        List<Vacuum> vacuums = vacuumRepository.findAll();
        List<Vacuum> myVac = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");//ovom
        //metodom definisemo kako ce se vreme parsirati iz stringa u localTime objekat
        //ofPattern se koristi da vidimo koji format ocekujemo
        LocalDateTime time1 = LocalTime.parse(time, formatter).atDate(LocalDate.now());
        //a metoda parse se koristi za pretvaranje stringa u localTime objekat na nacin koji je
        //definisan gore u formateru
        int hour = time1.getHour();
        int minutes = time1.getMinute();
        for(Vacuum vacuum: vacuums){
            if(vacuum.getDate().getHour() <= hour && vacuum.getDate().getMinute() <= minutes &&
                    vacuum.getActive()) {
                myVac.add(vacuum);
            }
        }
        if(myVac.isEmpty())
            throw new Exception("Prazna je lista usisivaca za trazeno vreme ili nisu aktivni");
        return myVac;
    }

    public void obrisiUsisivac(Integer id_vacuum,Integer id_user) throws Exception{
        Vacuum vacuum = vacuumRepository.findById(Long.valueOf(id_vacuum)).orElseThrow(()->
                new EntityNotFoundException("Ne postoji usisivac sa unetim ID-jem"));
        User user = userRepository.findById(Long.valueOf(id_user)).orElseThrow();
        int flag = 0;
        List<Vacuum> vacuums = user.getVacuums();
        Iterator<Vacuum> iterator = vacuums.iterator();
        while(iterator.hasNext()){
            Vacuum v = iterator.next();
            if(vacuum.equals(v)){
                flag = 1;
                user.getVacuums().remove(vacuum);
                break;
            }
        }
        userRepository.save(user);
        if(flag == 0)
            throw new Exception("Ne mozete obrisati usisivac jer ga niste vi ni dodali!");
    }

    /*
    @Async
    //@Transactional(rollbackFor = Exception.class)
    public CompletableFuture<String> startVacuum(Integer id_vacuum, Integer id_user) throws Exception{

        try {


            Vacuum vacuum = vacuumRepository.findById(Long.valueOf(id_vacuum)).orElseThrow();
            User user = userRepository.findById(Long.valueOf(id_user)).orElseThrow();
            List<Vacuum> vacuums = user.getVacuums();
            int flag = 0;
            for (Vacuum v : vacuums) {
                if (v.equals(vacuum) && vacuum.getStatus().equals("OFF")) {
                    flag = 1;
                    vacuum.setStatus("ON");
                    vacuumRepository.save(vacuum);
                    break;
                }
            }
            if (flag == 0) {
                if (!vacuum.getStatus().equals("OFF")) {
                    throw new Exception("Ne mozete da startujete usisivac jer nije u STOPPED!");
                }
                throw new Exception("Ne mozete da startujete usisivac jer ga niste vi dodali!");
            }

        }catch (Exception e){
            System.out.println("Doslo je do neke greske: " + e.getMessage());
            return CompletableFuture.completedFuture("Greska: " + e.getMessage());
        }
            Random rand = new Random();
            int devijacija = rand.nextInt(5000);
            int pauza = 15000 + devijacija;
            Thread.sleep(pauza);

        return CompletableFuture.completedFuture("Uspesno!");
    }
    */




}
