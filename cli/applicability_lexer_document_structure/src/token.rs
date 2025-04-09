use nom::Input;

#[derive(Debug, Clone, PartialEq, Eq)]
pub enum DocumentStructureToken<I: Input + Send + Sync> {
    SingleLineComment(I, (usize, u32), (usize, u32)),
    SingleLineTerminatedComment(I, (usize, u32), (usize, u32)),
    MultiLineComment(I, (usize, u32), (usize, u32)),
    Text(I, (usize, u32), (usize, u32)),
}

impl<I: Input + Send + Sync> DocumentStructureToken<I> {
    pub fn get_inner(&self) -> &I {
        match self {
            DocumentStructureToken::SingleLineComment(i, _, _) => i,
            DocumentStructureToken::SingleLineTerminatedComment(i, _, _) => i,
            DocumentStructureToken::MultiLineComment(i, _, _) => i,
            DocumentStructureToken::Text(i, _, _) => i,
        }
    }
}
