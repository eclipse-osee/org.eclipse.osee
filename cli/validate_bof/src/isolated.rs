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
use bill_of_features::{
    BillOfFeaturesConfig, BillOfFeaturesConfigGroup, BillOfFeaturesEnum, BillOfFeaturesLegacy,
};
use feature_definition::FeatureDefinition;
use itertools::Itertools;
use thiserror::Error;

pub fn validate(
    ple_model: &[FeatureDefinition],
    bill_of_features: &BillOfFeaturesEnum,
) -> Result<(), BillOfFeaturesValidationError> {
    if ple_model.is_empty() {
        return Ok(());
    }
    match bill_of_features {
        BillOfFeaturesEnum::Config(applicability_config_element_config) => {
            validate_config(ple_model, applicability_config_element_config)
        }
        BillOfFeaturesEnum::ConfigGroup(applicability_config_element_config_group) => {
            validate_config_group(ple_model, applicability_config_element_config_group)
        }
        BillOfFeaturesEnum::LegacyConfig(applicability_config_element_legacy) => {
            validate_legacy(ple_model, applicability_config_element_legacy)
        }
    }
}
fn validate_config_group(
    ple_model: &[FeatureDefinition],
    bill_of_features: &BillOfFeaturesConfigGroup,
) -> Result<(), BillOfFeaturesValidationError> {
    let features = &bill_of_features.features;
    let feature_tags: Vec<_> = features.iter().map(|feature| feature.tag.clone()).collect();
    let ple_model_feature_names: Vec<_> = ple_model.iter().map(|x| x.name.clone()).collect();
    let mut error_collection = vec![];
    features.iter().for_each(|feature| {
        if !ple_model_feature_names.contains(&feature.tag) {
            // add tag to errorlist
            error_collection.push(
                BillOfFeaturesInternalValidationError::TagMissingFromFeatureModel(
                    feature.tag.clone(),
                ),
            );
        } else {
            let contains_value = ple_model
                .iter()
                .filter(|x| x.name == feature.tag)
                .flat_map(|x| x.values.clone())
                .any(|x| x == feature.value);
            if !contains_value {
                //add value to errorlist
                error_collection.push(
                    BillOfFeaturesInternalValidationError::ValueMissingFromFeatureModel(
                        feature.tag.clone(),
                        feature.value.clone(),
                    ),
                );
            }
        }
    });
    ple_model_feature_names.iter().for_each(|feature_name| {
        if !feature_tags.contains(feature_name) {
            error_collection.push(
                BillOfFeaturesInternalValidationError::TagMissingFromBillOfFeatures(
                    feature_name.clone(),
                ),
            );
        }
    });
    let bof_feature_map = features
        .iter()
        .map(|x| (x.tag.clone(), x.value.clone()))
        .into_group_map();
    for feature in bof_feature_map {
        let ple_model_entry = ple_model.iter().find(|x| x.name == feature.0);
        if let Some(entry) = ple_model_entry
            && !entry.allow_multiple_values
            && feature.1.len() > 1
        {
            error_collection.push(BillOfFeaturesInternalValidationError::FailedMultiValueTest(
                feature.0, feature.1,
            ));
        }
    }
    if !error_collection.is_empty() {
        return Err(BillOfFeaturesValidationError {
            errors: error_collection,
        });
    }
    Ok(())
}

fn validate_config(
    ple_model: &[FeatureDefinition],
    bill_of_features: &BillOfFeaturesConfig,
) -> Result<(), BillOfFeaturesValidationError> {
    let features = &bill_of_features.features;
    let feature_tags: Vec<_> = features.iter().map(|feature| feature.tag.clone()).collect();
    let ple_model_feature_names: Vec<_> = ple_model.iter().map(|x| x.name.clone()).collect();
    let mut error_collection = vec![];
    features.iter().for_each(|feature| {
        if !ple_model_feature_names.contains(&feature.tag) {
            // add tag to errorlist
            error_collection.push(
                BillOfFeaturesInternalValidationError::TagMissingFromFeatureModel(
                    feature.tag.clone(),
                ),
            );
        } else {
            let contains_value = ple_model
                .iter()
                .filter(|x| x.name == feature.tag)
                .flat_map(|x| x.values.clone())
                .any(|x| x == feature.value);
            if !contains_value {
                //add value to errorlist
                error_collection.push(
                    BillOfFeaturesInternalValidationError::ValueMissingFromFeatureModel(
                        feature.tag.clone(),
                        feature.value.clone(),
                    ),
                );
            }
        }
    });
    ple_model_feature_names.iter().for_each(|feature_name| {
        if !feature_tags.contains(feature_name) {
            error_collection.push(
                BillOfFeaturesInternalValidationError::TagMissingFromBillOfFeatures(
                    feature_name.clone(),
                ),
            );
        }
    });
    let bof_feature_map = features
        .iter()
        .map(|x| (x.tag.clone(), x.value.clone()))
        .into_group_map();
    for feature in bof_feature_map {
        let ple_model_entry = ple_model.iter().find(|x| x.name == feature.0);
        if let Some(entry) = ple_model_entry
            && !entry.allow_multiple_values
            && feature.1.len() > 1
        {
            error_collection.push(BillOfFeaturesInternalValidationError::FailedMultiValueTest(
                feature.0, feature.1,
            ));
        }
    }
    if !error_collection.is_empty() {
        return Err(BillOfFeaturesValidationError {
            errors: error_collection,
        });
    }
    Ok(())
}

fn validate_legacy(
    ple_model: &[FeatureDefinition],
    bill_of_features: &BillOfFeaturesLegacy,
) -> Result<(), BillOfFeaturesValidationError> {
    let features = &bill_of_features.features;
    let feature_tags: Vec<_> = features.iter().map(|feature| feature.tag.clone()).collect();
    let ple_model_feature_names: Vec<_> = ple_model.iter().map(|x| x.name.clone()).collect();
    let mut error_collection = vec![];
    features.iter().for_each(|feature| {
        if !ple_model_feature_names.contains(&feature.tag) {
            // add tag to errorlist
            error_collection.push(
                BillOfFeaturesInternalValidationError::TagMissingFromFeatureModel(
                    feature.tag.clone(),
                ),
            );
        } else {
            let contains_value = ple_model
                .iter()
                .filter(|x| x.name == feature.tag)
                .flat_map(|x| x.values.clone())
                .any(|x| x == feature.value);
            if !contains_value {
                //add value to errorlist
                error_collection.push(
                    BillOfFeaturesInternalValidationError::ValueMissingFromFeatureModel(
                        feature.tag.clone(),
                        feature.value.clone(),
                    ),
                );
            }
        }
    });
    ple_model_feature_names.iter().for_each(|feature_name| {
        if !feature_tags.contains(feature_name) {
            error_collection.push(
                BillOfFeaturesInternalValidationError::TagMissingFromBillOfFeatures(
                    feature_name.clone(),
                ),
            );
        }
    });
    let bof_feature_map = features
        .iter()
        .map(|x| (x.tag.clone(), x.value.clone()))
        .into_group_map();
    for feature in bof_feature_map {
        let ple_model_entry = ple_model.iter().find(|x| x.name == feature.0);
        if let Some(entry) = ple_model_entry
            && !entry.allow_multiple_values
            && feature.1.len() > 1
        {
            error_collection.push(BillOfFeaturesInternalValidationError::FailedMultiValueTest(
                feature.0, feature.1,
            ));
        }
    }
    if !error_collection.is_empty() {
        return Err(BillOfFeaturesValidationError {
            errors: error_collection,
        });
    }
    Ok(())
}
#[derive(Debug)]
pub struct BillOfFeaturesValidationError {
    pub errors: Vec<BillOfFeaturesInternalValidationError>,
}
#[derive(Debug, Error, Clone, PartialEq)]
pub enum BillOfFeaturesInternalValidationError {
    #[error("Tag Missing From PLE Model: {:#?}",.0)]
    TagMissingFromFeatureModel(String),
    #[error("Value Missing From PLE Model: {:#?} for Tag {:#?}",.1,.0)]
    ValueMissingFromFeatureModel(String, String),
    #[error("Tag Missing From Bill Of Features: {:#?}",.0)]
    TagMissingFromBillOfFeatures(String),
    #[error("Tag has more values than expected: {:?}. Found {:?} tags.",.0,.1.len())]
    FailedMultiValueTest(String, Vec<String>),
}

#[cfg(test)]
mod tests {
    use applicability::applic_tag::ApplicabilityTag;
    use bill_of_features::{
        BillOfFeaturesConfig, BillOfFeaturesConfigGroup, BillOfFeaturesEnum, BillOfFeaturesLegacy,
    };
    use feature_definition::FeatureDefinition;

    use crate::{BillOfFeaturesInternalValidationError, validate};
    #[test]
    fn test_validate_missing_tag_from_model() {
        let ple_model = vec![FeatureDefinition {
            name: "ROBOT_SPKR".to_string(),
            values: vec!["SPKR_A".to_string(), "SPKR_B".to_string()],
            description: "".to_string(),
            product_applicabilities: None,
            applic_constraint: None,
            allow_multiple_values: true,
        }];
        let bill_of_features = BillOfFeaturesEnum::Config(BillOfFeaturesConfig {
            name: "PRODUCT_A".to_string(),
            group: "".to_string(),
            features: vec![
                ApplicabilityTag {
                    tag: "JHU_CONTROLLER".to_string(),
                    value: "Included".to_string(),
                },
                ApplicabilityTag {
                    tag: "ROBOT_SPKR".to_string(),
                    value: "SPKR_B".to_string(),
                },
            ],
            substitutions: None,
        });
        let results = validate(ple_model.as_slice(), &bill_of_features);
        assert!(results.is_err());
        let unwrapped_results = results.unwrap_err();
        assert_eq!(unwrapped_results.errors.len(), 1);
        unwrapped_results.errors.iter().for_each(|error| {
            assert_eq!(
                error,
                &BillOfFeaturesInternalValidationError::TagMissingFromFeatureModel(
                    "JHU_CONTROLLER".to_string()
                )
            )
        });
    }

    #[test]
    fn test_validate_missing_value_from_model() {
        let ple_model = vec![FeatureDefinition {
            name: "ROBOT_SPKR".to_string(),
            values: vec!["SPKR_A".to_string(), "SPKR_B".to_string()],
            description: "".to_string(),
            product_applicabilities: None,
            applic_constraint: None,
            allow_multiple_values: true,
        }];
        let bill_of_features = BillOfFeaturesEnum::ConfigGroup(BillOfFeaturesConfigGroup {
            name: "PRODUCT_A".to_string(),
            group: None,
            configs: vec![],
            features: vec![ApplicabilityTag {
                tag: "ROBOT_SPKR".to_string(),
                value: "SPKR_D".to_string(),
            }],
            substitutions: None,
        });
        let results = validate(ple_model.as_slice(), &bill_of_features);
        assert!(results.is_err());
        let unwrapped_results = results.unwrap_err();
        assert_eq!(unwrapped_results.errors.len(), 1);
        unwrapped_results.errors.iter().for_each(|error| {
            assert_eq!(
                error,
                &BillOfFeaturesInternalValidationError::ValueMissingFromFeatureModel(
                    "ROBOT_SPKR".to_string(),
                    "SPKR_D".to_string()
                )
            )
        });
    }

    #[test]
    fn test_validate_missing_tag_from_bof() {
        let ple_model = vec![FeatureDefinition {
            name: "ROBOT_SPKR".to_string(),
            values: vec!["SPKR_A".to_string(), "SPKR_B".to_string()],
            description: "".to_string(),
            product_applicabilities: None,
            applic_constraint: None,
            allow_multiple_values: true,
        }];
        let bill_of_features = BillOfFeaturesEnum::LegacyConfig(BillOfFeaturesLegacy {
            normalized_name: "PRODUCT_A".to_string(),
            parent_group: None,
            features: vec![],
            substitutions: None,
        });
        let results = validate(ple_model.as_slice(), &bill_of_features);
        assert!(results.is_err());
        let unwrapped_results = results.unwrap_err();
        assert_eq!(unwrapped_results.errors.len(), 1);
        unwrapped_results.errors.iter().for_each(|error| {
            assert_eq!(
                error,
                &BillOfFeaturesInternalValidationError::TagMissingFromBillOfFeatures(
                    "ROBOT_SPKR".to_string()
                )
            )
        });
    }

    #[test]
    fn test_do_not_validate_empty_ple_model() {
        let ple_model = vec![];
        let bill_of_features = BillOfFeaturesEnum::Config(BillOfFeaturesConfig {
            name: "PRODUCT_A".to_string(),
            group: "".to_string(),
            features: vec![],
            substitutions: None,
        });
        let results = validate(ple_model.as_slice(), &bill_of_features);
        assert!(results.is_ok());
    }
}
