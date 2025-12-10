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
    fs::create_dir_all,
    sync::{
        Arc,
        mpsc::{self},
    },
};

use crate::cli_options::PatInternalCliOptions;
use crate::util::{
    PATCliError, PATCliErrorBox, ProcessFileError, WriteProjectedFileError, process_file,
};
use anyhow::{Context, Result};
use applicability_project::{ProjectMode, discover_project, is_applicability_project_file};
use bill_of_features::{BillOfFeatures, BillOfFeaturesEnum};
use jwalk::{Parallelism, WalkDir};
use pat_config::{CompletePleConfig, read_ple_config_and_bof};
use rayon::iter::{ParallelBridge, ParallelIterator};
use tracing::{Span, error, trace, warn};
use tracing_indicatif::span_ext::IndicatifSpanExt;
pub fn project_repository_initial(args: PatInternalCliOptions, header_span: &Span) -> Result<()> {
    let mut error_pool: PATCliErrorBox<String> = PATCliErrorBox { errors: vec![] };
    let in_dir = args.in_dir.as_path();
    let out_dir = args.out_dir.as_path();
    let exclude_by_default = args.exclude;
    let hide_by_default = args.skip_hidden;
    trace!(
        "starting tool with the following parameters: \n\t Applicability Config \t{:#?} \n\t Output Directory \t{:#?} \n\t Input Directory \t{:#?} \n\t Exclude Mode: \t\t{:#?} \n\t Skip Hidden: \t\t{:#?}",
        args.applicability_config, args.out_dir, args.in_dir, exclude_by_default, hide_by_default
    );
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
    let bill_of_features = safe_configuration?.1;
    let ple_model_vec = pat_config.features.unwrap_or_default();
    let ple_model = ple_model_vec.as_slice();
    let inline_projection_exclusions = pat_config.project.inline_projection_exclusions;
    let results = validate_bof::validate(ple_model, &bill_of_features);
    if let Err(unwrapped_results) = results {
        let iter = unwrapped_results
            .errors
            .iter()
            .cloned()
            .map(|e1| e1.try_into());
        iter.clone().for_each(|e1| match e1 {
            Ok(x) => {
                error_pool.errors.push(x);
            }
            Err(e) => warn!("{}", e),
        });
        let should_add_error = !iter.filter(|x| x.is_ok()).collect::<Vec<_>>().is_empty();
        if should_add_error {
            error_pool.errors.push(PATCliError::ValidationFailed);
        }
    }
    let substitutions = bill_of_features
        .clone()
        .get_substitutions()
        .unwrap_or_default();
    let thread_pool = rayon::ThreadPoolBuilder::new()
        .num_threads(
            std::thread::available_parallelism()
                .with_context(|| "Failed to get thread count".to_string())?
                .get(),
        )
        .build()
        .with_context(|| "Failed to create thread pool".to_string())?;
    let thread_pool_arc = Arc::new(thread_pool);
    // find the vector of paths from all the .fileApplicability and .applicability files
    // capture the exact path referenced by the .fileApplicability
    header_span.pb_set_message("Finding routes to process.");
    let project_configuration = discover_project(
        in_dir,
        match exclude_by_default {
            true => ProjectMode::Exclude,
            false => ProjectMode::Include,
        },
        substitutions.as_slice(),
        &bill_of_features,
        ple_model,
        thread_pool_arc.clone(),
    );

    //pre-create the output directory
    create_dir_all(out_dir)
        .with_context(|| format!("Failed to create output directory {out_dir:#?}"))?;
    //re walk over the tree, processing each file and excluding or including based on the include param and the found_dirs list
    header_span.pb_set_message("Processing files.");
    let (error_tx, error_rx) = mpsc::channel();
    WalkDir::new(in_dir)
        .skip_hidden(hide_by_default)
        .parallelism(Parallelism::RayonExistingPool(thread_pool_arc.clone()))
        .into_iter()
        .par_bridge()
        .map(|e| match e {
            Ok(entr) => entr,
            Err(err) => {
                panic!("Error reading directory {err:#?}")
            }
        })
        .filter(|dir_entry| !is_applicability_project_file(&dir_entry.path()))
        .filter(|dir_entry| {
            //
            // If include mode:
            // Include all folders
            // If a folder/file is applicable, include it
            // If a folder/file is not applicable, do not include it
            // If a folder/file has no featurization, exclude it
            //
            // Detailed:
            // If a folder/file's parent folder is applicable, return it,
            // If a folder/file is applicable, return it,
            // If the glob pattern is applicable, return it
            //
            // If a folder/file's parent folder is not applicable, exclude it
            // If a folder/file is not applicable, exclude it
            // If a glob pattern matches for a not applicable reference, exclude it
            //
            // Treat No featurization as an error
            //
            project_configuration.path_is_allowed(&dir_entry.path()) || dir_entry.path() == in_dir
        })
        .for_each(|entry| {
            rayon::scope(|_| {
                let thread_tx = error_tx.clone();
                //create the location in the output folder where the file will exist
                let path = &entry.path();
                let projection_result = process_file(
                    path,
                    header_span,
                    in_dir,
                    out_dir,
                    inline_projection_exclusions.as_slice(),
                    substitutions.as_slice(),
                    &bill_of_features,
                    ple_model,
                );
                if let Err(e) = projection_result {
                    if let ProcessFileError::WriteProjectedFileError(write_projected_file_error) = e
                    {
                        if let WriteProjectedFileError::PATCliError(patcli_error_box) =
                            write_projected_file_error
                        {
                            let _ = thread_tx.send(patcli_error_box.errors);
                        }
                    } else {
                        panic!("{}", e)
                    }
                }
            });
        });
    drop(error_tx);
    for error in error_rx {
        error_pool.errors.extend(error);
    }
    error_pool.errors.iter().for_each(|err| error!("{}", err));
    if !error_pool.errors.is_empty() {
        return Err(anyhow::Error::msg(
            "Errors encountered during repository projection",
        ));
    }
    Ok(())
}
