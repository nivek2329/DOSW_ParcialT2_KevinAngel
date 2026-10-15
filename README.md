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
14. [Punto-3](#14-Punto-3)
15. [Punto-4](#15-punto-4)
16. [Punto-5](#16-punto-5)
17. [Punto-6](#17-punto-6)

## Fase de Implementacion

18. [Crear-Turno](#18-Crear-Turno)
19. [Pedir-Turno](#19-Pedir-Turno)
20. [consultar-mis-reservas](#20-consultar-mis-reservas)
21. [consultar-todas-las-reservas](#20-consultar-todas-las-reservas)
22. [iniciar-sesion](#22-iniciar-sesion)

## 1. Datos del estudiante

- **Nombre completo:** Kevin Andrey Angel Acevedo, Santiago Andres Garcia Almario
- **Grupo:** 1
- **Profesor:** Andres Martin Cantor

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



## 12. Punto-1

Respuestas preguntas:   
#### a. ¿Cuál es la diferencia entre una validación de entrada y una validación de negocio? Seleccionen tres reglas del caso, incluyendo al menos una de cada tipo, y justifiquen su clasificación.
La validación de entrada verifica que los datos enviados por el usuario tengan el formato, tipo y estructura correctos, como campos obligatorios, formatos de fecha o números de teléfono válidos. Se implementa generalmente en la capa Controller/DTO.

La validación de negocio comprueba que la solicitud cumpla las reglas y restricciones propias del sistema, considerando las condiciones del negocio y el estado de la base de datos. Por ejemplo, verificar la disponibilidad del barbero, evitar turnos duplicados o comprobar el límite de reservas permitidas. Se implementa principalmente en la capa de servicios o dominio.

#### b. En "Pedir un turno", ¿cómo debe responder la API si fallan varias condiciones? Expliquen qué error se informa y qué cambios deben evitarse.
La API debe identificar y comunicar los errores de forma consistente, deteniendo el proceso cuando una validación falla y evitando crear el turno o modificar la base de datos si la solicitud no cumple las condiciones necesarias. Para ello, puede utilizar una cadena de validaciones y un manejador global de excepciones.

La respuesta debe incluir un código HTTP adecuado al tipo de error (por ejemplo, 400 para datos incorrectos o 409 para conflictos), junto con un cuerpo JSON estructurado que contenga el código de error específico, un mensaje explicativo y, cuando corresponda, una marca de tiempo timestamp.


## 13. Punto-2
![diagrama_componentes_general.drawio.svg](docs/images/diagrama_componentes_general.drawio.svg)
![diagrama-componentes-especifico.drawio.svg](docs/images/diagrama-componentes-especifico.drawio.svg)
![diagrama_clases_modelos_dominio.drawio.svg](docs/images/diagrama_clases_modelos_dominio.drawio.svg)

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

## 14. Punto-3

* **Modelo Conceptual de Dominio:** [`diagrama_bd_modelo_conceptual.drawio`](docs/uml/diagrama_bd_modelo_conceptual.drawio)
* **Metamodelo Conceptual NoSQL:** [`diagrama_bd_metamodelo_conceptual.drawio`](docs/uml/diagrama_bd_metamodelo_conceptual.drawio)


![Modelo Conceptual de Datos](docs/images/diagrama_bd_metamodelo_conceptual.drawio.svg)


![Metamodelo Conceptual NoSQL](docs/images/diagrama_bd_modelo_conceptual.drawio.svg)


## 15. Punto-4

### a. ¿Cuál es la diferencia entre autenticación, autorización e integridad? Expliquen dónde y cómo aplicarán cada concepto en la API, con ejemplos del caso.

#### 1. Autenticación ("¿Quién eres?")
* **El concepto:** Es el paso donde comprobamos que la persona que intenta entrar realmente es quien dice ser.
* **Cómo lo aplicamos:** Lo hacemos en el login. Recibimos el correo y la contraseña, buscamos al usuario en la base de datos y verificamos si la clave coincide. Si todo está bien, le damos su token JWT para que pueda navegar; si no, le negamos la entrada.

#### 2. Autorización ("¿Qué tienes permitido hacer?")
* **El concepto:** Una vez que el usuario ya entró, la autorización se encarga de revisar qué permisos tiene y a qué partes de la aplicación puede acceder.
* **Cómo lo aplicamos:** Lo configuramos en las reglas de seguridad de nuestra API para separar lo que puede hacer un cliente y lo que puede hacer el administrador.
* **Ejemplo:** Si un cliente normal intenta entrar a la ruta para ver absolutamente todas las reservas del local, el sistema revisa su token, se da cuenta de que no es administrador y le bloquea el paso indicando que no tiene permisos.

#### 3. Integridad ("¿La información está intacta y sin alteraciones?")
* **El concepto:** Es asegurarnos de que nadie haya modificado o corrompido los datos, ya sea mientras viajan por internet o cuando están guardados.
* **Cómo lo aplicamos:** 
  * Por un lado, los tokens que le damos a los usuarios están firmados digitalmente. Si alguien intenta hacer trampa y modificar su token para darse permisos de administrador, el sistema detecta que la firma se rompió y lo rechaza. 
  * Por otro lado, al guardar una reserva, "congelamos" el precio y el nombre del servicio en ese instante. Así garantizamos la integridad de los datos históricos: si la barbería sube los precios mañana, las reservas viejas no se verán alteradas.

---

### b. Seleccionen dos riesgos del OWASP Top 10 que se pueden encontrar en su API. Para cada uno, describan cómo se vería ese escenario aplicable a la barbería, su mitigación y cómo verificarían que el control funciona.

#### Riesgo 1: BOLA / IDOR (Fallas al proteger los datos de los usuarios)
* **Escenario en la barbería:** El problema sería que un cliente quiera ver sus turnos y, simplemente cambiando un número de ID en la URL de la petición, termine viendo los turnos, teléfonos y datos personales de otros clientes.
* **Cómo lo solucionamos:** Para evitar esto, directamente no pedimos el ID del cliente en la petición. Lo que hacemos es extraer su identidad desde el token que ya nos envió. Así, la base de datos solo busca y devuelve las reservas que le pertenecen al dueño de ese token, sin importar qué nos envíe desde el navegador.
* **Cómo probamos que funciona:** Haremos pruebas simulando ser el "Cliente A" e intentaremos pedirle al sistema los datos del "Cliente B". La prueba será exitosa si confirmamos que el sistema nos ignora y solo nos devuelve la información del Cliente A.

#### Riesgo 2: Inyección NoSQL (Mandar código malicioso en vez de texto)
* **Escenario en la barbería:** El peligro es que un atacante, en lugar de poner su correo y contraseña en el login, escriba comandos de base de datos para engañar al sistema. Si el sistema se lo cree, podría dejarlo entrar a la cuenta del administrador sin siquiera saber la contraseña.
* **Cómo lo solucionamos:** Somos muy estrictos con lo que recibimos. Validamos que el correo tenga formato de correo y que solo recibamos texto normal. Además, usamos las herramientas de Spring que limpian y bloquean cualquier carácter extraño antes de buscar en la base de datos.
* **Cómo probamos que funciona:** Escribiremos una prueba donde enviaremos símbolos raros y comandos falsos al login. Verificaremos que la API rechace la petición inmediatamente con un error de "Bad Request" antes de que siquiera intente buscar en la base de datos.

---

### c. ¿Cuál de los tipos de autenticación es el más adecuado para este MVP? Justifique su respuesta.

Para este proyecto decidimos que lo mejor es usar **Autenticación con Tokens JWT sin estado (Stateless)**. 

Lo justificamos por estas razones principales:

1. **Ahorramos memoria:** Al no usar sesiones tradicionales, el servidor no tiene que gastar memoria recordando quién está conectado. Toda la información de identidad viaja segura dentro del token en cada petición.
2. **Es mucho más rápido:** Como el token ya trae la información de si el usuario es cliente o administrador, no tenemos que ir a preguntarle a la base de datos en cada clic si la persona tiene permisos o no.
3. **Nos facilita el crecimiento:** Si la barbería tiene mucho éxito y luego necesitamos poner más servidores para aguantar el tráfico, este sistema es ideal. Cualquier servidor nuevo podrá leer el token de los clientes sin necesidad de sincronizarse con los demás.
4. **Es seguro:** La firma digital del token nos da la tranquilidad de que nadie puede falsificar identidades ni alterar sus permisos.

## 16. Punto-5

## Estrategia de Pruebas: Caso de Uso "Pedir un Turno"

### 1. ¿Qué escenarios vamos a probar? (Nuestra matriz de pruebas)

Para asegurarnos de que la creación de reservas funciona perfecto, definimos qué pasa en el caso ideal y qué debe pasar cuando las cosas salen mal:

| Escenario | ¿Qué intentamos hacer? | ¿Qué debe responder el sistema? | 
| ----- | ----- | ----- | 
| **Todo perfecto (Happy Path)** | Pedimos un turno con datos válidos, hay espacio en la hora elegida y el barbero sabe hacer el corte. | **Todo OK (201):** Nos confirma la reserva y marca el turno como ocupado. | 
| **El local está lleno** | Intentamos meter una cuarta reserva en una hora que ya tiene 3 reservas ocupadas. | **Error (409):** Nos rechaza inmediatamente diciendo que no hay cupo. | 
| **Barbero sin la especialidad** | El barbero está libre, pero le pedimos un tinte y él solo hace cortes. | **Error (422):** Nos avisa que el barbero no ofrece ese servicio. | 
| **Datos del cliente incompletos** | Mandamos la petición pero dejamos el nombre vacío o ponemos un correo que no es de Gmail. | **Error (400):** Nos pide que corrijamos nuestros datos antes de seguir. | 
| **Servicio inventado** | Pedimos un servicio con un código que no existe en el catálogo de la barbería. | **Error (404):** Nos avisa que ese servicio no se ofrece. | 
| **Un admin intentando reservar** | Usamos un token de administrador para intentar sacar una cita como cliente. | **Error (403):** El sistema nos bloquea porque los admins no sacan citas propias. | 

### 2. ¿Cómo programamos esto? Usando TDD (Test-Driven Development)

Para estar seguros de que el código hace lo que debe, usamos el ciclo de TDD (Rojo, Verde, Refactorizar) en cada validación:

1. **Paso Rojo (Empezar fallando):** Antes de programar la regla (por ejemplo, "que no pasen de 3 reservas"), escribimos una prueba automática que intente romper esa regla. Al correrla, la prueba falla porque nuestro código aún no sabe defenderse.
2. **Paso Verde (Hacer que pase):** Escribimos el código mínimo y necesario en nuestro servidor para contar las reservas y lanzar un error si ya hay 3. Volvemos a correr la prueba y ahora pasa con éxito.
3. **Paso Refactor (Limpiar la casa):** Una vez que funciona, acomodamos el código para que quede ordenado, fácil de leer y bien estructurado en nuestra Cadena de Responsabilidad, verificando que ninguna prueba anterior se haya roto.

### 3. ¿Cómo demostramos que las reglas se evalúan en el orden correcto?

Tenemos una cadena de validaciones (1. Cupo ➔ 2. Especialidad ➔ 3. Datos del cliente). Para comprobar que el sistema no se salta el orden, usamos la **Estrategia del Doble Fallo**.

**¿En qué consiste?**  
Le mandamos al sistema una petición que tiene *dos errores a propósito*.

* **Ejemplo:** Pedimos un turno en una hora que ya está llena (Fallo 1) y además mandamos un número de teléfono incompleto (Fallo 3).
* **Nuestra prueba:** El sistema **está obligado** a quejarse primero del cupo (Error 409). Si el sistema nos llega a responder "el teléfono está mal", significa que evaluó al cliente antes de mirar si había espacio, lo cual rompería nuestro diseño. Con esto demostramos que el sistema corta el proceso apenas encuentra el primer error lógico en el orden establecido.

### 4. ¿Cómo aseguramos que no se guarde "basura" en la base de datos si algo sale mal?

Si un cliente intenta hacer una reserva y el proceso falla a la mitad, tenemos que garantizar que no se haya guardado una reserva a medias ni se haya bloqueado un turno por accidente. Lo verificamos de dos formas:

**A. Revisando la base de datos (Pruebas de Integración):**
1. Antes de la prueba, contamos cuántas reservas hay en total en MongoDB (ej. hay 10).
2. Mandamos una petición mala (ej. correo inválido) y el sistema nos da error.
3. Volvemos a contar. Si sigue habiendo exactamente 10 reservas y el turno sigue estando "DISPONIBLE", sabemos que el sistema es seguro y no guardó datos fantasma.

**B. Espiando el código con Mockito (Pruebas Unitarias):**  
Usamos herramientas de pruebas para "espiar" a nuestro sistema por dentro. Le decimos a Mockito que vigile la función que guarda datos en la base de datos. Si la petición del usuario tenía un error, hacemos una aserción estricta para confirmar que la función de `guardar()` **literalmente nunca fue llamada** durante ese intento.

---

## 5. Preguntas para la Revisión con el Equipo de Calidad

### a. ¿Cómo nuestras pruebas demuestran que cumplimos las reglas del negocio y qué probamos unitariamente?

**1. Las pruebas son nuestra "documentación viva":**  
En lugar de tener las reglas del negocio escritas en un papel que nadie lee, las convertimos en código ejecutable. Si un compañero cambia algo y por error permite que entren 4 personas en una misma hora, la prueba se pondrá en rojo al instante y bloqueará la subida de ese código defectuoso. Además, probamos "al límite": comprobamos que la tercera reserva pase perfectamente, pero que la cuarta explote como debe ser.

**2. Lo que probamos con Pruebas Unitarias:**  
Aquí probamos la lógica pura y dura súper rápido, aislando la aplicación y fingiendo la base de datos con *mocks*. Específicamente verificamos:
*   **Que cada validador haga su trabajo:** Le mandamos datos malos a cada eslabón (teléfonos con letras, correos de Yahoo, códigos de servicio inventados) para asegurar que cada uno lance su error específico.
*   **Los horarios y reglas de negocio:** Probamos que Miguel solo atienda de lunes a jueves y que el sistema rechace citas fuera de su horario.
*   **Las matemáticas:** Verificamos que la función que calcula el total a pagar sume correctamente los precios de todos los servicios solicitados.
*   **La seguridad interna:** Comprobamos que, ante cualquier mínimo error, la orden de "guardar en base de datos" nunca se ejecute.

### b. ¿Las pruebas por sí solas garantizan que no hay errores?

**No, definitivamente no.** Y lo justificamos por varias razones:

1.  **El principio de Dijkstra:** Como decía Edsger W. Dijkstra, uno de los pioneros del software: *"Las pruebas sirven para demostrar que hay errores, pero jamás pueden demostrar que no los hay"*. Pasar todas las pruebas solo significa que el sistema funciona para esos casos específicos que imaginamos.
2.  **Combinaciones infinitas:** Es humanamente imposible probar cada escenario. No podemos simular qué pasa si dos clientes dan clic en "Reservar" exactamente en el mismo milisegundo, o qué ocurre si el internet de Azure parpadea a mitad de la transacción.
3.  **El factor humano:** Si nosotros entendimos mal un requerimiento del cliente, vamos a escribir una prueba automatizada que valide ese error. La prueba pasará en verde (falso positivo), pero el error seguirá estando ahí.
4.  **Necesitamos un equipo de calidad integral:** Las pruebas son vitales, pero para asegurar un buen producto también usamos revisiones de código entre nosotros (Code Reviews), herramientas que analizan la salud del código (como SonarQube) y mucha observación y monitoreo una vez que la API está funcionando en la nube.

## 17. Punto-6

## 1. Especificación del Diagrama de Despliegue

![diagrama_despliegue.drawio.svg](docs/images/diagrama_despliegue.drawio.svg)

### Elementos Involucrados en el Despliegue:
1. **Nodo Dispositivo Cliente (`«device» Dispositivo Cliente`):**
   * Representa los equipos de los usuarios finales (PC, Laptop, Smartphone).
   * **Artefactos:** `Web Browser / Mobile App` (consumidor SPA) y `Swagger UI / Postman` (pruebas de API).
2. **Nodo Servidor en la Nube (`«device» Microsoft Azure Cloud Server`):**
   * Región: `Central US`.
   * **Entorno de Ejecución:** `Azure App Service (Linux Web App)` gestionando un contenedor Docker.
   * **Artefacto Docker:** Imagen `dosw-parcial-t2:latest` basada en `eclipse-temurin:21-jre-alpine`.
   * **Artefacto Backend:** Archivo `app.jar` (Spring Boot 3.5.16 sobre Java 21) ejecutando el servidor embebido Tomcat en el puerto `8080`.
   * **Variables de Entorno inyectadas:** `MONGODB_URI`, `JWT_SECRET`, `PORT=8080`.
3. **Nodo de Persistencia Administrada (`«device» MongoDB Cloud Cluster`):**
   * Base de datos como servicio en la nube (MongoDB Atlas DBaaS).
   * **Base de datos:** `dosw_parcial` con colecciones `usuarios`, `barberos`, `servicios`, `turnos` y `reservas`.
4. **Mecanismos y Rutas de Comunicación:**
   * **Cliente ➔ Azure App Service:** Protocolo seguro `HTTPS / TLS 1.3` (Puerto público 443 redirigido al puerto 8080 del contenedor). Cargas útiles en JSON y autenticación vía cabecera `Authorization: Bearer <JWT>`.
   * **Azure App Service ➔ MongoDB Atlas:** Protocolo de conexión binaria `MongoDB Wire Protocol` sobre `TCP / TLS` en el puerto `27017`, con autenticación criptográfica `SCRAM-SHA-256`.

---

## 2. Respuestas a las Preguntas para la Revisión con el Equipo de DevOps

---

### a. ¿Cuáles son las etapas principales de un pipeline CI/CD y qué propósito tiene cada una?

Basado en la configuración real de GitHub Actions de nuestro repositorio (`.github/workflows/ci-cd.yml`), el pipeline implementa 3 etapas principales secuenciales:

1. **Etapa 1: Build y Pruebas Continuas (Integración Continua - CI):**
   * **Propósito:** Validar la integridad y calidad del código fuente ante cada cambio (*push/pull request*).
   * **Acciones en el pipeline:** Descarga el código (`actions/checkout@v4`), configura el runtime de Java 21 Temurin, resuelve dependencias de Maven y ejecuta `mvn verify`. Esto compila el código, corre todas las pruebas unitarias y genera los reportes de cobertura (JaCoCo). Si alguna prueba o regla de compilación falla, el pipeline se aborta inmediatamente.
2. **Etapa 2: Empaquetamiento y Publicación de Contenedor (Docker Package & Registry):**
   * **Propósito:** Generar un artefacto inmutable, estándar y portátil que contenga exactamente la versión aprobada en la etapa de pruebas.
   * **Acciones en el pipeline:** Se autentica en Docker Hub (`docker/login-action@v3`) y ejecuta la construcción multi-stage del `Dockerfile`, generando la imagen optimizada `dosw-parcial-t2:latest` y publicándola en el registro de contenedores de Docker Hub.
3. **Etapa 3: Despliegue Continuo (Continuous Deployment - CD):**
   * **Propósito:** Poner la nueva versión en producción en el entorno cloud sin intervención manual ni tiempos de caída prolongados (*Zero Downtime Deployment*).
   * **Acciones en el pipeline:** Se autentica en Azure mediante Service Principal (`azure/login@v2`) y notifica a Azure App Service (`azure/webapps-deploy@v3`) para que descargue la nueva imagen de Docker Hub y reinicie el contenedor en el App Service de producción.

---

### b. ¿Qué sucede si una prueba falla? ¿Debe permitirse el despliegue? Justifique.

* **Qué sucede técnicamente:**  
  El runner de GitHub Actions detecta el código de salida distinto de cero (`exit code != 0`) en el paso `mvn verify`. Inmediatamente el job `test` se marca en estado **Fallo (Rojo)**. Debido a que los jobs posteriores tienen la directiva `needs: test` y `needs: docker`, el pipeline detiene su ejecución en seco (*Fail-Fast Pipeline*). No se construye la imagen Docker y no se ejecuta el despliegue a Azure.

* **¿Debe permitirse el despliegue?**  
  **Rotundamente NO.**

* **Justificación técnica:**
  1. **Principio de Calidad en la Fuente (*Quality Gates*):** Permitir el despliegue de una versión que falla pruebas significa introducir regresiones conocidas y defectos funcionales directamente a los usuarios en producción (por ejemplo, permitir que un cliente reserve en una franja ya llena `E01`, o que un barbero atienda servicios para los cuales no tiene especialidad `E02`).
  2. **Protección de la Integridad de la Base de Datos:** Si las pruebas de validación fallan, el código desplegado podría persistir datos inconsistentes o corruptos en MongoDB Atlas, lo cual requeriría costosas tareas manuales de limpieza y causaría pérdida de confianza del cliente.
  3. **Costo de Reparación:** Corregir un defecto detectado en la fase de CI toma minutos en la máquina del desarrollador; corregirlo después de desplegado en producción implica incidentes, caída del servicio, generación de parches de emergencia (hotfixes) e impacto comercial en la barbería.

---

### c. ¿Para qué sirven los logs y qué registrarían para investigar una reserva fallida o un error de autenticación?

* **Para qué sirven los logs:**
  Los logs son la bitácora cronológica estructurada de eventos del sistema. Permiten auditar la actividad, monitorear el rendimiento en tiempo real, entender el flujo de ejecución y realizar análisis forense (*root-cause analysis*) cuando se presentan anomalías o caídas sin necesidad de depurar interactivamente en producción.

* **Qué registraríamos para investigar una reserva fallida:**
  1. `Timestamp` exacto en formato UTC/ISO-8601.
  2. `Trace ID` / `Correlation ID` de la petición HTTP para rastrear todo el ciclo de vida de la transacción.
  3. Identificador del cliente autenticado extraído del token JWT (correo del cliente).
  4. Datos de la solicitud: fecha solicitada, hora de inicio (`10:00`), código de servicios (`[SER-001, SER-004]`) y barbero solicitado.
  5. Causa raíz del rechazo: código de negocio exacto (`E01 - Franja llena`, `E02 - Especialidad incompatible`, `E100 - Barbero no disponible`) y estado de la base de datos en ese instante (ej: *"Franja 2026-10-20 10:00 cuenta con 3 reservas activas. Rechazando cuarta reserva"*).
  6. Código HTTP retornado al cliente (`409 Conflict`, `422 Unprocessable Entity`).

* **Qué registraríamos para investigar un error de autenticación:**
  1. `Timestamp` y dirección IP de origen de la solicitud.
  2. Correo electrónico utilizado en el intento de inicio de sesión (`admin@barber.com` o `cliente@gmail.com`).
  3. Motivo técnico del fallo a nivel interno (ej: *"Usuario no encontrado en MongoDB"* o *"Hash de contraseña no coincide"*).
  4. Tipo de error de seguridad: si fue token vencido, firma HMAC inválida o claim de rol ausente.
  5. Código HTTP retornado (`401 Unauthorized`).

---

### d. Presente ejemplos de información útil para rastrear la operación y de datos que NO deben registrarse.

#### 1. Información Útil y Recomendada para Registrar:
* **Identificadores de Rastreo:** `CorrelationId` / `RequestId` autogenerado por cada petición HTTP para correlacionar logs entre múltiples componentes.
* **Metadatos de la Petición:** Método HTTP (`POST`, `GET`), ruta del endpoint (`/api/v1/reservas`), código de respuesta HTTP (`201`, `400`, `409`) y tiempo de respuesta en milisegundos (`latency: 45ms`).
* **Identidad Operativa:** Identificador o correo del usuario autenticado (`userId: "670c5e1f...", email: "carlos@gmail.com"`).
* **Eventos de Ciclo de Vida del Negocio:** *"Turno ID 670c5e... reservado con éxito por cliente carlos@gmail.com"*, *"Servicio recalculado: Total $45.000"*.

#### 2. Datos que NUNCA deben registrarse en los Logs (Violaciones de Seguridad y Privacidad):
* **Contraseñas en Texto Claro o Hashes:** Nunca loguear el campo `password` enviado en el login (riesgo crítico de fuga de credenciales en repositorios de logs).
* **Secretos Criptográficos y Claves del Sistema:** Clave secreta del JWT (`JWT_SECRET`), connection strings con contraseñas de MongoDB (`mongodb+srv://user:password@cluster...`) o credenciales de Azure.
* **Tokens de Acceso Completos:** El token JWT completo no debe imprimirse en logs (si un operador tiene acceso a los logs, podría suplantar la sesión del cliente).
* **Información Personal Sensible (PII / Datos Financieros):** Números de tarjetas de crédito, respuestas a preguntas de seguridad o datos médicos. En el caso del teléfono del cliente, si se loguea debe aplicarse enmascaramiento parcial (ej: `310****567`).

---

### e. ¿Qué diferencia debe existir entre el detalle del log interno y el mensaje que recibe el cliente?

| Criterio | Log Interno del Sistema (Backend / CloudWatch / Azure Log Stream) | Mensaje de Respuesta al Cliente (Cuerpo HTTP JSON) |
|---|---|---|
| **Audiencia** | Desarrolladores, equipo de DevOps, SRE y administradores de seguridad. | Usuario final de la barbería (cliente o barbero) y frontend/app móvil. |
| **Nivel de Detalle** | **Extremadamente detallado y técnico:** Incluye trazas de error (*Stack Traces*), nombres de clases Java (`ReservaServiceImpl.java:85`), queries ejecutadas en MongoDB, Correlation IDs, memoria y tiempos de CPU. | **Amigable, conciso y funcional:** Explica claramente el motivo del rechazo en lenguaje comprensible sin detalles técnicos de infraestructura (ej: *"No hay disponibilidad para esta hora. Por favor elija otro horario"*). |
| **Seguridad y Prevención de Fugas (*Information Disclosure*)** | Acceso restringido con autenticación y auditoría; contiene la causa raíz para depurar. | **Ofuscado y blindado:** Nunca debe revelar nombres de tablas, versiones de librerías, excepciones internas de Java (`NullPointerException`, `MongoSocketException`) ni detalles de la arquitectura interna para evitar ataques de reconocimiento (OWASP A05). |
| **Ejemplo Comparativo ante fallo de DB:** | `ERROR [ReservaService] [req-89a1] MongoTimeoutException: Timed out after 30000 ms while waiting to connect to [mongo-atlas.net:27017]. StackTrace: ...` | `HTTP 500 Internal Server Error` ➔ `{"codigo": "ERR-500", "mensaje": "Ocurrió un error inesperado al procesar su solicitud. Intente nuevamente más tarde.", "timestamp": "2026-10-13T22:30:00"}` |

## 18. Crear-Turno

### Descripción del alcance de este paso: Implementación de Reservas

En esta segunda parte del proyecto nos metimos de lleno en la funcionalidad principal de Bob's Barber: lograr que los clientes puedan agendar sus citas sin que el sistema colapse. 

Para arrancar, tuvimos que hacerle un par de ajustes a lo que ya traíamos del Paso 1. Le enseñamos a nuestro `TurnoRepository` a buscar huecos libres pidiéndole fechas y horas exactas (usando métodos como `findByFechaAndHoraInicioAndEstado`). Además, nos adelantamos a un problema clásico: ¿qué pasa si dos personas hacen clic en "Reservar" exactamente al mismo milisegundo por el último cupo? Para que la aplicación no explote, configuramos el `GlobalExceptionHandler` para que atrape ese choque en la base de datos (`DuplicateKeyException`) y lo convierta amablemente en un error HTTP 409 (código `E01`), avisando que hubo un conflicto.

Con esa base lista, creamos las nuevas piezas del rompecabezas: las entidades `Reserva`, `Servicio`, `EstadoReserva` y un documento clave llamado `CuotaFranjaDocument`, que es el que nos ayuda a llevar la cuenta de cuánta gente hay en el local.

**El motor de validaciones (Nuestra Cadena de Responsabilidad)**
Aquí está el corazón de la reserva. Armamos un filtro de 4 pasos súper estricto que funciona bajo el principio *fail-fast* (si algo falla, cortamos el proceso ahí mismo para no gastar memoria de más). Lo explicamos así:

1. Primero entra el `FranjaDisponibilidadValidator`, que le echa un ojo rápido al horario para ver si tiene sentido y si hay disponibilidad preliminar.
2. Luego pasa al `BarberoDisponibleValidator`. Este es súper interesante porque si el cliente no eligió a nadie, el sistema hace una asignación inteligente: busca qué barbero está libre y, además, sabe hacer el corte que pidió el cliente. (Si no existe nadie con esa habilidad, lanzamos un error 404, y si están todos ocupados, un 422).
3. El tercer filtro es el `ServicioPermitidoValidator`, que simplemente va al catálogo a confirmar que el servicio que pidieron de verdad exista (si inventan un código, lanzamos un 404).
4. Y cerramos con el `ConcurrenciaFranjaValidator`, que es el guardia de seguridad que garantiza que jamás pasemos de nuestro límite de 3 clientes por franja.

**Asegurando los datos (Transacciones y Atomicidad)**
A nivel de base de datos no podíamos correr riesgos de guardar reservas a medias. Configuramos el `MongoTransactionManager` en nuestro `MongoConfig` para habilitar transacciones reales. Para actualizar los cupos sin pisarnos, usamos una operación súper segura (atómica) con `mongoTemplate.findAndModify`. Si por alguna razón explota algo al momento de guardar la reserva final, el sistema hace una reversión automática (rollback) y deja los cupos tal como estaban.

Finalmente, para que los clientes puedan usar todo esto, creamos el `ReservaController` abriendo la ruta `POST /api/v1/reservas`. Y claro, nos aseguramos de que toda esta lógica funcione a la perfección con nuestras pruebas unitarias en `ReservaServiceTest`.

## 19. Pedir-Turno

### Descripción del alcance de este paso
* **Modificaciones de soporte al Paso 1:**
  * Se ampliaron los métodos en `TurnoRepository` (`findByFechaAndHoraInicioAndEstado`, `findByFechaAndHoraInicio`) para consultar turnos disponibles por fecha y hora.
  * Se configuró el `GlobalExceptionHandler` para capturar la excepción `DuplicateKeyException`, mapeando conflictos concurrentes a una respuesta HTTP 409 con el código de error `E01`.
* **Nuevos modelos y entidades:** 
  * Se integraron `Reserva`, `EstadoReserva`, `Servicio` y `CuotaFranjaDocument`.
* **Cadena de Responsabilidad (Validaciones en orden estricto con detención *fail-fast*):**
  1. `FranjaDisponibilidadValidator`: Valida el horario y la disponibilidad preliminar.
  2. `BarberoDisponibleValidator`: Valida el barbero o realiza la asignación inteligente según la especialidad requerida (HTTP 404 si no existe, 422 si el horario no está disponible).
  3. `ServicioPermitidoValidator`: Comprueba la existencia del servicio en el catálogo activo (HTTP 404).
  4. `ConcurrenciaFranjaValidator`: Asegura el cumplimiento del límite máximo de 3 clientes por franja horaria.
* **Persistencia transaccional y atómica:** 
  * Se configuró `MongoConfig` con `MongoTransactionManager`.
  * Se implementó control atómico mediante `mongoTemplate.findAndModify` sobre la cuota, asegurando la reversión (*rollback*) automática si falla el guardado.
* **Capa REST:** 
  * Se desarrolló el `ReservaController` habilitando el endpoint `POST /api/v1/reservas`.
* **Pruebas unitarias:** 
  * Se incorporaron las pruebas correspondientes en `ReservaServiceTest`.

## 20. consultar-mis-reservas

### Descripción del alcance de este paso: Consulta de Reservas del Cliente

En este breve pero importante paso, nos enfocamos en darle a los clientes la capacidad de ver su propio historial de turnos de una forma muy personalizada y cómoda.

Para lograrlo, empezamos trabajando desde abajo hacia arriba. En la capa de datos, fuimos a nuestro `ReservaRepository` y le agregamos una consulta a la medida: el método `findByClienteEmailOrderByFechaCreacionDesc`. Gracias a la magia de Spring Data, con este nombre tan descriptivo le estamos diciendo a MongoDB que filtre exactamente las reservas que le pertenecen a ese correo y, además, que nos entregue la lista ya ordenada cronológicamente de la más reciente a la más antigua.

Una vez tuvimos los datos, subimos a la capa de negocio. En nuestro `ReservaService` (junto con su clase `ReservaServiceImpl`), creamos el método `consultarMisReservas(clienteEmail)`. Este método actúa como puente: recibe el correo del cliente, va a buscar las reservas al repositorio y se encarga de mapearlas (transformarlas) en un formato limpio para que el usuario solo vea la información que le interesa.

Finalmente, para que los clientes puedan acceder a toda esta lógica desde su pantalla, conectamos los cables en la capa web. Fuimos al `ReservaController` y abrimos una nueva ruta: el endpoint `GET /api/v1/reservas/mis-reservas`. A través de esta ruta, el cliente hace la petición y nuestro sistema le devuelve todo su historial de citas al instante.

## 21. consultar-todas-las-reservas
### Descripción del alcance de este paso
* **Repositorio (`ReservaRepository`):** 
  * Se agregó el método de consulta ordenada cronológicamente `findAllByOrderByFechaCreacionDesc`.
* **Lógica de Negocio (`ReservaService` y `ReservaServiceImpl`):** 
  * Se implementó el método `consultarTodasLasReservas()`, encargado de retornar el listado completo de las reservas registradas en la barbería.
* **Capa REST (`ReservaController`):** 
  * Se incorporó el endpoint `GET /api/v1/reservas` para exponer la información administrativa.

## 22. iniciar-sesion

### Descripción del alcance de este paso: Seguridad, Autenticación y Autorización

Para cerrar, en este último paso nos dedicamos a blindar la aplicación. Implementamos toda la infraestructura de seguridad para asegurarnos de que solo entren las personas correctas y vean únicamente lo que les corresponde.

Para empezar, armamos la base de cómo el sistema reconoce a la gente. Creamos la entidad `Usuario`, definimos claramente quién es quién usando `Rol` (`ADMIN` y `CLIENTE`), y armamos su almacenamiento en MongoDB con `UsuarioDocument` y `UsuarioRepository`. Para manejar los datos que nos envía el usuario al iniciar sesión y lo que le respondemos, creamos los objetos `LoginRequestDTO` y `AuthResponseDTO`. Toda la lógica de verificar quién entra la metimos en nuestro `AuthService` (junto a su implementación `AuthServiceImpl`).

**La magia de Spring Security y JWT**
Para manejar la seguridad pesada, integramos Spring Security 6 configurando todo en nuestro `SecurityConfig`. Por supuesto, guardar contraseñas en texto plano era impensable, así que aplicamos encriptación robusta con BCrypt desde el `PasswordEncoderConfig`. 

Como nuestra arquitectura es sin estado (stateless), la llave de entrada es un token. Para eso, fabricamos nuestro propio `JwtTokenProvider` que genera y lee los tokens, un `JwtAuthenticationFilter` que actúa como guardia en la puerta revisando cada petición que llega, y conectamos el reconocimiento de usuarios con el `CustomUserDetailsService`.

Para que los usuarios puedan reclamar su token, habilitamos el `AuthController` exponiendo la ruta `POST /api/v1/auth/login`. Y como queríamos poder probar todo esto cómodamente desde Swagger, actualizamos el `OpenApiConfig` para que la documentación acepte nuestro esquema *Bearer Auth*.

**Protegiendo lo que ya habíamos construido (RBAC)**
El paso final fue regresar a los controladores de los pasos anteriores para ponerles candado según el rol del usuario:
* Al `TurnoController` lo restringimos totalmente para que solo el `ADMIN` pueda publicar horarios (etiquetándolo en la documentación con `@SecurityRequirement(name = "bearerAuth")`).
* En el `ReservaController` fuimos más cuidadosos con los permisos: configuramos que el `CLIENTE` solo tenga poder para crear citas y ver su propio historial (`POST /api/v1/reservas` y `GET /api/v1/reservas/mis-reservas`). Sin embargo, el `ADMIN` es el único que tiene permiso para consultar el listado global del negocio (`GET /api/v1/reservas`).

Finalmente, si algún usuario intenta pasarse de listo y acceder a una función para la cual no tiene permisos, configuramos nuestro `GlobalExceptionHandler`. Ahora, el sistema atrapa automáticamente esa `AccessDeniedException` y lo rebota de forma controlada con un error HTTP 403 Forbidden.