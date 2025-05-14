use nom::{error::ParseError, AsChar, Compare, Input, Parser};

use applicability_lexer_base::{
    applicability_structure::LexerToken,
    feature::else_if::FeatureElseIf,
    utils::locatable::{position, Locatable},
    position::Position
};

pub trait LexFeatureElseIf {
    fn lex_feature_else_if<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>;
    fn lex_feature_else_if_tag<'x>(&self) -> &'x str;
}

impl<T> LexFeatureElseIf for T
where
    T: FeatureElseIf,
{
    fn lex_feature_else_if<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        position().and(self.feature_else_if()).and(position()).map(
            |((start, _), end): ((Position, _), Position)| LexerToken::FeatureElseIf((start, end)),
        )
    }

    fn lex_feature_else_if_tag<'x>(&self) -> &'x str {
        self.feature_else_if_tag()
    }
}
