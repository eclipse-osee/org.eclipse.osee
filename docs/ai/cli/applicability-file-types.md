---
summary: "How the applicability parser (PLE tool & OSEE Markdown publishing) maps file types to comment syntax via lexer configs"
tags: [applicability, ple, parser, lexer, file-types]
fileMatch: "cli/applicability_document_schema/**"
---

# Applicability file types & comment syntax

The PLE tool parses applicability tags embedded inside comments. The comment
syntax used for a given file is resolved from the file's extension (or name for
extensionless files). This mapping is centralized — adding a new file type means
touching a small, fixed set of places.

## Source of truth

`cli/applicability_document_schema/src/lib.rs` is the authority:

- `SupportedSchema` — enum of supported schema families (one per distinct
  comment style, e.g. `BuildFile` = `#`, `ConfLike` = `;`).
- `get_schema_from_file_name_and_extension` — maps extensions / file names to a
  `SupportedSchema`.
- `get_schema` — maps schema aliases (`"build"`, `"conf"`, ...) to the enum.
- `DocTypeConfig` + `get_doc_config` — maps a `SupportedSchema` to its concrete
  lexer config instance.

Each distinct comment style has its own lexer config crate under
`cli/applicability_lexer_config_<name>/` (e.g. `_build_file` for `#`, `_conf`
for `;`, `_rust` for `//` + `/* */`). A lexer config implements the comment
traits from `applicability_lexer_base` (single-line terminated / non-terminated,
multi-line, code block).

## Adding a file type

1. **Reuse an existing comment style if one matches.** If the new extension uses
   a comment char already covered by a schema, just add the extension to that
   schema's match arms. Example: `.yml`/`.yaml` use `#`, so they were added to
   the `BuildFile` arms — no new crate needed.

2. **Create a new lexer config crate only for a new comment style.** Example:
   `.conf` uses `;`, which no existing schema had, so
   `applicability_lexer_config_conf` was added (mirroring `_build_file` with `;`).

### Checklist for a brand-new comment style

- Create `cli/applicability_lexer_config_<name>/` with `Cargo.toml`, `BUILD`,
  and `src/lib.rs` (copy an existing single-line config such as `_build_file`).
- Register the crate in the workspace `Cargo.toml` `members` list.
- In `applicability_document_schema`: add the dep to `Cargo.toml` and `BUILD`,
  add a `SupportedSchema` variant, map extensions in
  `get_schema_from_file_name_and_extension`, add a `get_schema` alias, add a
  `DocTypeConfig` variant, and handle it in `get_doc_config`.

> **`get_schema` aliases are also markdown code-fence language tags.** The
> string / byte-string keys matched in `get_schema` map 1:1 with the language
> tag expected after a markdown code fence. When the markdown lexer encounters a
> fenced block, it feeds the fence's language tag through
> `get_schema` → `get_doc_config` and parses the block body with that schema's
> comment syntax — the same way a markdown parser associates the block with a
> language. For example, a block opened with ` ```yml ` (or ` ```yaml `) is
> parsed for applicability using the `BuildFile` (`#`) syntax, because `"yml"`
> and `"yaml"` are `get_schema` aliases. So each alias you add here doubles as a
> supported code-block language tag inside markdown documents.

- Update the two `match &config` blocks in
  `cli/applicability_parser_config/src/lib.rs` and the `SupportedSchema` match in
  `get_comment_syntax_from_file`, plus the extension list in
  `get_comment_syntax_from_file_name_and_extension`.
- Add the arm to the `DocTypeConfig` match in
  `cli/applicability_lexer_applicability_structure_code_block/src/code_block_lexer.rs`.

### Non-exhaustive match gotcha

`SupportedSchema` and `DocTypeConfig` are matched exhaustively in several crates.
Adding a variant will break compilation until every match is updated. The
compiler will point you to each site; the known consumers are
`applicability_parser_config` and
`applicability_lexer_applicability_structure_code_block`.

Keep the inline doc-comment lists of supported extensions (in both
`applicability_document_schema` and `applicability_parser_config`) in sync.
