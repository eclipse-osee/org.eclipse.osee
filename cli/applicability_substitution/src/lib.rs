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
use applicability::{applic_tag::ApplicabilityTagTypes, substitution::Substitution};
use applicability_parser_types::{
    applic_tokens::{GetApplicabilityTag, MatchToken},
    applicability_parser_syntax_tag::{ApplicabilityParserSyntaxTag, SubstitutionSyntaxTag},
};

pub trait SubstituteApplicability {
    fn substitute(&self, substitutes: &[Substitution]) -> ApplicabilityParserSyntaxTag;
}

impl SubstituteApplicability for ApplicabilityParserSyntaxTag {
    fn substitute(&self, substitutes: &[Substitution]) -> ApplicabilityParserSyntaxTag {
        match self {
            ApplicabilityParserSyntaxTag::Text(_)
            | ApplicabilityParserSyntaxTag::Tag(_)
            | ApplicabilityParserSyntaxTag::TagNot(_) => self.clone(),
            ApplicabilityParserSyntaxTag::Substitution(t) => t.substitute(substitutes),
            //intentionally not implemented, future growth if needed, these paths don't exist yet.
            ApplicabilityParserSyntaxTag::SubstitutionNot(_) => todo!(),
        }
    }
}
impl SubstituteApplicability for SubstitutionSyntaxTag {
    fn substitute(&self, substitutes: &[Substitution]) -> ApplicabilityParserSyntaxTag {
        ApplicabilityParserSyntaxTag::Text(
            self.iter()
                .filter(|token| {
                    token.match_token(
                        substitutes,
                        "",
                        None,
                        None,
                        false,
                        &ApplicabilityTagTypes::Configuration,
                    )
                })
                .cloned()
                .flat_map(|token| {
                    //TODO maybe we want to add a trait to have some & | ! logic instead of mapping directly to substitute.substitute
                    substitutes
                        .iter()
                        .filter(|&substitute| substitute.match_text == token.get_tag())
                        .cloned()
                        .map(|substitute| substitute.substitute)
                        .collect::<Vec<String>>()
                })
                .collect::<Vec<String>>()
                .join(""),
        )
    }
}
