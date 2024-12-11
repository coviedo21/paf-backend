package cl.gob.ips.proceso_pago;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ProcesoPagoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProcesoPagoApplication.class, args);
	}

}
