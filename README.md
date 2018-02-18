# README

This solution is a (albeit crude) demo of on-platform flyway, 
using Spring Cloud Task, and Cloud Foundry Task for executing 
Database migrations.

There are three scenarios of using Flyway:

1. Off-platform Migration
2. On-platform Eager Migration
3. On-Platform Task Migration 

## Off-Platform Migration

The current CND migration lab uses this approach of running flyway off 
the Pal Tracker "Platform".  
The migration scripts are located, and Flyway is executed, external to 
the application.  
The gradle build uses the flyway plugin to sequence through its build.

We will not cover in this doc, it is fully covered under current CND
migration lab.  

## On-Platform Migrations
### Overview
Cloud Native guidelines push "Admin Processes" as part of 12 Factors.

But Kevin Hoffman argues against use of Admin Processe from interactive
or off platform shells.
This is somewhat of what the current CND lab does with flyway migrations 
and SSH.

The proposed solution does not completely do away with admin processes,
but provides a way to handle on platform, with platform supported tools
and methods, all while deploying from the domain's application
deployment.

### Solution
Flyway is deployed as a dependency with the `pal-tracker` application
and runs embedded with the pal-tracker application, both in-process,
and as a Spring Cloud Task.

The database migrations are packaged as resources within the 
`pal-tracker` application, and are sourced as classpath resources by
embedded flyway application.

There are several configuration properties used to drive the on-platform
behavior:

1. STAGE: Boolean - if false or not set, the migration will be eager.

1. CLEAN: Boolean - if set to true, flyway clean and baseline will be
run.

1. MIGRATION: Boolean - if set to true, flyway migrate will be run.

**NOTE**: CLEAN and MIGRATE must not be run together at this time (although
future evolution of example may set dependent tasks between clean and
migration with Spring Cloud Dataflow or Batches).

## On-Platform Eager Migration

Springboot will automatically wire Flyway to the SQL initializer that is
run during Springboot application startup.

Simply starting or pushing `pal-tracker` *without* `STAGE` environment
variable set will execute the packaged flyway migrations.
 
Eager migrations are suitable *only* when the database changes are
coupled to an app deployment (i.e. `cf push`), and the nature of the
migration is simple DDL executed in a short running task.

## On-Platform Task Migration

The On-platform task migration approach builds upon the On-platform
eager migration solution, but by suppressing the SQL initialization
of Flyway, and relying on either manual or scheduled Spring Cloud
Tasks that will execute the migrations.

The way this works is as follows:

1. The default migration strategy executed by the initializer may be
overridden by implementing a `FlywayMigrationStrategy`, and overriding
the `migrate()` method.  

1. The `StageMigrationStrategy` overrides migration with a Noop.  It
requires running `pal-tracker` with `STAGE=true`.  Running without or
`STAGE=false` will result in On-Platform Eager Migration.

1. `FlywayCleanTaskConfig` is a configuration class that sets up a
Spring Cloud Task that will execute a `Flyway.clean()`, followed by
`Flyway.baseline()` to initialize a database at version zero.

1. `FlywayMigrationTaskConfig` is a configuration class that sets up a
Spring Cloud Task that will execute a `Flyway.migrate()`.

## Monitoring Flyway State from Running Application

After starting `pal-tracker`, Springboot will wire flyway migration
monitor through actuator through the following endpoint:

```bash
curl {springboot app url}/flyway
```

## Executing Migrations
### On Platform Eager Migration - Local

Execute this either immediately after creation of the database,
or after executing below CLEAN task:

```bash
SERVER_PORT=8080 ./gradlew clean bootRun
```

That's it!

Check out flyway migrations here:

```bash
curl http://localhost:8080/flyway
```

### On Platform Eager Migration - Cloud Foundy

Remove `STAGE` environment variable from manifest.

Push `pal-tracker` to Cloud Foundry:

```bash
cf push pal-tracker
```

That's it!

Check out flyway migrations here:

```bash
curl http://{pal-tracker-route}/flyway
```

### On Platform Task Migration - Local
#### Run Monitoring Application

```bash
SERVER_PORT=8080 STAGE=true ./gradlew clean bootRun
```

Check out flyway migrations here:

```bash
curl http://localhost:8080/flyway
```

#### Clean and Baseline

```bash
SERVER_PORT=8081 STAGE=true CLEAN=true ./gradlew clean bootRun
```

Check out flyway migrations here:

```bash
curl http://localhost:8080/flyway
```

What is the state of migration #1?  Why (hint:  It is only staged).

*NOTE*: Look at the exceptions, you will see Spring Cloud Tasks
fail.  Why?

#### Migrate

```bash
SERVER_PORT=8082 STAGE=true MIGRATE=true ./gradlew clean bootRun
```

Check out flyway migrations here:

```bash
curl http://localhost:8080/flyway
```

### On Platform Task Migration - Cloud Foundry

#### Run Monitoring Application

Add `STAGE` environment variable back to manifest.
Simply push the `pal-tracker` application:

```bash
cf push pal-tracker -f manifest-review.yml
```

Check out flyway migrations here:

```bash
curl http://{pal-tracker-route}/flyway
```

#### Clean and Baseline

Run a Cloud Foundry task under pal-tracker application

```bash
cf run-task pal-tracker 'STAGE=true CLEAN=true $PWD/.java-buildpack/open_jdk_jre/bin/java -cp $PWD/. org.springframework.boot.loader.JarLauncher' --name clean-db
```

Monitor logs:

```bash
cf logs pal-tracker
```

Monitor state of the task:

```bash
cf tasks pal-tracker
```

*NOTE*: This task will fail; however, the DB state will be properly
initialized.  See notes below.

Check out flyway migrations here:

```bash
curl http://{pal-tracker-route}/flyway
```

#### Migrate

Run a Cloud Foundry task under pal-tracker application

```bash
cf run-task pal-tracker 'STAGE=true MIGRATE=true $PWD/.java-buildpack/open_jdk_jre/bin/java -cp $PWD/. org.springframework.boot.loader.JarLauncher' --name migration-db
```

Monitor logs:

```bash
cf logs pal-tracker
```

Monitor state of the task:

```bash
cf tasks pal-tracker
```

*NOTE*: This task will succeed.

Check out flyway migrations here:

```bash
curl http://{pal-tracker-route}/flyway
```

## Monitoring Flyway State from Running Application
Springboot automatically wires actuator with an endpoint to view the
current state of flyway migrations:

```bash
curl {springboot app url}/flyway
```

Check out flyway migrations here:

```bash
curl http://{pal-tracker-route}/flyway
```

## Caveats and Limitations

- The current implementation using default autoconfig and wiring.
It only handles single data source.

- The `FlywayCleanTaskConfig` will fail the Spring Cloud and Cloud
Foundry Task execution - The `Flyway.clean()` drops the Spring Cloud
Task Task execution and sequence tracking tables, thus Spring Cloud
Task lifecycle cannot write its state to the database.

- While it is possible to set up separate database and associated
datasource for Spring Cloud Task history, the Spring Cloud Task repo 
initializers only work in container with single default datasource.
If wanted to track in separate database will require separate process to
initialize.
The complexity detracts from the potential lab, so solution not included
here.



