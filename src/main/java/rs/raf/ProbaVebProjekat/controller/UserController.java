package rs.raf.ProbaVebProjekat.controller;

import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.raf.ProbaVebProjekat.dto.LoginCredentials;
import rs.raf.ProbaVebProjekat.model.User;
import rs.raf.ProbaVebProjekat.model.Vacuum;
import rs.raf.ProbaVebProjekat.model.VacuumDTO;
import rs.raf.ProbaVebProjekat.repository.UserRepository;
import rs.raf.ProbaVebProjekat.repository.VacuumRepository;
import rs.raf.ProbaVebProjekat.security.*;
import rs.raf.ProbaVebProjekat.service.TokenService;
import rs.raf.ProbaVebProjekat.service.UserService;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    TokenService tokenService;
    @Autowired
    VacuumRepository vacuumRepository;

    @CanCreateUser
    @PostMapping(value = "/saveUser",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> sacuvajKorisnika(@RequestHeader("Authorization") String authorization,
                                              @RequestBody User user) throws Exception {
        try {
            userService.sacuvajKorisnika(user);
            return ResponseEntity.status(HttpStatus.OK).body("Uspesno ste sacuvali korisnika");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @PostMapping(value = "/loginUser", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody LoginCredentials loginCredentials){
        try {
            userService.login(loginCredentials);
            return new ResponseEntity<>(userService.login(loginCredentials), HttpStatus.OK);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @CanDeleteUser
    @DeleteMapping(value = "/deleteUser/{id_user}")
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String authorization,
                                            @PathVariable Integer id_user){

        try{
            userService.obrisiKorisnika(id_user);
            //return new ResponseEntity<>("Uspesno obrisan korisnik", HttpStatus.OK);
            return ResponseEntity.status(HttpStatus.OK).body("Uspesno obrisan korisnik");
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @CanUpdateUser
    @PutMapping(value = "/updateUser/{id_user}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> azurirajKorisnika(@RequestHeader("Authorization") String authorization,
                                               @PathVariable Integer id_user, @RequestBody User user){
        try {
            userService.updateUser(id_user,user);
            return ResponseEntity.status(HttpStatus.OK).body("Uspesno ste azurirali korisnika" + " " +
                    "sa ID-jem " + id_user);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @CanReadUser
    @GetMapping(value = "/findUsers", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findAllUsers(@RequestHeader("Authorization") String authorization){
        return ResponseEntity.status(HttpStatus.OK).body(userRepository.findAll());
    }

    @CanAddVacuum
    @PostMapping(value = "/addVacuum",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addVacuum(@RequestHeader("Authorization") String authorization,
                                       @RequestBody VacuumDTO vacuum){
        //proveriti sta znaci da rezultat mora da bude usisivac koji je u stanju stopped
        //*mogu da dodam jos jedno polje 'state' koje ce da govori u kom je stanju usisivac
        String token = authorization.substring(7);//preskacem "Bearer "
        Claims claims = tokenService.parseToken(token);
        Integer id_user = claims.get("id", Integer.class);
        User user = userRepository.findById(Long.valueOf(id_user)).orElseThrow(()->
                new EntityNotFoundException("Nije pronadjen korisnik sa tim ID-jem"));
        userService.addVacuum(user,vacuum);
        return ResponseEntity.status(HttpStatus.OK).body("Uspesno ste dodali usisivac");
    }

    @CanSearchVacuum
    @GetMapping(value = "/searchVacuum", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> searchVacuum(@RequestHeader("Authorization") String authorization){
        String token = authorization.substring(7);
        Claims claims = tokenService.parseToken(token);
        Integer id_user = claims.get("id", Integer.class);

        try {
            return ResponseEntity.status(HttpStatus.OK).body(userService.searchVacuum(id_user));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @CanSearchVacuum
    @GetMapping(value = "/searchByStatus", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> searchByVacStatus(@RequestHeader("Authorization") String authorization,
                                               @RequestParam("status") String status){
        String token = authorization.substring(7);
        Claims claims = tokenService.parseToken(token);
        Integer id = claims.get("id",Integer.class);

        try {
            return ResponseEntity.status(HttpStatus.OK).body(userService.searchByStatus(id,status));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @CanSearchVacuum
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE,value = "/searchByName")
    public ResponseEntity<?> searchVacByName(@RequestHeader("Authorization") String authorization,
                                             @RequestParam("name") String name){

        String token = authorization.substring(7);
        Claims claims = tokenService.parseToken(token);
        Integer id_user = claims.get("id", Integer.class);

        try {
            return ResponseEntity.status(HttpStatus.OK).body(userService.searchByName(id_user,
                    name));
        } catch (Exception e) {
           return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @CanSearchVacuum
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE,value = "/searchByDateFrom")
    public ResponseEntity<?> searchVacByDateFrom(@RequestHeader("Authorization") String
                                             authorization, @RequestParam String time){
        String token = authorization.substring(7);
        Claims claims = tokenService.parseToken(token);
        Integer id_user = claims.get("id", Integer.class);

        try {
             return ResponseEntity.status(HttpStatus.OK).body(userService.searchByDateFrom(id_user,
                    time));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @CanSearchVacuum
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE,value = "/searchByDateTo")
    public ResponseEntity<?> searchVacByDateTo(@RequestHeader("Authorization") String
                                                         authorization, @RequestParam String time){
        String token = authorization.substring(7);
        Claims claims = tokenService.parseToken(token);
        Integer id_user = claims.get("id", Integer.class);

        try {
            return ResponseEntity.status(HttpStatus.OK).body(userService.searchByDateTo(id_user,
                    time));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @CanRemoveVacuum
    @DeleteMapping(value = "/deleteVacuum/{id_vacuum}")
    public ResponseEntity<?> deleteVacuum(@PathVariable Integer id_vacuum,
                                          @RequestHeader("Authorization") String authorization){
        String token = authorization.substring(7);
        Claims claims = tokenService.parseToken(token);
        Integer id_user = claims.get("id",Integer.class);
        Optional<Vacuum> vacuum = vacuumRepository.findById(Long.valueOf(id_vacuum));
        if(vacuum.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nije pronadjen " +
                    "usisivac sa unetim ID-jem");

        try {
            userService.obrisiUsisivac(id_vacuum,id_user);
            return ResponseEntity.status(HttpStatus.OK).body("Uspesno ste obrisali " +
                    "usisivac");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
        }
    }

    /*
    @CanStartVacuum
    @PostMapping(value = "/startVacuum")
    public CompletableFuture<ResponseEntity<String>> startVacuum(@RequestParam Integer id_vacuum,
                                                                 @RequestHeader("Authorization") String authorization) throws Exception {

        String token = authorization.substring(7);
        Integer id_user = tokenService.parseToken(token).get("id",Integer.class);
        Vacuum vacuum = vacuumRepository.findById(Long.valueOf(id_vacuum)).orElseThrow(() ->
                new EntityNotFoundException("Nije pronadjen usisivac sa unetim ID-jem"));

        CompletableFuture<String> result = userService.startVacuum(id_vacuum, id_user);

        return result.thenApply(poruka -> {
            System.out.println("Poruka: " + poruka);
            return ResponseEntity.status(HttpStatus.OK).body(poruka);
        }).exceptionally(e -> {
            System.out.println("Došlo je do greške prilikom pokretanja usisivača: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
        });
    }

    */

}
