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
