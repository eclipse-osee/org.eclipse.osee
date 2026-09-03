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
use bat_lib::{BatInternalCliOptions, perform_block_applicability};
use clap::{Parser, Subcommand};
use clap_verbosity_flag::{Verbosity, WarnLevel};
use cli_logging::initialize_logging;
use generate_cli_lib::{GeneratorArgs, run_generator};
use migrate_cli_lib::{MigrateArgs, perform_migrations};
use pat_lib::{PatInternalCliOptions, project_repository};
use validate_cli_lib::{ValidateCliOptions, validate};

#[cfg(not(target_env = "msvc"))]
#[global_allocator]
static GLOBAL: mimalloc::MiMalloc = mimalloc::MiMalloc;

#[derive(Parser)]
#[command(
    name = "ple",
    author = "Luciano Vaglienti",
    version,
    verbatim_doc_comment
)]
pub struct PleCliOptions {
    #[command(subcommand)]
    command: Commands,
    /// Verbosity of output, defaults to warnings and errors.
    /// -q will have no output
    /// -v will show warnings,info and errors
    /// -vv will show warnings,info,errors, and debug
    /// -vvv will show warnings,info,errors, debug and trace output
    #[command(flatten)]
    pub verbose: Verbosity<WarnLevel>,
}

#[derive(Debug, Subcommand)]
pub enum Commands {
    #[command(subcommand)]
    Compile(CompileCliOptions),
    #[command(name = "validate")]
    Validate(ValidateCliOptions),
    #[command(name = "migrate")]
    Migrate(MigrateArgs),
    #[command(name = "generate")]
    Generate(GeneratorArgs),
}

#[derive(Debug, Subcommand)]
#[clap(about = "Compile projections")]
pub enum CompileCliOptions {
    #[command(name = "project")]
    Pat(PatCliOptions),
    #[command(name = "file")]
    Bat(BatInternalCliOptions),
}

#[derive(Parser, Debug)]
pub struct PatCliOptions {
    #[clap(flatten)]
    options: PatInternalCliOptions,
}
#[tracing::instrument(err)]
fn main() -> anyhow::Result<()> {
    let args = PleCliOptions::parse();
    match args.command {
        Commands::Compile(compile_cli_options) => {
            let header_span = initialize_logging(&args.verbose, "compile");
            match compile_cli_options {
                CompileCliOptions::Pat(pat_cli_options) => {
                    project_repository(pat_cli_options.options, &header_span)
                }
                CompileCliOptions::Bat(bat_internal_cli_options) => {
                    perform_block_applicability(bat_internal_cli_options)
                }
            }
        }
        Commands::Validate(validate_cli_options) => {
            let _header_span = initialize_logging(&args.verbose, "validate");
            validate(validate_cli_options)
        }
        Commands::Migrate(migrate_args) => {
            let _header_span = initialize_logging(&args.verbose, "migrate");
            perform_migrations(migrate_args)?;
            Ok(())
        }
        Commands::Generate(generate_args) => {
            let _header_span = initialize_logging(&args.verbose, "generate");
            run_generator(generate_args)?;
            Ok(())
        }
    }
}
