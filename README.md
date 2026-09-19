This is the miniproject repo for 4156 in Fall 2026.

# Tax API

A Spring Boot REST API for managing clients, items, and calculating sales tax quotes.

## Run the application

```bash
mvn spring-boot:run
```

## Run tests and view coverage

```bash
mvn test
```

After the tests finish, open `target/site/jacoco/index.html` in a browser to view the JaCoCo coverage report.

## Static analysis with PMD

I used PMD as the static bug finder for this project. PMD is free and open source.

### Install PMD on macOS

```bash
cd $HOME
curl -OL https://github.com/pmd/pmd/releases/download/pmd_releases%2F7.27.0/pmd-dist-7.27.0-bin.zip
unzip pmd-dist-7.27.0-bin.zip
alias pmd="$HOME/pmd-bin-7.27.0/bin/pmd"
```

### Run PMD

From the project root, run:

```bash
pmd check -d src/main/java -R rulesets/java/quickstart.xml -f text
```

PMD prints the file, line number, rule name, and description for each violation. I reviewed the results manually and fixed the issues that were actual bugs.

See `bugs.txt` for the bugs found and fixed during Part 0, testing, manual API testing, and PMD analysis.