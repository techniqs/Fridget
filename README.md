Fridget
=======

### Building Fridget

1. Install:
    * Oracle JDK 9+
2. Navigate to the proj_ws17_sepm_qse_01 (fridget) code directory
3. Build the server as well as the client with the following commands

#### Server + Client:
```
./mvnw clean verify
```

#### Server:
```
./mvnw -pl=server -am clean verify
```

#### Client
```
./mvnw -pl=client -am clean verify
```

### Running Fridget

1. Navigate to the fridget code directory
2. Download FakeSMTP and start it on port 25 
3. Run the server as well as the client with with the following commands

#### Server:
```
./mvnw -pl=server -am spring-boot:run
```

#### Client
```
./mvnw -pl=client -am spring-boot:run
```


Links
-----

- [GIT Guidelines](docs/git.md)

