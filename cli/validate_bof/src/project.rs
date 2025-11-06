use bill_of_features::{ReadBillOfFeaturesConfigError, read_bill_of_features};
use pat_config::read_ple_config_with_starting_path;
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
    let mut applic_config = read_bill_of_features(args.applicability_config)?;
    let pat_config = read_ple_config_with_starting_path(in_dir);
    if let Ok(config) = &pat_config
        && let Some(new_config) = &config.config
    {
        applic_config.merge(new_config);
    };
    let ple_model = match &pat_config {
        Ok(config) => match &config.features {
            Some(f) => f.as_slice(),
            _ => &[],
        },
        Err(_) => &[],
    };
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
    #[error("ValidationFailed")]
    ValidationFailed,
    #[error("{:?}", .0)]
    ReadBillOfFeaturesConfigError(#[from] ReadBillOfFeaturesConfigError),
}
