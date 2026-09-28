# Getting Started

### Reference Documentation

For further reference, please consider the following sections:

### Guides

The following guides illustrate how to use some features concretely:

### Docker Compose support

This project contains a Docker Compose file named `compose.yaml`.
In this file, the following services have been defined:

```java
//EXAMPLE postgres: [`postgres:latest`](https://hub.docker.com/_/postgres)
```

Please review the tags of the used images and set them to the same as you're running in production.

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.

## Comments Glossary

```java
/* ?
 * <<COMPONENT>>
 *
 * NOTE:
 *   What is this?
 *
 * INFO:
 *   Relevant contextual information.
 *
 * RESPONSIBILITY:
 *   What does it own?
 *
 * BOUNDARY:
 *   Where does communication cross?
 *
 * CONTRACT:
 *   What does it promise?
 *
 * DEPENDENCY:
 *   What does it depend on?
 *
 * DECISION:
 *   What was intentionally chosen?
 *
 * WHY:
 *   Why was it chosen?
 *
 * LEARNING:
 *   What am I learning from this?
 *
 * TODO:
 *   What remains to be done?
 */
```

### Example

```java
/* ?
 * <<REPOSITORY>>
 *
 * NOTE:
 *   Defines the persistence boundary for User.
 *
 * INFO:
 *   Data access and persistence.
 *
 * RESPONSIBILITY:
 *   Provide access to User data without exposing persistence
 *   details to the business layer.
 *
 * BOUNDARY:
 *   Service → Repository → Database.
 *
 * CONTRACT:
 *   The Service communicates with persistence through this interface.
 *
 * DEPENDENCY:
 *   Spring Data MongoDB provides the implementation.
 *
 * LEARNING:
 *   The interface represents the communication contract;
 *   the Repository represents the responsibility boundary.
 *
 * TODO:
 *   Add custom repository queries when required.
 */
```
