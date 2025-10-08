use applicability_parser_types::applic_tokens::MatchApplicabilityInternalError;
/*********************************************************************
 * Copyright (c) 2024 Boeing
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
use feature_definition::FeatureDefinition;
use thiserror::Error;
pub trait MatchApplicability<T> {
    type TagType;
    fn match_applicability(
        &self,
        match_list: &[T],
        config_name: &Self::TagType,
        parent_group: Option<&Self::TagType>,
        child_configurations: Option<&[Self::TagType]>,
        ple_model: &[FeatureDefinition<Self::TagType>],
    ) -> Result<bool, MatchApplicabilityError<Self::TagType>>;
}
#[derive(Debug, Error, Clone, PartialEq)]
pub enum MatchApplicabilityError<I1> {
    #[error("Feature Tag does not exist in the PLE Model: {0}")]
    FeatureTagDoesNotExist(I1),
    #[error("Feature Value does not exist does not exist in the PLE Model: {0}")]
    FeatureValueDoesNotExist(String),
}
impl<I1> From<MatchApplicabilityInternalError<I1>> for MatchApplicabilityError<I1> {
    fn from(value: MatchApplicabilityInternalError<I1>) -> Self {
        match value {
            MatchApplicabilityInternalError::FeatureTagDoesNotExist(i) => {
                Self::FeatureTagDoesNotExist(i)
            }
            MatchApplicabilityInternalError::FeatureValueDoesNotExist(i) => {
                Self::FeatureValueDoesNotExist(i)
            }
        }
    }
}
