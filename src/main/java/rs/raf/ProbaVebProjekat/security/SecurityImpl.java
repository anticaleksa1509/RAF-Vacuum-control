package rs.raf.ProbaVebProjekat.security;

import io.jsonwebtoken.Claims;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import rs.raf.ProbaVebProjekat.service.TokenService;

@Aspect
@Configuration
public class SecurityImpl {

    @Autowired
    TokenService tokenService;

    @Around("@annotation(rs.raf.ProbaVebProjekat.security.CanCreateUser)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable{

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String token = null;
        for(int i = 0; i < methodSignature.getParameterNames().length; i++){
            if(methodSignature.getParameterNames()[i].equals("authorization")){
                if(joinPoint.getArgs()[i].toString().startsWith("Bearer")){
                    token = joinPoint.getArgs()[i].toString().split(" ")[1];
                }
            }
        }
        if(token == null){
            return new ResponseEntity<>("Niste autorizovani", HttpStatus.UNAUTHORIZED);
        }

        Claims claims = tokenService.parseToken(token);
        if(claims == null)
            return new ResponseEntity<>("Niste autorizovani",HttpStatus.UNAUTHORIZED);

        Boolean canCreate = claims.get("can_create", Boolean.class);
        if(canCreate != null && canCreate){
            return joinPoint.proceed();
        }
        return new ResponseEntity<>("Nemate dozvolu da kreirate korisnika",HttpStatus.FORBIDDEN);



    }

    @Around("@annotation(rs.raf.ProbaVebProjekat.security.CanUpdateUser)")
    public Object around1(ProceedingJoinPoint joinPoint) throws Throwable{

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String token = null;
        for(int i = 0; i < methodSignature.getParameterNames().length; i++){
            if(methodSignature.getParameterNames()[i].equals("authorization")){
                if(joinPoint.getArgs()[i].toString().startsWith("Bearer")){
                    token = joinPoint.getArgs()[i].toString().split(" ")[1];
                }
            }
        }
        if(token == null){
            return new ResponseEntity<>("Niste autorizovani",HttpStatus.UNAUTHORIZED);
        }
        Claims claims = tokenService.parseToken(token);
        if(claims == null){
            return new ResponseEntity<>("Niste autorizovani",HttpStatus.UNAUTHORIZED);
        }
        Boolean isCanUpdate = claims.get("can_update", Boolean.class);

        if(isCanUpdate != null && isCanUpdate){
            return joinPoint.proceed();
        }
        return new ResponseEntity<>("Nemate dozvolu za trazenu akciju!", HttpStatus.FORBIDDEN);
    }

    @Around("@annotation(rs.raf.ProbaVebProjekat.security.CanReadUser)")
    public Object around2(ProceedingJoinPoint joinPoint) throws Throwable{

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String token = null;
        for(int i = 0; i < methodSignature.getParameterNames().length; i++){
            if(methodSignature.getParameterNames()[i].equals("authorization")){
                if(joinPoint.getArgs()[i].toString().startsWith("Bearer")){
                    token = joinPoint.getArgs()[i].toString().split(" ")[1];
                }
            }
        }
        if(token == null){
            return new ResponseEntity<>("Niste autorizovani",HttpStatus.UNAUTHORIZED);
        }
        Claims claims = tokenService.parseToken(token);

        if(claims == null){
            return new ResponseEntity<>("Niste autorizovani",HttpStatus.UNAUTHORIZED);
        }

        Boolean isCanRead = claims.get("can_read", Boolean.class);
        if(isCanRead != null && isCanRead){
            return joinPoint.proceed();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Nemate dozvolu za trazenu akciju!");
    }

    @Around("@annotation(rs.raf.ProbaVebProjekat.security.CanDeleteUser)")
    public Object around3(ProceedingJoinPoint joinPoint) throws Throwable{

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        String token = null;
        for(int i = 0; i < methodSignature.getParameterNames().length; i++){
            if(methodSignature.getParameterNames()[i].equals("authorization")){
                if(joinPoint.getArgs()[i].toString().startsWith("Bearer")){
                    token = joinPoint.getArgs()[i].toString().split(" ")[1];
                }
            }
        }
        if(token == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Niste autorizovani");

        Claims claims = tokenService.parseToken(token);

        if(claims == null){
            return new ResponseEntity<>("Niste autorizovani",HttpStatus.UNAUTHORIZED);
        }

        Boolean isCanDelete = claims.get("can_delete", Boolean.class);
        if(isCanDelete != null && isCanDelete)
            return joinPoint.proceed();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Nemate dozvolu da obriste korisnika");

    }
    @Around("@annotation(rs.raf.ProbaVebProjekat.security.CanAddVacuum)")
    public Object around4(ProceedingJoinPoint joinPoint) throws Throwable{

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        String token = null;

        for(int i = 0; i < methodSignature.getParameterNames().length; i++) {
            if (methodSignature.getParameterNames()[i].equals("authorization")) {
                if (joinPoint.getArgs()[i].toString().startsWith("Bearer")) {
                    token = joinPoint.getArgs()[i].toString().split(" ")[1];
                }
            }
        }

            if(token == null){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Niste autorizovani");
            }
            Claims claims = tokenService.parseToken(token);
            if(claims == null)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Niste autorizovani");
            Boolean canAddVacuum = claims.get("can_add_vacuum", Boolean.class);
            if(canAddVacuum != null && canAddVacuum){
                return joinPoint.proceed();
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Nemate dozvolu da dodate usisivac");

        }

        @Around("@annotation(rs.raf.ProbaVebProjekat.security.CanSearchVacuum)")
        public Object around5(ProceedingJoinPoint joinPoint) throws Throwable{

            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            String token = null;
            for(int i = 0; i < methodSignature.getParameterNames().length; i++){
                if(methodSignature.getParameterNames()[i].equals("authorization")){
                    if(joinPoint.getArgs()[i].toString().startsWith("Bearer")){
                        token = joinPoint.getArgs()[i].toString().split(" ")[1];
                    }
                }
            }
            if(token == null)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Niste autorizovani");

            Claims claims = tokenService.parseToken(token);
            if(claims == null)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Niste autorizovani");
            Boolean isCanSearch = claims.get("can_search_vacuum", Boolean.class);
            if(isCanSearch != null && isCanSearch)
                return joinPoint.proceed();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Nemate dozvolu za trazenu akciju");
        }

        @Around("@annotation(rs.raf.ProbaVebProjekat.security.CanRemoveVacuum)")
        public Object around6(ProceedingJoinPoint joinPoint) throws Throwable{

            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            String token = null;
            for(int i = 0; i < methodSignature.getParameterNames().length; i++){
                if(methodSignature.getParameterNames()[i].equals("authorization")){
                    if(joinPoint.getArgs()[i].toString().startsWith("Bearer ")){
                     token = joinPoint.getArgs()[i].toString().split(" ")[1];
                    }
                }
            }
            if(token == null)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Niste autorizovani");

            Boolean can_remove = tokenService.parseToken(token).get("can_remove_vacuum",Boolean.class);
            if(can_remove)
                return joinPoint.proceed();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Nemate dozvolu za trazenu akciju!");


        }

        @Around("@annotation(rs.raf.ProbaVebProjekat.security.CanStartVacuum)")
        public Object around7(ProceedingJoinPoint joinPoint) throws Throwable{

            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            String token = null;
            //kada pozivamo methodSignature.getParameterNames mi tada dobijamo samo
            //imena koja imamo u potpisu metoda a kada radimo sa joinPointGetArgs tada zapravo
            //dobijamo realne vrednosti odredjenog parametra koji je prosledjen
            for(int i = 0; i < methodSignature.getParameterNames().length; i++){
                if(methodSignature.getParameterNames()[i].equals("authorization")){
                    if(joinPoint.getArgs()[i].toString().startsWith("Bearer")){
                        token = joinPoint.getArgs()[i].toString().split(" ")[1];
                    }
                }
            }
            if(token == null)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Niste autorizovani");
            Boolean isCanStart = tokenService.parseToken(token).get("can_start_vacuum", Boolean.class);
            if(isCanStart != null && isCanStart)
                return joinPoint.proceed();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Nemate dozvolu za trazenu akciju");

        }

}
