release: java -cp target/elide-spring-boot-1.0.jar -Dloader.main=liquibase.integration.commandline.Main org.springframework.boot.loader.PropertiesLauncher --changeLogFile=src/main/resources/db/changelog/changelog.xml --url=$JDBC_DATABASE_URL update
web: java -Xmx224m -Dspring.profiles.active=production -jar target/elide-spring-boot-1.0.jar
