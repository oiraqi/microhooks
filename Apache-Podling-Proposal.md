## Abstract
Microhooks will be a framework that streamlines the development of microservices

Proposal
Microhooks aims at allowing microservice developers to focus on their business logic while declaratively expressing low-level, recurring needs, such as inter-service communication, brokering, event management, data replication, and distributed transaction management. Based on the inversion of control, the materialized view pattern, and the two-phase commit protocol among others, our framework automatically generates and injects the corresponding artifacts. It leverages 100% build time code introspection and instrumentation, as well as context building, for optimized runtime performance. From the user perspective, Microhooks exposes an intuitive, container-agnostic, broker-neutral, and ORM framework-independent API.

## Background
Microservices is an architectural style and model for the development of loosely coupled, isolated, but collaborating services constituting a single application. Therefore, these microservices are highly maintainable, easily testable, and independently deployable. This architectural pattern aims at addressing the problems encountered in traditional tiered and monolithic architectures. Such problems can be related to the following non-functional requirement areas: scalability, maintainability, and security. There are several microservices patterns. But, we focus here on three patterns that are the most relevant to our project:
- The Database-per-Service Pattern: This pattern stems from the well-known domain-driven design (DDD) whereby a large business domain is broken down into several more cohesive and smaller (sub)domains, each with well-defined boundaries. Then, objects or entities within each domain are identified and modeled. Following DDD, a traditional large data model would rather become a set of smaller data models, each representing a specific domain. DDD can be leveraged to segregate microservices so that each bounded context corresponds to a microservice describing a distinct problem area. In fact, DDD is the first step to identifying and designing microservices. Each microservice represents an abstraction over its own data model, hence its own database. Each microservice should be able to independently store and retrieve information from its own data store. This is the database-per-service pattern.
- 
## Rationale
The microservices architectural model has gained widespread adoption in recent years thanks to its ability to deliver high scalability and maintainability. However, the development process of microservices-based applications can be complex and challenging. Indeed, it often requires developers to manage a large number of distributed components with the burden of handling low-level, recurring needs, inherent to distributed systems. The associated costs may outweigh the benefits expected from the microservice architecture, and consequently discourage from its adoption. Microhooks strives to alleviate such costs by making the so-called low-level details completely transaprent to developers.

## Current Status
So far, Microhooks initial functional and non-functional requirements have been specified, and the corresponding high-level design, detailed design, and implementation have been performed. Microhooks evaluation against state-of-the-art practices has demonstrated its effectiveness in drastically reducing code size and complexity, without incurring any considerable cost on performance.

## Meritocracy
Microhooks was originally created by Omar Iraqi in 2022. We highly welcome other developers around the world to write and be part of Microhooks success story with us. We are and remain committed to elect any developer who has made enough good contributions as a committer. Moreover, we are committed to elect active contributors to the PMC.

## Git Repositories
https://github.com/oiraqi/microhooks