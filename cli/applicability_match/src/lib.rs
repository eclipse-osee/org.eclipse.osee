use std::fmt::Debug;

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
    applic_tokens::{ApplicTokens, MatchToken},
    applicability_parser_syntax_tag::{ApplicabilitySyntaxTag, ApplicabilitySyntaxTagNot},
};

pub trait MatchApplicability<T> {
    type TagType;
    fn match_applicability(
        &self,
        match_list: &[T],
        config_name: &Self::TagType,
        parent_group: Option<&Self::TagType>,
        child_configurations: Option<&[Self::TagType]>,
    ) -> bool;
}
// impl<X1> MatchApplicability<ApplicabilityTag<X1>> for ApplicabilitySyntaxTag<X1>
// where
//     X1: PartialEq + Debug + Clone,
//     ApplicTokens<X1>: MatchToken<ApplicabilityTag<X1>, TagType = X1>,
//     // <ApplicTokens<X1> as MatchToken<ApplicabilityTag<X1>>>::TagType: X1,
// {
//     type TagType = X1;
//     fn match_applicability(
//         &self,
//         match_list: &[ApplicabilityTag<X1>],
//         config_name: Self::TagType,
//         parent_group: Option<Self::TagType>,
//         child_configurations: Option<&[Self::TagType]>,
//     ) -> bool {
//         let mut found = false;
//         let tags = self.0.to_vec();
//         for applic_tag in tags {
//             found = applic_tag.match_token(
//                 match_list,
//                 config_name.clone(),
//                 parent_group.clone(),
//                 child_configurations,
//                 found,
//                 &self.2,
//             );
//         }
//         found
//     }
// }

// impl<X1> MatchApplicability<ApplicabilityTag<X1>> for ApplicabilitySyntaxTagNot<X1>
// where
//     X1: PartialEq + Debug + Clone,
//     ApplicTokens<X1>: MatchToken<ApplicabilityTag<X1>, TagType = X1>,
// {
//     type TagType = X1;
//     fn match_applicability(
//         &self,
//         match_list: &[ApplicabilityTag<X1>],
//         config_name: Self::TagType,
//         parent_group: Option<Self::TagType>,
//         child_configurations: Option<&[Self::TagType]>,
//     ) -> bool {
//         let mut found = false;
//         let tags = self.0.to_vec();
//         for applic_tag in tags {
//             found = applic_tag.match_token(
//                 match_list,
//                 config_name.clone(),
//                 parent_group.clone(),
//                 child_configurations,
//                 found,
//                 &self.2,
//             );
//         }
//         !found
//     }
// }
