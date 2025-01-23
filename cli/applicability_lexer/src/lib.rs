use nom::{bytes::complete::tag, combinator::value, IResult, Parser};

mod config_def;
mod config_group_def;
mod feature_def;

/**
*
* the alts in the comment need to be many0'd or take_until'd
* StartCommentSingleLine should take until EndCommentSingleLine
* StartCommentMultiLine should take until EndCommentMultiLine
* SingleLineCommentCharacter should take until NewLine
* Theoretical prototype:

    StartParenParse = many_till(Space,StartParen)
    FeatureParse = tuple((Feature, StartParenParse, many_till(alt((Space,NewLine,Not,And,Or,Tag(String))),EndParen)))
    FeatureNotParse = tuple((FeatureNot, StartParenParse, many_till(alt((Space,NewLine,Not,And,Or,Tag(String))),EndParen)))
    FeatureCaseParse = tuple((FeatureCase, StartParenParse, many_till(alt((Space,NewLine,Not,And,Or,Tag(String))),EndParen)))
    ConfigurationParse = tuple((Configuration, StartParenParse, many_till(alt((Space,NewLine,Not,And,Or,Tag(String))),EndParen)))
    ConfigurationNotParse = tuple((ConfigurationNot, StartParenParse, many_till(alt((Space,NewLine,Not,And,Or,Tag(String))),EndParen)))
    ConfigurationCaseParse = tuple((ConfigurationCase, StartParenParse, many_till(alt((Space,NewLine,Not,And,Or,Tag(String))),EndParen)))
    ConfigurationGroupParse = tuple((ConfigurationGroup, StartParenParse, many_till(alt((Space,NewLine,Not,And,Or,Tag(String))),EndParen)))
    ConfigurationGroupNotParse = tuple((ConfigurationGroupNot, StartParenParse, many_till(alt((Space,NewLine,Not,And,Or,Tag(String))),EndParen)))
    ConfigurationGroupCaseParse = tuple((ConfigurationGroupCase, StartParenParse, many_till(alt((Space,NewLine,Not,And,Or,Tag(String))),EndParen)))
    SubstitutionParse = tuple((Substitution, StartParenParse, many_till(alt((Space,NewLine,Not,And,Or,Tag(String))),EndParen)))
* results = alt((
* tuple((StartCommentSingleLine,
many_till(
* alt((
   FeatureParse,
   FeatureNotParse,
   FeatureSwitch,
   FeatureCaseParse,
   FeatureElse,
   EndFeature,
   ConfigurationParse,
   ConfigurationNotParse,
   ConfigurationSwitch,
   ConfigurationCaseParse,
   ConfigurationElse,
   EndConfiguration,
   ConfigurationGroupParse,
   ConfigurationGroupNotParse,
   ConfigurationGroupSwitch,
   ConfigurationGroupCaseParse,
   ConfigurationGroupElse,
   EndConfigurationGroup,
   SubstitutionParse,
   Space,
   NewLine,
* ))
, EndCommentSingleLine)
)),
* tuple((
* StartCommentMultiLine,
many_till(
* alt((
* MultilineCommentCharacter,
   FeatureParse,
   FeatureNotParse,
   FeatureSwitch,
   FeatureCaseParse,
   FeatureElse,
   EndFeature,
   ConfigurationParse,
   ConfigurationNotParse,
   ConfigurationSwitch,
   ConfigurationCaseParse,
   ConfigurationElse,
   EndConfiguration,
   ConfigurationGroupParse,
   ConfigurationGroupNotParse,
   ConfigurationGroupSwitch,
   ConfigurationGroupCaseParse,
   ConfigurationGroupElse,
   EndConfigurationGroup,
   SubstitutionParse,
   Space,
   NewLine,
* )), EndCommentMultiLine)
)),
* tuple((
* SingleLineCommentCharacter,
many_till(
* alt((
   FeatureParse,
   FeatureNotParse,
   FeatureSwitch,
   FeatureCaseParse,
   FeatureElse,
   EndFeature,
   ConfigurationParse,
   ConfigurationNotParse,
   ConfigurationSwitch,
   ConfigurationCaseParse,
   ConfigurationElse,
   EndConfiguration,
   ConfigurationGroupParse,
   ConfigurationGroupNotParse,
   ConfigurationGroupSwitch,
   ConfigurationGroupCaseParse,
   ConfigurationGroupElse,
   EndConfigurationGroup,
   SubstitutionParse,
   Space,
   * ))
   , NewLine)
* ))
* Nothing,
* Illegal,
* Identity,
* Eof,
* ))
*/

#[derive(Debug, Clone, PartialEq, Eq, Default)]
pub enum LexerToken {
    #[default]
    Nothing,
    Illegal,
    Identity,
    Text(String),
    Eof,
    StartCommentSingleLine,
    StartCommentMultiLine,
    SingleLineCommentCharacter,
    // the following should only be tokenized inside a comment(i.e. not normal Text(String))
    EndCommentSingleLine,
    EndCommentMultiLine,
    MultilineCommentCharacter,
    Feature,
    FeatureNot,
    FeatureSwitch,
    FeatureCase,
    FeatureElse,
    EndFeature,
    Configuration,
    ConfigurationNot,
    ConfigurationSwitch,
    ConfigurationCase,
    ConfigurationElse,
    EndConfiguration,
    ConfigurationGroup,
    ConfigurationGroupNot,
    ConfigurationGroupSwitch,
    ConfigurationGroupCase,
    ConfigurationGroupElse,
    EndConfigurationGroup,
    Substitution,
    Space,
    NewLine,
    //the following should only be tokenized following one of the Feature|Configuration|ConfigurationGroup Base|Not|Switch|Case
    StartParen,
    EndParen,
    // the following should only be tokenized following a StartParen and preceding an EndParen
    Not,
    And,
    Or,
    Tag(String),
}

// type ParseFn<I, O, E> = impl Parser<I, O, E>;
pub struct LexerConfig<
    I1,
    I2,
    I3,
    I4,
    I5,
    I6,
    I7,
    I8,
    I9,
    I10,
    I11,
    I12,
    I13,
    I14,
    I15,
    I16,
    I17,
    I18,
    O1,
    O2,
    O3,
    O4,
    O5,
    O6,
    O7,
    O8,
    O9,
    O10,
    O11,
    O12,
    O13,
    O14,
    O15,
    O16,
    O17,
    O18,
    E1,
    E2,
    E3,
    E4,
    E5,
    E6,
    E7,
    E8,
    E9,
    E10,
    E11,
    E12,
    E13,
    E14,
    E15,
    E16,
    E17,
    E18,
> {
    feature: ApplicabilityLexerConfig<
        I1,
        I2,
        I3,
        I4,
        I5,
        I6,
        O1,
        O2,
        O3,
        O4,
        O5,
        O6,
        E1,
        E2,
        E3,
        E4,
        E5,
        E6,
    >,
    configuration: ApplicabilityLexerConfig<
        I7,
        I8,
        I9,
        I10,
        I11,
        I12,
        O7,
        O8,
        O9,
        O10,
        O11,
        O12,
        E7,
        E8,
        E9,
        E10,
        E11,
        E12,
    >,
    configuration_group: ApplicabilityLexerConfig<
        I13,
        I14,
        I15,
        I16,
        I17,
        I18,
        O13,
        O14,
        O15,
        O16,
        O17,
        O18,
        E13,
        E14,
        E15,
        E16,
        E17,
        E18,
    >,
}

pub struct ApplicabilityLexerConfig<
    I1,
    I2,
    I3,
    I4,
    I5,
    I6,
    O1,
    O2,
    O3,
    O4,
    O5,
    O6,
    E1,
    E2,
    E3,
    E4,
    E5,
    E6,
> {
    base: Box<dyn Parser<I1, O1, E1>>,
    not: Box<dyn Parser<I2, O2, E2>>,
    switch: Box<dyn Parser<I3, O3, E3>>,
    case: Box<dyn Parser<I4, O4, E4>>,
    applic_else: Box<dyn Parser<I5, O5, E5>>,
    end: Box<dyn Parser<I6, O6, E6>>,
}

fn lex_start_comment<'a>(
    custom_start_comment_syntax: &'a str,
) -> impl FnMut(&'a str) -> IResult<&'a str, LexerToken> {
    value(
        LexerToken::StartCommentSingleLine,
        tag(custom_start_comment_syntax),
    )
}
fn lex_end_comment<'a>(
    custom_end_comment_syntax: &'a str,
) -> impl FnMut(&'a str) -> IResult<&'a str, LexerToken> {
    value(
        LexerToken::EndCommentSingleLine,
        tag(custom_end_comment_syntax),
    )
}

#[cfg(test)]
mod lex_comment_test {

    use nom::{
        error::{Error, ErrorKind, ParseError},
        Err,
    };

    use super::{lex_end_comment, lex_start_comment, LexerToken};

    #[test]
    fn test_start_comment() {
        let mut parser = lex_start_comment("``");
        assert_eq!(parser("``"), Ok(("", LexerToken::StartCommentSingleLine)));
        assert_eq!(
            parser("Not the word Feature"),
            Err(Err::Error(Error::from_error_kind(
                "Not the word Feature",
                ErrorKind::Tag
            )))
        );
    }

    #[test]
    fn test_end_comment() {
        let mut parser = lex_end_comment("``");
        assert_eq!(parser("``"), Ok(("", LexerToken::EndCommentSingleLine)));
        assert_eq!(
            parser("Not the word Feature"),
            Err(Err::Error(Error::from_error_kind(
                "Not the word Feature",
                ErrorKind::Tag
            )))
        );
    }
}
// pub fn lex_applicability<'a>(
//     input: &'a str,
//     custom_start_comment_syntax: &'a str,
//     custom_end_comment_syntax: &'a str,
// ) -> IResult<&'a str, Vec<LexerTokens>> {
//     let mut parser = map(
//         permutation((
//             many0(next(custom_start_comment_syntax, custom_end_comment_syntax)),
//             get_remaining_text,
//         )),
//         |(mut parsed_results, remaining)| {
//             parsed_results.push(remaining);
//             parsed_results
//         },
//     );
//     parser(input)
// }
