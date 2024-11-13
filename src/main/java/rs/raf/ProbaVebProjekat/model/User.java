package rs.raf.ProbaVebProjekat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_user;
    private String name;
    private String lastName;
    private String email;
    private String lozinka;
    private Boolean can_read;
    private Boolean can_update;
    private Boolean can_create;
    private Boolean can_delete;
    private Boolean can_add_vacuum;
    private Boolean can_search_vacuum;
    private Boolean can_remove_vacuum;
    private Boolean can_start_vacuum;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Vacuum> vacuums = new ArrayList<>();




}
