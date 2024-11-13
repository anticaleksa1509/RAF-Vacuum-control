package rs.raf.ProbaVebProjekat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import rs.raf.ProbaVebProjekat.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

        Optional<User> findUserByEmailAndLozinka(String email,String lozinka);

}
