use std::marker::PhantomData;

use applicability_document_schema::{StringOrByteArray, get_doc_config, get_schema};
use applicability_lexer_applicability_structure_multi_line::MultiLine;
use applicability_lexer_applicability_structure_single_line_non_terminated::SingleLineNonTerminated;
use applicability_lexer_applicability_structure_single_line_terminated::SingleLineTerminated;
use applicability_lexer_base::{
    applicability_structure::LexerToken,
    comment::code_block::{EndCodeBlock, StartCodeBlock},
    position::{Position, TokenPosition},
    utils::{locatable::position, take_first::take_until_first3},
};
use applicability_lexer_document_structure::document_structure_parser::IdentifyComments;
use nom::{
    AsBytes, AsChar, Compare, FindSubstring, Input, Offset, Parser,
    bytes::{tag, take_until},
    combinator::opt,
    error::ParseError,
};

use applicability_lexer_applicability_structure_base::{
    delimiters::space::LexSpace,
    line_terminations::{carriage_return::LexCarriageReturn, new_line::LexNewLine},
};
use nom_locate::LocatedSpan;

use crate::tokenize_other::tokenize_others_parser;

pub trait CodeBlock {
    fn get_code_block<'a, 'b, I, E>(
        &self,
    ) -> impl Parser<
        LocatedSpan<I, TokenPosition>,
        Output = Vec<LexerToken<LocatedSpan<I, TokenPosition>>>,
        Error = E,
    >
    where
        I: Input
            + for<'x> FindSubstring<&'x str>
            + for<'x> Compare<&'x str>
            + Send
            + Sync
            + AsBytes
            + Offset
            + Default
            + 'a,
        I::Item: AsChar,
        E: ParseError<LocatedSpan<I, TokenPosition>>,
        'a: 'b,
        StringOrByteArray<'b>: From<I>;
}
impl<T> CodeBlock for T
where
    T: StartCodeBlock
        + EndCodeBlock
        + MultiLine
        + SingleLineNonTerminated
        + SingleLineTerminated
        + LexNewLine
        + LexSpace
        + LexCarriageReturn
        + IdentifyComments,
{
    fn get_code_block<'a, 'b, I, E>(
        &self,
    ) -> impl Parser<
        LocatedSpan<I, TokenPosition>,
        Output = Vec<LexerToken<LocatedSpan<I, TokenPosition>>>,
        Error = E,
    >
    where
        I: Input
            + for<'x> FindSubstring<&'x str>
            + for<'x> Compare<&'x str>
            + Send
            + Sync
            + AsBytes
            + Offset
            + Default
            + 'a,
        I::Item: AsChar,
        E: ParseError<LocatedSpan<I, TokenPosition>>,
        'a: 'b,
        StringOrByteArray<'b>: From<I>,
    {
        CodeBlockParser {
            config: self,
            _ph: PhantomData,
        }
    }
}

struct CodeBlockParser<'a, T, E> {
    config: &'a T,
    _ph: PhantomData<E>,
}

type LanguageTypeTagType<I> = (
    (
        (
            (
                (
                    LexerToken<LocatedSpan<I, TokenPosition>>,
                    Option<LocatedSpan<I, TokenPosition>>,
                ),
                LocatedSpan<I, TokenPosition>,
            ),
            Position,
        ),
        LocatedSpan<I, TokenPosition>,
    ),
    Position,
);

#[allow(clippy::extra_unused_lifetimes)]
impl<'a, 'b, I, T, E> Parser<LocatedSpan<I, TokenPosition>> for CodeBlockParser<'_, T, E>
where
    I: Input
        + for<'x> FindSubstring<&'x str>
        + for<'x> Compare<&'x str>
        + Send
        + Sync
        + AsBytes
        + Offset
        + Default
        + 'a,
    I::Item: AsChar,
    E: ParseError<LocatedSpan<I, TokenPosition>>,
    T: StartCodeBlock
        + EndCodeBlock
        + MultiLine
        + SingleLineNonTerminated
        + SingleLineTerminated
        + LexNewLine
        + LexSpace
        + LexCarriageReturn
        + IdentifyComments,
    'a: 'b,
    StringOrByteArray<'b>: From<I>,
{
    type Output = Vec<LexerToken<LocatedSpan<I, TokenPosition>>>;

    type Error = E;

    fn process<OM: nom::OutputMode>(
        &mut self,
        input: LocatedSpan<I, TokenPosition>,
    ) -> nom::PResult<OM, LocatedSpan<I, TokenPosition>, Self::Output, Self::Error> {
        //this will take the three backticks ```
        // let start_code_block = input.take_from(self.config.start_code_block_tag().len());
        let start = position()
            .and(tag(self.config.start_code_block_tag()))
            .and(position())
            .map(|((start, text), end)| LexerToken::Text(text, (start, end)));
        // this will get all text up to the newline, space, or carriage return(indicating the language)
        let language_type_tag = start
            .and(opt(take_until_first3(
                self.config.lex_space_tag(),
                self.config.lex_carriage_return_tag(),
                self.config.lex_new_line_tag(),
            )))
            .and(take_until(self.config.end_code_block_tag()))
            .and(position())
            .and(tag(self.config.end_code_block_tag()))
            .and(position());

        language_type_tag
            .map(
                |(
                    ((((start_tag, tag), remaining), position_before_end_code), end_code),
                    position_after_end_code,
                ): LanguageTypeTagType<I>| {
                    let end_start_offset = position_before_end_code.0;
                    let end_start_line = position_before_end_code.1;
                    let end_end_offset = position_after_end_code.0;
                    let end_end_line = position_after_end_code.1;
                    let end_insert = LexerToken::Text(
                        end_code,
                        (
                            (end_start_offset, end_start_line),
                            (end_end_offset, end_end_line),
                        ),
                    );
                    match tag {
                        Some(t) => {
                            let config = get_doc_config(get_schema(
                                Into::<StringOrByteArray>::into(t.fragment().clone()),
                            ));
                            let tag_offset = t.location_offset();
                            let tag_line = t.location_line();
                            let remaining_offset = remaining.location_offset();
                            let remaining_line = remaining.location_line();
                            let language_tag_insert = LexerToken::Text(
                                t,
                                ((tag_offset, tag_line), (remaining_offset, remaining_line)),
                            );
                            match config {
                                applicability_document_schema::DocTypeConfig::Md(
                                    applicability_markdown_lexer_config,
                                ) => tokenize_others_parser(&applicability_markdown_lexer_config)
                                    .map(|mut x| {
                                        x.insert(0, language_tag_insert.to_owned());
                                        x.insert(0, start_tag.to_owned());
                                        x.push(end_insert.to_owned());
                                        x
                                    })
                                    .parse_complete(remaining),
                                applicability_document_schema::DocTypeConfig::Cpp(
                                    applicability_cpp_like_lexer_config,
                                ) => tokenize_others_parser(&applicability_cpp_like_lexer_config)
                                    .map(|mut x| {
                                        x.insert(0, language_tag_insert.to_owned());
                                        x.insert(0, start_tag.to_owned());
                                        x.push(end_insert.to_owned());
                                        x
                                    })
                                    .parse_complete(remaining),
                                applicability_document_schema::DocTypeConfig::Build(
                                    applicability_build_file_lexer_config,
                                ) => tokenize_others_parser(&applicability_build_file_lexer_config)
                                    .map(|mut x| {
                                        x.insert(0, language_tag_insert.to_owned());
                                        x.insert(0, start_tag.to_owned());
                                        x.push(end_insert.to_owned());
                                        x
                                    })
                                    .parse_complete(remaining),
                                applicability_document_schema::DocTypeConfig::Rust(
                                    applicability_rust_lexer_config,
                                ) => tokenize_others_parser(&applicability_rust_lexer_config)
                                    .map(|mut x| {
                                        x.insert(0, language_tag_insert.to_owned());
                                        x.insert(0, start_tag.to_owned());
                                        x.push(end_insert.to_owned());
                                        x
                                    })
                                    .parse_complete(remaining),
                                applicability_document_schema::DocTypeConfig::Latex(
                                    applicability_latex_lexer_config,
                                ) => tokenize_others_parser(&applicability_latex_lexer_config)
                                    .map(|mut x| {
                                        x.insert(0, language_tag_insert.to_owned());
                                        x.insert(0, start_tag.to_owned());
                                        x.push(end_insert.to_owned());
                                        x
                                    })
                                    .parse_complete(remaining),
                                applicability_document_schema::DocTypeConfig::Custom(
                                    applicability_custom_lexer_config,
                                ) => tokenize_others_parser(&applicability_custom_lexer_config)
                                    .map(|mut x| {
                                        x.insert(0, language_tag_insert.to_owned());
                                        x.insert(0, start_tag.to_owned());
                                        x.push(end_insert.to_owned());
                                        x
                                    })
                                    .parse_complete(remaining),
                                applicability_document_schema::DocTypeConfig::Plantuml(
                                    applicability_plantuml_lexer_config,
                                ) => tokenize_others_parser(&applicability_plantuml_lexer_config)
                                    .map(|mut x| {
                                        x.insert(0, language_tag_insert.to_owned());
                                        x.insert(0, start_tag.to_owned());
                                        x.push(end_insert.to_owned());
                                        x
                                    })
                                    .parse_complete(remaining),
                                applicability_document_schema::DocTypeConfig::NotSupported => {
                                    let remaining_text_insert = LexerToken::Text(
                                        remaining,
                                        (
                                            (remaining_offset, remaining_line),
                                            (end_start_offset, end_start_line),
                                        ),
                                    );
                                    let resulting_vec: Vec<
                                        LexerToken<LocatedSpan<I, TokenPosition>>,
                                    > = vec![
                                        start_tag,
                                        language_tag_insert,
                                        remaining_text_insert,
                                        end_insert,
                                    ];
                                    Ok((
                                        LocatedSpan::new_extra(
                                            I::default(),
                                            TokenPosition::default(),
                                        ),
                                        resulting_vec,
                                    ))
                                }
                            }
                        }
                        None => {
                            let remaining_offset = remaining.location_offset();
                            let remaining_line = remaining.location_line();
                            let remaining_text_insert = LexerToken::Text(
                                remaining,
                                (
                                    (remaining_offset, remaining_line),
                                    (end_start_offset, end_start_line),
                                ),
                            );
                            let resulting_vec: Vec<LexerToken<LocatedSpan<I, TokenPosition>>> =
                                vec![start_tag, remaining_text_insert, end_insert];
                            Ok((
                                LocatedSpan::new_extra(I::default(), TokenPosition::default()),
                                resulting_vec,
                            ))
                        }
                    }
                },
            )
            .map(|x| match x {
                Ok((_input2, output2)) => output2,
                Err(_) => Vec::<LexerToken<LocatedSpan<I, TokenPosition>>>::new(),
            })
            .process::<OM>(input)
    }
}
