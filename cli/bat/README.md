# BAT CLI
The Block Applicability Tool(BAT) is a CLI processor for features,configurations and substitutions on arbitrary files. Some file types have already been configured by default.
- Default Supported Types
    - Markdown(.md)
## Development Setup
### Prerequisites
- Install rust according to https://www.rust-lang.org/tools/install
### Building
- Run cargo build
    - This can be run with release flag and should be compatible with most tier 1/2 rust targets https://doc.rust-lang.org/nightly/rustc/platform-support.html
- Bazel - TBD waiting on rules_rust 0.37.0
## Dependencies
- Cargo
- rustc 1.74
- Serde
- Serde JSON
- Clap
## Running
Help documentation can be seen by running the executable with a -h flag.

Of note, mixed file types aren't supported as of yet, however the processor can be run with the same commands but different set of files in the same folder to produce a desired output.

Available flags currently:
- a/applicability-config: This is the path to the config file that houses the configurations, features and substitutions.
- o/out-dir: This is the path to place output files.
- s/srcs: This is the path to each source file that should be processed.
- b/begin-comment-syntax: This is the start of a comment in the file type in order to recognize features & configurations.
- e/end-comment-syntax: This is the end of a comment in the file type in order to recognize features & configurations
- V/version: Prints the version of the BAT CLI being run.

