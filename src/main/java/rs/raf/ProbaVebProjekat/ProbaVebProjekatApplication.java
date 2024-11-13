package rs.raf.ProbaVebProjekat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ProbaVebProjekatApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProbaVebProjekatApplication.class, args);
	}

}
