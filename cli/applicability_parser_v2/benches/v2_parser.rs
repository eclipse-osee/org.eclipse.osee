use applicability_lexer_config_markdown::ApplicabilityMarkdownLexerConfig;
use applicability_parser_v2::parse_applicability;
use criterion::*;
use nom_locate::LocatedSpan;
use rand::distributions::Alphanumeric;
use rand::{Rng, thread_rng};

fn bench_parser(c: &mut Criterion) {
    let doc_config: ApplicabilityMarkdownLexerConfig = ApplicabilityMarkdownLexerConfig::default();
    let mut group = c.benchmark_group("v2_parser");
    let rand_string: String = thread_rng()
        .sample_iter(&Alphanumeric)
        .take(100000)
        .map(char::from)
        .collect();
    group.bench_function("parse_large_string", |b| {
        b.iter(|| {
            let _ = std::hint::black_box(parse_applicability(
                LocatedSpan::new_extra(rand_string.as_bytes(), ((0, 0), (0, 0))),
                &doc_config,
            ));
        })
    });
    let test_text_str = "# Overview

This is a test file for using PLE

## Feature Tests

``Feature[APPLIC_1=Included]``
Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``End Feature``

``Feature[APPLIC_1=Included]``
Included Text
``End Feature``

``Feature[APPLIC_1=Excluded]``
Excluded Text
``End Feature``


## Else Tests

``Feature[APPLIC_1]``
Tag 1
``Feature Else``
Not Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``Feature Else``
Not Tag 2
``End Feature``

## Boolean Tests

``Feature[APPLIC_1 | APPLIC_2]``
Included `OR` Excluded Feature
``End Feature``

``Feature[APPLIC_1 & APPLIC_2]``
Included `AND` Excluded Feature
``End Feature``

## Substitution Tests

``Eval[SUB_1]``
``Eval[SUB_2]``

- ``Eval[SUB_1]``
- ``Eval[SUB_2]``

## List Tests

``Feature[APPLIC_1]``
1. Tag 1
``End Feature``
2. Common Row 1
``Feature[APPLIC_1]``
    - Tag 2.1
``End Feature``
``Feature[APPLIC_2]``
3. Tag 2
    - Tag 2 Subbullet
``End Feature``
4. Common Row 2

## Nested Tests

``Feature[APPLIC_1]``
Level 1

``Feature[APPLIC_2]``
Level 2
``End Feature``
``End Feature``

## Feature and Substitution Test

``Feature[APPLIC_1]``
Tag1

``Eval[SUB_1]``
``End Feature``

## Tables

### Table Rows

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
``Feature[APPLIC_1]``| 0a | 0b | 0c | 0d  | 0e |``End Feature``
| 1a | 1b | 1c | 1d | 1e |
``Feature[APPLIC_2]``| 2a | 2b | 2c | 2d  | 2e |``End Feature``
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c | 4d | 4e``End Feature`` |
| 5a | 5b | 5c | 5d | 5e |

### Table Cells

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
| 1a | 1b | 1c | 1d | 1e |
| ``Feature[APPLIC_1]``2a | 2b | 2c | 2d | 2e``End Feature`` |
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c``End Feature`` | 4d | 4e |
| 5a | 5b | 5c | 5d | 5e |
| ``Feature[APPLIC_1]``6a``End Feature`` | 6b | 6c | 6d | ``Feature[APPLIC_2]``6e``End Feature`` |
| 7a | 7b | 7c | 7d | 7e |

### Table Columns

| Col A | ``Feature[APPLIC_1]``Col B |``End Feature`` Col C | Col D ``Feature[APPLIC_2]``| Col E ``End Feature``|
|---|``Feature[APPLIC_1]``---|``End Feature``---|---``Feature[APPLIC_2]``|---:``End Feature``|
| 1a | ``Feature[APPLIC_1]``1b |``End Feature`` 1c | 1d ``Feature[APPLIC_2]``| 1e ``End Feature``|
| 2a | ``Feature[APPLIC_1]``2b |``End Feature`` 2c | 2d ``Feature[APPLIC_2]``| 2e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|

# Overview

This is a test file for using PLE

## Feature Tests

``Feature[APPLIC_1=Included]``
Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``End Feature``

``Feature[APPLIC_1=Included]``
Included Text
``End Feature``

``Feature[APPLIC_1=Excluded]``
Excluded Text
``End Feature``


## Else Tests

``Feature[APPLIC_1]``
Tag 1
``Feature Else``
Not Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``Feature Else``
Not Tag 2
``End Feature``

## Boolean Tests

``Feature[APPLIC_1 | APPLIC_2]``
Included `OR` Excluded Feature
``End Feature``

``Feature[APPLIC_1 & APPLIC_2]``
Included `AND` Excluded Feature
``End Feature``

## Substitution Tests

``Eval[SUB_1]``
``Eval[SUB_2]``

- ``Eval[SUB_1]``
- ``Eval[SUB_2]``

## List Tests

``Feature[APPLIC_1]``
1. Tag 1
``End Feature``
2. Common Row 1
``Feature[APPLIC_1]``
    - Tag 2.1
``End Feature``
``Feature[APPLIC_2]``
3. Tag 2
    - Tag 2 Subbullet
``End Feature``
4. Common Row 2

## Nested Tests

``Feature[APPLIC_1]``
Level 1

``Feature[APPLIC_2]``
Level 2
``End Feature``
``End Feature``

## Feature and Substitution Test

``Feature[APPLIC_1]``
Tag1

``Eval[SUB_1]``
``End Feature``

## Tables

### Table Rows

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
``Feature[APPLIC_1]``| 0a | 0b | 0c | 0d  | 0e |``End Feature``
| 1a | 1b | 1c | 1d | 1e |
``Feature[APPLIC_2]``| 2a | 2b | 2c | 2d  | 2e |``End Feature``
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c | 4d | 4e``End Feature`` |
| 5a | 5b | 5c | 5d | 5e |

### Table Cells

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
| 1a | 1b | 1c | 1d | 1e |
| ``Feature[APPLIC_1]``2a | 2b | 2c | 2d | 2e``End Feature`` |
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c``End Feature`` | 4d | 4e |
| 5a | 5b | 5c | 5d | 5e |
| ``Feature[APPLIC_1]``6a``End Feature`` | 6b | 6c | 6d | ``Feature[APPLIC_2]``6e``End Feature`` |
| 7a | 7b | 7c | 7d | 7e |

### Table Columns

| Col A | ``Feature[APPLIC_1]``Col B |``End Feature`` Col C | Col D ``Feature[APPLIC_2]``| Col E ``End Feature``|
|---|``Feature[APPLIC_1]``---|``End Feature``---|---``Feature[APPLIC_2]``|---:``End Feature``|
| 1a | ``Feature[APPLIC_1]``1b |``End Feature`` 1c | 1d ``Feature[APPLIC_2]``| 1e ``End Feature``|
| 2a | ``Feature[APPLIC_1]``2b |``End Feature`` 2c | 2d ``Feature[APPLIC_2]``| 2e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
# Overview

This is a test file for using PLE

## Feature Tests

``Feature[APPLIC_1=Included]``
Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``End Feature``

``Feature[APPLIC_1=Included]``
Included Text
``End Feature``

``Feature[APPLIC_1=Excluded]``
Excluded Text
``End Feature``


## Else Tests

``Feature[APPLIC_1]``
Tag 1
``Feature Else``
Not Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``Feature Else``
Not Tag 2
``End Feature``

## Boolean Tests

``Feature[APPLIC_1 | APPLIC_2]``
Included `OR` Excluded Feature
``End Feature``

``Feature[APPLIC_1 & APPLIC_2]``
Included `AND` Excluded Feature
``End Feature``

## Substitution Tests

``Eval[SUB_1]``
``Eval[SUB_2]``

- ``Eval[SUB_1]``
- ``Eval[SUB_2]``

## List Tests

``Feature[APPLIC_1]``
1. Tag 1
``End Feature``
2. Common Row 1
``Feature[APPLIC_1]``
    - Tag 2.1
``End Feature``
``Feature[APPLIC_2]``
3. Tag 2
    - Tag 2 Subbullet
``End Feature``
4. Common Row 2

## Nested Tests

``Feature[APPLIC_1]``
Level 1

``Feature[APPLIC_2]``
Level 2
``End Feature``
``End Feature``

## Feature and Substitution Test

``Feature[APPLIC_1]``
Tag1

``Eval[SUB_1]``
``End Feature``

## Tables

### Table Rows

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
``Feature[APPLIC_1]``| 0a | 0b | 0c | 0d  | 0e |``End Feature``
| 1a | 1b | 1c | 1d | 1e |
``Feature[APPLIC_2]``| 2a | 2b | 2c | 2d  | 2e |``End Feature``
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c | 4d | 4e``End Feature`` |
| 5a | 5b | 5c | 5d | 5e |

### Table Cells

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
| 1a | 1b | 1c | 1d | 1e |
| ``Feature[APPLIC_1]``2a | 2b | 2c | 2d | 2e``End Feature`` |
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c``End Feature`` | 4d | 4e |
| 5a | 5b | 5c | 5d | 5e |
| ``Feature[APPLIC_1]``6a``End Feature`` | 6b | 6c | 6d | ``Feature[APPLIC_2]``6e``End Feature`` |
| 7a | 7b | 7c | 7d | 7e |

### Table Columns

| Col A | ``Feature[APPLIC_1]``Col B |``End Feature`` Col C | Col D ``Feature[APPLIC_2]``| Col E ``End Feature``|
|---|``Feature[APPLIC_1]``---|``End Feature``---|---``Feature[APPLIC_2]``|---:``End Feature``|
| 1a | ``Feature[APPLIC_1]``1b |``End Feature`` 1c | 1d ``Feature[APPLIC_2]``| 1e ``End Feature``|
| 2a | ``Feature[APPLIC_1]``2b |``End Feature`` 2c | 2d ``Feature[APPLIC_2]``| 2e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
# Overview

This is a test file for using PLE

## Feature Tests

``Feature[APPLIC_1=Included]``
Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``End Feature``

``Feature[APPLIC_1=Included]``
Included Text
``End Feature``

``Feature[APPLIC_1=Excluded]``
Excluded Text
``End Feature``


## Else Tests

``Feature[APPLIC_1]``
Tag 1
``Feature Else``
Not Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``Feature Else``
Not Tag 2
``End Feature``

## Boolean Tests

``Feature[APPLIC_1 | APPLIC_2]``
Included `OR` Excluded Feature
``End Feature``

``Feature[APPLIC_1 & APPLIC_2]``
Included `AND` Excluded Feature
``End Feature``

## Substitution Tests

``Eval[SUB_1]``
``Eval[SUB_2]``

- ``Eval[SUB_1]``
- ``Eval[SUB_2]``

## List Tests

``Feature[APPLIC_1]``
1. Tag 1
``End Feature``
2. Common Row 1
``Feature[APPLIC_1]``
    - Tag 2.1
``End Feature``
``Feature[APPLIC_2]``
3. Tag 2
    - Tag 2 Subbullet
``End Feature``
4. Common Row 2

## Nested Tests

``Feature[APPLIC_1]``
Level 1

``Feature[APPLIC_2]``
Level 2
``End Feature``
``End Feature``

## Feature and Substitution Test

``Feature[APPLIC_1]``
Tag1

``Eval[SUB_1]``
``End Feature``

## Tables

### Table Rows

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
``Feature[APPLIC_1]``| 0a | 0b | 0c | 0d  | 0e |``End Feature``
| 1a | 1b | 1c | 1d | 1e |
``Feature[APPLIC_2]``| 2a | 2b | 2c | 2d  | 2e |``End Feature``
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c | 4d | 4e``End Feature`` |
| 5a | 5b | 5c | 5d | 5e |

### Table Cells

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
| 1a | 1b | 1c | 1d | 1e |
| ``Feature[APPLIC_1]``2a | 2b | 2c | 2d | 2e``End Feature`` |
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c``End Feature`` | 4d | 4e |
| 5a | 5b | 5c | 5d | 5e |
| ``Feature[APPLIC_1]``6a``End Feature`` | 6b | 6c | 6d | ``Feature[APPLIC_2]``6e``End Feature`` |
| 7a | 7b | 7c | 7d | 7e |

### Table Columns

| Col A | ``Feature[APPLIC_1]``Col B |``End Feature`` Col C | Col D ``Feature[APPLIC_2]``| Col E ``End Feature``|
|---|``Feature[APPLIC_1]``---|``End Feature``---|---``Feature[APPLIC_2]``|---:``End Feature``|
| 1a | ``Feature[APPLIC_1]``1b |``End Feature`` 1c | 1d ``Feature[APPLIC_2]``| 1e ``End Feature``|
| 2a | ``Feature[APPLIC_1]``2b |``End Feature`` 2c | 2d ``Feature[APPLIC_2]``| 2e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
# Overview

This is a test file for using PLE

## Feature Tests

``Feature[APPLIC_1=Included]``
Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``End Feature``

``Feature[APPLIC_1=Included]``
Included Text
``End Feature``

``Feature[APPLIC_1=Excluded]``
Excluded Text
``End Feature``


## Else Tests

``Feature[APPLIC_1]``
Tag 1
``Feature Else``
Not Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``Feature Else``
Not Tag 2
``End Feature``

## Boolean Tests

``Feature[APPLIC_1 | APPLIC_2]``
Included `OR` Excluded Feature
``End Feature``

``Feature[APPLIC_1 & APPLIC_2]``
Included `AND` Excluded Feature
``End Feature``

## Substitution Tests

``Eval[SUB_1]``
``Eval[SUB_2]``

- ``Eval[SUB_1]``
- ``Eval[SUB_2]``

## List Tests

``Feature[APPLIC_1]``
1. Tag 1
``End Feature``
2. Common Row 1
``Feature[APPLIC_1]``
    - Tag 2.1
``End Feature``
``Feature[APPLIC_2]``
3. Tag 2
    - Tag 2 Subbullet
``End Feature``
4. Common Row 2

## Nested Tests

``Feature[APPLIC_1]``
Level 1

``Feature[APPLIC_2]``
Level 2
``End Feature``
``End Feature``

## Feature and Substitution Test

``Feature[APPLIC_1]``
Tag1

``Eval[SUB_1]``
``End Feature``

## Tables

### Table Rows

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
``Feature[APPLIC_1]``| 0a | 0b | 0c | 0d  | 0e |``End Feature``
| 1a | 1b | 1c | 1d | 1e |
``Feature[APPLIC_2]``| 2a | 2b | 2c | 2d  | 2e |``End Feature``
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c | 4d | 4e``End Feature`` |
| 5a | 5b | 5c | 5d | 5e |

### Table Cells

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
| 1a | 1b | 1c | 1d | 1e |
| ``Feature[APPLIC_1]``2a | 2b | 2c | 2d | 2e``End Feature`` |
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c``End Feature`` | 4d | 4e |
| 5a | 5b | 5c | 5d | 5e |
| ``Feature[APPLIC_1]``6a``End Feature`` | 6b | 6c | 6d | ``Feature[APPLIC_2]``6e``End Feature`` |
| 7a | 7b | 7c | 7d | 7e |

### Table Columns

| Col A | ``Feature[APPLIC_1]``Col B |``End Feature`` Col C | Col D ``Feature[APPLIC_2]``| Col E ``End Feature``|
|---|``Feature[APPLIC_1]``---|``End Feature``---|---``Feature[APPLIC_2]``|---:``End Feature``|
| 1a | ``Feature[APPLIC_1]``1b |``End Feature`` 1c | 1d ``Feature[APPLIC_2]``| 1e ``End Feature``|
| 2a | ``Feature[APPLIC_1]``2b |``End Feature`` 2c | 2d ``Feature[APPLIC_2]``| 2e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
# Overview

This is a test file for using PLE

## Feature Tests

``Feature[APPLIC_1=Included]``
Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``End Feature``

``Feature[APPLIC_1=Included]``
Included Text
``End Feature``

``Feature[APPLIC_1=Excluded]``
Excluded Text
``End Feature``


## Else Tests

``Feature[APPLIC_1]``
Tag 1
``Feature Else``
Not Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``Feature Else``
Not Tag 2
``End Feature``

## Boolean Tests

``Feature[APPLIC_1 | APPLIC_2]``
Included `OR` Excluded Feature
``End Feature``

``Feature[APPLIC_1 & APPLIC_2]``
Included `AND` Excluded Feature
``End Feature``

## Substitution Tests

``Eval[SUB_1]``
``Eval[SUB_2]``

- ``Eval[SUB_1]``
- ``Eval[SUB_2]``

## List Tests

``Feature[APPLIC_1]``
1. Tag 1
``End Feature``
2. Common Row 1
``Feature[APPLIC_1]``
    - Tag 2.1
``End Feature``
``Feature[APPLIC_2]``
3. Tag 2
    - Tag 2 Subbullet
``End Feature``
4. Common Row 2

## Nested Tests

``Feature[APPLIC_1]``
Level 1

``Feature[APPLIC_2]``
Level 2
``End Feature``
``End Feature``

## Feature and Substitution Test

``Feature[APPLIC_1]``
Tag1

``Eval[SUB_1]``
``End Feature``

## Tables

### Table Rows

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
``Feature[APPLIC_1]``| 0a | 0b | 0c | 0d  | 0e |``End Feature``
| 1a | 1b | 1c | 1d | 1e |
``Feature[APPLIC_2]``| 2a | 2b | 2c | 2d  | 2e |``End Feature``
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c | 4d | 4e``End Feature`` |
| 5a | 5b | 5c | 5d | 5e |

### Table Cells

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
| 1a | 1b | 1c | 1d | 1e |
| ``Feature[APPLIC_1]``2a | 2b | 2c | 2d | 2e``End Feature`` |
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c``End Feature`` | 4d | 4e |
| 5a | 5b | 5c | 5d | 5e |
| ``Feature[APPLIC_1]``6a``End Feature`` | 6b | 6c | 6d | ``Feature[APPLIC_2]``6e``End Feature`` |
| 7a | 7b | 7c | 7d | 7e |

### Table Columns

| Col A | ``Feature[APPLIC_1]``Col B |``End Feature`` Col C | Col D ``Feature[APPLIC_2]``| Col E ``End Feature``|
|---|``Feature[APPLIC_1]``---|``End Feature``---|---``Feature[APPLIC_2]``|---:``End Feature``|
| 1a | ``Feature[APPLIC_1]``1b |``End Feature`` 1c | 1d ``Feature[APPLIC_2]``| 1e ``End Feature``|
| 2a | ``Feature[APPLIC_1]``2b |``End Feature`` 2c | 2d ``Feature[APPLIC_2]``| 2e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
# Overview

This is a test file for using PLE

## Feature Tests

``Feature[APPLIC_1=Included]``
Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``End Feature``

``Feature[APPLIC_1=Included]``
Included Text
``End Feature``

``Feature[APPLIC_1=Excluded]``
Excluded Text
``End Feature``


## Else Tests

``Feature[APPLIC_1]``
Tag 1
``Feature Else``
Not Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``Feature Else``
Not Tag 2
``End Feature``

## Boolean Tests

``Feature[APPLIC_1 | APPLIC_2]``
Included `OR` Excluded Feature
``End Feature``

``Feature[APPLIC_1 & APPLIC_2]``
Included `AND` Excluded Feature
``End Feature``

## Substitution Tests

``Eval[SUB_1]``
``Eval[SUB_2]``

- ``Eval[SUB_1]``
- ``Eval[SUB_2]``

## List Tests

``Feature[APPLIC_1]``
1. Tag 1
``End Feature``
2. Common Row 1
``Feature[APPLIC_1]``
    - Tag 2.1
``End Feature``
``Feature[APPLIC_2]``
3. Tag 2
    - Tag 2 Subbullet
``End Feature``
4. Common Row 2

## Nested Tests

``Feature[APPLIC_1]``
Level 1

``Feature[APPLIC_2]``
Level 2
``End Feature``
``End Feature``

## Feature and Substitution Test

``Feature[APPLIC_1]``
Tag1

``Eval[SUB_1]``
``End Feature``

## Tables

### Table Rows

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
``Feature[APPLIC_1]``| 0a | 0b | 0c | 0d  | 0e |``End Feature``
| 1a | 1b | 1c | 1d | 1e |
``Feature[APPLIC_2]``| 2a | 2b | 2c | 2d  | 2e |``End Feature``
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c | 4d | 4e``End Feature`` |
| 5a | 5b | 5c | 5d | 5e |

### Table Cells

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
| 1a | 1b | 1c | 1d | 1e |
| ``Feature[APPLIC_1]``2a | 2b | 2c | 2d | 2e``End Feature`` |
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c``End Feature`` | 4d | 4e |
| 5a | 5b | 5c | 5d | 5e |
| ``Feature[APPLIC_1]``6a``End Feature`` | 6b | 6c | 6d | ``Feature[APPLIC_2]``6e``End Feature`` |
| 7a | 7b | 7c | 7d | 7e |

### Table Columns

| Col A | ``Feature[APPLIC_1]``Col B |``End Feature`` Col C | Col D ``Feature[APPLIC_2]``| Col E ``End Feature``|
|---|``Feature[APPLIC_1]``---|``End Feature``---|---``Feature[APPLIC_2]``|---:``End Feature``|
| 1a | ``Feature[APPLIC_1]``1b |``End Feature`` 1c | 1d ``Feature[APPLIC_2]``| 1e ``End Feature``|
| 2a | ``Feature[APPLIC_1]``2b |``End Feature`` 2c | 2d ``Feature[APPLIC_2]``| 2e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
# Overview

This is a test file for using PLE

## Feature Tests

``Feature[APPLIC_1=Included]``
Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``End Feature``

``Feature[APPLIC_1=Included]``
Included Text
``End Feature``

``Feature[APPLIC_1=Excluded]``
Excluded Text
``End Feature``


## Else Tests

``Feature[APPLIC_1]``
Tag 1
``Feature Else``
Not Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``Feature Else``
Not Tag 2
``End Feature``

## Boolean Tests

``Feature[APPLIC_1 | APPLIC_2]``
Included `OR` Excluded Feature
``End Feature``

``Feature[APPLIC_1 & APPLIC_2]``
Included `AND` Excluded Feature
``End Feature``

## Substitution Tests

``Eval[SUB_1]``
``Eval[SUB_2]``

- ``Eval[SUB_1]``
- ``Eval[SUB_2]``

## List Tests

``Feature[APPLIC_1]``
1. Tag 1
``End Feature``
2. Common Row 1
``Feature[APPLIC_1]``
    - Tag 2.1
``End Feature``
``Feature[APPLIC_2]``
3. Tag 2
    - Tag 2 Subbullet
``End Feature``
4. Common Row 2

## Nested Tests

``Feature[APPLIC_1]``
Level 1

``Feature[APPLIC_2]``
Level 2
``End Feature``
``End Feature``

## Feature and Substitution Test

``Feature[APPLIC_1]``
Tag1

``Eval[SUB_1]``
``End Feature``

## Tables

### Table Rows

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
``Feature[APPLIC_1]``| 0a | 0b | 0c | 0d  | 0e |``End Feature``
| 1a | 1b | 1c | 1d | 1e |
``Feature[APPLIC_2]``| 2a | 2b | 2c | 2d  | 2e |``End Feature``
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c | 4d | 4e``End Feature`` |
| 5a | 5b | 5c | 5d | 5e |

### Table Cells

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
| 1a | 1b | 1c | 1d | 1e |
| ``Feature[APPLIC_1]``2a | 2b | 2c | 2d | 2e``End Feature`` |
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c``End Feature`` | 4d | 4e |
| 5a | 5b | 5c | 5d | 5e |
| ``Feature[APPLIC_1]``6a``End Feature`` | 6b | 6c | 6d | ``Feature[APPLIC_2]``6e``End Feature`` |
| 7a | 7b | 7c | 7d | 7e |

### Table Columns

| Col A | ``Feature[APPLIC_1]``Col B |``End Feature`` Col C | Col D ``Feature[APPLIC_2]``| Col E ``End Feature``|
|---|``Feature[APPLIC_1]``---|``End Feature``---|---``Feature[APPLIC_2]``|---:``End Feature``|
| 1a | ``Feature[APPLIC_1]``1b |``End Feature`` 1c | 1d ``Feature[APPLIC_2]``| 1e ``End Feature``|
| 2a | ``Feature[APPLIC_1]``2b |``End Feature`` 2c | 2d ``Feature[APPLIC_2]``| 2e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
# Overview

This is a test file for using PLE

## Feature Tests

``Feature[APPLIC_1=Included]``
Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``End Feature``

``Feature[APPLIC_1=Included]``
Included Text
``End Feature``

``Feature[APPLIC_1=Excluded]``
Excluded Text
``End Feature``


## Else Tests

``Feature[APPLIC_1]``
Tag 1
``Feature Else``
Not Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``Feature Else``
Not Tag 2
``End Feature``

## Boolean Tests

``Feature[APPLIC_1 | APPLIC_2]``
Included `OR` Excluded Feature
``End Feature``

``Feature[APPLIC_1 & APPLIC_2]``
Included `AND` Excluded Feature
``End Feature``

## Substitution Tests

``Eval[SUB_1]``
``Eval[SUB_2]``

- ``Eval[SUB_1]``
- ``Eval[SUB_2]``

## List Tests

``Feature[APPLIC_1]``
1. Tag 1
``End Feature``
2. Common Row 1
``Feature[APPLIC_1]``
    - Tag 2.1
``End Feature``
``Feature[APPLIC_2]``
3. Tag 2
    - Tag 2 Subbullet
``End Feature``
4. Common Row 2

## Nested Tests

``Feature[APPLIC_1]``
Level 1

``Feature[APPLIC_2]``
Level 2
``End Feature``
``End Feature``

## Feature and Substitution Test

``Feature[APPLIC_1]``
Tag1

``Eval[SUB_1]``
``End Feature``

## Tables

### Table Rows

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
``Feature[APPLIC_1]``| 0a | 0b | 0c | 0d  | 0e |``End Feature``
| 1a | 1b | 1c | 1d | 1e |
``Feature[APPLIC_2]``| 2a | 2b | 2c | 2d  | 2e |``End Feature``
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c | 4d | 4e``End Feature`` |
| 5a | 5b | 5c | 5d | 5e |

### Table Cells

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
| 1a | 1b | 1c | 1d | 1e |
| ``Feature[APPLIC_1]``2a | 2b | 2c | 2d | 2e``End Feature`` |
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c``End Feature`` | 4d | 4e |
| 5a | 5b | 5c | 5d | 5e |
| ``Feature[APPLIC_1]``6a``End Feature`` | 6b | 6c | 6d | ``Feature[APPLIC_2]``6e``End Feature`` |
| 7a | 7b | 7c | 7d | 7e |

### Table Columns

| Col A | ``Feature[APPLIC_1]``Col B |``End Feature`` Col C | Col D ``Feature[APPLIC_2]``| Col E ``End Feature``|
|---|``Feature[APPLIC_1]``---|``End Feature``---|---``Feature[APPLIC_2]``|---:``End Feature``|
| 1a | ``Feature[APPLIC_1]``1b |``End Feature`` 1c | 1d ``Feature[APPLIC_2]``| 1e ``End Feature``|
| 2a | ``Feature[APPLIC_1]``2b |``End Feature`` 2c | 2d ``Feature[APPLIC_2]``| 2e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
# Overview

This is a test file for using PLE

## Feature Tests

``Feature[APPLIC_1=Included]``
Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``End Feature``

``Feature[APPLIC_1=Included]``
Included Text
``End Feature``

``Feature[APPLIC_1=Excluded]``
Excluded Text
``End Feature``


## Else Tests

``Feature[APPLIC_1]``
Tag 1
``Feature Else``
Not Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``Feature Else``
Not Tag 2
``End Feature``

## Boolean Tests

``Feature[APPLIC_1 | APPLIC_2]``
Included `OR` Excluded Feature
``End Feature``

``Feature[APPLIC_1 & APPLIC_2]``
Included `AND` Excluded Feature
``End Feature``

## Substitution Tests

``Eval[SUB_1]``
``Eval[SUB_2]``

- ``Eval[SUB_1]``
- ``Eval[SUB_2]``

## List Tests

``Feature[APPLIC_1]``
1. Tag 1
``End Feature``
2. Common Row 1
``Feature[APPLIC_1]``
    - Tag 2.1
``End Feature``
``Feature[APPLIC_2]``
3. Tag 2
    - Tag 2 Subbullet
``End Feature``
4. Common Row 2

## Nested Tests

``Feature[APPLIC_1]``
Level 1

``Feature[APPLIC_2]``
Level 2
``End Feature``
``End Feature``

## Feature and Substitution Test

``Feature[APPLIC_1]``
Tag1

``Eval[SUB_1]``
``End Feature``

## Tables

### Table Rows

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
``Feature[APPLIC_1]``| 0a | 0b | 0c | 0d  | 0e |``End Feature``
| 1a | 1b | 1c | 1d | 1e |
``Feature[APPLIC_2]``| 2a | 2b | 2c | 2d  | 2e |``End Feature``
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c | 4d | 4e``End Feature`` |
| 5a | 5b | 5c | 5d | 5e |

### Table Cells

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
| 1a | 1b | 1c | 1d | 1e |
| ``Feature[APPLIC_1]``2a | 2b | 2c | 2d | 2e``End Feature`` |
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c``End Feature`` | 4d | 4e |
| 5a | 5b | 5c | 5d | 5e |
| ``Feature[APPLIC_1]``6a``End Feature`` | 6b | 6c | 6d | ``Feature[APPLIC_2]``6e``End Feature`` |
| 7a | 7b | 7c | 7d | 7e |

### Table Columns

| Col A | ``Feature[APPLIC_1]``Col B |``End Feature`` Col C | Col D ``Feature[APPLIC_2]``| Col E ``End Feature``|
|---|``Feature[APPLIC_1]``---|``End Feature``---|---``Feature[APPLIC_2]``|---:``End Feature``|
| 1a | ``Feature[APPLIC_1]``1b |``End Feature`` 1c | 1d ``Feature[APPLIC_2]``| 1e ``End Feature``|
| 2a | ``Feature[APPLIC_1]``2b |``End Feature`` 2c | 2d ``Feature[APPLIC_2]``| 2e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
# Overview

This is a test file for using PLE

## Feature Tests

``Feature[APPLIC_1=Included]``
Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``End Feature``

``Feature[APPLIC_1=Included]``
Included Text
``End Feature``

``Feature[APPLIC_1=Excluded]``
Excluded Text
``End Feature``


## Else Tests

``Feature[APPLIC_1]``
Tag 1
``Feature Else``
Not Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``Feature Else``
Not Tag 2
``End Feature``

## Boolean Tests

``Feature[APPLIC_1 | APPLIC_2]``
Included `OR` Excluded Feature
``End Feature``

``Feature[APPLIC_1 & APPLIC_2]``
Included `AND` Excluded Feature
``End Feature``

## Substitution Tests

``Eval[SUB_1]``
``Eval[SUB_2]``

- ``Eval[SUB_1]``
- ``Eval[SUB_2]``

## List Tests

``Feature[APPLIC_1]``
1. Tag 1
``End Feature``
2. Common Row 1
``Feature[APPLIC_1]``
    - Tag 2.1
``End Feature``
``Feature[APPLIC_2]``
3. Tag 2
    - Tag 2 Subbullet
``End Feature``
4. Common Row 2

## Nested Tests

``Feature[APPLIC_1]``
Level 1

``Feature[APPLIC_2]``
Level 2
``End Feature``
``End Feature``

## Feature and Substitution Test

``Feature[APPLIC_1]``
Tag1

``Eval[SUB_1]``
``End Feature``

## Tables

### Table Rows

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
``Feature[APPLIC_1]``| 0a | 0b | 0c | 0d  | 0e |``End Feature``
| 1a | 1b | 1c | 1d | 1e |
``Feature[APPLIC_2]``| 2a | 2b | 2c | 2d  | 2e |``End Feature``
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c | 4d | 4e``End Feature`` |
| 5a | 5b | 5c | 5d | 5e |

### Table Cells

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
| 1a | 1b | 1c | 1d | 1e |
| ``Feature[APPLIC_1]``2a | 2b | 2c | 2d | 2e``End Feature`` |
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c``End Feature`` | 4d | 4e |
| 5a | 5b | 5c | 5d | 5e |
| ``Feature[APPLIC_1]``6a``End Feature`` | 6b | 6c | 6d | ``Feature[APPLIC_2]``6e``End Feature`` |
| 7a | 7b | 7c | 7d | 7e |

### Table Columns

| Col A | ``Feature[APPLIC_1]``Col B |``End Feature`` Col C | Col D ``Feature[APPLIC_2]``| Col E ``End Feature``|
|---|``Feature[APPLIC_1]``---|``End Feature``---|---``Feature[APPLIC_2]``|---:``End Feature``|
| 1a | ``Feature[APPLIC_1]``1b |``End Feature`` 1c | 1d ``Feature[APPLIC_2]``| 1e ``End Feature``|
| 2a | ``Feature[APPLIC_1]``2b |``End Feature`` 2c | 2d ``Feature[APPLIC_2]``| 2e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
# Overview

This is a test file for using PLE

## Feature Tests

``Feature[APPLIC_1=Included]``
Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``End Feature``

``Feature[APPLIC_1=Included]``
Included Text
``End Feature``

``Feature[APPLIC_1=Excluded]``
Excluded Text
``End Feature``


## Else Tests

``Feature[APPLIC_1]``
Tag 1
``Feature Else``
Not Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``Feature Else``
Not Tag 2
``End Feature``

## Boolean Tests

``Feature[APPLIC_1 | APPLIC_2]``
Included `OR` Excluded Feature
``End Feature``

``Feature[APPLIC_1 & APPLIC_2]``
Included `AND` Excluded Feature
``End Feature``

## Substitution Tests

``Eval[SUB_1]``
``Eval[SUB_2]``

- ``Eval[SUB_1]``
- ``Eval[SUB_2]``

## List Tests

``Feature[APPLIC_1]``
1. Tag 1
``End Feature``
2. Common Row 1
``Feature[APPLIC_1]``
    - Tag 2.1
``End Feature``
``Feature[APPLIC_2]``
3. Tag 2
    - Tag 2 Subbullet
``End Feature``
4. Common Row 2

## Nested Tests

``Feature[APPLIC_1]``
Level 1

``Feature[APPLIC_2]``
Level 2
``End Feature``
``End Feature``

## Feature and Substitution Test

``Feature[APPLIC_1]``
Tag1

``Eval[SUB_1]``
``End Feature``

## Tables

### Table Rows

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
``Feature[APPLIC_1]``| 0a | 0b | 0c | 0d  | 0e |``End Feature``
| 1a | 1b | 1c | 1d | 1e |
``Feature[APPLIC_2]``| 2a | 2b | 2c | 2d  | 2e |``End Feature``
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c | 4d | 4e``End Feature`` |
| 5a | 5b | 5c | 5d | 5e |

### Table Cells

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
| 1a | 1b | 1c | 1d | 1e |
| ``Feature[APPLIC_1]``2a | 2b | 2c | 2d | 2e``End Feature`` |
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c``End Feature`` | 4d | 4e |
| 5a | 5b | 5c | 5d | 5e |
| ``Feature[APPLIC_1]``6a``End Feature`` | 6b | 6c | 6d | ``Feature[APPLIC_2]``6e``End Feature`` |
| 7a | 7b | 7c | 7d | 7e |

### Table Columns

| Col A | ``Feature[APPLIC_1]``Col B |``End Feature`` Col C | Col D ``Feature[APPLIC_2]``| Col E ``End Feature``|
|---|``Feature[APPLIC_1]``---|``End Feature``---|---``Feature[APPLIC_2]``|---:``End Feature``|
| 1a | ``Feature[APPLIC_1]``1b |``End Feature`` 1c | 1d ``Feature[APPLIC_2]``| 1e ``End Feature``|
| 2a | ``Feature[APPLIC_1]``2b |``End Feature`` 2c | 2d ``Feature[APPLIC_2]``| 2e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
# Overview

This is a test file for using PLE

## Feature Tests

``Feature[APPLIC_1=Included]``
Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``End Feature``

``Feature[APPLIC_1=Included]``
Included Text
``End Feature``

``Feature[APPLIC_1=Excluded]``
Excluded Text
``End Feature``


## Else Tests

``Feature[APPLIC_1]``
Tag 1
``Feature Else``
Not Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``Feature Else``
Not Tag 2
``End Feature``

## Boolean Tests

``Feature[APPLIC_1 | APPLIC_2]``
Included `OR` Excluded Feature
``End Feature``

``Feature[APPLIC_1 & APPLIC_2]``
Included `AND` Excluded Feature
``End Feature``

## Substitution Tests

``Eval[SUB_1]``
``Eval[SUB_2]``

- ``Eval[SUB_1]``
- ``Eval[SUB_2]``

## List Tests

``Feature[APPLIC_1]``
1. Tag 1
``End Feature``
2. Common Row 1
``Feature[APPLIC_1]``
    - Tag 2.1
``End Feature``
``Feature[APPLIC_2]``
3. Tag 2
    - Tag 2 Subbullet
``End Feature``
4. Common Row 2

## Nested Tests

``Feature[APPLIC_1]``
Level 1

``Feature[APPLIC_2]``
Level 2
``End Feature``
``End Feature``

## Feature and Substitution Test

``Feature[APPLIC_1]``
Tag1

``Eval[SUB_1]``
``End Feature``

## Tables

### Table Rows

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
``Feature[APPLIC_1]``| 0a | 0b | 0c | 0d  | 0e |``End Feature``
| 1a | 1b | 1c | 1d | 1e |
``Feature[APPLIC_2]``| 2a | 2b | 2c | 2d  | 2e |``End Feature``
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c | 4d | 4e``End Feature`` |
| 5a | 5b | 5c | 5d | 5e |

### Table Cells

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
| 1a | 1b | 1c | 1d | 1e |
| ``Feature[APPLIC_1]``2a | 2b | 2c | 2d | 2e``End Feature`` |
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c``End Feature`` | 4d | 4e |
| 5a | 5b | 5c | 5d | 5e |
| ``Feature[APPLIC_1]``6a``End Feature`` | 6b | 6c | 6d | ``Feature[APPLIC_2]``6e``End Feature`` |
| 7a | 7b | 7c | 7d | 7e |

### Table Columns

| Col A | ``Feature[APPLIC_1]``Col B |``End Feature`` Col C | Col D ``Feature[APPLIC_2]``| Col E ``End Feature``|
|---|``Feature[APPLIC_1]``---|``End Feature``---|---``Feature[APPLIC_2]``|---:``End Feature``|
| 1a | ``Feature[APPLIC_1]``1b |``End Feature`` 1c | 1d ``Feature[APPLIC_2]``| 1e ``End Feature``|
| 2a | ``Feature[APPLIC_1]``2b |``End Feature`` 2c | 2d ``Feature[APPLIC_2]``| 2e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
# Overview

This is a test file for using PLE

## Feature Tests

``Feature[APPLIC_1=Included]``
Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``End Feature``

``Feature[APPLIC_1=Included]``
Included Text
``End Feature``

``Feature[APPLIC_1=Excluded]``
Excluded Text
``End Feature``


## Else Tests

``Feature[APPLIC_1]``
Tag 1
``Feature Else``
Not Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``Feature Else``
Not Tag 2
``End Feature``

## Boolean Tests

``Feature[APPLIC_1 | APPLIC_2]``
Included `OR` Excluded Feature
``End Feature``

``Feature[APPLIC_1 & APPLIC_2]``
Included `AND` Excluded Feature
``End Feature``

## Substitution Tests

``Eval[SUB_1]``
``Eval[SUB_2]``

- ``Eval[SUB_1]``
- ``Eval[SUB_2]``

## List Tests

``Feature[APPLIC_1]``
1. Tag 1
``End Feature``
2. Common Row 1
``Feature[APPLIC_1]``
    - Tag 2.1
``End Feature``
``Feature[APPLIC_2]``
3. Tag 2
    - Tag 2 Subbullet
``End Feature``
4. Common Row 2

## Nested Tests

``Feature[APPLIC_1]``
Level 1

``Feature[APPLIC_2]``
Level 2
``End Feature``
``End Feature``

## Feature and Substitution Test

``Feature[APPLIC_1]``
Tag1

``Eval[SUB_1]``
``End Feature``

## Tables

### Table Rows

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
``Feature[APPLIC_1]``| 0a | 0b | 0c | 0d  | 0e |``End Feature``
| 1a | 1b | 1c | 1d | 1e |
``Feature[APPLIC_2]``| 2a | 2b | 2c | 2d  | 2e |``End Feature``
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c | 4d | 4e``End Feature`` |
| 5a | 5b | 5c | 5d | 5e |

### Table Cells

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
| 1a | 1b | 1c | 1d | 1e |
| ``Feature[APPLIC_1]``2a | 2b | 2c | 2d | 2e``End Feature`` |
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c``End Feature`` | 4d | 4e |
| 5a | 5b | 5c | 5d | 5e |
| ``Feature[APPLIC_1]``6a``End Feature`` | 6b | 6c | 6d | ``Feature[APPLIC_2]``6e``End Feature`` |
| 7a | 7b | 7c | 7d | 7e |

### Table Columns

| Col A | ``Feature[APPLIC_1]``Col B |``End Feature`` Col C | Col D ``Feature[APPLIC_2]``| Col E ``End Feature``|
|---|``Feature[APPLIC_1]``---|``End Feature``---|---``Feature[APPLIC_2]``|---:``End Feature``|
| 1a | ``Feature[APPLIC_1]``1b |``End Feature`` 1c | 1d ``Feature[APPLIC_2]``| 1e ``End Feature``|
| 2a | ``Feature[APPLIC_1]``2b |``End Feature`` 2c | 2d ``Feature[APPLIC_2]``| 2e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
# Overview

This is a test file for using PLE

## Feature Tests

``Feature[APPLIC_1=Included]``
Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``End Feature``

``Feature[APPLIC_1=Included]``
Included Text
``End Feature``

``Feature[APPLIC_1=Excluded]``
Excluded Text
``End Feature``


## Else Tests

``Feature[APPLIC_1]``
Tag 1
``Feature Else``
Not Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``Feature Else``
Not Tag 2
``End Feature``

## Boolean Tests

``Feature[APPLIC_1 | APPLIC_2]``
Included `OR` Excluded Feature
``End Feature``

``Feature[APPLIC_1 & APPLIC_2]``
Included `AND` Excluded Feature
``End Feature``

## Substitution Tests

``Eval[SUB_1]``
``Eval[SUB_2]``

- ``Eval[SUB_1]``
- ``Eval[SUB_2]``

## List Tests

``Feature[APPLIC_1]``
1. Tag 1
``End Feature``
2. Common Row 1
``Feature[APPLIC_1]``
    - Tag 2.1
``End Feature``
``Feature[APPLIC_2]``
3. Tag 2
    - Tag 2 Subbullet
``End Feature``
4. Common Row 2

## Nested Tests

``Feature[APPLIC_1]``
Level 1

``Feature[APPLIC_2]``
Level 2
``End Feature``
``End Feature``

## Feature and Substitution Test

``Feature[APPLIC_1]``
Tag1

``Eval[SUB_1]``
``End Feature``

## Tables

### Table Rows

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
``Feature[APPLIC_1]``| 0a | 0b | 0c | 0d  | 0e |``End Feature``
| 1a | 1b | 1c | 1d | 1e |
``Feature[APPLIC_2]``| 2a | 2b | 2c | 2d  | 2e |``End Feature``
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c | 4d | 4e``End Feature`` |
| 5a | 5b | 5c | 5d | 5e |

### Table Cells

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
| 1a | 1b | 1c | 1d | 1e |
| ``Feature[APPLIC_1]``2a | 2b | 2c | 2d | 2e``End Feature`` |
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c``End Feature`` | 4d | 4e |
| 5a | 5b | 5c | 5d | 5e |
| ``Feature[APPLIC_1]``6a``End Feature`` | 6b | 6c | 6d | ``Feature[APPLIC_2]``6e``End Feature`` |
| 7a | 7b | 7c | 7d | 7e |

### Table Columns

| Col A | ``Feature[APPLIC_1]``Col B |``End Feature`` Col C | Col D ``Feature[APPLIC_2]``| Col E ``End Feature``|
|---|``Feature[APPLIC_1]``---|``End Feature``---|---``Feature[APPLIC_2]``|---:``End Feature``|
| 1a | ``Feature[APPLIC_1]``1b |``End Feature`` 1c | 1d ``Feature[APPLIC_2]``| 1e ``End Feature``|
| 2a | ``Feature[APPLIC_1]``2b |``End Feature`` 2c | 2d ``Feature[APPLIC_2]``| 2e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
# Overview

This is a test file for using PLE

## Feature Tests

``Feature[APPLIC_1=Included]``
Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``End Feature``

``Feature[APPLIC_1=Included]``
Included Text
``End Feature``

``Feature[APPLIC_1=Excluded]``
Excluded Text
``End Feature``


## Else Tests

``Feature[APPLIC_1]``
Tag 1
``Feature Else``
Not Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``Feature Else``
Not Tag 2
``End Feature``

## Boolean Tests

``Feature[APPLIC_1 | APPLIC_2]``
Included `OR` Excluded Feature
``End Feature``

``Feature[APPLIC_1 & APPLIC_2]``
Included `AND` Excluded Feature
``End Feature``

## Substitution Tests

``Eval[SUB_1]``
``Eval[SUB_2]``

- ``Eval[SUB_1]``
- ``Eval[SUB_2]``

## List Tests

``Feature[APPLIC_1]``
1. Tag 1
``End Feature``
2. Common Row 1
``Feature[APPLIC_1]``
    - Tag 2.1
``End Feature``
``Feature[APPLIC_2]``
3. Tag 2
    - Tag 2 Subbullet
``End Feature``
4. Common Row 2

## Nested Tests

``Feature[APPLIC_1]``
Level 1

``Feature[APPLIC_2]``
Level 2
``End Feature``
``End Feature``

## Feature and Substitution Test

``Feature[APPLIC_1]``
Tag1

``Eval[SUB_1]``
``End Feature``

## Tables

### Table Rows

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
``Feature[APPLIC_1]``| 0a | 0b | 0c | 0d  | 0e |``End Feature``
| 1a | 1b | 1c | 1d | 1e |
``Feature[APPLIC_2]``| 2a | 2b | 2c | 2d  | 2e |``End Feature``
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c | 4d | 4e``End Feature`` |
| 5a | 5b | 5c | 5d | 5e |

### Table Cells

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
| 1a | 1b | 1c | 1d | 1e |
| ``Feature[APPLIC_1]``2a | 2b | 2c | 2d | 2e``End Feature`` |
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c``End Feature`` | 4d | 4e |
| 5a | 5b | 5c | 5d | 5e |
| ``Feature[APPLIC_1]``6a``End Feature`` | 6b | 6c | 6d | ``Feature[APPLIC_2]``6e``End Feature`` |
| 7a | 7b | 7c | 7d | 7e |

### Table Columns

| Col A | ``Feature[APPLIC_1]``Col B |``End Feature`` Col C | Col D ``Feature[APPLIC_2]``| Col E ``End Feature``|
|---|``Feature[APPLIC_1]``---|``End Feature``---|---``Feature[APPLIC_2]``|---:``End Feature``|
| 1a | ``Feature[APPLIC_1]``1b |``End Feature`` 1c | 1d ``Feature[APPLIC_2]``| 1e ``End Feature``|
| 2a | ``Feature[APPLIC_1]``2b |``End Feature`` 2c | 2d ``Feature[APPLIC_2]``| 2e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
# Overview

This is a test file for using PLE

## Feature Tests

``Feature[APPLIC_1=Included]``
Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``End Feature``

``Feature[APPLIC_1=Included]``
Included Text
``End Feature``

``Feature[APPLIC_1=Excluded]``
Excluded Text
``End Feature``


## Else Tests

``Feature[APPLIC_1]``
Tag 1
``Feature Else``
Not Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``Feature Else``
Not Tag 2
``End Feature``

## Boolean Tests

``Feature[APPLIC_1 | APPLIC_2]``
Included `OR` Excluded Feature
``End Feature``

``Feature[APPLIC_1 & APPLIC_2]``
Included `AND` Excluded Feature
``End Feature``

## Substitution Tests

``Eval[SUB_1]``
``Eval[SUB_2]``

- ``Eval[SUB_1]``
- ``Eval[SUB_2]``

## List Tests

``Feature[APPLIC_1]``
1. Tag 1
``End Feature``
2. Common Row 1
``Feature[APPLIC_1]``
    - Tag 2.1
``End Feature``
``Feature[APPLIC_2]``
3. Tag 2
    - Tag 2 Subbullet
``End Feature``
4. Common Row 2

## Nested Tests

``Feature[APPLIC_1]``
Level 1

``Feature[APPLIC_2]``
Level 2
``End Feature``
``End Feature``

## Feature and Substitution Test

``Feature[APPLIC_1]``
Tag1

``Eval[SUB_1]``
``End Feature``

## Tables

### Table Rows

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
``Feature[APPLIC_1]``| 0a | 0b | 0c | 0d  | 0e |``End Feature``
| 1a | 1b | 1c | 1d | 1e |
``Feature[APPLIC_2]``| 2a | 2b | 2c | 2d  | 2e |``End Feature``
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c | 4d | 4e``End Feature`` |
| 5a | 5b | 5c | 5d | 5e |

### Table Cells

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
| 1a | 1b | 1c | 1d | 1e |
| ``Feature[APPLIC_1]``2a | 2b | 2c | 2d | 2e``End Feature`` |
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c``End Feature`` | 4d | 4e |
| 5a | 5b | 5c | 5d | 5e |
| ``Feature[APPLIC_1]``6a``End Feature`` | 6b | 6c | 6d | ``Feature[APPLIC_2]``6e``End Feature`` |
| 7a | 7b | 7c | 7d | 7e |

### Table Columns

| Col A | ``Feature[APPLIC_1]``Col B |``End Feature`` Col C | Col D ``Feature[APPLIC_2]``| Col E ``End Feature``|
|---|``Feature[APPLIC_1]``---|``End Feature``---|---``Feature[APPLIC_2]``|---:``End Feature``|
| 1a | ``Feature[APPLIC_1]``1b |``End Feature`` 1c | 1d ``Feature[APPLIC_2]``| 1e ``End Feature``|
| 2a | ``Feature[APPLIC_1]``2b |``End Feature`` 2c | 2d ``Feature[APPLIC_2]``| 2e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
# Overview

This is a test file for using PLE

## Feature Tests

``Feature[APPLIC_1=Included]``
Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``End Feature``

``Feature[APPLIC_1=Included]``
Included Text
``End Feature``

``Feature[APPLIC_1=Excluded]``
Excluded Text
``End Feature``


## Else Tests

``Feature[APPLIC_1]``
Tag 1
``Feature Else``
Not Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``Feature Else``
Not Tag 2
``End Feature``

## Boolean Tests

``Feature[APPLIC_1 | APPLIC_2]``
Included `OR` Excluded Feature
``End Feature``

``Feature[APPLIC_1 & APPLIC_2]``
Included `AND` Excluded Feature
``End Feature``

## Substitution Tests

``Eval[SUB_1]``
``Eval[SUB_2]``

- ``Eval[SUB_1]``
- ``Eval[SUB_2]``

## List Tests

``Feature[APPLIC_1]``
1. Tag 1
``End Feature``
2. Common Row 1
``Feature[APPLIC_1]``
    - Tag 2.1
``End Feature``
``Feature[APPLIC_2]``
3. Tag 2
    - Tag 2 Subbullet
``End Feature``
4. Common Row 2

## Nested Tests

``Feature[APPLIC_1]``
Level 1

``Feature[APPLIC_2]``
Level 2
``End Feature``
``End Feature``

## Feature and Substitution Test

``Feature[APPLIC_1]``
Tag1

``Eval[SUB_1]``
``End Feature``

## Tables

### Table Rows

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
``Feature[APPLIC_1]``| 0a | 0b | 0c | 0d  | 0e |``End Feature``
| 1a | 1b | 1c | 1d | 1e |
``Feature[APPLIC_2]``| 2a | 2b | 2c | 2d  | 2e |``End Feature``
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c | 4d | 4e``End Feature`` |
| 5a | 5b | 5c | 5d | 5e |

### Table Cells

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
| 1a | 1b | 1c | 1d | 1e |
| ``Feature[APPLIC_1]``2a | 2b | 2c | 2d | 2e``End Feature`` |
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c``End Feature`` | 4d | 4e |
| 5a | 5b | 5c | 5d | 5e |
| ``Feature[APPLIC_1]``6a``End Feature`` | 6b | 6c | 6d | ``Feature[APPLIC_2]``6e``End Feature`` |
| 7a | 7b | 7c | 7d | 7e |

### Table Columns

| Col A | ``Feature[APPLIC_1]``Col B |``End Feature`` Col C | Col D ``Feature[APPLIC_2]``| Col E ``End Feature``|
|---|``Feature[APPLIC_1]``---|``End Feature``---|---``Feature[APPLIC_2]``|---:``End Feature``|
| 1a | ``Feature[APPLIC_1]``1b |``End Feature`` 1c | 1d ``Feature[APPLIC_2]``| 1e ``End Feature``|
| 2a | ``Feature[APPLIC_1]``2b |``End Feature`` 2c | 2d ``Feature[APPLIC_2]``| 2e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
# Overview

This is a test file for using PLE

## Feature Tests

``Feature[APPLIC_1=Included]``
Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``End Feature``

``Feature[APPLIC_1=Included]``
Included Text
``End Feature``

``Feature[APPLIC_1=Excluded]``
Excluded Text
``End Feature``


## Else Tests

``Feature[APPLIC_1]``
Tag 1
``Feature Else``
Not Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``Feature Else``
Not Tag 2
``End Feature``

## Boolean Tests

``Feature[APPLIC_1 | APPLIC_2]``
Included `OR` Excluded Feature
``End Feature``

``Feature[APPLIC_1 & APPLIC_2]``
Included `AND` Excluded Feature
``End Feature``

## Substitution Tests

``Eval[SUB_1]``
``Eval[SUB_2]``

- ``Eval[SUB_1]``
- ``Eval[SUB_2]``

## List Tests

``Feature[APPLIC_1]``
1. Tag 1
``End Feature``
2. Common Row 1
``Feature[APPLIC_1]``
    - Tag 2.1
``End Feature``
``Feature[APPLIC_2]``
3. Tag 2
    - Tag 2 Subbullet
``End Feature``
4. Common Row 2

## Nested Tests

``Feature[APPLIC_1]``
Level 1

``Feature[APPLIC_2]``
Level 2
``End Feature``
``End Feature``

## Feature and Substitution Test

``Feature[APPLIC_1]``
Tag1

``Eval[SUB_1]``
``End Feature``

## Tables

### Table Rows

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
``Feature[APPLIC_1]``| 0a | 0b | 0c | 0d  | 0e |``End Feature``
| 1a | 1b | 1c | 1d | 1e |
``Feature[APPLIC_2]``| 2a | 2b | 2c | 2d  | 2e |``End Feature``
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c | 4d | 4e``End Feature`` |
| 5a | 5b | 5c | 5d | 5e |

### Table Cells

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
| 1a | 1b | 1c | 1d | 1e |
| ``Feature[APPLIC_1]``2a | 2b | 2c | 2d | 2e``End Feature`` |
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c``End Feature`` | 4d | 4e |
| 5a | 5b | 5c | 5d | 5e |
| ``Feature[APPLIC_1]``6a``End Feature`` | 6b | 6c | 6d | ``Feature[APPLIC_2]``6e``End Feature`` |
| 7a | 7b | 7c | 7d | 7e |

### Table Columns

| Col A | ``Feature[APPLIC_1]``Col B |``End Feature`` Col C | Col D ``Feature[APPLIC_2]``| Col E ``End Feature``|
|---|``Feature[APPLIC_1]``---|``End Feature``---|---``Feature[APPLIC_2]``|---:``End Feature``|
| 1a | ``Feature[APPLIC_1]``1b |``End Feature`` 1c | 1d ``Feature[APPLIC_2]``| 1e ``End Feature``|
| 2a | ``Feature[APPLIC_1]``2b |``End Feature`` 2c | 2d ``Feature[APPLIC_2]``| 2e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
# Overview

This is a test file for using PLE

## Feature Tests

``Feature[APPLIC_1=Included]``
Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``End Feature``

``Feature[APPLIC_1=Included]``
Included Text
``End Feature``

``Feature[APPLIC_1=Excluded]``
Excluded Text
``End Feature``


## Else Tests

``Feature[APPLIC_1]``
Tag 1
``Feature Else``
Not Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``Feature Else``
Not Tag 2
``End Feature``

## Boolean Tests

``Feature[APPLIC_1 | APPLIC_2]``
Included `OR` Excluded Feature
``End Feature``

``Feature[APPLIC_1 & APPLIC_2]``
Included `AND` Excluded Feature
``End Feature``

## Substitution Tests

``Eval[SUB_1]``
``Eval[SUB_2]``

- ``Eval[SUB_1]``
- ``Eval[SUB_2]``

## List Tests

``Feature[APPLIC_1]``
1. Tag 1
``End Feature``
2. Common Row 1
``Feature[APPLIC_1]``
    - Tag 2.1
``End Feature``
``Feature[APPLIC_2]``
3. Tag 2
    - Tag 2 Subbullet
``End Feature``
4. Common Row 2

## Nested Tests

``Feature[APPLIC_1]``
Level 1

``Feature[APPLIC_2]``
Level 2
``End Feature``
``End Feature``

## Feature and Substitution Test

``Feature[APPLIC_1]``
Tag1

``Eval[SUB_1]``
``End Feature``

## Tables

### Table Rows

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
``Feature[APPLIC_1]``| 0a | 0b | 0c | 0d  | 0e |``End Feature``
| 1a | 1b | 1c | 1d | 1e |
``Feature[APPLIC_2]``| 2a | 2b | 2c | 2d  | 2e |``End Feature``
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c | 4d | 4e``End Feature`` |
| 5a | 5b | 5c | 5d | 5e |

### Table Cells

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
| 1a | 1b | 1c | 1d | 1e |
| ``Feature[APPLIC_1]``2a | 2b | 2c | 2d | 2e``End Feature`` |
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c``End Feature`` | 4d | 4e |
| 5a | 5b | 5c | 5d | 5e |
| ``Feature[APPLIC_1]``6a``End Feature`` | 6b | 6c | 6d | ``Feature[APPLIC_2]``6e``End Feature`` |
| 7a | 7b | 7c | 7d | 7e |

### Table Columns

| Col A | ``Feature[APPLIC_1]``Col B |``End Feature`` Col C | Col D ``Feature[APPLIC_2]``| Col E ``End Feature``|
|---|``Feature[APPLIC_1]``---|``End Feature``---|---``Feature[APPLIC_2]``|---:``End Feature``|
| 1a | ``Feature[APPLIC_1]``1b |``End Feature`` 1c | 1d ``Feature[APPLIC_2]``| 1e ``End Feature``|
| 2a | ``Feature[APPLIC_1]``2b |``End Feature`` 2c | 2d ``Feature[APPLIC_2]``| 2e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
# Overview

This is a test file for using PLE

## Feature Tests

``Feature[APPLIC_1=Included]``
Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``End Feature``

``Feature[APPLIC_1=Included]``
Included Text
``End Feature``

``Feature[APPLIC_1=Excluded]``
Excluded Text
``End Feature``


## Else Tests

``Feature[APPLIC_1]``
Tag 1
``Feature Else``
Not Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``Feature Else``
Not Tag 2
``End Feature``

## Boolean Tests

``Feature[APPLIC_1 | APPLIC_2]``
Included `OR` Excluded Feature
``End Feature``

``Feature[APPLIC_1 & APPLIC_2]``
Included `AND` Excluded Feature
``End Feature``

## Substitution Tests

``Eval[SUB_1]``
``Eval[SUB_2]``

- ``Eval[SUB_1]``
- ``Eval[SUB_2]``

## List Tests

``Feature[APPLIC_1]``
1. Tag 1
``End Feature``
2. Common Row 1
``Feature[APPLIC_1]``
    - Tag 2.1
``End Feature``
``Feature[APPLIC_2]``
3. Tag 2
    - Tag 2 Subbullet
``End Feature``
4. Common Row 2

## Nested Tests

``Feature[APPLIC_1]``
Level 1

``Feature[APPLIC_2]``
Level 2
``End Feature``
``End Feature``

## Feature and Substitution Test

``Feature[APPLIC_1]``
Tag1

``Eval[SUB_1]``
``End Feature``

## Tables

### Table Rows

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
``Feature[APPLIC_1]``| 0a | 0b | 0c | 0d  | 0e |``End Feature``
| 1a | 1b | 1c | 1d | 1e |
``Feature[APPLIC_2]``| 2a | 2b | 2c | 2d  | 2e |``End Feature``
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c | 4d | 4e``End Feature`` |
| 5a | 5b | 5c | 5d | 5e |

### Table Cells

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
| 1a | 1b | 1c | 1d | 1e |
| ``Feature[APPLIC_1]``2a | 2b | 2c | 2d | 2e``End Feature`` |
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c``End Feature`` | 4d | 4e |
| 5a | 5b | 5c | 5d | 5e |
| ``Feature[APPLIC_1]``6a``End Feature`` | 6b | 6c | 6d | ``Feature[APPLIC_2]``6e``End Feature`` |
| 7a | 7b | 7c | 7d | 7e |

### Table Columns

| Col A | ``Feature[APPLIC_1]``Col B |``End Feature`` Col C | Col D ``Feature[APPLIC_2]``| Col E ``End Feature``|
|---|``Feature[APPLIC_1]``---|``End Feature``---|---``Feature[APPLIC_2]``|---:``End Feature``|
| 1a | ``Feature[APPLIC_1]``1b |``End Feature`` 1c | 1d ``Feature[APPLIC_2]``| 1e ``End Feature``|
| 2a | ``Feature[APPLIC_1]``2b |``End Feature`` 2c | 2d ``Feature[APPLIC_2]``| 2e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
# Overview

This is a test file for using PLE

## Feature Tests

``Feature[APPLIC_1=Included]``
Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``End Feature``

``Feature[APPLIC_1=Included]``
Included Text
``End Feature``

``Feature[APPLIC_1=Excluded]``
Excluded Text
``End Feature``


## Else Tests

``Feature[APPLIC_1]``
Tag 1
``Feature Else``
Not Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``Feature Else``
Not Tag 2
``End Feature``

## Boolean Tests

``Feature[APPLIC_1 | APPLIC_2]``
Included `OR` Excluded Feature
``End Feature``

``Feature[APPLIC_1 & APPLIC_2]``
Included `AND` Excluded Feature
``End Feature``

## Substitution Tests

``Eval[SUB_1]``
``Eval[SUB_2]``

- ``Eval[SUB_1]``
- ``Eval[SUB_2]``

## List Tests

``Feature[APPLIC_1]``
1. Tag 1
``End Feature``
2. Common Row 1
``Feature[APPLIC_1]``
    - Tag 2.1
``End Feature``
``Feature[APPLIC_2]``
3. Tag 2
    - Tag 2 Subbullet
``End Feature``
4. Common Row 2

## Nested Tests

``Feature[APPLIC_1]``
Level 1

``Feature[APPLIC_2]``
Level 2
``End Feature``
``End Feature``

## Feature and Substitution Test

``Feature[APPLIC_1]``
Tag1

``Eval[SUB_1]``
``End Feature``

## Tables

### Table Rows

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
``Feature[APPLIC_1]``| 0a | 0b | 0c | 0d  | 0e |``End Feature``
| 1a | 1b | 1c | 1d | 1e |
``Feature[APPLIC_2]``| 2a | 2b | 2c | 2d  | 2e |``End Feature``
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c | 4d | 4e``End Feature`` |
| 5a | 5b | 5c | 5d | 5e |

### Table Cells

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
| 1a | 1b | 1c | 1d | 1e |
| ``Feature[APPLIC_1]``2a | 2b | 2c | 2d | 2e``End Feature`` |
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c``End Feature`` | 4d | 4e |
| 5a | 5b | 5c | 5d | 5e |
| ``Feature[APPLIC_1]``6a``End Feature`` | 6b | 6c | 6d | ``Feature[APPLIC_2]``6e``End Feature`` |
| 7a | 7b | 7c | 7d | 7e |

### Table Columns

| Col A | ``Feature[APPLIC_1]``Col B |``End Feature`` Col C | Col D ``Feature[APPLIC_2]``| Col E ``End Feature``|
|---|``Feature[APPLIC_1]``---|``End Feature``---|---``Feature[APPLIC_2]``|---:``End Feature``|
| 1a | ``Feature[APPLIC_1]``1b |``End Feature`` 1c | 1d ``Feature[APPLIC_2]``| 1e ``End Feature``|
| 2a | ``Feature[APPLIC_1]``2b |``End Feature`` 2c | 2d ``Feature[APPLIC_2]``| 2e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
# Overview

This is a test file for using PLE

## Feature Tests

``Feature[APPLIC_1=Included]``
Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``End Feature``

``Feature[APPLIC_1=Included]``
Included Text
``End Feature``

``Feature[APPLIC_1=Excluded]``
Excluded Text
``End Feature``


## Else Tests

``Feature[APPLIC_1]``
Tag 1
``Feature Else``
Not Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``Feature Else``
Not Tag 2
``End Feature``

## Boolean Tests

``Feature[APPLIC_1 | APPLIC_2]``
Included `OR` Excluded Feature
``End Feature``

``Feature[APPLIC_1 & APPLIC_2]``
Included `AND` Excluded Feature
``End Feature``

## Substitution Tests

``Eval[SUB_1]``
``Eval[SUB_2]``

- ``Eval[SUB_1]``
- ``Eval[SUB_2]``

## List Tests

``Feature[APPLIC_1]``
1. Tag 1
``End Feature``
2. Common Row 1
``Feature[APPLIC_1]``
    - Tag 2.1
``End Feature``
``Feature[APPLIC_2]``
3. Tag 2
    - Tag 2 Subbullet
``End Feature``
4. Common Row 2

## Nested Tests

``Feature[APPLIC_1]``
Level 1

``Feature[APPLIC_2]``
Level 2
``End Feature``
``End Feature``

## Feature and Substitution Test

``Feature[APPLIC_1]``
Tag1

``Eval[SUB_1]``
``End Feature``

## Tables

### Table Rows

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
``Feature[APPLIC_1]``| 0a | 0b | 0c | 0d  | 0e |``End Feature``
| 1a | 1b | 1c | 1d | 1e |
``Feature[APPLIC_2]``| 2a | 2b | 2c | 2d  | 2e |``End Feature``
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c | 4d | 4e``End Feature`` |
| 5a | 5b | 5c | 5d | 5e |

### Table Cells

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
| 1a | 1b | 1c | 1d | 1e |
| ``Feature[APPLIC_1]``2a | 2b | 2c | 2d | 2e``End Feature`` |
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c``End Feature`` | 4d | 4e |
| 5a | 5b | 5c | 5d | 5e |
| ``Feature[APPLIC_1]``6a``End Feature`` | 6b | 6c | 6d | ``Feature[APPLIC_2]``6e``End Feature`` |
| 7a | 7b | 7c | 7d | 7e |

### Table Columns

| Col A | ``Feature[APPLIC_1]``Col B |``End Feature`` Col C | Col D ``Feature[APPLIC_2]``| Col E ``End Feature``|
|---|``Feature[APPLIC_1]``---|``End Feature``---|---``Feature[APPLIC_2]``|---:``End Feature``|
| 1a | ``Feature[APPLIC_1]``1b |``End Feature`` 1c | 1d ``Feature[APPLIC_2]``| 1e ``End Feature``|
| 2a | ``Feature[APPLIC_1]``2b |``End Feature`` 2c | 2d ``Feature[APPLIC_2]``| 2e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
# Overview

This is a test file for using PLE

## Feature Tests

``Feature[APPLIC_1=Included]``
Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``End Feature``

``Feature[APPLIC_1=Included]``
Included Text
``End Feature``

``Feature[APPLIC_1=Excluded]``
Excluded Text
``End Feature``


## Else Tests

``Feature[APPLIC_1]``
Tag 1
``Feature Else``
Not Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``Feature Else``
Not Tag 2
``End Feature``

## Boolean Tests

``Feature[APPLIC_1 | APPLIC_2]``
Included `OR` Excluded Feature
``End Feature``

``Feature[APPLIC_1 & APPLIC_2]``
Included `AND` Excluded Feature
``End Feature``

## Substitution Tests

``Eval[SUB_1]``
``Eval[SUB_2]``

- ``Eval[SUB_1]``
- ``Eval[SUB_2]``

## List Tests

``Feature[APPLIC_1]``
1. Tag 1
``End Feature``
2. Common Row 1
``Feature[APPLIC_1]``
    - Tag 2.1
``End Feature``
``Feature[APPLIC_2]``
3. Tag 2
    - Tag 2 Subbullet
``End Feature``
4. Common Row 2

## Nested Tests

``Feature[APPLIC_1]``
Level 1

``Feature[APPLIC_2]``
Level 2
``End Feature``
``End Feature``

## Feature and Substitution Test

``Feature[APPLIC_1]``
Tag1

``Eval[SUB_1]``
``End Feature``

## Tables

### Table Rows

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
``Feature[APPLIC_1]``| 0a | 0b | 0c | 0d  | 0e |``End Feature``
| 1a | 1b | 1c | 1d | 1e |
``Feature[APPLIC_2]``| 2a | 2b | 2c | 2d  | 2e |``End Feature``
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c | 4d | 4e``End Feature`` |
| 5a | 5b | 5c | 5d | 5e |

### Table Cells

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
| 1a | 1b | 1c | 1d | 1e |
| ``Feature[APPLIC_1]``2a | 2b | 2c | 2d | 2e``End Feature`` |
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c``End Feature`` | 4d | 4e |
| 5a | 5b | 5c | 5d | 5e |
| ``Feature[APPLIC_1]``6a``End Feature`` | 6b | 6c | 6d | ``Feature[APPLIC_2]``6e``End Feature`` |
| 7a | 7b | 7c | 7d | 7e |

### Table Columns

| Col A | ``Feature[APPLIC_1]``Col B |``End Feature`` Col C | Col D ``Feature[APPLIC_2]``| Col E ``End Feature``|
|---|``Feature[APPLIC_1]``---|``End Feature``---|---``Feature[APPLIC_2]``|---:``End Feature``|
| 1a | ``Feature[APPLIC_1]``1b |``End Feature`` 1c | 1d ``Feature[APPLIC_2]``| 1e ``End Feature``|
| 2a | ``Feature[APPLIC_1]``2b |``End Feature`` 2c | 2d ``Feature[APPLIC_2]``| 2e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
# Overview

This is a test file for using PLE

## Feature Tests

``Feature[APPLIC_1=Included]``
Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``End Feature``

``Feature[APPLIC_1=Included]``
Included Text
``End Feature``

``Feature[APPLIC_1=Excluded]``
Excluded Text
``End Feature``


## Else Tests

``Feature[APPLIC_1]``
Tag 1
``Feature Else``
Not Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``Feature Else``
Not Tag 2
``End Feature``

## Boolean Tests

``Feature[APPLIC_1 | APPLIC_2]``
Included `OR` Excluded Feature
``End Feature``

``Feature[APPLIC_1 & APPLIC_2]``
Included `AND` Excluded Feature
``End Feature``

## Substitution Tests

``Eval[SUB_1]``
``Eval[SUB_2]``

- ``Eval[SUB_1]``
- ``Eval[SUB_2]``

## List Tests

``Feature[APPLIC_1]``
1. Tag 1
``End Feature``
2. Common Row 1
``Feature[APPLIC_1]``
    - Tag 2.1
``End Feature``
``Feature[APPLIC_2]``
3. Tag 2
    - Tag 2 Subbullet
``End Feature``
4. Common Row 2

## Nested Tests

``Feature[APPLIC_1]``
Level 1

``Feature[APPLIC_2]``
Level 2
``End Feature``
``End Feature``

## Feature and Substitution Test

``Feature[APPLIC_1]``
Tag1

``Eval[SUB_1]``
``End Feature``

## Tables

### Table Rows

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
``Feature[APPLIC_1]``| 0a | 0b | 0c | 0d  | 0e |``End Feature``
| 1a | 1b | 1c | 1d | 1e |
``Feature[APPLIC_2]``| 2a | 2b | 2c | 2d  | 2e |``End Feature``
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c | 4d | 4e``End Feature`` |
| 5a | 5b | 5c | 5d | 5e |

### Table Cells

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
| 1a | 1b | 1c | 1d | 1e |
| ``Feature[APPLIC_1]``2a | 2b | 2c | 2d | 2e``End Feature`` |
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c``End Feature`` | 4d | 4e |
| 5a | 5b | 5c | 5d | 5e |
| ``Feature[APPLIC_1]``6a``End Feature`` | 6b | 6c | 6d | ``Feature[APPLIC_2]``6e``End Feature`` |
| 7a | 7b | 7c | 7d | 7e |

### Table Columns

| Col A | ``Feature[APPLIC_1]``Col B |``End Feature`` Col C | Col D ``Feature[APPLIC_2]``| Col E ``End Feature``|
|---|``Feature[APPLIC_1]``---|``End Feature``---|---``Feature[APPLIC_2]``|---:``End Feature``|
| 1a | ``Feature[APPLIC_1]``1b |``End Feature`` 1c | 1d ``Feature[APPLIC_2]``| 1e ``End Feature``|
| 2a | ``Feature[APPLIC_1]``2b |``End Feature`` 2c | 2d ``Feature[APPLIC_2]``| 2e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
# Overview

This is a test file for using PLE

## Feature Tests

``Feature[APPLIC_1=Included]``
Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``End Feature``

``Feature[APPLIC_1=Included]``
Included Text
``End Feature``

``Feature[APPLIC_1=Excluded]``
Excluded Text
``End Feature``


## Else Tests

``Feature[APPLIC_1]``
Tag 1
``Feature Else``
Not Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``Feature Else``
Not Tag 2
``End Feature``

## Boolean Tests

``Feature[APPLIC_1 | APPLIC_2]``
Included `OR` Excluded Feature
``End Feature``

``Feature[APPLIC_1 & APPLIC_2]``
Included `AND` Excluded Feature
``End Feature``

## Substitution Tests

``Eval[SUB_1]``
``Eval[SUB_2]``

- ``Eval[SUB_1]``
- ``Eval[SUB_2]``

## List Tests

``Feature[APPLIC_1]``
1. Tag 1
``End Feature``
2. Common Row 1
``Feature[APPLIC_1]``
    - Tag 2.1
``End Feature``
``Feature[APPLIC_2]``
3. Tag 2
    - Tag 2 Subbullet
``End Feature``
4. Common Row 2

## Nested Tests

``Feature[APPLIC_1]``
Level 1

``Feature[APPLIC_2]``
Level 2
``End Feature``
``End Feature``

## Feature and Substitution Test

``Feature[APPLIC_1]``
Tag1

``Eval[SUB_1]``
``End Feature``

## Tables

### Table Rows

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
``Feature[APPLIC_1]``| 0a | 0b | 0c | 0d  | 0e |``End Feature``
| 1a | 1b | 1c | 1d | 1e |
``Feature[APPLIC_2]``| 2a | 2b | 2c | 2d  | 2e |``End Feature``
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c | 4d | 4e``End Feature`` |
| 5a | 5b | 5c | 5d | 5e |

### Table Cells

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
| 1a | 1b | 1c | 1d | 1e |
| ``Feature[APPLIC_1]``2a | 2b | 2c | 2d | 2e``End Feature`` |
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c``End Feature`` | 4d | 4e |
| 5a | 5b | 5c | 5d | 5e |
| ``Feature[APPLIC_1]``6a``End Feature`` | 6b | 6c | 6d | ``Feature[APPLIC_2]``6e``End Feature`` |
| 7a | 7b | 7c | 7d | 7e |

### Table Columns

| Col A | ``Feature[APPLIC_1]``Col B |``End Feature`` Col C | Col D ``Feature[APPLIC_2]``| Col E ``End Feature``|
|---|``Feature[APPLIC_1]``---|``End Feature``---|---``Feature[APPLIC_2]``|---:``End Feature``|
| 1a | ``Feature[APPLIC_1]``1b |``End Feature`` 1c | 1d ``Feature[APPLIC_2]``| 1e ``End Feature``|
| 2a | ``Feature[APPLIC_1]``2b |``End Feature`` 2c | 2d ``Feature[APPLIC_2]``| 2e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
# Overview

This is a test file for using PLE

## Feature Tests

``Feature[APPLIC_1=Included]``
Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``End Feature``

``Feature[APPLIC_1=Included]``
Included Text
``End Feature``

``Feature[APPLIC_1=Excluded]``
Excluded Text
``End Feature``


## Else Tests

``Feature[APPLIC_1]``
Tag 1
``Feature Else``
Not Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``Feature Else``
Not Tag 2
``End Feature``

## Boolean Tests

``Feature[APPLIC_1 | APPLIC_2]``
Included `OR` Excluded Feature
``End Feature``

``Feature[APPLIC_1 & APPLIC_2]``
Included `AND` Excluded Feature
``End Feature``

## Substitution Tests

``Eval[SUB_1]``
``Eval[SUB_2]``

- ``Eval[SUB_1]``
- ``Eval[SUB_2]``

## List Tests

``Feature[APPLIC_1]``
1. Tag 1
``End Feature``
2. Common Row 1
``Feature[APPLIC_1]``
    - Tag 2.1
``End Feature``
``Feature[APPLIC_2]``
3. Tag 2
    - Tag 2 Subbullet
``End Feature``
4. Common Row 2

## Nested Tests

``Feature[APPLIC_1]``
Level 1

``Feature[APPLIC_2]``
Level 2
``End Feature``
``End Feature``

## Feature and Substitution Test

``Feature[APPLIC_1]``
Tag1

``Eval[SUB_1]``
``End Feature``

## Tables

### Table Rows

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
``Feature[APPLIC_1]``| 0a | 0b | 0c | 0d  | 0e |``End Feature``
| 1a | 1b | 1c | 1d | 1e |
``Feature[APPLIC_2]``| 2a | 2b | 2c | 2d  | 2e |``End Feature``
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c | 4d | 4e``End Feature`` |
| 5a | 5b | 5c | 5d | 5e |

### Table Cells

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
| 1a | 1b | 1c | 1d | 1e |
| ``Feature[APPLIC_1]``2a | 2b | 2c | 2d | 2e``End Feature`` |
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c``End Feature`` | 4d | 4e |
| 5a | 5b | 5c | 5d | 5e |
| ``Feature[APPLIC_1]``6a``End Feature`` | 6b | 6c | 6d | ``Feature[APPLIC_2]``6e``End Feature`` |
| 7a | 7b | 7c | 7d | 7e |

### Table Columns

| Col A | ``Feature[APPLIC_1]``Col B |``End Feature`` Col C | Col D ``Feature[APPLIC_2]``| Col E ``End Feature``|
|---|``Feature[APPLIC_1]``---|``End Feature``---|---``Feature[APPLIC_2]``|---:``End Feature``|
| 1a | ``Feature[APPLIC_1]``1b |``End Feature`` 1c | 1d ``Feature[APPLIC_2]``| 1e ``End Feature``|
| 2a | ``Feature[APPLIC_1]``2b |``End Feature`` 2c | 2d ``Feature[APPLIC_2]``| 2e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
# Overview

This is a test file for using PLE

## Feature Tests

``Feature[APPLIC_1=Included]``
Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``End Feature``

``Feature[APPLIC_1=Included]``
Included Text
``End Feature``

``Feature[APPLIC_1=Excluded]``
Excluded Text
``End Feature``


## Else Tests

``Feature[APPLIC_1]``
Tag 1
``Feature Else``
Not Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``Feature Else``
Not Tag 2
``End Feature``

## Boolean Tests

``Feature[APPLIC_1 | APPLIC_2]``
Included `OR` Excluded Feature
``End Feature``

``Feature[APPLIC_1 & APPLIC_2]``
Included `AND` Excluded Feature
``End Feature``

## Substitution Tests

``Eval[SUB_1]``
``Eval[SUB_2]``

- ``Eval[SUB_1]``
- ``Eval[SUB_2]``

## List Tests

``Feature[APPLIC_1]``
1. Tag 1
``End Feature``
2. Common Row 1
``Feature[APPLIC_1]``
    - Tag 2.1
``End Feature``
``Feature[APPLIC_2]``
3. Tag 2
    - Tag 2 Subbullet
``End Feature``
4. Common Row 2

## Nested Tests

``Feature[APPLIC_1]``
Level 1

``Feature[APPLIC_2]``
Level 2
``End Feature``
``End Feature``

## Feature and Substitution Test

``Feature[APPLIC_1]``
Tag1

``Eval[SUB_1]``
``End Feature``

## Tables

### Table Rows

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
``Feature[APPLIC_1]``| 0a | 0b | 0c | 0d  | 0e |``End Feature``
| 1a | 1b | 1c | 1d | 1e |
``Feature[APPLIC_2]``| 2a | 2b | 2c | 2d  | 2e |``End Feature``
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c | 4d | 4e``End Feature`` |
| 5a | 5b | 5c | 5d | 5e |

### Table Cells

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
| 1a | 1b | 1c | 1d | 1e |
| ``Feature[APPLIC_1]``2a | 2b | 2c | 2d | 2e``End Feature`` |
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c``End Feature`` | 4d | 4e |
| 5a | 5b | 5c | 5d | 5e |
| ``Feature[APPLIC_1]``6a``End Feature`` | 6b | 6c | 6d | ``Feature[APPLIC_2]``6e``End Feature`` |
| 7a | 7b | 7c | 7d | 7e |

### Table Columns

| Col A | ``Feature[APPLIC_1]``Col B |``End Feature`` Col C | Col D ``Feature[APPLIC_2]``| Col E ``End Feature``|
|---|``Feature[APPLIC_1]``---|``End Feature``---|---``Feature[APPLIC_2]``|---:``End Feature``|
| 1a | ``Feature[APPLIC_1]``1b |``End Feature`` 1c | 1d ``Feature[APPLIC_2]``| 1e ``End Feature``|
| 2a | ``Feature[APPLIC_1]``2b |``End Feature`` 2c | 2d ``Feature[APPLIC_2]``| 2e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
# Overview

This is a test file for using PLE

## Feature Tests

``Feature[APPLIC_1=Included]``
Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``End Feature``

``Feature[APPLIC_1=Included]``
Included Text
``End Feature``

``Feature[APPLIC_1=Excluded]``
Excluded Text
``End Feature``


## Else Tests

``Feature[APPLIC_1]``
Tag 1
``Feature Else``
Not Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``Feature Else``
Not Tag 2
``End Feature``

## Boolean Tests

``Feature[APPLIC_1 | APPLIC_2]``
Included `OR` Excluded Feature
``End Feature``

``Feature[APPLIC_1 & APPLIC_2]``
Included `AND` Excluded Feature
``End Feature``

## Substitution Tests

``Eval[SUB_1]``
``Eval[SUB_2]``

- ``Eval[SUB_1]``
- ``Eval[SUB_2]``

## List Tests

``Feature[APPLIC_1]``
1. Tag 1
``End Feature``
2. Common Row 1
``Feature[APPLIC_1]``
    - Tag 2.1
``End Feature``
``Feature[APPLIC_2]``
3. Tag 2
    - Tag 2 Subbullet
``End Feature``
4. Common Row 2

## Nested Tests

``Feature[APPLIC_1]``
Level 1

``Feature[APPLIC_2]``
Level 2
``End Feature``
``End Feature``

## Feature and Substitution Test

``Feature[APPLIC_1]``
Tag1

``Eval[SUB_1]``
``End Feature``

## Tables

### Table Rows

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
``Feature[APPLIC_1]``| 0a | 0b | 0c | 0d  | 0e |``End Feature``
| 1a | 1b | 1c | 1d | 1e |
``Feature[APPLIC_2]``| 2a | 2b | 2c | 2d  | 2e |``End Feature``
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c | 4d | 4e``End Feature`` |
| 5a | 5b | 5c | 5d | 5e |

### Table Cells

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
| 1a | 1b | 1c | 1d | 1e |
| ``Feature[APPLIC_1]``2a | 2b | 2c | 2d | 2e``End Feature`` |
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c``End Feature`` | 4d | 4e |
| 5a | 5b | 5c | 5d | 5e |
| ``Feature[APPLIC_1]``6a``End Feature`` | 6b | 6c | 6d | ``Feature[APPLIC_2]``6e``End Feature`` |
| 7a | 7b | 7c | 7d | 7e |

### Table Columns

| Col A | ``Feature[APPLIC_1]``Col B |``End Feature`` Col C | Col D ``Feature[APPLIC_2]``| Col E ``End Feature``|
|---|``Feature[APPLIC_1]``---|``End Feature``---|---``Feature[APPLIC_2]``|---:``End Feature``|
| 1a | ``Feature[APPLIC_1]``1b |``End Feature`` 1c | 1d ``Feature[APPLIC_2]``| 1e ``End Feature``|
| 2a | ``Feature[APPLIC_1]``2b |``End Feature`` 2c | 2d ``Feature[APPLIC_2]``| 2e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
# Overview

This is a test file for using PLE

## Feature Tests

``Feature[APPLIC_1=Included]``
Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``End Feature``

``Feature[APPLIC_1=Included]``
Included Text
``End Feature``

``Feature[APPLIC_1=Excluded]``
Excluded Text
``End Feature``


## Else Tests

``Feature[APPLIC_1]``
Tag 1
``Feature Else``
Not Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``Feature Else``
Not Tag 2
``End Feature``

## Boolean Tests

``Feature[APPLIC_1 | APPLIC_2]``
Included `OR` Excluded Feature
``End Feature``

``Feature[APPLIC_1 & APPLIC_2]``
Included `AND` Excluded Feature
``End Feature``

## Substitution Tests

``Eval[SUB_1]``
``Eval[SUB_2]``

- ``Eval[SUB_1]``
- ``Eval[SUB_2]``

## List Tests

``Feature[APPLIC_1]``
1. Tag 1
``End Feature``
2. Common Row 1
``Feature[APPLIC_1]``
    - Tag 2.1
``End Feature``
``Feature[APPLIC_2]``
3. Tag 2
    - Tag 2 Subbullet
``End Feature``
4. Common Row 2

## Nested Tests

``Feature[APPLIC_1]``
Level 1

``Feature[APPLIC_2]``
Level 2
``End Feature``
``End Feature``

## Feature and Substitution Test

``Feature[APPLIC_1]``
Tag1

``Eval[SUB_1]``
``End Feature``

## Tables

### Table Rows

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
``Feature[APPLIC_1]``| 0a | 0b | 0c | 0d  | 0e |``End Feature``
| 1a | 1b | 1c | 1d | 1e |
``Feature[APPLIC_2]``| 2a | 2b | 2c | 2d  | 2e |``End Feature``
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c | 4d | 4e``End Feature`` |
| 5a | 5b | 5c | 5d | 5e |

### Table Cells

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
| 1a | 1b | 1c | 1d | 1e |
| ``Feature[APPLIC_1]``2a | 2b | 2c | 2d | 2e``End Feature`` |
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c``End Feature`` | 4d | 4e |
| 5a | 5b | 5c | 5d | 5e |
| ``Feature[APPLIC_1]``6a``End Feature`` | 6b | 6c | 6d | ``Feature[APPLIC_2]``6e``End Feature`` |
| 7a | 7b | 7c | 7d | 7e |

### Table Columns

| Col A | ``Feature[APPLIC_1]``Col B |``End Feature`` Col C | Col D ``Feature[APPLIC_2]``| Col E ``End Feature``|
|---|``Feature[APPLIC_1]``---|``End Feature``---|---``Feature[APPLIC_2]``|---:``End Feature``|
| 1a | ``Feature[APPLIC_1]``1b |``End Feature`` 1c | 1d ``Feature[APPLIC_2]``| 1e ``End Feature``|
| 2a | ``Feature[APPLIC_1]``2b |``End Feature`` 2c | 2d ``Feature[APPLIC_2]``| 2e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
# Overview

This is a test file for using PLE

## Feature Tests

``Feature[APPLIC_1=Included]``
Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``End Feature``

``Feature[APPLIC_1=Included]``
Included Text
``End Feature``

``Feature[APPLIC_1=Excluded]``
Excluded Text
``End Feature``


## Else Tests

``Feature[APPLIC_1]``
Tag 1
``Feature Else``
Not Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``Feature Else``
Not Tag 2
``End Feature``

## Boolean Tests

``Feature[APPLIC_1 | APPLIC_2]``
Included `OR` Excluded Feature
``End Feature``

``Feature[APPLIC_1 & APPLIC_2]``
Included `AND` Excluded Feature
``End Feature``

## Substitution Tests

``Eval[SUB_1]``
``Eval[SUB_2]``

- ``Eval[SUB_1]``
- ``Eval[SUB_2]``

## List Tests

``Feature[APPLIC_1]``
1. Tag 1
``End Feature``
2. Common Row 1
``Feature[APPLIC_1]``
    - Tag 2.1
``End Feature``
``Feature[APPLIC_2]``
3. Tag 2
    - Tag 2 Subbullet
``End Feature``
4. Common Row 2

## Nested Tests

``Feature[APPLIC_1]``
Level 1

``Feature[APPLIC_2]``
Level 2
``End Feature``
``End Feature``

## Feature and Substitution Test

``Feature[APPLIC_1]``
Tag1

``Eval[SUB_1]``
``End Feature``

## Tables

### Table Rows

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
``Feature[APPLIC_1]``| 0a | 0b | 0c | 0d  | 0e |``End Feature``
| 1a | 1b | 1c | 1d | 1e |
``Feature[APPLIC_2]``| 2a | 2b | 2c | 2d  | 2e |``End Feature``
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c | 4d | 4e``End Feature`` |
| 5a | 5b | 5c | 5d | 5e |

### Table Cells

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
| 1a | 1b | 1c | 1d | 1e |
| ``Feature[APPLIC_1]``2a | 2b | 2c | 2d | 2e``End Feature`` |
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c``End Feature`` | 4d | 4e |
| 5a | 5b | 5c | 5d | 5e |
| ``Feature[APPLIC_1]``6a``End Feature`` | 6b | 6c | 6d | ``Feature[APPLIC_2]``6e``End Feature`` |
| 7a | 7b | 7c | 7d | 7e |

### Table Columns

| Col A | ``Feature[APPLIC_1]``Col B |``End Feature`` Col C | Col D ``Feature[APPLIC_2]``| Col E ``End Feature``|
|---|``Feature[APPLIC_1]``---|``End Feature``---|---``Feature[APPLIC_2]``|---:``End Feature``|
| 1a | ``Feature[APPLIC_1]``1b |``End Feature`` 1c | 1d ``Feature[APPLIC_2]``| 1e ``End Feature``|
| 2a | ``Feature[APPLIC_1]``2b |``End Feature`` 2c | 2d ``Feature[APPLIC_2]``| 2e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
# Overview

This is a test file for using PLE

## Feature Tests

``Feature[APPLIC_1=Included]``
Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``End Feature``

``Feature[APPLIC_1=Included]``
Included Text
``End Feature``

``Feature[APPLIC_1=Excluded]``
Excluded Text
``End Feature``


## Else Tests

``Feature[APPLIC_1]``
Tag 1
``Feature Else``
Not Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``Feature Else``
Not Tag 2
``End Feature``

## Boolean Tests

``Feature[APPLIC_1 | APPLIC_2]``
Included `OR` Excluded Feature
``End Feature``

``Feature[APPLIC_1 & APPLIC_2]``
Included `AND` Excluded Feature
``End Feature``

## Substitution Tests

``Eval[SUB_1]``
``Eval[SUB_2]``

- ``Eval[SUB_1]``
- ``Eval[SUB_2]``

## List Tests

``Feature[APPLIC_1]``
1. Tag 1
``End Feature``
2. Common Row 1
``Feature[APPLIC_1]``
    - Tag 2.1
``End Feature``
``Feature[APPLIC_2]``
3. Tag 2
    - Tag 2 Subbullet
``End Feature``
4. Common Row 2

## Nested Tests

``Feature[APPLIC_1]``
Level 1

``Feature[APPLIC_2]``
Level 2
``End Feature``
``End Feature``

## Feature and Substitution Test

``Feature[APPLIC_1]``
Tag1

``Eval[SUB_1]``
``End Feature``

## Tables

### Table Rows

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
``Feature[APPLIC_1]``| 0a | 0b | 0c | 0d  | 0e |``End Feature``
| 1a | 1b | 1c | 1d | 1e |
``Feature[APPLIC_2]``| 2a | 2b | 2c | 2d  | 2e |``End Feature``
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c | 4d | 4e``End Feature`` |
| 5a | 5b | 5c | 5d | 5e |

### Table Cells

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
| 1a | 1b | 1c | 1d | 1e |
| ``Feature[APPLIC_1]``2a | 2b | 2c | 2d | 2e``End Feature`` |
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c``End Feature`` | 4d | 4e |
| 5a | 5b | 5c | 5d | 5e |
| ``Feature[APPLIC_1]``6a``End Feature`` | 6b | 6c | 6d | ``Feature[APPLIC_2]``6e``End Feature`` |
| 7a | 7b | 7c | 7d | 7e |

### Table Columns

| Col A | ``Feature[APPLIC_1]``Col B |``End Feature`` Col C | Col D ``Feature[APPLIC_2]``| Col E ``End Feature``|
|---|``Feature[APPLIC_1]``---|``End Feature``---|---``Feature[APPLIC_2]``|---:``End Feature``|
| 1a | ``Feature[APPLIC_1]``1b |``End Feature`` 1c | 1d ``Feature[APPLIC_2]``| 1e ``End Feature``|
| 2a | ``Feature[APPLIC_1]``2b |``End Feature`` 2c | 2d ``Feature[APPLIC_2]``| 2e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|

";
    let test_text = test_text_str.as_bytes();
    group.bench_function("test_text", |b| {
        b.iter(|| {
            let _ = std::hint::black_box(parse_applicability(
                LocatedSpan::new_extra(test_text, ((0usize, 0), (0usize, 0))),
                &doc_config,
            ));
        });
    });
    group.bench_function("sample_text", |b| {
        b.iter(|| {
            let _ = std::hint::black_box(parse_applicability(
                LocatedSpan::new_extra(
                    r#"Some other text
``Feature[SOMETHING] ``
Some text here  
``End Feature`` More text
``Configuration [SOME_CONFIGURATION]``
configuration text
``End Configuration``","``","``"#,
                    ((0usize, 0), (0usize, 0)),
                ),
                &doc_config,
            ));
        });
    });
    group.finish();
}
criterion_group! {
    name = benches;
    config = Criterion::default().sample_size(1000);
    targets = bench_parser
}
criterion_main!(benches);
