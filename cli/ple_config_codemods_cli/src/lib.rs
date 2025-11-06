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
use ple_config_codemods::{AddPLEModelOptions, add_model_to_config};
use thiserror::Error;

#[derive(Parser, Debug)]
#[clap(
    author = "Luciano Vaglienti",
    version,
    verbatim_doc_comment,
    about = "Various PLE Config migration utilities"
)]
pub struct MigrationArgs {
    #[command(subcommand)]
    pub command: Commands,
}
#[derive(Debug, Subcommand)]
pub enum Commands {
    #[command(name = "add-ple-config")]
    AddPleModel(AddPleModelArgs),
}
#[derive(Parser, Debug)]
#[clap(
    author = "Luciano Vaglienti",
    version,
    verbatim_doc_comment,
    about = "Adds PLE model to PLE Config file."
)]
pub struct AddPleModelArgs {
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
    /// Config file containing the definitions of features, project configuration, and bill of features.
    /// An example:
    ///includes = ["some\\other\\path\\to\\config.toml"]
    ///[project]
    ///inline_project_exclusions = [ "path/to/file.md", "another/path/to/file.cpp" ]
    ///[[feature]]
    ///name = "test_feature"
    ///values = ["Included", "Excluded"]
    ///description = "description of test feature"
    ///[[feature.test_category]]
    ///name = "test_feature2"
    ///values = ["Included", "Excluded"]
    ///product_applicabilities = [ "ABC", "DEF"]
    ///applicability_constraint = "hello4=Included"
    ///description = "description of test feature2"
    ///[config]
    ///name="myConfig"
    ///group = ""
    ///features = [
    ///    "JHU_CONTROLLER"
    ///]
    ///substitutions = [
    ///    { match_text ="MY_EVAL_TAG", substitute = "MY_EVAL_VALUE" }
    ///]
    ///
    #[clap(short, long, verbatim_doc_comment)]
    pub ple_config: std::path::PathBuf,
}

impl From<AddPleModelArgs> for AddPLEModelOptions {
    fn from(value: AddPleModelArgs) -> Self {
        Self {
            ple_model: value.ple_model,
            ple_config: value.ple_config,
        }
    }
}
pub fn migrate(args: MigrationArgs) -> Result<(), MigrateError> {
    match args.command {
        Commands::AddPleModel(add_ple_model_args) => {
            add_model_to_config(add_ple_model_args.into())?;
        }
    }
    Ok(())
}

#[derive(Debug, Error)]
pub enum MigrateError {
    #[error("{}",.0)]
    AddPleModelError(#[from] ple_config_codemods::AddPleModelError),
}
