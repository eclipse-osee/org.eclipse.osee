This wiki describes OSEE branches, conflicts and merge handling. Target
audience: developers.

To also help in understanding of a OSEE branches, sample scenario
(default Demo database) will be weaved into the text.

# Branch

A branch in OSEE is represented by an entry in OSEE_BRANCH table.

![image:osee_branch_intro1.png](/docs/images/osee_branch_intro1.png "image:osee_branch_intro1.png")

|                    |              |                           |                       |                     |          |            |               |                    |                         |
| ------------------ | ------------ | ------------------------- | --------------------- | ------------------- | -------- | ---------- | ------------- | ------------------ | ----------------------- |
| BRANCH_NAME       | BRANCH_TYPE | BASELINE_TRANSACTION_ID | BRANCH_GUID          | ASSOCIATED_ART_ID | ARCHIVED | BRANCH_ID | BRANCH_STATE | PARENT_BRANCH_ID | PARENT_TRANSACTION_ID |
| System Root Branch | 4            | 1                         | AyH_fDnM2RFEhyybolQA | \-1                 | 0        | 1          | 1             | \-1                | 1                       |
| Common             | 2            | 4                         | AyH_fDpMERA+zDfML4gA | \-1                 | 0        | 2          | 2             | 2                  | 3                       |
| SAW_Bld_1        | 2            | 19                        | AyH_f2sSKy3l07fIvAAA | \-1                 | 0        | 3          | 1             | 1                  | 3                       |
| CIS_Bld_1        | 2            | 21                        | AyH_f2sSKy3l07fIvDDD | \-1                 | 0        | 4          | 1             | 1                  | 3                       |









## Adding

Creating a child branch CIS_child_branch from CIS_Bld_1.

![image:osee_branch_adding_a_branch.png](/docs/images/osee_branch_adding_a_branch.png "image:osee_branch_adding_a_branch.png")

OSEE_BRANCH Table:

|                    |              |                           |                       |                     |          |            |               |                    |                         |
| ------------------ | ------------ | ------------------------- | --------------------- | ------------------- | -------- | ---------- | ------------- | ------------------ | ----------------------- |
| BRANCH_NAME       | BRANCH_TYPE | BASELINE_TRANSACTION_ID | BRANCH_GUID          | ASSOCIATED_ART_ID | ARCHIVED | BRANCH_ID | BRANCH_STATE | PARENT_BRANCH_ID | PARENT_TRANSACTION_ID |
| System Root Branch | 4            | 1                         | AyH_fDnM2RFEhyybolQA | \-1                 | 0        | 1          | 1             | \-1                | 1                       |
| Common             | 2            | 4                         | AyH_fDpMERA+zDfML4gA | \-1                 | 0        | 2          | 2             | 2                  | 3                       |
| SAW_Bld_1        | 2            | 19                        | AyH_f2sSKy3l07fIvAAA | \-1                 | 0        | 3          | 1             | 1                  | 3                       |
| CIS_Bld_1        | 2            | 21                        | AyH_f2sSKy3l07fIvDDD | \-1                 | 0        | 4          | 1             | 1                  | 3                       |
| CIS_child_branch | 0            | 24                        | AAnWwP8Wdn3nt74X1PgA  | 10                  | 0        | 5          | 1             | 4                  | 22                      |










OSEE_TX_DETAILS Table: (from `SELECT * FROM OSEE.OSEE_TX_DETAILS WHERE
TRANSACTION_ID = 24;`)

|        |                         |                                  |          |                 |            |                 |
| ------ | ----------------------- | -------------------------------- | -------- | --------------- | ---------- | --------------- |
| AUTHOR | TIME                    | OSEE_COMMENT                    | TX_TYPE | COMMIT_ART_ID | BRANCH_ID | TRANSACTION_ID |
| 17     | 2012-01-31 13:30:03.865 | New Branch from CIS_Bld_1 (22) | 1        | null            | 5          | 24              |










# Merge(s)

Merge branches are considered to be "half" branches, difference being in
that they only exist in OSEE_MERGE table, (with backing transaction
data in OSEE_TXS and OSEE_TX_DETAILS). In contrast, a typical full
branch would contains full, current records of artifacts, relations and
other meta data related to it. A merge branch can be thought of a place
holder to reference for a conflicted artifact, relation or type at a
specific time.

Refer to
`plugins/org.eclipse.osee.framework.core.datastore/support/SKYNET.VERSIONING.SCHEMA.xml`
for schema descriptions.

# Conflict(s)