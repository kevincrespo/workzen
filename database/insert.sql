USE workzen;

-- =============================================
-- DATOS DE PRUEBA
-- Password de todos los usuarios: admin1234
-- =============================================

-- Usuarios (4)
INSERT INTO usuarios (id, email, password) VALUES
(1, 'admin@workzen.es',     '$2a$12$nZmit0jGDh4hDzR4aHpdrOycncrPlUPWeQamSABWNvgQLilPImWVC'),
(2, 'rrhh@workzen.es',      '$2a$12$nZmit0jGDh4hDzR4aHpdrOycncrPlUPWeQamSABWNvgQLilPImWVC'),
(3, 'empleado1@workzen.es', '$2a$12$nZmit0jGDh4hDzR4aHpdrOycncrPlUPWeQamSABWNvgQLilPImWVC'),
(4, 'empleado2@workzen.es', '$2a$12$nZmit0jGDh4hDzR4aHpdrOycncrPlUPWeQamSABWNvgQLilPImWVC');

-- Privilegios (3)
INSERT INTO privilegios (id, nombre) VALUES
(1, 'admin'),
(2, 'rrhh'),
(3, 'empleado');

-- Privilegios - Usuarios
INSERT INTO privilegios_usuarios (usuario_id, privilegio_id) VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 3);

-- Departamentos (4)
INSERT INTO departamentos (id, nombre) VALUES
(1, 'Administracion'),
(2, 'Recursos Humanos'),
(3, 'Tecnologia'),
(4, 'Marketing');

-- Empleados (4)
INSERT INTO empleados (id, usuario_id, departamento_id, nombre, apellido1, apellido2, nif, numero_ss, direccion, provincia, localidad, codigo_postal, fecha_nacimiento, iban, telefono, dias_vacaciones) VALUES
(1, 1, 1, 'Carlos',  'Garcia',    'Perez',    '12345678A', '281234567890', 'Calle Mayor 10',       'Madrid',    'Madrid',     28001, '1985-03-15', 'ES7620770024003102575766', '600111222', 22),
(2, 2, 2, 'Ana',     'Rodriguez', 'Gomez',    '23456789B', '282345678901', 'Avenida Libertad 25',  'Madrid',    'Alcobendas', 28100, '1990-07-22', 'ES1200810001220001234567', '600333444', 22),
(3, 3, 3, 'Lucia',   'Martinez',  'Ruiz',     '34567890C', '283456789012', 'Calle Sol 8',          'Barcelona', 'Barcelona',  08001, '1992-11-05', 'ES9121000418450200051332', '600555666', 22),
(4, 4, 4, 'David',   'Lopez',     'Santos',   '45678901D', '284567890123', 'Plaza Espana 3',       'Valencia',  'Valencia',   46001, '1988-01-30', 'ES6000491500051234567892', '600777888', 22);

-- Ausencias (15)
INSERT INTO ausencias (id, empleado_id, fecha_inicio, fecha_fin, estado, tipo) VALUES
(1,  3, '2026-01-05 00:00:00', '2026-01-09 00:00:00', 'aprobado',  'vacaciones'),
(2,  4, '2026-02-16 00:00:00', '2026-02-20 00:00:00', 'aprobado',  'vacaciones'),
(3,  3, '2026-01-15 00:00:00', '2026-01-16 00:00:00', 'aprobado',  'medico'),
(4,  4, '2026-02-10 00:00:00', '2026-02-11 00:00:00', 'aprobado',  'medico'),
(5,  3, '2026-03-02 00:00:00', '2026-03-02 00:00:00', 'aprobado',  'permiso'),
(6,  4, '2026-04-06 00:00:00', '2026-04-10 00:00:00', 'pendiente', 'vacaciones'),
(7,  3, '2026-03-20 00:00:00', '2026-03-21 00:00:00', 'aprobado',  'medico'),
(8,  4, '2026-01-20 00:00:00', '2026-01-21 00:00:00', 'rechazado', 'otros'),
(9,  3, '2026-06-01 00:00:00', '2026-06-05 00:00:00', 'pendiente', 'vacaciones'),
(10, 1, '2026-07-14 00:00:00', '2026-07-25 00:00:00', 'aprobado',  'vacaciones'),
(11, 1, '2026-01-20 00:00:00', '2026-01-21 00:00:00', 'aprobado',  'medico'),
(12, 2, '2026-02-05 00:00:00', '2026-02-06 00:00:00', 'aprobado',  'medico'),
(13, 4, '2026-03-10 00:00:00', '2026-03-11 00:00:00', 'aprobado',  'medico'),
(14, 1, '2026-04-15 00:00:00', '2026-04-16 00:00:00', 'aprobado',  'medico'),
(15, 2, '2026-05-12 00:00:00', '2026-05-13 00:00:00', 'aprobado',  'medico');

-- Justificantes (10) - vinculados a ausencias medicas aprobadas
INSERT INTO justificantes (id, ausencia_id, motivo, archivo) VALUES
(1,  3,  'Consulta medica de urgencia',             'justificante_001.pdf'),
(2,  4,  'Revision traumatologia',                  'justificante_002.pdf'),
(3,  7,  'Analisis de sangre programado',           'justificante_003.pdf'),
(4,  11, 'Gripe con fiebre alta',                   'justificante_004.pdf'),
(5,  12, 'Consulta con especialista',               'justificante_005.jpg'),
(6,  13, 'Dolor lumbar agudo',                      'justificante_006.pdf'),
(7,  14, 'Revision oftalmologica anual',            'justificante_007.png'),
(8,  15, 'Alergia estacional severa',               'justificante_008.pdf'),
(9,  3,  'Certificado adicional del medico',        'justificante_009.pdf'),
(10, 4,  'Informe de seguimiento traumatologia',    'justificante_010.pdf');

-- Fichajes (12)
INSERT INTO fichajes (id, empleado_id, fecha_entrada, fecha_salida, estado) VALUES
(1,  1, '2026-01-05 08:00:00', '2026-01-05 17:00:00', 'finalizado'),
(2,  1, '2026-01-06 08:30:00', '2026-01-06 17:15:00', 'finalizado'),
(3,  2, '2026-01-05 09:00:00', '2026-01-05 18:00:00', 'finalizado'),
(4,  3, '2026-01-07 08:00:00', '2026-01-07 16:30:00', 'finalizado'),
(5,  3, '2026-01-08 07:45:00', '2026-01-08 16:00:00', 'finalizado'),
(6,  4, '2026-01-05 09:30:00', '2026-01-05 18:30:00', 'finalizado'),
(7,  4, '2026-01-06 09:00:00', '2026-01-06 17:45:00', 'finalizado'),
(8,  1, '2026-02-02 08:00:00', '2026-02-02 17:00:00', 'finalizado'),
(9,  2, '2026-02-02 09:00:00', '2026-02-02 18:00:00', 'finalizado'),
(10, 3, '2026-02-03 08:15:00', '2026-02-03 16:45:00', 'finalizado'),
(11, 4, '2026-02-03 09:00:00', '2026-02-03 17:30:00', 'finalizado'),
(12, 1, '2026-02-28 08:00:00', NULL,                   'activo');

-- Pausas (12)
INSERT INTO pausas (id, fichaje_id, hora_inicio, hora_fin) VALUES
(1,  1,  '2026-01-05 10:00:00', '2026-01-05 10:15:00'),
(2,  1,  '2026-01-05 13:00:00', '2026-01-05 13:30:00'),
(3,  2,  '2026-01-06 10:30:00', '2026-01-06 10:45:00'),
(4,  3,  '2026-01-05 11:00:00', '2026-01-05 11:15:00'),
(5,  3,  '2026-01-05 14:00:00', '2026-01-05 14:30:00'),
(6,  4,  '2026-01-07 10:00:00', '2026-01-07 10:20:00'),
(7,  5,  '2026-01-08 12:00:00', '2026-01-08 12:30:00'),
(8,  6,  '2026-01-05 11:00:00', '2026-01-05 11:15:00'),
(9,  7,  '2026-01-06 13:00:00', '2026-01-06 13:45:00'),
(10, 8,  '2026-02-02 10:00:00', '2026-02-02 10:15:00'),
(11, 9,  '2026-02-02 11:30:00', '2026-02-02 11:45:00'),
(12, 10, '2026-02-03 10:00:00', '2026-02-03 10:30:00');

-- Modificaciones (10)
INSERT INTO modificaciones (id, empleado_id, pausa_id, campo, valor_anterior, valor_nuevo, motivo) VALUES
(1,  1, 1,    'hora_fin',       '10:10:00', '10:15:00', 'Correccion de hora de fin de pausa'),
(2,  1, 2,    'hora_inicio',    '13:05:00', '13:00:00', 'Ajuste por error de fichaje'),
(3,  2, 4,    'hora_fin',       '11:10:00', '11:15:00', 'Empleado notifico error en pausa'),
(4,  3, 6,    'hora_inicio',    '10:05:00', '10:00:00', 'Retraso al pulsar boton'),
(5,  3, 7,    'hora_fin',       '12:25:00', '12:30:00', 'Correccion solicitada por empleado'),
(6,  4, 8,    'hora_fin',       '11:10:00', '11:15:00', 'Ajuste manual por supervision'),
(7,  4, 9,    'hora_inicio',    '13:05:00', '13:00:00', 'Fallo en la app al registrar'),
(8,  1, 10,   'hora_fin',       '10:10:00', '10:15:00', 'Correccion por RRHH'),
(9,  2, 11,   'hora_inicio',    '11:35:00', '11:30:00', 'Error al fichar pausa'),
(10, 3, 12,   'hora_fin',       '10:25:00', '10:30:00', 'Ajuste de pausa');

-- Nominas (12) - 3 meses x 4 empleados
INSERT INTO nominas (id, empleado_id, fecha, archivo) VALUES
(1,  1, '2026-01-31', 'nomina_carlos_enero2026.pdf'),
(2,  2, '2026-01-31', 'nomina_ana_enero2026.pdf'),
(3,  3, '2026-01-31', 'nomina_lucia_enero2026.pdf'),
(4,  4, '2026-01-31', 'nomina_david_enero2026.pdf'),
(5,  1, '2026-02-28', 'nomina_carlos_febrero2026.pdf'),
(6,  2, '2026-02-28', 'nomina_ana_febrero2026.pdf'),
(7,  3, '2026-02-28', 'nomina_lucia_febrero2026.pdf'),
(8,  4, '2026-02-28', 'nomina_david_febrero2026.pdf'),
(9,  1, '2026-03-31', 'nomina_carlos_marzo2026.pdf'),
(10, 2, '2026-03-31', 'nomina_ana_marzo2026.pdf'),
(11, 3, '2026-03-31', 'nomina_lucia_marzo2026.pdf'),
(12, 4, '2026-03-31', 'nomina_david_marzo2026.pdf');

-- Contratos (10)
INSERT INTO contratos (id, empleado_id, tipo, salario, fecha, archivo) VALUES
(1,  1, 'alta',          35000, '2026-01-01', 'contrato_carlos_alta.pdf'),
(2,  2, 'alta',          30000, '2026-01-01', 'contrato_ana_alta.pdf'),
(3,  3, 'alta',          28000, '2026-01-15', 'contrato_lucia_alta.pdf'),
(4,  4, 'alta',          27000, '2026-02-01', 'contrato_david_alta.pdf'),
(5,  1, 'modificacion',  37000, '2026-03-01', 'contrato_carlos_mod.pdf'),
(6,  3, 'modificacion',  30000, '2026-04-01', 'contrato_lucia_mod.pdf'),
(7,  2, 'modificacion',  32000, '2026-05-01', 'contrato_ana_mod.pdf'),
(8,  4, 'modificacion',  29000, '2026-06-01', 'contrato_david_mod.pdf'),
(9,  1, 'modificacion',  40000, '2026-07-01', 'contrato_carlos_mod2.pdf'),
(10, 3, 'baja',          30000, '2026-09-30', 'contrato_lucia_baja.pdf');

-- Certificados (10)
INSERT INTO certificados (id, empleado_id, nombre, fecha, archivo) VALUES
(1,  1, 'Certificado de empresa',           '2026-01-10', 'cert_empresa_carlos.pdf'),
(2,  2, 'Certificado de empresa',           '2026-01-10', 'cert_empresa_ana.pdf'),
(3,  3, 'Certificado de empresa',           '2026-01-15', 'cert_empresa_lucia.pdf'),
(4,  4, 'Certificado de empresa',           '2026-02-01', 'cert_empresa_david.pdf'),
(5,  1, 'Certificado de retenciones',       '2026-01-31', 'cert_retenciones_carlos.pdf'),
(6,  2, 'Certificado de retenciones',       '2026-01-31', 'cert_retenciones_ana.pdf'),
(7,  3, 'Certificado de retenciones',       '2026-01-31', 'cert_retenciones_lucia.pdf'),
(8,  4, 'Certificado de retenciones',       '2026-01-31', 'cert_retenciones_david.pdf'),
(9,  1, 'Vida laboral',                     '2026-03-15', 'cert_vidalaboral_carlos.pdf'),
(10, 3, 'Certificado de formacion',         '2026-04-20', 'cert_formacion_lucia.pdf');

-- Dietas (10)
INSERT INTO dietas (id, empleado_id, tipo, fecha_inicio, fecha_fin, motivo, estado) VALUES
(1,  1, 'nacional',       '2026-01-12', '2026-01-13', 'Reunion con cliente en Barcelona',       'aprobado'),
(2,  3, 'nacional',       '2026-01-20', '2026-01-20', 'Visita a oficina central Madrid',        'aprobado'),
(3,  4, 'internacional',  '2026-02-03', '2026-02-07', 'Feria tecnologica en Berlin',            'aprobado'),
(4,  1, 'nacional',       '2026-02-18', '2026-02-19', 'Formacion presencial en Sevilla',        'aprobado'),
(5,  2, 'nacional',       '2026-03-05', '2026-03-05', 'Auditoria oficina Valencia',             'aprobado'),
(6,  3, 'internacional',  '2026-03-16', '2026-03-20', 'Conferencia en Lisboa',                  'pendiente'),
(7,  4, 'nacional',       '2026-04-01', '2026-04-02', 'Reunion con proveedor en Bilbao',        'pendiente'),
(8,  1, 'internacional',  '2026-05-10', '2026-05-14', 'Workshop en Paris',                      'pendiente'),
(9,  2, 'nacional',       '2026-06-08', '2026-06-09', 'Evento corporativo en Malaga',           'rechazado'),
(10, 3, 'nacional',       '2026-04-22', '2026-04-22', 'Visita cliente en Zaragoza',             'anulado');

-- Teletrabajo (10)
INSERT INTO teletrabajo (id, empleado_id, fecha_inicio, fecha_fin, estado) VALUES
(1,  3, '2026-01-12', '2026-01-16', 'aprobado'),
(2,  4, '2026-01-19', '2026-01-23', 'aprobado'),
(3,  3, '2026-02-02', '2026-02-06', 'aprobado'),
(4,  1, '2026-02-09', '2026-02-13', 'aprobado'),
(5,  4, '2026-02-23', '2026-02-27', 'aprobado'),
(6,  3, '2026-03-09', '2026-03-13', 'pendiente'),
(7,  2, '2026-03-16', '2026-03-20', 'pendiente'),
(8,  4, '2026-04-06', '2026-04-10', 'pendiente'),
(9,  1, '2026-04-13', '2026-04-17', 'rechazado'),
(10, 3, '2026-05-04', '2026-05-08', 'anulado');

-- Documentos (10)
INSERT INTO documentos (id, nombre, tipo, archivo) VALUES
(1,  'Convenio colectivo 2026',                'laboral',    'convenio_2026.pdf'),
(2,  'Politica de vacaciones',                 'laboral',    'politica_vacaciones.pdf'),
(3,  'Normativa teletrabajo',                  'laboral',    'normativa_teletrabajo.pdf'),
(4,  'Manual de bienvenida',                   'manuales',   'manual_bienvenida.pdf'),
(5,  'Guia de herramientas internas',          'manuales',   'guia_herramientas.pdf'),
(6,  'Manual de seguridad informatica',        'manuales',   'manual_seguridad.pdf'),
(7,  'Plan de prevencion de riesgos',          'bienestar',  'plan_prevencion.pdf'),
(8,  'Protocolo de acoso laboral',             'bienestar',  'protocolo_acoso.pdf'),
(9,  'Programa de bienestar emocional',        'bienestar',  'programa_bienestar.pdf'),
(10, 'Politica de desconexion digital',        'laboral',    'desconexion_digital.pdf');

-- Festivos 2026 (12)
INSERT INTO festivos (id, nombre, fecha) VALUES
(1,  'Ano Nuevo',                       '2026-01-01'),
(2,  'Dia de Reyes',                    '2026-01-06'),
(3,  'Viernes Santo',                   '2026-04-03'),
(4,  'Dia del Trabajo',                 '2026-05-01'),
(5,  'Asuncion de la Virgen',           '2026-08-15'),
(6,  'Fiesta Nacional de Espana',       '2026-10-12'),
(7,  'Todos los Santos',                '2026-11-01'),
(8,  'Dia de la Constitucion',          '2026-12-06'),
(9,  'Inmaculada Concepcion',           '2026-12-08'),
(10, 'Navidad',                         '2026-12-25'),
(11, 'San Jose',                        '2026-03-19'),
(12, 'Santiago Apostol',                '2026-07-25');

-- Notificaciones (12)
INSERT INTO notificaciones (id, empleado_id, tipo, mensaje, leida, fecha_creacion) VALUES
(1,  3, 'nomina_nueva',           'Se ha subido tu nomina de enero 2026',              1, '2026-01-31 10:00:00'),
(2,  4, 'nomina_nueva',           'Se ha subido tu nomina de enero 2026',              1, '2026-01-31 10:05:00'),
(3,  3, 'ausencia_aprobada',      'Tu ausencia del 05/01 al 09/01 ha sido aprobada',  1, '2026-01-04 09:00:00'),
(4,  4, 'ausencia_aprobada',      'Tu ausencia del 16/02 al 20/02 ha sido aprobada',  0, '2026-02-15 11:00:00'),
(5,  4, 'ausencia_rechazada',     'Tu ausencia del 20/01 al 21/01 ha sido rechazada', 1, '2026-01-19 14:00:00'),
(6,  3, 'teletrabajo_aprobado',   'Tu teletrabajo del 12/01 al 16/01 ha sido aprobado', 1, '2026-01-11 08:30:00'),
(7,  4, 'teletrabajo_aprobado',   'Tu teletrabajo del 19/01 al 23/01 ha sido aprobado', 0, '2026-01-18 09:15:00'),
(8,  1, 'dieta_aprobada',         'Tu dieta del 12/01 al 13/01 ha sido aprobada',     1, '2026-01-11 16:00:00'),
(9,  3, 'contrato_nuevo',         'Se ha subido un nuevo contrato',                    0, '2026-01-15 12:00:00'),
(10, 1, 'nomina_nueva',           'Se ha subido tu nomina de febrero 2026',            0, '2026-02-28 10:00:00'),
(11, 2, 'nomina_nueva',           'Se ha subido tu nomina de febrero 2026',            0, '2026-02-28 10:05:00'),
(12, 1, 'ausencia_aprobada',      'Tu ausencia del 14/07 al 25/07 ha sido aprobada',  0, '2026-02-20 15:30:00');