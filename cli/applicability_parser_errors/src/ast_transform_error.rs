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
use applicability_lexer_base::position::TokenPosition;
use thiserror::Error;

#[derive(Debug, Error, Clone, PartialEq)]
pub enum AstTransformError {
    #[error("Missing End Feature on line: {:?} at column {:?}",.0.0.1, .0.0.2)]
    MissingEndFeature(TokenPosition),
    #[error("Missing End Configuration on line: {:?} at column {:?}",.0.0.1, .0.0.2)]
    MissingEndConfiguration(TokenPosition),
    #[error("Missing End Configuration Group on line: {:?} at column {:?}",.0.0.1, .0.0.2)]
    MissingEndConfigurationGroup(TokenPosition),
    #[error("Unexpected Feature on line: {:?} at column {:?}",.0.0.1, .0.0.2)]
    UnexpectedFeature(TokenPosition),
    #[error("Unexpected Feature Not on line: {:?} at column {:?}",.0.0.1, .0.0.2)]
    UnexpectedFeatureNot(TokenPosition),
    #[error("Unexpected Feature Switch on line: {:?} at column {:?}",.0.0.1, .0.0.2)]
    UnexpectedFeatureSwitch(TokenPosition),
    #[error("Unexpected Feature Case on line: {:?} at column {:?}",.0.0.1, .0.0.2)]
    UnexpectedFeatureCase(TokenPosition),
    #[error("Unexpected Feature Else on line: {:?} at column {:?}",.0.0.1, .0.0.2)]
    UnexpectedFeatureElse(TokenPosition),
    #[error("Unexpected Feature Else If on line: {:?} at column {:?}",.0.0.1, .0.0.2)]
    UnexpectedFeatureElseIf(TokenPosition),
    #[error("Unexpected End Feature on line: {:?} at column {:?}",.0.0.1, .0.0.2)]
    UnexpectedEndFeature(TokenPosition),
    #[error("Unexpected Configuration on line: {:?} at column {:?}",.0.0.1, .0.0.2)]
    UnexpectedConfiguration(TokenPosition),
    #[error("Unexpected Configuration Not on line: {:?} at column {:?}",.0.0.1, .0.0.2)]
    UnexpectedConfigurationNot(TokenPosition),
    #[error("Unexpected Configuration Switch on line: {:?} at column {:?}",.0.0.1, .0.0.2)]
    UnexpectedConfigurationSwitch(TokenPosition),
    #[error("Unexpected Configuration Case on line: {:?} at column {:?}",.0.0.1, .0.0.2)]
    UnexpectedConfigurationCase(TokenPosition),
    #[error("Unexpected Configuration Else on line: {:?} at column {:?}",.0.0.1, .0.0.2)]
    UnexpectedConfigurationElse(TokenPosition),
    #[error("Unexpected Configuration Else If on line: {:?} at column {:?}",.0.0.1, .0.0.2)]
    UnexpectedConfigurationElseIf(TokenPosition),
    #[error("Unexpected End Configuration on line: {:?} at column {:?}",.0.0.1, .0.0.2)]
    UnexpectedEndConfiguration(TokenPosition),
    #[error("Unexpected Configuration Group on line: {:?} at column {:?}",.0.0.1, .0.0.2)]
    UnexpectedConfigurationGroup(TokenPosition),
    #[error("Unexpected Configuration Group Not on line: {:?} at column {:?}",.0.0.1, .0.0.2)]
    UnexpectedConfigurationGroupNot(TokenPosition),
    #[error("Unexpected Configuration Group Switch on line: {:?} at column {:?}",.0.0.1, .0.0.2)]
    UnexpectedConfigurationGroupSwitch(TokenPosition),
    #[error("Unexpected Configuration Group Case on line: {:?} at column {:?}",.0.0.1, .0.0.2)]
    UnexpectedConfigurationGroupCase(TokenPosition),
    #[error("Unexpected Configuration Group Else on line: {:?} at column {:?}",.0.0.1, .0.0.2)]
    UnexpectedConfigurationGroupElse(TokenPosition),
    #[error("Unexpected Configuration Group Else If on line: {:?} at column {:?}",.0.0.1, .0.0.2)]
    UnexpectedConfigurationGroupElseIf(TokenPosition),
    #[error("Unexpected End Configuration Group on line: {:?} at column {:?}",.0.0.1, .0.0.2)]
    UnexpectedEndConfigurationGroup(TokenPosition),
}
