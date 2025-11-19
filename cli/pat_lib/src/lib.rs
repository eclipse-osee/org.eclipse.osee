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
    ffi::OsString,
    fmt::Display,
    fs::{self, File, create_dir_all},
    io::{self, ErrorKind},
    path::{Path, PathBuf},
    sync::{
        Arc,
        mpsc::{self},
    },
};

use anyhow::{Context, Result};
use applicability::substitution::Substitution;
use applicability_parser_config::{get_config_from_file, get_file_contents, is_schema_supported};
use applicability_parser_errors::ApplicabilityParserError;
use applicability_project::{ProjectMode, discover_project, is_applicability_project_file};
use applicability_sanitization::v2::{
    SanitizeApplicabilityExternalError, SanitizeApplicabilityInternalError, SanitizeApplicabilityV2,
};
use applicability_tokens_to_ast::tree::ApplicabilityExprKind;
use bill_of_features::{BillOfFeatures, BillOfFeaturesEnum};
use clap::{ArgAction, Parser};
use clap_verbosity_flag::{Verbosity, WarnLevel};
use doc_definitions::{
    applic_file_example, applicability_definitions, dot_applicability_syntax_and_notes,
    pat_config_note, ple_applicability_tag_syntax_rules, ple_config_example, supported_file_types,
};
use feature_definition::FeatureDefinition;
use jwalk::{Parallelism, WalkDir};
use pat_config::{CompletePleConfig, read_ple_config_and_bof};
use rayon::iter::{ParallelBridge, ParallelIterator};
use thiserror::Error;
use tracing::{Level, Span, error, trace, warn};
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

pub fn project_repository(args: PatInternalCliOptions, header_span: &Span) -> Result<()> {
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

#[allow(clippy::too_many_arguments)]
fn process_file(
    path: &std::path::Path,
    span: &Span,
    in_dir: &std::path::Path,
    out_dir: &std::path::Path,
    inline_projection_exclusions: &[String],
    substitutions: &[Substitution],
    bill_of_features: &BillOfFeaturesEnum,
    ple_model: &[FeatureDefinition],
) -> Result<(), ProcessFileError> {
    span.pb_set_message(
        ("Processing ".to_string() + path.as_os_str().to_str().unwrap_or_default()).as_str(),
    );
    let parent_directory = match path.parent() {
        Some(dir) => dir,
        None => {
            return Err(ProcessFileError::ParentDirectoryNotFound(
                path.file_name().unwrap_or_default().to_owned(),
            ));
        }
    };
    let dir_to_create = parent_directory.strip_prefix(in_dir);
    let out_dir_to_create = match dir_to_create {
        Ok(directory) => out_dir.join(directory),
        Err(_) => out_dir.to_path_buf(),
    };
    match create_dir_all(out_dir_to_create) {
        Ok(dir) => dir,
        Err(e) => {
            return Err(ProcessFileError::OutputDirectoryCreationFailed(
                out_dir.to_path_buf(),
                e,
            ));
        }
    }
    if path.is_file() {
        let projection_result = process_file_for_applicability(
            in_dir,
            out_dir,
            path,
            inline_projection_exclusions,
            substitutions,
            bill_of_features,
            ple_model,
        );
        if let Err(e) = projection_result {
            return Err(e.into());
        }
    }
    Ok(())
}
#[tracing::instrument(
    err,
    name = "Processing file",
    fields(_file_name)
    skip_all
)]
fn process_file_for_applicability(
    in_dir: &std::path::Path,
    out_dir: &std::path::Path,
    entry: &std::path::Path,
    inline_projection_exclusions: &[String],
    substitutions: &[Substitution],
    applic_config: &BillOfFeaturesEnum,
    ple_model: &[FeatureDefinition],
) -> Result<(), WriteProjectedFileError> {
    let _file_name = entry.file_name().unwrap_or_default();
    let file_to_create = entry.strip_prefix(in_dir);
    let out_file_to_create = match file_to_create {
        Ok(directory) => out_dir.join(directory),
        Err(_) => panic!("Failed to join output directory"),
    };

    /*
    Exclude files from processing if specified in the inline_projection_exclusions
     */
    // Normalize the path string for exclusion check
    let normalized_path_str = match entry.to_str() {
        Some(path) => path.replace("\\", "/"),
        None => {
            warn!("Failed to resolve path");
            "".to_string()
        }
    };
    if let Err(_e) = copy_projection_excluded_from_inline_projection(
        inline_projection_exclusions,
        normalized_path_str,
        &out_file_to_create,
        entry,
    ) {
        return Ok(());
    }

    write_projected_file(
        entry,
        &out_file_to_create,
        substitutions,
        applic_config,
        ple_model,
    )
}
#[tracing::instrument(err, name = "Checking if file should be directly copied", skip_all)]
fn copy_projection_excluded_from_inline_projection(
    inline_projection_exclusions: &[String],
    normalized_path_str: String,
    targeted_out_file: &std::path::Path,
    input_file: &std::path::Path,
) -> Result<(), CopyFileInsteadOfProjectionError> {
    if inline_projection_exclusions
        .iter()
        .any(|exclusion_path| exclusion_path.replace("\\", "/") == normalized_path_str)
    {
        if let Some(parent) = targeted_out_file.parent()
            && let Err(e) = create_dir_all(parent)
        {
            warn!(
                "Failed to create directory: {}. Error: {:?}",
                parent.display(),
                e
            );
            return Err(e.into()); // Skip copying if the directory cannot be created
        }
        if let Err(e) = fs::copy(input_file, targeted_out_file) {
            warn!(
                "Failed to copy excluded file: {}. Error: {:?}",
                normalized_path_str, e
            );
            return Err(e.into());
        }
        return Err(CopyFileInsteadOfProjectionError::Excluded(
            input_file.to_path_buf(),
        ));
    }
    Ok(())
}

#[tracing::instrument(
    err,
    name = "Projecting file",
    fields(_file_name)
    skip_all
)]
fn write_projected_file(
    file: &std::path::Path,
    out_file_to_create: &std::path::Path,
    substitutions: &[Substitution],
    applic_config: &BillOfFeaturesEnum,
    ple_model: &[FeatureDefinition],
) -> Result<(), WriteProjectedFileError> {
    let _file_name = file.file_name().unwrap_or_default();
    if is_schema_supported(file, "", "") {
        let file_contents = get_file_contents_based_on_applicability(
            file,
            substitutions,
            applic_config,
            ple_model,
        )?;
        write_output_file(out_file_to_create, file_contents)?;
    } else {
        ensure_file_is_available_to_write(out_file_to_create)?;
        match fs::copy(file, out_file_to_create) {
            Ok(_) => trace!("Successfully copied: {:#?}", file.to_str()),
            Err(_) => warn!("Failed to copy: {:#?}", file.to_str()),
        };
    }
    Ok(())
}

#[tracing::instrument(
    err,
    name = "Checking availability of file to write to",
    fields(_file_name),
    skip_all
)]
fn ensure_file_is_available_to_write(file: &std::path::Path) -> Result<(), io::Error> {
    let _file_name = file.file_name().unwrap_or_default();
    if file.exists() {
        trace!("Preparing to delete file. {:#?}", file);
        let file_metadata = fs::metadata(file)?;
        let mut file_permissions = file_metadata.permissions();
        #[allow(clippy::permissions_set_readonly_false)]
        file_permissions.set_readonly(false);
        match fs::set_permissions(file, file_permissions) {
            Ok(_) => trace!("Set file permissions to not read-only"),
            Err(_) => warn!("Failed to set file permissions to not read-only"),
        };
        match fs::remove_file(file) {
            Ok(_) => trace!("Successfully removed pre-existing file. {:#?}", file),
            Err(e) => error!("Failed to remove pre-existing file {:#?} {:#?}", file, e),
        };
    }
    Ok(())
}
#[tracing::instrument(err, name = "Writing output file", fields(_file_name), skip_all)]
fn write_output_file(
    out_file_to_create: &std::path::Path,
    file_contents: String,
) -> Result<(), PATWriteOutputFileError> {
    let _file_name = out_file_to_create.file_name().unwrap_or_default();
    ensure_file_is_available_to_write(out_file_to_create)?;
    let _f = match File::create(out_file_to_create) {
        Ok(fc) => fc,
        Err(_) => panic!("Failed to create output file"),
    };
    let file_result = File::open(out_file_to_create);
    let _file = match file_result {
        Ok(file) => file,
        Err(error) => match error.kind() {
            ErrorKind::NotFound => match File::create(out_file_to_create) {
                Ok(fc) => fc,
                Err(e) => panic!("Problem creating the file: {e:?}"),
            },
            other_error => {
                panic!("Problem opening the file: {other_error:?}");
            }
        },
    };
    //write the file out to the out_dir based on dir_entry's location
    fs::write(out_file_to_create, file_contents.clone())?;
    //.with_context(|| {
    //  format!("Failed to write {file_contents:#?} to {out_file_to_create:#?}.")
    //})?;
    let metadata = _file.metadata()?;
    //.with_context(|| format!("Failed to get file metadata {_file:#?}!",))?;
    trace!("file metadata before setting read-only: {metadata:#?}");
    let mut perms = metadata.permissions();
    trace!("file permissions before setting read-only: {:#?}", perms);
    perms.set_readonly(true);
    match fs::set_permissions(out_file_to_create, perms) {
        Ok(_) => trace!("Set file permissions to read-only"),
        Err(_) => warn!("Failed to set file permissions to read-only"),
    };
    let meta_data_after = _file.metadata()?;
    let perms2 = meta_data_after.permissions();
    trace!("file permissions after setting read-only: {:#?}", perms2);
    Ok(())
}
#[tracing::instrument(name="Getting contents of file based on applicability", level=Level::DEBUG, fields(_file_name), skip(file, substitutions, bill_of_features, ple_model))]
fn get_file_contents_based_on_applicability(
    file: &std::path::Path,
    substitutions: &[Substitution],
    bill_of_features: &BillOfFeaturesEnum,
    ple_model: &[FeatureDefinition<String>],
) -> Result<String, PATCliErrorBox<String>> {
    let _file_name = file.display().to_string();
    let file_contents = get_file_contents(file);
    let error_contents = file_contents.clone();
    let parser_fn = get_config_from_file(file);
    let parsed_contents = parser_fn(file_contents.as_str());
    match parsed_contents {
        Ok(c) => {
            let contents = c
                .into_iter()
                .map(Into::<ApplicabilityExprKind<String>>::into)
                .collect::<Vec<_>>();
            let group = bill_of_features.get_parent_group().map(|x| x.to_string());
            let configs = bill_of_features
                .get_configs()
                .iter()
                .map(|x| x.to_string())
                .collect::<Vec<_>>();
            let (successful_results, failed_results): (Vec<_>, Vec<_>) = contents
                .iter()
                .map(|c| {
                    c.sanitize(
                        bill_of_features.get_features(),
                        bill_of_features.get_name(),
                        substitutions,
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
                return accumulated_failures
                    .unwrap()
                    .map_err(|x| PATCliErrorBox::from_sanitize_external_error(x, file));
            }
            Ok(successful_results
                .into_iter()
                .filter_map(|x| x.ok())
                .collect::<Vec<_>>()
                .join(""))
        }
        Err(e) => Err(PATCliErrorBox::from_applic_parser_error(
            e,
            file.to_path_buf(),
            error_contents,
        )),
    }
}

#[derive(Debug)]
struct PATCliErrorBox<X = String>
where
    X: Display,
{
    pub errors: Vec<PATCliError<X>>,
}

impl<X> Display for PATCliErrorBox<X>
where
    X: Display,
{
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        writeln!(f, "Errors occurred during repository projection: ")?;
        write!(f, "[")?;
        if !self.errors.is_empty() {
            writeln!(f)?;
        }
        self.errors.iter().for_each(|e| {
            let _ = writeln!(f, "{e},");
        });
        write!(f, "]")?;
        Ok(())
    }
}

#[derive(Debug, Error)]
enum PATCliError<X = String>
where
    X: Display,
{
    ValidationFailed,
    SanitizeError(SanitizeApplicabilityInternalError<X>, PathBuf),
    ParserError(ApplicabilityParserError, PathBuf, String),
    BillOfFeaturesError(BillOfFeaturesInternalValidationError),
}

impl<X> Display for PATCliError<X>
where
    X: Display,
{
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        match self {
            PATCliError::ValidationFailed => writeln!(f, "Validation Failed"),
            PATCliError::SanitizeError(sanitize_applicability_internal_error, path_buf) => {
                writeln!(
                    f,
                    "Sanitization Validation Failed for File {}: {sanitize_applicability_internal_error}",
                    path_buf.display()
                )
            }
            PATCliError::ParserError(applicability_parser_error, path_buf, file_contents) => {
                if let ApplicabilityParserError::AstTransformError(e) = applicability_parser_error {
                    let lines = file_contents.lines();
                    let position = e.get_position();
                    let start_line = std::cmp::max(position.0.1 - 1, 0);
                    let end_line = std::cmp::min(position.1.1 + 1, u32::MAX);
                    let enumerated_lines = lines.enumerate();
                    let filtered_lines = enumerated_lines
                        .filter(|(idx, _)| {
                            let actual_idx_result = u32::try_from(*idx);
                            if let Ok(actual_idx) = actual_idx_result
                                && actual_idx >= start_line
                                && actual_idx <= end_line
                            {
                                return true;
                            }
                            false
                        })
                        .map(|(_, content)| content);
                    #[cfg(windows)]
                    const LINE_ENDING: &str = "\r\n"; // CRLF for Windows

                    #[cfg(not(windows))]
                    const LINE_ENDING: &str = "\n";
                    let content = filtered_lines.collect::<Vec<&str>>().join(LINE_ENDING);
                    writeln!(
                        f,
                        "File Parsing Failed for File {}: {applicability_parser_error}.Location: {LINE_ENDING}{content}",
                        path_buf.display()
                    )
                } else {
                    writeln!(
                        f,
                        "File Parsing Failed for File {}: {applicability_parser_error}",
                        path_buf.display()
                    )
                }
            }
            PATCliError::BillOfFeaturesError(_bill_of_features_internal_validation_error) => {
                writeln!(f, "Bill Of Features Validation failed")
            }
        }
    }
}

impl<X> TryFrom<BillOfFeaturesInternalValidationError> for PATCliError<X>
where
    X: Display,
{
    type Error = BillOfFeaturesInternalValidationError;

    fn try_from(
        value: BillOfFeaturesInternalValidationError,
    ) -> std::result::Result<Self, Self::Error> {
        if let BillOfFeaturesInternalValidationError::TagMissingFromBillOfFeatures(e) = value {
            return Err(BillOfFeaturesInternalValidationError::TagMissingFromBillOfFeatures(e));
        }
        Ok(PATCliError::BillOfFeaturesError(value))
    }
}

impl<X> PATCliError<X>
where
    X: Display,
{
    fn from_applic_parser_error(
        value: ApplicabilityParserError,
        path: PathBuf,
        file_contents: String,
    ) -> Self {
        PATCliError::ParserError(value, path, file_contents)
    }
    fn from_sanitize_error(value: SanitizeApplicabilityInternalError<X>, path: PathBuf) -> Self {
        PATCliError::SanitizeError(value, path)
    }
}
impl<X> PATCliErrorBox<X>
where
    X: Display,
{
    fn from_applic_parser_error(
        value: ApplicabilityParserError,
        path: PathBuf,
        file_contents: String,
    ) -> Self {
        PATCliErrorBox {
            errors: vec![PATCliError::from_applic_parser_error(
                value,
                path,
                file_contents,
            )],
        }
    }
    fn from_sanitize_external_error(
        value: SanitizeApplicabilityExternalError<X>,
        path: &Path,
    ) -> Self {
        PATCliErrorBox {
            errors: value
                .errors
                .into_iter()
                .map(|x| PATCliError::from_sanitize_error(x, path.to_path_buf()))
                .collect(),
        }
    }
}
#[derive(Debug, Error)]
enum CopyFileInsteadOfProjectionError {
    #[error("{}",.0)]
    Io(#[from] io::Error),
    #[error("File excluded from projection {}",.0.display())]
    Excluded(std::path::PathBuf),
}

#[derive(Debug, Error)]
enum WriteProjectedFileError {
    #[error("{}",.0)]
    PATWriteOutputFileError(#[from] PATWriteOutputFileError),
    #[error("{}",.0)]
    Io(#[from] io::Error),
    #[error("{}",.0)]
    PATCliError(PATCliErrorBox),
}

impl From<PATCliErrorBox> for WriteProjectedFileError {
    fn from(value: PATCliErrorBox) -> Self {
        WriteProjectedFileError::PATCliError(value)
    }
}

#[derive(Debug, Error)]
enum PATWriteOutputFileError {
    #[error("{}",.0)]
    Io(#[from] io::Error),
}

#[derive(Debug, Error)]
enum ProcessFileError {
    #[error("Could not find parent directory for file {:#?}",.0)]
    ParentDirectoryNotFound(OsString),
    #[error("Failed to create output directory {:#?}! Error: {:#?}",.0, .1)]
    OutputDirectoryCreationFailed(PathBuf, std::io::Error),
    #[error("{}",.0)]
    WriteProjectedFileError(#[from] WriteProjectedFileError),
}
