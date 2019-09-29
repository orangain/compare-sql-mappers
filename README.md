# compare-sql-mappers

A program to check what kind of types can SQL mappers bind or map.

SQL mappers to compare:

- [Apache Commons DbUtils](https://commons.apache.org/proper/commons-dbutils): 1.7
- [Spring JDBC Template](https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/data-access.html): 5.1.9.RELEASE
- [MyBatis](https://mybatis.org/mybatis-3/ja/index.html): 3.5.2
- [JDBI](http://jdbi.org/) (with jdbi3-postgres): 3.10.1
- [Sql2o](https://www.sql2o.org/) (with sql2o-postgres): 1.6.0

## How to run

1. Launch PostgreSQL
   ```
   $ docker-compose up
   ```
2. Run program
   ```
   $ ./gradlew run
   ```

## Result

See: https://docs.google.com/spreadsheets/d/1Tf3uB5xFIZnP1ieSu9JQdKaGDY_XfJyGJYleAXI3328/edit#gid=1885614317

## License

MIT License. See LICENSE.
