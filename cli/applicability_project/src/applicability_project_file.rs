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
use std::{ffi::OsStr, path::Path};

use applicability::substitution::Substitution;
use applicability_parser_config::{get_config_from_file, get_file_contents};
use applicability_parser_errors::ApplicabilityParserError;
use applicability_path::{FileApplicabilityPath, ParsePaths};
use applicability_tokens_to_ast::tree::ApplicabilityExprKind;
use bill_of_features::{BillOfFeatures, BillOfFeaturesEnum};
use feature_definition::FeatureDefinition;

fn is_file(entry: &Path) -> bool {
    entry.is_file()
}
pub fn is_applicability_project_file(entry: &Path) -> bool {
    is_file(entry)
        && (entry
            .file_stem()
            .unwrap_or(OsStr::new(""))
            .to_str()
            .map(|e| matches!(e, ".fileApplicability" | ".applicability"))
            .unwrap_or(false)
            || entry
                .extension()
                .unwrap_or(OsStr::new(""))
                .to_str()
                .map(|e| matches!(e, "fileApplicability" | "applicability"))
                .unwrap_or(false))
}

pub(crate) fn get_applicability_project_file_contents_based_on_applicability(
    dir_entry: &Path,
    substitutions: Vec<Substitution>,
    applic_config: BillOfFeaturesEnum,
    ple_model: &[FeatureDefinition<String>],
) -> Result<Vec<FileApplicabilityPath>, ApplicabilityParserError> {
    let _file_name = dir_entry.to_str().unwrap_or_default();
    let file_contents = get_file_contents(dir_entry);
    let file_path = dir_entry;
    let parser_fn = get_config_from_file(file_path);
    let parsed_contents = parser_fn(file_contents.as_str());
    match parsed_contents {
        Ok(c) => {
            let contents = c
                .into_iter()
                .map(Into::<ApplicabilityExprKind<String>>::into)
                .collect::<Vec<_>>();
            let group = applic_config.get_parent_group().map(|x| x.to_string());
            let configs = applic_config
                .get_configs()
                .iter()
                .map(|x| x.to_string())
                .collect::<Vec<_>>();
            Ok(contents
                .iter()
                .flat_map(|c| {
                    c.parse_path(
                        applic_config.clone().get_features().as_slice(),
                        &applic_config.clone().get_name(),
                        &substitutions,
                        group.as_ref(),
                        Some(configs.as_slice()),
                        Some(true),
                        ple_model,
                    )
                })
                .collect())
        }
        Err(e) => Err(e),
    }
}
