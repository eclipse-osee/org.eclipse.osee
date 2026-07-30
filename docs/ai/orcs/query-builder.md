---
summary: "ORCS QueryBuilder API: building queries, follow/followFork for relation loading, and terminal methods (asArtifacts vs asArtifactIds)"
tags:
  [
    orcs,
    query,
    querybuilder,
    follow,
    followFork,
    relations,
    artifacts,
    FollowRelation,
  ]
fileMatch: "**/orcs/**/search/QueryBuilder.java,**/orcs/**/search/QueryData.java,**/orcs/**/SelectiveArtifactSqlWriter.java,**/orcs/**/QueryEngineImpl.java,**/orcs/core/ds/FollowRelation.java,**/accessor/**/ArtifactAccessorImpl.java"
---

# ORCS QueryBuilder

The `QueryBuilder` (interface) / `QueryData` (implementation) is the primary API for
querying artifacts from the OSEE database. It uses a builder pattern to accumulate
criteria, relation follows, and options, then terminates with a method that executes the
query and returns results.

## Lifecycle of a Query

```
QueryFactory.fromBranch(branch, view)   // 1. Start: select branch + applicability view
   .andId(artifactId)                   // 2. Criteria: narrow to specific artifact(s)
   .andIsOfType(artifactType)           //    (can chain multiple criteria)
   .follow(RelationTypeSide)            // 3. Follow: pre-load related artifacts
   .followFork(RelationTypeSide)        //    (branch into multiple relation paths)
   .asArtifacts()                       // 4. Terminal: execute and return results
```

### 1. Starting a Query

All artifact queries begin with `QueryFactory.fromBranch(...)`. This sets the branch
context and optionally an applicability view. The view controls which artifacts are
visible based on product line configuration.

```java
QueryBuilder query = orcsApi.getQueryFactory().fromBranch(branch);
QueryBuilder query = orcsApi.getQueryFactory().fromBranch(branch, viewArtifactId);
```

### 2. Adding Criteria

Criteria narrow which artifacts the query matches. Common patterns:

| Method                                            | Purpose                                       |
| ------------------------------------------------- | --------------------------------------------- |
| `andId(ArtifactId)`                               | Match a specific artifact by ID               |
| `andIds(Collection)`                              | Match multiple artifacts by ID                |
| `andIsOfType(ArtifactTypeToken...)`               | Match by type (includes subtypes)             |
| `andTypeEquals(ArtifactTypeToken...)`             | Match by exact type (no inheritance)          |
| `andNameEquals(String)`                           | Match by exact Name attribute value           |
| `and(AttributeTypeToken, String, QueryOption...)` | Match by attribute value                      |
| `andRelatedTo(RelationTypeSide, ArtifactId)`      | Match artifacts related to a given artifact   |
| `andRelationExists(RelationTypeSide)`             | Match artifacts that have a given relation    |
| `andNotRelatedTo(RelationTypeSide, ArtifactId)`   | Exclude artifacts related to a given artifact |

All criteria methods return `this` (the same `QueryBuilder`), enabling fluent chaining.

### 3. Following Relations (Pre-Loading)

The `follow` family of methods does **not** filter the result set. Instead, they instruct
the query engine to **join and load related artifacts into memory** so that subsequent
calls to `ArtifactReadable.getRelated(...)` return pre-loaded data without additional
database round-trips.

This is a critical distinction: `follow` affects **what gets loaded**, not **what gets
returned**.

#### `follow(RelationTypeSide)`

Creates a **linear chain** of relation traversals. Each `follow` goes one level deeper
from the previous level. The result list still contains only the "top-level" artifacts
(those matching the criteria), but those artifacts will have their related artifacts
pre-populated.

Critically, `follow` **returns a child builder** positioned at the next level down.
Any subsequent calls (more `follow` or `followFork` calls) operate at that deeper level,
not at the original level. This is what enables multi-level graph traversal.

```java
// Load the artifact and pre-load its lower-level requirements
List<ArtifactReadable> results = orcsApi.getQueryFactory().fromBranch(branch)
   .andId(artifactId)
   .follow(CoreRelationTypes.RequirementTrace_LowerLevelRequirement)
   .asArtifacts();

// The result list contains only the artifact matching andId(...)
ArtifactReadable art = results.get(0);
// But getRelated returns pre-loaded artifacts (no additional DB call)
List<ArtifactReadable> children = art.getRelatedList(
   CoreRelationTypes.RequirementTrace_LowerLevelRequirement);
```

Chaining multiple `follow` calls creates a single path deeper into the relation graph:

```java
// Follows: artifact -> LowerLevel -> LowerLevel (two levels deep)
query.andId(artifactId)
   .follow(CoreRelationTypes.RequirementTrace_LowerLevelRequirement)
   .follow(CoreRelationTypes.RequirementTrace_LowerLevelRequirement)
   .asArtifacts();
```

#### `followFork(RelationTypeSide)`

Creates a **branch point** (fork) in the relation traversal. Use `followFork` when you
need to follow **multiple different relation types from the same artifact**.

`followFork` returns `this` (the current builder), so you remain at the same level after
calling it. This is the key difference from `follow`, which descends into a child level.

**Key rule: `followFork` must be called BEFORE any `follow` calls at the same level.**
This is because `followFork` returns `this` (the current builder), letting you chain
more forks or a terminal `follow` at the same level. Plain `follow` returns a **child**
builder — anything chained after it operates one level deeper. If you called `followFork`
after a `follow`, the fork would attach to the child level rather than the intended level.

**Example 1 — Multiple forks from the same level (from `AmsPrOperations`):**

```java
// Goal: Load a ProblemReport TeamWorkflow, its related Bids,
//       and from each Bid load BOTH its Version AND its TeamWorkflow.
//
// Relation graph:
//   PrTeamWf --(ProblemReportToBid)--> Bid --(BuildImpactDataToVer)--> Version
//                                          --(BuildImpactDataToTeamWf)--> TeamWf
//
// follow navigates PrTeamWf → Bid (one level down).
// Then followFork x2 creates two sibling paths FROM Bid:
Collection<ArtifactReadable> prTeamWfArts =
   orcsApi.getQueryFactory().fromBranch(atsApi.getAtsBranch())
      .andIds(prViewData.getPrWfs())
      .follow(AtsRelationTypes.ProblemReportToBid_Bid)
      // followFork because both paths start at Bid (same level)
      .followFork(AtsRelationTypes.BuildImpactDataToVer_Version)
      .followFork(AtsRelationTypes.BuildImpactDataToTeamWf_TeamWf)
      .asArtifacts();

// Result list contains the PrTeamWf artifacts (the top-level match).
// Bids, Versions, and TeamWfs are all pre-loaded via getRelated().
```

**Example 2 — Fork with a sub-query for deeper paths (from `InterfaceDifferenceReportApiImpl`):**

```java
// Goal: Starting from a PlatformType, load its related Elements,
//       and from each Element follow TWO diverging paths:
//         Path A (fork): Element → ArrayElement → (sub-query deeper chain)
//         Path B (follow): Element → Structure
//
// The sub-query pre-builds the deeper chain for Path A:
QueryBuilder subQuery = new QueryData(QueryType.SELECT, orcsApi.tokenService())
   .follow(CoreRelationTypes.InterfaceStructureContent_Structure);

// Main query:
ArtifactReadable pType = orcsApi.getQueryFactory().fromBranch(branch)
   .andId(artId)
   .follow(CoreRelationTypes.InterfaceElementPlatformType_Element)
   // followFork FIRST — creates Path A from Element level
   .followFork(CoreRelationTypes.InterfaceElementArrayElement_Element, subQuery)
   // follow AFTER fork — creates Path B (linear) from Element level
   .follow(CoreRelationTypes.InterfaceStructureContent_Structure)
   .asArtifact();

// After loading, traverse the graph without additional DB calls:
for (ArtifactReadable element : pType.getRelated(
      CoreRelationTypes.InterfaceElementPlatformType_Element)) {
   elements.add(element);
}
```

**Example 3 — Forks at the root level (from `SoftwareReqVolatilityMetrics`):**

```java
// Goal: Load a Version artifact and pre-load BOTH its related
//       TeamWorkflows AND its TeamDefinition in one query.
//
// No follow before the forks — both forks start from the root artifact.
ArtifactReadable version =
   orcsApi.getQueryFactory().fromBranch(atsApi.getAtsBranch())
      .andTypeEquals(AtsArtifactTypes.Version)
      .andId(ArtifactId.valueOf(targetVersion))
      .followFork(AtsRelationTypes.TeamWorkflowTargetedForVersion_TeamWorkflow)
      .followFork(AtsRelationTypes.TeamDefinitionToVersion_TeamDefinition)
      .asArtifact();

// Both relation paths are pre-loaded:
List<ArtifactReadable> teamDefList =
   version.getRelated(AtsRelationTypes.TeamDefinitionToVersion_TeamDefinition).getList();
List<ArtifactReadable> workflowArts =
   version.getRelated(AtsRelationTypes.TeamWorkflowTargetedForVersion_TeamWorkflow).getList();
```

**Why the ordering matters — a conceptual model:**

```
.follow(A)           → returns child builder (you are now "inside" A's level)
.followFork(B)       → returns THIS builder  (you stay at the current level)

// CORRECT: fork then follow at the same level
query.followFork(X)  // fork path X, stay at current level  → returns this
     .followFork(Y)  // fork path Y, stay at current level  → returns this
     .follow(Z)      // linear path Z from current level    → returns child

// WRONG: follow then fork
query.follow(X)      // go one level deeper                 → returns child of X
     .followFork(Y)  // this fork is now INSIDE X, not beside it!
```

The fork creates sibling child `QueryData` nodes in the query tree. Each forked path
generates its own `artWith` common table expression (CTE) in the SQL, and the results
are `UNION`ed together so all related artifacts across all forked paths are loaded.

`followFork` with a sub-query (as in Example 2) allows attaching a pre-built follow
chain to the fork, useful when a forked path needs to continue multiple levels deep.

#### Building Multi-Level Graphs with `follow` + `followFork`

The real power of these two methods emerges when they are combined to trace an entire
relation graph — potentially many levels deep with branching paths — in a **single
database call**. The key insight is that `follow` and `followFork` operate relative to
the **current level** in the builder, and `follow` advances that level while `followFork`
stays put:

- **`followFork` before any `follow`**: loads relations at the current (root) level.
- **`followFork` after a `follow`**: loads relations at the deeper level that `follow`
  navigated to (because `follow` returned a child builder).
- **Chaining both**: Each `follow` moves deeper, and at each depth you can call
  `followFork` to branch out, then continue with another `follow` to go deeper still.

This means you can describe an arbitrarily deep and wide tree in one fluent chain:

```java
// Trace a 3-level deep graph with branches at each level — one DB call.
//
// Level 0 (root): Artifact matching criteria
//   ├── [fork] RelationA  (sibling path at root)
//   └── [follow] RelationB → Level 1
//         ├── [fork] RelationC  (sibling path at level 1)
//         └── [follow] RelationD → Level 2
//               └── [fork] RelationE  (path at level 2)
ArtifactReadable root = orcsApi.getQueryFactory().fromBranch(branch)
   .andId(rootId)
   .followFork(RelationA)      // fork at root level — stays at root
   .follow(RelationB)          // descend to level 1 — returns child builder
   .followFork(RelationC)      // fork at level 1 — stays at level 1
   .follow(RelationD)          // descend to level 2 — returns grandchild builder
   .followFork(RelationE)      // fork at level 2
   .asArtifact();

// The entire graph is now in memory. Traverse without any additional DB calls:
root.getRelated(RelationA);                          // level 0 fork
ArtifactReadable b = root.getRelated(RelationB)...;  // level 1
b.getRelated(RelationC);                             // level 1 fork
ArtifactReadable d = b.getRelated(RelationD)...;     // level 2
d.getRelated(RelationE);                             // level 2 fork
```

Each call to `follow` advances the builder one level deeper. Each `followFork` at that
level adds a sibling path without advancing. The query engine collects all of these
paths into a single SQL query with multiple CTEs joined together, loading every artifact
in the described graph at once.

This composability is what makes a single `QueryBuilder` chain sufficient for loading
complex domain models (e.g., the entire MIM interface tree from Connection down to
EnumerationState) without N+1 query problems.

#### `FollowRelation` — Declarative Follow Lists

For complex queries with many levels of follows and forks, the `FollowRelation` utility
class provides a declarative list-based alternative to chaining builder calls directly.
This is used heavily in the MIM subsystem.

`FollowRelation.follow(...)` and `FollowRelation.fork(...)` create list entries that get
translated into `query.follow(...)` and `query.followFork(...)` calls by
`ArtifactAccessorImpl.buildFollowRelationQuery`. A fork can include nested children that
become a sub-query.

**Example — Deep relation graph loading in `MimIcdGenerator`:**

```java
// Goal: Load an InterfaceConnection and pre-load its entire relation tree:
//   Connection → Node (fork — not a linear follow, multiple nodes)
//   Connection → TransportType (fork)
//   Connection → Message → SubMessage → Structure → DataElement (linear chain)
//     DataElement → PlatformType (fork with deeper sub-chain)
//       PlatformType → EnumerationSet → EnumerationState
//     DataElement → ArrayElement (linear)
//     DataElement → ArrayIndexDescriptionSet (fork)
//     DataElement → PlatformType → EnumerationSet → EnumerationState (linear tail)
InterfaceConnection conn = mimApi.getInterfaceConnectionViewApi().get(branch, view, connectionId,
   Arrays.asList(
      // Forks FIRST — these are sibling paths from the Connection level
      FollowRelation.fork(CoreRelationTypes.InterfaceConnectionNode_Node),
      FollowRelation.fork(CoreRelationTypes.InterfaceConnectionTransportType_TransportType),
      // Linear chain: Connection → Message → SubMessage → Structure → DataElement
      FollowRelation.follow(CoreRelationTypes.InterfaceConnectionMessage_Message),
      FollowRelation.follow(CoreRelationTypes.InterfaceMessageSubMessageContent_SubMessage),
      FollowRelation.follow(CoreRelationTypes.InterfaceSubMessageContent_Structure),
      FollowRelation.follow(CoreRelationTypes.InterfaceStructureContent_DataElement),
      // Fork with nested children (becomes followFork with sub-query):
      //   DataElement → PlatformType → EnumerationSet → EnumerationState
      FollowRelation.fork(CoreRelationTypes.InterfaceElementPlatformType_PlatformType,
         FollowRelation.follow(CoreRelationTypes.InterfacePlatformTypeEnumeration_EnumerationSet),
         FollowRelation.follow(CoreRelationTypes.InterfaceEnumeration_EnumerationState)),
      // Continue linear chain from DataElement level
      FollowRelation.follow(CoreRelationTypes.InterfaceElementArrayElement_ArrayElement),
      // Another fork at DataElement level
      FollowRelation.fork(CoreRelationTypes.InterfaceElementArrayIndexDescriptionSet_Set),
      // Final linear tail from DataElement level
      FollowRelation.follow(CoreRelationTypes.InterfaceElementPlatformType_PlatformType),
      FollowRelation.follow(CoreRelationTypes.InterfacePlatformTypeEnumeration_EnumerationSet),
      FollowRelation.follow(CoreRelationTypes.InterfaceEnumeration_EnumerationState)));
```

The same ordering rule applies: **forks before follows at each level**. In the list, the
two `fork` entries for Node and TransportType come before the `follow` chain for Message.
The `ArtifactAccessorImpl` iterates the list sequentially, calling `followFork` or
`follow` on the builder for each entry, so the list order maps directly to the call order.

This pattern is preferred when the relation tree is deep and wide — it's more readable
than deeply nested builder chains and allows the follow structure to be passed as data
(e.g., as a method parameter).

#### `followAll()` / `followAll(FollowAllCriteria)`

Loads all relations of the matched artifacts (uses `RelationTypeSide.SENTINEL` internally
to signal "all relation types"). With `FollowAllCriteria.OneLevel`, limits loading to
one level of depth.

#### `followNoSelect(RelationTypeSide, ArtifactTypeToken)`

Same as `follow`, but the followed artifacts are not marked as "selected" (top-level).
They are loaded for relationship access only.

### 4. Terminal Methods

Terminal methods execute the query and return results. The query engine generates very
different SQL depending on which terminal method is called.

#### `asArtifacts()` / `asArtifact()` / `asArtifactMap()`

Sets `QueryType.SELECT`. The engine generates a complex SQL query that:

1. Builds `artWith` CTEs from criteria and follow/followFork relation chains
2. Joins `osee_attribute` to load all attribute values
3. Joins `osee_relation` / `osee_relation2` to load relation links between followed artifacts
4. Combines attributes and relations into a `fields` CTE via `UNION ALL`
5. Orders results by relation order, attribute, or both

The result rows contain **both attributes and relations** interleaved. The engine
processes each row:

- If `attr_id != 0`: populates the artifact's attribute hash map
- If `attr_id == 0`: populates the artifact's relation hash maps (`relationsSideA` / `relationsSideB`)

Artifacts are distinguished as "top" (selected by criteria, `top = 1`) or "other"
(loaded via follow). Only top-level artifacts are returned in the result list, but all
loaded artifacts are accessible via `getRelated(...)`.

#### `asArtifactIds()` / `asArtifactId()`

Sets `QueryType.ID`. The engine generates a **much simpler** SQL query that:

1. Builds `artWith` CTEs from criteria
2. **Skips** attribute joins, relation joins, and the `fields` CTE entirely
3. Selects only `art_id` from the final `artWith`

This means:

- **Follow directives are effectively ignored** for ID queries. The SQL only resolves
  which artifact IDs match the criteria; it does not load attributes or relations.
- The result is a flat list of `ArtifactId` values — no `ArtifactReadable` objects,
  no pre-loaded relations.
- ID queries are significantly cheaper than SELECT queries.

#### `asArtifactTokens()` / `asArtifactToken()`

Sets `QueryType.TOKEN`. The engine generates SQL that joins the `osee_attribute` table
to fetch the Name attribute (or a specified attribute type), producing lightweight
`ArtifactToken` objects (id + name + type). No relations are loaded.

#### Other Terminals

| Method               | QueryType       | Returns                     |
| -------------------- | --------------- | --------------------------- |
| `getCount()`         | COUNT           | Integer count               |
| `exists()`           | COUNT           | Boolean (count > 0)         |
| `asArtifactMaps()`   | ATTRIBUTES_ONLY | `List<Map<String, Object>>` |
| `asArtifactsTable()` | ATTRIBUTES_ONLY | `ArtifactTable`             |

## Internal Query Tree Structure

Internally, `QueryData` forms a tree:

- The **root** node holds the primary criteria (andId, andIsOfType, etc.)
- Each `follow` or `followFork` call creates a **child** `QueryData` node
- `follow` chains create a linear parent→child→grandchild path
- `followFork` creates **sibling** children at the same level

```
Root QueryData [andId(123)]
├── Child: follow(RequirementTrace_LowerLevel)     ← linear chain
│   └── Grandchild: follow(...)
├── Child: followFork(BuildImpactData_Version)     ← fork siblings
└── Child: followFork(BuildImpactData_TeamWf)
```

The `SelectiveArtifactSqlWriter.build()` method walks this tree recursively via
`follow(handlerFactory, artWithAliases, sourceArtTable)`, generating a CTE for each
node. If the tree has multiple leaves, the CTEs are `UNION`ed into an `arts` CTE.

## Options and Modifiers

| Method                         | Effect                                               |
| ------------------------------ | ---------------------------------------------------- |
| `includeDeletedArtifacts()`    | Include deleted artifacts in results                 |
| `includeDeletedAttributes()`   | Include deleted attribute versions                   |
| `includeDeletedRelations()`    | Include deleted relation links                       |
| `includeApplicabilityTokens()` | Load applicability names (not just IDs)              |
| `includeTransactionDetails()`  | Load transaction metadata (author, time, comment)    |
| `setNoLoadRelations()`         | Skip relation loading even with follow directives    |
| `setOrderByAttribute(attr)`    | Order results by an attribute value                  |
| `setOrderMechanism(mechanism)` | "RELATION", "ATTRIBUTE", or "RELATION AND ATTRIBUTE" |
| `isOnPage(pageNum, pageSize)`  | Pagination                                           |
| `fromTransaction(txId)`        | Query at a specific transaction                      |
| `headTransaction()`            | Query only the latest transaction                    |
| `excludeDeleted()`             | Explicitly exclude deleted artifacts                 |

## Common Patterns

### Load an artifact with one level of children

```java
ArtifactReadable parent = orcsApi.getQueryFactory().fromBranch(branch)
   .andId(parentId)
   .follow(CoreRelationTypes.DefaultHierarchical_Child)
   .asArtifact();

List<ArtifactReadable> children = parent.getRelatedList(
   CoreRelationTypes.DefaultHierarchical_Child);
```

### Load an artifact with two parallel relation paths

```java
// From InterfaceDifferenceReportApiImpl — fork before follow at the Element level:
ArtifactReadable pType = orcsApi.getQueryFactory().fromBranch(branch)
   .andId(artId)
   .follow(CoreRelationTypes.InterfaceElementPlatformType_Element)
   // At the Element level, we need two paths:
   //   Path A (fork): Element → ArrayElement (with deeper sub-query)
   //   Path B (follow): Element → Structure
   .followFork(CoreRelationTypes.InterfaceElementArrayElement_Element, subQuery)
   .follow(CoreRelationTypes.InterfaceStructureContent_Structure)
   .asArtifact();
```

Here `followFork` forks one path for ArrayElement (with a sub-query for deeper loading),
then `follow` — called on the same builder that `followFork` returned — creates the
linear Structure path. Both paths originate from the Element level because `followFork`
returned `this`, keeping the builder at that level.

### Get just the IDs matching criteria (no loading)

```java
List<ArtifactId> ids = orcsApi.getQueryFactory().fromBranch(branch)
   .andIsOfType(CoreArtifactTypes.SoftwareRequirementMsWord)
   .asArtifactIds();
```

## Key Takeaways

1. **`follow` loads, it does not filter.** The result list contains only top-level
   artifacts; follow just ensures related artifacts are pre-populated in memory.
2. **`follow` descends, `followFork` stays.** `follow` returns a child builder at the
   next level; `followFork` returns `this` and remains at the current level. This is
   what allows combining them to describe multi-level graphs.
3. **`followFork` before `follow`** when multiple paths diverge from the same level.
   `followFork` returns `this` (the original builder), allowing you to chain more forks
   or a final `follow`. Plain `follow` returns the child builder, so subsequent calls
   continue deeper.
4. **Combine both to build deep graphs in one call.** Alternating `followFork` (to
   branch) and `follow` (to descend) traces through multiple relations and levels,
   loading an entire tree of data in a single database round-trip. After a `follow`
   advances to a new level, any `followFork` at that point branches from that deeper
   level, not from the root.
5. **`asArtifacts` is heavy, `asArtifactIds` is light.** SELECT queries join attributes
   and relations; ID queries select only IDs. Use the lightest terminal that meets your
   needs.
6. **`getRelated(...)` reads from memory.** After loading via `asArtifacts()` with
   appropriate follows, `getRelated` simply reads from in-memory hash maps — no
   additional database calls are made for the followed relations.
