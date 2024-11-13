package rs.raf.ProbaVebProjekat.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
public class Vacuum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_vacuum;

    private String name;
    private String status;

    private Boolean active;

    private LocalDateTime date;

    @ManyToOne
    private User user;

}
