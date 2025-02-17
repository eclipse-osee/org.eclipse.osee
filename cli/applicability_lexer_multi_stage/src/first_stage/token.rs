use nom::Input;

#[derive(Debug, Clone, PartialEq, Eq)]
pub(crate) enum FirstStageToken<I> {
    SingleLineComment(I),
    SingleLineTerminatedComment(I),
    MultiLineComment(I),
    Text(I),
}
