# DOSW_ParcialT2_KevinAngel

## Índice

1. [Datos del estudiante](#1-datos-del-estudiante)
2. [Descripción del proyecto](#2-descripción-del-proyecto)
3. [Despliegue](#3-despliegue)
4. [Estructura del proyecto](#4-estructura-del-proyecto)
5. [Tecnologías y dependencias](#5-tecnologías-y-dependencias)
6. [Configuración y variables de entorno](#6-configuración-y-variables-de-entorno)
7. [Cómo ejecutar](#7-cómo-ejecutar)
8. [Pruebas](#8-pruebas)
9. [Docker](#9-docker)
10. [CI/CD](#10-cicd)
11. [Herramientas de modelado y diseño (evidencias)](#11-herramientas-de-modelado-y-diseño-evidencias)
12. [Punto-1](#12-Punto-1)
13. [Punto-2](#13-Punto-2)


## 1. Datos del estudiante

- **Nombre completo:** Kevin Andrey Angel Acevedo,santiago andres garcia
- **Grupo:** 1
- **Profesor:** andres martin cantor

## 2. Descripción del proyecto

Servicio REST base para el parcial práctico del segundo tercio de DOSW. Incluye una estructura MVC, persistencia preparada para MongoDB, validación, seguridad con Spring Security, documentación OpenAPI y un endpoint de salud. La funcionalidad de negocio se construirá durante el laboratorio.

## 3. Despliegue

- **Servicio Azure:** [DOSW Parcial T2](https://dosw-parcial-t2-kevin-fuapdsgbgcgebnbn.centralus-01.azurewebsites.net)
- **Health:** [GET /api/v1/health](https://dosw-parcial-t2-kevin-fuapdsgbgcgebnbn.centralus-01.azurewebsites.net/api/v1/health) — verificado, responde `{"service":"DOSW-ParcialT2","status":"UP"}`.
- **Swagger:** [Swagger UI](https://dosw-parcial-t2-kevin-fuapdsgbgcgebnbn.centralus-01.azurewebsites.net/swagger-ui/index.html) — verificado.

![Respuesta del health desplegado en Azure](docs/images/azure-health.png)

![Swagger desplegado en Azure](docs/images/azure-swagger.png)

## 4. Estructura del proyecto

```text
docs/                                      Diagramas, imágenes y evidencias
├── images/
└── uml/
src/main/java/edu/dosw/parcial/
├── config/                                Configuración de Spring y seguridad
├── controller/
│   ├── dtos/request/                      DTOs de entrada
│   ├── dtos/response/                     DTOs de salida
│   ├── handlers/                          Manejo global de errores
│   └── mappers/                           Mapeo entre DTOs y dominio
├── core/
│   ├── models/                             Modelos de dominio
│   ├── services/                           Casos de uso
│   ├── utils/                              Utilidades
│   └── validators/                         Validaciones de dominio
├── main/                                  Clase de inicio de Spring Boot
└── persistence/
    ├── entities/                           Documentos MongoDB
    ├── mappers/                            Mapeo entre dominio y documentos
    └── repositories/                       Repositorios MongoDB
src/main/resources/                         Configuración de la aplicación
src/test/java/edu/dosw/parcial/             Pruebas unitarias
src/test/resources/                         Recursos de pruebas
```

El Mapper IN convierte DTOs en objetos de dominio y viceversa. El Mapper OUT convierte entre dominio y documentos MongoDB. Los controladores no dependen de documentos de persistencia.


![Diagrama UML de componentes general](diagrama_componentes_general.svg)

## 5. Tecnologías y dependencias

| Tecnología | Versión | Uso |
|---|---:|---|
| Java | 21 | Lenguaje y runtime |
| Spring Boot | 3.5.16 | Aplicación y servicios REST |
| Spring Data MongoDB | BOM de Spring Boot | Persistencia documental |
| Spring Security | BOM de Spring Boot | Seguridad HTTP |
| JJWT | 0.12.6 | Soporte para tokens JWT |
| Springdoc OpenAPI | 2.8.5 | Swagger UI y OpenAPI |
| MapStruct | 1.5.5.Final | Mapeo de objetos |
| Lombok | BOM de Spring Boot | Reducción de código repetitivo |
| JUnit / Mockito | BOM de Spring Boot | Pruebas |
| JaCoCo | 0.8.12 | Reporte de cobertura |
| Sonar Maven Plugin | 3.10.0.2594 | Análisis SonarQube |
| Docker | Multi-stage, Java 21 | Empaquetado del servicio |

## 6. Configuración y variables de entorno

| Variable | Descripción | Ejemplo ficticio |
|---|---|---|
| `MONGODB_URI` | URI de conexión a MongoDB | `mongodb://localhost:27017/dosw_parcial` |
| `JWT_SECRET` | Secreto JWT codificado en Base64 (mínimo 44 caracteres) | `<secreto-base64-de-44-caracteres>` |
| `PORT` | Puerto HTTP | `8080` |
| `DOCKERHUB_USERNAME` | Usuario de Docker Hub, como secret de GitHub | `<usuario>` |
| `DOCKERHUB_TOKEN` | Token de acceso de Docker Hub, como secret de GitHub | `<token>` |
| `AZURE_CREDENTIALS` | JSON del Service Principal, como secret de GitHub | `<json-del-service-principal>` |
| `AZURE_WEBAPP_NAME` | Nombre del App Service, como secret de GitHub | `<nombre-app>` |

No guardar credenciales reales en archivos versionados. En Azure también configurar `MONGODB_URI`, `JWT_SECRET` y `WEBSITES_PORT=8080` en Application settings.

## 7. Cómo ejecutar

Requiere Java 21, Maven 3.9+ y MongoDB local o accesible.

```powershell
# Iniciar MongoDB local con Docker
docker run -d -p 27017:27017 --name mongo-parcial mongo:7

# Definir un secreto Base64 local temporal de 48 bytes (PowerShell)
[byte[]]$secretBytes = 1..48 | ForEach-Object { Get-Random -Maximum 256 }
$env:JWT_SECRET = [Convert]::ToBase64String($secretBytes)

# Ejecutar con Maven
mvn spring-boot:run
```

El servicio escucha en `http://localhost:8080`. El endpoint `GET /api/v1/health` devuelve estado y nombre del servicio.

## 8. Pruebas

```powershell
mvn test
mvn clean verify
```

`mvn clean verify` pasó con 1 prueba. JaCoCo genera el reporte HTML en `target/site/jacoco/index.html`; la captura muestra la cobertura actual del scaffolding (13%).

![Reporte de cobertura JaCoCo](docs/images/jacoco-reporte.png)

El análisis SonarQube se puede ejecutar con `mvn sonar:sonar -Dsonar.host.url=<url-del-servidor> -Dsonar.token=<token>`; configurar la URL y el token fuera del repositorio.

## 9. Docker

```powershell
docker build -t dosw-parcial:local .
$env:JWT_SECRET = [Convert]::ToBase64String([byte[]](1..48 | ForEach-Object { Get-Random -Maximum 256 }))
docker run --rm -p 8080:8080 -e JWT_SECRET=$env:JWT_SECRET dosw-parcial:local
```

Para utilizar otra base de datos, pasar también `-e MONGODB_URI='<uri-de-mongodb>'`.

![Imagen Docker construida y contenedor local en ejecución](docs/images/docker-contenedor-redacted.png)

![Endpoint health local respondiendo](docs/images/docker-health-local.png)

## 10. CI/CD

El workflow [`.github/workflows/ci-cd.yml`](.github/workflows/ci-cd.yml) ejecuta `mvn verify` en los pushes a `main` y `develop`, publica la imagen `dosw-parcial-t2:latest` en Docker Hub y la despliega en Azure App Service. Requiere los secrets `DOCKERHUB_USERNAME`, `DOCKERHUB_TOKEN`, `AZURE_CREDENTIALS`, `AZURE_WEBAPP_NAME` y `JWT_SECRET`. El workflow terminó correctamente con los jobs `test`, `docker` y `deploy` en verde.

![Pipeline CI/CD exitoso en GitHub Actions](docs/images/github-actions-exitoso.png)

## 11. Herramientas de modelado y diseño (evidencias)

- Herramienta de modelado elegida: Draw.io.
- Diseño de interfaces: Figma.

![Acceso a Draw.io](docs/images/evidencia-drawio.png)

![Acceso a Figma](docs/images/evidencia-figma.png)

<<<<<<< HEAD



=======
## 12. Punto-1

Respuestas preguntas:   
#### a. ¿Cuál es la diferencia entre una validación de entrada y una validación de negocio? Seleccionen tres reglas del caso, incluyendo al menos una de cada tipo, y justifiquen su clasificación.
La validación de entrada verifica que los datos enviados por el usuario tengan el formato, tipo y estructura correctos, como campos obligatorios, formatos de fecha o números de teléfono válidos. Se implementa generalmente en la capa Controller/DTO.

La validación de negocio comprueba que la solicitud cumpla las reglas y restricciones propias del sistema, considerando las condiciones del negocio y el estado de la base de datos. Por ejemplo, verificar la disponibilidad del barbero, evitar turnos duplicados o comprobar el límite de reservas permitidas. Se implementa principalmente en la capa de servicios o dominio.

#### b. En "Pedir un turno", ¿cómo debe responder la API si fallan varias condiciones? Expliquen qué error se informa y qué cambios deben evitarse.
La API debe identificar y comunicar los errores de forma consistente, deteniendo el proceso cuando una validación falla y evitando crear el turno o modificar la base de datos si la solicitud no cumple las condiciones necesarias. Para ello, puede utilizar una cadena de validaciones y un manejador global de excepciones.

La respuesta debe incluir un código HTTP adecuado al tipo de error (por ejemplo, 400 para datos incorrectos o 409 para conflictos), junto con un cuerpo JSON estructurado que contenga el código de error específico, un mensaje explicativo y, cuando corresponda, una marca de tiempo timestamp.
>>>>>>> 91f98028b9b25f6908f6daf8b979d75c189d8300

## 13. Punto-2

### a. ¿Qué problemas aparecen cuando no se separan correctamente las capas? Presenten un ejemplo del caso y expliquen cómo distribuyen sus responsabilidades.

**Lo que pasa si mezclamos todo (Problemas):**
1. **Código Espagueti (Alto acoplamiento):** Si ponemos todo en un solo lugar, un cambio tonto en la base de datos termina rompiendo las rutas HTTP.
2. **No podemos hacer pruebas unitarias:** No podríamos probar si la regla de los 3 clientes por hora funciona sin tener que prender una base de datos real.
3. **Repetimos código:** Copiaríamos la lógica de "revisar si el barbero está libre" en cada clase nueva que creemos.
4. **Fragilidad:** Si mañana cambiamos de base de datos, habría que rehacer todo el proyecto.

**Nuestro ejemplo en Bob's Barber (El "Fat Controller"):**
Si metiéramos todo en el `ReservaController` (contar reservas en Mongo para el `E01`, validar si el barbero sabe cortar el pelo para el `E02` y sumar los precios), ese controlador quedaría gigantesco. Si el profesor nos pide cambiar el límite a 4 clientes, tendríamos que ir a tocar el controlador web, lo cual no tiene sentido.

**Por eso distribuimos las responsabilidades así:**
* **Capa Controller (`controller`):** Su único trabajo es el transporte HTTP. Recibe el JSON, hace una validación sintáctica rápida (`@Valid`), le pasa la pelota al Servicio y devuelve el código (`201`, `400`, `409`, `422`).
* **Capa Servicios (`core.services`):** Son los directores de orquesta. Coordinan los validadores y le dicen a los repositorios cuándo guardar.
* **Capa Dominio (`core.models`):** Nuestras entidades no son tontas. El objeto `Barbero` sabe si trabaja ese día; la `Reserva` sabe calcular su propio total.
* **Capa Persistencia (`persistence`):** Hablan con MongoDB y guardan los BSON usando `Spring Data Repositories`.

---

### b. ¿Qué diferencia existe entre un validador, una utilidad y un servicio? Ubiquen un ejemplo de cada uno en su diseño e indiquen su responsabilidad.

* **Servicio (`Service`): Es el "Director de Orquesta"**
  - **Definición:** Gestiona todo el flujo de un caso de uso, llama a los demás componentes y maneja la transacción.
  - **Nuestro Ejemplo:** `ReservaService` (`core.services`).
  - **Su trabajo:** Su función es llamar a la cadena de validadores, asociar al cliente con el barbero, calcular el cobro, cambiar el estado a `OCUPADO` y mandar a guardar todo al `ReservaRepository`.

* **Validador (`Validator`): Es el "Guardia de Seguridad"**
  - **Definición:** Componente especializado (SRP) que evalúa una sola regla del negocio. No guarda datos, solo detiene el proceso lanzando una excepción si algo está mal (*Fail-Fast*).
  - **Nuestro Ejemplo:** `FranjaDisponibilidadValidator` (`core.validators`).
  - **Su trabajo:** Revisa la regla `E01`: que no haya 3 reservas y que haya un turno `DISPONIBLE`. Si no, lanza un error HTTP 409.

* **Utilidad (`Util`): Es la "Caja de Herramientas"**
  - **Definición:** Clases técnicas sin estado (stateless) que hacen cálculos universales (matemáticas o fechas) sin saber nada de MongoDB ni de las reglas del negocio.
  - **Nuestro Ejemplo:** `DateUtils` (`core.utils`).
  - **Su trabajo:** Nos ayuda a sacar el día de la semana (`DayOfWeek`) desde una fecha, o a validar si la cita es en una hora en punto (`LocalTime.getMinute() == 0`).

---

### c. ¿Qué diferencia existe entre el Mapper IN y el Mapper OUT? Indiquen dónde se ubican y qué representaciones transforman cada uno.

Para proteger el núcleo de nuestra aplicación, usamos traductores (Mappers) tanto a la entrada como a la salida:

| Criterio | Mapper IN (Nuestra barrera con la Web) | Mapper OUT (Nuestra barrera con MongoDB) |
|---|---|---|
| **Ubicación exacta** | `edu.dosw.parcial.controller.mappers` | `edu.dosw.parcial.persistence.mappers` |
| **Transformación 1 (Lo que Entra)** | Traduce el JSON del cliente (**Request DTOs**) hacia nuestros **Objetos de Dominio** (Core). | Traduce los documentos que vienen de la base de datos (**Documentos MongoDB**) hacia nuestros **Objetos de Dominio** (Core). |
| **Transformación 2 (Lo que Sale)** | Traduce el **Objeto de Dominio** hacia el JSON de respuesta (**Response DTOs**). | Traduce nuestro **Objeto de Dominio** hacia un **Documento MongoDB** para guardarlo. |
| **¿Por qué son vitales? (Propósito)** | Aíslan nuestra lógica pura de los cambios en los nombres del JSON o las versiones de la API REST. | Aíslan nuestra lógica pura de cosas exclusivas de MongoDB (como los `@Id`, el `ObjectId` o las colecciones BSON). |

---

### d. Retomen las tres reglas clasificadas en el paso anterior: ¿en qué componente debe ejecutarse cada una y por qué?

1. **Regla 1 — Datos del Cliente (Nombre vacío [E03], Teléfono 10 dígitos [E07], Correo @gmail [E08]):**
   - **¿Dónde la pusimos?** En el `Controller` (usando los `Request DTO` y anotaciones como `@NotBlank`, `@Pattern`).
   - **¿Por qué?** Porque es una **validación sintáctica/perimetral**. Solo revisa si el texto está bien escrito. La hacemos en la mismísima puerta de entrada para rebotar peticiones malformadas sin gastar memoria y sin tener que molestar a la base de datos.

2. **Regla 2 — Franja Horaria (Límite de 3 reservas [E01]):**
   - **¿Dónde la pusimos?** En el `FranjaDisponibilidadValidator` dentro de nuestro Core, orquestado por el `ReservaService`.
   - **¿Por qué?** Porque esta es una **validación de estado del negocio**. Para saber esto necesitamos obligatoriamente consultar la base de datos (`ReservaRepository.countByFechaAndHora()`). La pusimos en el Core porque si la dejábamos en el Controller estaríamos acoplando la capa web con MongoDB.

3. **Regla 3 — Especialidad del Barbero (Errores E02 / E100):**
   - **¿Dónde la pusimos?** En el `BarberoEspecialidadValidator`, pero usando la inteligencia de nuestra propia entidad de dominio `Barbero` (con un método como `cubreTodasEspecialidades()`).
   - **¿Por qué?** Porque es una **regla semántica y pura del dominio**. Estamos cruzando las habilidades físicas del barbero con lo que exige el catálogo (`CORTE`, `BARBA`, `CAPILAR`). Debe vivir en el dominio profundo porque garantiza que nunca le asignemos a un cliente un barbero que no sepa hacer el trabajo que pidió.