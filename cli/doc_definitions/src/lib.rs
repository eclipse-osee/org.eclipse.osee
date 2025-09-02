/*********************************************************************
 * Copyright (c) 2025 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
pub fn supported_file_types() -> &'static str {
    r#"
    Supported Default Formats:
    *.md                  Starting Single Line Terminated Syntax: ``             Ending Single Line Terminated Syntax: ``              Starting Single Line Non Terminated Syntax: Not Supported  Starting Multiline Syntax: Not Supported   Ending Multiline Syntax: Not Supported  Starting Code Block Syntax: ```            Ending Code Block Syntax: ```
    *.cpp                 Starting Single Line Terminated Syntax: /*             Ending Single Line Terminated Syntax: */              Starting Single Line Non Terminated Syntax: //             Starting Multiline Syntax: /*              Ending Multiline Syntax: */             Starting Code Block Syntax: Not Supported  Ending Code Block Syntax: Not Supported
    *.cxx                 Starting Single Line Terminated Syntax: /*             Ending Single Line Terminated Syntax: */              Starting Single Line Non Terminated Syntax: //             Starting Multiline Syntax: /*              Ending Multiline Syntax: */             Starting Code Block Syntax: Not Supported  Ending Code Block Syntax: Not Supported
    *.cc                  Starting Single Line Terminated Syntax: /*             Ending Single Line Terminated Syntax: */              Starting Single Line Non Terminated Syntax: //             Starting Multiline Syntax: /*              Ending Multiline Syntax: */             Starting Code Block Syntax: Not Supported  Ending Code Block Syntax: Not Supported
    *.c                   Starting Single Line Terminated Syntax: /*             Ending Single Line Terminated Syntax: */              Starting Single Line Non Terminated Syntax: //             Starting Multiline Syntax: /*              Ending Multiline Syntax: */             Starting Code Block Syntax: Not Supported  Ending Code Block Syntax: Not Supported
    *.hpp                 Starting Single Line Terminated Syntax: /*             Ending Single Line Terminated Syntax: */              Starting Single Line Non Terminated Syntax: //             Starting Multiline Syntax: /*              Ending Multiline Syntax: */             Starting Code Block Syntax: Not Supported  Ending Code Block Syntax: Not Supported
    *.hxx                 Starting Single Line Terminated Syntax: /*             Ending Single Line Terminated Syntax: */              Starting Single Line Non Terminated Syntax: //             Starting Multiline Syntax: /*              Ending Multiline Syntax: */             Starting Code Block Syntax: Not Supported  Ending Code Block Syntax: Not Supported
    *.hh                  Starting Single Line Terminated Syntax: /*             Ending Single Line Terminated Syntax: */              Starting Single Line Non Terminated Syntax: //             Starting Multiline Syntax: /*              Ending Multiline Syntax: */             Starting Code Block Syntax: Not Supported  Ending Code Block Syntax: Not Supported
    *.h                   Starting Single Line Terminated Syntax: /*             Ending Single Line Terminated Syntax: */              Starting Single Line Non Terminated Syntax: //             Starting Multiline Syntax: /*              Ending Multiline Syntax: */             Starting Code Block Syntax: Not Supported  Ending Code Block Syntax: Not Supported
    *.rs                  Starting Single Line Terminated Syntax: /*             Ending Single Line Terminated Syntax: */              Starting Single Line Non Terminated Syntax: //             Starting Multiline Syntax: /*              Ending Multiline Syntax: */             Starting Code Block Syntax: Not Supported  Ending Code Block Syntax: Not Supported
    *.bzl                 Starting Single Line Terminated Syntax: Not Supported  Ending Single Line Terminated Syntax: Not Supported   Starting Single Line Non Terminated Syntax: #              Starting Multiline Syntax: Not Supported   Ending Multiline Syntax: Not Supported  Starting Code Block Syntax: Not Supported  Ending Code Block Syntax: Not Supported
    *.bazel               Starting Single Line Terminated Syntax: Not Supported  Ending Single Line Terminated Syntax: Not Supported   Starting Single Line Non Terminated Syntax: #              Starting Multiline Syntax: Not Supported   Ending Multiline Syntax: Not Supported  Starting Code Block Syntax: Not Supported  Ending Code Block Syntax: Not Supported
    *.tex                 Starting Single Line Terminated Syntax: \\if           Ending Single Line Terminated Syntax: {}              Starting Single Line Non Terminated Syntax: Not Supported  Starting Multiline Syntax: Not Supported   Ending Multiline Syntax: Not Supported  Starting Code Block Syntax: Not Supported  Ending Code Block Syntax: Not Supported
    *.adoc                Starting Single Line Terminated Syntax: ``             Ending Single Line Terminated Syntax: ``              Starting Single Line Non Terminated Syntax: Not Supported  Starting Multiline Syntax: Not Supported   Ending Multiline Syntax: Not Supported  Starting Code Block Syntax: ```            Ending Code Block Syntax: ```
    *.bat                 Starting Single Line Terminated Syntax: Not Supported  Ending Single Line Terminated Syntax: Not Supported   Starting Single Line Non Terminated Syntax: #              Starting Multiline Syntax: Not Supported   Ending Multiline Syntax: Not Supported  Starting Code Block Syntax: Not Supported  Ending Code Block Syntax: Not Supported
    *.cmd                 Starting Single Line Terminated Syntax: Not Supported  Ending Single Line Terminated Syntax: Not Supported   Starting Single Line Non Terminated Syntax: #              Starting Multiline Syntax: Not Supported   Ending Multiline Syntax: Not Supported  Starting Code Block Syntax: Not Supported  Ending Code Block Syntax: Not Supported
    *.java                Starting Single Line Terminated Syntax: /*             Ending Single Line Terminated Syntax: */              Starting Single Line Non Terminated Syntax: //             Starting Multiline Syntax: /*              Ending Multiline Syntax: */             Starting Code Block Syntax: Not Supported  Ending Code Block Syntax: Not Supported
    WORKSPACE             Starting Single Line Terminated Syntax: Not Supported  Ending Single Line Terminated Syntax: Not Supported   Starting Single Line Non Terminated Syntax: #              Starting Multiline Syntax: Not Supported   Ending Multiline Syntax: Not Supported  Starting Code Block Syntax: Not Supported  Ending Code Block Syntax: Not Supported
    BUILD                 Starting Single Line Terminated Syntax: Not Supported  Ending Single Line Terminated Syntax: Not Supported   Starting Single Line Non Terminated Syntax: #              Starting Multiline Syntax: Not Supported   Ending Multiline Syntax: Not Supported  Starting Code Block Syntax: Not Supported  Ending Code Block Syntax: Not Supported
    *.fileApplicability   Starting Single Line Terminated Syntax: Not Supported  Ending Single Line Terminated Syntax: Not Supported   Starting Single Line Non Terminated Syntax: #              Starting Multiline Syntax: Not Supported   Ending Multiline Syntax: Not Supported  Starting Code Block Syntax: Not Supported  Ending Code Block Syntax: Not Supported
    *.applicability       Starting Single Line Terminated Syntax: Not Supported  Ending Single Line Terminated Syntax: Not Supported   Starting Single Line Non Terminated Syntax: #              Starting Multiline Syntax: Not Supported   Ending Multiline Syntax: Not Supported  Starting Code Block Syntax: Not Supported  Ending Code Block Syntax: Not Supported
    *.gpj                 Starting Single Line Terminated Syntax: Not Supported  Ending Single Line Terminated Syntax: Not Supported   Starting Single Line Non Terminated Syntax: #              Starting Multiline Syntax: Not Supported   Ending Multiline Syntax: Not Supported  Starting Code Block Syntax: Not Supported  Ending Code Block Syntax: Not Supported
    *.mk                  Starting Single Line Terminated Syntax: Not Supported  Ending Single Line Terminated Syntax: Not Supported   Starting Single Line Non Terminated Syntax: #              Starting Multiline Syntax: Not Supported   Ending Multiline Syntax: Not Supported  Starting Code Block Syntax: Not Supported  Ending Code Block Syntax: Not Supported
    *.opt                 Starting Single Line Terminated Syntax: Not Supported  Ending Single Line Terminated Syntax: Not Supported   Starting Single Line Non Terminated Syntax: #              Starting Multiline Syntax: Not Supported   Ending Multiline Syntax: Not Supported  Starting Code Block Syntax: Not Supported  Ending Code Block Syntax: Not Supported
    Makefile              Starting Single Line Terminated Syntax: Not Supported  Ending Single Line Terminated Syntax: Not Supported   Starting Single Line Non Terminated Syntax: #              Starting Multiline Syntax: Not Supported   Ending Multiline Syntax: Not Supported  Starting Code Block Syntax: Not Supported  Ending Code Block Syntax: Not Supported
    makefile              Starting Single Line Terminated Syntax: Not Supported  Ending Single Line Terminated Syntax: Not Supported   Starting Single Line Non Terminated Syntax: #              Starting Multiline Syntax: Not Supported   Ending Multiline Syntax: Not Supported  Starting Code Block Syntax: Not Supported  Ending Code Block Syntax: Not Supported
    MAKEFILE              Starting Single Line Terminated Syntax: Not Supported  Ending Single Line Terminated Syntax: Not Supported   Starting Single Line Non Terminated Syntax: #              Starting Multiline Syntax: Not Supported   Ending Multiline Syntax: Not Supported  Starting Code Block Syntax: Not Supported  Ending Code Block Syntax: Not Supported
"#
}

pub fn applic_file_example() -> &'static str {
    r#"
    Example contents of .applicability:
Configuration[Product_A]
csvfiles
End Configuration
Feature[JHU_CONTROLLER]
CppTest_Exclude.cpp
End Feature
Feature[ENGINE_5=A2543]
**/enginefiles
End Configuration

How this reads:
If the source is being processed for Product A, include any directories/files at this location named "csvfiles".
If the source is being processed for a product that is not Product A, DO NOT include any directories/files at this location named "csvfiles".
If the JHU_CONTROLLER is set to Included for the view being processed, include the file at this location named "CppTest_Exclude.cpp".
If the JHU_CONTROLLER is not set to Included for the view being processed, DO NOT include the file at this location named "CppTest_Exclude.cpp".
If the ENGINE_5 is set to A2543, include any directories/files at any level at or below this location named "enginefiles".
If the ENGINE_5 is not set to A2543, DO NOT include any directories/files at any level at or below this location named "enginefiles".
"#
}

pub fn pat_config_example() -> &'static str {
    r#"
    Example pat-config.toml:
[project]
inline_project_exclusions = [ "path/to/file.md", "another/path/to/file.cpp" ]
[[feature]]
name = "test_feature"
values = ["Included", "Excluded"]
description = "description of test feature"
[[feature.test_category]]
name = "test_feature2"
values = ["Included", "Excluded"]
productApplicabilities = [ "ABC", "DEF"]
applicabilityConstraint = "hello4=Included"
description = "description of test feature2"{n}
"#
}
pub fn dot_applicability_syntax_and_notes() -> &'static str {
    r#"
    .applicability & .fileApplicability{n}
----------------------------------------------------{n}
Using feature tagging similar to what is used in the source, specify which directory names (including relative paths or glob patterns) and/or filenames should or should not be processed for specific product line configurations.
Any file or directory that is not listed in an .applicability file should be included and processed.
{n}
The syntax of the .applicability/.fileApplicability
is as follows:{n}
# Feature[APPLIC_TAG]
path/to/include
another/path/to/include/**/*
# End Feature

The first .applicability/.fileApplicability file found will have precedence in the case of conflicting definitions.
In most cases, this should be the highest "level" .applicability file for the project being processed.

Note: The paths in your .fileApplicability/.applicability files is sensitive to your OS's path handling and how you passed in your input/output paths
"#
}
pub fn pat_config_note() -> &'static str {
    r#"
    PAT Config{n}
----------------------------------------------------
The pat-config.toml is a configuration file that allows for:
* Configuration of which files should not be subject to inline projection
* Definition of the PLE model, regardless of configuration. (in-development)
* Definition of the Bill of Features. (in-development)
* Configuration of which files should be included in projection, same as the .applicability & .fileApplicability files. (not implemented)
* Configuration of custom file type handling. (not implemented)
The pat-config.toml file will be looked for at the root of the project directory.
{n}"#
}

pub fn ple_applicability_tag_syntax_rules() -> &'static str {
    r#"
    Applicability Syntax{n}
----------------------------------------------------{n}
All tags must be included within a supported comment block per the tool(s) supported file types.

Features, Configurations, Configuration Groups, Substitution Tags may contain:
* Letters
* Numbers
* Spaces
* Underscores(_)
* Dashes(-)
* Periods(.)
* Parentheses(())

Supported Applicability Tags:
* Feature
* FeatureNot
* FeatureElse
* FeatureElseIf
* FeatureSwitch
* FeatureCase
* End Feature
* Configuration
* ConfigurationNot
* ConfigurationElse
* ConfigurationElseIf
* ConfigurationSwitch
* ConfigurationCase
* End Configuration
* ConfigurationGroup
* ConfigurationGroupNot
* ConfigurationGroupElse
* ConfigurationGroupElseIf
* ConfigurationGroupSwitch
* ConfigurationGroupCase
* End ConfigurationGroup
* Eval

An applicability block is defined as one(or more) combinations of Feature, Configuration, or Configuration Group Tags
which are followed by the appropriate end block.

Applicability Tags which are not a part of an applicability block are an error, and will not be processed correctly.
Substitution Tags(Eval) are not included in this requirement.

Applicability Values are matched with the same capitalization i.e. Included != INCLUDED.

The following examples are written in markdown syntax.

``Feature[A=Included]`` included_text ``End Feature``
included_text is included in the projection when feature A is set to Included.

``FeatureNot[A=Included]`` included_text ``End Feature``
included_text is included in the projection when feature A is not set to Included.

``Feature[A]`` included_text ``End Feature``
Feature[A] is shorthand for Feature[A=Included] and thus will be treated the same.

``Feature[!A]`` included_text ``End Feature``
Feature[!A] is shorthand for FeatureNot[A=Included] and thus will be treated the same.

``Feature[A=Included]`` included_text ``FeatureElse`` else_text ``End Feature``
included_text is included in the projection when feature A is set to Included. 
else_text will be included in the projection when feature A is not set to Included.

``Feature[A=Included]`` included_text ``FeatureElseIf[B=Included]`` else_if_text ``End Feature``
included_text is included in the projection when feature A is set to Included. 
else_if_text will be included in the projection when feature B is set to Included and feature A is not set to Included.

``Feature[A=Included]`` included_text ``FeatureElseIf[B=Included]`` else_if_text  ``FeatureElse`` else_text ``End Feature``
included_text is included in the projection when feature A is set to Included. 
else_if_text will be included in the projection when feature B is set to Included and feature A is not set to Included.
else_text will be included in the projection when feature B is not set to Included and feature A is not set to Included.

``Feature Switch````Feature Case[A]``a_text``Feature Case[B]``b_text``End Feature``
a_text will be included when feature A is set to Included.
b_text will be included when feature B is set to Included, and feature A is not set to Included.

``Feature[A=Included & B=Included]`` included_text ``End Feature``
included_text is included in the projection when feature A and feature B are set to Included.

``Feature[A & B]`` included_text ``End Feature``
included_text is included in the projection when feature A and feature B are set to Included.

``Feature[A & !B]`` included_text ``End Feature``
included_text is included in the projection when feature A is set to Included, and feature B is not set to Included.

``Feature[A=Included | B=Included]`` included_text ``End Feature``
included_text is included in the projection when feature A or feature B are set to Included.

``Feature[A | B]`` included_text ``End Feature``
included_text is included in the projection when feature A or feature B are set to Included.

``Feature[A | !B]`` included_text ``End Feature``
included_text is included in the projection when feature A is set to Included or feature B is not set to Included.

``Feature[A & ( B | C)]`` included_text ``End Feature``
included_text is included in the projection when feature A is set to Included and feature B or feature C is set to Included.

``Feature[A & ( B | !C)]`` included_text ``End Feature``
included_text is included in the projection when feature A is set to Included and feature B is set to Included, or feature C is not set to Included.

``Feature[A & !( B | C)]`` included_text ``End Feature``
included_text is included in the projection when feature A is set to Included and feature B and feature C are not set to Included.

Parentheses can be used to compose all manners of complex logic inside an applicability tag.

``Feature[A]``
1. Tag 1
``End Feature``
2. Common Row 1
``Feature[B]``
    - Tag 2.1
``End Feature``
``Feature[C]``
3. Tag 2
    - Tag 2 Subbullet
``End Feature``
4. Common Row 2
1. Tag 1 is included in the projection when feature A is set to Included.
2. Common Row 1 is in all projections.
- Tag 2.1 is included in the projection when feature B is set to Included.
3. Tag 2
    - Tag 2 Subbullet
    is included in the projection when feature C is set to Included.
4. Common Row 2 is in all projections.

``Feature[A]`` included_text_A ``Feature[B]`` included_text_B ``FeatureElse`` else_text_B ``End Feature`` ``FeatureElse`` else_text_A ``End Feature``
included_text_A is included in the projection when feature A is set to Included.
included_text_B is included in the projection when feature A is set to Included, and feature B is set to Included.
else_text_B is included in the projection when feature A is set to Included, but feature B is not set to Included.
else_text_A is included in the projection when feature A is not set to Included.

``Eval[TEXT_REPL_A]``
This comment block will be replaced by the contents of TEXT_REPL_A. If TEXT_REPL_A is not defined, it will be replaced by an empty string.

``Feature[A]`` a_text ``Eval[TEXT_REPL_A]````End Feature``
a_text is included in the projection when feature A is set to Included.
TEXT_REPL_A's tag is replaced by the contents of TEXT_REPL_A when feature A is set to Included.

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
| 1a | 1b | 1c | 1d | 1e |
| ``Feature[A]``2a | 2b | 2c | 2d | 2e``End Feature`` |
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[A]``4a | 4b | 4c``End Feature`` | 4d | 4e |
| 5a | 5b | 5c | 5d | 5e |
| ``Feature[A]``6a``End Feature`` | 6b | 6c | 6d | ``Feature[B]``6e``End Feature`` |
| 7a | 7b | 7c | 7d | 7e |
Row 2 of the table will be included in projection when feature A is set to Included.
Cells 4a,4b,4c will be included in projection when feature A is set to Included. The table will be malformed for other configurations.
Cell 6a will be included in the projection when feature A is set to Included. The table will be malformed for other configurations.
Cell 6e will be included in the projection when feature B is set to Included. The table will be malformed for other configurations.

| Col A | ``Feature[A]``Col B |``End Feature`` Col C | Col D ``Feature[B]``| Col E ``End Feature``|
|---|``Feature[A]``---|``End Feature``---|---``Feature[B]``|---:``End Feature``|
| 1a | ``Feature[A]``1b |``End Feature`` 1c | 1d ``Feature[B]``| 1e ``End Feature``|
| 2a | ``Feature[A]``2b |``End Feature`` 2c | 2d ``Feature[B]``| 2e ``End Feature``|
| 3a | ``Feature[A]``3b |``End Feature`` 3c | 3d ``Feature[B]``| 3e ``End Feature``|
| 3a | ``Feature[A]``3b |``End Feature`` 3c | 3d ``Feature[B]``| 3e ``End Feature``|
Column B will be included in the projection when feature A is set to Included.
Column D will be included in the projection when feature B is set to Included.

Other types of tag logic can be used on tables similarly(i.e. Else, ElseIf, Switch/Case), and the logic will be evaluated left to right.

The above examples are also true for Configuration and ConfigurationGroup styled tags, barring anything that utilizes &, as multiple configurations/configuration groups can not be valid at the same time.

If a feature value is found in a file being processed, but is not available in the PLE model or Bill of Features(defined in the pat-config.toml), it will be evaluated as false(excluded from projection).

    "#
}

pub fn applicability_definitions() -> &'static str {
    r#"
    Definitions{n}
----------------------------------------------------{n}
    * PLE model is the definition of the features, feature constraints, bill of features, and substitutions for a given project.
    * Bill Of Features is the set of features and substitutions valid for a given configuration or configuration group, 
    as well as the relations that configuration/configuration group has with other configuration(s) or configuration group(s).
    "#
}
