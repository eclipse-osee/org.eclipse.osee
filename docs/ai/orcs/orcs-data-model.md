---
summary: "ORCS (Object Revision Control System) data model: Artifacts on Branches with typed Attributes and Relations, multiplicity, gamma-id versioning, and the critical data-storage rule that null/empty attribute values must never be persisted (clear = delete the attribute instead)."
tags: [orcs, data-model, artifact, attribute, relation, branch, multiplicity]
fileMatch: "**/*Artifact*,**/*Attribute*,**/*Branch*,**/*Relation*,**/orcs/**"
---

# ORCS (Object Revision Control System) Data Model

## Overview

OSEE uses ORCS as its data storage layer. All data is stored as Artifacts on Branches with Attributes and Relations.

## Core Concepts

### Branches
- All data lives on a Branch (similar to git branches)
- `BranchId` identifies a branch
- Common Branch (`CoreBranches.COMMON`) is the shared system branch
- Working branches are created for changes, then committed back

### Artifacts
- The fundamental data unit in OSEE
- Has a type (`ArtifactTypeToken`), a unique ID (`ArtifactId`), and lives on a branch
- Contains Attributes and participates in Relations
- `Artifact` (client) / `ArtifactReadable` (server) are the runtime representations

### Attributes
- Key-value data attached to an Artifact
- Typed by `AttributeTypeToken` which defines the data type (string, integer, date, boolean, etc.)
- Multiplicity defines min/max count (EXACTLY_ONE, ZERO_OR_ONE, ZERO_OR_MORE, AT_LEAST_ONE)
- Each attribute instance has a unique `AttributeId` and a gamma ID for versioning

### Relations
- Links between two Artifacts (A-side and B-side)
- Typed by `RelationTypeToken`
- Can be ordered (relation order is stored as a special attribute)

## Data Storage Rules

### No Null or Empty Values in the Database
- **NEVER** store null or empty string ("") as an attribute value
- If an attribute has no meaningful value, **delete the attribute** — do not set it to null/empty
- When a user "clears" a value, the attribute instance should be deleted via `artifact.deleteAttributes(attrType)` or `artifact.deleteAttribute(attr)`
- When adding an attribute, always ensure a valid value is provided before persisting
- Warn or prevent any code path that could result in `null` or `""` being persisted

### Attribute Lifecycle
- **Add**: Create attribute with a valid value → persist
- **Edit**: Change value to a new valid value → persist
- **Clear/Delete**: Remove the attribute instance entirely → persist
- **Never**: Set value to null, empty string, or whitespace-only string

### Validation Before Persist
- Before calling `artifact.persist()`, verify no attributes have null/empty values
- Use `Strings.isValid(value)` to check — returns false for null, empty, and whitespace-only
- If a widget's value is cleared and the attribute type allows ZERO instances (ZERO_OR_ONE, ZERO_OR_MORE), delete the attribute
- If the attribute type requires at least one (EXACTLY_ONE, AT_LEAST_ONE), the value must remain valid

## Multiplicity

| Multiplicity | Min | Max | Behavior |
|---|---|---|---|
| EXACTLY_ONE | 1 | 1 | Always exists, cannot be deleted, must have value |
| ZERO_OR_ONE | 0 | 1 | Optional, delete when cleared |
| ZERO_OR_MORE | 0 | ∞ | Optional multi-value, delete individual instances when cleared |
| AT_LEAST_ONE | 1 | ∞ | Must have at least one, cannot delete last instance |

## Versioning / Gamma IDs
- Every change creates a new gamma (version) of the attribute/relation
- Changes are tracked in transactions on branches
- `artifact.persist("comment")` creates a transaction with all pending changes

## Common Patterns

### Adding an attribute
```java
artifact.addAttributeFromString(attributeType, value);
artifact.persist("Add attribute");
```

### Editing an attribute
```java
attribute.setFromString(newValue);
artifact.persist("Edit attribute");
```

### Deleting an attribute (clearing a value)
```java
artifact.deleteAttribute(attribute);  // single instance
// or
artifact.deleteAttributes(attributeType);  // all instances of type
artifact.persist("Delete attribute");
```

### Checking before persist
```java
// Do NOT do this:
artifact.addAttributeFromString(type, "");  // BAD - empty string
artifact.addAttributeFromString(type, null);  // BAD - null

// DO this:
if (Strings.isValid(value)) {
   artifact.addAttributeFromString(type, value);
} else {
   artifact.deleteAttributes(type);  // Clear = delete
}
artifact.persist("Update attribute");
```
