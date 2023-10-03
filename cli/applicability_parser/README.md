# Applicability Parsing
Parser for parsing arbitrary text chunks by features,configurations and substitutions.
## Development Setup
### Prerequisites
- Install rust according to https://www.rust-lang.org/tools/install
### Building
- Run cargo build
    - This can be run with release flag and should be compatible with most tier 1/2 rust targets https://doc.rust-lang.org/nightly/rustc/platform-support.html
    - Can also be run with cargo build --features "serde"
- Bazel - TBD waiting on rules_rust 0.37.0
## Dependencies
- Cargo
- rustc 1.74
- nom
Optional Dependencies:
- serde