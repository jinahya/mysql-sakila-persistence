# mysql-sakila-persistence

Jakarta Persistence mappings for the [MySQL Sakila Sample Database](https://dev.mysql.com/doc/sakila/en/).

## The database

```shell
$ ./_docker-compose-up.sh
```

Brings up `mysql:9.7` (the current LTS) on `localhost:3306`, as `root` / `password`, and follows its log; Ctrl-C
takes it down again. Every run starts from an empty database: the image loads
[`docker-entrypoint-initdb.d`](docker-entrypoint-initdb.d) in name order,

| file                   | from                                                                         |
|------------------------|------------------------------------------------------------------------------|
| `01-sakila-schema.sql` | `sakila-db/sakila-schema.sql` of https://downloads.mysql.com/docs/sakila-db.tar.gz |
| `02-sakila-data.sql`   | `sakila-db/sakila-data.sql` of the same                                      |

both unchanged.

```shell
$ docker exec -it mysql-sakila mysql -uroot -ppassword -Dsakila
```

## Building

Two persistence providers, one profile each; `jakarta-ee-11-hibernate-orm` is active by default.

```shell
$ ./_mvn_jakarta_ee_11_test_hibernate-orm.sh              # unit tests, H2 in MySQL mode
$ ./_mvn_jakarta_ee_11_test_eclipselink.sh
$ ./_mvn_jakarta_ee_11_verify_hibernate-orm.sh            # + integration tests, against the database above
$ ./_mvn_jakarta_ee_11_verify_eclipselink.sh
$ ./_mvn_jakarta_ee_11_verify_notests_hibernate-orm.sh    # no database needed
$ ./_mvn_jakarta_ee_11_verify_notests_eclipselink.sh
```
