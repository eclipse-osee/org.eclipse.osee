# Mission Software Requirements Signal Checking

## Signal Checking Description

This document describes a requirements-time signal checking feature for Mission Software programs. The feature automatically finds and expands signal expressions (from Interface Control Documents - ICDs) inside requirement documents, checks that every referenced signal is defined in the signal database, and produces clear reports that show where each signal is used and whether it is valid.

Mission Software programs use a compact grammar to reference signals so that entire ranges and groups can be described without listing each signal individually. For example, instead of typing ten separate signal names like `EMITTERS.E01_MODE` through `EMITTERS.E10_MODE`, an engineer can write a single expression such as `EMITTERS.E(01..10)_MODE` that represents all ten. This is powerful and keeps requirements readable, but it makes searching for a single signal difficult and manual reviews error‑prone.

When it is time to auto-generate code, test harnesses, or other downstream products, those compact expressions must be expanded into the full list of concrete signal names. The signal checking feature performs this expansion automatically, so the same requirements notation can efficiently serve both human authors and automated tooling.

By expanding these patterns into the full set of concrete signals and checking them against the signal database during the requirements phase, we can identify missing or inconsistent signals before design and implementation begin. Fixing issues at this early stage is significantly faster and less expensive than discovering them later in coding or test. This capability improves the quality of requirements, reduces rework in software development, and lowers overall program cost.

## Signal Checking Architecture 

**Architecture / Terminonolgy**

***Generic Operations / Objects***

- **SC (Signal Checker)** – A single end-to-end operation that processes one or more requirements artifacts to validate the referenced signals (ICD objects).
- **SCD (SignalCheckerData)** – Per-run data object created by an SC. It holds inputs, aggregate statistics, and the run-wide `XResultData rd` log.
- **ACD (ArtifactSignalData)** – Per-artifact data object contained within an SCD. Each ACD holds artifact identity, raw/text content, and its `SignalData` instances.
- **SD (SignalData)** – Per-signal data object contained within an ACD. Each SD represents a single signal expression, its expansions, and related validation details.

***Program Specific Contributions***

- **SigDb (Signal Database)** – Generic term for the signal database used by a program. Examples: an Oracle/Postgres SigDb instance, , or a file-based catalog.
- **SigDbApi (Signal Database API)** – Program-specific interface used to access SigDb. Each program provides its own implementation that knows how to talk to its particular SigDb (schema, services, connection details, etc.).

## Phases

The signal checker runs through a series of phases for each invocation. These phases are expressed in terms of SCD (the run), ACD (per-artifact data), and SD (per-signal data).

1. **SCD Initialization and Invocation**

   All entry points create and initialize an SCD with:
   - `branch` – branch on which to run the check.
   - `artifacts` – initial set of artifacts selected by the user.
   - `sigDbSystem` – string identifying which SigDb system/variant to use.
   - `recurse` – flag indicating whether to recurse child requirements.
   - `rd` – new `XResultData` used to log what is done and any warnings/errors.

   Log to `SCD.rd` basic run info (user, branch, sigDbSystem, recurse flag, artifact count).

   Then the SCD is sent to the server via the REST endpoint (`POST /ats/icd/chksig`), which delegates to `atsApi.getAtsIcdService().checkSignals(scd)`. Phases 2–6 execute server-side. The populated SCD is returned to the caller for reporting (phase 7).

   **1.1 IDE Client (NavigateItem / BLAM / XWidget)**
   - The user triggers the operation from a Navigate Item (e.g., "Check Signals - Demo Db"), a BLAM, or an XWidget on a Workflow Definition.
   - Client code constructs the SCD, then calls `atsApi.getServerEndpoints().getIcdEp().checkSignals(scd)`.
   - On return, opens `XResultDataUI` with `SCD.rd` to display the results.

   **1.2 REST / Browser / Postman / Curl**
   - The caller directly POSTs the SCD JSON to `/ats/icd/chksig`.
   - The server processes phases 2–6 and returns the populated SCD as JSON.
   - The caller (browser, Postman, curl) simply displays the returned JSON.

   **1.3 Web UI (future)**
   - A web page (TBD) will provide a form to select branch, artifacts, sigDbSystem, and recurse.
   - On submit, it constructs the SCD, calls the REST endpoint, and displays the results in a manner TBD (e.g., HTML table, formatted report view).

2. **Loading (SigDbApi)**
   - Call `SigDbApi` with SCD to determine the full set of artifacts that need to be checked.
   - SigDbApi uses `SCD.branch`, `SCD.artifacts`, `SCD.sigDbSystem`, and `SCD.recurse` to:
     - Resolve additional artifacts (e.g., children, related requirements) according to program-specific rules.
   - Update SCD:
     - Expand the artifact list and/or populate `SCD.artifacts` with initial ACD instances.
   - Log to `SCD.rd`, for example:
     - `Processing artifact 1/50 - <id> - <name> ...`
     - `Loading: added 37 child artifacts via recurse=true for branch <branchId>`.

3. **ConvertToRaw**
   - For each ACD in SCD:
     - If the artifact is Word-based:
       - Read Word XML via existing utilities (e.g., `WordTemplateContent`) and convert immediately to plain text.
     - If the artifact is Markdown-based:
       - Read the Markdown source as text.
     - Populate ACD fields:
       - `rawData` – original/raw representation (Word XML, Markdown source, etc.).
       - `textData` – normalized plain text used for signal finding.
   - Log to `SCD.rd` conversions and counts, e.g.:
     - `ConvertToRaw: converted artifact <id> (<name>) to text`.
   - Errors reading or converting a single artifact are logged and do not stop the run; that ACD is marked as failed (no `textData`) and skipped in later phases.

4. **FindSignals**
   - For each ACD with valid `textData`:
     - Scan the text for the common signal format (normally patterns like `[.*]<msg type>`; the exact language is pluggable per program).
     - For each match, create an SD with:
       - `rawSignal` – the literal signal token as it appears in the text.
       - `lineNumber` – 1-based line number where the signal was found.
       - `expandedSignals` – initially empty.
     - Add each SD to `ACD.signals`.
   - Update SCD aggregates (e.g., `totalSignalsFound`).
   - Log to `SCD.rd`, e.g.:
     - `FindSignals: '<rawSignal>' on line <lineNumber> in artifact <id> (<name>)`.
   - Parsing problems on individual lines are logged as warnings; the rest of the artifact continues to be processed.

5. **ExpandSignals**
   - For each SD in each ACD:
     - Determine whether the raw signal looks expandable (e.g., contains ranges or alternations) and set `sd.isExpandable` accordingly.
     - Expand the raw signal into concrete signal names according to the expansion language (to be fully defined later). Examples:
       - `EMITTERS.E(01..10)_MODE` →
         - `EMITTERS.E01_MODE`, `EMITTERS.E02_MODE`, ..., `EMITTERS.E10_MODE`.
       - `MODE.(P|C)_FMT` →
         - `MODE.P_FMT`, `MODE.C_FMT`.
     - Populate `sd.expandedSignals` with all expanded names.
   - Enforce allowed-character rules on final signal names:
     - Each expanded name must consist only of `[A-Za-z0-9_.]`.
     - If any expanded name contains other characters, mark SD as having invalid names (see SD fields), log a warning/error to `SCD.rd`, and skip SigDb checks for the invalid names.
   - Update SCD aggregates (e.g., `totalExpandedSignals`).
   - Log to `SCD.rd`, for example:
     - `ExpandSignals: '<rawSignal>' -> <N> expanded signals`.

6. **CheckSigDb**
   - For each SD (per ACD):
     - Decide which names to validate:
       - If `sd.expandedSignals` is non-empty:
         - Validate each expanded name via `SigDbApi` (or via a `SignalCatalog` that wraps `SigDbApi`).
       - If `sd.expandedSignals` is empty:
         - Validate `sd.rawSignal` itself.
     - Only names that pass the allowed-character rule (`[A-Za-z0-9_.]+`) are sent to SigDb. Others are treated as invalid and logged via `SCD.rd` without SigDb calls.
   - For each checked name, consult SigDb to determine if it is defined:
     - Set `sd.isDefinedInSigDb` based on the results.
     - Optionally record richer details in `sd.definition` (via `SignalDefinition`) if the SigDb implementation provides them.
   - Classify SDs:
     - `Found` vs `NotFound` based on SigDb results.
     - Use `sd.severity` and `sd.validationMessage` to capture summary information, e.g.,
       - `OK / "All 10 expanded signals found in SigDbSystem=<X>."`
       - `ERROR / "Missing 2/10 expanded signals: EMITTERS.E03_MODE, EMITTERS.E07_MODE."`
   - Update SCD aggregates such as `totalUndefinedSignals`, `totalWarnings`, and `totalErrors`.
   - Log to `SCD.rd` per SD, for example:
     - `CheckSigDb: '<rawSignal>' (line <lineNumber>) -> FOUND`
     - `CheckSigDb: '<rawSignal>' expanded to <N> signals; <foundCount> found, <missingCount> missing`.

7. **Reporting / Results Display**

   After phases 2–6 complete on the server, the populated SCD (with all ACDs, SDs, and `SCD.rd` log) is returned to the caller. How results are displayed depends on the entry point:

   **7.1 IDE Client (NavigateItem / BLAM / XWidget)**
   - Opens `XResultDataUI` with `SCD.rd` to show the chronological log of findings.
   - Optionally opens HTML tabs with artifact-level and signal-level summary views rendered from SCD/ACDs/SDs.
   - Can publish summary information back into ATS attributes via `AtsResultPublisher`.

   **7.2 REST / Browser / Postman / Curl**
   - The populated SCD is returned as JSON directly. The caller sees the full object graph including all ACDs, SDs, expanded signals, validation messages, and the `rd` log entries.

   **7.3 Web UI (future)**
   - Takes the returned SCD JSON and renders it in a web-based report view (TBD).
   - Likely includes an HTML summary (artifact-level and signal-level views) and optional trace views (requirements ↔ signals ↔ expanded signals).

## Error handling and logging

All phases are required to handle errors gracefully and continue processing other artifacts and signals whenever possible. A single failure (for one artifact or one SD) must not stop the overall run.

- **Central logging via SCD.rd**
  - `SignalCheckerData` (SCD) owns a single `XResultData rd` instance for the entire run.
  - Every phase logs its progress, warnings, and errors to this RD.
  - Typical log messages include:
    - `Processing artifact 1/50 - <id> - <name> ...`
    - `ConvertToRaw: converted Word artifact <id> (<name>) to text`
    - `FindSignals: <rawSignal> on line <lineNumber>`
    - `ExpandSignals: '<rawSignal>' -> <N> expanded signals`
    - `Warning: failed to parse line <lineNumber> in artifact <id> (<name>): <reason>`
    - `Error: SigDb lookup failed for signal <signalName> in system <sigDbSystem>: <reason>`

- **Graceful error handling per phase**
  - **UI / SCD initialization**:
    - Validate required inputs (branch, artifacts, sigDbSystem) and log errors via RD if missing.
    - If some inputs are invalid but at least one artifact remains usable, proceed with the usable subset.
  - **Loading (SigDbApi)**:
    - If resolving artifacts via `SigDbApi` fails for some items, log the issue and continue with the rest.
  - **ConvertToRaw**:
    - For each ACD, wrap Word/Markdown reading and text conversion in try/catch.
    - On failure, log an error to RD, mark that artifact as failed (e.g., `textData == null`), and skip subsequent phases for that ACD.
  - **FindSignals**:
    - For each ACD, scan `textData` line by line.
    - If an individual line cannot be parsed, log a warning and skip that line while continuing with the rest.
  - **ExpandSignals**:
    - For each SD, if expansion fails for a `rawSignal`, log a warning or error and either leave `expandedSignals` empty or partially filled, then continue with other SDs.
  - **SigDb lookup / validation (later phases)**:
    - If SigDb is unavailable or individual lookups fail, log errors and continue to classify what can be classified.

At the end of the run, SCD.rd provides a complete, chronological log of what was attempted, what succeeded, and what failed, suitable for display via `XResultDataUI` and for debugging.

## Data model

At the core of the architecture is a `SignalData` (SD) POJO that represents a single signal discovered in text. It starts life during FindSignals and is progressively enriched as the pipeline advances through the phases (expansion, SigDb lookup, validation).

On top of that, an `ArtifactSignalData` (ACD) POJO represents all signal-related information for a single artifact/document (name, id, raw/text data, and its `SignalData` instances). `SignalCheckerData` (SCD) then maintains a collection of `ArtifactSignalData` instances for the entire run.

At a higher level, a `SignalCheckerData` object represents a single run of the signal checker (a user invocation over one or more documents). `SignalCheckerData` owns the run-wide `XResultData` that reports what was done and logs warnings and errors.

Program-specific implementations:

  - Word-oriented logic that can reuse existing parsing utilities (e.g., `ReportWarnings.treatThisArtifactsWordFormattedData` and/or `SignalsChecker` regex logic).
  - Markdown-oriented logic that knows how to parse Markdown tables, code blocks, or inline tokens containing signals.
  - Other format-specific logic as needed (e.g., JSON specs, CSV catalogs).

### 3. Signal catalog / SigDb abstraction

```java
public interface SignalCatalog {
   boolean isSignalDefined(String signalName, String variantId);

   SignalDefinition getSignalDefinition(String signalName, String variantId);

   Set<String> getVariantIds();
}

/**
 * Program-specific facade over SigDb.
 *
 * Concrete implementations of this interface are responsible for talking to
 * the program's chosen SigDb (Oracle, Postgres, files, services, etc.) and
 * mapping its schema into the generic SignalCatalog view.
 */
public interface SigDbApi {
   boolean isSignalDefined(String signalName, String variantId);

   SignalDefinition readSignal(String signalName, String variantId);

   Set<String> listVariantIds();
}

public final class SignalDefinition {
   public final String name;
   public final Map<String, Object> attributes; // ranges, types, message membership, etc.
}
```

Program-specific catalog implementations:

- `SigDbBackedSignalCatalog`
  - Wraps a program-specific `SigDbApi` implementation.
  - Implements `isSignalDefined`, `getSignalDefinition`, and `getVariantIds` by delegating to `SigDbApi`.
- `PostgresSignalCatalog`
  - Uses JDBC/JPA to query a Postgres schema with equivalent signal semantics.
- `FileBasedSignalCatalog`
  - Reads from XML/JSON config for small or test environments.

### SigDb abstraction

```java
/**
 * Program-specific facade over SigDb.
 *
 * Concrete implementations of this interface are responsible for talking to
 * the program's chosen SigDb (Oracle, Postgres, files, services, etc.) and
 * mapping its schema into the generic SignalCatalog view.
 */
public interface SigDbApi {
   boolean isSignalDefined(String signalName, String variantId);

   SignalDefinition readSignal(String signalName, String variantId);

   Set<String> listVariantIds();
}

public final class SignalDefinition {
   public final String name;
   public final Map<String, Object> attributes; // ranges, types, message membership, etc.
}
```

## Deployment / Package Structure

For a detailed explanation of the IDE client-to-server communication pattern (endpoint interfaces, JAX-RS proxies, service layer, registration), see:

#[[file:docs/ai/ide/ide_client_server_communication_architecture.md]]

The signal checker is a **server-side operation**. Packages are split across bundles so that model classes are available to both IDE client and server code.

**Serialization constraint**: `SignalCheckerData` (SCD) and all enclosed classes (`ArtifactSignalData`, `SignalData`, `SignalDefinition`) must remain serializable (JSON via Jackson) so the object graph can be passed between client and server over REST.

### `org.eclipse.osee.ats.api` — Shared model and API interface (package `org.eclipse.osee.ats.api.reqts.icd`)

Contains everything that must be visible to both the IDE client and the server:

- **POJOs / data model** — `SignalCheckerData` (SCD), `ArtifactSignalData` (ACD), `SignalData` (SD), `SignalDefinition`
- **Interfaces** — `SigDbApi`, `SignalCatalog`, `AtsIcdEndpointApi` (JAX-RS interface)
- **Expansion grammar utilities** — signal expression parser/expander (stateless, no server dependencies)
- **Constants** — allowed-character rules, severity enums, validation message templates

This package has no dependency on server infrastructure (no ORCS, no REST impl, no JDBC). It depends only on `framework.jdk.core` and `framework.core.data` types (e.g., `XResultData`, `ArtifactToken`, `BranchToken`). It lives in the `ats.api` bundle so the endpoint interface can reference the POJOs directly in its method signatures.

#### `SigDbApi`

The primary interface for program-specific signal database access. Key method:

```java
public interface SigDbApi {
   /**
    * Run the full signal check pipeline against the provided SCD.
    * Returns the same SCD populated with results (ACDs, SDs, rd log).
    */
   SignalCheckerData checkSignals(SignalCheckerData scd);

   boolean isSignalDefined(String signalName, String variantId);

   SignalDefinition readSignal(String signalName, String variantId);

   Set<String> listVariantIds();
}
```

### `org.eclipse.osee.ats.rest` — Server operation (package `org.eclipse.osee.ats.rest.internal.reqts.icd`)

Contains the orchestrating operation that runs the 7-phase pipeline on the server:

- **AtsIcdServiceImpl** — implements `AtsIcdService`. Orchestrates all phases (loading, ConvertToRaw, FindSignals, ExpandSignals, CheckSigDb, reporting) and returns the populated SCD.
- **AtsIcdEndpointImpl** — JAX-RS resource implementation (see REST Endpoint below). Delegates to `atsApi.getAtsIcdService().checkSignals(scd)`.
- **Phase implementations** — loading via ORCS queries, ConvertToRaw (Word XML / Markdown reading), FindSignals, ExpandSignals, CheckSigDb delegation, report rendering.

This package imports `org.eclipse.osee.ats.api.reqts.icd` for the shared model and uses server-side services (ORCS, JDBC, template engine, etc.) for artifact loading and report publishing.

### `org.eclipse.osee.ats.ide.demo` — Demo/test implementation (package `org.eclipse.osee.ats.ide.demo.reqts.icd`)

The demo database is the test bed for this feature. It also serves as a **reference implementation** showing how other programs would implement their own `SigDbApi` to provide program-specific signal checking operations.

- **`SigDbImpl`** — implements `SigDbApi`. Provides a demo signal database backed by the demo OSEE database or in-memory test data. Other programs would create their own `SigDbApi` implementation following this same pattern, connecting to their particular SigDb (Oracle, Postgres, file-based, etc.).
- **"Check Signals - Demo Db" Navigate Item** — registered in `DemoNavigateViewItems`. Upon selection, creates an SCD populated with dummy data from the demo database, calls the endpoint through `atsApi.getAtsIcdService().checkSignals(scd)`, and displays the returned results via `XResultDataUI`.
- **"Check Signals Test" Navigate Item** — registered in `DemoNavigateViewItems`. Upon selection, creates a minimal/dummy SCD, calls `atsApi.getAtsIcdService().checkSignalsTest(scd)`. The server fills it with dummy artifacts, signals (with and without expansions), and logging as if CS ran, then returns the result. Client opens `XResultDataUI`. Used to validate the full round-trip before real data is wired.
- Used for integration testing and development of the signal checker pipeline without needing a real program SigDb.

## AtsIcdService

A service interface accessible via `atsApi.getAtsIcdService()`, following the same pattern as other ATS services (e.g., `IAtsReviewService`, `IAtsBranchService`).

### Interface — `AtsIcdService` (in `org.eclipse.osee.ats.api.reqts.icd`)

```java
public interface AtsIcdService {

   /**
    * Run the full signal check pipeline. On the client this delegates to the
    * REST endpoint; on the server it instantiates CS and runs the phases.
    */
   SignalCheckerData checkSignals(SignalCheckerData scd);

   /**
    * Test method that accepts a minimal/dummy SCD from the client, fills it with
    * dummy artifacts, signals (with and without expansions), and logging as if CS
    * was run, then returns the populated result. Used to validate the full
    * client→server→client round-trip before real data and expansion rules are wired.
    */
   SignalCheckerData checkSignalsTest(SignalCheckerData scd);
}
```

### Client-side implementation (in `org.eclipse.osee.ats.ide` or similar)

- `checkSignals(scd)` delegates to `atsApi.getServerEndpoints().getIcdEndpoint().checkSignals(scd)`.
- This makes the REST POST call to the server and returns the populated SCD.

### Server-side implementation — `AtsIcdServiceImpl` (in `org.eclipse.osee.ats.rest.internal.reqts.icd`)

- `checkSignals(scd)` instantiates the signal checker (CS) operation, runs it with the SCD (phases 2–6), and returns the populated result.
- `AtsIcdEndpointImpl.checkSignals(scd)` calls `atsApi.getAtsIcdService().checkSignals(scd)`.

### Call Flow (Demo Navigate Item → Server → Results)

```
DemoNavigateItem (IDE client)
  │
  ├── Constructs SCD with demo data (branch, artifacts, sigDbSystem)
  │
  ├── Calls: atsApi.getAtsIcdService().checkSignals(scd)
  │         └── Client impl delegates to:
  │             atsApi.getServerEndpoints().getIcdEndpoint().checkSignals(scd)
  │               └── POST /ats/icd/chksig  (SCD as JSON body)
  │
  └── Server receives SCD
        │
        ├── AtsIcdEndpointImpl.checkSignals(scd)
        │     └── atsApi.getAtsIcdService().checkSignals(scd)
        │           └── Server impl instantiates CS, runs phases 2–6
        │
        └── Returns populated SCD (JSON) to client
              │
              └── Client displays results via XResultDataUI on SCD.rd
```

## REST Endpoint

The signal checker exposes its operations via a JAX-RS endpoint following the standard ATS endpoint pattern (see `AtsApplication` for registration).

### API Interface — `AtsIcdEndpointApi`

- **Package**: `org.eclipse.osee.ats.api.reqts.icd`
- **Path**: `@Path("icd")`
- Defines the REST contract (HTTP methods, paths, media types, parameter bindings).
- Lives in the `ats.api` bundle alongside the POJOs so that method signatures can reference `SignalCheckerData` etc. directly.
- IDE client code creates a JAX-RS proxy via `jaxRsApi.newProxy("ats", AtsIcdEndpointApi.class)`.

#### Methods

| Method | HTTP | Sub-path | Description |
|--------|------|----------|-------------|
| `checkSignals(SignalCheckerData scd)` | POST | `chksig` | Runs the full signal check pipeline. Accepts an SCD with inputs, returns the SCD populated with results. |
| `checkSignalsTest(SignalCheckerData scd)` | POST | `chksigtest` | Test round-trip. Server fills dummy artifacts/signals/expansions/logging and returns populated SCD. |

```java
@Path("icd")
public interface AtsIcdEndpointApi {

   @Path("chksig")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   SignalCheckerData checkSignals(SignalCheckerData scd);

   @Path("chksigtest")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   SignalCheckerData checkSignalsTest(SignalCheckerData scd);
}
```

### Implementation — `AtsIcdEndpointImpl`

- **Package**: `org.eclipse.osee.ats.rest.internal.reqts.icd`
- Implements `AtsIcdEndpointApi`.
- Constructed with `AtsApi` — following the same pattern as other endpoints (e.g., `AtsReportEndpointImpl`).
- Delegates to `atsApi.getAtsIcdService().checkSignals(scd)` for the actual work.

### Registration

In `AtsApplication.start()`:
```java
singletons.add(new AtsIcdEndpointImpl(atsApiServer));
```

### Client Access

In `IAtsServerEndpointProvider` and `AtsServerEndpointProviderImpl`:
```java
AtsIcdEndpointApi getIcdEp();
```
The IDE client obtains the proxy via:
```java
icdEp = jaxRsApi.newProxy("ats", AtsIcdEndpointApi.class);
```
