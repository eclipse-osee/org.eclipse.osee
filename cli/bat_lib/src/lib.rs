use anyhow::Context;
use applicability_parser_config::{get_config_from_file, get_file_contents};
use applicability_sanitization::v2::SanitizeApplicabilityV2;
use applicability_tokens_to_ast::tree::ApplicabilityExprKind;
use bill_of_features::{BillOfFeatures, BillOfFeaturesEnum, read_multiple_bill_of_features};
use clap::Parser;
use clap_verbosity_flag::{Verbosity, WarnLevel};
use common_path::common_path;
use doc_definitions::{applicability_definitions, supported_file_types};
use std::{
    fs::{self, File, create_dir_all},
    io::ErrorKind,
    path::{Path, PathBuf},
    sync::mpsc::{Receiver, channel},
    thread,
};
use tracing::info;
#[derive(Parser, Debug)]
#[clap(
    author = "Luciano Vaglienti",
    version,
    about = BatCliOptions::about()
)]
pub struct BatCliOptions {
    #[command(flatten)]
    pub options: BatInternalCliOptions,
}
impl BatCliOptions {
    pub fn about() -> String {
        "".to_string()
            + r#"Block Applicability Tool(BAT)
----------------------------------------------------{n}"#
            + &BatInternalCliOptions::bat_about()
    }
}
#[derive(Parser, Debug)]
#[command(
    author = "Luciano Vaglienti",
    version,
    verbatim_doc_comment,
    name = "file",
    about = BatInternalCliOptions::base_about()
)]
pub struct BatInternalCliOptions {
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
    pub verbose: Verbosity<WarnLevel>,
}
impl BatInternalCliOptions {
    pub fn base_about() -> String {
        "".to_string()
            + r#"Process Applicability tags on a given file or set of files for 1 or more configurations present in a multi-bill of features file."#
    }
    pub fn bat_about() -> String {
        Self::base_about() + applicability_definitions() + supported_file_types()
    }
}

pub fn perform_block_applicability(args: BatInternalCliOptions) -> anyhow::Result<()> {
    let out_dir = args.out_dir.as_path();
    let applic_config = read_multiple_bill_of_features(args.applicability_config)?;
    thread::scope(|scope| {
        for input in &args.srcs {
            let applic_config_for_file = applic_config.clone();
            let use_direct_output = args.use_direct_output;
            let should_not_write_config_folder = args.no_write_config_folder;
            let _outer_thread = scope.spawn(move || {
                info!("Processing input {}", input.to_str().unwrap_or(""));
                let file_contents = get_file_contents(input);
                let parser_fn = get_config_from_file(input);
                let ast = parser_fn(file_contents.as_str())
                    .unwrap_or_default()
                    .into_iter()
                    .map(Into::<ApplicabilityExprKind<String>>::into)
                    .collect::<Vec<_>>();
                for config in applic_config_for_file {
                    let contents = ast.clone();
                    let input_config = config.clone();
                    let output_config = config.clone();
                    let (sender, receiver) = channel();
                    let _s1 = scope.spawn(move || {
                        let substitutions = config.clone().get_substitutions().unwrap_or_default();
                        let sanitized_content = contents
                            .iter()
                            .cloned()
                            .filter_map(|ast_result| {
                                let group = config.get_parent_group().map(|x| x.to_string());
                                let configs = config
                                    .get_configs()
                                    .iter()
                                    .map(|x| x.to_string())
                                    .collect::<Vec<_>>();
                                ast_result
                                    .sanitize(
                                        input_config.clone().get_features().as_slice(),
                                        &input_config.clone().get_name(),
                                        &substitutions,
                                        group.as_ref(),
                                        Some(configs.as_slice()),
                                        Some(false),
                                        &[],
                                    )
                                    .ok()
                            })
                            .collect::<Vec<_>>()
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
    Ok(())
}
#[tracing::instrument(err)]
fn create_starting_output_directory_structure(out_dir: &Path) -> Result<(), anyhow::Error> {
    create_dir_all(out_dir)
        .with_context(|| format!("Failed to create output directory {out_dir:#?}!"))
}

#[tracing::instrument(err)]
fn find_starting_output_directory(out_dir: &Path) -> Result<PathBuf, anyhow::Error> {
    fs::canonicalize(out_dir)
        .with_context(|| format!("Error finding output directory {out_dir:#?}"))
}
#[tracing::instrument(err)]
fn find_starting_input_directory(input: &PathBuf) -> Result<PathBuf, anyhow::Error> {
    fs::canonicalize(input).with_context(|| {
        format!("Error finding input file {input:#?} . You should check to see if the file exists.")
    })
}
#[tracing::instrument(err)]
fn output_thread(
    out_dir: &Path,
    input: &PathBuf,
    should_not_write_config_folder: bool,
    use_direct_output: bool,
    cloned_config: BillOfFeaturesEnum,
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
                    "Failed to unwrap input file name in direct output mode! {file_name:#?}",
                ),
            },
            None => panic!(
                "Failed to unwrap input file name in direct output mode! {input:#?}",
            ),
        },
        false => match common_path(&input_path, &out_dirs) {
            Some(prefix) => match input_path.strip_prefix(prefix) {
                Ok(i) => match i.to_str() {
                    Some(str) => str,
                    None => panic!(
                        "Failed to unwrap input file name in common path mode! {i:#?}",
                    ),
                },
                Err(e) => {
                    println!(
                        "Error stripping input prefix {e:?} from input {input:?}",
                    );
                    match input.to_str() {
                        Some(i) => i,
                        None => panic!(
                            "Failed to unwrap input file name in common path mode! {input:#?}",
                        ),
                    }
                }
            },
            None => panic!(
                "Error finding the common path between the input {input_path:#?} and output directory {out_dirs:#?}.",
            ),
             },
    });
    let parent = &out_dirs.parent().unwrap();
    let _create_dir_all = create_dir_all(parent);
    let parent_path_buf = parent.to_path_buf();
    let create_directory = &parent_path_buf;
    create_dir_all(create_directory)
        .with_context(|| format!("Failed to create directory {create_directory:#?}",))?;
    let _f = match File::create(&out_dirs) {
        Ok(fc) => fc,
        Err(e) => panic!("Problem creating the file: {e:?}"),
    };
    let file_result = File::open(&out_dirs);

    let _file = match file_result {
        Ok(file) => file,
        Err(error) => match error.kind() {
            ErrorKind::NotFound => match File::create(&out_dirs) {
                Ok(fc) => fc,
                Err(e) => panic!("Problem creating the file: {e:?}"),
            },
            other_error => {
                panic!("Problem opening the file: {other_error:?}");
            }
        },
    };

    for received in receiver {
        //write the file out
        let _text = received.clone();
        match fs::write(&out_dirs, received) {
            Ok(r) => r,
            Err(e) => {
                println!("Failed to write {_text:#?} to {out_dirs:#?}. \n Error Code: {e:#?}",)
            }
        };
    }
    Ok(())
}
