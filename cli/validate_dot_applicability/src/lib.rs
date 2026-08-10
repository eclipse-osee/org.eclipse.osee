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
use std::{
    fmt::{Debug, Display},
    sync::Arc,
};

use applicability_project::{FileApplicabilityLinkValidationError, discover_project};
use bill_of_features::{BillOfFeatures, BillOfFeaturesEnum, ReadBillOfFeaturesConfigError};
use pat_config::{CompletePleConfig, read_ple_config_and_bof};
use thiserror::Error;
use tracing::{trace, warn};
#[derive(Debug)]
pub struct ValidateDotApplicabilityOptions {
    /// Config file containing the valid applicabilities,configurations, and substitutions.
    /// An example:
    ///     {
    ///         "name":"PRODUCT_A",
    ///         "group":["abGroup"],
    ///         "features":["ENGINE_5=A2543","JHU_CONTROLLER=Excluded","ROBOT_ARM_LIGHT=Excluded","ROBOT_SPEAKER=SPKR_A"],
    ///         "substitutions":[
    ///             {"matchText":"SOME_SUBSTITUTION","substitute":"SOME NEW TEXT CONTENT"}
    ///         ]
    ///     }
    applicability_config: std::path::PathBuf,
    /// The input directory to process files.
    in_dir: std::path::PathBuf,
}

impl ValidateDotApplicabilityOptions {
    pub fn new(
        applicability_config: std::path::PathBuf,
        input_directory: std::path::PathBuf,
    ) -> Self {
        ValidateDotApplicabilityOptions {
            applicability_config,
            in_dir: input_directory,
        }
    }
}

pub fn validate(
    args: ValidateDotApplicabilityOptions,
) -> Result<(), ValidateDotApplicabilityErrors> {
    trace!(
        "starting tool with the following parameters: \n\t Applicability Config \t{:#?} \n\t Input Directory \t{:#?} ",
        args.applicability_config, args.in_dir,
    );
    let in_dir = args.in_dir.as_path();
    let unsafe_configuration = read_ple_config_and_bof(args.applicability_config.as_path(), in_dir);
    let safe_configuration = match unsafe_configuration {
        Ok(x) => Ok(x),
        Err(err1) => match err1 {
            pat_config::PleAndBofReadError::PleConfigReadError(ple_config_read_error) => {
                warn!("Failed to read ple-config.toml: {ple_config_read_error:?}");
                Ok((CompletePleConfig::default(), BillOfFeaturesEnum::default()))
            }
            pat_config::PleAndBofReadError::BofConfigReadError(
                read_bill_of_features_config_error,
            ) => Err(read_bill_of_features_config_error),
        },
    };
    let pat_config = match safe_configuration {
        Ok(ref x) => x.0.clone(),
        Err(_) => CompletePleConfig::default(),
    };
    let applic_config = safe_configuration?.1;
    let ple_model_vec = pat_config.features.unwrap_or_default();
    let ple_model = ple_model_vec.as_slice();
    let substitutions = applic_config
        .clone()
        .get_substitutions()
        .unwrap_or_default();
    let thread_pool = rayon::ThreadPoolBuilder::new()
        .num_threads(std::thread::available_parallelism()?.into())
        .build()?;
    let thread_pool_arc = Arc::new(thread_pool);
    let project = discover_project(
        in_dir,
        applicability_project::ProjectMode::All,
        substitutions.as_slice(),
        &applic_config,
        ple_model,
        thread_pool_arc,
    );
    let link_validation_errors = project.validate_links(in_dir);
    if !link_validation_errors.is_empty() {
        return Err(link_validation_errors.into());
    }
    Ok(())
}
#[derive(Debug, Error)]
pub enum ValidateDotApplicabilityErrors {
    #[error("{:?}",.0)]
    ReadBillOfFeaturesConfigError(#[from] ReadBillOfFeaturesConfigError),
    #[error("{:?}",.0)]
    Io(#[from] std::io::Error),
    #[error("Failed to create threadpool: {:?}",.0)]
    ThreadPoolError(#[from] rayon::ThreadPoolBuildError),
    #[error("{}", .0)]
    LinkValidationError(FileApplicabilityLinkValidationErrors),
}

pub struct FileApplicabilityLinkValidationErrors(Vec<FileApplicabilityLinkValidationError>);
impl Debug for FileApplicabilityLinkValidationErrors {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        let mut builder = f.debug_list();
        self.0.iter().for_each(|value| {
            builder.entry(value);
        });
        builder.finish()
    }
}

impl Display for FileApplicabilityLinkValidationErrors {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        for value in &self.0 {
            writeln!(
                f,
                "Link Validation Failed. Path : {}, Error : {}",
                value.path, value.error
            )?;
        }
        Ok(())
    }
}

impl From<Vec<FileApplicabilityLinkValidationError>> for FileApplicabilityLinkValidationErrors {
    fn from(value: Vec<FileApplicabilityLinkValidationError>) -> Self {
        FileApplicabilityLinkValidationErrors(value)
    }
}
impl From<Vec<FileApplicabilityLinkValidationError>> for ValidateDotApplicabilityErrors {
    fn from(value: Vec<FileApplicabilityLinkValidationError>) -> Self {
        Self::LinkValidationError(value.into())
    }
}

impl From<FileApplicabilityLinkValidationErrors> for ValidateDotApplicabilityErrors {
    fn from(value: FileApplicabilityLinkValidationErrors) -> Self {
        Self::LinkValidationError(value)
    }
}
