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
    env,
    ffi::OsStr,
    fs::{self, File, create_dir_all, read_to_string},
    io::ErrorKind,
    path::{Path, PathBuf},
    sync::Arc,
};

use anyhow::{Context, Result};
use applicability::substitution::Substitution;
use applicability_parser_config::{
    applic_config::{ApplicabilityConfig, ApplicabilityConfigElement},
    get_config_from_file, get_file_contents, is_schema_supported,
};
use applicability_path::{FileApplicabilityPath, ParsePaths};
use applicability_sanitization::v2::SanitizeApplicabilityV2;
use applicability_tokens_to_ast::tree::ApplicabilityExprKind;
use clap::{ArgAction, Parser};
use clap_verbosity_flag::{Verbosity, WarnLevel};
use doc_definitions::{
    applic_file_example, applicability_definitions, dot_applicability_syntax_and_notes,
    pat_config_example, pat_config_note, ple_applicability_tag_syntax_rules, supported_file_types,
};
use feature_definition::FeatureDefinition;
use globset::Glob;
use jwalk::{ClientState, DirEntry, Parallelism, WalkDir};
use pat_config::{CompletePatConfig, from_str};
use path_slash::PathExt;
use rayon::iter::{ParallelBridge, ParallelIterator};
use thiserror::Error;
use tracing::{Level, Span, error, trace, trace_span, warn};
use tracing_indicatif::span_ext::IndicatifSpanExt;

#[derive(Parser, Debug)]
#[clap(
    author = "Luciano Vaglienti",
    version,
    about = PatCliOptions::about(),
    long_about = PatCliOptions::long_about()
)]
pub struct PatCliOptions {
    #[command(flatten)]
    pub options: PatInternalCliOptions,
    /// Verbosity of output, defaults to warnings and errors.
    /// -q will have no output
    /// -v will show warnings,info and errors
    /// -vv will show warnings,info,errors, and debug
    /// -vvv will show warnings,info,errors, debug and trace output
    #[command(flatten)]
    pub verbose: Verbosity<WarnLevel>,
}

impl PatCliOptions {
    pub fn about() -> String {
        "".to_string()
            + r#"Project Applicability Tool(PAT)
----------------------------------------------------{n}"#
            + &PatInternalCliOptions::pat_about()
    }
    pub fn long_about() -> String {
        "".to_string()
            + r#"Project Applicability Tool(PAT)
----------------------------------------------------{n}"#
            + &PatInternalCliOptions::pat_long_about()
    }
}

#[derive(Parser, Debug)]
#[clap(author = "Luciano Vaglienti", version,about=PatInternalCliOptions::ple_about(), long_about=PatInternalCliOptions::ple_long_about(), verbatim_doc_comment)]
pub struct PatInternalCliOptions {
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
    #[clap(short, long, verbatim_doc_comment)]
    applicability_config: std::path::PathBuf,

    /// The output directory for processed files.
    #[clap(short, long)]
    out_dir: std::path::PathBuf,

    /// The input directory to process files.
    #[clap(short, long)]
    in_dir: std::path::PathBuf,

    /// Excludes folders by default, and treats paths in .fileApplicability/.applicability as files/folders/patterns to include
    #[clap(short = 'x', long)]
    exclude: bool,

    /// Hides dotfiles. Default value: on
    #[clap(short = 'z', long, action=ArgAction::SetFalse)]
    skip_hidden: bool,
}
impl PatInternalCliOptions {
    pub fn base_about() -> String {
        "".to_string()
            + r#"Process .applicability & .fileApplicability files
to include/exclude certain folders/files based on
the PLE model and Bill of Features.{n}"#
    }
    pub fn pat_about() -> String {
        Self::base_about()
            + applicability_definitions()
            + dot_applicability_syntax_and_notes()
            + applic_file_example()
            + "{n}"
            + pat_config_note()
            + pat_config_example()
    }
    pub fn pat_long_about() -> String {
        Self::pat_about()
            + ple_applicability_tag_syntax_rules()
            + supported_file_types()
            + r#"
For unsupported file types, PAT will copy the file directly to the output directory instead of sanitizing it."#
    }
    pub fn ple_about() -> String {
        Self::base_about()
            + dot_applicability_syntax_and_notes()
            + applic_file_example()
            + "{n}"
            + pat_config_note()
            + pat_config_example()
    }
    pub fn ple_long_about() -> String {
        Self::ple_about()
            + applicability_definitions()
            + ple_applicability_tag_syntax_rules()
            + supported_file_types()
            + r#"
For unsupported file types, the PLE Compiler will copy the file directly to the output directory instead of sanitizing it."#
    }
}

fn read_pat_config(starting_path: &std::path::Path) -> Result<CompletePatConfig, anyhow::Error> {
    let config_path = starting_path.join(Path::new("ple-config.toml"));
    let contents = read_to_string(config_path.clone())?;
    let cwd = env::current_dir()?;
    match from_str(
        config_path.parent().unwrap_or(cwd.as_path()).to_owned(),
        &contents,
    ) {
        Ok(o) => Ok(o),
        Err(e) => Err(e.into()),
    }
}

pub fn project_repository(args: PatInternalCliOptions, header_span: Span) -> Result<()> {
    let in_dir = args.in_dir.as_path();
    let out_dir = args.out_dir.as_path();
    let exclude_by_default = args.exclude;
    let hide_by_default = args.skip_hidden;
    trace!(
        "starting tool with the following parameters: \n\t Applicability Config \t{:#?} \n\t Output Directory \t{:#?} \n\t Input Directory \t{:#?} \n\t Exclude Mode: \t\t{:#?} \n\t Skip Hidden: \t\t{:#?}",
        args.applicability_config, args.out_dir, args.in_dir, exclude_by_default, hide_by_default
    );
    let applic_processing = (
        args.applicability_config.clone(),
        args.applicability_config.extension(),
    );
    let mut applic_config: ApplicabilityConfigElement = match applic_processing {
        (path, Some(file_ext)) => match file_ext.to_str() {
            Some("json") => {
                let applic_file = File::open(path);
                match applic_file {
                    Ok(file) => match serde_json::from_reader(file) {
                        Ok(res) => res,
                        Err(e) => panic!(
                            "Could not parse applicability config JSON \n{:?}: \tat line {:?} column {:?}",
                            e.classify(),
                            e.line(),
                            e.column()
                        ),
                    },
                    Err(e) => panic!("Could not find applicability config {e:?}"),
                }
            }
            Some("toml") => {
                let file_contents = read_to_string(path);
                match file_contents {
                    Ok(c) => match toml::de::from_str(&c) {
                        Ok(res) => res,
                        Err(e) => panic!(
                            "Could not parse applicability config TOML \n{:?}: \tat {:?}",
                            e.to_string(),
                            e.line_col()
                        ),
                    },
                    Err(e) => panic!("Could not find applicability config {e:?}"),
                }
            }
            Some(x) => {
                panic!(
                    "Applicability Config has incorrect file extension. Received: {x:#?}, want: [toml, json]"
                )
            }
            _ => {
                panic!("Applicability Config has no file extension")
            }
        },
        _ => {
            panic!("Applicability Config has no file extension")
        }
    };
    // Read the inline_projection_exclusions (as paths) from the pat-config.toml file
    let pat_config = read_pat_config(in_dir);
    let inline_projection_exclusions = match &pat_config {
        Ok(config) => config.project.inline_projection_exclusions.clone(),
        Err(e) => {
            warn!("Failed to read ple-config.toml: {e:?}");
            vec![] // Return an empty list if there's an error
        }
    };
    if let Ok(config) = &pat_config {
        if let Some(new_config) = &config.config {
            applic_config.merge(new_config);
        }
    };
    let ple_model = match &pat_config {
        Ok(config) => match &config.features {
            Some(f) => f.as_slice(),
            None => &[],
        },
        Err(_) => &[],
    };
    let results = validate_bof::validate(ple_model, &applic_config);
    if results.is_err() {
        let unwrapped_results = results.unwrap_err();
        unwrapped_results.errors.iter().for_each(|e1| match e1 {
            validate_bof::BillOfFeaturesInternalValidationError::TagMissingFromFeatureModel(
                tag,
            ) => error!("Tag Missing From PLE Model: {:#?}", tag),
            validate_bof::BillOfFeaturesInternalValidationError::ValueMissingFromFeatureModel(
                tag,
                value,
            ) => error!(
                "Value Missing From PLE Model: {:#?} for Tag {:#?}",
                value, tag
            ),
            validate_bof::BillOfFeaturesInternalValidationError::TagMissingFromBillOfFeatures(
                tag,
            ) => warn!("Tag Missing From Bill Of Features: {:#?}", tag),
        });
        if !unwrapped_results.errors.iter().filter(|x| matches!(x, validate_bof::BillOfFeaturesInternalValidationError::TagMissingFromFeatureModel(_) | validate_bof::BillOfFeaturesInternalValidationError::ValueMissingFromFeatureModel(_,_))).collect::<Vec<_>>().is_empty() {
            return Err(PATCliError::ValidationFailed.into());
        }
    }
    let substitutions = applic_config
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
    let found_dirs = WalkDir::new(in_dir)
        .skip_hidden(false)
        .parallelism(Parallelism::RayonExistingPool(thread_pool_arc.clone()))
        .into_iter()
        .map(|entr| match entr {
            Ok(entr) => entr,
            Err(err) => panic!("Error unwrapping directory listing {err:#?}"),
        })
        .filter(is_file_applicability_file)
        .flat_map(|dir_entry| {
            get_file_applicability_contents_based_on_applicability(
                &dir_entry,
                substitutions.clone(),
                applic_config.clone(),
                ple_model,
            )
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
            .map(|f| {
                (
                    dir_entry
                        .path()
                        .parent()
                        .unwrap_or(Path::new(""))
                        .to_owned(),
                    f,
                )
            })
            .collect::<Vec<_>>()
        })
        .fold(vec![], |mut acc, (current_path, found_path)| {
            //collect only paths that are previously made available in the list
            match exclude_by_default {
                true => add_path_to_file_applicability_manifest_exclude_mode(
                    &mut acc,
                    current_path,
                    found_path,
                    in_dir,
                ),
                false => add_path_to_file_applicability_manifest_include_mode(
                    &mut acc,
                    current_path,
                    found_path,
                    in_dir,
                ),
            };
            acc
        });

    //pre-create the output directory
    create_dir_all(out_dir)
        .with_context(|| format!("Failed to create output directory {out_dir:#?}"))?;
    //re walk over the tree, processing each file and excluding or including based on the include param and the found_dirs list
    header_span.pb_set_message("Processing files.");
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
        .filter(|dir_entry| !is_file_applicability_file(dir_entry))
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
            (!exclude_by_default
                && !path_string_vec_contains_any_excluded_pathbuf(&dir_entry.path(), &found_dirs)
                // && !found_dirs.contains(&dir_entry.path().to_str().unwrap_or("").to_owned())
                && !path_string_vec_contains_any_excluded_pathbuf_plus_name(&dir_entry.path(), &found_dirs)
                && !path_string_vec_contains_any_excluded_glob_pathbuf(&dir_entry.path(), &found_dirs))
                || (exclude_by_default
                    && (
                        // found_dirs.contains(&dir_entry.path().to_str().unwrap_or("").to_owned())
                        // || 
                        path_string_vec_contains_any_included_pathbuf_plus_name(
                            &dir_entry.path(),
                            &found_dirs,
                        )
                        || path_string_vec_contains_exact_glob_pathbuf(
                            &dir_entry.path(),
                            &found_dirs,
                        )))
                || dir_entry.path() == in_dir
        }).for_each(|entry|
    {
        rayon::scope(|_|{
        //create the location in the output folder where the file will exist
        let path = &entry.path();
        header_span.pb_set_message(("Processing ".to_string()+entry.path().as_os_str().to_str().unwrap_or_default()).as_str());
        let parent_directory = match path.parent() {
            Some(dir) => dir,
            None => panic!(
                "Could not find parent directory for current file {:#?}",
                entry.file_name()
            ),
        };
        let dir_to_create = parent_directory.strip_prefix(in_dir);
        let out_dir_to_create = match dir_to_create {
            Ok(directory) => out_dir.join(directory),
            Err(_) => out_dir.to_path_buf(),
        };
        //make sure the directory is available in the output dir
        match create_dir_all(out_dir_to_create) {
            Ok(dir) => dir,
            Err(e) => panic!(
                "Failed to create output directory {out_dir:#?}! Error: {e:#?}"
            ),
        }
        if entry.file_type().is_file() {
            let file_write_span = trace_span!("File write: ","{:#?}", entry.path().to_str().unwrap_or(""));
            let _file_write_span_enter = file_write_span.enter();
            let file_to_create_path = entry.path();
            let file_to_create = file_to_create_path.strip_prefix(in_dir);
            let out_file_to_create = match file_to_create {
                Ok(directory) => out_dir.join(directory),
                Err(_) => panic!("Failed to join output directory"),
            };

            /*
            Exclude files from processing if specified in the inline_projection_exclusions
             */
            // Normalize the path string for exclusion check
            let normalized_path_str = match path.to_str() {
                Some(path) => path.replace("\\", "/"),
                None => { warn!("Failed to resolve path"); "".to_string() },
            };
            // Check if the path is in the exclusion list (inline_projection_exclusions)
            if inline_projection_exclusions.iter().any(|exclusion_path| exclusion_path.replace("\\", "/") == normalized_path_str) {
                // Ensure the parent directory exists for the output file
                if let Some(parent) = out_file_to_create.parent() {
                    if let Err(e) = create_dir_all(parent) {
                        warn!("Failed to create directory: {}. Error: {:?}", parent.display(), e);
                        return; // Skip copying if the directory cannot be created
                    }
                }
                // Copy the excluded file directly to the output directory
                if let Err(e) = fs::copy(entry.path(), out_file_to_create) {
                    warn!("Failed to copy excluded file: {}. Error: {:?}", normalized_path_str, e);
                }
                return; // Skip further processing for this file
            }

            if is_schema_supported(entry.path().as_path(), "", ""){
                let file_contents = get_file_contents_based_on_applicability(
                    &entry,
                    substitutions.clone(),
                    applic_config.clone(),
                    ple_model
                );
                let _ = write_output_file(out_file_to_create, file_contents);
            } else {
                let _ = ensure_file_is_available_to_write(out_file_to_create.clone());
                match fs::copy(entry.path().as_path(), out_file_to_create){
                    Ok(_) => trace!("Successfully copied: {:#?}", entry.path().to_str()),
                    Err(_) => warn!("Failed to copy: {:#?}", entry.path().to_str()),
                };
            }
        }
    });
    });
    Ok(())
}

#[tracing::instrument(
    err,
    name = "Checking availability of file to write to",
    fields(_file_name),
    skip_all
)]
fn ensure_file_is_available_to_write(file: PathBuf) -> Result<(), anyhow::Error> {
    let _file_name = file.file_name().unwrap_or_default();
    if file.exists() {
        trace!("Preparing to delete file. {:#?}", file.clone());
        let file_metadata = fs::metadata(file.clone())?;
        let mut file_permissions = file_metadata.permissions();
        #[allow(clippy::permissions_set_readonly_false)]
        file_permissions.set_readonly(false);
        match fs::set_permissions(file.clone(), file_permissions) {
            Ok(_) => trace!("Set file permissions to not read-only"),
            Err(_) => warn!("Failed to set file permissions to not read-only"),
        };
        match fs::remove_file(file.clone()) {
            Ok(_) => trace!("Successfully removed pre-existing file. {:#?}", file),
            Err(e) => error!("Failed to remove pre-existing file {:#?} {:#?}", file, e),
        };
    }
    Ok(())
}
#[tracing::instrument(err, name = "Writing output file", fields(_file_name), skip_all)]
fn write_output_file(
    out_file_to_create: PathBuf,
    file_contents: String,
) -> Result<(), anyhow::Error> {
    let _file_name = out_file_to_create.file_name().unwrap_or_default();
    ensure_file_is_available_to_write(out_file_to_create.clone())?;
    let _f = match File::create(out_file_to_create.clone()) {
        Ok(fc) => fc,
        Err(_) => panic!("Failed to create output file"),
    };
    let file_result = File::open(out_file_to_create.clone());
    let _file = match file_result {
        Ok(file) => file,
        Err(error) => match error.kind() {
            ErrorKind::NotFound => match File::create(out_file_to_create.clone()) {
                Ok(fc) => fc,
                Err(e) => panic!("Problem creating the file: {e:?}"),
            },
            other_error => {
                panic!("Problem opening the file: {other_error:?}");
            }
        },
    };
    //write the file out to the out_dir based on dir_entry's location
    fs::write(out_file_to_create.clone(), file_contents.clone()).with_context(|| {
        format!("Failed to write {file_contents:#?} to {out_file_to_create:#?}.")
    })?;
    let metadata = _file
        .metadata()
        .with_context(|| format!("Failed to get file metadata {_file:#?}!",))?;
    trace!("file metadata before setting read-only: {metadata:#?}");
    let mut perms = metadata.permissions();
    trace!("file permissions before setting read-only: {:#?}", perms);
    perms.set_readonly(true);
    match fs::set_permissions(out_file_to_create.clone(), perms) {
        Ok(_) => trace!("Set file permissions to read-only"),
        Err(_) => warn!("Failed to set file permissions to read-only"),
    };
    let meta_data_after = _file.metadata()?;
    let perms2 = meta_data_after.permissions();
    trace!("file permissions after setting read-only: {:#?}", perms2);
    Ok(())
}

// if exclude_mode:
// only add path to list if current_path = in_dir or current_path.parent() = in_dir or all of the parents of current_path in acc
fn add_path_to_file_applicability_manifest_exclude_mode(
    acc: &mut Vec<(String, FileApplicabilityPath)>,
    current_path: PathBuf,
    found_paths: FileApplicabilityPath,
    in_dir: &Path,
) {
    if path_string_vec_contains_all_pathbuf(&current_path, acc)
        || current_path.parent().unwrap_or(Path::new("")) == in_dir
        || current_path == in_dir
    {
        acc.push((current_path.to_str().unwrap_or("").to_string(), found_paths))
    }
}

// if include_mode:
// only add path to list if current_path = in_dir or current_path.parent() = in_dir or any of the parents of current_path not in acc
fn add_path_to_file_applicability_manifest_include_mode(
    acc: &mut Vec<(String, FileApplicabilityPath)>,
    current_path: PathBuf,
    found_paths: FileApplicabilityPath,
    in_dir: &Path,
) {
    if !path_string_vec_contains_any_excluded_pathbuf(&current_path, acc)
        && !path_string_vec_contains_any_excluded_glob_pathbuf(&current_path, acc)
        || current_path.parent().unwrap_or(Path::new("")) == in_dir
        || current_path == in_dir
    {
        acc.push((current_path.to_str().unwrap_or("").to_string(), found_paths))
    }
}
#[tracing::instrument(name="Searching for earlier exclusion of Path", fields(_file_name), level=Level::TRACE, skip_all)]
fn path_string_vec_contains_any_excluded_pathbuf(
    path: &Path,
    acc: &[(String, FileApplicabilityPath)],
) -> bool {
    let mut path_to_search = path;
    let _file_name = match path.file_name() {
        Some(nm) => match nm.to_str() {
            Some(str) => str.to_string(),
            None => "".to_string(),
        },
        None => "".to_string(),
    };
    let excluded = acc
        .iter()
        .filter(|(_, file)| match file {
            FileApplicabilityPath::Excluded(_) => true,
            FileApplicabilityPath::Text(_) | FileApplicabilityPath::Included(_) => false,
        })
        .cloned()
        .map(|content| {
            Path::new(&content.0)
                .join(match content.1 {
                    FileApplicabilityPath::Included(text) => text,
                    FileApplicabilityPath::Excluded(text) => text,
                    FileApplicabilityPath::Text(text) => text,
                })
                .to_str()
                .unwrap_or("")
                .to_string()
        })
        .collect::<Vec<String>>();
    while let Some(x) = path_to_search.parent() {
        trace!(
            "{:#?} {:#?} {:#?}",
            excluded.contains(&get_parent_as_string(path_to_search)),
            excluded,
            acc
        );
        if excluded.contains(&get_parent_as_string(path_to_search)) {
            return true;
        }
        path_to_search = x;
    }
    false
}

#[tracing::instrument(name="Searching for earlier exclusion of Path's parent+name", fields(file_name), level=Level::TRACE, skip_all)]
fn path_string_vec_contains_any_excluded_pathbuf_plus_name(
    path: &Path,
    acc: &[(String, FileApplicabilityPath)],
) -> bool {
    let file_name = match path.file_name() {
        Some(nm) => match nm.to_str() {
            Some(str) => str.to_string(),
            None => "".to_string(),
        },
        None => "".to_string(),
    };
    let mut path_to_search = path;
    let excluded = acc
        .iter()
        .filter(|(_, file)| match file {
            FileApplicabilityPath::Excluded(_) => true,
            FileApplicabilityPath::Text(_) | FileApplicabilityPath::Included(_) => false,
        })
        .cloned()
        .map(|content| {
            Path::new(&content.0)
                .join(match content.1 {
                    FileApplicabilityPath::Included(text) => text,
                    FileApplicabilityPath::Excluded(text) => text,
                    FileApplicabilityPath::Text(text) => text,
                })
                .to_str()
                .unwrap_or("")
                .to_string()
        })
        .collect::<Vec<String>>();
    while let Some(x) = path_to_search.parent() {
        let filename = match match path_to_search.parent() {
            Some(paren) => paren.join(&file_name),
            None => PathBuf::new(),
        }
        .to_str()
        {
            Some(str) => str.to_string(),
            None => "".to_string(),
        };
        trace!("{:#?} {:#?}", excluded.contains(&filename), acc);
        if excluded.contains(&filename) {
            return true;
        }
        path_to_search = x;
    }
    false
}

#[tracing::instrument(name="Searching for earlier inclusion of Path's parent+name", fields(file_name), level=Level::TRACE, skip_all)]
fn path_string_vec_contains_any_included_pathbuf_plus_name(
    path: &Path,
    acc: &[(String, FileApplicabilityPath)],
) -> bool {
    let file_name = match path.file_name() {
        Some(nm) => match nm.to_str() {
            Some(str) => str.to_string(),
            None => "".to_string(),
        },
        None => "".to_string(),
    };
    let mut path_to_search = path;
    let included = acc
        .iter()
        .filter(|(_, file)| match file {
            FileApplicabilityPath::Included(_) => true,
            FileApplicabilityPath::Text(_) | FileApplicabilityPath::Excluded(_) => false,
        })
        .cloned()
        .map(|content| {
            Path::new(&content.0)
                .join(match content.1 {
                    FileApplicabilityPath::Included(text) => text,
                    FileApplicabilityPath::Excluded(text) => text,
                    FileApplicabilityPath::Text(text) => text,
                })
                .to_str()
                .unwrap_or("")
                .to_string()
        })
        .collect::<Vec<String>>();
    while let Some(x) = path_to_search.parent() {
        let filename = match match path_to_search.parent() {
            Some(paren) => paren.join(&file_name),
            None => PathBuf::new(),
        }
        .to_str()
        {
            Some(str) => str.to_string(),
            None => "".to_string(),
        };
        trace!(
            "{:#?} {:#?} {:#?}",
            filename,
            included.contains(&filename),
            acc
        );
        if included.contains(&filename) {
            return true;
        }
        path_to_search = x;
    }
    false
}

#[tracing::instrument(name="Searching for earlier exclusion of Path using GLOB EXACT", fields(_file_name), level=Level::TRACE, skip_all)]
fn path_string_vec_contains_exact_glob_pathbuf(
    path: &Path,
    acc: &[(String, FileApplicabilityPath)],
) -> bool {
    let _file_name = path.as_os_str().to_str().unwrap_or("");
    let starting_path = path.to_str().unwrap_or("");
    let starting_glob_test = acc
        .iter()
        .filter(|(_, file)| match file {
            FileApplicabilityPath::Included(_) => true,
            FileApplicabilityPath::Text(_) | FileApplicabilityPath::Excluded(_) => false,
        })
        .cloned()
        .any(|content| {
            let glob_to_match = &Path::new(&content.0)
                .join(match content.1 {
                    FileApplicabilityPath::Included(text) => text,
                    FileApplicabilityPath::Excluded(text) => text,
                    FileApplicabilityPath::Text(text) => text,
                })
                .to_slash()
                .unwrap()
                .to_string();
            trace!(
                "{:#?} \n{:#?}",
                glob_to_match,
                Path::new(&starting_path)
                    .to_slash()
                    .unwrap()
                    .to_string()
                    .as_str()
            );
            if let Ok(g) = Glob::new(glob_to_match) {
                let glob = g.compile_matcher();
                return glob.is_match(
                    Path::new(&starting_path)
                        .to_slash()
                        .unwrap()
                        .to_string()
                        .as_str(),
                );
            };
            false
        });
    trace!("{:#?} {:#?}", starting_path, starting_glob_test);
    if starting_glob_test {
        return true;
    }
    false
}

#[tracing::instrument(name="Searching for earlier exclusion of Path using GLOB ANY", fields(_file_name), level=Level::TRACE, skip_all)]
fn path_string_vec_contains_any_excluded_glob_pathbuf(
    path: &Path,
    acc: &[(String, FileApplicabilityPath)],
) -> bool {
    let _file_name = path.as_os_str().to_str().unwrap_or("");
    let mut path_to_search = path;
    let starting_path = path.as_os_str().to_str().unwrap_or_default();
    let starting_glob_test = acc
        .iter()
        .filter(|(_, file)| match file {
            FileApplicabilityPath::Excluded(_) => true,
            FileApplicabilityPath::Text(_) | FileApplicabilityPath::Included(_) => false,
        })
        .cloned()
        .any(|content| {
            let glob_to_match = &Path::new(&content.0)
                .join(match content.1 {
                    FileApplicabilityPath::Included(text) => text,
                    FileApplicabilityPath::Excluded(text) => text,
                    FileApplicabilityPath::Text(text) => text,
                })
                .to_slash()
                .unwrap()
                .to_string();
            trace!(
                "Starting: \nGlob:\t{:#?} \nStarting Path:\t{:#?}",
                glob_to_match.as_str(),
                Path::new(&starting_path)
                    .to_slash()
                    .unwrap()
                    .to_string()
                    .as_str()
            );
            if let Ok(g) = Glob::new(glob_to_match.as_str()) {
                let glob = g.compile_matcher();
                return glob.is_match(
                    Path::new(&starting_path)
                        .to_slash()
                        .unwrap()
                        .to_string()
                        .as_str(),
                );
            };
            false
        });
    trace!(
        "Starting path complete:\t{:#?}\nTest Result:\t{:#?}",
        starting_path, starting_glob_test
    );
    if starting_glob_test {
        return true;
    }
    while let Some(x) = path_to_search.parent() {
        let searched_path = path_to_search.to_str().unwrap_or("");
        let has_glob = acc
            .iter()
            .filter(|(_, file)| match file {
                FileApplicabilityPath::Excluded(_) => true,
                FileApplicabilityPath::Text(_) | FileApplicabilityPath::Included(_) => false,
            })
            .cloned()
            .map(|content| {
                let glob_to_match = &Path::new(&content.0).join(match content.1 {
                    FileApplicabilityPath::Included(text) => text,
                    FileApplicabilityPath::Excluded(text) => text,
                    FileApplicabilityPath::Text(text) => text,
                });
                trace!(
                    "Path Search: \nGlob:\t{:#?} \nPath:\t{:#?}",
                    Path::new(glob_to_match)
                        .to_slash()
                        .unwrap()
                        .to_string()
                        .as_str(),
                    Path::new(&searched_path)
                        .to_slash()
                        .unwrap()
                        .to_string()
                        .as_str()
                );
                if let Ok(g) = Glob::new(
                    Path::new(glob_to_match)
                        .to_slash()
                        .unwrap()
                        .to_string()
                        .as_str(),
                ) {
                    let glob = g.compile_matcher();
                    return glob.is_match(
                        Path::new(&searched_path)
                            .to_slash()
                            .unwrap()
                            .to_string()
                            .as_str(),
                    );
                };
                false
            })
            .any(|v| {
                trace!(v);
                v
            });
        trace!(
            "Path Search complete:\t{:#?}\nTest Result:\t{:#?}",
            searched_path, has_glob
        );
        if has_glob {
            return true;
        }
        path_to_search = x;
    }
    false
}

fn path_string_vec_contains_all_pathbuf(
    path: &Path,
    acc: &[(String, FileApplicabilityPath)],
) -> bool {
    let mut path_to_search = path;
    let paths: Vec<String> = acc.iter().cloned().map(|x| x.0).collect();
    while let Some(x) = path_to_search.parent() {
        if !paths.contains(&get_parent_as_string(path_to_search)) {
            return false;
        }
        path_to_search = x;
    }
    true
}

fn get_parent_as_string(path_to_convert: &Path) -> String {
    match path_to_convert.parent() {
        Some(path) => match path.to_str() {
            Some(str) => str.to_string(),
            None => "".to_string(),
        },
        None => "".to_string(),
    }
}
#[tracing::instrument(name="Getting file applicability", level=Level::DEBUG, fields(_file_name), skip(dir_entry, substitutions, applic_config, ple_model))]
fn get_file_contents_based_on_applicability<C: ClientState>(
    dir_entry: &DirEntry<C>,
    substitutions: Vec<Substitution>,
    applic_config: ApplicabilityConfigElement,
    ple_model: &[FeatureDefinition<String>],
) -> String {
    let _file_name = dir_entry.path().to_str().unwrap_or_default();
    let file_contents = get_file_contents(dir_entry.path().as_path());
    let file_path = dir_entry.path();
    let parser_fn = get_config_from_file(file_path.as_path());
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
            contents
                .iter()
                .filter_map(|c| {
                    c.sanitize(
                        applic_config.clone().get_features().as_slice(),
                        &applic_config.clone().get_name(),
                        &substitutions,
                        group.as_ref(),
                        Some(configs.as_slice()),
                        Some(true),
                        ple_model,
                    )
                })
                .collect::<Vec<_>>()
                .join("")
        }
        Err(e) => {
            error!("{:#?}", e);
            file_contents.clone()
        }
    }
}

// should get results like:
// path, Included(text1,else_text1)
// path, Excluded(else_text1, text1)
// path, Excluded(text2,else_text2)
// path, Included(else_text2, text2)
// path, Text(text3)
#[tracing::instrument(name="Getting file applicability from Applicability File", level=Level::DEBUG, fields(_file_name), skip(dir_entry, substitutions, applic_config, ple_model))]
fn get_file_applicability_contents_based_on_applicability<C: ClientState>(
    dir_entry: &DirEntry<C>,
    substitutions: Vec<Substitution>,
    applic_config: ApplicabilityConfigElement,
    ple_model: &[FeatureDefinition<String>],
) -> Vec<FileApplicabilityPath> {
    let _file_name = dir_entry.path().to_str().unwrap_or_default();
    let file_contents = get_file_contents(dir_entry.path().as_path());
    let file_path = dir_entry.path();
    let parser_fn = get_config_from_file(file_path.as_path());
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
            contents
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
                .collect()
        }
        Err(_) => vec![],
    }
}

fn is_file<C: ClientState>(entry: &DirEntry<C>) -> bool {
    entry.file_type().is_file()
}
fn is_file_applicability_file<C: ClientState>(entry: &DirEntry<C>) -> bool {
    is_file(entry)
        && (entry
            .path()
            .file_stem()
            .unwrap_or(OsStr::new(""))
            .to_str()
            .map(|e| matches!(e, ".fileApplicability" | ".applicability"))
            .unwrap_or(false)
            || entry
                .path()
                .extension()
                .unwrap_or(OsStr::new(""))
                .to_str()
                .map(|e| matches!(e, "fileApplicability" | "applicability"))
                .unwrap_or(false))
}
#[derive(Debug, Error)]
enum PATCliError {
    #[error("ValidationFailed")]
    ValidationFailed,
}
