---
title: Applicability
description: Applicability Syntax Specification
---

## Definitions

An applicability expression is a conditional expression which includes
one or more key value pairs being evaluated for validity.

A complete applicability expression is one or more applicability expression(s) which can be evaluated together as one if read from left to right.

An applicability tag is a combination of the word(s) Feature, Configuration, ConfigurationGroup with an optional keyword modifier, and potentially followed by a complete applicability expression contained within braces. For the list of standard applicability tags, see [here](/org.eclipse.osee/ple/applicability-syntax#standard-applicability-tags).

An applicability block is defined as one(or more) combinations of applicability tags which are followed by the appropriate end block.

Applicability content refers to text content that is located between two separate applicability tags(and their comment characters).

Bill Of Features is the set of applicability expressions(features and substitutions) valid for a given configuration or configuration group, as well as the relations that configuration/configuration group has with other configuration(s) or configuration group(s).

PLE model is the definition of the features, feature constraints, bill of features, and substitutions for a given project.


## Evaluation Rules

Applicability Expressions may contain:
* Letters
* Numbers
* Spaces
* Underscores(_)
* Dashes(-)
* Periods(.)
* Parentheses(())

All applicability tags must be contained within a relevant comment block for the language/document type that they exist within.

Applicability expression keys and values are matched/compared with the same capitalization i.e. Included != INCLUDED.

Substitution Tags follow the same evaluation rules as applicability tags, but are not constrained to an applicability block.

If a applicability expression is found in content being processed, but is not available in the PLE model or Bill of Features, it will be evaluated as false(excluded from projection).

Configuration and ConfigurationGroup applicability tags MUST NOT contain AND'd expressions, as Configurations and Configuration Groups are mutually exclusive.

Applicability Blocks and Tags are always evaluated left to right.

An applicability expression which contains a key but no value should be evaluated as including the value "Included".

## Standard Applicability Tags
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

## Examples

The following examples are in markdown.

```md
``Feature[A=Included]`` included_text ``End Feature``
```
included_text is included in the projection when feature A is set to Included.

```md
``FeatureNot[A=Included]`` included_text ``End Feature``
```
included_text is included in the projection when feature A is not set to Included.

```md
``Feature[A]`` included_text ``End Feature``
```
Feature[A] is shorthand for Feature[A=Included] and thus will be treated the same.

```md
``Feature[!A]`` included_text ``End Feature``
```
Feature[!A] is shorthand for FeatureNot[A=Included] and thus will be treated the same.

```md
``Feature[A=Included]`` included_text ``FeatureElse`` else_text ``End Feature``
```
included_text is included in the projection when feature A is set to Included. 
else_text will be included in the projection when feature A is not set to Included.

```md
``Feature[A=Included]`` included_text ``FeatureElseIf[B=Included]`` else_if_text ``End Feature``
```
included_text is included in the projection when feature A is set to Included. 
else_if_text will be included in the projection when feature B is set to Included and feature A is not set to Included.

```md
``Feature[A=Included]`` included_text ``FeatureElseIf[B=Included]`` else_if_text  ``FeatureElse`` else_text ``End Feature``
```
included_text is included in the projection when feature A is set to Included. 
else_if_text will be included in the projection when feature B is set to Included and feature A is not set to Included.
else_text will be included in the projection when feature B is not set to Included and feature A is not set to Included.

```md
``Feature Switch````Feature Case[A]``a_text``Feature Case[B]``b_text``End Feature``
```
a_text will be included when feature A is set to Included.
b_text will be included when feature B is set to Included, and feature A is not set to Included.

```md
``Feature[A=Included & B=Included]`` included_text ``End Feature``
```
included_text is included in the projection when feature A and feature B are set to Included.

```md
``Feature[A & B]`` included_text ``End Feature``
```
included_text is included in the projection when feature A and feature B are set to Included.

```md
``Feature[A & !B]`` included_text ``End Feature``
```
included_text is included in the projection when feature A is set to Included, and feature B is not set to Included.

```md
``Feature[A=Included | B=Included]`` included_text ``End Feature``
```
included_text is included in the projection when feature A or feature B are set to Included.

```md
``Feature[A | B]`` included_text ``End Feature``
```
included_text is included in the projection when feature A or feature B are set to Included.

```md
``Feature[A | !B]`` included_text ``End Feature``
```
included_text is included in the projection when feature A is set to Included or feature B is not set to Included.

```md
``Feature[A & ( B | C)]`` included_text ``End Feature``
```
included_text is included in the projection when feature A is set to Included and feature B or feature C is set to Included.

```md
``Feature[A & ( B | !C)]`` included_text ``End Feature``
```
included_text is included in the projection when feature A is set to Included and feature B is set to Included, or feature C is not set to Included.

```md
``Feature[A & !( B | C)]`` included_text ``End Feature``
```
included_text is included in the projection when feature A is set to Included and feature B and feature C are not set to Included.

Parentheses can be used to compose all manners of complex logic inside an applicability tag.

```md
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
```
1. Tag 1 is included in the projection when feature A is set to Included.
2. Common Row 1 is in all projections.
- Tag 2.1 is included in the projection when feature B is set to Included.
3. Tag 2
    - Tag 2 Subbullet
    is included in the projection when feature C is set to Included.
4. Common Row 2 is in all projections.

```md
``Feature[A]`` included_text_A ``Feature[B]`` included_text_B ``FeatureElse`` else_text_B ``End Feature`` ``FeatureElse`` else_text_A ``End Feature``
```
included_text_A is included in the projection when feature A is set to Included.
included_text_B is included in the projection when feature A is set to Included, and feature B is set to Included.
else_text_B is included in the projection when feature A is set to Included, but feature B is not set to Included.
else_text_A is included in the projection when feature A is not set to Included.

```md
``Eval[TEXT_REPL_A]``
```
This comment block will be replaced by the contents of TEXT_REPL_A. If TEXT_REPL_A is not defined, it will be replaced by an empty string.

```md
``Feature[A]`` a_text ``Eval[TEXT_REPL_A]````End Feature``
```
a_text is included in the projection when feature A is set to Included.
TEXT_REPL_A's tag is replaced by the contents of TEXT_REPL_A when feature A is set to Included.

```md
| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
| 1a | 1b | 1c | 1d | 1e |
| ``Feature[A]``2a | 2b | 2c | 2d | 2e``End Feature`` |
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[A]``4a | 4b | 4c``End Feature`` | 4d | 4e |
| 5a | 5b | 5c | 5d | 5e |
| ``Feature[A]``6a``End Feature`` | 6b | 6c | 6d | ``Feature[B]``6e``End Feature`` |
| 7a | 7b | 7c | 7d | 7e |
```
Row 2 of the table will be included in projection when feature A is set to Included.
Cells 4a,4b,4c will be included in projection when feature A is set to Included. The table will be malformed for other configurations.
Cell 6a will be included in the projection when feature A is set to Included. The table will be malformed for other configurations.
Cell 6e will be included in the projection when feature B is set to Included. The table will be malformed for other configurations.

```md
| Col A | ``Feature[A]``Col B |``End Feature`` Col C | Col D ``Feature[B]``| Col E ``End Feature``|
|---|``Feature[A]``---|``End Feature``---|---``Feature[B]``|---:``End Feature``|
| 1a | ``Feature[A]``1b |``End Feature`` 1c | 1d ``Feature[B]``| 1e ``End Feature``|
| 2a | ``Feature[A]``2b |``End Feature`` 2c | 2d ``Feature[B]``| 2e ``End Feature``|
| 3a | ``Feature[A]``3b |``End Feature`` 3c | 3d ``Feature[B]``| 3e ``End Feature``|
| 3a | ``Feature[A]``3b |``End Feature`` 3c | 3d ``Feature[B]``| 3e ``End Feature``|
```
Column B will be included in the projection when feature A is set to Included.
Column D will be included in the projection when feature B is set to Included.


    
