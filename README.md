# Taller del Patrón Observer con Spring Boot y Docker

## Objetivo General

Aplicar el patrón **Observer** en un servicio REST real: cuando cambia la configuración del sistema, todos los componentes interesados (el log, la auditoría en base de datos y el formateador de precios) se enteran automáticamente, sin que el servicio de configuración los conozca y sin que ellos tengan que consultarla en cada momento. Todo el sistema se levanta con un solo comando de Docker Compose.

## Índice

- [Conceptos clave](#conceptos-clave)
- [CONOCE EL TALLER](#conoce-el-taller)
  - [Requisitos](#requisitos)
  - [Estructura del proyecto](#estructura-del-proyecto)
- [PASO A PASO](#paso-a-paso)
  - [Paso 1: Levantar el ambiente](#paso-1-levantar-el-ambiente)
  - [Paso 2: Estudiar el observador de ejemplo](#paso-2-estudiar-el-observador-de-ejemplo)
  - [Paso 3: Notificar a todos los observadores](#paso-3-notificar-a-todos-los-observadores)
  - [Paso 4: Observador de auditoría](#paso-4-observador-de-auditoría)
  - [Paso 5: Formateador de precios como observador](#paso-5-formateador-de-precios-como-observador)
  - [Paso 6: Pruebas](#paso-6-pruebas)
- [Retos opcionales](#retos-opcionales)
- [Buenas prácticas](#buenas-prácticas)
- [Para entregar](#para-entregar-con-este-taller)
- [Resumen del taller](#hagamos-un-resumen)
- [Recursos recomendados](#recursos-recomendados)
- [Créditos y uso académico](#créditos-y-uso-académico)
- [Licencia](#licencia-de-uso)

---

## Conceptos clave

| Concepto | Qué es |
|---|---|
| **Observer** | Patrón de comportamiento que define una dependencia de uno a muchos: cuando el sujeto cambia de estado, todos sus observadores son notificados y actualizados automáticamente. |
| **Sujeto** | El objeto observado. En este taller es `ServicioConfiguracion`, que guarda la configuración del POS. |
| **Observador** | Cualquier objeto que implementa `ObservadorConfiguracion` y reacciona a los cambios. |
| **Acoplamiento débil** | El sujeto solo conoce la interfaz de los observadores, nunca sus clases concretas. |
| **Inyección de dependencias** | Spring crea los observadores y se los entrega al sujeto, sin `new` en el código del sujeto. |
| **Docker Compose** | Herramienta que levanta varios contenedores (la aplicación y MySQL) con un solo archivo `compose.yaml`. |
| **Principio abierto/cerrado** | Agregar un observador nuevo no obliga a modificar el sujeto ni los observadores existentes. |

---

## CONOCE EL TALLER

Un punto de venta (POS) tiene una configuración que se puede cambiar mientras el sistema está en marcha: el formato de las fechas y el formato de la moneda. Varios componentes dependen de esa configuración:

- El **log** de la aplicación debe registrar cada cambio.
- La **auditoría** debe guardar en MySQL quién cambió qué y cuándo (valor anterior, valor nuevo y fecha).
- El **formateador de precios** debe mostrar los precios y fechas con el formato vigente.

En lugar de que cada componente pregunte a cada rato si la configuración cambió, el servicio de configuración les **avisa** cuando cambia.

El servicio expone estos endpoints:

| Método y ruta | Qué hace | Respuestas |
|---|---|---|
| `GET /api/configuracion` | Consulta la configuración vigente | `200` |
| `PUT /api/configuracion/{clave}` con `{ "valor": "..." }` | Cambia `formato-fecha` o `formato-moneda` | `200` con el cambio, `404` si la clave no existe, `400` si el valor falta o no es un formato válido |
| `GET /api/precios?valor=1234.5` | Muestra un valor con el formato de moneda y la fecha de hoy con el formato de fecha | `200` con `{ "valor": "1.234,50", "fecha": "2026/10/30" }` |
| `GET /api/auditoria` | Últimos 50 cambios guardados por la auditoría | `200` |

Los formatos siguen la sintaxis de [`DateTimeFormatter`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/time/format/DateTimeFormatter.html) para las fechas y de [`DecimalFormat`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/text/DecimalFormat.html) para la moneda, con separadores de Colombia (punto para miles y coma para decimales).

### Requisitos

- JDK 21 y Maven 3.9 o superior (o el wrapper `./mvnw` incluido).
- Docker Desktop o Docker Engine con Docker Compose v2.
- Git y un IDE (VS Code, IntelliJ IDEA o Eclipse).
- Haber hecho el [Taller de Docker](https://github.com/CesarAVegaF312/DYAS-Docker), partes 1 y 2.

Verifique el ambiente antes de empezar:

```bash
java -version          # debe mostrar 21
./mvnw -version
docker compose version
```

### Estructura del proyecto

```text
.
├── compose.yaml                  # aplicación + MySQL
├── .env.example                  # variables de entorno; cópielo como .env
├── Dockerfile
├── mvnw, mvnw.cmd                # Maven Wrapper: no necesita instalar Maven
├── db/
│   └── init.sql                  # tabla auditoria_configuracion
├── src/main/java/edu/unisabana/dyas/pos/
│   ├── PosApplication.java
│   ├── api/
│   │   ├── ConfiguracionController.java      # ya implementado, NO se modifica
│   │   ├── AuditoriaController.java          # consulta la auditoría, NO se modifica
│   │   └── SolicitudCambio.java              # cuerpo del PUT
│   ├── configuracion/
│   │   ├── ObservadorConfiguracion.java      # interfaz del observador, NO se modifica
│   │   ├── CambioConfiguracion.java          # la notificación que reciben los observadores
│   │   ├── ServicioConfiguracion.java        # sujeto: debe completarlo (Paso 3)
│   │   └── observadores/
│   │       ├── ObservadorLog.java            # resuelto como ejemplo guía
│   │       └── ObservadorAuditoria.java      # debe implementarlo (Paso 4)
│   └── precios/
│       ├── PreciosController.java            # ya implementado, NO se modifica
│       └── FormateadorPrecios.java           # debe convertirlo en observador (Paso 5)
├── src/main/resources/
│   └── application.yml           # valores iniciales de la configuración
└── src/test/java/...             # pruebas del controlador, del sujeto y del observador de ejemplo
```

---

## PASO A PASO

### Paso 1: Levantar el ambiente

1. Haga un *fork* del repositorio y clónelo:

   ```bash
   git clone https://github.com/<tu-usuario>/DYAS-BehavioralPatterns-observer.git
   cd DYAS-BehavioralPatterns-observer
   ```

2. Cree su archivo de variables a partir de la plantilla y revise su contenido:

   ```bash
   cp .env.example .env
   ```

3. Levante la aplicación y la base de datos:

   ```bash
   docker compose up --build -d
   docker compose ps          # ambos servicios deben aparecer como "healthy"
   ```

4. Consulte la configuración y un precio formateado:

   ```bash
   curl http://localhost:8080/api/configuracion
   curl "http://localhost:8080/api/precios?valor=1234.5"
   ```

5. Cambie el formato de la moneda y vuelva a consultar el precio y la auditoría:

   ```bash
   curl -i -X PUT http://localhost:8080/api/configuracion/formato-moneda \
        -H "Content-Type: application/json" \
        -d '{"valor":"$ #,##0"}'
   curl "http://localhost:8080/api/precios?valor=1234.5"
   curl http://localhost:8080/api/auditoria
   docker compose logs app | grep "Configuración cambiada"
   ```

   Observe que el cambio aparece en el log, pero el precio **sigue con el formato anterior** y la auditoría **está vacía**. Eso es lo que va a corregir.

> Si el puerto 8080 o 3306 está ocupado, cambie `APP_PORT` o `DB_PORT` en el archivo `.env`.
>
> MySQL ejecuta `db/init.sql` solo la primera vez, cuando su volumen de datos está vacío. Si cambia ese archivo o la clave de MySQL en el `.env`, borre el volumen con `docker compose down -v` y vuelva a levantar el sistema.

### Paso 2: Estudiar el observador de ejemplo

Abra `ObservadorConfiguracion.java`, `CambioConfiguracion.java`, `ObservadorLog.java` y `ServicioConfiguracion.java`. Responda en su informe:

1. ¿Qué información recibe un observador cuando la configuración cambia? ¿Por qué incluir el valor anterior y no solo el nuevo?
2. ¿Qué clase cumple el rol de **sujeto** y cuáles el de **observadores**? ¿Qué rol cumple `ConfiguracionController`?
3. ¿Por qué el código actual de `ServicioConfiguracion` no cumple el patrón Observer, aunque `ObservadorLog` sí implementa la interfaz?
4. ¿Qué pasa hoy si se cambia un formato al mismo valor que ya tenía? ¿Por qué tiene sentido?

Dibuje el diagrama de clases del patrón con el sujeto y los tres observadores (dos de ellos aún sin implementar).

### Paso 3: Notificar a todos los observadores

Complete `ServicioConfiguracion` para que notifique a **todos** los observadores registrados en Spring. Condiciones:

- El sujeto no puede contener `new` sobre los observadores ni importar ninguna clase del paquete `observadores` ni de `precios`.
- Agregar un observador nuevo solo debe requerir crear una clase que implemente `ObservadorConfiguracion` y registrarla como *bean*.
- Si un observador lanza una excepción, los demás deben recibir la notificación igual, y el error debe quedar en el log.
- Un valor inválido o igual al actual no debe notificar a nadie (esto ya lo hace el código; no lo rompa).

Pista: Spring puede inyectar en una `List` todos los *beans* que implementan una interfaz.

Al cambiar el constructor, ajuste también el método `crearServicio()` de `ServicioConfiguracionTest`.

### Paso 4: Observador de auditoría

Implemente `ObservadorAuditoria` para que guarde cada cambio en la tabla `auditoria_configuracion` de MySQL (ver `db/init.sql`):

- Use `JdbcTemplate` con una **sentencia parametrizada**. Nunca concatene los valores en el SQL: el valor de un formato lo escribe un usuario.
- Guarde la clave, el valor anterior, el valor nuevo y la fecha del cambio.
- Los datos de conexión llegan por variables de entorno desde el `.env`. No escriba credenciales en el código ni en `application.yml`.

Reconstruya y pruebe:

```bash
docker compose up --build -d
curl -X PUT http://localhost:8080/api/configuracion/formato-fecha \
     -H "Content-Type: application/json" -d '{"valor":"dd-MM-yyyy"}'
curl http://localhost:8080/api/auditoria
```

Luego detenga MySQL con `docker compose stop mysql`, haga otro cambio y verifique que la respuesta sigue siendo `200`, que el log muestra el error de la auditoría y que los demás observadores sí se enteraron. Vuelva a levantar MySQL con `docker compose start mysql`.

### Paso 5: Formateador de precios como observador

Hoy `FormateadorPrecios` usa siempre los formatos con los que arrancó la aplicación. Conviértalo en observador:

- Cuando cambie `formato-moneda` o `formato-fecha`, debe actualizar su formato. Los cambios de otras claves no lo afectan.
- No puede consultar `ServicioConfiguracion` en cada petición: se entera de los cambios solo por la notificación.
- Las peticiones a `/api/precios` llegan en hilos distintos al del cambio. Asegúrese de que el nuevo formato sea visible para todos los hilos (por ejemplo, con campos `volatile`).

Compruebe que después de un `PUT`, `GET /api/precios` ya usa el formato nuevo sin reiniciar la aplicación.

### Paso 6: Pruebas

1. En `ServicioConfiguracionTest`, use observadores de prueba (por ejemplo, *lambdas* que guardan lo que reciben en una lista) para verificar que:
   - todos los observadores reciben el cambio, con valor anterior y nuevo;
   - un observador que lanza una excepción no impide que los demás reciban la notificación;
   - un valor igual al actual o inválido no notifica a nadie.
2. Pruebe que `FormateadorPrecios` cambia su formato al recibir una notificación y que ignora otras claves.
3. Pruebe `ObservadorAuditoria` contra MySQL. Puede usar [Testcontainers](https://java.testcontainers.org/) con el mismo `db/init.sql`.
4. Ejecute todo:

   ```bash
   ./mvnw test
   ```

---

## Retos opcionales

1. **Eventos de Spring.** Reemplace la notificación manual por `ApplicationEventPublisher` en el sujeto y `@EventListener` en los observadores. Compare en el informe: ¿qué gana y qué pierde frente a su implementación? ¿Sigue siendo el patrón Observer?
2. **Observador asíncrono.** Haga que la auditoría se ejecute en otro hilo con `@Async`. ¿Qué pasa con el orden de los registros y con la respuesta del `PUT` si MySQL está lento? Este reto prepara el módulo de comunicación asíncrona.
3. **Desactivar un observador por configuración.** Permita apagar la auditoría con una variable del `.env`, sin modificar el sujeto. Es el equivalente a quitar un observador de la lista.

---

## Buenas prácticas

- La notificación lleva todo lo que el observador necesita. Si un observador tiene que volver a preguntarle al sujeto, la notificación está incompleta.
- El sujeto no decide qué hace cada observador ni en qué orden; si el orden importa, el diseño tiene un problema.
- Un observador lento o que falla no debe tumbar la operación del usuario. Registre el error y siga.
- El archivo `.env` está en `.gitignore`. Solo se versiona `.env.example`.

---

## PARA ENTREGAR CON ESTE TALLER

Entregue el enlace a su *fork* con el código y un informe breve (`INFORME.md` en la raíz) con las respuestas del Paso 2, el diagrama de clases y la evidencia de cada paso (comandos y salidas).

### Criterios de evaluación

| Criterio | Qué se revisa | Peso |
|---|---|---|
| **Ambiente** | `docker compose up --build` levanta la aplicación y MySQL desde un clon limpio, sin pasos manuales adicionales al `.env`. | 15 % |
| **Diseño** | `ConfiguracionController`, `PreciosController`, `AuditoriaController` y `ObservadorConfiguracion` no se modificaron. `ServicioConfiguracion` no conoce las clases concretas de los observadores. Agregar un observador solo requiere una clase nueva. `FormateadorPrecios` no consulta el sujeto en cada petición. | 30 % |
| **Implementación** | Sentencia parametrizada en la auditoría, credenciales solo en variables de entorno, un observador que falla no impide notificar a los demás, formatos visibles entre hilos. | 20 % |
| **Funcionalidad** | Después de un `PUT`, `/api/precios` usa el formato nuevo y `/api/auditoria` muestra el cambio; un valor igual al actual no genera auditoría; con MySQL detenido el `PUT` sigue respondiendo `200`. | 20 % |
| **Pruebas e informe** | Las pruebas del Paso 6 pasan con `./mvnw test`; el informe responde el Paso 2 e incluye el diagrama. | 15 % |

---

## Hagamos un resumen

- El sujeto avisa; los observadores deciden qué hacer con el aviso.
- El sujeto depende de una interfaz; los observadores concretos solo los conoce Spring.
- Los componentes interesados se actualizan cuando hay un cambio, no cuando preguntan.
- Agregar un componente interesado no obliga a modificar el código existente.

---

## Recursos recomendados

- [Refactoring Guru: Observer](https://refactoring.guru/es/design-patterns/observer)
- [Spring Framework: inyección de colecciones de beans](https://docs.spring.io/spring-framework/reference/core/beans/dependencies/factory-autowire.html)
- [Spring Framework: eventos de la aplicación](https://docs.spring.io/spring-framework/reference/core/beans/context-introduction.html#context-functionality-events)
- [OWASP: Prevención de inyección SQL](https://cheatsheetseries.owasp.org/cheatsheets/SQL_Injection_Prevention_Cheat_Sheet.html)
- [Docker Compose: referencia del archivo](https://docs.docker.com/reference/compose-file/)

---

## Créditos y uso académico

**Autor:** César Augusto Vega Fernández
**Curso:** Diseño y Arquitectura de Software, Universidad de La Sabana

## Licencia de uso

Este material se distribuye bajo la licencia [Creative Commons Atribución-NoComercial-CompartirIgual 4.0 Internacional (CC BY-NC-SA 4.0)](https://creativecommons.org/licenses/by-nc-sa/4.0/deed.es).
