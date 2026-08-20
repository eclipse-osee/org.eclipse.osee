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
};

use anyhow::Result;
use applicability::substitution::Substitution;
use applicability_parser_config::{get_config_from_file, get_file_contents, is_schema_supported};
use applicability_parser_errors::ApplicabilityParserError;
use applicability_sanitization::v2::{
    SanitizeApplicabilityExternalError, SanitizeApplicabilityInternalError, SanitizeApplicabilityV2,
};
use applicability_tokens_to_ast::tree::ApplicabilityExprKind;
use bill_of_features::{BillOfFeatures, BillOfFeaturesEnum};
use feature_definition::FeatureDefinition;
use thiserror::Error;
use tracing::{Level, Span, error, trace, warn};
use tracing_indicatif::span_ext::IndicatifSpanExt;
use validate_bof::BillOfFeaturesInternalValidationError;
#[allow(clippy::too_many_arguments)]
pub(crate) fn process_file(
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
pub(crate) struct PATCliErrorBox<X = String>
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
pub(crate) enum PATCliError<X = String>
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
pub(crate) enum WriteProjectedFileError {
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
pub(crate) enum PATWriteOutputFileError {
    #[error("{}",.0)]
    Io(#[from] io::Error),
}

#[derive(Debug, Error)]
pub(crate) enum ProcessFileError {
    #[error("Could not find parent directory for file {:#?}",.0)]
    ParentDirectoryNotFound(OsString),
    #[error("Failed to create output directory {:#?}! Error: {:#?}",.0, .1)]
    OutputDirectoryCreationFailed(PathBuf, std::io::Error),
    #[error("{}",.0)]
    WriteProjectedFileError(#[from] WriteProjectedFileError),
}
