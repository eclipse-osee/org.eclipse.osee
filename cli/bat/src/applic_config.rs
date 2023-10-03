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
use applicability::applic_tag::ApplicabilityTag;
use applicability_parser::substitute_applicability::Substitution;
use serde::Deserialize;

/// Applicability Config to setup valid features for parser
#[derive(Debug, Deserialize, Clone)]
#[serde(rename_all = "camelCase")]
pub struct BatConfigElement {
    ///Name of the configuration or configuration group
    pub normalized_name: String,
    /// list of valid feature tags to parse for this configuration
    pub features: Vec<ApplicabilityTag>,
    /// list of valid substitutions to make for this configuration
    pub substitutions: Option<Vec<Substitution>>,
}
