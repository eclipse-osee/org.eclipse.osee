---
title: Applicability Reference Implementations
description: Applicability Reference Implementations
---

## PLE Reference Implementation

Files are processed according to the [applicability specification](/org.eclipse.osee/ple/applicability-syntax).

Files processed according to the applicability specification will have all invalid applicability blocks removed in accordance with the view, and applicability tags used to denote applicabiity blocks removed.
Evaluation tags will be substituted with valid content from the bill of features.

A large portion of the utilities provided require an applicability configuration file which can exist in either TOML or JSON format.

Example(toml):

```toml
name="PRODUCT_A"
group=["abGroup"]
features=["ENGINE_5=A2543","JHU_CONTROLLER=Excluded","ROBOT_ARM_LIGHT=Excluded","ROBOT_SPEAKER=SPKR_A"],
substitutions=[{"match_text":"SOME_SUBSTITUTION","substitute":"SOME NEW TEXT CONTENT"}]
```

Example(json):

```json
{
    "name": "PRODUCT_A",
    "group": ["abGroup"],
    "features": [
        "ENGINE_5=A2543",
        "JHU_CONTROLLER=Excluded",
        "ROBOT_ARM_LIGHT=Excluded",
        "ROBOT_SPEAKER=SPKR_A"
    ],
    "substitutions": [
        {
            "matchText": "SOME_SUBSTITUTION",
            "substitute": "SOME NEW TEXT CONTENT"
        }
    ]
}
```

Utilities which can operate on a wide range of configurations concurrently(i.e. compile file/BAT), accept the same format, but within an array, so multiple configurations can be specified.

If a feature value is found in a file being processed, but is not available in the PLE model or Bill of Features(defined in the ple-config.toml), it will be evaluated as false(excluded from projection).

Default Formats:

| File Type            | Starting Single Line Terminated Syntax | Ending Single Line Terminated Syntax | Starting Single Line Non Terminated Syntax | Starting Multiline Syntax | Ending Multiline Syntax | Starting Code Block Syntax | Ending Code Block Syntax |
| -------------------- | -------------------------------------- | ------------------------------------ | ------------------------------------------ | ------------------------- | ----------------------- | -------------------------- | ------------------------ |
| \*.md                | ` `` `                                 | ` `` `                               | Not Supported                              | Not Supported             | Not Supported           | ```                        | ```                      |
| \*.cpp               | /\*                                    | \*/                                  | //                                         | /\*                       | \*/                     | Not Supported              | Not Supported            |
| \*.cxx               | /\*                                    | \*/                                  | //                                         | /\*                       | \*/                     | Not Supported              | Not Supported            |
| \*.cc                | /\*                                    | \*/                                  | //                                         | /\*                       | \*/                     | Not Supported              | Not Supported            |
| \*.c                 | /\*                                    | \*/                                  | //                                         | /\*                       | \*/                     | Not Supported              | Not Supported            |
| \*.hpp               | /\*                                    | \*/                                  | //                                         | /\*                       | \*/                     | Not Supported              | Not Supported            |
| \*.hh                | /\*                                    | \*/                                  | //                                         | /\*                       | \*/                     | Not Supported              | Not Supported            |
| \*.h                 | /\*                                    | \*/                                  | //                                         | /\*                       | \*/                     | Not Supported              | Not Supported            |
| \*.rs                | /\*                                    | \*/                                  | //                                         | /\*                       | \*/                     | Not Supported              | Not Supported            |
| \*.bzl               | Not Supported                          | Not Supported                        | #                                          | Not Supported             | Not Supported           | Not Supported              | Not Supported            |
| \*.bazel             | Not Supported                          | Not Supported                        | #                                          | Not Supported             | Not Supported           | Not Supported              | Not Supported            |
| \*.tex               | \\if                                   | {}                                   | Not Supported                              | Not Supported             | Not Supported           | Not Supported              | Not Supported            |
| \*.adoc              | ` `` `                                 | ` `` `                               | Not Supported                              | Not Supported             | Not Supported           | ```                        | ```                      |
| \*.bat               | Not Supported                          | Not Supported                        | #                                          | Not Supported             | Not Supported           | Not Supported              | Not Supported            |
| \*.cmd               | Not Supported                          | Not Supported                        | #                                          | Not Supported             | Not Supported           | Not Supported              | Not Supported            |
| \*.java              | /\*                                    | \*/                                  | //                                         | /\*                       | \*/                     | Not Supported              | Not Supported            |
| WORKSPACE            | Not Supported                          | Not Supported                        | #                                          | Not Supported             | Not Supported           | Not Supported              | Not Supported            |
| BUILD                | Not Supported                          | Not Supported                        | #                                          | Not Supported             | Not Supported           | Not Supported              | Not Supported            |
| \*.fileApplicability | Not Supported                          | Not Supported                        | #                                          | Not Supported             | Not Supported           | Not Supported              | Not Supported            |
| \*.applicability     | Not Supported                          | Not Supported                        | #                                          | Not Supported             | Not Supported           | Not Supported              | Not Supported            |
| \*.gpj               | Not Supported                          | Not Supported                        | #                                          | Not Supported             | Not Supported           | Not Supported              | Not Supported            |
| \*.mk                | Not Supported                          | Not Supported                        | #                                          | Not Supported             | Not Supported           | Not Supported              | Not Supported            |
| \*.opt               | Not Supported                          | Not Supported                        | #                                          | Not Supported             | Not Supported           | Not Supported              | Not Supported            |
| Makefile             | Not Supported                          | Not Supported                        | #                                          | Not Supported             | Not Supported           | Not Supported              | Not Supported            |
| makefile             | Not Supported                          | Not Supported                        | #                                          | Not Supported             | Not Supported           | Not Supported              | Not Supported            |
| MAKEFILE             | Not Supported                          | Not Supported                        | #                                          | Not Supported             | Not Supported           | Not Supported              | Not Supported            |
