# Despliegue de Workzen

## URL de producción

https://workzen.kevincrespodev.com

## Estructura en el VPS

Frontend Angular:

/var/www/workzen.kevincrespodev.com

Backend Spring Boot:

/opt/workzen/backend/workzen.jar

Variables de entorno del backend:

/opt/workzen/backend/workzen.env

Uploads:

/var/workzen/uploads

## Servicio systemd

Nombre del servicio:

workzen

Comandos útiles:

sudo systemctl status workzen
sudo systemctl restart workzen
journalctl -u workzen -n 100 --no-pager

## Base de datos

Motor:

MariaDB

Base de datos:

workzen

Usuario de aplicación:

workzen_user

## CI/CD

El despliegue automático se ejecuta con GitHub Actions al hacer push a main.

Workflow:

.github/workflows/deploy.yml

Secrets usados en GitHub:

VPS_HOST
VPS_USER
VPS_SSH_KEY

## Flujo de despliegue

1. GitHub Actions compila el backend.
2. GitHub Actions compila el frontend.
3. Sube Angular al VPS.
4. Sube workzen.jar al VPS.
5. Reinicia el servicio workzen.