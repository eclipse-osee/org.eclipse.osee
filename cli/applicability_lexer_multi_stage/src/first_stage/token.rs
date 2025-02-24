#[derive(Debug, Clone, PartialEq, Eq)]
pub(crate) enum FirstStageToken<I> {
    SingleLineComment(I),
    SingleLineTerminatedComment(I),
    MultiLineComment(I),
    Text(I),
}

impl<I> FirstStageToken<I> {
    pub fn get_inner(&self) -> &I {
        match self {
            FirstStageToken::SingleLineComment(i) => i,
            FirstStageToken::SingleLineTerminatedComment(i) => i,
            FirstStageToken::MultiLineComment(i) => i,
            FirstStageToken::Text(i) => i,
        }
    }
}
