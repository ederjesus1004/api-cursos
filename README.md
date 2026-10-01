# API de Cursos con Spring Security y JWT

API REST para gestionar cursos con registro, inicio de sesión y control de acceso por roles. La hice para aprender cómo se protege un backend con Spring Security y tokens JWT.

Cualquier usuario puede registrarse y ver los cursos. Solo un administrador puede crearlos, editarlos o eliminarlos.

## Tecnologías

| Tecnología | Uso |
|------------|-----|
| Java 21 | Lenguaje |
| Spring Boot | Framework principal |
| Spring Security | Autenticación y autorización |
| JJWT 0.12 | Creación y validación de tokens JWT |
| Spring Data JPA | Acceso a la base de datos |
| PostgreSQL 16 | Base de datos |
| Lombok | Menos código repetido (getters, setters, constructores) |
| Docker | Levantar PostgreSQL sin instalarlo |

## Cómo funciona la seguridad

```
1. LOGIN
Cliente --(email + contraseña)--> /api/auth/login --> valida en la BD --> devuelve un token

2. PETICIONES PROTEGIDAS
Cliente --(Authorization: Bearer <token>)--> JwtAuthFilter --> SecurityConfig --> Controller
                                                 |                  |
                                          ¿token válido?     ¿el rol tiene permiso?
                                           no → 401             no → 403
```

- Las contraseñas se guardan con **BCrypt**, nunca en texto plano.
- El token dura **1 hora** y está firmado con una clave secreta.
- El servidor no guarda sesiones. Cada petición se identifica con su token.
- Al registrarse, todos los usuarios son `USER`. El rol no se puede elegir desde el cliente.

## Roles y permisos

| Acción | Sin token | USER | ADMIN |
|--------|:---------:|:----:|:-----:|
| Registrarse e iniciar sesión | ✅ | ✅ | ✅ |
| Ver cursos | ❌ 401 | ✅ | ✅ |
| Crear, editar y eliminar cursos | ❌ 401 | ❌ 403 | ✅ |

## Estructura del proyecto

```
src/main/java/com/ejemplo/cursos/
├── model/          # entidades Curso y Usuario, enum Rol
├── repository/     # acceso a la base de datos
├── dto/            # datos de registro, login y respuesta
├── security/       # JwtService, JwtAuthFilter y UsuarioDetailsService
├── config/         # reglas de seguridad y creación del admin inicial
├── service/        # lógica de cursos y de autenticación
├── controller/     # endpoints REST
└── exception/      # manejo global de errores
```

## Requisitos

- Java 21
- Docker Desktop
- Postman (para probar)

## Cómo ejecutarlo

**1. Clonar el repositorio**

```bash
git clone https://github.com/ederjesus1004/api-cursos.git
cd api-cursos
```

**2. Levantar PostgreSQL**

```bash
docker compose up -d
```

La base queda en `localhost:5435`.

**3. Ejecutar la API**

```bash
./mvnw spring-boot:run
```

En Windows usa `mvnw.cmd spring-boot:run`. La API queda en `http://localhost:8080`.

Al arrancar por primera vez se crea un usuario administrador:

| Email | Contraseña |
|-------|------------|
| `admin@cursos.com` | `Admin123` |

## Endpoints

### Autenticación (públicos)

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/auth/registro` | Crea una cuenta con rol USER y devuelve un token |
| POST | `/api/auth/login` | Inicia sesión y devuelve un token |

### Cursos (requieren token)

| Método | Ruta | Rol necesario |
|--------|------|---------------|
| GET | `/api/cursos` | USER o ADMIN |
| GET | `/api/cursos/{id}` | USER o ADMIN |
| POST | `/api/cursos` | ADMIN |
| PUT | `/api/cursos/{id}` | ADMIN |
| DELETE | `/api/cursos/{id}` | ADMIN |

## Ejemplos

**Registro**

`POST /api/auth/registro`

```json
{
  "nombre": "Eder Cuaresma",
  "email": "eder@correo.com",
  "password": "clave123"
}
```

Respuesta `201`:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "eder@correo.com",
  "rol": "USER"
}
```

**Usar el token**

En cada petición protegida se manda esta cabecera:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

En Postman: pestaña **Authorization** > **Bearer Token** > pegar el token.

**Crear un curso (solo ADMIN)**

`POST /api/cursos`

```json
{
  "nombre": "Programación Web",
  "descripcion": "Angular y Spring Boot",
  "creditos": 4
}
```

## Códigos de respuesta

| Código | Cuándo sale |
|--------|-------------|
| 200 | Petición correcta |
| 201 | Recurso creado |
| 204 | Curso eliminado |
| 400 | Datos inválidos (por ejemplo, contraseña de menos de 6 caracteres) |
| 401 | Sin token, token vencido o email/contraseña incorrectos |
| 403 | Tiene token pero su rol no tiene permiso |
| 404 | El curso no existe |
| 409 | El email ya está registrado |

## Variables de entorno

Todas tienen un valor por defecto para trabajar en local. En producción se deben cambiar.

| Variable | Para qué sirve |
|----------|----------------|
| `DB_URL` | URL de conexión a PostgreSQL |
| `DB_USER` | Usuario de la base de datos |
| `DB_PASSWORD` | Contraseña de la base de datos |
| `JWT_SECRET` | Clave para firmar los tokens (mínimo 32 caracteres) |
| `ADMIN_EMAIL` | Email del administrador inicial |
| `ADMIN_PASSWORD` | Contraseña del administrador inicial |

## Lo que aprendí

- La diferencia entre autenticación (401) y autorización (403)
- Por qué las contraseñas se guardan como hash y no como texto
- Cómo se arma y se valida un token JWT, y por qué no se deben poner datos privados dentro
- Que el filtro JWT solo identifica al usuario y que las reglas de acceso se definen en `SecurityConfig`
- Por qué el registro usa un DTO sin campo `rol`

## Autor

**Eder Cuaresma**
Estudiante de Ingeniería de Sistemas e Informática, UTP

- GitHub: [@ederjesus1004](https://github.com/ederjesus1004)
