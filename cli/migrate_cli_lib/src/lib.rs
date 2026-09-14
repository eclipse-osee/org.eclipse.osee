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
use bill_of_features_codemods_cli::{
    BillOfFeaturesCodeModArgs, BillOfFeaturesCodeModError, perform_conversions,
};
use clap::{Parser, Subcommand};
use thiserror::Error;
#[derive(Parser, Debug)]
#[clap(
    verbatim_doc_comment,
    about = "Performs code migrations.",
    long_about = r#"Performs code migrations."#
)]
pub struct MigrateArgs {
    #[command(subcommand)]
    pub command: Commands,
}
#[derive(Debug, Subcommand)]
pub enum Commands {
    #[command(name = "bill-of-features")]
    BOF(BillOfFeaturesCodeModArgs),
    #[command(name = "ple-model")]
    PLEModel(ple_model_codemods_cli::MigrationArgs),
    #[command(name = "ple-config")]
    PLEConfig(ple_config_codemods_cli::MigrationArgs),
}
#[derive(Debug, Error)]
pub enum MigrationError {
    #[error("{}",.0)]
    BillOfFeaturesCodeModError(#[from] BillOfFeaturesCodeModError),
    #[error("{}",.0)]
    PleModel(#[from] ple_model_codemods_cli::MigrateError),
    #[error("{}",.0)]
    PleConfig(#[from] ple_config_codemods_cli::MigrateError),
}

pub fn perform_migrations(args: MigrateArgs) -> Result<(), MigrationError> {
    match args.command {
        Commands::BOF(bof_args) => {
            perform_conversions(bof_args)?;
        }
        Commands::PLEModel(ple_args) => {
            ple_model_codemods_cli::migrate(ple_args)?;
        }
        Commands::PLEConfig(migration_args) => {
            ple_config_codemods_cli::migrate(migration_args)?;
        }
    }
    Ok(())
}
