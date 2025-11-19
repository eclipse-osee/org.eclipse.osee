use bill_of_features::{BillOfFeaturesEnum, ReadBillOfFeaturesConfigError};
use pat_config::{CompletePleConfig, read_ple_config_and_bof};
use thiserror::Error;
use tracing::{error, warn};

pub struct ValidateBillOfFeaturesOptions {
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

impl ValidateBillOfFeaturesOptions {
    pub fn new(applicability_config: std::path::PathBuf, in_dir: std::path::PathBuf) -> Self {
        ValidateBillOfFeaturesOptions {
            applicability_config,
            in_dir,
        }
    }
}

pub fn validate_bill_of_features_project(
    args: ValidateBillOfFeaturesOptions,
) -> Result<(), ValidateBillOfFeaturesError> {
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
    let results = crate::validate(ple_model, &applic_config);
    if let Err(unwrapped_results) = results {
        unwrapped_results.errors.iter().for_each(|e1| match e1 {
            crate::BillOfFeaturesInternalValidationError::TagMissingFromFeatureModel(tag) => {
                error!("Tag Missing From PLE Model: {:#?}", tag)
            }
            crate::BillOfFeaturesInternalValidationError::ValueMissingFromFeatureModel(
                tag,
                value,
            ) => error!(
                "Value Missing From PLE Model: {:#?} for Tag {:#?}",
                value, tag
            ),
            crate::BillOfFeaturesInternalValidationError::TagMissingFromBillOfFeatures(tag) => {
                warn!("Tag Missing From Bill Of Features: {:#?}", tag)
            }
            crate::BillOfFeaturesInternalValidationError::FailedMultiValueTest(tag, items) => {
                error!(
                    "Tag has more values than expected: {:?}. Found {:?} tags.",
                    tag,
                    items.len()
                )
            }
        });
        if !unwrapped_results.errors.iter().filter(|x| matches!(x, crate::BillOfFeaturesInternalValidationError::TagMissingFromFeatureModel(_) | crate::BillOfFeaturesInternalValidationError::ValueMissingFromFeatureModel(_,_) | crate::BillOfFeaturesInternalValidationError::FailedMultiValueTest(_, _))).collect::<Vec<_>>().is_empty() {
            return Err(ValidateBillOfFeaturesError::ValidationFailed);
        }
    }
    Ok(())
}

#[derive(Debug, Error)]
pub enum ValidateBillOfFeaturesError {
    #[error("Validation Failed")]
    ValidationFailed,
    #[error("{:?}", .0)]
    ReadBillOfFeaturesConfigError(#[from] ReadBillOfFeaturesConfigError),
}
