// impl <I> Input for FirstStageTokens<I> where I: Input{
//     type Item = I::Item;

//     type Iter = I::Iter;

//     type IterIndices = I::IterIndices;

//     fn input_len(&self) -> usize {
//         match self{
//             FirstStageTokens::SingleLineComment(x) | FirstStageTokens::SingleLineTerminatedComment(x) | FirstStageTokens::MultiLineComment(x) | FirstStageTokens::Text(x)=> x.input_len(),
//         }
//     }

//     fn take(&self, index: usize) -> Self {
//         match self{
//             FirstStageTokens::SingleLineComment(x) | FirstStageTokens::SingleLineTerminatedComment(x) | FirstStageTokens::MultiLineComment(x) | FirstStageTokens::Text(x)=> x.take(index),
//         }
//     }

//     fn take_from(&self, index: usize) -> Self {
//         match self{
//             FirstStageTokens::SingleLineComment(x) | FirstStageTokens::SingleLineTerminatedComment(x) | FirstStageTokens::MultiLineComment(x) | FirstStageTokens::Text(x)=> x.take_from(index),
//         }
//     }

//     fn take_split(&self, index: usize) -> (Self, Self) {
//         match self{
//             FirstStageTokens::SingleLineComment(x) | FirstStageTokens::SingleLineTerminatedComment(x) | FirstStageTokens::MultiLineComment(x) | FirstStageTokens::Text(x)=> x.take_split(index),
//         }
//     }

//     fn position<P>(&self, predicate: P) -> Option<usize>
//       where
//         P: Fn(Self::Item) -> bool {
//         match self{
//             FirstStageTokens::SingleLineComment(x) | FirstStageTokens::SingleLineTerminatedComment(x) | FirstStageTokens::MultiLineComment(x) | FirstStageTokens::Text(x)=> x.position(predicate),
//         }
//     }

//     fn iter_elements(&self) -> Self::Iter {
//         match self{
//             FirstStageTokens::SingleLineComment(x) | FirstStageTokens::SingleLineTerminatedComment(x) | FirstStageTokens::MultiLineComment(x) | FirstStageTokens::Text(x)=> x.iter_elements(),
//         }
//     }

//     fn iter_indices(&self) -> Self::IterIndices {
//         match self{
//             FirstStageTokens::SingleLineComment(x) | FirstStageTokens::SingleLineTerminatedComment(x) | FirstStageTokens::MultiLineComment(x) | FirstStageTokens::Text(x)=> x.iter_indices(),
//         }
//     }

//     fn slice_index(&self, count: usize) -> Result<usize, nom::Needed> {
//         match self{
//             FirstStageTokens::SingleLineComment(x) | FirstStageTokens::SingleLineTerminatedComment(x) | FirstStageTokens::MultiLineComment(x) | FirstStageTokens::Text(x)=> x.slice_index(count),
//         }
//     }
// }