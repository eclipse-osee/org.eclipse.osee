use anyhow::{Context, Result};
use applicability::substitution::Substitution;
use applicability_parser::parse_applicability;
use applicability_parser_config::applic_config::ApplicabilityConfigElement;
use applicability_parser_config::{get_comment_syntax, get_file_contents, is_schema_supported};
use applicability_path::{FileApplicabilityPath, ParsePaths};
use applicability_sanitization::SanitizeApplicability;
use applicability_substitution::SubstituteApplicability;
use clap::{ArgAction, Parser};
use clap_verbosity_flag::{Verbosity, WarnLevel};
use globset::Glob;
use jwalk::{ClientState, DirEntry, Parallelism, WalkDir};
use path_slash::PathExt;
use rayon::iter::{ParallelBridge, ParallelIterator};
use std::ffi::OsStr;
use std::fs::File;
use std::fs::{self, create_dir_all};
use std::io::ErrorKind;
use std::path::{Path, PathBuf};
use std::sync::Arc;
use tracing::{debug_span, error, trace, trace_span, warn, Level};

#[cfg(not(target_env = "msvc"))]
#[global_allocator]
static GLOBAL: mimalloc::MiMalloc = mimalloc::MiMalloc;

// theory of execution:
// take in input folder, output folder, applicability config, do not allow begin/end comment syntax customization as it will be mixed
// sort the .fileApplicability and .applicability's first, use WalkDir's sort by fn
// filter based on the results of fileApplicability being BAT processed
// this implies that the .fileApplicability will have all folders in the repo that should be traversed...or should we do the opposite,
// only include folders that get excluded for a given tag
// apply BAT processing to all files/folders
// output results of processing to output folder(alt: symlink if input == output)

/// Project Applicability Tool(PAT)
/// ----------------------------------------------------{n}
/// The Project Applicability Tool will process
/// .applicability & .fileApplicability files
/// to include/exclude certain folders/files based on
/// an applicability config.{n}
/// {n}
/// Using feature tagging similar to what is used in the source, specify which directory names (including relative paths or glob patterns) and/or filenames should or should not be processed for specific product line configurations.
/// Any file or directory that is not listed in an .applicability file should be included and processed.
/// {n}
/// The syntax of the .applicability/.fileApplicability
/// is as follows:{n}
/// # Feature[APPLIC_TAG]
/// path/to/include
/// another/path/to/include/**/*
/// # End Feature
/// {n}
/// Note: the paths in your .fileApplicability/.applicability files is sensitive to your OS's path handling and how you passed in your input/output paths
/// {n}
/// Example contents of .applicability:
/// Configuration[Product_A]
/// csvfiles
/// End Configuration
/// {n}
/// Feature[JHU_CONTROLLER]
/// CppTest_Exclude.cpp
/// End Feature
/// {n}
/// Feature[ENGINE_5=A2543]
/// **/enginefiles
/// End Configuration
/// {n}
/// How this reads:
/// {n}
/// If the source is being processed for Product A, include any directories/files at this location named "csvfiles".
/// If the source is being processed for a product that is not Product A, DO NOT include any directories/files at this location named "csvfiles".
/// If the JHU_CONTROLLER is set to Included for the view being processed, include the file at this location named "CppTest_Exclude.cpp".
/// If the JHU_CONTROLLER is not set to Included for the view being processed, DO NOT include the file at this location named "CppTest_Exclude.cpp".
/// If the ENGINE_5 is set to A2543, include any directories/files at any level at or below this location named "enginefiles".
/// If the ENGINE_5 is not set to A2543, DO NOT include any directories/files at any level at or below this location named "enginefiles".
/// {n}
/// NOTE: Configuration names may not have spaces or special characters. Replace with underscores. E.g. Product A = Product_A
/// {n}
/// Supported Default Formats:
///     *.md                            Starting Syntax: ``     Ending Syntax: ``
///     *.cpp                           Starting Syntax: //     Ending Syntax:
///     *.cxx                           Starting Syntax: //     Ending Syntax:
///     *.cc                            Starting Syntax: //     Ending Syntax:
///     *.c                             Starting Syntax: //     Ending Syntax:
///     *.hpp                           Starting Syntax: //     Ending Syntax:
///     *.hxx                           Starting Syntax: //     Ending Syntax:
///     *.hh                            Starting Syntax: //     Ending Syntax:
///     *.h                             Starting Syntax: //     Ending Syntax:
///     *.rs                            Starting Syntax: //     Ending Syntax:
///     *.bzl                           Starting Syntax: #      Ending Syntax:
///     *.bazel                         Starting Syntax: #      Ending Syntax:
///     *.tex                           Starting Syntax: \if    Ending Syntax: {}
///     WORKSPACE                       Starting Syntax: #      Ending Syntax:
///     BUILD                           Starting Syntax: #      Ending Syntax:
///     *.fileApplicability             Starting Syntax: #      Ending Syntax:
///     *.applicability                 Starting Syntax: #      Ending Syntax:
///     *.gpj                           Starting Syntax: #      Ending Syntax:
///     *.mk                            Starting Syntax: #      Ending Syntax:
///     *.opt                           Starting Syntax: #      Ending Syntax:
///     Makefile                        Starting Syntax: #      Ending Syntax:
///     makefile                        Starting Syntax: #      Ending Syntax:
///     MAKEFILE                        Starting Syntax: #      Ending Syntax:
/// {n}
/// For unsupported file types, PAT will copy the file directly to the output directory instead of sanitizing it.
#[derive(Parser)]
#[clap(author = "Luciano Vaglienti", version, verbatim_doc_comment)]
struct CliOptions {
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

    /// Verbosity of output, defaults to warnings and errors.
    /// -q will have no output
    /// -v will show warnings,info and errors
    /// -vv will show warnings,info,errors, and debug
    /// -vvv will show warnings,info,errors, debug and trace output
    #[command(flatten)]
    verbose: Verbosity<WarnLevel>,
}

#[tracing::instrument(err)]
fn main() -> Result<()> {
    let args = CliOptions::parse();
    let handle = std::io::BufWriter::new(std::io::stdout());
    let (non_blocking, _guard) = tracing_appender::non_blocking(handle);
    let debug_output = match args.verbose.log_level_filter() {
        clap_verbosity_flag::LevelFilter::Debug => true,
        clap_verbosity_flag::LevelFilter::Trace => true,
        _rest => false,
    };
    let subscriber = tracing_subscriber::FmtSubscriber::builder()
        .with_writer(non_blocking)
        .with_max_level(match args.verbose.log_level_filter() {
            clap_verbosity_flag::LevelFilter::Error => Level::ERROR,
            clap_verbosity_flag::LevelFilter::Warn => Level::WARN,
            clap_verbosity_flag::LevelFilter::Info => Level::INFO,
            clap_verbosity_flag::LevelFilter::Debug => Level::DEBUG,
            clap_verbosity_flag::LevelFilter::Trace => Level::TRACE,
            clap_verbosity_flag::LevelFilter::Off => Level::ERROR,
        })
        .with_line_number(debug_output)
        .with_thread_ids(debug_output)
        .with_thread_names(debug_output)
        .pretty()
        .finish();
    let _ = tracing::subscriber::set_global_default(subscriber)
        .map_err(|_err| eprintln!("Unable to set global default subscriber"));
    let in_dir = args.in_dir.as_path();
    let out_dir = args.out_dir.as_path();
    let exclude_by_default = args.exclude;
    let hide_by_default = args.skip_hidden;
    trace!("starting tool with the following parameters: \n\t Applicability Config \t{:#?} \n\t Output Directory \t{:#?} \n\t Input Directory \t{:#?} \n\t Exclude Mode: \t\t{:#?} \n\t Skip Hidden: \t\t{:#?}", args.applicability_config,args.out_dir,args.in_dir, exclude_by_default, hide_by_default);
    let applic_config: ApplicabilityConfigElement = match File::open(args.applicability_config) {
        Ok(file) => match serde_json::from_reader(file) {
            Ok(res) => res,
            Err(e) => panic!(
                "Could not parse applicability config JSON \n{:?}: \tat line {:?} column {:?}",
                e.classify(),
                e.line(),
                e.column()
            ),
        },

        Err(e) => panic!("Could not find applicability config {:?}", e),
    };
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
    //capture the exact path referenced by the .fileApplicability
    let found_dirs = WalkDir::new(in_dir)
        .skip_hidden(false)
        .parallelism(Parallelism::RayonExistingPool(thread_pool_arc.clone()))
        .into_iter()
        .map(|entr| match entr {
            Ok(entr) => entr,
            Err(err) => panic!("Error unwrapping directory listing {:#?}", err),
        })
        .filter(is_file_applicability_file)
        .flat_map(|dir_entry| {
            let span = debug_span!(
                &("Getting file applicability"),
                value = dir_entry.path().to_str().unwrap_or("")
            );
            let _enter = span.enter();
            get_file_applicability_contents_based_on_applicability(
                &dir_entry,
                substitutions.clone(),
                applic_config.clone(),
            )
            .iter()
            .cloned()
            .flat_map(|c| match c {
                FileApplicabilityPath::Included(text, _) => text
                    .lines()
                    .map(|x| FileApplicabilityPath::Included(x.to_string(), "".to_string()))
                    .collect::<Vec<FileApplicabilityPath>>(),
                FileApplicabilityPath::Excluded(text, _) => text
                    .lines()
                    .map(|x| FileApplicabilityPath::Excluded("".to_string(), x.to_string()))
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
        .with_context(|| format!("Failed to create output directory {:#?}", out_dir))?;
    //re walk over the tree, processing each file and excluding or including based on the include param and the found_dirs list
    //TODO: switch to using par_bridge
    WalkDir::new(in_dir)
        .skip_hidden(hide_by_default)
        .parallelism(Parallelism::RayonExistingPool(thread_pool_arc.clone()))
        .into_iter()
        .par_bridge()
        .map(|e| match e {
            Ok(entr) => entr,
            Err(err) => {
                panic!("Error reading directory {:#?}", err)
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
                "Failed to create output directory {:#?}! Error: {:#?}",
                out_dir, e
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
            if is_schema_supported(&entry.path().as_path(), "", ""){
                
                let file_contents = get_file_contents_based_on_applicability(
                    &entry,
                    substitutions.clone(),
                    applic_config.clone(),
                );
                let _ = write_output_file(out_file_to_create, file_contents);
                
            } else {
                let _ = ensure_file_is_available_to_write(out_file_to_create.clone());
                match fs::copy(&entry.path().as_path(), out_file_to_create){
                    Ok(_) => trace!("Successfully copied: {:#?}", entry.path().to_str()),
                    Err(_) => warn!("Failed to copy: {:#?}", entry.path().to_str()),
                };

            }
        }
    });
    });
    Ok(())
}
#[tracing::instrument(err)]
fn create_symlink(entry: PathBuf, out_file: PathBuf) -> Result<(), anyhow::Error> {
    symlink::symlink_file(&entry, &out_file).with_context(|| {
        format!(
            "Error creating symlink between {:#?} and {:#?}",
            entry, &out_file
        )
    })
}
#[tracing::instrument(err)]
fn ensure_file_is_available_to_write(file: PathBuf)-> Result<(), anyhow::Error>{
    if file.exists(){
        trace!(
            "Preparing to delete file. {:#?}",
            file.clone()
        );
        let file_metadata = fs::metadata(file.clone())?;
        let mut file_permissions = file_metadata.permissions();
        #[allow(clippy::permissions_set_readonly_false)]
        file_permissions.set_readonly(false);
        match fs::set_permissions(file.clone(), file_permissions) {
            Ok(_) => trace!("Set file permissions to not read-only"),
            Err(_) => warn!("Failed to set file permissions to not read-only"),
        };
        match fs::remove_file(file.clone()) {
            Ok(_) => trace!(
                "Successfully removed pre-existing file. {:#?}",
                file
            ),
            Err(e) => error!(
                "Failed to remove pre-existing file {:#?} {:#?}",
                file, e
            ),
        };
    }
    Ok(())
}
#[tracing::instrument(err)]
fn write_output_file(
    out_file_to_create: PathBuf,
    file_contents: String,
) -> Result<(), anyhow::Error> {
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
                Err(e) => panic!("Problem creating the file: {:?}", e),
            },
            other_error => {
                panic!("Problem opening the file: {:?}", other_error);
            }
        },
    };
    //write the file out to the out_dir based on dir_entry's location
    fs::write(out_file_to_create.clone(), file_contents.clone()).with_context(|| {
        format!(
            "Failed to write {:#?} to {:#?}.",
            file_contents, out_file_to_create
        )
    })?;
    let metadata = _file
        .metadata()
        .with_context(|| format!("Failed to get file metadata {:#?}!", _file))?;
    trace!("file metadata before setting read-only: {:#?}", metadata);
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

fn path_string_vec_contains_any_excluded_pathbuf(
    path: &Path,
    acc: &[(String, FileApplicabilityPath)],
) -> bool {
    let mut path_to_search = path;
    let name = match path.file_name() {
        Some(nm) => match nm.to_str() {
            Some(str) => str.to_string(),
            None => "".to_string(),
        },
        None => "".to_string(),
    };
    let span = trace_span!(
        "Searching path buf for parent for earlier exclusion",
        value = name
    );
    let _enter = span.enter();
    let excluded = acc
        .iter()
        .filter(|(_, file)| match file {
            FileApplicabilityPath::Excluded(_, _) => true,
            FileApplicabilityPath::Text(_) | FileApplicabilityPath::Included(_, _) => false,
        })
        .cloned()
        .map(|content| {
            Path::new(&content.0)
                .join(match content.1 {
                    FileApplicabilityPath::Included(text, _) => text,
                    FileApplicabilityPath::Excluded(text, _) => text,
                    FileApplicabilityPath::Text(text) => text,
                })
                .to_str()
                .unwrap_or("")
                .to_string()
        })
        .collect::<Vec<String>>();
    while let Some(x) = path_to_search.parent() {
        trace!(
            "{:#?} {:#?} {:#?} {:#?}",
            name,
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

fn path_string_vec_contains_any_excluded_pathbuf_plus_name(
    path: &Path,
    acc: &[(String, FileApplicabilityPath)],
) -> bool {
    let name = match path.file_name() {
        Some(nm) => match nm.to_str() {
            Some(str) => str.to_string(),
            None => "".to_string(),
        },
        None => "".to_string(),
    };
    let span = trace_span!(
        "Searching path buf for parent + name for earlier exclusion",
        value = name
    );
    let _enter = span.enter();
    let mut path_to_search = path;
    let excluded = acc
        .iter()
        .filter(|(_, file)| match file {
            FileApplicabilityPath::Excluded(_, _) => true,
            FileApplicabilityPath::Text(_) | FileApplicabilityPath::Included(_, _) => false,
        })
        .cloned()
        .map(|content| {
            Path::new(&content.0)
                .join(match content.1 {
                    FileApplicabilityPath::Included(text, _) => text,
                    FileApplicabilityPath::Excluded(text, _) => text,
                    FileApplicabilityPath::Text(text) => text,
                })
                .to_str()
                .unwrap_or("")
                .to_string()
        })
        .collect::<Vec<String>>();
    while let Some(x) = path_to_search.parent() {
        let filename = match match path_to_search.parent() {
            Some(paren) => paren.join(&name),
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
            excluded.contains(&filename),
            acc
        );
        if excluded.contains(&filename) {
            return true;
        }
        path_to_search = x;
    }
    false
}

fn path_string_vec_contains_any_included_pathbuf_plus_name(
    path: &Path,
    acc: &[(String, FileApplicabilityPath)],
) -> bool {
    let name = match path.file_name() {
        Some(nm) => match nm.to_str() {
            Some(str) => str.to_string(),
            None => "".to_string(),
        },
        None => "".to_string(),
    };
    let span = trace_span!(
        "Searching path buf for parent + name for earlier exclusion",
        value = name
    );
    let _enter = span.enter();
    let mut path_to_search = path;
    let included = acc
        .iter()
        .filter(|(_, file)| match file {
            FileApplicabilityPath::Included(_, _) => true,
            FileApplicabilityPath::Text(_) | FileApplicabilityPath::Excluded(_, _) => false,
        })
        .cloned()
        .map(|content| {
            Path::new(&content.0)
                .join(match content.1 {
                    FileApplicabilityPath::Included(text, _) => text,
                    FileApplicabilityPath::Excluded(_, text) => text,
                    FileApplicabilityPath::Text(text) => text,
                })
                .to_str()
                .unwrap_or("")
                .to_string()
        })
        .collect::<Vec<String>>();
    while let Some(x) = path_to_search.parent() {
        let filename = match match path_to_search.parent() {
            Some(paren) => paren.join(&name),
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

fn path_string_vec_contains_exact_glob_pathbuf(
    path: &Path,
    acc: &[(String, FileApplicabilityPath)],
) -> bool {
    let name = path.as_os_str().to_str().unwrap_or("");
    let span = trace_span!("Searching path buf for glob for exclusion", value = name);
    let _enter = span.enter();
    let starting_path = path.to_str().unwrap_or("");
    let starting_glob_test = acc
        .iter()
        .filter(|(_, file)| match file {
            FileApplicabilityPath::Included(_, _) => true,
            FileApplicabilityPath::Text(_) | FileApplicabilityPath::Excluded(_, _) => false,
        })
        .cloned()
        .map(|content| {
            let glob_to_match = &Path::new(&content.0)
                .join(match content.1 {
                    FileApplicabilityPath::Included(text, _) => text,
                    FileApplicabilityPath::Excluded(_, text) => text,
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
        })
        .any(|v| {
            trace!(v);
            v
        });
    trace!("{:#?} {:#?}", starting_path, starting_glob_test);
    if starting_glob_test {
        return true;
    }
    false
}

fn path_string_vec_contains_any_excluded_glob_pathbuf(
    path: &Path,
    acc: &[(String, FileApplicabilityPath)],
) -> bool {
    let name = path.as_os_str().to_str().unwrap_or("");
    let span = trace_span!("Searching path buf for glob for exclusion", value = name);
    let _enter = span.enter();
    let mut path_to_search = path;
    let starting_path = path.as_os_str().to_str().unwrap_or_default();
    let starting_glob_test = acc
        .iter()
        .filter(|(_, file)| match file {
            FileApplicabilityPath::Excluded(_, _) => true,
            FileApplicabilityPath::Text(_) | FileApplicabilityPath::Included(_, _) => false,
        })
        .cloned()
        .map(|content| {
            let glob_to_match = &Path::new(&content.0)
                .join(match content.1 {
                    FileApplicabilityPath::Included(text, _) => text,
                    FileApplicabilityPath::Excluded(_, text) => text,
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
        })
        .any(|v| {
            trace!(v);
            v
        });
    trace!(
        "Starting path complete:\t{:#?}\nTest Result:\t{:#?}",
        starting_path,
        starting_glob_test
    );
    if starting_glob_test {
        return true;
    }
    while let Some(x) = path_to_search.parent() {
        let searched_path = path_to_search.to_str().unwrap_or("");
        let has_glob = acc
            .iter()
            .filter(|(_, file)| match file {
                FileApplicabilityPath::Excluded(_, _) => true,
                FileApplicabilityPath::Text(_) | FileApplicabilityPath::Included(_, _) => false,
            })
            .cloned()
            .map(|content| {
                let glob_to_match = &Path::new(&content.0).join(match content.1 {
                    FileApplicabilityPath::Included(text, _) => text,
                    FileApplicabilityPath::Excluded(_, text) => text,
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
            searched_path,
            has_glob
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

fn get_file_contents_based_on_applicability<C: ClientState>(
    dir_entry: &DirEntry<C>,
    substitutions: Vec<Substitution>,
    applic_config: ApplicabilityConfigElement,
) -> String {
    let file_contents = get_file_contents(dir_entry.path().as_path());
    let (start_syntax, end_syntax) = get_comment_syntax(dir_entry.path().as_path(), "", "");
    let parsed_contents = parse_applicability(
        file_contents.as_str(),
        start_syntax.as_str(),
        end_syntax.as_str(),
    );
    let contents = match parsed_contents {
        Ok((_remaining, results)) => results,
        Err(_) => panic!("Failed to unwrap parsed AST"),
    };
    contents
        .iter()
        .map(|c| {
            c.substitute(&substitutions)
                .sanitize(
                    applic_config.clone().get_features(),
                    &applic_config.clone().get_name(),
                    &substitutions,
                    applic_config.get_parent_group(),
                    Some(applic_config.get_configs().as_slice()),
                )
                .into()
        })
        .collect::<Vec<String>>()
        .join("")
}

// should get results like:
// path, Included(text1,else_text1)
// path, Excluded(else_text1, text1)
// path, Excluded(text2,else_text2)
// path, Included(else_text2, text2)
// path, Text(text3)
fn get_file_applicability_contents_based_on_applicability<C: ClientState>(
    dir_entry: &DirEntry<C>,
    substitutions: Vec<Substitution>,
    applic_config: ApplicabilityConfigElement,
) -> Vec<FileApplicabilityPath> {
    let file_contents = get_file_contents(dir_entry.path().as_path());
    let (start_syntax, end_syntax) = get_comment_syntax(dir_entry.path().as_path(), "", "");
    let parsed_contents = parse_applicability(
        file_contents.as_str(),
        start_syntax.as_str(),
        end_syntax.as_str(),
    );
    let contents = match parsed_contents {
        Ok((_remaining, results)) => results,
        Err(_) => panic!("Failed to unwrap parsed AST"),
    };
    contents
        .iter()
        .map(|c| {
            c.substitute(&substitutions).parse_path(
                applic_config.clone().get_features(),
                &applic_config.clone().get_name(),
                &substitutions,
                applic_config.get_parent_group(),
                Some(applic_config.get_configs().as_slice()),
            )
        })
        .chain(contents.iter().map(|c| {
            c.substitute(&substitutions).parse_path_else(
                applic_config.clone().get_features(),
                &applic_config.clone().get_name(),
                &substitutions,
                applic_config.get_parent_group(),
                Some(applic_config.get_configs().as_slice()),
            )
        }))
        .collect()
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
