# EXPLAINED.md - Poora project kaise kaam karta hai (Hinglish)

Ye file interview ke liye hai. Har part ka: **kya hai, kyu hai, kaise chalta hai**.

---

## 1. Big picture: ek request ka safar

```
Client (Swagger/Postman)
   |  GET /products   +   Header: Authorization: Bearer <token>
   v
[JwtAuthFilter]      -> token padhta hai, valid hai to user ko "logged in" mark karta hai
   v
[Spring Security rules (SecurityConfig)]  -> role check: USER/ADMIN allowed? nahi to 401/403
   v
[ProductController]  -> request receive, @Valid se validation
   v
[ProductService]     -> business logic
   v
[ProductRepository]  -> database se baat (JPA)
   v
Database (H2 / PostgreSQL)
```

Agar koi bhi jagah error aaye -> **GlobalExceptionHandler** (ya SecurityConfig ke handlers) ek jaisa JSON error bhejte hain.

**Layers kyu?** Har class ka ek hi kaam (Single Responsibility). Controller = HTTP, Service = logic, Repository = DB. Isse code test karna aur badalna easy hota hai.

---

## 2. Login flow (JWT kaise milta hai)

1. Client `POST /api/auth/login` bhejta hai `{username, password}` ke saath.
2. `AuthController.login()` -> `authenticationManager.authenticate(...)` call karta hai.
3. AuthenticationManager andar se `CustomUserDetailsService.loadUserByUsername()` chalata hai -> DB se user nikalta hai.
4. Spring password ko **BCrypt** se compare karta hai (DB me hash hai, plain password nahi).
5. Galat hua -> `BadCredentialsException` -> `GlobalExceptionHandler` -> **401**.
6. Sahi hua -> `JwtService.generateToken()` token banata hai -> response me token milta hai.

### JWT kya hai?
Teen parts, dot (`.`) se separated: `header.payload.signature`
- **Header**: algorithm (HS256)
- **Payload**: data (username = `subject`, roles, issuedAt, expiration)
- **Signature**: header+payload ko **secret key** se sign kiya hua hash

Server ko token yaad nahi rakhna padta (**stateless**). Bas signature verify karta hai. Agar koi payload badle to signature match nahi hoga -> reject.
Payload encrypted nahi hota, sirf encoded hota hai. Isliye token me password ya sensitive data kabhi nahi daalte.

---

## 3. Protected request flow

`JwtAuthFilter` (har request pe chalta hai):
1. `Authorization` header dekho. `Bearer ` se start hota hai?
2. Token se username nikalo (`JwtService.extractUsername`). Yahin signature + expiry verify hoti hai. Bad token pe `JwtException` aati hai.
3. DB se user load karo, `UsernamePasswordAuthenticationToken` banao, `SecurityContextHolder` me daal do. Matlab "ye request authenticated hai".
4. Token galat hai to kuch set nahi hota. Aage Spring Security dekhta hai ki endpoint protected hai aur user authenticated nahi -> **401**.

Filter `OncePerRequestFilter` extend karta hai taaki ek request me sirf ek baar chale.
Filter me `@Component` nahi lagaya, kyunki wo double register ho jata hai (ek baar Spring Boot ke servlet filter me, ek baar security chain me). Isliye `SecurityConfig` me `new JwtAuthFilter(...)` karke manually jodha.

---

## 4. SecurityConfig - rules ek ek karke

```java
.csrf(csrf -> csrf.disable())
```
CSRF attack cookie/session wale login pe hota hai (browser khud cookie bhejta hai). Hum token header use karte hain jo browser khud nahi bhejta, to CSRF disable safe hai.

```java
.sessionManagement(STATELESS)
```
Server session nahi banata. Har request me token aata hai.

```java
.requestMatchers("/api/auth/**", swagger...).permitAll()          // login aur docs sab ke liye
.requestMatchers(GET, "/products", "/products/**").hasAnyRole("USER","ADMIN")
.requestMatchers("/products", "/products/**").hasRole("ADMIN")     // POST, PUT, DELETE
.anyRequest().authenticated()
```
**Order important hai.** Spring upar se neeche match karta hai aur pehla match jeet-ta hai. GET wala rule pehle hai, isliye GET USER ko mil jata hai. Baaki methods ADMIN ko.

`hasRole("ADMIN")` internally `ROLE_ADMIN` dhoondta hai. `CustomUserDetailsService` me `.roles("ADMIN")` isi liye `ROLE_ADMIN` banata hai.

### 401 vs 403 (interview ka favourite)
- **401 Unauthorized** = "tum kaun ho, pata nahi" (token nahi hai / galat hai / expire ho gaya).
- **403 Forbidden** = "tum kaun ho pata hai, lekin tumhe ye karne ki permission nahi" (USER ne POST kiya).

### 401/403 ka JSON alag se kyu likha?
Filter controller se **pehle** chalta hai. `@RestControllerAdvice` sirf controller ke andar ke errors pakadta hai. Isliye `authenticationEntryPoint` (401) aur `accessDeniedHandler` (403) me manually JSON likha.

### PasswordEncoder (BCrypt)
One-way hash + random salt. Same password ka hash har baar alag banta hai. Hash se password wapas nahi nikal sakte. DB leak ho jaye to bhi passwords safe rehte hain.

---

## 5. Entities aur DTOs

**Entity** (`Product`, `User`) = DB table ka Java form (`@Entity`).
**DTO** (`ProductRequest`, `ProductResponse`) = API ka contract.

### Entity ko seedha API me kyu expose nahi karte?
- `User` expose karte to password hash bahar jaata.
- Client `id` ya internal fields set kar sakta hai (mass assignment).
- DB badli to API bhi toot jaati hai.
- Validation aur response shape alag-alag control nahi hota.

`ProductRequest` me validation annotations hain:
- `@NotBlank`, `@Size` (name, description)
- `@NotNull`, `@DecimalMin(0, inclusive=false)`, `@Digits` (price)
- `@NotNull`, `@Min(0)` (stockQuantity)

`@Valid` controller me lagne se Spring in rules ko check karta hai. Fail hone pe `MethodArgumentNotValidException` throw hoti hai, jise handler 400 me badal deta hai.

`record` = chhota immutable class (Java 16+). Getters/constructor automatic.
`PageResponse` isliye banaya ki Spring ka `Page` object seedha JSON me dena recommended nahi hai, uska shape version ke saath badal sakta hai.

---

## 6. Product CRUD

| Method | Kaam | Success status |
|---|---|---|
| POST /products | create | 201 Created |
| GET /products | list (pagination) | 200 |
| GET /products/{id} | ek product | 200 |
| PUT /products/{id} | update | 200 |
| DELETE /products/{id} | delete | 204 No Content |

`ProductService`:
- `findOrThrow(id)` -> nahi mila to `ResourceNotFoundException` -> **404**.
- `@Transactional` -> method ke andar ke saare DB kaam ek transaction me. Fail hua to rollback.
- `@Transactional(readOnly = true)` -> sirf padhne wale methods ke liye, thoda optimized.

### Pagination aur sorting
`GET /products?page=0&size=5&sort=price,desc`
- Spring `Pageable` object khud bana deta hai query params se.
- `repository.findAll(pageable)` SQL me `LIMIT/OFFSET` aur `ORDER BY` lagata hai.
- `@PageableDefault(size=10, sort="id")` = kuch na bheja to ye default.
- `max-page-size=50` = koi `size=100000` na maange.
- Service me sort field whitelist hai. Galat field (`sort=abc`) pe 500 nahi, saaf 400 milta hai.

---

## 7. GlobalExceptionHandler

`@RestControllerAdvice` = saare controllers ke liye ek common error catcher.
`@ExceptionHandler(X.class)` = "X exception aaye to ye method chalao".

| Exception | Status |
|---|---|
| MethodArgumentNotValidException | 400 + field-wise details |
| HttpMessageNotReadableException | 400 (toota hua JSON) |
| MethodArgumentTypeMismatchException | 400 (`/products/abc`) |
| BadRequestException | 400 (galat sort field) |
| AuthenticationException | 401 (galat login) |
| AccessDeniedException | 403 |
| ResourceNotFoundException / NoResourceFoundException | 404 |
| Exception (baaki sab) | 500 (detail sirf log me, client ko nahi) |

Sab ka format same: `timestamp, status, error, message, details`.

---

## 8. Config files

- `DataSeeder` (`CommandLineRunner`): app start hote hi admin/user aur 3 sample products bana deta hai (agar tables khaali hain).
- `OpenApiConfig`: Swagger me **Authorize** button aata hai (`@SecurityScheme`), controller pe `@SecurityRequirement` se link hota hai.
- `application.properties`: DB, JWT secret/expiry (env var se override hota hai: `${JWT_SECRET:default}`), pagination limit.
- `application-postgres.properties`: `postgres` profile active karne pe PostgreSQL use hota hai.

---

## 9. Tests (JUnit 5 + MockMvc)

- `@SpringBootTest` = poori app start hoti hai (real security, real H2 DB).
- `@AutoConfigureMockMvc` + `MockMvc` = bina server port khole HTTP requests simulate karna.
- Test flow: `perform(request)` -> `andExpect(status/jsonPath)`.
- Login test token nikalta hai, phir usko `Authorization` header me daal ke product APIs hit karta hai.
- Covered: login success, wrong password, blank login, no token, garbage token, USER ka 403, create/get/update/delete, validation 400, pagination, invalid sort.

**MockMvc vs Mockito:** MockMvc HTTP layer test karta hai. Mockito dependencies ko fake (mock) karta hai. Yahan integration style test hai, isliye real beans use hue.

---

## 10. Docker (multi-stage)

- **Stage 1 (build):** Maven + JDK wali badi image. `mvn package` se JAR banta hai.
- **Stage 2 (run):** sirf JRE wali chhoti image. Stage 1 se sirf JAR copy hota hai.
- Fayda: final image chhoti aur secure (Maven, source code, build tools nahi hote).
- `COPY pom.xml` + `dependency:go-offline` pehle karne se Docker cache use hota hai, dependencies baar-baar download nahi hoti.
- Non-root user (`appuser`) se app chalti hai (security best practice).

---

## 11. Interview Q&A (taiyari)

1. **JWT kya hai?** Signed token (header.payload.signature) jo stateless authentication deta hai.
2. **Token expire hone pe?** `parseSignedClaims` `ExpiredJwtException` fenkta hai -> filter pakadta hai -> 401.
3. **Secret leak ho jaye to?** Koi bhi fake token bana sakta hai. Isliye secret env var me rakhte hain, aur rotate karte hain.
4. **Stateless matlab?** Server koi session yaad nahi rakhta.
5. **Role change turant kaise reflect hota hai?** Filter har request pe DB se user load karta hai.
6. **`@Valid` kaise kaam karta hai?** Hibernate Validator annotations check karta hai, fail pe exception, handler 400 deta hai.
7. **PUT vs PATCH?** PUT poora object replace karta hai, PATCH partial update.
8. **Page vs List?** Page me total count aur page info bhi hoti hai.
9. **`ddl-auto=update` production me kyu risky?** Schema auto badalta hai. Production me Flyway/Liquibase use karte hain.
10. **H2 kyu?** Zero setup, development/test ke liye. Production ke liye PostgreSQL.
11. **Improvement ideas?** Refresh tokens, Flyway, rate limiting, audit fields (createdAt), role management API, Testcontainers.

---

## 12. Khud se check karna (interview se pehle)

1. `mvn test` -> sab tests green hone chahiye.
2. `mvn spring-boot:run` -> Swagger kholo, login karo, Authorize karo.
3. Product create/update/delete karo. Galat data bhejo, error format dekho.
4. `user/user123` se POST karo -> 403.
5. Bina token ke GET -> 401.
6. `docker build` + `docker run` karke wahi dobara test karo.
7. Code ko ek baar khud type karke ya padh ke samjho. Interviewer kuch bhi poochh sakta hai.
