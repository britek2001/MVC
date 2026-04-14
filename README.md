# Game Execution 

cd ~/MVC/livraison
mvn clean compile
mvn clean test

## Executer le jeu 
java -cp target/classes mvc.Main

# Test Execution 

## Compiler le projet
mvn clean compile

## Compiler + exécuter tous les tests
mvn clean test

## Compiler toutes les classes (src + tests)
javac -cp ".:lib/junit-platform-console-standalone-1.10.2.jar" -d out $(find src -name "*.java")

## Exécuter tous les tests
java -jar lib/junit-platform-console-standalone-1.10.2.jar --class-path out --scan-class-path


## SonarQube 
mvn sonar:sonar \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.organization=bob239999999 \
  -Dsonar.projectKey=britek2001_MVC \
  -Dsonar.login=46c521ce41ec17c72a887cd33ade3c72a650b299
