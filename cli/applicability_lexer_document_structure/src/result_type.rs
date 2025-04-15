#[cfg(test)]
use applicability_lexer_base::document_structure::{
    DocumentStructureError, DocumentStructureToken,
};
#[cfg(test)]
use nom::IResult;
#[cfg(test)]
use nom_locate::LocatedSpan;

#[cfg(test)]
pub type ResultType<I> = IResult<
    LocatedSpan<I>,
    Vec<DocumentStructureToken<LocatedSpan<I>>>,
    DocumentStructureError<LocatedSpan<I>>,
>;
