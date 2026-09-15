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
use clap::{Parser, Subcommand};
use ple_model_codemods::{MigrateV0ToV1Options, migrate_v0_to_v1};
use thiserror::Error;

#[derive(Parser, Debug)]
#[clap(
    author = "Luciano Vaglienti",
    version,
    verbatim_doc_comment,
    about = "Various PLE Model migration utilities"
)]
pub struct MigrationArgs {
    #[command(subcommand)]
    pub command: Commands,
}
#[derive(Debug, Subcommand)]
pub enum Commands {
    #[command(name = "v0-to-v1")]
    MigrateV0ToV1(MigrateV0ToV1Args),
}
#[derive(Parser, Debug)]
#[clap(
    author = "Luciano Vaglienti",
    version,
    verbatim_doc_comment,
    about = "Migrates initial ple model to version 1.0 of PLE Model."
)]
pub struct MigrateV0ToV1Args {
    /// Config file containing the definitions of features.
    /// An example:
    /// [
    ///     {
    ///         "name":"FEATURE_A",
    ///         "values":["Included", "Excluded"],
    ///         "description":"Sample description",
    ///         "productApplicabililties":["PRODUCT_APP_A"],
    ///         "applicConstraint" : "OTHER_FEATURE=Included"
    ///     }
    /// ]
    #[clap(short, long, verbatim_doc_comment)]
    pub ple_model: std::path::PathBuf,
}

impl From<MigrateV0ToV1Args> for MigrateV0ToV1Options {
    fn from(value: MigrateV0ToV1Args) -> Self {
        Self {
            ple_model: value.ple_model,
        }
    }
}
pub fn migrate(args: MigrationArgs) -> Result<(), MigrateError> {
    match args.command {
        Commands::MigrateV0ToV1(migrate_v0_to_v1_args) => {
            migrate_v0_to_v1(migrate_v0_to_v1_args.into())?;
        }
    }
    Ok(())
}

#[derive(Debug, Error)]
pub enum MigrateError {
    #[error("{}",.0)]
    MigrateV0ToV1(#[from] ple_model_codemods::MigrateV0ToV1Error),
}
