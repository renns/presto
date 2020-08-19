# Deploy Steps
- Create branch from official release branch
    - e.g. branch `a8/release-0.239` from branch `release-0.239`
- Apply A8 changes to `presto-main` and `presto-spi` modules
- Run `./mvnw clean compile -pl presto-main -am` to verify everything compiles
- Commit A8 changes
- Change version in pom.xml files across the project
    - e.g. Change `0.239` to `0.239-a8`
- Run `./mvnw deploy -DskipTests -P locus -pl presto-common`
- Run `./mvnw deploy -DskipTests -P locus -pl presto-spi`
- Run `./mvnw deploy -DskipTests -P locus -pl presto-main`

# Package Steps
- Download appropriate presto-server release package from https://repo1.maven.org/maven2/com/facebook/presto/presto-server/
    - e.g. https://repo1.maven.org/maven2/com/facebook/presto/presto-server/0.239/presto-server-0.239.tar.gz
- Replace `presto-spi` jar file in release package with packaged jar file
    - e.g. Replace `presto-server-0.239/lib/presto-spi-0.239.jar` with `presto-spi/target/presto-spi-0.239-a8.jar`
- Replace `presto-main` jar file in release package with packaged jar file
    - e.g. Replace `presto-server-0.239/lib/presto-main-0.239.jar` with `presto-main/target/presto-main-0.239-a8.jar`
- Append `-a8` to the base filename
    - e.g. `presto-server-0.239-a8.tar.gz`
