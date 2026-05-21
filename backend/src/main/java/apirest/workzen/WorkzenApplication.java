package apirest.workzen;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Clase principal de la aplicacion Workzen.
 * Extiende SpringBootServletInitializer para permitir el despliegue en Tomcat externo.
 */
@SpringBootApplication
public class WorkzenApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		// Fija la zona horaria a Europe/Madrid para que LocalDateTime.now()
		// y la serializacion JSON coincidan con la hora local del navegador.
		TimeZone.setDefault(TimeZone.getTimeZone("Europe/Madrid"));
		return application.sources(WorkzenApplication.class);
	}

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("Europe/Madrid"));
		SpringApplication.run(WorkzenApplication.class, args);
	}

}
