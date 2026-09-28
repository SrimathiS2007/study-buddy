# Study Buddy - Backend (Spring Boot 3, JPA, MySQL, Swagger)

## Run
1. Need JDK 17+ and Maven. In VS Code install "Extension Pack for Java" + "Spring Boot Extension Pack".
2. Open folder `study-buddy`.
3. **With MySQL:** edit username/password in `src/main/resources/application.properties`
   (database `studybuddy` is created automatically), then:  `mvn spring-boot:run`
4. **Without MySQL (quick test):** `mvn spring-boot:run -Dspring-boot.run.profiles=h2`
5. Swagger UI: http://localhost:8080/swagger-ui.html

## Swagger test flow (normal path)
1. POST /api/students        {"name":"Arun","email":"arun@x.com"}      -> 201 (id 1)
2. POST /api/students        {"name":"Priya","email":"priya@x.com"}    -> 201 (id 2)
3. GET  /api/subjects        -> 4 seeded subjects (ids 1-4)
4. POST /api/groups          {"name":"DBMS Finals","subjectId":3,"creatorId":1,"capacity":2} -> 201
5. POST /api/groups/1/members {"studentId":2}                          -> 201  (group now full)
6. GET  /api/subjects/3/groups -> shows currentMembers 2 / capacity 2 / full true
7. DELETE /api/groups/1/members/2?requesterId=1  (creator removes)     -> 204

## Edge cases (all handled)
| Case | Result |
|---|---|
| Join a full group | 409 "Study group 1 is full" |
| Same student joins twice | 409 "already a member" |
| Unknown group / student / subject | 404 |
| capacity 0 / negative / missing, blank name, bad email | 400 with fieldErrors |
| Malformed JSON, non-numeric id | 400 |
| Non-creator removes someone / edits / deletes group | 403 |
| Creator tries to leave | 403 (delete the group instead) |
| Remove a student who isn't a member | 404 |
| Lower capacity below current member count | 409 |
| Duplicate subject name / email / group name in subject | 409 |
| Two people grab last seat at once | row lock -> one succeeds, one 409 |
