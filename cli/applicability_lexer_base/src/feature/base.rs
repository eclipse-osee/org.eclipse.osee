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
use nom::{
    AsChar, Compare, FindSubstring, Input, Parser,
    bytes::{tag, take_until},
    error::ParseError,
};

pub trait FeatureBase {
    fn feature_base<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        tag(self.feature_base_tag())
    }
    fn feature_base_multi_line<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        tag(self.feature_base_tag_nl())
            .or(tag(self.feature_base_tag_cr()))
            .or(tag(self.feature_base_tag_t()))
            .or(tag(self.feature_base_tag_s()))
    }
    fn feature_base_tag<'x>(&self) -> &'x str {
        "Feature"
    }
    fn feature_base_tag_nl<'x>(&self) -> &'x str {
        "\nFeature"
    }
    fn feature_base_tag_cr<'x>(&self) -> &'x str {
        "\r\nFeature"
    }
    fn feature_base_tag_t<'x>(&self) -> &'x str {
        "\tFeature"
    }
    fn feature_base_tag_s<'x>(&self) -> &'x str {
        " Feature"
    }
    fn take_until_feature_base<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str> + FindSubstring<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        take_until(self.feature_base_tag())
    }
}
