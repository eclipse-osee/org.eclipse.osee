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
    fs::{self, File, create_dir_all},
    io::ErrorKind,
    path::{Path, PathBuf},
    sync::{Arc, mpsc},
};

use anyhow::{Context, Result};
use applicability::substitution::Substitution;
use applicability_parser_config::{
    get_config_from_file, get_file_contents, is_schema_supported,
};
use applicability_parser_errors::ApplicabilityParserError;
use applicability_project::{ProjectMode, discover_project, is_applicability_project_file};
use applicability_sanitization::v2::{
    SanitizeApplicabilityExternalError, SanitizeApplicabilityInternalError, SanitizeApplicabilityV2,
};
use applicability_tokens_to_ast::tree::ApplicabilityExprKind;
use bill_of_features::{BillOfFeatures, BillOfFeaturesEnum, read_bill_of_features};
use clap::{ArgAction, Parser};
use clap_verbosity_flag::{Verbosity, WarnLevel};
use doc_definitions::{
    applic_file_example, applicability_definitions, dot_applicability_syntax_and_notes,
    pat_config_note, ple_applicability_tag_syntax_rules, ple_config_example, supported_file_types,
};
use feature_definition::FeatureDefinition;
use jwalk::{ClientState, DirEntry, Parallelism, WalkDir};
use pat_config::read_ple_config_with_starting_path;
use rayon::iter::{ParallelBridge, ParallelIterator};
use thiserror::Error;
use tracing::{Level, Span, error, trace, trace_span, warn};
use tracing_indicatif::span_ext::IndicatifSpanExt;
use validate_bof::BillOfFeaturesInternalValidationError;

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
            + ple_config_example()
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
            + ple_config_example()
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


pub fn project_repository(args: PatInternalCliOptions, header_span: Span) -> Result<()> {
    let mut error_pool: PATCliErrorBox<String> = PATCliErrorBox { errors: vec![] };
    let in_dir = args.in_dir.as_path();
    let out_dir = args.out_dir.as_path();
    let exclude_by_default = args.exclude;
    let hide_by_default = args.skip_hidden;
    trace!(
        "starting tool with the following parameters: \n\t Applicability Config \t{:#?} \n\t Output Directory \t{:#?} \n\t Input Directory \t{:#?} \n\t Exclude Mode: \t\t{:#?} \n\t Skip Hidden: \t\t{:#?}",
        args.applicability_config, args.out_dir, args.in_dir, exclude_by_default, hide_by_default
    );
    let mut applic_config = read_bill_of_features(args.applicability_config)?;
    // Read the inline_projection_exclusions (as paths) from the ple-config.toml file
    let pat_config = read_ple_config_with_starting_path(in_dir);
    let inline_projection_exclusions = match &pat_config {
        Ok(config) => config.project.inline_projection_exclusions.clone(),
        Err(e) => {
            warn!("Failed to read ple-config.toml: {e:?}");
            vec![] // Return an empty list if there's an error
        }
    };
    if let Ok(config) = &pat_config
        && let Some(new_config) = &config.config
    {
        applic_config.merge(new_config);
    };
    let ple_model = match &pat_config {
        Ok(config) => match &config.features {
            Some(f) => f.as_slice(),
            None => &[],
        },
        Err(_) => &[],
    };
    let results = validate_bof::validate(ple_model, &applic_config);
    if let Err(unwrapped_results) = results {
        unwrapped_results
            .errors
            .iter()
            .cloned()
            .for_each(|e1| {
                match e1.clone() {
            validate_bof::BillOfFeaturesInternalValidationError::TagMissingFromFeatureModel(
                tag,
            ) => {
                error!("Tag Missing From PLE Model: {:#?}", tag);
                error_pool.errors.push(e1.into());
            }
            validate_bof::BillOfFeaturesInternalValidationError::ValueMissingFromFeatureModel(
                tag,
                value,
            ) => {
                error!(
                "Value Missing From PLE Model: {:#?} for Tag {:#?}",
                value, tag
            ); 
            error_pool.errors.push(e1.into());
        },
        validate_bof::BillOfFeaturesInternalValidationError::FailedMultiValueTest(tag, items) => {
            error!(
                    "Tag has more values than expected: {:?}. Found {:?} tags.",
                    tag,
                    items.len()
                );
                error_pool.errors.push(e1.into());
        },
            validate_bof::BillOfFeaturesInternalValidationError::TagMissingFromBillOfFeatures(
                tag,
            ) => warn!("Tag Missing From Bill Of Features: {:#?}", tag),
        }
            });
        if !unwrapped_results.errors.iter().filter(|x| matches!(x, validate_bof::BillOfFeaturesInternalValidationError::TagMissingFromFeatureModel(_) | validate_bof::BillOfFeaturesInternalValidationError::ValueMissingFromFeatureModel(_,_))).collect::<Vec<_>>().is_empty() {
            error_pool.errors.push(PATCliError::ValidationFailed);
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
    let project_configuration = discover_project(in_dir, match exclude_by_default{
        true => ProjectMode::Exclude,
        false => ProjectMode::Include,
    }, substitutions.clone(), applic_config.clone(), ple_model, thread_pool_arc.clone());

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
            project_configuration.path_is_allowed(&dir_entry.path())
                || dir_entry.path() == in_dir
        }).for_each(|entry|
    {
        rayon::scope(|_|{
            let thread_tx = error_tx.clone();
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
                if let Some(parent) = out_file_to_create.parent()
                    && let Err(e) = create_dir_all(parent) {
                        warn!("Failed to create directory: {}. Error: {:?}", parent.display(), e);
                        return; // Skip copying if the directory cannot be created
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
                match file_contents{
                    Ok(fc) => {
                        let _ = write_output_file(out_file_to_create, fc);
                    },
                    Err(e) => {
                        let _ = thread_tx.send(e.errors);
                    },
                }
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
    drop(error_tx);
    for error in error_rx {
        error_pool.errors.extend(error);
    }
    error_pool.errors.iter().for_each(|err| match err {
        PATCliError::ValidationFailed => error!("{}", err),
        PATCliError::SanitizeError(sanitize_applicability_internal_error, path) => {
            error!("File: {} : {}", path.display(), sanitize_applicability_internal_error)
        }
        PATCliError::ParserError(applicability_parser_error, path) => {
            if let ApplicabilityParserError::AstTransformError(ast_transform_error) =
                applicability_parser_error
            {
                error!("File: {} : {}", path.display(), ast_transform_error)
            } else {
                error!("File: {} : {}", path.display(), applicability_parser_error)
            }
        }
        PATCliError::BillOfFeaturesError(bof_error) => match bof_error {
            BillOfFeaturesInternalValidationError::TagMissingFromFeatureModel(tag) => {
                error!("Tag Missing From PLE Model: {:#?}", tag)
            }
            BillOfFeaturesInternalValidationError::ValueMissingFromFeatureModel(tag, value) => {
                error!(
                    "Value Missing From PLE Model: {:#?} for Tag {:#?}",
                    value, tag
                )
            }
            BillOfFeaturesInternalValidationError::TagMissingFromBillOfFeatures(_) => {}
            BillOfFeaturesInternalValidationError::FailedMultiValueTest(tag, items) => {
                error!(
                    "Tag has more values than expected: {:?}. Found {:?} tags.",
                    tag,
                    items.len()
                )
            },
        },
    });
    if !error_pool.errors.is_empty() {
        return Err(anyhow::Error::msg(
            "Errors encountered during repository projection",
        ));
    }
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
#[tracing::instrument(name="Getting file applicability", level=Level::DEBUG, fields(_file_name), skip(dir_entry, substitutions, applic_config, ple_model))]
fn get_file_contents_based_on_applicability<C: ClientState>(
    dir_entry: &DirEntry<C>,
    substitutions: Vec<Substitution>,
    applic_config: BillOfFeaturesEnum,
    ple_model: &[FeatureDefinition<String>],
) -> Result<String, PATCliErrorBox<String>> {
    let _file_name = dir_entry.path().to_str().unwrap_or_default();
    let file_contents = get_file_contents(dir_entry.path().as_path());
    let file_path = dir_entry.path();
    let tmp_file_path = file_path.clone();
    let parser_fn = get_config_from_file(tmp_file_path.as_path());
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
            let (successful_results, failed_results): (Vec<_>, Vec<_>) = contents
                .iter()
                .map(|c| {
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
                .partition(|x| x.is_ok());
            if !failed_results.is_empty() {
                //return accumulated errors
                let accumulated_failures =
                    failed_results
                        .into_iter()
                        .reduce(|acc, item| match (acc, item) {
                            (Ok(_), Ok(_)) => Err(SanitizeApplicabilityExternalError {
                                errors: vec![
                                    SanitizeApplicabilityInternalError::InvalidIteratorCondition,
                                ],
                            }),
                            (Ok(_), Err(e2)) => Err(e2),
                            (Err(e1), Ok(_)) => Err(e1),
                            (Err(mut e1), Err(e2)) => {
                                e1.errors.extend(e2.errors);
                                Err(e1)
                            }
                        });
                return accumulated_failures.unwrap().map_err(|x| PATCliErrorBox::from_sanitize_external_error(x, &file_path));
            }
            Ok(successful_results
                .into_iter()
                .filter_map(|x| x.ok())
                .collect::<Vec<_>>()
                .join(""))
        }
        Err(e) => Err(PATCliErrorBox::from_applic_parser_error(e, file_path)),
    }
}


struct PATCliErrorBox<X = String> {
    pub errors: Vec<PATCliError<X>>,
}
#[derive(Debug, Error)]
enum PATCliError<X = String> {
    #[error("Validation Failed")]
    ValidationFailed,
    #[error("Sanitization Validation Failed")]
    SanitizeError(SanitizeApplicabilityInternalError<X>, PathBuf),
    #[error("File Parsing Failed")]
    ParserError(ApplicabilityParserError, PathBuf),
    #[error("Bill Of Features Validation failed")]
    BillOfFeaturesError(#[from] BillOfFeaturesInternalValidationError),
}

impl <X> PATCliError<X>{
    fn from_applic_parser_error(value: ApplicabilityParserError, path: PathBuf) -> Self {
        PATCliError::ParserError(value, path)
    }
    fn from_sanitize_error(value: SanitizeApplicabilityInternalError<X>, path: PathBuf) -> Self{
        PATCliError::SanitizeError(value, path)
    }
}
impl <X> PATCliErrorBox<X>{
    fn from_applic_parser_error(value: ApplicabilityParserError, path: PathBuf) -> Self {
        PATCliErrorBox {
            errors: vec![PATCliError::from_applic_parser_error(value, path)],
        }
    }
    fn from_sanitize_external_error(value: SanitizeApplicabilityExternalError<X>, path: &Path) -> Self {
        PATCliErrorBox {
            errors: value.errors.into_iter().map(|x| PATCliError::from_sanitize_error(x, path.to_path_buf())).collect(),
        }
    }
}
