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
use applicability::substitution::Substitution;
use applicability_path::FileApplicabilityPath;
use bill_of_features::BillOfFeaturesEnum;
use feature_definition::FeatureDefinition;
use jwalk::Parallelism;
use jwalk::WalkDir;
use rayon::ThreadPool;
use std::{path::Path, sync::Arc};

use crate::ApplicabilityProject;
use crate::FileApplicabilityEntry;
use crate::ProjectMode;
use crate::applicability_project_file::get_applicability_project_file_contents_based_on_applicability;
use crate::is_applicability_project_file;

pub fn discover_project(
    input_directory: &Path,
    mode: ProjectMode,
    substitutions: &[Substitution],
    bill_of_features: &BillOfFeaturesEnum,
    ple_model: &[FeatureDefinition],
    threadpool: Arc<ThreadPool>,
) -> ApplicabilityProject {
    let initial_project = ApplicabilityProject {
        mode,
        current_directory: input_directory.to_path_buf(),
        starting_directory: input_directory.to_path_buf(),
        paths: vec![],
    };
    let mut project = WalkDir::new(input_directory)
        .skip_hidden(false)
        .parallelism(Parallelism::RayonExistingPool(threadpool))
        .into_iter()
        .map(|entr| match entr {
            Ok(entr) => entr,
            Err(err) => panic!("Error unwrapping directory listing {err:#?}"),
        })
        .filter(|dir_entry| is_applicability_project_file(&dir_entry.path()))
        .flat_map(
            |dir_entry| match get_applicability_project_file_contents_based_on_applicability(
                &dir_entry.path(),
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
                        path: dir_entry
                            .path()
                            .parent()
                            .unwrap_or(Path::new(""))
                            .to_owned(),
                        entry: f,
                    })
                    .collect::<Vec<_>>(),
                Err(_) => vec![],
            },
        )
        .fold(initial_project.clone(), |mut acc, file_entry| {
            acc.current_directory = file_entry.clone().path;
            acc += file_entry;
            acc
        });
    //move back to starting position so future processing can occur
    project.current_directory = project.starting_directory.clone();
    project
}
