---
summary: "Architecture for IDE client to server REST communication via endpoint interfaces, proxies, and service implementations"
tags: [ide, rest, client-server, architecture, endpoints]
fileMatch: "**/IAtsServerEndpointProvider*,**/AtsServerEndpointProviderImpl*,**/AtsApplication*,**/*EndpointApi*,**/*EndpointImpl*"
---

# IDE Client–Server Communication Architecture

## Overview

The OSEE IDE client communicates with the OSEE server exclusively through JAX-RS REST endpoints. This architecture allows the IDE (Eclipse RCP) to invoke server-side operations as if calling local Java methods, with serialization/deserialization handled transparently by the JAX-RS proxy layer.

The pattern is consistent across all ATS features: a shared interface defines the contract, the server implements it, and the client obtains a proxy that converts method calls into HTTP requests.

## Key Components

```
┌─────────────────────────────────────────────────────────────────────────┐
│  ats.api (shared bundle)                                                │
│                                                                         │
│  ┌───────────────────────┐    ┌──────────────────────────────────────┐  │
│  │  Endpoint Interface   │    │  POJOs / Data Transfer Objects       │  │
│  │  (e.g. AtsReport-     │    │  (request/response objects that are  │  │
│  │   EndpointApi)        │    │   serialized as JSON over the wire)  │  │
│  │                       │    │                                      │  │
│  │  @Path("report")      │    │  Must remain Jackson-serializable    │  │
│  │  interface with        │    │  (no server deps, no IDE deps)      │  │
│  │  JAX-RS annotations   │    │                                      │  │
│  └───────────────────────┘    └──────────────────────────────────────┘  │
│                                                                         │
│  ┌───────────────────────┐                                              │
│  │ IAtsServerEndpoint-   │                                              │
│  │ Provider              │  ← declares accessor for each endpoint       │
│  └───────────────────────┘                                              │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│  ats.ide (IDE client bundle)                                            │
│                                                                         │
│  ┌───────────────────────┐                                              │
│  │ AtsServerEndpoint-    │                                              │
│  │ ProviderImpl          │  ← creates JAX-RS proxies per endpoint       │
│  │                       │     jaxRsApi.newProxy("ats", XxxApi.class)   │
│  └───────────────────────┘                                              │
│                                                                         │
│  Client code calls:                                                     │
│    atsApi.getServerEndpoints().getXxxEp().someMethod(data)              │
│                                                                         │
│  The proxy serializes `data` to JSON, sends HTTP request, deserializes  │
│  the response back into a Java object.                                  │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│  ats.rest (server bundle)                                               │
│                                                                         │
│  ┌───────────────────────┐    ┌──────────────────────────────────────┐  │
│  │ XxxEndpointImpl       │    │ AtsApplication                       │  │
│  │                       │    │                                      │  │
│  │ implements XxxApi     │    │ Registers all endpoint impls as      │  │
│  │ Has server-side deps  │    │ JAX-RS singletons in start()         │  │
│  │ (ORCS, JDBC, etc.)   │    │                                      │  │
│  └───────────────────────┘    └──────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────┘
```

## Communication Flow

```
IDE Client                          Network                    Server
─────────                          ───────                    ──────

User action (e.g. NavigateItem)
       │
       ▼
atsApi.getServerEndpoints()
       .getReportEp()              ← lazy-init JAX-RS proxy
       .getBuildMemo(request)
       │
       ▼
JAX-RS Proxy layer
  • Serializes `request` → JSON
  • Builds HTTP request:
    POST /ats/report/BuildMemo
    Content-Type: application/json
    Body: { ... }
       │
       ▼                                                   JAX-RS Runtime
                              HTTP ──────────────────►     deserializes JSON
                                                          into `request` POJO
                                                                │
                                                                ▼
                                                     AtsReportEndpointImpl
                                                       .getBuildMemo(request)
                                                                │
                                                                ▼
                                                     Server-side logic runs
                                                     (ORCS queries, business
                                                      logic, etc.)
                                                                │
                                                                ▼
                                                     Returns result object
                                                                │
                              HTTP ◄──────────────────   serialized as JSON
       │
       ▼
JAX-RS Proxy layer
  • Deserializes JSON → result POJO
       │
       ▼
Client code receives result
  • Opens XResultDataUI, or
  • Populates a view, or
  • Returns to caller
```

## Step-by-Step: Adding a New Endpoint

### 1. Define the interface in `ats.api`

Create a JAX-RS-annotated interface in the appropriate `ats.api` sub-package:

```java
package org.eclipse.osee.ats.api.reqts.icd;

@Path("icd")
public interface AtsIcdEndpointApi {

   @Path("chksig")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   SignalCheckerData checkSignals(SignalCheckerData scd);
}
```

Rules:
- The interface and all types in its method signatures must live in `ats.api` (shared bundle).
- All parameter/return types must be Jackson-serializable (no circular references, no server-only types).

### 2. Define POJOs in `ats.api`

Request/response objects are plain Java classes with public fields or getters/setters:

```java
package org.eclipse.osee.ats.api.reqts.icd;

public class SignalCheckerData {
   private BranchToken branch;
   private List<ArtifactSignalData> artifacts;
   private XResultData rd;
   // getters/setters...
}
```

### 3. Implement on the server in `ats.rest`

```java
package org.eclipse.osee.ats.rest.internal.reqts.icd;

public class AtsIcdEndpointImpl implements AtsIcdEndpointApi {

   private final AtsApi atsApi;

   public AtsIcdEndpointImpl(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public SignalCheckerData checkSignals(SignalCheckerData scd) {
      return atsApi.getAtsIcdService().checkSignals(scd);
   }
}
```

### 4. Register in `AtsApplication`

In `AtsApplication.start()`:

```java
singletons.add(new AtsIcdEndpointImpl(atsApiServer));
```

### 5. Add to `IAtsServerEndpointProvider`

```java
AtsIcdEndpointApi getIcdEp();
```

### 6. Implement in `AtsServerEndpointProviderImpl`

```java
private AtsIcdEndpointApi icdEp;

@Override
public AtsIcdEndpointApi getIcdEp() {
   if (icdEp == null) {
      icdEp = jaxRsApi.newProxy("ats", AtsIcdEndpointApi.class);
   }
   return icdEp;
}
```

### 7. Call from client code

```java
SignalCheckerData scd = new SignalCheckerData();
scd.setBranch(demoBranch);
// ... populate inputs ...

SignalCheckerData result = atsApi.getServerEndpoints().getIcdEp().checkSignals(scd);
XResultDataUI.report(result.getRd(), "Check Signals Results");
```

## Design Principles

| Principle | Rationale |
|-----------|-----------|
| Interface in shared bundle | Both client (for proxy) and server (for impl) need the same type |
| POJOs in shared bundle | Serialized/deserialized on both sides of the wire |
| No server types in interface signatures | Client cannot depend on ORCS, JDBC, etc. |
| Lazy proxy initialization | Proxies are created on first use, not at startup |
| Endpoint impl delegates to service | Keeps REST layer thin; business logic lives in service classes |
| Singleton registration | One instance per endpoint, shared across all requests |

## Service Layer Pattern

For non-trivial operations, the endpoint impl delegates to a service interface that also lives in `ats.api`. The service has two implementations — one on the client and one on the server — giving IDE code a single transparent call point.

### Full Call Chain (IDE Client → Server → Back)

```
┌─────────────────────────────────────────────────────────────────────────────┐
│  IDE CLIENT                                                                 │
│                                                                             │
│  NavigateItem / BLAM / XWidget                                              │
│       │                                                                     │
│       ▼                                                                     │
│  atsApi.getAtsIcdService().checkSignals(scd)                                │
│       │                                                                     │
│       ▼                                                                     │
│  Client-side AtsIcdService impl                                             │
│       │  (delegates to REST endpoint)                                       │
│       ▼                                                                     │
│  atsApi.getServerEndpoints().getIcdEp().checkSignals(scd)                   │
│       │                                                                     │
│       ▼                                                                     │
│  JAX-RS Proxy  ──── serializes SCD to JSON ────►  HTTP POST /ats/icd/chksig│
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  SERVER                                                                     │
│                                                                             │
│  AtsIcdEndpointImpl.checkSignals(scd)     ← JAX-RS deserializes JSON       │
│       │                                                                     │
│       ▼                                                                     │
│  atsApi.getAtsIcdService().checkSignals(scd)                                │
│       │                                                                     │
│       ▼                                                                     │
│  Server-side AtsIcdServiceImpl                                              │
│       │  (instantiates CS operation, runs phases 2–6)                       │
│       ▼                                                                     │
│  Returns populated SCD (with ACDs, SDs, rd log)                             │
│       │                                                                     │
│       ▼                                                                     │
│  JAX-RS serializes SCD to JSON  ────►  HTTP 200 Response                    │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  IDE CLIENT (continued)                                                     │
│                                                                             │
│  JAX-RS Proxy  ◄──── deserializes JSON back to SCD POJO                    │
│       │                                                                     │
│       ▼                                                                     │
│  Client code receives populated SCD                                         │
│       │                                                                     │
│       ▼                                                                     │
│  Display results:                                                           │
│    • XResultDataUI.report(scd.getRd(), "title")                             │
│    • Open HTML report tab                                                   │
│    • Populate a view/editor                                                 │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Key Insight

The **same interface** (`AtsIcdService`) has **two implementations**:

| Side | Implementation | What it does |
|------|----------------|--------------|
| Client (ats.ide) | `AtsIcdServiceClientImpl` | Calls `atsApi.getServerEndpoints().getIcdEp().checkSignals(scd)` — makes REST call |
| Server (ats.rest) | `AtsIcdServiceImpl` | Instantiates the operation, runs business logic, returns populated result |

This means any IDE code just calls `atsApi.getAtsIcdService().checkSignals(scd)` without knowing or caring about the REST layer. The result flows all the way back to the caller for display or further processing.

## URL Structure

All ATS endpoints share the base path `/ats` (from `@ApplicationPath("ats")` on `AtsApplication`). Each endpoint interface adds its own sub-path:

```
/ats/report/BuildMemo          → AtsReportEndpointApi
/ats/action/...                → AtsActionEndpointApi
/ats/icd/chksig                → AtsIcdEndpointApi
/ats/icd/chksigtest            → AtsIcdEndpointApi
```

## Error Handling

- Server-side exceptions result in HTTP error responses (500, 400, etc.).
- The JAX-RS proxy on the client translates these into Java exceptions.
- For operations that can partially succeed, use `XResultData` in the response POJO to carry warnings/errors alongside valid results (the HTTP response is still 200).

## Key Files

| File | Bundle | Role |
|------|--------|------|
| `IAtsServerEndpointProvider.java` | ats.api | Declares all endpoint accessors |
| `AtsServerEndpointProviderImpl.java` | ats.ide | Creates JAX-RS proxies for each endpoint |
| `AtsApplication.java` | ats.rest | Registers all endpoint impls as JAX-RS singletons |
| `*EndpointApi.java` | ats.api | JAX-RS interface (contract) |
| `*EndpointImpl.java` | ats.rest | Server-side implementation |
