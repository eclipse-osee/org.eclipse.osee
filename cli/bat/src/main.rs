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
use applic_config::BatConfigElement;
use applicability_parser::{parse_applicability,sanitize_applicability::SanitizeApplicability,substitute_applicability::SubstituteApplicability};
use clap::Parser;
use std::{ thread::{ self }, fs::{File, create_dir_all, self}, sync::mpsc::channel, path::Path, io::ErrorKind };
use common_path::common_path;

mod applic_config;

/// Block Applicability Tool(BAT)
/// Supported Default Formats:
/// *.md: Starting Syntax: `` Ending Syntax: ``
#[derive(Parser)]
#[clap(author="Luciano Vaglienti",version,verbatim_doc_comment)]
struct CliOptions {
    /// Config file containing the valid applicabilities,configurations, and substitutions.
    /// An example:
    ///[
    ///     {    
    ///         "normalizedName":"PRODUCT_A",
    ///         "features":["ENGINE_5=A2543","JHU_CONTROLLER=EXCLUDED","ROBOT_ARM_LIGHT=EXCLUDED","ROBOT_SPEAKER=SPKR_A"],
    ///         "substitutions":[
    ///             {"matchText":"SOME_SUBSTITUTION","substitute":"SOME NEW TEXT CONTENT"}
    ///         ]
    ///     },
    ///     {   
    ///         "normalizedName":"PRODUCT_B",
    ///         "features":["ENGINE_5=A2543","JHU_CONTROLLER=INCLUDED","ROBOT_ARM_LIGHT=INCLUDED","ROBOT_SPEAKER=SPKR_A"]
    ///     },
    ///     {   
    ///         "normalizedName":"abGroup",
    ///         "features":["ENGINE_5=A2543","JHU_CONTROLLER=INCLUDED","ROBOT_ARM_LIGHT=INCLUDED","ROBOT_SPEAKER=SPKR_A"]
    ///     },
    ///     {   
    ///         "normalizedName":"PRODUCT_D",
    ///         "features":["ENGINE_5=B5543","JHU_CONTROLLER=EXCLUDED","ROBOT_ARM_LIGHT=EXCLUDED","ROBOT_SPEAKER=SPKR_B"]
    ///     },
    ///     {   
    ///         "normalizedName":"PRODUCT_C",
    ///         "features":["ENGINE_5=A2543","JHU_CONTROLLER=INCLUDED","ROBOT_ARM_LIGHT=EXCLUDED","ROBOT_SPEAKER=SPKR_B"]
    ///     }
    ///]
    #[clap(short, long,verbatim_doc_comment)]
    applicability_config: std::path::PathBuf,

    /// The output directory for processed files.
    #[clap(short, long)]
    out_dir: std::path::PathBuf,

    /// The input files to pre-process
    #[clap(short, long, value_delimiter = ',', value_terminator=";")]
    srcs: Vec<std::path::PathBuf>,

    /// Override start comment syntax if the file type is not already natively supported.
    /// For a C style language, you should opt for // or if you are intending to use multi-line,
    /// use /*
    #[clap(short, long, default_value="//",verbatim_doc_comment)]
    begin_comment_syntax:String,

    /// Override end comment syntax if the file type is not already natively supported.
    /// For a C style language you should not fill this out, unless you are intending to use multi-line, in which case
    /// you should use */
    #[clap(short, long, default_value=None,verbatim_doc_comment)]
    end_comment_syntax:Option<String>
}

fn main() {
    let args = CliOptions::parse();
    let out_dir = args.out_dir.as_path();
    let applic_config: Vec<BatConfigElement> = match File::open(args.applicability_config) {
        Ok(file) =>
            match serde_json::from_reader(file) {
                Ok(res) => res,
                Err(e) => panic!("Could not parse applicability config JSON \n{:?}: \tat line {:?} column {:?}",e.classify(),e.line(),e.column()),
            }

        Err(e) => panic!("Could not find applicability config {:?}",e),
    };
    let start_comment_syntax = args.begin_comment_syntax.as_str();
    let end_comment_syntax_temp = match args.end_comment_syntax{
        Some(i) =>i,
        None => "".to_owned(),
    };
    let end_comment_syntax= end_comment_syntax_temp.as_str();
    thread::scope(|scope| {
        for input in &args.srcs {
            let applic_config_for_file = applic_config.clone();
            let _outer_thread = scope.spawn(move ||{
                
                println!("Processing input {}", input.to_str().unwrap_or(""));
                let file_contents = get_file_contents(input);
                let (start_syntax,end_syntax)= get_comment_syntax(input, start_comment_syntax, end_comment_syntax);
                let content_result = parse_applicability(&file_contents,start_syntax.as_str(),end_syntax.as_str());
                let contents = match content_result{
                    Ok((_remaining, results)) => results,
                    Err(_) => panic!("Failed to unwrap parsed AST"),
                };
                for config in applic_config_for_file.iter().cloned(){
                    let copy = contents.clone();
                    let cloned_config= config.clone();
                    let (sender, receiver) = channel();
                    let _s1 =scope.spawn(move ||{
                        let substitutions = match config.substitutions{
                            Some(res) => res,
                            None => vec![],
                        };
                    let sanitized_content = copy.iter().cloned().map(|c|c.substitute(&substitutions).sanitize(config.features.to_owned(), &config.normalized_name,&substitutions).into()).collect::<Vec<String>>().join("");
                    sender.send(sanitized_content)
                        
                    });
    
                    let _s2 = scope.spawn(move||{
        
                        // make sure the folders are available
                        match create_dir_all(out_dir){
                            Ok(dir) => dir,
                            Err(e) => panic!("Failed to create output directory {:#?}! Error: {:#?}",out_dir,e),
                        };
                        //convert any relative paths to absolute paths
                        let out_dirs = match fs:: canonicalize(out_dir){
                            Ok(i) => i,
                            Err(e) => {println!("Error finding output directory {:#?} .Received {:#?}",out_dir,e); out_dir.to_path_buf()},
                        };
                        let input_path = match fs:: canonicalize(input){
                            Ok(i) => i,
                            Err(e) => {println!("Error finding input file {:#?} .Received {:#?}. You should check to see if the file exists.",input,e); input.to_path_buf()},
                        };
                        let prefix = common_path(&input_path,&out_dirs).unwrap();
                        let config_path = Path::new("config").join(Path::new(&cloned_config.normalized_name));
                        let output_config_path = out_dirs.join(config_path);
                        let processed_path =output_config_path.join(match input_path.strip_prefix(prefix){
                            Ok(i) => i,
                            Err(e) => {println!("Error stripping input prefix {:?} from input {:?}",e, input); input},
                        });
                        let parent = &processed_path.parent().unwrap();
                        let _create_dir_all = create_dir_all(parent);
                        let _f = match File::create(&processed_path){
                            Ok(fc) => fc,
                            Err(e) => panic!("Problem creating the file: {:?}", e),
                        };
                        let file_result = File::open(&processed_path);
        
                        let _file = match file_result {
                            Ok(file) => file,
                            Err(error) => match error.kind() {
                                ErrorKind::NotFound => match File::create(&processed_path) {
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
                            match fs::write(&processed_path,received){
                                Ok(r) => r,
                                Err(e) => println!("Failed to write {:#?} to {:#?}. \n Error Code: {:#?}",_text, processed_path,e),
                            };
                        }
                        //close the file
                    });
                }
            });
        }
    });
}

fn get_file_contents(file:&Path) ->String{
    match std::fs::read_to_string(file){
        Ok(i) => i,
        Err(e) => panic!("Can't convert file {:#?} to bytes. \n Error: {:#?}",file.as_os_str(),e),
    }
}
/// Sets the comment syntax to the defaults if they are defined for a given file type.
/// 
/// Currently supported:
/// 
///     .md
fn get_comment_syntax(file: &Path, start_comment_syntax:& str,end_comment_syntax:& str) ->(String,String){
    let file_ref_copy = file;
    let ext = match file_ref_copy.extension(){
        Some(extension) => extension.to_str(),
        None => None,//do nothing
    };
    let (start_comment_syntax, end_comment_syntax) = match ext{
        Some("md")=>("``","``"),
        _rest=> (start_comment_syntax,end_comment_syntax),
    };
    println!("start comment syntax {:#?}\r\n end comment syntax {:#?}",start_comment_syntax,end_comment_syntax);
    (start_comment_syntax.to_string(),end_comment_syntax.to_string())
}