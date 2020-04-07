## PR Hawk [![Master Build](https://github.com/michaelcamilleri/prhawk/workflows/Master%20Build/badge.svg)](https://github.com/michaelcamilleri/prhawk/actions?query=workflow%3A%22Master+Build%22)

### Setup

#### Requirements
 - JDK8+
 - Maven

#### Checkout
```
git clone https://github.com/michaelcamilleri/prhawk.git
cd prhawk
```

#### Build
```
mvn package
```

#### Start
```
mvn spring-boot:run
```

#### Try it out
- Use the form to enter username and select whether to list pull requests: http://localhost:8080

Or access the user page directly (no styling):
- List all user repositories: http://localhost:8080/user/{username}
- List all user repositories and PRs: http://localhost:8080/user/{username}?lisPRs=true
- Get first page of user repositories, limit to 10 results per page: http://localhost:8080/user/{username}?page=1&perPage=10
