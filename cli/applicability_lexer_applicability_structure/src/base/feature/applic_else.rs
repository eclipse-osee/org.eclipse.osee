use nom::{error::ParseError, AsChar, Compare, Input, Parser};

use applicability_lexer_base::{
    applicability_structure::LexerToken,
    feature::applic_else::FeatureElse,
    utils::locatable::{position, Locatable},
    position::Position,
};

pub trait LexFeatureElse {
    fn lex_feature_else<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>;
    fn lex_feature_else_tag<'x>(&self) -> &'x str;
}

impl<T> LexFeatureElse for T
where
    T: FeatureElse,
{
    fn lex_feature_else<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        position().and(self.feature_else()).and(position()).map(
            |((start, _), end): ((Position, _), Position)| LexerToken::FeatureElse((start, end)),
        )
    }

    fn lex_feature_else_tag<'x>(&self) -> &'x str {
        self.feature_else_tag()
    }
}
