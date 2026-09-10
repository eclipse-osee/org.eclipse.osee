---
summary: "ORCS strongly-typed object model: branches, artifact/attribute/relation type tokens, how they tie together, and full-history versioning across server, web, and IDE"
tags: [orcs, types, artifact-type, attribute-type, relation-type, branch, transaction, gamma, framework, data-model]
fileMatch: "**/framework/core/data/**,**/framework/core/enums/Core*Types.java,**/ats/api/data/Ats*Types.java,**/*TypeTokenProvider*.java"
---

# ORCS Types Model

## What ORCS is

ORCS = **Object Revision Control System**. It is OSEE's strongly-typed object database: every artifact is a typed object with typed attributes and typed relations, and **every change to every artifact, attribute, and relation is versioned with full history**. Nothing is overwritten in place; each change creates a new immutable version.

Although the term "ORCS" mostly appears in the server bundles (`org.eclipse.osee.orcs`, `org.eclipse.osee.orcs.core`, and related `org.eclipse.osee.orcs.*` plugins), the *type model itself* is defined once in the framework and shared by all tiers:

- **Server** (ORCS engine, `OrcsApi`) reads/writes the database using these type tokens.
- **REST** endpoints accept and return data keyed by the same numeric type ids.
- **IDE client** (Eclipse RCP) uses the same Java token classes directly.
- **Web** (Angular, `web/apps/osee`) mirrors the type system as string-valued numeric-id constants and talks to REST.

> Long-term direction: the IDE client is intended to talk directly to the database for all reads, writes, and searching, so that the "client talks to server which talks to database" hop goes away. The shared type model is what makes that feasible - the same tokens describe the data regardless of which tier holds the connection.

## The four kinds of type token

All token types live in `plugins/org.eclipse.osee.framework.core/src/org/eclipse/osee/framework/core/data/`. A token is a lightweight `NamedId` (a stable numeric `id` + a `name`); the id is the contract that ties every tier together.

### Artifact types - `ArtifactTypeToken`

`ArtifactTypeToken.java`. An artifact type defines:
- a stable `id` and `name`,
- whether it is `isAbstract()`,
- its `getSuperTypes()` (single or multiple inheritance; `inheritsFrom(...)` walks the chain),
- the set of attribute types it may hold, each with a multiplicity (see below).

Artifact types are **not** built by calling `create(...)` directly. They are declared through the `AttributeMultiplicity` builder returned by `OrcsTypeTokens.artifactType(...)`, chained with multiplicity methods, and finalized by `osee.add(...)` (which calls `AttributeMultiplicity.get()`):

```java
// plugins/org.eclipse.osee.framework.core/.../enums/CoreArtifactTypes.java
ArtifactTypeToken Artifact = osee.add(osee.artifactType(1L, "Artifact", false, new MaterialIcon("article"),
      Collections.asHashSet(CoreOperationTypes.CreateChildArtifact, CoreOperationTypes.DeleteArtifact))
   .any(Annotation)
   .zeroOrOne(ContentUrl)
   .zeroOrOne(Description)
   .exactlyOne(Name, "unnamed")   // "unnamed" is the default value
   .zeroOrOne(RelationOrder)
   .any(StaticId));
```

`artifactType(id, name, isAbstract, [icon], [operationTypes], superType...)`. Multiple super types are allowed (e.g. ATS `Team Definition` extends both `AbstractAccessControlled` and a responsible-team type).

### Attribute types - `AttributeTypeToken`

`AttributeTypeToken.java` plus typed subtypes in the same package: `AttributeTypeString`, `AttributeTypeBoolean`, `AttributeTypeDate`, `AttributeTypeInteger`, `AttributeTypeLong`, `AttributeTypeDouble`, `AttributeTypeArtifactId`, `AttributeTypeBranchId`, `AttributeTypeEnum`, and others. The concrete generic base is `AttributeTypeGeneric<T>`.

An attribute type carries: `id`, `name`, a **media type** (e.g. `text/plain`, `application/json`, `text/calendar`), a description, its Java storage type (surfaced via `isString()/isBoolean()/isDate()/isInteger()/isArtifactId()/isEnumerated()/...`), optional `DisplayHint`s (e.g. `SingleLine`, `MultiLine`), and a **tagger** (`TaggerTypeToken`) that controls search indexing.

Declared through `OrcsTypeTokens` factory methods:

```java
// plugins/org.eclipse.osee.framework.core/.../enums/CoreAttributeTypes.java
AttributeTypeString  Annotation       = osee.createString(1152921504606847094L, "Annotation", MediaType.TEXT_PLAIN, "");
AttributeTypeBoolean Active           = osee.createBoolean(1152921504606847065L, "Active", MediaType.TEXT_PLAIN, "");
AttributeTypeArtifactId BaselinedBy   = osee.createArtifactIdNoTag(1152921504606847247L, "Baselined By", MediaType.TEXT_PLAIN, "");
AttributeTypeDate    BaselinedTimestamp = osee.createDateNoTag(1152921504606847244L, "Baselined Timestamp", AttributeTypeToken.TEXT_CALENDAR, "");
ComponentAttributeType Component      = osee.createEnum(new ComponentAttributeType());
```

`createString(id, name, mediaType, description, [DisplayHint...])`. The `...NoTag` variants pass `TaggerTypeToken.SENTINEL` so the attribute is **not** indexed for search; the plain `create*` variants pick a default tagger via `AttributeTypeToken.determineTaggerType(mediaType)` (XmlTagger for msword/html, PlainTextTagger otherwise). Enumerated attribute types are declared as dedicated classes under `org.eclipse.osee.framework.core.enums.token.*` and registered with `createEnum(...)`.

### Relation types - `RelationTypeToken` and `RelationTypeSide`

`RelationTypeToken.java`. A relation type ties **two artifact types** together and stores:
- multiplicity (`RelationTypeMultiplicity`: `ONE_TO_ONE`, `ONE_TO_MANY`, `MANY_TO_ONE`, `MANY_TO_MANY`),
- sort order (`RelationSorter`: `UNORDERED`, `LEXICOGRAPHICAL_ASC`, ...),
- a side-A artifact type + side name, and a side-B artifact type + side name.

The sides are **type-constrained, not just name-based**: `isArtifactTypeAllowed(side, artType)` is implemented as `artType.inheritsFrom(getArtifactType(side))`, so inheritance is honored.

```java
// plugins/org.eclipse.osee.framework.core/.../enums/CoreRelationTypes.java
RelationTypeToken Allocation = osee.add(2305843009213694295L, "Allocation", MANY_TO_MANY, LEXICOGRAPHICAL_ASC,
      Requirement, "requirement", Component, "component");
RelationTypeSide Allocation_Requirement = RelationTypeSide.create(Allocation, SIDE_A);
RelationTypeSide Allocation_Component   = RelationTypeSide.create(Allocation, SIDE_B);
```

`RelationTypeSide` (`RelationTypeSide.java`) binds a `RelationTypeToken` to one `RelationSide` (`SIDE_A`/`SIDE_B`); it is the handle code uses to navigate a specific end (`getOpposite()`, `getSide()`, `getRelationType()`). The canonical parent/child tree relation is `CoreRelationTypes.DefaultHierarchical` (`Default Hierarchical`, parent=SIDE_A, child=SIDE_B).

### Branches - `BranchId` / `BranchToken`

`BranchId.java` is a `Long` identity (optionally carrying a view `ArtifactId`); `BranchToken.java` adds a name. Well-known branches are constants:

```java
// plugins/org.eclipse.osee.framework.core/.../enums/CoreBranches.java
public static final BranchToken COMMON      = BranchToken.create(570, "Common");
public static final BranchToken SYSTEM_ROOT = BranchToken.create(1, "System Root Branch");
```

## How the types tie together

```
Branch  (a versioned container of data)
  |
  +-- Transaction  (one commit; ordered by TransactionId)
        |
        +-- creates new gamma-versioned rows for changed:
              Artifact   (typed by ArtifactTypeToken)
                |-- Attributes  (each typed by AttributeTypeToken, subject to the
                |                 artifact type's multiplicity: exactlyOne/zeroOrOne/any/atLeastOne)
                +-- Relations   (each typed by RelationTypeToken; side A/B artifact
                                 types constrained by inheritsFrom)
```

- **Artifact type -> allowed attributes:** every `.zeroOrOne / .exactlyOne / .any / .atLeastOne(attrType[, default])` on the `AttributeMultiplicity` builder records a min/max for that attribute type. At runtime this is queried via `ArtifactTypeToken.getValidAttributeTypes()`, `getMin/getMax`, `getMultiplicity`, and `getAttributeDefault`. Multiplicities: `any` = 0..*, `zeroOrOne` = 0..1, `exactlyOne` = 1..1, `atLeastOne` = 1..*. `anyWhen(attr, supplier)` includes an attribute conditionally.
- **Relation type -> two artifact types:** the relation stores an `ArtifactTypeToken` per side and enforces it via `inheritsFrom`. `OrcsTokenService.getValidRelationTypes(ArtifactTypeToken)` uses this to find relations valid for a given artifact type.
- **Attribute type -> data + search:** the concrete `AttributeType<T>` subtype fixes the storage type; the tagger (tagged vs `NoTag`) controls whether it is indexed for search.

## Type sheets and providers (registration)

Concrete types are declared as static constants on **type "sheets"**:

| Layer | Artifact types | Attribute types | Relation types | Branches |
|-------|----------------|-----------------|----------------|----------|
| Framework (`org.eclipse.osee.framework.core.enums`) | `CoreArtifactTypes` | `CoreAttributeTypes` | `CoreRelationTypes` | `CoreBranches` |
| ATS (`org.eclipse.osee.ats.api.data`) | `AtsArtifactTypes` | `AtsAttributeTypes` | `AtsRelationTypes` | - |

Each layer owns a **namespace** and an `OrcsTypeTokens` factory instance. `OrcsTypeTokens` (`plugins/org.eclipse.osee.framework.core/.../data/OrcsTypeTokens.java`) is the per-namespace factory + registry: it provides `artifactType(...)`, the `create<Type>(...)` attribute factories, relation `add(...)`, accumulates every token, and exposes `registerTypes(OrcsTokenService)`.

A **provider** extends `OrcsTypeTokenProviderBase` to allocate a namespace and register its sheets:

```java
// plugins/org.eclipse.osee.ats.api/.../data/AtsTypeTokenProvider.java
public static final NamespaceToken ATS = NamespaceToken.valueOf(2, "ats", "...");
public static final OrcsTypeTokens ats = new OrcsTypeTokens(ATS);
public static final NamespaceToken ATSDEMO = NamespaceToken.valueOf(10, "ats demo", "...");
public static final OrcsTypeTokens atsDemo = new OrcsTypeTokens(ATSDEMO);

public AtsTypeTokenProvider() {
   super(ats, atsDemo);
   // loadClasses(...) forces static-field init of the sheets before registration
   loadClasses(AtsArtifactTypes.AtsArtifact, AtsAttributeTypes.Actionable, AtsRelationTypes.Derive_To, AtsArtifactTypes.Action);
   registerTokenClasses(AtsArtifactTypes.class, AtsAttributeTypes.class, AtsRelationTypes.class, AtsArtifactTypes.class);
}
```

At runtime, `OrcsTokenService` (`plugins/org.eclipse.osee.framework.core/.../OrcsTokenService.java`) is the central registry. Providers (discovered via OSGi) call `registerTypes(...)` to populate it; it then answers `getArtifactType/getAttributeType/getRelationType` by id or name, `getValidAttributeTypes/getValidRelationTypes`, `getTaggedAttrs()`, and `getBranch(BranchId)`, keeping a single canonical token per id.

Note the loose alphabetical grouping in the sheets and the requirement that **type ids are globally unique** across all namespaces - adding a new type means picking an unused id.

## Full-history versioning (revision control)

Identity types in `org.eclipse.osee.framework.core.data`:

- **`TransactionId`** - a `Long` id; every commit is a transaction, ordered (`isOlderThan`). A transaction is the unit of change on a branch.
- **`GammaId`** - a `Long` id; **every distinct version of an artifact/attribute/relation row gets a new gamma**. History is the set of gammas across transactions; nothing is updated in place.
- **`BranchType`** (`org.eclipse.osee.framework.core.enums`) - `WORKING(0)`, `BASELINE(2)`, `MERGE(3)`, `SYSTEM_ROOT(4)`, `PORT(5)`, with `isBaselineBranch()/isWorkingBranch()/...`.

Branches form a **parent/child tree**: `SYSTEM_ROOT` (id 1) is the root, `COMMON` (id 570) hangs beneath it, and working branches branch off baselines. Each branch holds transactions; each transaction introduces new gamma-versioned rows. The full parent-branch/baseline-transaction structure lives on the richer branch data object rather than the lightweight `BranchToken`.

## Cross-tier usage

- **Java tiers (server ORCS, REST, IDE):** consume `org.eclipse.osee.framework.core.data.*` and `org.eclipse.osee.ats.api.data.*` directly. Server API is `OrcsApi` (`plugins/org.eclipse.osee.orcs/src/org/eclipse/osee/orcs/OrcsApi.java`); query/storage internals (e.g. `CriteriaBranchType`, `DeleteFromAllTablesWithGammaId`) live in `org.eclipse.osee.orcs.core`.
- **Web (Angular):** there is no structural port of the Java token classes. The web mirrors the type system as string-valued numeric-id constants that match the Java token ids - `ARTIFACTTYPEIDENUM` in `web/apps/osee/src/app/shared/types/constants/ArtifactTypeId.enum.ts`, plus analogous `ATTRIBUTETYPEID` and `RELATIONTYPEID` constants - and sends those ids (and per-attribute `gammaId`s) to REST via transaction payloads (`web/apps/osee/src/app/transactions/types/transaction.ts`). REST resolves the ids against the shared Java tokens through `OrcsTokenService`.

## Where a new type goes

1. Pick the sheet for the owning layer (Core* for framework, Ats* for ATS).
2. Declare the constant with a new globally-unique id via the namespace's `OrcsTypeTokens` factory (`create*` for attributes, `artifactType(...).add()` for artifacts, `add(...)` for relations).
3. For an attribute to be storable on an artifact, add it to that artifact type's builder chain with the right multiplicity (declaring the attribute type alone is not enough - see `AtsArtifactTypes.AbstractWorkflowArtifact` for how work-item attributes are attached).
4. If the web needs the type, add the matching id constant to the corresponding TypeScript enum.
