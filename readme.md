### Mercury

A tiny showcase project facilitating my studies and covering basic usage of Spring Security. It's a Web service issuing JWTs in two mutually exclusive ways:
- contained in a header, to be consumed by other Web services.
- contained in a hardened cookie, to be consumed by Web applications.

The Docker setup is as simple as possible, not optimized in any way.

---

Mercury can be built with:
- `mvn install` if one has JDK 11 and Maven set up.
- `./containerization/build-in-container.sh` if one has Docker set up.

The `python-scripts` directory contains scripts i use during development, but they require my local environment. To run Mercury using Docker, run `./containerization/run-in-container.sh`. The system accepts two environment variables:
- `JWT_SECRET`: a secret key to use for JWT signatures, at least 512 bits (32 BMP characters). If not provided, a random one will be generated.
- `JWT_VALIDITY_MINUTES`: JWT validity period in minutes, 60 if not provided.

Example : `JWT_SECRET=00000000000000000000000000000000 JWT_VALIDITY_MINUTES=518400 ./containerization/run-in-container.sh`.

System tests are invoked by activating `systemTests` Maven profile. These require a running Mercury instance, *http://localhost:9001* is used by default. `mercury.test.protocol` and `mercury.test.service` can be used to point the tests to a different instance.

---

There is one hard-coded user and two JWT-generating endpoints. Example requests:
- `curl -v -X POST -H "Content-Type: application/json" -d '{ "username" : "yakom", "password" : "yakom" }' http://localhost:9001/authenticate-ws`: returns a JWT in a header.
- `url -v -X POST -H "Content-Type: application/json" -d '{ "username" : "yakom", "password" : "yakom" }' http://localhost:9001/authenticate-ui`: returns a JWT in a cookie.

One can then invoke the endpoint returning one's username:
- `curl -v -H "JWT:$jwt" http://localhost:9001/ws/me`: the WS version.
- `curl -v -b "MERCURY_TOKEN=$jwt" http://localhost:8080/ui/me`: the UI version.

---

Links:
- [Tomcat documentation](https://tomcat.apache.org/tomcat-9.0-doc/index.html).
- [Spring documentation](https://docs.spring.io/spring/docs/5.2.6.RELEASE/spring-framework-reference/).
- [Spring Security documentation](https://docs.spring.io/spring-security/site/docs/5.3.3.RELEASE/reference/html5/).
