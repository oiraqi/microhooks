## Abstract
Microhooks will be a framework that streamlines the development of microservices

Proposal
Microhooks aims at allowing microservice developers to focus on their business logic while declaratively expressing low-level, recurring needs, such as inter-service communication, brokering, event management, data replication, and distributed transaction management. Based on the inversion of control, the materialized view pattern, and the two-phase commit protocol among others, our framework automatically generates and injects the corresponding artifacts. It leverages 100% build time code introspection and instrumentation, as well as context building, for optimized runtime performance. From the user perspective, Microhooks exposes an intuitive, container-agnostic, broker-neutral, and ORM framework-independent API.

Background

Rationale
The microservices architectural model has gained widespread adoption in recent years thanks to its ability to deliver high scalability and maintainability. However, the development process of microservices-based applications can be complex and challenging. Indeed, it often requires developers to manage a large number of distributed components with the burden of handling low-level, recurring needs, inherent to distributed systems. The associated costs may outweigh the benefits expected from the microservice architecture, and consequently discourage from its adoption. Microhooks strives to alleviate such costs by making the so-called low-level details completely transaprent to developers.

Current Status
So far, Microhooks initial functional and non-functional requirements have been specified, and the corresponding high-level design, detailed design, and implementation have been performed. Microhooks evaluation against state-of-the-art practices has demonstrated its effectiveness in drastically reducing code size
and complexity, without incurring any considerable cost on performance.

Git Repositories
https://github.com/oiraqi/microhooks