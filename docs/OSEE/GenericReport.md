# OSEE Generic Report

The Generic Report system provides a configurable, template-driven mechanism for generating Excel XML reports from OSEE artifact data. Reports are defined as Java code stored in a ReportTemplate artifact, parsed at runtime via the Eclipse JDT AST parser, and executed against the OSEE data model to produce hierarchical, multi-level spreadsheets.

## Setting Up a Report Template Artifact

A Generic Report is driven by a **ReportTemplate** artifact stored on the COMMON branch. To create one:

1. Create a new artifact of type `ReportTemplate` on the COMMON branch (typically under the Default Hierarchy Root).
2. Set the **JavaCode** attribute to a Java class that defines the report structure (see below).
3. Optionally, set **Annotation** attributes to provide additional classpath or source path entries the AST parser needs to resolve imports in your code.

### JavaCode Attribute

The JavaCode attribute contains a complete Java class with a method that accepts a `GenericReport` parameter and uses the fluent builder API to define levels and columns. The class must include proper package and import declarations so the AST parser can resolve types.

Example:

```java
package org.eclipse.osee.orcs.rest.internal.writers;

import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.orcs.rest.model.GenericReport;

public class GenericReportCode {
   public void reportCode(GenericReport report) {
      report.level("Subsystem Requirements",
         report.query().andIsOfType(CoreArtifactTypes.SubsystemRequirementMsWord))
         .column("Artifact Id")
         .column("Requirement Name", CoreAttributeTypes.Name);

      report.relationLevel("Related Code Units", "Code-Requirement", "SIDE_A")
         .column("Artifact Id")
         .column("Code Unit", CoreAttributeTypes.Name)
         .column("File System Path", CoreAttributeTypes.FileSystemPath);
   }
}
```

### Annotation Attribute (Optional Paths)

If your template code imports types beyond the standard OSEE core types, you may need to add paths so the AST parser can resolve them. Add **Annotation** attribute values with:

- **Jar or compiled class paths** — absolute paths to `.jar` files or directories containing `.class` files (used as classpath entries).
- **Source directories** — absolute paths to directories containing `.java` source files (used as sourcepath entries). These are detected automatically when the path is a directory without a `META-INF` folder.
- **Relative paths** — prefixed with `relative/` followed by a fully qualified class name. The system resolves the path from the class's protection domain at runtime (e.g., `relative/org.eclipse.osee.orcs.rest.model.GenericReport`).

## Report Levels

Levels define the hierarchical structure of the report. Each level represents a tier of artifacts that will be queried and displayed. Levels are added sequentially, and each subsequent level is treated as a child of the previous one.

### `level(String levelName, QueryBuilder query)`

Defines a level using an explicit query. The query determines which artifacts appear at this level.

```java
report.level("System Functions",
   report.query().andIsOfType(CoreArtifactTypes.SystemFunctionMsWord))
```

### `level(String levelName, String typeName)`

Defines a level by artifact type name (resolved via the token service).

```java
report.level("Folders", "Folder")
```

### `relationLevel(String levelName, String relationName, String relationSide)`

Defines a level that traverses a named relation from the previous level's artifacts. The relation is resolved by name via the token service, and the side is specified as `"SIDE_A"` or `"SIDE_B"`.

```java
report.relationLevel("Related Code Units", "Code-Requirement", "SIDE_A")
```

### `followFork(String relationName, String relationSide)`

Adds a fork to the query at the current relation level, eagerly loading artifacts from an additional relation type. This is useful when you need the query to also traverse a second (or third) relation from the same starting point as the `relationLevel`.

**Constraints:**

- Can only be used on a level that was created by `relationLevel`. Calling it on a query-based `level` throws an error.
- Must be called before any columns are added to the level. Place all `followFork` calls immediately after `relationLevel`, before `column`, `type`, or `filter`.
- Each `followFork` in a level must specify a different relation than the `relationLevel` and any previous `followFork` calls. Duplicate relations are rejected.

```java
report.relationLevel("Elements", "Code-Requirement", "SIDE_A")
   .followFork("Supporting Info", "SIDE_B")
   .followFork("Requirement Trace", "SIDE_B")
   .column("Artifact Id")
   .column("Name", CoreAttributeTypes.Name);
```

### Level Depth

Levels are automatically assigned increasing depth values. The first level is depth 0, the second is depth 1, and so on. During report generation, the system recursively traverses from parent levels to child levels using the configured relations or query follow operations.

## Report Columns

Columns define what data is extracted from each artifact at a given level. Columns are added to the current (most recently defined) level.

### `column(String columnName)`

Adds an **Artifact ID** column — displays the artifact's numeric ID.

```java
.column("Artifact Id")
```

### `column(String columnName, AttributeTypeToken type)`

Adds an **Attribute** column — displays the value of the specified attribute type.

```java
.column("Requirement Name", CoreAttributeTypes.Name)
```

### `column(AttributeTypeToken type)`

Adds an attribute column using the attribute type's name as the column header.

```java
.column(CoreAttributeTypes.FDAL)
```

### `column(String columnName, String typeName)`

Adds an attribute column where the type is resolved by name via the token service.

```java
.column("Description", "Description")
```

### `type(String columnName)`

Adds an **Artifact Type** column — displays the artifact type name.

```java
.type("Type")
```

## Filtering

Filters allow rows to be excluded based on regex matching against column data.

### `filter(AttributeTypeToken type, String regex)`

If any attribute column of the given type produces a value matching the regex, the entire row (and its child levels) is skipped.

```java
.filter(CoreAttributeTypes.Name, ".*DELETED.*")
```

## Running the Report

### Synchronous Endpoint

```
GET /orcs/report/{branch}/view/{view}/template/{template}
```

Returns the report as a streamed Excel XML file attachment. Parameters:

| Parameter  | Description                                                |
| ---------- | ---------------------------------------------------------- |
| `branch`   | The branch ID to query artifacts from                      |
| `view`     | The applicability view ID (use `-1` for no view filtering) |
| `template` | The artifact ID of the ReportTemplate on the COMMON branch |

### Asynchronous Endpoint

```
GET /orcs/report/{branch}/view/{view}/template/{template}/async/{email}
```

Kicks off report generation in the background. When complete, the report is written to the server's publish directory and an email with a download link is sent to the specified recipient. Returns immediately with a JSON confirmation:

```json
{
  "status": "Report generation started",
  "fileName": "Generic_Trace_Report_2026-05-28_10-30.xml",
  "branch": "570",
  "view": "-1",
  "template": "12345",
  "emailRecipient": "user@example.com",
  "downloadLink": "http://server:8089/orcs/resources/publish?path=Generic_Trace_Report_2026-05-28_10-30.xml"
}
```

## Output Format

The report is generated as **Excel XML** (Microsoft SpreadsheetML format), which can be opened directly in Excel or LibreOffice Calc.

### Worksheet Structure

The output workbook contains two sheets:

1. **Report Sheet** — Named after the ReportTemplate artifact. Contains:

   - **Row 1 (Top Row):** Level names spanning their respective columns. Each level name appears in the first column of that level's group.
   - **Row 2 (Header Row):** Individual column headers for every column across all levels.
   - **Data Rows:** One row per leaf-level artifact. Parent level data is repeated for each child combination. If a parent has multiple children, the parent columns are duplicated across rows.

2. **DebugInfo Sheet** — Contains logging and diagnostic output from the template parsing and report generation process. Useful for troubleshooting AST parser issues, missing paths, or query problems.

### Example Output Layout

For a two-level report with "Requirements" (columns: ID, Name) and "Code Units" (columns: ID, File Path):

| Requirements |                  | Code Units  |                  |
| ------------ | ---------------- | ----------- | ---------------- |
| Artifact Id  | Requirement Name | Artifact Id | File System Path |
| 1001         | Login Feature    | 2001        | /src/Login.java  |
| 1001         | Login Feature    | 2002        | /src/Auth.java   |
| 1002         | Logout Feature   | 2003        | /src/Logout.java |

### Multi-Level Example

A complete multi-level trace report:

```java
public void reportCode(GenericReport report) {
   report.level("System Functions",
      report.query().andIsOfType(CoreArtifactTypes.SystemFunctionMsWord))
      .column("Artifact Id")
      .column("System Function Name", CoreAttributeTypes.Name)
      .column(CoreAttributeTypes.FDAL);

   report.level("Subsystem Functions",
      report.query().follow(CoreRelationTypes.Dependency_Dependency))
      .column("Artifact Id")
      .column("Subsystem Function Name", CoreAttributeTypes.Name);

   report.level("Software Requirements",
      report.query().follow(CoreRelationTypes.RequirementTrace_LowerLevelRequirement))
      .column("Artifact Id")
      .column("Software Requirement Name", CoreAttributeTypes.Name)
      .column(CoreAttributeTypes.IDAL);
}
```

This produces a report that traces from System Functions → Subsystem Functions → Software Requirements, with each level's columns appearing side by side and parent data repeated for each child combination.
