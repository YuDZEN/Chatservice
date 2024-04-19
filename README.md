<!-- ABOUT THE PROJECT -->
## About The Project

DcissApp messenger service

<!-- GETTING STARTED -->
## Getting Started

### Prerequisites

Java 11 or higher
Maven

### Installation

1. Clone the repo
```sh
   git clone 
```
2. Compile/build
 ```sh
   mvn package
```

3. Clean the project
```sh
   mvn clean
```

4. Remove the correct ligns in the file pom.xml, either these ones below or the other ones depending on your version
```xml
   <dependency>
      <groupId>mysql</groupId>
     	<artifactId>mysql-connector-java</artifactId>
      <version>8.0.28</version>
   </dependency>

   <!-- https://mvnrepository.com/artifact/org.mindrot/jbcrypt -->
		
   <dependency>
      <groupId>org.mindrot</groupId>
      <artifactId>jbcrypt</artifactId>
      <version>0.4</version>
   </dependency>
```

5. Change this lign in DatabaseManager.java if you can't name your DB with Upper Case characters :
```java
   private static final String DB_URL = "jdbc:mysql://localhost/chat_service";
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- USAGE EXAMPLES -->
## Usage

Launch server
   ```sh
      mvn package exec:java -Dexec.mainClass="fr.uga.miashs.dciss.chatservice.server.ServerMsg"
   ```
or (mvn package required to recompile if changes are made)
   ```sh
      java -cp target/chatservice-0.0.1-SNAPSHOT-jar-with-dependencies.jar fr.uga.miashs.dciss.chatservice.server.ServerMsg
   ```

Launch client
   ```sh
      mvn package exec:java -Dexec.mainClass="fr.uga.miashs.dciss.chatservice.client.ClientMsg"
   ```
or (mvn package required to recompile if changes are made)
   ```sh
      java -cp target/chatservice-0.0.1-SNAPSHOT-jar-with-dependencies.jar fr.uga.miashs.dciss.chatservice.client.ClientMsg
   ```

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- LICENSE -->
## License

Distributed under the MIT License. See `LICENSE.txt` for more information.

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- CONTACT -->
## Contact


<!-- ACKNOWLEDGMENTS -->
## Acknowledgments

* []()
* []()
* []()