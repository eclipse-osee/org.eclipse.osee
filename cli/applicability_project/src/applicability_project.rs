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
use path_slash::PathExt;
use std::{
    fs,
    ops::AddAssign,
    path::{Path, PathBuf},
};
use thiserror::Error;

use applicability_path::FileApplicabilityPath;
use glob::{MatchOptions, glob_with};
use globset::GlobBuilder;

#[derive(Debug)]
pub struct FileApplicabilityLinkValidationError {
    pub path: String,
    pub error: FileApplicabilityLinkValidationInternalError,
}

#[derive(Debug, Error)]
pub enum FileApplicabilityLinkValidationInternalError {
    #[error("{:?}",.0)]
    Io(#[from] std::io::Error),
    #[error("{:?}",.0)]
    Glob(#[from] glob::GlobError),
    #[error("{:?}",.0)]
    PatternError(#[from] glob::PatternError),
}

impl From<std::io::ErrorKind> for FileApplicabilityLinkValidationInternalError {
    fn from(val: std::io::ErrorKind) -> Self {
        let error: std::io::Error = val.into();
        error.into()
    }
}

#[derive(Debug, Default, Clone)]
pub enum ProjectMode {
    #[default]
    Include,
    Exclude,
    All,
}
#[derive(Debug, Clone)]
pub struct FileApplicabilityEntry {
    pub(crate) path: PathBuf,
    pub(crate) entry: FileApplicabilityPath,
}
#[derive(Debug, Default, Clone)]
pub struct ApplicabilityProject {
    pub(crate) mode: ProjectMode,
    pub(crate) current_directory: PathBuf,
    pub(crate) starting_directory: PathBuf,
    pub(crate) paths: Vec<FileApplicabilityEntry>,
}
impl ApplicabilityProject {
    fn current_directory_exists_up_to_root(&self) -> bool {
        //walk up the tree, and of the current_directory until it's not present in the paths vec, or success
        let mut current_directory_path = vec![];
        let mut cd = self.current_directory.clone();
        while let Some(parent) = cd.parent()
            && !get_parent_as_string(parent).is_empty()
        {
            current_directory_path.push(parent.to_path_buf());
            cd = parent.to_path_buf();
        }
        let paths: Vec<PathBuf> = self.paths.iter().map(|x| x.path.clone()).collect();
        !current_directory_path
            .into_iter()
            .filter(|directory| paths.contains(directory))
            .collect::<Vec<PathBuf>>()
            .is_empty()
    }
    fn directory_is_excluded_non_glob(&self, path: &Path) -> bool {
        let mut path_to_search = path.to_path_buf();
        let excluded = self
            .paths
            .iter()
            .filter(|x| match x.entry {
                FileApplicabilityPath::Excluded(_) => true,
                FileApplicabilityPath::Text(_) | FileApplicabilityPath::Included(_) => false,
            })
            .cloned()
            .filter_map(|content| {
                Path::new(&content.path)
                    .join(match content.entry {
                        FileApplicabilityPath::Included(text) => text,
                        FileApplicabilityPath::Excluded(text) => text,
                        FileApplicabilityPath::Text(text) => text,
                    })
                    .to_slash()
                    .map(|x| x.to_string())
            })
            .collect::<Vec<String>>();
        while let Some(x) = path_to_search.parent() {
            if let Some(text) = path_to_search.to_str()
                && excluded.contains(&text.to_string())
            {
                return true;
            }
            path_to_search = x.to_path_buf();
        }
        false
    }

    fn directory_is_excluded_glob(&self, path: &Path) -> bool {
        let mut path_to_search = path.to_path_buf();
        let starting_directory = path.to_path_buf();
        let starting_path = starting_directory.as_os_str().to_str().unwrap_or_default();
        let starting_glob_test = self
            .paths
            .iter()
            .filter(|path_entry| match path_entry.entry {
                FileApplicabilityPath::Excluded(_) => true,
                FileApplicabilityPath::Text(_) | FileApplicabilityPath::Included(_) => false,
            })
            .cloned()
            .any(|content| {
                let glob_to_match = &Path::new(&content.path)
                    .join(match content.entry {
                        FileApplicabilityPath::Included(text) => text,
                        FileApplicabilityPath::Excluded(text) => text,
                        FileApplicabilityPath::Text(text) => text,
                    })
                    .to_slash()
                    .unwrap()
                    .to_string();
                if let Ok(g) = GlobBuilder::new(glob_to_match.as_str())
                    .literal_separator(true)
                    .build()
                {
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
        if starting_glob_test {
            return true;
        }
        while let Some(x) = path_to_search.parent() {
            let searched_path = path_to_search.to_str().unwrap_or("");
            let has_glob = self
                .paths
                .iter()
                .filter(|path_entry| match path_entry.entry {
                    FileApplicabilityPath::Excluded(_) => true,
                    FileApplicabilityPath::Text(_) | FileApplicabilityPath::Included(_) => false,
                })
                .cloned()
                .any(|content| {
                    let glob_to_match = &Path::new(&content.path).join(match content.entry {
                        FileApplicabilityPath::Included(text) => text,
                        FileApplicabilityPath::Excluded(text) => text,
                        FileApplicabilityPath::Text(text) => text,
                    });
                    if let Ok(g) = GlobBuilder::new(
                        Path::new(glob_to_match)
                            .to_slash()
                            .unwrap()
                            .to_string()
                            .as_str(),
                    )
                    .literal_separator(true)
                    .build()
                    {
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
                });
            if has_glob {
                return true;
            }
            path_to_search = x.to_path_buf();
        }
        false
    }

    fn directory_is_excluded(&self, path: &Path) -> bool {
        self.directory_is_excluded_non_glob(path) || self.directory_is_excluded_glob(path)
    }
    fn current_directory_is_excluded(&self) -> bool {
        self.directory_is_excluded(&self.current_directory)
    }
    fn directory_is_included_non_glob(&self, path: &Path) -> bool {
        let mut path_to_search = path.to_path_buf();
        let file_name = match path_to_search.file_name() {
            Some(nm) => match nm.to_str() {
                Some(str) => str.to_string(),
                None => "".to_string(),
            },
            None => "".to_string(),
        };
        let included = self
            .paths
            .iter()
            .filter(|file_entry| match file_entry.entry {
                FileApplicabilityPath::Included(_) => true,
                FileApplicabilityPath::Text(_) | FileApplicabilityPath::Excluded(_) => false,
            })
            .cloned()
            .filter_map(|file_entry| {
                Path::new(&file_entry.path)
                    .join(match file_entry.entry {
                        FileApplicabilityPath::Included(text) => text,
                        FileApplicabilityPath::Excluded(text) => text,
                        FileApplicabilityPath::Text(text) => text,
                    })
                    .to_slash()
                    .map(|x| x.to_string())
            })
            .collect::<Vec<String>>();
        while let Some(parent) = path_to_search.parent() {
            let filename = match match path_to_search.parent() {
                Some(paren) => match paren.join(&file_name).to_slash() {
                    Some(text) => Path::new(&text.to_string()).to_path_buf(),
                    None => PathBuf::new(),
                },
                None => PathBuf::new(),
            }
            .to_str()
            {
                Some(str) => str.to_string(),
                None => "".to_string(),
            };
            if included.contains(&filename) {
                return true;
            }
            path_to_search = parent.to_path_buf();
        }
        false
    }

    fn directory_is_included_glob(&self, path: &Path) -> bool {
        let starting_path = path.to_str().unwrap_or("");
        let starting_glob_test = self
            .paths
            .iter()
            .filter(|file_entry| match file_entry.entry {
                FileApplicabilityPath::Included(_) => true,
                FileApplicabilityPath::Text(_) | FileApplicabilityPath::Excluded(_) => false,
            })
            .cloned()
            .any(|content| {
                let glob_to_match = &Path::new(&content.path)
                    .join(match content.entry {
                        FileApplicabilityPath::Included(text) => text,
                        FileApplicabilityPath::Excluded(text) => text,
                        FileApplicabilityPath::Text(text) => text,
                    })
                    .to_slash()
                    .unwrap()
                    .to_string();
                if let Ok(g) = GlobBuilder::new(glob_to_match)
                    .literal_separator(true)
                    .build()
                {
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
        if starting_glob_test {
            return true;
        }
        false
    }

    fn directory_is_included(&self, path: &Path) -> bool {
        self.directory_is_included_non_glob(path) || self.directory_is_included_glob(path)
    }
    //TODO: maybe remove at some point if we determine we aren't ever going to need this, and change the tests
    #[cfg(test)]
    fn current_directory_is_included(&self) -> bool {
        self.directory_is_included(&self.current_directory)
    }
    pub fn path_is_allowed(&self, path: &Path) -> bool {
        match self.mode {
            ProjectMode::Include => !self.directory_is_excluded(path),
            ProjectMode::Exclude => self.directory_is_included(path),
            ProjectMode::All => true,
        }
    }
    pub fn validate_links(
        &self,
        starting_path: &Path,
    ) -> Vec<FileApplicabilityLinkValidationError> {
        let mut failed_links = vec![];
        self.paths.iter().for_each(|file_entry| {
            let file_text = match &file_entry.entry {
                FileApplicabilityPath::Included(text) => text,
                FileApplicabilityPath::Excluded(text) => text,
                FileApplicabilityPath::Text(text) => text,
            };
            let pattern = |c| c == '*' || c == '[' || c == ']' || c == '{' || c == '}' || c == '?';
            let is_a_glob = file_text.contains(pattern);
            let validation_path = match is_a_glob {
                false => Path::new(&file_entry.path)
                    .join(file_text)
                    .to_slash()
                    .map(|x| x.to_string()),
                true => None,
            };

            if let Some(path) = validation_path
                && let Some(full_path) = starting_path
                    .join(path.clone())
                    .to_slash()
                    .map(|x| x.to_string())
                && let Err(e) = fs::exists(full_path.clone())
                && !path.is_empty()
            {
                failed_links.push(FileApplicabilityLinkValidationError {
                    path,
                    error: e.into(),
                });
            }
            if is_a_glob {
                let glob_validation_path = Path::new(&file_entry.path)
                    .join(file_text)
                    .to_slash()
                    .map(|x| x.to_string());
                let glob_options = MatchOptions {
                    case_sensitive: true,
                    require_literal_separator: true,
                    require_literal_leading_dot: false,
                };
                if let Some(glob_path) = glob_validation_path {
                    match glob_with(&glob_path, glob_options) {
                        Ok(paths) => {
                            let all_paths = paths.collect::<Vec<_>>();
                            if all_paths.is_empty() && !file_text.starts_with(".") {
                                failed_links.push(FileApplicabilityLinkValidationError {
                                    path: glob_path,
                                    error: std::io::ErrorKind::NotFound.into(),
                                })
                            } else {
                                all_paths.into_iter().for_each(|next| match next {
                                    Ok(path_buf) => {
                                        if let Err(e) = fs::exists(path_buf) {
                                            failed_links.push(
                                                FileApplicabilityLinkValidationError {
                                                    path: glob_path.clone(),
                                                    error: e.into(),
                                                },
                                            );
                                        }
                                    }
                                    Err(e) => {
                                        failed_links.push(FileApplicabilityLinkValidationError {
                                            path: glob_path.clone(),
                                            error: e.into(),
                                        })
                                    }
                                })
                            }
                        }
                        Err(e) => failed_links.push(FileApplicabilityLinkValidationError {
                            path: glob_path.clone(),
                            error: e.into(),
                        }),
                    }
                }
            }
        });
        failed_links
    }
}
impl AddAssign<FileApplicabilityEntry> for ApplicabilityProject {
    fn add_assign(&mut self, rhs: FileApplicabilityEntry) {
        match self.mode {
            ProjectMode::Include => {
                if !self.current_directory_is_excluded()
                    || self.current_directory.parent().unwrap_or(Path::new(""))
                        == self.starting_directory
                    || self.current_directory == self.starting_directory
                {
                    self.paths.push(rhs)
                }
            }
            ProjectMode::Exclude => {
                if self.current_directory_exists_up_to_root()
                    || self.current_directory.parent().unwrap_or(Path::new(""))
                        == self.starting_directory
                    || self.current_directory == self.starting_directory
                {
                    self.paths.push(rhs)
                }
            }
            ProjectMode::All => self.paths.push(rhs),
        };
    }
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

#[cfg(test)]
mod tests {
    mod internal {
        use std::path::Path;

        use applicability_path::FileApplicabilityPath;

        use crate::{
            ApplicabilityProject, FileApplicabilityEntry, applicability_project::ProjectMode,
        };

        #[test]
        fn current_directory_exists_up_to_root_all_valid() {
            let search_path = Path::new("/some/other/path");
            let path_list = vec![
                FileApplicabilityEntry {
                    path: "/".to_string().into(),
                    entry: FileApplicabilityPath::Included("".to_string()),
                },
                FileApplicabilityEntry {
                    path: "/some".to_string().into(),
                    entry: FileApplicabilityPath::Included("".to_string()),
                },
                FileApplicabilityEntry {
                    path: "/some/other".to_string().into(),
                    entry: FileApplicabilityPath::Included("".to_string()),
                },
                FileApplicabilityEntry {
                    path: "/some/other/path".to_string().into(),
                    entry: FileApplicabilityPath::Included("".to_string()),
                },
            ];
            let starting_path = Path::new("");
            let project = ApplicabilityProject {
                mode: ProjectMode::default(),
                current_directory: search_path.to_path_buf(),
                starting_directory: starting_path.to_path_buf(),
                paths: path_list,
            };
            assert!(project.current_directory_exists_up_to_root());
        }
        #[test]
        fn current_directory_exists_up_to_root_all_valid_last_different() {
            let search_path = Path::new("/some/other/path");
            let path_list = vec![
                FileApplicabilityEntry {
                    path: "/".to_string().into(),
                    entry: FileApplicabilityPath::Included("".to_string()),
                },
                FileApplicabilityEntry {
                    path: "/some".to_string().into(),
                    entry: FileApplicabilityPath::Included("".to_string()),
                },
                FileApplicabilityEntry {
                    path: "/some/other".to_string().into(),
                    entry: FileApplicabilityPath::Included("".to_string()),
                },
                FileApplicabilityEntry {
                    path: "/some/other/path2".to_string().into(),
                    entry: FileApplicabilityPath::Included("".to_string()),
                },
            ];
            let starting_path = Path::new("");
            let project = ApplicabilityProject {
                mode: ProjectMode::default(),
                current_directory: search_path.to_path_buf(),
                starting_directory: starting_path.to_path_buf(),
                paths: path_list,
            };
            assert!(project.current_directory_exists_up_to_root());
        }
        #[test]
        fn current_directory_exists_up_to_root_all_valid_last_dir_different() {
            let search_path = Path::new("/some/other/path");
            let path_list = vec![
                FileApplicabilityEntry {
                    path: "/".to_string().into(),
                    entry: FileApplicabilityPath::Included("".to_string()),
                },
                FileApplicabilityEntry {
                    path: "/some".to_string().into(),
                    entry: FileApplicabilityPath::Included("".to_string()),
                },
                FileApplicabilityEntry {
                    path: "/some/other".to_string().into(),
                    entry: FileApplicabilityPath::Included("".to_string()),
                },
                FileApplicabilityEntry {
                    path: "/some/other2/path".to_string().into(),
                    entry: FileApplicabilityPath::Included("".to_string()),
                },
            ];
            let starting_path = Path::new("");
            let project = ApplicabilityProject {
                mode: ProjectMode::default(),
                current_directory: search_path.to_path_buf(),
                starting_directory: starting_path.to_path_buf(),
                paths: path_list,
            };
            assert!(project.current_directory_exists_up_to_root());
        }
        #[test]
        fn current_directory_exists_up_to_root_not_valid() {
            let search_path = Path::new("/some/other/path");
            let path_list = vec![
                FileApplicabilityEntry {
                    path: "/".to_string().into(),
                    entry: FileApplicabilityPath::Included("".to_string()),
                },
                FileApplicabilityEntry {
                    path: "/some".to_string().into(),
                    entry: FileApplicabilityPath::Included("".to_string()),
                },
                FileApplicabilityEntry {
                    path: "/some/other2".to_string().into(),
                    entry: FileApplicabilityPath::Included("".to_string()),
                },
                FileApplicabilityEntry {
                    path: "/some/other/path".to_string().into(),
                    entry: FileApplicabilityPath::Included("".to_string()),
                },
            ];
            let starting_path = Path::new("");
            let project = ApplicabilityProject {
                mode: ProjectMode::default(),
                current_directory: search_path.to_path_buf(),
                starting_directory: starting_path.to_path_buf(),
                paths: path_list,
            };
            assert!(project.current_directory_exists_up_to_root());
        }
        #[test]
        fn current_directory_exists_up_to_root_not_valid_and_not_included() {
            let search_path = Path::new("/some/other/path");
            let path_list = vec![
                FileApplicabilityEntry {
                    path: "/".to_string().into(),
                    entry: FileApplicabilityPath::Included("".to_string()),
                },
                FileApplicabilityEntry {
                    path: "/some".to_string().into(),
                    entry: FileApplicabilityPath::Included("".to_string()),
                },
                FileApplicabilityEntry {
                    path: "/some/other2".to_string().into(),
                    entry: FileApplicabilityPath::Included("".to_string()),
                },
                FileApplicabilityEntry {
                    path: "/some/other/path".to_string().into(),
                    entry: FileApplicabilityPath::Included("".to_string()),
                },
            ];
            let starting_path = Path::new("");
            let project = ApplicabilityProject {
                mode: ProjectMode::default(),
                current_directory: search_path.to_path_buf(),
                starting_directory: starting_path.to_path_buf(),
                paths: path_list,
            };
            assert!(project.current_directory_exists_up_to_root());
        }

        #[test]
        fn current_directory_is_excluded() {
            let search_path = Path::new("/some/other/path");
            let path_list = vec![
                FileApplicabilityEntry {
                    path: "/".to_string().into(),
                    entry: FileApplicabilityPath::Included("some".to_string()),
                },
                FileApplicabilityEntry {
                    path: "/some".to_string().into(),
                    entry: FileApplicabilityPath::Included("other".to_string()),
                },
                FileApplicabilityEntry {
                    path: "/some/other".to_string().into(),
                    entry: FileApplicabilityPath::Excluded("path".to_string()),
                },
            ];
            let starting_path = Path::new("");
            let project = ApplicabilityProject {
                mode: ProjectMode::default(),
                current_directory: search_path.to_path_buf(),
                starting_directory: starting_path.to_path_buf(),
                paths: path_list,
            };
            assert!(project.current_directory_is_excluded());
        }

        #[test]
        fn parent_directory_is_excluded() {
            let search_path = Path::new("/some/other/path");
            let path_list = vec![
                FileApplicabilityEntry {
                    path: "/".to_string().into(),
                    entry: FileApplicabilityPath::Included("some".to_string()),
                },
                FileApplicabilityEntry {
                    path: "/some".to_string().into(),
                    entry: FileApplicabilityPath::Excluded("other".to_string()),
                },
                FileApplicabilityEntry {
                    path: "/some/other".to_string().into(),
                    entry: FileApplicabilityPath::Included("path".to_string()),
                },
            ];
            let starting_path = Path::new("");
            let project = ApplicabilityProject {
                mode: ProjectMode::default(),
                current_directory: search_path.to_path_buf(),
                starting_directory: starting_path.to_path_buf(),
                paths: path_list,
            };
            assert!(project.current_directory_is_excluded());
        }

        #[test]
        fn highest_level_directory_is_excluded() {
            let search_path = Path::new("/some/other/path");
            let path_list = vec![
                FileApplicabilityEntry {
                    path: "/".to_string().into(),
                    entry: FileApplicabilityPath::Excluded("some".to_string()),
                },
                FileApplicabilityEntry {
                    path: "/some".to_string().into(),
                    entry: FileApplicabilityPath::Included("other".to_string()),
                },
                FileApplicabilityEntry {
                    path: "/some/other".to_string().into(),
                    entry: FileApplicabilityPath::Included("path".to_string()),
                },
            ];
            let starting_path = Path::new("");
            let project = ApplicabilityProject {
                mode: ProjectMode::default(),
                current_directory: search_path.to_path_buf(),
                starting_directory: starting_path.to_path_buf(),
                paths: path_list,
            };
            assert!(project.current_directory_is_excluded());
        }
        #[test]
        fn current_directory_is_excluded_glob() {
            let search_path = Path::new("/some/other/path");
            let path_list = vec![
                FileApplicabilityEntry {
                    path: "/".to_string().into(),
                    entry: FileApplicabilityPath::Included("some".to_string()),
                },
                FileApplicabilityEntry {
                    path: "/some".to_string().into(),
                    entry: FileApplicabilityPath::Included("other".to_string()),
                },
                FileApplicabilityEntry {
                    path: "/some/other".to_string().into(),
                    entry: FileApplicabilityPath::Excluded("*".to_string()),
                },
            ];
            let starting_path = Path::new("");
            let project = ApplicabilityProject {
                mode: ProjectMode::default(),
                current_directory: search_path.to_path_buf(),
                starting_directory: starting_path.to_path_buf(),
                paths: path_list,
            };
            assert!(project.current_directory_is_excluded());
        }

        #[test]
        fn parent_directory_is_excluded_glob() {
            let search_path = Path::new("/some/other/path");
            let path_list = vec![
                FileApplicabilityEntry {
                    path: "/".to_string().into(),
                    entry: FileApplicabilityPath::Included("some".to_string()),
                },
                FileApplicabilityEntry {
                    path: "/some".to_string().into(),
                    entry: FileApplicabilityPath::Excluded("other/*".to_string()),
                },
                FileApplicabilityEntry {
                    path: "/some/other".to_string().into(),
                    entry: FileApplicabilityPath::Included("path".to_string()),
                },
            ];
            let starting_path = Path::new("");
            let project = ApplicabilityProject {
                mode: ProjectMode::default(),
                current_directory: search_path.to_path_buf(),
                starting_directory: starting_path.to_path_buf(),
                paths: path_list,
            };
            assert!(project.current_directory_is_excluded());
        }
        #[test]
        fn parent_directory_is_excluded_glob_star_star() {
            let search_path = Path::new("/some/other/path");
            let path_list = vec![
                FileApplicabilityEntry {
                    path: "/".to_string().into(),
                    entry: FileApplicabilityPath::Included("some".to_string()),
                },
                FileApplicabilityEntry {
                    path: "/some".to_string().into(),
                    entry: FileApplicabilityPath::Excluded("**/*".to_string()),
                },
                FileApplicabilityEntry {
                    path: "/some/other".to_string().into(),
                    entry: FileApplicabilityPath::Included("path".to_string()),
                },
            ];
            let starting_path = Path::new("");
            let project = ApplicabilityProject {
                mode: ProjectMode::default(),
                current_directory: search_path.to_path_buf(),
                starting_directory: starting_path.to_path_buf(),
                paths: path_list,
            };
            assert!(project.current_directory_is_excluded());
        }

        #[test]
        fn highest_level_directory_is_excluded_glob() {
            let search_path = Path::new("/some/other/path");
            let path_list = vec![
                FileApplicabilityEntry {
                    path: "/".to_string().into(),
                    entry: FileApplicabilityPath::Excluded("some/*".to_string()),
                },
                FileApplicabilityEntry {
                    path: "/some/other".to_string().into(),
                    entry: FileApplicabilityPath::Included("path".to_string()),
                },
            ];
            let starting_path = Path::new("");
            let project = ApplicabilityProject {
                mode: ProjectMode::default(),
                current_directory: search_path.to_path_buf(),
                starting_directory: starting_path.to_path_buf(),
                paths: path_list,
            };
            assert!(project.current_directory_is_excluded());
        }
        #[test]
        fn current_directory_is_included() {
            let search_path = Path::new("/some/other/path");
            let path_list = vec![
                FileApplicabilityEntry {
                    path: "/".to_string().into(),
                    entry: FileApplicabilityPath::Included("some".to_string()),
                },
                FileApplicabilityEntry {
                    path: "/some".to_string().into(),
                    entry: FileApplicabilityPath::Included("other".to_string()),
                },
                FileApplicabilityEntry {
                    path: "/some/other".to_string().into(),
                    entry: FileApplicabilityPath::Included("path".to_string()),
                },
            ];
            let starting_path = Path::new("");
            let project = ApplicabilityProject {
                mode: ProjectMode::default(),
                current_directory: search_path.to_path_buf(),
                starting_directory: starting_path.to_path_buf(),
                paths: path_list,
            };
            assert!(project.current_directory_is_included());
        }
        #[test]
        fn current_directory_is_not_included() {
            let search_path = Path::new("/some/other/path");
            let path_list = vec![
                FileApplicabilityEntry {
                    path: "/".to_string().into(),
                    entry: FileApplicabilityPath::Included("some".to_string()),
                },
                FileApplicabilityEntry {
                    path: "/some".to_string().into(),
                    entry: FileApplicabilityPath::Included("other".to_string()),
                },
            ];
            let starting_path = Path::new("");
            let project = ApplicabilityProject {
                mode: ProjectMode::default(),
                current_directory: search_path.to_path_buf(),
                starting_directory: starting_path.to_path_buf(),
                paths: path_list,
            };
            assert!(!project.current_directory_is_included());
        }
        #[test]
        fn current_directory_is_explicitly_excluded() {
            let search_path = Path::new("/some/other/path");
            let path_list = vec![
                FileApplicabilityEntry {
                    path: "/".to_string().into(),
                    entry: FileApplicabilityPath::Included("some".to_string()),
                },
                FileApplicabilityEntry {
                    path: "/some".to_string().into(),
                    entry: FileApplicabilityPath::Included("other".to_string()),
                },
                FileApplicabilityEntry {
                    path: "/some/other".to_string().into(),
                    entry: FileApplicabilityPath::Excluded("path".to_string()),
                },
            ];
            let starting_path = Path::new("");
            let project = ApplicabilityProject {
                mode: ProjectMode::default(),
                current_directory: search_path.to_path_buf(),
                starting_directory: starting_path.to_path_buf(),
                paths: path_list,
            };
            assert!(!project.current_directory_is_included());
        }
        #[test]
        fn current_directory_is_not_explicitly_included() {
            let search_path = Path::new("/some/other/path");
            let path_list = vec![
                FileApplicabilityEntry {
                    path: "/".to_string().into(),
                    entry: FileApplicabilityPath::Included("some".to_string()),
                },
                FileApplicabilityEntry {
                    path: "/some".to_string().into(),
                    entry: FileApplicabilityPath::Included("other".to_string()),
                },
                FileApplicabilityEntry {
                    path: "/some/other2".to_string().into(),
                    entry: FileApplicabilityPath::Included("path".to_string()),
                },
            ];
            let starting_path = Path::new("");
            let project = ApplicabilityProject {
                mode: ProjectMode::default(),
                current_directory: search_path.to_path_buf(),
                starting_directory: starting_path.to_path_buf(),
                paths: path_list,
            };
            assert!(!project.current_directory_is_included());
        }
        #[test]
        fn current_directory_is_included_glob() {
            let search_path = Path::new("/some/other/path");
            let path_list = vec![
                FileApplicabilityEntry {
                    path: "/".to_string().into(),
                    entry: FileApplicabilityPath::Included("some".to_string()),
                },
                FileApplicabilityEntry {
                    path: "/some".to_string().into(),
                    entry: FileApplicabilityPath::Included("other".to_string()),
                },
                FileApplicabilityEntry {
                    path: "/some/other".to_string().into(),
                    entry: FileApplicabilityPath::Included("*".to_string()),
                },
            ];
            let starting_path = Path::new("");
            let project = ApplicabilityProject {
                mode: ProjectMode::default(),
                current_directory: search_path.to_path_buf(),
                starting_directory: starting_path.to_path_buf(),
                paths: path_list,
            };
            assert!(project.current_directory_is_included());
        }
        #[test]
        fn parent_directory_is_included_glob() {
            let search_path = Path::new("/some/other/path");
            let path_list = vec![
                FileApplicabilityEntry {
                    path: "/".to_string().into(),
                    entry: FileApplicabilityPath::Included("some".to_string()),
                },
                FileApplicabilityEntry {
                    path: "/some".to_string().into(),
                    entry: FileApplicabilityPath::Included("other/*".to_string()),
                },
            ];
            let starting_path = Path::new("");
            let project = ApplicabilityProject {
                mode: ProjectMode::default(),
                current_directory: search_path.to_path_buf(),
                starting_directory: starting_path.to_path_buf(),
                paths: path_list,
            };
            assert!(project.current_directory_is_included());
        }
        #[test]
        fn top_directory_is_included_glob() {
            let search_path = Path::new("/some/other/path");
            let path_list = vec![FileApplicabilityEntry {
                path: "/".to_string().into(),
                entry: FileApplicabilityPath::Included("some/*".to_string()),
            }];
            let starting_path = Path::new("");
            let project = ApplicabilityProject {
                mode: ProjectMode::default(),
                current_directory: search_path.to_path_buf(),
                starting_directory: starting_path.to_path_buf(),
                paths: path_list,
            };
            assert!(!project.current_directory_is_included());
        }
        #[test]
        fn top_directory_is_included_glob_but_not_parent() {
            let search_path = Path::new("/some/other/path");
            let path_list = vec![
                FileApplicabilityEntry {
                    path: "/".to_string().into(),
                    entry: FileApplicabilityPath::Included("some/*".to_string()),
                },
                FileApplicabilityEntry {
                    path: "/some".to_string().into(),
                    entry: FileApplicabilityPath::Excluded("other".to_string()),
                },
            ];
            let starting_path = Path::new("");
            let project = ApplicabilityProject {
                mode: ProjectMode::default(),
                current_directory: search_path.to_path_buf(),
                starting_directory: starting_path.to_path_buf(),
                paths: path_list,
            };
            assert!(!project.current_directory_is_included());
        }
        #[test]
        fn top_directory_is_included_glob_star_star() {
            let search_path = Path::new("/some/other/path");
            let path_list = vec![FileApplicabilityEntry {
                path: "/".to_string().into(),
                entry: FileApplicabilityPath::Included("some/**/*".to_string()),
            }];
            let starting_path = Path::new("");
            let project = ApplicabilityProject {
                mode: ProjectMode::default(),
                current_directory: search_path.to_path_buf(),
                starting_directory: starting_path.to_path_buf(),
                paths: path_list,
            };
            assert!(project.current_directory_is_included());
        }
    }
}
