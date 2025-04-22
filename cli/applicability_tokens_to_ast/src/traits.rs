pub trait HasContents<I> {
    fn push(&mut self, value: FlattenApplicabilityAst<I>);
}
