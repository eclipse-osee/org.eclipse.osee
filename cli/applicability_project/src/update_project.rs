use std::path::Path;

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
use crate::{
    ApplicabilityProject, FileApplicabilityEntry,
    applicability_project_file::get_applicability_project_file_contents_based_on_applicability,
    is_applicability_project_file,
};
use applicability::substitution::Substitution;
use applicability_path::FileApplicabilityPath;
use bill_of_features::BillOfFeaturesEnum;
use feature_definition::FeatureDefinition;
use path_slash::PathExt;
pub fn update_project(
    initial_project: &ApplicabilityProject,
    path_to_update: &std::path::Path,
    substitutions: &[Substitution],
    bill_of_features: &BillOfFeaturesEnum,
    ple_model: &[FeatureDefinition],
) -> ApplicabilityProject {
    let mut project = initial_project.clone();
    let targeted_path = path_to_update.to_slash_lossy();
    //paths to retain
    let base_project_paths = initial_project
        .paths
        .iter()
        .filter(|x| !x.path.to_slash_lossy().contains(&targeted_path.to_string()))
        .cloned()
        .collect::<Vec<_>>();
    //paths which lie underneath the current path, but aren't the current path
    let paths_to_update = initial_project
        .paths
        .iter()
        .filter(|x| {
            x.path.to_slash_lossy().contains(&targeted_path.to_string())
                && x.path.to_slash_lossy() != targeted_path
        })
        .cloned()
        .collect::<Vec<_>>();
    project.paths = base_project_paths;
    //check if project has paths underneath this location, if so, files need to also be scanned after updating this path
    if is_applicability_project_file(path_to_update) {
        let sanitized_contents =
            match get_applicability_project_file_contents_based_on_applicability(
                path_to_update,
                substitutions,
                bill_of_features,
                ple_model,
            ) {
                Ok(contents) => contents
                    .iter()
                    .cloned()
                    .flat_map(|c| match c {
                        FileApplicabilityPath::Included(text) => text
                            .lines()
                            .map(|x| FileApplicabilityPath::Included(x.to_string()))
                            .collect::<Vec<FileApplicabilityPath>>(),
                        FileApplicabilityPath::Excluded(text) => text
                            .lines()
                            .map(|x| FileApplicabilityPath::Excluded(x.to_string()))
                            .collect(),
                        FileApplicabilityPath::Text(text) => text
                            .lines()
                            .map(|x| FileApplicabilityPath::Text(x.to_string()))
                            .collect(),
                    })
                    .map(|f| FileApplicabilityEntry {
                        path: path_to_update.parent().unwrap_or(Path::new("")).to_owned(),
                        entry: f,
                    })
                    .collect::<Vec<_>>(),
                Err(_) => vec![],
            };
        project.current_directory = path_to_update.to_path_buf();
        sanitized_contents.into_iter().for_each(|x| project += x);
        paths_to_update.iter().for_each(|x| {
            project = update_project(
                &project,
                &x.path,
                substitutions,
                bill_of_features,
                ple_model,
            )
        });
        project.current_directory = project.starting_directory.clone();
        // base_project_paths.extend(sanitized_contents);
    } else {
        //zero out any entries under this path, by doing nothing
    }
    project
}
