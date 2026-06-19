---
summary: "Generic Report: template-driven REST endpoint, AST-parsed Java code, hierarchical Excel XML output"
tags: [orcs, report, rest, excel, ast, template]
fileMatch: "**/GenericReport*,**/ReportEndpoint*,**/PublishTemplateReport*,**/TemplateParser*,**/GenericReportBuilder*,**/ASTParserUtil*"
---

# Generic Report

## Overview

The Generic Report system generates hierarchical Excel XML spreadsheets from OSEE artifact data. Reports are defined as Java code stored in a `ReportTemplate` artifact's `JavaCode` attribute, parsed at runtime via the Eclipse JDT AST parser, and executed against artifact queries. It is exposed via the REST endpoint at `orcs/report/{branch}/view/{view}/template/{template}`.

## Key Files

- `plugins/org.eclipse.osee.orcs.rest.model/src/org/eclipse/osee/orcs/rest/model/GenericReport.java` — Fluent builder API interface
- `plugins/org.eclipse.osee.orcs.rest.model/src/org/eclipse/osee/orcs/rest/model/ReportEndpoint.java` — REST endpoint interface
- `plugins/org.eclipse.osee.orcs.rest/src/org/eclipse/osee/orcs/rest/internal/ReportEndpointImpl.java` — REST endpoint implementation (sync + async)
- `plugins/org.eclipse.osee.orcs.rest/src/org/eclipse/osee/orcs/rest/internal/writers/PublishTemplateReport.java` — StreamingOutput orchestrator
- `plugins/org.eclipse.osee.orcs.rest/src/org/eclipse/osee/orcs/rest/internal/writers/TemplateParser.java` — AST parsing and reflective method invocation
- `plugins/org.eclipse.osee.orcs.rest/src/org/eclipse/osee/orcs/rest/internal/writers/GenericReportBuilder.java` — GenericReport implementation with query execution and data row generation
- `plugins/org.eclipse.osee.orcs.rest/src/org/eclipse/osee/orcs/rest/internal/writers/GenericReportCode.java` — Example report definitions
- `plugins/org.eclipse.osee.orcs.rest/src/org/eclipse/osee/orcs/rest/internal/writers/reflection/ASTParserUtil.java` — JDT AST parser configuration (classpath + sourcepath)
- `plugins/org.eclipse.osee.orcs.rest/src/org/eclipse/osee/orcs/rest/internal/writers/reflection/TemplateReflector.java` — Reflective argument resolution and method invocation
- `plugins/org.eclipse.osee.orcs.rest/src/org/eclipse/osee/orcs/rest/internal/writers/reflection/TemplateVisitor.java` — AST visitor extracting method invocations
- `plugins/org.eclipse.osee.orcs/src/org/eclipse/osee/orcs/search/ReportLevel.java` — Level data model (name, depth, columns, relation)
- `plugins/org.eclipse.osee.orcs/src/org/eclipse/osee/orcs/search/ReportColumn.java` — Abstract column base class
- `plugins/org.eclipse.osee.orcs/src/org/eclipse/osee/orcs/search/AttributeReportColumn.java` — Attribute value column
- `plugins/org.eclipse.osee.orcs/src/org/eclipse/osee/orcs/search/ArtifactIdReportColumn.java` — Artifact ID column
- `plugins/org.eclipse.osee.orcs/src/org/eclipse/osee/orcs/search/ArtifactTypeReportColumn.java` — Artifact type name column
- `plugins/org.eclipse.osee.orcs/src/org/eclipse/osee/orcs/search/ReportFilter.java` — Regex-based row filter
- `plugins/org.eclipse.osee.orcs.rest.test/src/org/eclipse/osee/orcs/rest/internal/GenericReportBuilderTest.java` — Unit test
- `plugins/org.eclipse.osee.ats.ide.integration.tests/src/org/eclipse/osee/ats/ide/integration/tests/orcs/rest/ReportEndpointTest.java` — Integration test

## Architecture

### Data Flow

1. REST endpoint receives branch, view, and template artifact IDs.
2. `PublishTemplateReport` (StreamingOutput) is created with a `GenericReportBuilder` and `XResultData`.
3. `TemplateParser` loads the template artifact from COMMON branch and reads its `JavaCode` attribute.
4. The Java code is parsed via `ASTParserUtil` (Eclipse JDT `ASTParser.JLS20`) with configured classpath and sourcepath entries.
5. `TemplateVisitor` (ASTVisitor) extracts method invocations and import declarations.
6. `TemplateReflector` resolves arguments from AST expressions (literals, qualified names, nested method calls) and invokes the corresponding `GenericReport` builder methods via reflection.
7. `GenericReportBuilder` accumulates `ReportLevel` objects, each with columns and an associated relation (main + optional fork relations stored on the level).
8. `getDataRowsFromQuery()` executes the built query, recursively traverses levels via all level relations (main + forks), fills data rows.
9. `ExcelXmlWriter` outputs the data as Excel XML (SpreadsheetML format).
10. A `DebugInfo` sheet is appended with parsing logs from `XResultData`.

### AST Parser Configuration

`ASTParserUtil.setEnvironment()` accepts two separate path lists:

- **Classpath entries** (first argument): `.jar` files or directories with compiled `.class` files. The base paths (GenericReport, GenericReportBuilder, QueryBuilder, ResultSet, ArtifactId) are resolved at runtime from `Class.getProtectionDomain().getCodeSource().getLocation()`.
- **Sourcepath entries** (second argument): directories containing `.java` source files. Used when the template imports types from source trees rather than compiled bundles.

The `TemplateParser.setPathsForParser()` method auto-classifies paths from the template artifact's `Annotation` attribute:

- Paths prefixed with `relative/` are resolved from the class's protection domain → classpath.
- Paths that are directories without a `META-INF` folder → sourcepath.
- Everything else (jars, compiled output directories) → classpath.

### Important Patterns

- **Do NOT pass source directories as classpath entries.** The AST parser cannot resolve imports from `.java` files via classpath. Source directories must go in the sourcepath argument of `setEnvironment()`.
- **The `GenericReport` builder uses a shared mutable `QueryBuilder` reference.** Each `level()` or `relationLevel()` call chains onto or replaces the query. This is intentional for the builder pattern — `report.query().follow(...)` mutates the shared query state. `relationLevel` additionally stores the main relation on the `ReportLevel` eagerly; query-based levels defer relation resolution to data retrieval time.
- **Reflection whitelisting is required.** Only classes registered via `reflector.setAllowedReflectionClass()` can be resolved from qualified names in the template code. By default: `CoreArtifactTypes`, `CoreAttributeTypes`, `CoreRelationTypes`. Additional classes are loaded from template imports that contain "AttributeType", "ArtifactType", or "RelationType" in the name.
- **Do NOT add per-artifact queries in the data row loop.** `GenericReportBuilder.getArtsForLevel()` fetches related artifacts via `art.getRelated(relation)` for all relations in `level.getAllRelations()` (main + fork relations). The initial query (`query.asArtifacts()`) is the only database call during data generation.
- **`ReportLevel` owns its relation state.** `relationLevel` sets the main relation eagerly on the level via `setRelation()`. `followFork` adds fork relations via `addForkRelation()`. For query-based levels (created by `level(name, query)` with a follow in the query), the main relation is lazily resolved from `query.getRelationTypesForLevel()` on first data access. `getAllRelations()` returns main + fork relations for use in `getArtsForLevel()`.
- **`relationLevel` resolves relation types by name** via `orcsApi.tokenService().getRelationType(name)`. The side is parsed from the string `"SIDE_A"` or `"SIDE_B"` using `RelationSide.valueOf()`.
- **Filters are applied during row generation.** If a column's filter regex matches its data value, the entire row and all child level rows are skipped (early return in `fillReportDataFromQuery`).
- **`followFork` is level-scoped and position-sensitive.** It can only be called on a level created by `relationLevel`, and only before any columns are added to that level. The builder checks `currentLevel.isRelationLevel()` (set by `setRelation` in `relationLevel`) and `currentLevel.getColumns().isEmpty()`. Duplicate relation names within the same level are also rejected via `currentLevel.hasForkRelationName()`. This works correctly with the AST parser's LIFO invoke stack because columns from the previous level are added to the previous `currentLevel`, and `relationLevel` creates a fresh level with zero columns before `followFork` executes.
- **In the TemplateParser**, `followFork` is dispatched alongside `level` and `relationLevel` — it pushes onto the method stack and invokes immediately since it modifies query state.

### Async Report Generation

The async endpoint (`/async/{email}`) spawns a background `Thread` that:

1. Writes the Excel XML to the server's publish directory (`OseeClient.OSEE_APPLICATION_SERVER_DATA + "/publish"`).
2. Sends an email to the recipient with a download link pointing to `/orcs/resources/publish?path={fileName}`.
3. Returns immediately with a JSON response containing status, file name, and download link.

Error handling: exceptions during setup (invalid email, missing data path) return a 500 response with a JSON error message. Exceptions during async generation are logged via `OseeCoreException` on the background thread.

### Template Artifact Setup

A `ReportTemplate` artifact on COMMON branch requires:

- **JavaCode** attribute: complete Java class with package, imports, and a method accepting `GenericReport`.
- **Annotation** attributes (optional): additional classpath/sourcepath entries for the AST parser.

The template class name is extracted via regex (`\sclass\s+(\w+)`) and used as the AST parser unit name.

### GenericReport Builder API

| Method                                    | Description                                                                  |
| ----------------------------------------- | ---------------------------------------------------------------------------- |
| `level(name, QueryBuilder)`               | Add level with explicit query                                                |
| `level(name, typeName)`                   | Add level by artifact type name                                              |
| `relationLevel(name, relationName, side)` | Add level traversing a named relation                                        |
| `followFork(relationName, side)`          | Fork an additional relation at the current level (must follow relationLevel) |
| `column(name)`                            | Add artifact ID column                                                       |
| `column(name, AttributeTypeToken)`        | Add attribute column                                                         |
| `column(AttributeTypeToken)`              | Add attribute column (type name as header)                                   |
| `column(name, typeName)`                  | Add attribute column by type name                                            |
| `type(name)`                              | Add artifact type column                                                     |
| `filter(AttributeTypeToken, regex)`       | Filter rows matching regex                                                   |
| `query()`                                 | Access the mutable QueryBuilder                                              |

### Output Structure

The Excel XML workbook contains:

1. **Report sheet** (named after template artifact): top row with level names, header row with column names, then data rows.
2. **DebugInfo sheet**: one column with all `XResultData` log entries.

Data rows are generated recursively. For each artifact at level N, related artifacts at level N+1 are fetched, and parent columns are repeated for each child combination.

## Testing

- **Unit test** (`GenericReportBuilderTest`): uses Mockito to verify level/column/query construction, header generation, data row filling with mocked artifacts and relations, and `followFork` validation (valid chaining, rejection after column, duplicate relation detection via `ReportLevel.hasForkRelationName`, rejection after query-based level via `ReportLevel.isRelationLevel`).
- **Integration test** (`ReportEndpointTest`): creates ReportTemplate artifacts with `relationLevel` and `followFork` code, sets up test artifacts with Code-Requirement relations on the SAW working branch, calls the endpoint, and verifies Excel XML content contains expected data. Also tests the async endpoint returns proper JSON, and that a `followFork` template parses and executes without errors.

Run via the ATS IDE integration test suite. Tests validate response status, XML worksheet presence, test data in cells, level/column headers, and async JSON fields.
