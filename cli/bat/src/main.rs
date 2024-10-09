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
use anyhow::Context;
use applicability_parser::parse_applicability;
use applicability_parser_config::{
    applic_config::ApplicabilityConfigElement, get_comment_syntax, get_file_contents,
};
use applicability_sanitization::SanitizeApplicability;
use applicability_substitution::SubstituteApplicability;
use clap::Parser;
use clap_verbosity_flag::{Verbosity, WarnLevel};
use common_path::common_path;
use std::{
    fs::{self, create_dir_all, File},
    io::ErrorKind,
    path::{Path, PathBuf},
    sync::mpsc::{channel, Receiver},
    thread,
};
use tracing::{info, Level};

/// Block Applicability Tool(BAT)
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
///     *.tex                           Starting Syntax: \if   Ending Syntax: {}
///     WORKSPACE                       Starting Syntax: #      Ending Syntax:
///     BUILD                           Starting Syntax: #      Ending Syntax:
///     *.fileApplicability             Starting Syntax: #      Ending Syntax:
///     *.applicability                 Starting Syntax: #      Ending Syntax:
#[derive(Parser)]
#[clap(author = "Luciano Vaglienti", version, verbatim_doc_comment)]
struct CliOptions {
    /// Config file containing the valid applicabilities,configurations, and substitutions.
    /// An example:
    ///[
    ///     {
    ///         "name":"PRODUCT_A",
    ///         "group":"abGroup",
    ///         "features":["ENGINE_5=A2543","JHU_CONTROLLER=Excluded","ROBOT_ARM_LIGHT=Excluded","ROBOT_SPEAKER=SPKR_A"],
    ///         "substitutions":[
    ///             {"matchText":"SOME_SUBSTITUTION","substitute":"SOME NEW TEXT CONTENT"}
    ///         ]
    ///     },
    ///     {
    ///         "name":"PRODUCT_B",
    ///         "group":"abGroup",
    ///         "features":["ENGINE_5=A2543","JHU_CONTROLLER=Included","ROBOT_ARM_LIGHT=Included","ROBOT_SPEAKER=SPKR_A"]
    ///     },
    ///     {
    ///         "name":"abGroup",
    ///         "configs":["PRODUCT_A","PRODUCT_B"],
    ///         "features":["ENGINE_5=A2543","JHU_CONTROLLER=Included","ROBOT_ARM_LIGHT=Included","ROBOT_SPEAKER=SPKR_A"]
    ///     },
    ///     {
    ///         "name":"PRODUCT_D",
    ///         "group":"",
    ///         "features":["ENGINE_5=B5543","JHU_CONTROLLER=Excluded","ROBOT_ARM_LIGHT=Excluded","ROBOT_SPEAKER=SPKR_B"]
    ///     },
    ///     {
    ///         "name":"PRODUCT_C",
    ///         "group":"",
    ///         "features":["ENGINE_5=A2543","JHU_CONTROLLER=Included","ROBOT_ARM_LIGHT=Excluded","ROBOT_SPEAKER=SPKR_B"]
    ///     }
    ///]
    #[clap(short, long, verbatim_doc_comment)]
    applicability_config: std::path::PathBuf,

    /// The output directory for processed files.
    #[clap(short, long)]
    out_dir: std::path::PathBuf,

    /// The input files to pre-process
    #[clap(short, long, value_delimiter = ',', value_terminator = ";")]
    srcs: Vec<std::path::PathBuf>,

    /// Override start comment syntax if the file type is not already natively supported.
    /// For a C style language, you should opt for // or if you are intending to use multi-line,
    /// use /*
    #[clap(short, long, default_value = "//", verbatim_doc_comment)]
    begin_comment_syntax: String,

    /// Override end comment syntax if the file type is not already natively supported.
    /// For a C style language you should not fill this out, unless you are intending to use multi-line, in which case
    /// you should use */
    #[clap(short, long, default_value = None, verbatim_doc_comment)]
    end_comment_syntax: Option<String>,

    /// Use output directly as specified instead of looking for a common path
    #[clap(short, long, verbatim_doc_comment)]
    use_direct_output: bool,

    /// Do not write the processed files to a directory in {out_dir}/config/{config_name}
    #[clap(short, long, verbatim_doc_comment)]
    no_write_config_folder: bool,

    ///Verbosity of output, defaults to warnings and errors.
    /// -q will have no output
    /// -v will show warnings,info and errors
    /// -vv will show warnings,info,errors, and debug
    /// -vvv will show warnings,info,errors, debug and trace output
    #[command(flatten)]
    verbose: Verbosity<WarnLevel>,
}

fn main() {
    let args = CliOptions::parse();
    let handle = std::io::BufWriter::new(std::io::stdout());
    let (non_blocking, _guard) = tracing_appender::non_blocking(handle);
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
        .with_line_number(true)
        .pretty()
        .finish();
    let _ = tracing::subscriber::set_global_default(subscriber)
        .map_err(|_err| eprintln!("Unable to set global default subscriber"));
    let out_dir = args.out_dir.as_path();
    let applic_config: Vec<ApplicabilityConfigElement> = match File::open(args.applicability_config)
    {
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
    let start_comment_syntax = args.begin_comment_syntax.as_str();
    let end_comment_syntax_temp = match args.end_comment_syntax {
        Some(i) => i,
        None => "".to_owned(),
    };
    let end_comment_syntax = end_comment_syntax_temp.as_str();
    thread::scope(|scope| {
        for input in &args.srcs {
            let applic_config_for_file = applic_config.clone();
            let use_direct_output = args.use_direct_output;
            let should_not_write_config_folder = args.no_write_config_folder;
            let _outer_thread = scope.spawn(move || {
                info!("Processing input {}", input.to_str().unwrap_or(""));
                let file_contents = get_file_contents(input);
                let (start_syntax, end_syntax) =
                    get_comment_syntax(input, start_comment_syntax, end_comment_syntax);
                let content_result =
                    parse_applicability(&file_contents, start_syntax.as_str(), end_syntax.as_str());
                let contents = match content_result {
                    Ok((_remaining, results)) => results,
                    Err(_) => panic!("Failed to unwrap parsed AST"),
                };
                for config in applic_config_for_file {
                    let copy = contents.clone();
                    let input_config = config.clone();
                    let output_config = config.clone();
                    let (sender, receiver) = channel();
                    let _s1 = scope.spawn(move || {
                        let substitutions = config.clone().get_substitutions().unwrap_or_default();
                        let sanitized_content = copy
                            .iter()
                            .cloned()
                            .map(|c| {
                                c.substitute(&substitutions)
                                    .sanitize(
                                        input_config.clone().get_features(),
                                        &input_config.clone().get_name(),
                                        &substitutions,
                                        config.get_parent_group(),
                                        Some(config.get_configs().as_slice()),
                                    )
                                    .into()
                            })
                            .collect::<Vec<String>>()
                            .join("");
                        sender.send(sanitized_content)
                    });

                    let _s2 = scope.spawn(move || {
                        output_thread(
                            out_dir,
                            input,
                            should_not_write_config_folder,
                            use_direct_output,
                            output_config,
                            receiver,
                        )
                    });
                }
            });
        }
    });
}
#[tracing::instrument(err)]
fn create_starting_output_directory_structure(out_dir: &Path) -> Result<(), anyhow::Error> {
    create_dir_all(out_dir)
        .with_context(|| format!("Failed to create output directory {:#?}!", out_dir))
}

#[tracing::instrument(err)]
fn find_starting_output_directory(out_dir: &Path) -> Result<PathBuf, anyhow::Error> {
    fs::canonicalize(out_dir)
        .with_context(|| format!("Error finding output directory {:#?}", out_dir))
}
#[tracing::instrument(err)]
fn find_starting_input_directory(input: &PathBuf) -> Result<PathBuf, anyhow::Error> {
    fs::canonicalize(input).with_context(|| {
        format!(
            "Error finding input file {:#?} . You should check to see if the file exists.",
            input
        )
    })
}
#[tracing::instrument(err)]
fn output_thread(
    out_dir: &Path,
    input: &PathBuf,
    should_not_write_config_folder: bool,
    use_direct_output: bool,
    cloned_config: ApplicabilityConfigElement,
    receiver: Receiver<String>,
) -> Result<(), anyhow::Error> {
    create_starting_output_directory_structure(out_dir)?;
    //convert any relative paths to absolute paths
    let mut out_dirs = find_starting_output_directory(out_dir)?;
    let input_path = find_starting_input_directory(input)?;
    let config_path = match should_not_write_config_folder {
        false => Path::new("config").join(Path::new(&cloned_config.clone().get_name())),
        true => PathBuf::new(),
    };
    out_dirs.push(config_path);
    out_dirs.push(match use_direct_output {
        true => match input.file_name() {
            Some(file_name) => match file_name.to_str() {
                Some(i) => i,
                None => panic!(
                    "Failed to unwrap input file name in direct output mode! {:#?}",
                    file_name
                ),
            },
            None => panic!(
                "Failed to unwrap input file name in direct output mode! {:#?}",
                input
            ),
        },
        false => match common_path(&input_path, &out_dirs) {
            Some(prefix) => match input_path.strip_prefix(prefix) {
                Ok(i) => match i.to_str() {
                    Some(str) => str,
                    None => panic!(
                        "Failed to unwrap input file name in common path mode! {:#?}",
                        i
                    ),
                },
                Err(e) => {
                    println!(
                        "Error stripping input prefix {:?} from input {:?}",
                        e, input
                    );
                    match input.to_str() {
                        Some(i) => i,
                        None => panic!(
                            "Failed to unwrap input file name in common path mode! {:#?}",
                            input
                        ),
                    }
                }
            },
            None => panic!(
                "Error finding the common path between the input {:#?} and output directory {:#?}.",
                input_path, out_dirs
            ),
        },
    });
    let parent = &out_dirs.parent().unwrap();
    let _create_dir_all = create_dir_all(parent);
    let parent_path_buf = parent.to_path_buf();
    let create_directory = &parent_path_buf;
    create_dir_all(create_directory)
        .with_context(|| format!("Failed to create directory {:#?}", create_directory))?;
    let _f = match File::create(&out_dirs) {
        Ok(fc) => fc,
        Err(e) => panic!("Problem creating the file: {:?}", e),
    };
    let file_result = File::open(&out_dirs);

    let _file = match file_result {
        Ok(file) => file,
        Err(error) => match error.kind() {
            ErrorKind::NotFound => match File::create(&out_dirs) {
                Ok(fc) => fc,
                Err(e) => panic!("Problem creating the file: {:?}", e),
            },
            other_error => {
                panic!("Problem opening the file: {:?}", other_error);
            }
        },
    };

    for received in receiver {
        //write the file out
        let _text = received.clone();
        match fs::write(&out_dirs, received) {
            Ok(r) => r,
            Err(e) => println!(
                "Failed to write {:#?} to {:#?}. \n Error Code: {:#?}",
                _text, out_dirs, e
            ),
        };
    }
    Ok(())
}
