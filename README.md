# Crediya - Solicitudes

Crediya es el backend para gestionar las solicitudes. Está construido con Spring Boot, siguiendo principios de
Arquitectura Limpia Hexagonal y utilizando programación reactiva con Spring WebFlux.

## Características

- Registro de solicitud

## Requisitos previos

- JDK 17 o superior
- Gradle 8.x

## Instalación

1. Clona el repositorio:
   ```bash
   git clone 
   cd crediya-solicitudes
   ```

2. Construye el proyecto:
   ```bash
   ./gradlew build
   ```

3. Configura las variables de entorno:
    - La configuración de la aplicación se encuentra en `applications/app-service/src/main/resources/application.yml`.
      Ajusta los valores según tu entorno (base de datos, secretos, etc.).

## Uso

- Inicia el servidor de desarrollo:
  ```bash
  ./gradlew bootRun
  ```
- El servicio estará disponible en `http://localhost:8080` (o el puerto configurado en `application.yml`).

## Documentación API

La documentación de la API se encuentra en `http://localhost:8080/swagger-ui/index.html` una vez que el servidor esté en
funcionamiento.

## Estructura del proyecto

El proyecto sigue una arquitectura limpia y está organizado en los siguientes módulos de Gradle:

```
Solcitudes/
├── applications/app-service/     # Módulo principal y configuración de Spring Boot
├── domain/
│   ├── model/                    # Entidades y modelos del dominio
│   └── usecase/                  # Lógica de negocio y casos de uso
├── infrastructure/
│   ├── driven-adapters/          # Implementaciones para tecnología externa (BD, APIs)
│   └── entry-points/             # Controladores (REST, etc.)
├── build.gradle                  # Script de construcción principal
├── settings.gradle               # Definición de los módulos del proyecto
└── README.md
```

## Pruebas

Ejecuta las pruebas unitarias y de integración con:

```bash
./gradlew test
```
