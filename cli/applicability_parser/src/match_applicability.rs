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
use crate::applicability_parser_syntax_tag::{
    ApplicabilitySyntaxTag, ApplicabilitySyntaxTagAnd, ApplicabilitySyntaxTagNot,
    ApplicabilitySyntaxTagNotAnd, ApplicabilitySyntaxTagNotOr, ApplicabilitySyntaxTagOr,
};
use applicability::applic_tag::{ApplicabilityTag, ApplicabilityTagTypes};

pub trait MatchApplicability<T> {
    fn match_applicability(&self, match_list: &[T], config_name: &str) -> bool;
}
impl MatchApplicability<ApplicabilityTag> for ApplicabilitySyntaxTag {
    fn match_applicability(&self, match_list: &[ApplicabilityTag], config_name: &str) -> bool {
        let mut found = false;
        for applic_tag in match_list {
            if self.0.contains(applic_tag) {
                found = true;
            };
        }
        if self.0.iter().any(|tag| tag == config_name) {
            found = true;
        }
        if !found && self.2 == ApplicabilityTagTypes::Feature {
            //look through match_list to see if the feature exists, if it is a feature,
            //and let the user know that they have a missing feature in their text file.
            let untagged_list = match_list
                .iter()
                .map(|match_text| match_text.tag.as_str())
                .collect::<Vec<&str>>();
            let remaining_features = self
                .0
                .iter()
                .map(|tag| tag.tag.as_str())
                .collect::<Vec<&str>>();
            let missing_features = remaining_features
                .iter()
                .filter(|&tag| !untagged_list.contains(tag))
                .cloned()
                .collect::<Vec<&str>>();
            for missing in missing_features {
                println!(
                    "The Feature [{:#}] is missing from {:#?}. Could not sanitize properly.",
                    missing, config_name
                )
            }
        }
        found
    }
}

impl MatchApplicability<ApplicabilityTag> for ApplicabilitySyntaxTagAnd {
    fn match_applicability(&self, match_list: &[ApplicabilityTag], config_name: &str) -> bool {
        let mut count = self.0.len();
        for tag in &self.0 {
            if match_list.contains(tag) {
                count -= 1;
            }
        }
        if self.0.iter().any(|tag| tag == config_name) {
            count -= 1;
        }
        if count > 0 && self.2 == ApplicabilityTagTypes::Feature {
            //look through match_list to see if the feature exists, if it is a feature,
            //and let the user know that they have a missing feature in their text file.
            let untagged_list = match_list
                .iter()
                .map(|match_text| match_text.tag.as_str())
                .collect::<Vec<&str>>();
            let remaining_features = self
                .0
                .iter()
                .map(|tag| tag.tag.as_str())
                .collect::<Vec<&str>>();
            let missing_features = remaining_features
                .iter()
                .filter(|&tag| !untagged_list.contains(tag))
                .cloned()
                .collect::<Vec<&str>>();
            for missing in missing_features {
                println!(
                    "The Feature [{:#}] is missing from {:#?}. Could not sanitize properly.",
                    missing, config_name
                )
            }
        }
        count == 0
    }
}
impl MatchApplicability<ApplicabilityTag> for ApplicabilitySyntaxTagOr {
    fn match_applicability(&self, match_list: &[ApplicabilityTag], config_name: &str) -> bool {
        let mut found = false;
        for applic_tag in match_list {
            if self.0.contains(applic_tag) {
                found = true;
            };
        }
        if self.0.iter().any(|tag| tag == config_name) {
            found = true;
        }
        if !found && self.2 == ApplicabilityTagTypes::Feature {
            //look through match_list to see if the feature exists, if it is a feature,
            //and let the user know that they have a missing feature in their text file.
            let untagged_list = match_list
                .iter()
                .map(|match_text| match_text.tag.as_str())
                .collect::<Vec<&str>>();
            let remaining_features = self
                .0
                .iter()
                .map(|tag| tag.tag.as_str())
                .collect::<Vec<&str>>();
            let missing_features = remaining_features
                .iter()
                .filter(|&tag| !untagged_list.contains(tag))
                .cloned()
                .collect::<Vec<&str>>();
            for missing in missing_features {
                println!(
                    "The Feature [{:#}] is missing from {:#?}. Could not sanitize properly.",
                    missing, config_name
                )
            }
        }
        found
    }
}
impl MatchApplicability<ApplicabilityTag> for ApplicabilitySyntaxTagNot {
    fn match_applicability(&self, match_list: &[ApplicabilityTag], config_name: &str) -> bool {
        let mut found = false;
        for applic_tag in match_list {
            if self.0.contains(applic_tag) {
                found = true;
            };
        }
        if self.0.iter().any(|tag| tag == config_name) {
            found = true;
        }
        if !found && self.2 == ApplicabilityTagTypes::Feature {
            //look through match_list to see if the feature exists, if it is a feature,
            //and let the user know that they have a missing feature in their text file.
            let untagged_list = match_list
                .iter()
                .map(|match_text| match_text.tag.as_str())
                .collect::<Vec<&str>>();
            let remaining_features = self
                .0
                .iter()
                .map(|tag| tag.tag.as_str())
                .collect::<Vec<&str>>();
            let missing_features = remaining_features
                .iter()
                .filter(|&tag| !untagged_list.contains(tag))
                .cloned()
                .collect::<Vec<&str>>();
            for missing in missing_features {
                println!(
                    "The Feature Not [{:#}] is missing from {:#?}. Could not sanitize properly.",
                    missing, config_name
                )
            }
        }
        found
    }
}
impl MatchApplicability<ApplicabilityTag> for ApplicabilitySyntaxTagNotAnd {
    fn match_applicability(&self, match_list: &[ApplicabilityTag], config_name: &str) -> bool {
        let mut count = self.0.len();
        for tag in &self.0 {
            if match_list.contains(tag) {
                count = match count.checked_sub(1) {
                    Some(i) => i,
                    None => count,
                };
            }
        }
        if self.0.iter().any(|tag| tag == config_name) {
            count = match count.checked_sub(1) {
                Some(i) => i,
                None => count,
            };
        }
        if count > 0 && self.2 == ApplicabilityTagTypes::Feature {
            //look through match_list to see if the feature exists, if it is a feature,
            //and let the user know that they have a missing feature in their text file.
            let untagged_list = match_list
                .iter()
                .map(|match_text| match_text.tag.as_str())
                .collect::<Vec<&str>>();
            let remaining_features = self
                .0
                .iter()
                .map(|tag| tag.tag.as_str())
                .collect::<Vec<&str>>();
            let missing_features = remaining_features
                .iter()
                .filter(|&tag| !untagged_list.contains(tag))
                .cloned()
                .collect::<Vec<&str>>();
            for missing in missing_features {
                println!(
                    "The Feature Not [{:#}] is missing from {:#?}. Could not sanitize properly.",
                    missing, config_name
                )
            }
        }
        count == 0
    }
}
impl MatchApplicability<ApplicabilityTag> for ApplicabilitySyntaxTagNotOr {
    fn match_applicability(&self, match_list: &[ApplicabilityTag], config_name: &str) -> bool {
        let mut found = false;
        for applic_tag in match_list {
            if self.0.contains(applic_tag) {
                found = true;
            };
        }
        if self.0.iter().any(|tag| tag == config_name) {
            found = true;
        }
        if !found && self.2 == ApplicabilityTagTypes::Feature {
            //look through match_list to see if the feature exists, if it is a feature,
            //and let the user know that they have a missing feature in their text file.
            let untagged_list = match_list
                .iter()
                .map(|match_text| match_text.tag.as_str())
                .collect::<Vec<&str>>();
            let remaining_features = self
                .0
                .iter()
                .map(|tag| tag.tag.as_str())
                .collect::<Vec<&str>>();
            let missing_features = remaining_features
                .iter()
                .filter(|&tag| !untagged_list.contains(tag))
                .cloned()
                .collect::<Vec<&str>>();
            for missing in missing_features {
                println!(
                    "The Feature Not [{:#}] is missing from {:#?}. Could not sanitize properly.",
                    missing, config_name
                )
            }
        }
        found
    }
}
