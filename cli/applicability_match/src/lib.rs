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
use applicability::applic_tag::ApplicabilityTag;
use applicability_parser_types::{
    applic_tokens::MatchToken,
    applicability_parser_syntax_tag::{ApplicabilitySyntaxTag, ApplicabilitySyntaxTagNot},
};

pub trait MatchApplicability<T> {
    fn match_applicability(
        &self,
        match_list: &[T],
        config_name: &str,
        parent_group: Option<&str>,
        child_configurations: Option<&[&str]>,
    ) -> bool;
}
impl MatchApplicability<ApplicabilityTag> for ApplicabilitySyntaxTag {
    fn match_applicability(
        &self,
        match_list: &[ApplicabilityTag],
        config_name: &str,
        parent_group: Option<&str>,
        child_configurations: Option<&[&str]>,
    ) -> bool {
        let mut found = false;
        let tags = self.0.to_vec();
        for applic_tag in tags {
            found = applic_tag.match_token(
                match_list,
                config_name,
                parent_group,
                child_configurations,
                found,
                &self.2,
            );
        }
        found
    }
}

impl MatchApplicability<ApplicabilityTag> for ApplicabilitySyntaxTagNot {
    fn match_applicability(
        &self,
        match_list: &[ApplicabilityTag],
        config_name: &str,
        parent_group: Option<&str>,
        child_configurations: Option<&[&str]>,
    ) -> bool {
        let mut found = false;
        let tags = self.0.to_vec();
        for applic_tag in tags {
            found = applic_tag.match_token(
                match_list,
                config_name,
                parent_group,
                child_configurations,
                found,
                &self.2,
            );
        }
        !found
    }
}
