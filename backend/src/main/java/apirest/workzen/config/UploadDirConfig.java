package apirest.workzen.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * Configuracion que crea los directorios de subida de archivos
 * al arrancar la aplicacion, si no existen.
 */
@Configuration
public class UploadDirConfig {

    @Value("${nominas.upload-dir}")
    private String nominasDir;

    @Value("${documentos.upload.dir:uploads/documentos}")
    private String documentosDir;

    @Value("${justificantes.upload-dir:uploads/justificantes}")
    private String justificantesDir;

    /**
     * Crea los directorios de subida si no existen.
     *
     * @throws IOException si no se pueden crear los directorios
     */
    @PostConstruct
    public void crearDirectorios() throws IOException {
        Files.createDirectories(Paths.get(nominasDir).toAbsolutePath());
        Files.createDirectories(Paths.get(documentosDir).toAbsolutePath());
        Files.createDirectories(Paths.get(justificantesDir).toAbsolutePath());
    }
}