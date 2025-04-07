use nom::Input;

#[derive(Debug, Clone, PartialEq, Eq)]
pub enum FirstStageToken<I: Input + Send + Sync> {
    SingleLineComment(I, (usize, u32), (usize, u32)),
    SingleLineTerminatedComment(I, (usize, u32), (usize, u32)),
    MultiLineComment(I, (usize, u32), (usize, u32)),
    Text(I, (usize, u32), (usize, u32)),
}

impl<I: Input + Send + Sync> FirstStageToken<I> {
    pub fn get_inner(&self) -> &I {
        match self {
            FirstStageToken::SingleLineComment(i, _, _) => i,
            FirstStageToken::SingleLineTerminatedComment(i, _, _) => i,
            FirstStageToken::MultiLineComment(i, _, _) => i,
            FirstStageToken::Text(i, _, _) => i,
        }
    }
}
