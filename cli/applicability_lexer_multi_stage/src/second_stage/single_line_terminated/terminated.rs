use nom::{error::ParseError, multi::many0, AsChar, Compare, FindSubstring, Input, Parser};

use crate::{
    base::comment::single_line::{EndCommentSingleLine, StartCommentSingleLine},
    second_stage::token::LexerToken,
};

use super::{
    config::tag::ConfigTagSingleLineTerminated,
    config_group::tag::ConfigGroupTagSingleLineTerminated,
    feature::tag::FeatureTagSingleLineTerminated, utils::loose_text::LooseTextTerminated,
};

pub trait SingleLineTerminated {
    fn get_single_line_terminated<I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<LexerToken<String>>, Error = E>
    where
        I: Input + Into<String> + for<'x> FindSubstring<&'x str> + for<'x> Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>;
}
impl<T> SingleLineTerminated for T
where
    T: StartCommentSingleLine
        + EndCommentSingleLine
        + FeatureTagSingleLineTerminated
        + ConfigTagSingleLineTerminated
        + ConfigGroupTagSingleLineTerminated
        + LooseTextTerminated,
{
    fn get_single_line_terminated<I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<LexerToken<String>>, Error = E>
    where
        I: Input + Into<String> + for<'x> FindSubstring<&'x str> + for<'x> Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        let start = self
            .start_comment_single_line()
            .map(|_| LexerToken::StartCommentSingleLine);

        let applic_tag = self
            .feature_tag_terminated()
            .or(self.config_tag_terminated())
            .or(self.config_group_tag_terminated());
        let inner_select = applic_tag.or(self.loose_text_terminated());
        let inner = many0(inner_select)
            .map(|x| x.into_iter().flatten().collect::<Vec<LexerToken<String>>>());
        let end = self
            .end_comment_single_line()
            .map(|_| LexerToken::EndCommentSingleLine);
        let parse_comment = start.and(inner).and(end).map(|((start, tag), end)| {
            let mut results = vec![start];
            results.extend(tag.into_iter());
            results.push(end);
            results
        });
        parse_comment
    }
}
