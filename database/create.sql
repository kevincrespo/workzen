CREATE DATABASE IF NOT EXISTS workzen;
USE workzen;

-- Usuarios
CREATE TABLE usuarios (
    id INT NOT NULL AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

-- Privilegios
CREATE TABLE privilegios (
    id INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

-- Privilegios - Usuarios (M:N)
CREATE TABLE privilegios_usuarios (
    id INT NOT NULL AUTO_INCREMENT,
    usuario_id INT NOT NULL,
    privilegio_id INT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (privilegio_id) REFERENCES privilegios(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

-- Departamentos
CREATE TABLE departamentos (
    id INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

-- Empleados
CREATE TABLE empleados (
    id INT NOT NULL AUTO_INCREMENT,
    usuario_id INT NOT NULL,
    departamento_id INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido1 VARCHAR(100) NOT NULL,
    apellido2 VARCHAR(100) DEFAULT NULL,
    nif VARCHAR(20) NOT NULL,
    numero_ss VARCHAR(30) NOT NULL,
    direccion TEXT NOT NULL,
    provincia VARCHAR(100) NOT NULL,
    localidad VARCHAR(100) NOT NULL,
    codigo_postal INT NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    iban VARCHAR(50) DEFAULT NULL,
    telefono VARCHAR(50) DEFAULT NULL,
    dias_vacaciones INT NOT NULL DEFAULT 22,
    PRIMARY KEY (id),
    UNIQUE KEY (nif),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (departamento_id) REFERENCES departamentos(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

-- Ausencias
CREATE TABLE ausencias (
    id INT NOT NULL AUTO_INCREMENT,
    empleado_id INT NOT NULL,
    fecha_inicio DATETIME NOT NULL,
    fecha_fin DATETIME NOT NULL,
    estado ENUM('pendiente','aprobado','rechazado','anulado') DEFAULT NULL,
    tipo ENUM('vacaciones','medico','permiso','otros') DEFAULT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (empleado_id) REFERENCES empleados(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

-- Justificantes
CREATE TABLE justificantes (
    id INT NOT NULL AUTO_INCREMENT,
    ausencia_id INT NOT NULL,
    motivo TEXT NOT NULL,
    archivo VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (ausencia_id) REFERENCES ausencias(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

-- Fichajes
CREATE TABLE fichajes (
    id INT NOT NULL AUTO_INCREMENT,
    empleado_id INT NOT NULL,
    fecha_entrada DATETIME NOT NULL,
    fecha_salida DATETIME DEFAULT NULL,
    estado ENUM('activo','finalizado','error') DEFAULT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (empleado_id) REFERENCES empleados(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

-- Pausas
CREATE TABLE pausas (
    id INT NOT NULL AUTO_INCREMENT,
    fichaje_id INT NOT NULL,
    hora_inicio DATETIME DEFAULT NULL,
    hora_fin DATETIME DEFAULT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (fichaje_id) REFERENCES fichajes(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

-- Modificaciones
CREATE TABLE modificaciones (
    id INT NOT NULL AUTO_INCREMENT,
    empleado_id INT NOT NULL,
    pausa_id INT DEFAULT NULL,
    campo VARCHAR(255) NOT NULL,
    valor_anterior VARCHAR(255) NOT NULL,
    valor_nuevo VARCHAR(255) NOT NULL,
    motivo TEXT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (empleado_id) REFERENCES empleados(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (pausa_id) REFERENCES pausas(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

-- Nominas
CREATE TABLE nominas (
    id INT NOT NULL AUTO_INCREMENT,
    empleado_id INT NOT NULL,
    fecha DATE NOT NULL,
    archivo VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (empleado_id) REFERENCES empleados(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

-- Contratos
CREATE TABLE contratos (
    id INT NOT NULL AUTO_INCREMENT,
    empleado_id INT NOT NULL,
    tipo ENUM('alta','baja','modificacion') DEFAULT NULL,
    salario INT DEFAULT NULL,
    fecha DATE NOT NULL,
    archivo VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (empleado_id) REFERENCES empleados(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

-- Certificados
CREATE TABLE certificados (
    id INT NOT NULL AUTO_INCREMENT,
    empleado_id INT NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    fecha DATE NOT NULL,
    archivo VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (empleado_id) REFERENCES empleados(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

-- Dietas
CREATE TABLE dietas (
    id INT NOT NULL AUTO_INCREMENT,
    empleado_id INT NOT NULL,
    tipo ENUM('nacional','internacional') DEFAULT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    motivo TEXT,
    estado ENUM('pendiente','aprobado','rechazado','anulado') DEFAULT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (empleado_id) REFERENCES empleados(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

-- Teletrabajo
CREATE TABLE teletrabajo (
    id INT NOT NULL AUTO_INCREMENT,
    empleado_id INT NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    estado ENUM('pendiente','aprobado','rechazado','anulado') DEFAULT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (empleado_id) REFERENCES empleados(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

-- Documentos
CREATE TABLE documentos (
    id INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(255) NOT NULL,
    tipo ENUM('laboral','manuales','bienestar') DEFAULT NULL,
    archivo VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

-- Festivos
CREATE TABLE festivos (
    id INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(255) NOT NULL,
    fecha DATE NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

-- Notificaciones
CREATE TABLE notificaciones (
    id INT NOT NULL AUTO_INCREMENT,
    empleado_id INT NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    mensaje VARCHAR(500) NOT NULL,
    leida TINYINT(1) DEFAULT 0,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (empleado_id) REFERENCES empleados(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;


