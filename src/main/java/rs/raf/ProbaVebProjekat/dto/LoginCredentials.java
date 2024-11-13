package rs.raf.ProbaVebProjekat.dto;

import lombok.Data;

@Data
public class LoginCredentials {

    private String email;
    private String lozinka;

    public LoginCredentials(String email, String lozinka){
        this.email = email;
        this.lozinka = lozinka;
    }
}
